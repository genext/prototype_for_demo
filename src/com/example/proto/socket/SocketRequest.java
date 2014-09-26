
package com.example.proto.socket;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.proto.mediaplayer.MyPlayer;
import com.example.proto.socket.FtpTask.OnFtpErrorListener;
import com.example.proto.socket.FtpTask.OnFtpResponceListener;
import com.example.proto.socket.SocketTask.OnSocketErrorListener;
import com.example.proto.socket.SocketTask.OnSocketResponceListener;
// jkoh code start
// jkoh code end

//추후에 멀티 쓰레드를 지원해야 할 경우 개발이 용이 하도록..
public class SocketRequest {
	
	public static final String SERV_IP = "106.247.232.26";
	public static final int PORT = 5725;

	public static<T> void objectRequest(String serverIP, int port, Packet packet, Class<T> clazz, OnSocketResponceListener<T> listener, OnSocketErrorListener errorListener){
		SocketTask<T> task = new SocketTask<T>(serverIP, port, packet, clazz, listener, errorListener);
		task.execute();
	}

	// jkoh code start
	public static<T> void ftpRequest(String serverIP, int port, Context context, ProgressDialog mDlg, long fileid, MyPlayer player, Packet packet, Class<T> clazz, OnFtpResponceListener listener, OnFtpErrorListener errorListener){
		FtpTask<T> task = new FtpTask<T>(serverIP, port, context, mDlg, fileid, player, packet, clazz, listener, errorListener);
		task.execute();
	}
	// jkoh code end

}
