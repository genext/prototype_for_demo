
package com.example.proto.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.proto.dao.ContentsDownDAO;
import com.example.proto.mediaplayer.MyPlayer;
import com.example.proto.util.MyLog;

public class FtpTask<T> extends AsyncTask<Void, Integer, T>{	
	
	private String ip;//서버 ip
	private int port;//서버 port
	private Socket socket;
	private DataInputStream  dis;
	private DataOutputStream  dos;	
	private OnFtpResponceListener listener;//서버로 부터 요청한 데이터가 성공일 경우 callback listener
	private OnFtpErrorListener errorListener;//서버로 부터 요청한 데이터가  실패일 경우 callback listener
	private Packet packet;//전송 패킷 정의
	private Class<T> clazz;//응답 받을 DAO 클래스
	private Context mContext;
	private MyPlayer player;
	private String mfileName;
	private ProgressDialog dlg;
	private final int fileHEADER = 20 * 1024 * 1024;  // 20 Megabytes
	private boolean bPlayerStart = false;
	
	//socket 데이터 처리에 대해서 성공했을 경우 callback 처리를 위한 interface
	public interface OnFtpResponceListener{
		public void onResponce();
	}
	
	//socket 데이터 처리에 대해서 실패하였을 경우의 callback 처리를 위한 interface
	public interface OnFtpErrorListener{
		public void onError(int failCode);
	}
	
	public FtpTask(String ip, 
				   int port, 
				   Context mContext,
				   ProgressDialog mDlg,
				   long fileid,
				   MyPlayer player, 
				   Packet packet, 
				   Class<T> clazz, 
				   OnFtpResponceListener listener, 
				   OnFtpErrorListener errorListener){
		this.ip = ip;
		this.port = port;		
		this.packet = packet;
		this.clazz = clazz;
		this.listener = listener;
		this.errorListener = errorListener;
		this.mContext = mContext;
		this.player = player;
		this.mfileName = String.valueOf(fileid);
		this.dlg = mDlg;
	}	
	
	
	@Override
	protected void onPreExecute() {//소켓 통신이 시작되기 전에 해야 할일을 넣어줌
		// dlg.setCanceledOnTouchOutside(false);
		dlg.setMessage(mfileName);
		dlg.setProgress(0);
		dlg.show();

		super.onPreExecute();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		dlg.setProgress(values[0]);

		if (values[0] >= 99) {
			bPlayerStart = true;

/*			if (player.isPlaying()) {
				player.stop();
			}
			player.setLooping(false);
			String path = new String("file:" + mContext.getFilesDir().toString()	+ "/" + mfileName);
			MyLog.d("download file path is %s", path);

			player.setVideoURI(Uri.parse(path));
			player.start();
			dlg.dismiss();
*/		}
	}

