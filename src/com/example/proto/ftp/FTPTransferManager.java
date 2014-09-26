
package com.example.proto.ftp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class FTPTransferManager implements TransferProgressListener {

	private TransferListener listener;

	private int transferSequense;
	private final List<Transfer> transferList;
	private final List<TransferTask> transferTasks;
	
	//transfer task를 queue 형태로 구현 하였음
	private final int maxTransferTaksCount = 1;

	public FTPTransferManager() {
		transferSequense = 0;
		transferList = new ArrayList<Transfer>();
		transferTasks = new ArrayList<TransferTask>();
	}

	public void addTransferQueue(Context context, FileEntity entity, String ip, int port) {
		Transfer transfer = new Transfer(++transferSequense);
		transfer.setIp(ip);
		transfer.setPort(port);
		transfer.setPending(true);
		transfer.setFileName(entity.getName());
		transfer.setFileSize(entity.getSize());

		// source path
		transfer.setSourcePath(entity.getParentPath());

		// destination path(현재는 해당 프로젝트가 생성되는 위치에 다운받지만 필요할 경우 위치 변경..ex.sd카드..)
		transfer.setDestPath(context.getFilesDir().toString());

		synchronized (transferList) {
			transferList.add(transfer);
		}
	}

	public void processTransferQueue() {
		synchronized (transferList) {

			//no task ?
			if ((transferList == null) || transferList.isEmpty()) {
				return;
			}

			for (;;) {
				if (transferTasks.size() < maxTransferTaksCount) {

					//pending transfer
					Transfer pendingTransfer = null;
					for (Transfer transfer : transferList) {
						if (transfer.isPending()) {
							pendingTransfer = transfer;
							break;
						}
					}

					//no pending transfer
					if (pendingTransfer == null) {
						break;
					}

					//create new transfer task
					FTPTransferTask task = new FTPTransferTask(transferList,
							this);
					transferTasks.add(task);
					task.execute();
				} else {
					break;
				}
			}
		}
	}

	@Override
	public void onBeginTransferTask(TransferTask task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBeginTransfer(TransferTask task, int id) {
		// TODO Auto-generated method stub
		if (listener != null) {			
			listener.onBeginTransfer(task.currentTransfer.getFileName(), id);
			//Log.d("", "id : " + Integer.toString(id) + " - onBeginTransfer()");
		}
	}


	@Override
	public void onPregressUpdate(TransferTask task, int id, int progress) {
		// TODO Auto-generated method stub
		if (listener != null) {
			listener.onUpdateTransfer(id, progress);
			//Log.d("", "id : " + Integer.toString(id) + " - onPregressUpdate()");
		}
	}


	@Override
	public void onEndTransferTask(TransferTask task) {
		// TODO Auto-generated method stub
		// Remove finiched task
        transferTasks.remove(task);
	}


	@Override
	public void onEndTransfer(TransferTask task, int id) {
		// TODO Auto-generated method stub

		if(transferList.size() > 0){
			synchronized (transferList) {
				Transfer completeTransfer = null;
				// Find complete transfer			
				for (Transfer transfer : transferList) {
					if (transfer.getTransferId() == id) {
						completeTransfer = transfer;
						break;
					}
				}
				
				if(completeTransfer != null){
					transferList.remove(completeTransfer);
					if (listener != null) {		
						Log.d("onEndTransfer() -", task.toString());
						listener.onEndTransfer(completeTransfer.getFileName(),id);
					}
				}
				
				
			}
		}		
	}	

    public List<Transfer> getTransferList() {
            return transferList;
    }

    public void setTransferListener(TransferListener listener) {
            this.listener = listener;            
    }

}
