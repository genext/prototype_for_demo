
package com.example.proto.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.proto.R;
import com.example.proto.dao.CertificationKeyDAO;
import com.example.proto.service.AdvertisementService;
import com.example.proto.socket.Packet;
import com.example.proto.socket.SocketRequest;
import com.example.proto.socket.SocketTask.OnSocketErrorListener;
import com.example.proto.socket.SocketTask.OnSocketResponceListener;
import com.example.proto.util.MyLog;
import com.example.proto.util.Prefs;


public class SplashActivity extends Activity{

	private ProgressBar spinner;
	private String MacAddress;
	private DialogFragment loginFragment;
	private DialogFragment networkAlertFragment;
	private String md5Hex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);		// 윈도우 프로그램 제목 줄 없애기 (SetContentView 보다 먼저 선언되어야 함)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	// 상태표시줄 없애고 전체 화면 모드 표시
		// TODO splash의 텍스트를 회사 로고로 변경.
		setContentView(R.layout.splash);

		spinner = (ProgressBar) findViewById(R.id.progress);
		spinner.setIndeterminate(true);
		
		// system info
		// cpu, manufacturer, model name, serial no, version
		String strMessage = "CPU_ABI: "+Build.CPU_ABI + "\nBrand: " + Build.BRAND + "\nModel: " + Build.MODEL + "\nSerial: " + Build.SERIAL
				+ "\nVersion: " + Build.VERSION.RELEASE;
		MyLog.d("system info : %s", strMessage);
		// monitor info
		DisplayMetrics disMetrics = getResources().getDisplayMetrics();
		String strDisplayInfo = "Display Width: " + disMetrics.widthPixels + "\nDisplay Height: " + disMetrics.heightPixels
				+ "\nDisplayDpi: " + disMetrics.densityDpi + "\nHorizental Size: " + (float)disMetrics.widthPixels / (float)disMetrics.densityDpi
				+ "\nVertical Size: " + (float)disMetrics.heightPixels / (float)disMetrics.densityDpi;
		
		MyLog.d("monitor info : %s", strDisplayInfo);
		
		// TODO check network : not only wife but also ethernet
    	ConnectivityManager conn = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	
    	
    	if (conn.getActiveNetworkInfo() != null) {
    		NetworkInfo activeNetwork = conn.getActiveNetworkInfo();
    		
    		switch (activeNetwork.getType()) {
    		case ConnectivityManager.TYPE_WIFI:
    			MyLog.d("wifi activated");
    	    	// read Mac Address
    			WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    			WifiInfo info = manager.getConnectionInfo();
    			MacAddress = info.getMacAddress();
    			int ipAddress = info.getIpAddress();
    			String ip = String.format("%d.%d.%d.%d",  (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
    			MyLog.d("wifi mac address: %s ip address : %s", MacAddress, ip);
    			// TODO ip address가 0.0.0.0인 경우도 처리할 것...이노디지털의 셋탑문제일까?
    			break;
    		case ConnectivityManager.TYPE_ETHERNET:
    			MyLog.d("wired activated");
    			MacAddress = getEthernetMacAddress();
    			MyLog.d("ethernet mac address : %s ip address : %s", MacAddress.toString(), getLocalIpAddress());     			
    			break;
    		default:
    				
    		}
    		// TODO 셋탑박스의 wifi가 안 켜져 있으면 랜선이 연결되어 있는데도 앱이 바로 죽어버림. 이걸 회피하도록...
    		//Splash부분은 어떻게 수정하실지 몰라서 최대한 원본 그대로 유지했습니다.
    		
    		// read config 
    		String CertificateKey = Prefs.getString("CERTKEY", null);
    		
    		if (CertificateKey == null) {
    			Log.d("tvbogo", "no cert key so save id/passwd");
    			// id/password dialog box
    			loginFragment = new LoginDialogFragment();
    	    	showDialogFragment(loginFragment);
    			// TODO 셋탑에서 저장된 인증키를 얻을 수 없이 이미 이전에 인증키를 서버에서 부여한 경우에는 서버쪽에서 인증키가 남아 있다. 이 때에는 로그인 아이디를 입력하더라도 인증키를 새로 만들어주는 것이 아니라 서버에서 이미 등록된 인증키가 있다고 알려준다.
    	    	// 이럴 경우에 대한 처리 로직 추가 필요.

    		}
    		else {
    			spinner.setVisibility(View.GONE);																	
    			startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
    		}
    		
    		//registerService(); //TODO uncomment this line when you are ready.
    	}
    	else {
    		// TODO 네트웍 불통을 알려야 한다.
    		MyLog.d("no active network");
    		networkAlertFragment = new NetworkAlertDialogFragment();
    		showDialogFragment(networkAlertFragment);
    	}

	}
	
	public class NetworkAlertDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
    		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		final View layout = inflater.inflate(R.layout.net_alert, (ViewGroup) getActivity().findViewById(R.id.net_msg));

    		final TextView msg = (TextView) layout.findViewById(R.id.warning);
    		msg.setText("네트웍이 사용가능한 상태가 아닙니다.");

    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setView(layout);
    		// Now configure the AlertDialog
    		builder.setTitle("네트웍 경고");
    		builder.setCancelable(false);
    		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				finish();
    			}
    		});
    		
    		// Create the AlertDialog and show it
    		AlertDialog netAlertDialog = builder.create();
    		return netAlertDialog;
		}
		
	}
	
	public class LoginDialogFragment extends DialogFragment {
/*    	public static LoginDialogFragment newInstance() {	//jkoh 이게 왜 주석 처리? 내가 했는지 기억이 안 남. 왜 주석을 풀면 에러가 나지? 꼭 클래스도 static이어야 하나?
    		LoginDialogFragment newInstance = new LoginDialogFragment();
    		return newInstance;
    	}
*/
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {    		
    		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		final View layout = inflater.inflate(R.layout.login_dialog, (ViewGroup) getActivity().findViewById(R.id.root));
    		final EditText p1 = (EditText) layout.findViewById(R.id.EditText_loginid);
    		final EditText p2 = (EditText) layout.findViewById(R.id.EditText_Pwd1);
    		final EditText p3 = (EditText) layout.findViewById(R.id.EditText_Pwd2);
    		final TextView error = (TextView) layout.findViewById(R.id.TextView_PwdProblem);
    		p3.addTextChangedListener(new TextWatcher() {
    			@Override
    			public void afterTextChanged(Editable s) {
    		        String strPass1 = p2.getText().toString();
    		        String strPass2 = p3.getText().toString();
    		        if (strPass1.equals(strPass2)) {
    		            error.setText(R.string.settings_pwd_equal);
    		        } else {
    		            error.setText(R.string.settings_pwd_not_equal);
    		        }
    		    }

    		    // ... other required overrides do nothing
    			@Override
    			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    		    }

    			@Override
    		    public void onTextChanged(CharSequence s, int start, int before, int count) {
    		    }
    		});
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setView(layout);
    		// Now configure the AlertDialog
    		builder.setTitle(R.string.login_title);
/*    		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int whichButton) {
    		        // We forcefully dismiss and remove the Dialog, so it
    		        // cannot be used again
    		        dialog.dismiss();
    		    }
    		});
*/    		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				String strLoginid = p1.getText().toString();
    		        String strPassword1 = p2.getText().toString();
    		        String strPassword2 = p3.getText().toString();
    		        if (strPassword1.equals(strPassword2)) {
        				strLoginid = "dmadb33";
        				Prefs.putString("loginid", strLoginid);

        				strPassword1 = "123456";  // jkoh TODO replace this code to getText....
        				// password encryption by MD5
        				md5Hex = new String(Hex.encodeHex(DigestUtils.md5(strPassword1)));			
        				
        				Prefs.putString("password",  md5Hex);
        		        //socket으로 데이터 요청(인증 요청 1000)
        				Packet packet = new Packet(Packet.CERT, Packet.OPTION_NONE);//패킷 종류 선택         		             		        
        		        packet.createPacketData(strLoginid, md5Hex.substring(0, 16), MacAddress);//패킷 데이터 입력       		        
        		        
        		        SocketRequest.objectRequest(SocketRequest.SERV_IP, //서버 ip
        		        								  SocketRequest.PORT,     //서버 port
        		        								  packet,                 //전송할 데이터
        		        								  CertificationKeyDAO.class, //리턴 받을 객체 타입 선언
        		        								  new OnSocketResponceListener<CertificationKeyDAO>(){//전송이 성공하였을 경우 listener

																@Override
																public void onResponce(CertificationKeyDAO object) {
																	Prefs.putString("CERTKEY", object.getCertKey());
																	spinner.setVisibility(View.GONE);	
																	startMainActivity();
																}
									        		        	
									        		      },  new OnSocketErrorListener<CertificationKeyDAO>(){//전송이 실패하였을 경우 listener
									
																@Override
																public void onError(int failCode, CertificationKeyDAO object) {
																	// TODO 프로그램 끝내도록...
																	MyLog.d("socket error : %d", failCode);
																	switch (failCode) {
																	// TODO hardcode number를 코드화
																	case 0:
																	case 2:
																		new AlertDialog.Builder(getApplicationContext())
																		.setTitle("알림")
																		.setMessage("고객 센터에 문의하시기 바랍니다.")
																		.setPositiveButton("확인",
																				new DialogInterface.OnClickListener() {
																					public void onClick(DialogInterface dialog,
																							int whichButton) {
																						dialog.dismiss();
																					}
																				}).show();
																		break;
																	case 3:
																		new AlertDialog.Builder(getApplicationContext())
																		.setTitle("알림")
																		.setMessage("사용이 정지된 아이디입니다. 사용정지를 해제하십시오.")
																		.setPositiveButton("확인",
																				new DialogInterface.OnClickListener() {
																					public void onClick(DialogInterface dialog,
																							int whichButton) {
																						dialog.dismiss();
																					}
																				}).show();
																		break;
																	case 4:
																		new AlertDialog.Builder(getApplicationContext())
																		.setTitle("알림")
																		.setMessage("일치하는 아이디가 없습니다.")
																		.setPositiveButton("확인",
																				new DialogInterface.OnClickListener() {
																					public void onClick(DialogInterface dialog,
																							int whichButton) {
																						dialog.dismiss();
																					}
																				}).show();
																		break;
																	case 5:
																		new AlertDialog.Builder(getApplicationContext())
																		.setTitle("알림")
																		.setMessage("비밀번호가 다릅니다.")
																		.setPositiveButton("확인",
																				new DialogInterface.OnClickListener() {
																					public void onClick(DialogInterface dialog,
																							int whichButton) {
																						dialog.dismiss();
																					}
																				}).show();
																		break;
																	case 6:
																		new AlertDialog.Builder(getApplicationContext())
																		.setTitle("알림")
																		.setMessage("인증키 생성에 실패했습니다. 다시 시도해주십시오.")
																		.setPositiveButton("확인",
																				new DialogInterface.OnClickListener() {
																					public void onClick(DialogInterface dialog,
																							int whichButton) {
																						dialog.dismiss();
																					}
																				}).show();
																		break;
																	case 7:
																		Prefs.putString("CERTKEY", object.getCertKey());
																		new AlertDialog.Builder(getApplicationContext())
																		.setTitle("알림")
																		.setMessage("이미 등록된 셋탑입니다.")
																		.setPositiveButton("확인",
																				new DialogInterface.OnClickListener() {
																					public void onClick(DialogInterface dialog,
																							int whichButton) {
																						dialog.dismiss();
																						spinner.setVisibility(View.GONE);	
																						startMainActivity();
																					}
																				}).show();
																		break;
																	case 8:
																		new AlertDialog.Builder(getApplicationContext())
																		.setTitle("알림")
																		.setMessage("다른 사용자에게 등록된 셋탑입니다. 고객선터에 문의하시기 바랍니다.")
																		.setPositiveButton("확인",
																				new DialogInterface.OnClickListener() {
																					public void onClick(DialogInterface dialog,
																							int whichButton) {
																						dialog.dismiss();
																					}
																				}).show();
																		break;

																	}
																}
									        		        	
									        		      });        				
        		        
    		        }
    		    }
    		});
    		// Create the AlertDialog and show it
    		AlertDialog passwordDialog = builder.create();
    		return passwordDialog;
    	}
    }
	
	private void startMainActivity(){
		startActivity(new Intent(SplashActivity.this,MainActivity.class));																	
		finish();
	}
    
	void showDialogFragment(DialogFragment newFragment) {
		newFragment.show(getFragmentManager(), null);
	}
	
	//특정시간에 background에서 광고 영상 가져오도록 service 등록
	public void registerService(){
		MyLog.d("start registerService");
		Intent intent = new Intent(this, AdvertisementService.class);
		PendingIntent sender = PendingIntent.getService(this, 0, intent, 0);		
		
        // 현재 시간 가져와서, 24시간 마다 실행되게
		long now = System.currentTimeMillis();
		Date tomorrow = new Date(now);			
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC, tomorrow.getTime(), 24 * 60 * 60 * 1000, sender);		
	}
	
	/*
	 * Load file content to String
	 */
	public static String loadFileAsString(String filePath) throws java.io.IOException{
	    StringBuffer fileData = new StringBuffer(1000);
	    BufferedReader reader = new BufferedReader(new FileReader(filePath));
	    char[] buf = new char[1024];
	    int numRead=0;
	    while((numRead=reader.read(buf)) != -1){
	        String readData = String.valueOf(buf, 0, numRead);
	        fileData.append(readData);
	    }
	    reader.close();
	    return fileData.toString();
	}

	/*
	 * Get the STB MacAddress
	 */
	public String getEthernetMacAddress(){
	    try {
	        return loadFileAsString("/sys/class/net/eth0/address")
	            .toUpperCase().substring(0, 17);
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    String ip = Formatter.formatIpAddress(inetAddress.hashCode());
	                    return ip;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        MyLog.e(ex.toString());
	    }
	    return null;
	}
}
