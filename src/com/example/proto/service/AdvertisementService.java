
package com.example.proto.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.proto.dao.SimpleDAO;
import com.example.proto.dao.json.AdJSON;
import com.example.proto.socket.Packet;
import com.example.proto.socket.SocketRequest;
import com.example.proto.socket.SocketTask.OnSocketErrorListener;
import com.example.proto.socket.SocketTask.OnSocketResponceListener;
import com.example.proto.util.MyLog;
import com.example.proto.util.Prefs;
import com.google.gson.Gson;

//IntentService는 시작 요청이 들어올때마다 작업을 수행하는 쓰레드를 별도로 생성함, 
//작업이 완료되면 서비스를 알아서 중단해서 stopService()를 호출할 필요 없음
public class AdvertisementService extends IntentService{

	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.example.proto.service";
	
	public AdvertisementService() {
		super("AdvertisementService");
	}

	//실제 광고 컨텐츠에 대한 요청이 들어왔을때를 처리
	@Override
	protected void onHandleIntent(Intent intent) {
		
		//인증이 되었을때만 광고 요청 가능한지? 일단은 인증 되었을때만 광고 요청 가능하도록 함...프로토콜이 그렇게 구성되어 있음
		String certKey = Prefs.getString("CERTKEY", null);
		if(certKey != null && certKey != ""){
			//광고 리스트 요청
			Packet packet = new Packet(Packet.ETC, Packet.OPTION_AD);//패킷 종류 선택 	
	        packet.createPacketData(Prefs.getString("CERTKEY", null));//패킷 데이터 입력 (인증키, 프로그램 코드)
	        
			SocketRequest.objectRequest(SocketRequest.SERV_IP, //서버 ip
					  SocketRequest.PORT,     //서버 port
					  packet,                 //전송할 데이터
					  SimpleDAO.class, //리턴 받을 객체 타입 선언
					  new OnSocketResponceListener<SimpleDAO>(){//전송이 성공하였을 경우 listener

							@Override
							public void onResponce(SimpleDAO object) {
								// TODO Auto-generated method stub
								if(object != null){
									Gson gson = new Gson();
									String json = object.getData();
									AdJSON adJSON = gson.fromJson(json, AdJSON.class);
									
									//json으로 받아온 내용을 preference에 저장 하던 singleton으로 가지고 있던 한다.
								}
								
							}
			        	
			      },  new OnSocketErrorListener<SimpleDAO>(){//전송이 실패하였을 경우 listener

							@Override
							public void onError(int failCode, SimpleDAO object) {
								// TODO Auto-generated method stub
								MyLog.d("socket error : %d", failCode);
							}
			        	
			      });
		}		
	}

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MyLog.d("%s", "AdvertisementService create...");		
	}

	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		MyLog.d("%s", "AdvertisementService start...");
	}
}
