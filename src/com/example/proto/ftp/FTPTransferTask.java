
package com.example.proto.ftp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

public class FTPTransferTask extends TransferTask {

	private FTPClient ftpClient;

	public FTPTransferTask(List<Transfer> transferList,
			TransferProgressListener listener) {
		super(transferList, listener);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(Transfer... transfers) {
		String result;
		ftpClient = new FTPClient();
		ftpClient.addProtocolCommandListener(new PrintCommandListener(
				new PrintWriter(System.out)));
		result = super.doInBackground(transfers);

		// disconnect ftp client
		if ((ftpClient != null) && ftpClient.isConnected()) {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	protected void doInBackgoudForDownload() {
		try {
			if (!ftpClient.isConnected()) {
				connect();
			}
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			// Go to directory
            if (!ftpClient.printWorkingDirectory().equals(currentTransfer.getSourcePath())) {
            	ftpClient.changeWorkingDirectory(currentTransfer.getSourcePath());
            }

            // Open local file
            FileOutputStream fos = new FileOutputStream(currentTransfer.getFullDestinationPath());
            CountingOutputStream cos = new CountingOutputStream(fos) {
                    protected void beforeWrite(int n) {
                            super.beforeWrite(n);

                            Log.d("", "count : " + Integer.toString(getCount()) + " fileSize : " + Long.toString(20 * 1024 * 1024));
                            //jkoh TODO 파일 사이즈를 가져와서 20M이상일 경우 완료 처리하는 코드 필요함, 현재는 실제 백분위로 계산
                            //int progress = Math.round((getCount() * 100) / currentTransfer.getFileSize());
                            int progress = Math.round((getCount() * 100) / (5 * 1024 * 1024));
                            currentTransfer.setProgress(progress);
                            //Log.d("test", Integer.toString(progress));
                            publishProgress(currentTransfer.getTransferId(), progress);
                    }
            };

            // Download file
            boolean downloadSuccess = ftpClient.retrieveFile(currentTransfer.getFileName(), cos);
            Log.d("", Boolean.toString(downloadSuccess));

            // Close local file
            fos.close();
            cos.close();
            ftpClient.logout();
            disconnect();
            
            // End of transfer
            publishProgress(currentTransfer.getTransferId(), 101);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connect() throws SocketException, IOException {
		
		//해당 함수에서는 파일에 대한 관리없이 dummy로 작성 함
		ftpClient.connect(currentTransfer.getIp(), currentTransfer.getPort());

		int reply = ftpClient.getReplyCode();
		
		//check vuerify success
		if(!FTPReply.isPositiveCompletion(reply)){
			ftpClient.disconnect();
			return;
		}
		
		ftpClient.setDataTimeout(10000);
		if (!ftpClient.login("funnc", "funnc1234")) {
			ftpClient.logout();
			return;
		}
		
		ftpClient.enterLocalPassiveMode();	
	}
	
	private void disconnect() throws IOException{
		if(ftpClient != null && ftpClient.isConnected()){
			ftpClient.disconnect();
		}
	}

}
