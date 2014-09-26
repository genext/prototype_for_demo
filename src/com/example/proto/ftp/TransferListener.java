
package com.example.proto.ftp;

public interface TransferListener {	
	public void onBeginTransfer(String fileName, int id);
	public void onEndTransfer(String fileName, int id);	
	public void onUpdateTransfer(int id, int progress);
}