	@Override
	protected T doInBackground(Void... params) {//소켓 통신 본문~
		// TODO stop when other download start...
		try {
			long recvSize = 0;
			socket = new Socket(ip, port);
			dis  = new DataInputStream(socket.getInputStream());
			dos  = new DataOutputStream(socket.getOutputStream());			
			
			//프로토콜에 따라 전송할 패킷을 만듬
			byte[] snd = packet.convertByteArray();
			MyLog.d("전송 데이터 %s", snd.toString());

			//전송
			dos.write(snd, 0, snd.length);
			dos.flush();	
			
			//데이터 크기가 정해지지 않아서 2048로 임의로 받을 데이터의 크기 지정 (2014.08.19 오재경)	
			byte[] rcv = new byte[2048];
			dis.read(rcv);		
			
			ContentsDownDAO object = (ContentsDownDAO)packet.convertDAO(clazz, rcv);
			
			recvSize = object.getFileSize();
			MyLog.d("수신할 파일 크기 : %d", object.getFileSize());
			
			StringBuilder ftpip = new StringBuilder();
			String[] ipDigit = object.getDownInfo().split(",");								

			ftpip.append(ipDigit[0]);
			ftpip.append(".");
			ftpip.append(ipDigit[1]);
			ftpip.append(".");
			ftpip.append(ipDigit[2]);
			ftpip.append(".");
			ftpip.append(ipDigit[3]);
			
			int ftpport = Integer.parseInt(ipDigit[4]) * 256 + Integer.parseInt(ipDigit[5]);

			MyLog.d("ftp ip : %s", ftpip.toString());
			MyLog.d("ftp port : %d", ftpport);

			File root = mContext.getFilesDir();
			File[] files = root.listFiles();
			File saved = new File(mfileName);
			
			for (int i = 0; i < files.length; i++) {
				File temp = files[i];
					Date lastModified = new Date(temp.lastModified());
					MyLog.d("File %s mtime : %s size : %d", mfileName, lastModified.toString(), temp.length());
			}
			long totalSpace = root.getTotalSpace();
			long usableSpace = root.getUsableSpace();
			long freeSpace = root.getFreeSpace();
			
			MyLog.d("path : %s, total : %d, usable : %d, free : %d", root.getPath(), totalSpace, usableSpace, freeSpace);
			Socket ftpsocket = new Socket(ftpip.toString(), ftpport);
			
			DataInputStream ftp_is = new DataInputStream(ftpsocket.getInputStream());

			// local file open TODO 다운로드 요청하기 전 이미 이전에 다운 받았는지 확인하고 파일 크기를 확인하여 서버에 그 크기를 보내줘야 한다.
			FileOutputStream outputStream = mContext.openFileOutput(mfileName, Context.MODE_PRIVATE);
			byte[] ftp_rcv = new byte[8192];
			long length = 0;
			long totalRcv = 0;
			int current = 0;
			long mid;

			while ((length = ftp_is.read(ftp_rcv)) > 0) {
				totalRcv += length;
				outputStream.write(ftp_rcv, 0, (int)length);
				//MyLog.d("receiving...%d", totalRcv);
				
				//if (bPlayerStart == false) {
					//mid = totalRcv*100;
					//current = (int)(mid / recvSize);
					//current = mid / fileHEADER;
					//MyLog.d("current ratio : %d", current);
					//publishProgress(current);										
				//}
			}
			MyLog.d("end of ftp total length %d", totalRcv);
			ftp_is.close();
			outputStream.close();
			ftpsocket.close();
			ftpsocket = null;

			ByteBuffer buffer = ByteBuffer.allocate(16);
			byte[] ftp_end = new byte[16];
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			
			buffer.putInt(1301 & 0xffff);//code		
			buffer.putInt(16 & 0xffff);//total length	
			buffer.putInt(0 & 0xffff);//data length
			buffer.put(new byte[]{65, 78, 68, 0});//tail		
		
			ftp_end = buffer.array();
			MyLog.d("send 1301");
			
			dos.write(ftp_end, 0, 16);
			dos.flush();		
			
			dis.close();
			dos.close();
			socket.close();
			socket = null;
			
			//받은 byte[] 데이터를 해당 DAO에 맞게 변경
			return packet.convertDAO(clazz, rcv);
			
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			//요것도 따로 처리해 주어야 합니다. 아래처럼요
			//if(errorListener != null){
			//	errorListener.onError(resultCode);
			//}
			MyLog.d("UnknownHostExceptopn");
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//서요것도 따로 처리해 주어야 합니다. 아래처럼요
			//if(errorListener != null){
			//	errorListener.onError(resultCode);
			//}
			MyLog.d("IOException");
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

			// TODO ftp 수신 중 에러가 발생할 경우도 처리할 수 있도록 수정.
			if(resultCode == 9){//성공
				if(listener != null){
					listener.onResponce();
				}
			}else{//실패
				if(errorListener != null){
					errorListener.onError(resultCode);
				}
			}
		}		
	}

	//소켓 통신 성공 리스너 등록
	public void setOnFtpResponceListener(OnFtpResponceListener listener){
		this.listener = listener;
	}
	
	//소켓 통신 실패 리스너 등록
	public void setOnFtpErrorListener(OnFtpErrorListener errorListener){
		this.errorListener = errorListener;
	}
	

}
