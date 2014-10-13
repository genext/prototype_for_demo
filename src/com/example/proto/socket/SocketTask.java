
package com.example.proto.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;

import com.example.proto.dao.SimpleDAO;
import com.example.proto.util.MyLog;
import com.example.proto.util.Tools;
public class SocketTask<T> extends AsyncTask<Void, Void, T>{	
	
	private String ip;//서버 ip
	private int port;//서버 port
	private Socket socket;
	private DataInputStream  dis;
	private DataOutputStream  dos;	
	private OnSocketResponceListener<T> listener;//서버로 부터 요청한 데이터가 성공일 경우 callback listener
	private OnSocketErrorListener<T> errorListener;//서버로 부터 요청한 데이터가  실패일 경우 callback listener
	private Packet packet;//전송 패킷 정의
	private Class<T> clazz;//응답 받을 DAO 클래스
	
	
	//socket 데이터 처리에 대해서 성공했을 경우 callback 처리를 위한 interface
	public interface OnSocketResponceListener<T>{
		public void onResponce(T object);
	}
	
	//socket 데이터 처리에 대해서 실패하였을 경우의 callback 처리를 위한 interface
	public interface OnSocketErrorListener<T>{
		public void onError(int failCode, T object);
	}
	
	public SocketTask(String ip, int port, Packet packet, Class<T> clazz, OnSocketResponceListener<T> listener, OnSocketErrorListener<T> errorListener){
		this.ip = ip;
		this.port = port;		
		this.packet = packet;
		this.clazz = clazz;
		this.listener = listener;
		this.errorListener = errorListener;
	}	
	
	
	@Override
	protected void onPreExecute() {//소켓 통신이 시작되기 전에 해야 할일을 넣어줌
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	@Override
	protected T doInBackground(Void... params) {//소켓 통신 본문~
		// TODO Auto-generated method stub
		try {
			socket = new Socket(ip, port);
			dis  = new DataInputStream(socket.getInputStream());
			dos  = new DataOutputStream(socket.getOutputStream());			
			
			//프로토콜에 따라 전송할 패킷을 만듬
			byte[] snd = packet.convertByteArray();

			//전송
			dos.write(snd, 0, snd.length);
			dos.flush();	

			byte[] rcv = new byte[8192];
			int length = 0;
			int total_length = 0;
			int toRecv = 0;

			MyLog.d("before recv start");
			ByteArrayBuffer allrcv = new ByteArrayBuffer(8192);
			allrcv.clear();
			
			while ((length = dis.read(rcv)) > 0) {
				if (toRecv == 0) {
					toRecv = Tools.byteArrayToInt(Tools.getBytes(rcv, 4, 4));
					MyLog.d("toRecv : %d", toRecv);
				}

				total_length += length;
				allrcv.append(rcv, 0, length);

				if (toRecv == total_length) {
					MyLog.d("received all data!!!! loop out!!");
					break;
				}
			}
			
			byte[] result = allrcv.buffer();

/* 다른 방식으로 받은 것을 저장한 코드로 이 코드도 정상 동작			
			ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
			while ((length = dis.read(rcv)) != -1) {
				if (toRecv == 0) {
					toRecv = Tools.byteArrayToInt(Tools.getBytes(rcv, 4, 4));
					MyLog.d("toRecv : %d", toRecv);
					byte[] dest = new byte[length - 11];
					Arrays.fill(dest, (byte)0);
					System.arraycopy(rcv, 0, dest, 0, length - 12);
					MyLog.d("data1 : %s", new String(dest, "UTF-8"));
				}
				else {
					byte[] dest = new byte[length + 1];
					Arrays.fill(dest, (byte)0);
					System.arraycopy(rcv, 0, dest, 0, length);					
					MyLog.d("data1 : %s", new String(dest, "UTF-8"));
				}
				total_length += length;
				byteBuffer.write(rcv, 0, length);
				MyLog.d("In receiving total_length : %d", total_length);
				if (toRecv == total_length) {
					MyLog.d("received all data!!!! loop out!!");
					break;
				}
			
			}
			byte[] result = byteBuffer.toByteArray();
*/			dis.close();
			dos.close();
			socket.close();
			socket = null;
			
			//받은 byte[] 데이터를 해당 DAO에 맞게 변경
			return packet.convertDAO(clazz, result);
			//return packet.convertDAO(clazz, rcv);
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			//요것도 따로 처리해 주어야 합니다. 아래처럼요
			//if(errorListener != null){
			//	errorListener.onError(resultCode);
			//}
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//서요것도 따로 처리해 주어야 합니다. 아래처럼요
			//if(errorListener != null){
			//	errorListener.onError(resultCode);
			//}
			e1.printStackTrace();
		}		
				
		return null;
	}


	@Override
	protected void onPostExecute(T object) {//소켓 통신이 완료된 후 lisner로 성공 실패 callback
		super.onPostExecute(object);
		
		if(object != null){
			int agnetType = packet.getAgentType();
			int resultCode = packet.getResult();
			
			//프로토콜 마다 성공 메시지가 동일하다면 Packet 클래스에서 한번에 처리 가능, 현재 서버에서 지원하지 않으므로 하드 코딩 해서 처리..악!!		
			if(agnetType == Packet.CERT || agnetType == Packet.H2){
				if(resultCode == 1){//성공
					if(listener != null){
						listener.onResponce(object);
					}
				}else{//실패
					if(errorListener != null){
						errorListener.onError(resultCode, object);
					}
				}
			}else if(agnetType == Packet.ETC || agnetType == Packet.PAY || agnetType == Packet.FTP){
				if(resultCode == 9){//성공
					if(listener != null){
						listener.onResponce(object);
					}
				}else{//실패
					if(errorListener != null){
						errorListener.onError(resultCode, object);
					}
				}
			}
			
		}		
	}

	//소켓 통신 성공 리스너 등록
	public void setOnSocketResponceListener(OnSocketResponceListener<T> listener){
		this.listener = listener;
	}
	
	//소켓 통신 실패 리스너 등록
	public void setOnSocketErrorListener(OnSocketErrorListener<T> errorListener){
		this.errorListener = errorListener;
	}
	

}
