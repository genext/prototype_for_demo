
package com.example.proto.ftp;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

public abstract class TransferTask extends AsyncTask<Transfer, Integer, String> {

	public TransferProgressListener listener;
	public final List<Transfer> transferList;
	public Transfer currentTransfer;

	public TransferTask(List<Transfer> transferList, TransferProgressListener listener) {
		this.transferList = transferList;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		listener.onBeginTransferTask(this);
	}

	@Override
	protected String doInBackground(Transfer... transfers) {
		for (;;) {
			currentTransfer = null;
			synchronized (transferList) {
				for (Transfer t : transferList) {
					if (t.isPending()) {
						currentTransfer = t;
						currentTransfer.setPending(false);
						break;
					}
				}
			}

			if (currentTransfer == null) {
				break;
			}

			currentTransfer.setProgress(0);
			publishProgress(currentTransfer.getTransferId(), 0);
			doInBackgoudForDownload();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);

		int transferId = values[0];
		int progress = values[1];

		Log.d("progress", Integer.toString(progress));
		switch (progress) {
		case 0:
			listener.onBeginTransfer(this, transferId);
			break;

		case 101: /* Finished */			
			listener.onEndTransfer(this, transferId);
			break;

		default:
			listener.onPregressUpdate(this, transferId, progress);	
			break;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

	protected abstract void doInBackgoudForDownload();
}
