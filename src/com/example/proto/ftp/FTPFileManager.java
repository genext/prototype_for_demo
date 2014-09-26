
package com.example.proto.ftp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

//추후에는 FTPFileManager를 통해 파일 정보를 가져오지 않고, 해당 정보의 FTP서버와 매칭된 미들서버에서 
//파일 정보를 가져와서 활용한다.
public class FTPFileManager {

	private FTPClient ftp;
	private String currnetPath;
	private OnFTPListener listener;
	
	public interface OnFTPListener{
		public void onFileLoadSuccess(ArrayList<FileEntity> fileList);
	}
	
	public FTPFileManager(OnFTPListener listener) {
		this.listener = listener;
	}

	public void doConnect() throws SocketException, IOException {
		ftp = new FTPClient();
		ftp.addProtocolCommandListener(new PrintCommandListener(
				new PrintWriter(System.out)));
		ftp.connect("199.180.157.161", 2002);
		int reply = ftp.getReplyCode();
		
		//check vuerify success
		if(!FTPReply.isPositiveCompletion(reply)){
			ftp.disconnect();
			return;
		}
		
		if (!ftp.login("a1", "upuserpw")) {
			ftp.logout();
			return;
		}
		currnetPath = ftp.printWorkingDirectory();
		loadFiles();
	}

	private void loadFiles() throws IOException {
		FTPFile[] list = ftp.listFiles(currnetPath, new FTPFileFilter() {

			@Override
			public boolean accept(FTPFile file) {
				// TODO Auto-generated method stub
				String fileName = file.getName();

				if (file.isDirectory()) {
					return !".".equals(fileName) && !"..".equals(fileName);
				}

				return !file.isSymbolicLink();
			}

		});

		// Scan all files
		ArrayList<FileEntity> fileList = new ArrayList<FileEntity>();
		if ((list != null) && (list.length > 0)) {			
			for (FTPFile sf : list) {
				if(!sf.isDirectory()){
					FileEntity df = new FileEntity();
					df.setName(sf.getName());
					if (currnetPath.endsWith(File.separator)) {
						df.setAbsolutePath(currnetPath + sf.getName());
					} else {
						df.setAbsolutePath(currnetPath + File.separator
								+ sf.getName());
					}
					df.setParentPath(currnetPath);
					df.setSize(sf.getSize());
					fileList.add(df);
				}			
			}			
		}	
		listener.onFileLoadSuccess(fileList);
	}	
}
