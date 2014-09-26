
package com.example.proto.ftp;

public interface TransferProgressListener {

	public void onBeginTransferTask(TransferTask task);
	
	public void onBeginTransfer(TransferTask task, int id);
	
	public void onPregressUpdate(TransferTask task, int id, int progress);
	
	public void onEndTransferTask(TransferTask task);
	
	public void onEndTransfer(TransferTask task, int id);	
}
