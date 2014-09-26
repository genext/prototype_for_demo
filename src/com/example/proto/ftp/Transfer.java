
package com.example.proto.ftp;

import java.io.File;

public class Transfer {

	private int transferId;	
	private boolean checked;
	private boolean pending;
	private String fileName;
	private String sourcePath;
	private String destPath;
	private long fileSize;
	private int progress;
	private String ip;
	private int port;
	
	
	
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	public Transfer(int id){
		this.transferId = id;
	}
	/**
	 * @return the transferId
	 */
	public int getTransferId() {
		return transferId;
	}
	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(int transferId) {
		this.transferId = transferId;
	}
	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}
	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	/**
	 * @return the pending
	 */
	public boolean isPending() {
		return pending;
	}
	/**
	 * @param pending the pending to set
	 */
	public void setPending(boolean pending) {
		this.pending = pending;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}
	/**
	 * @param sourcePath the sourcePath to set
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	/**
	 * @return the destPath
	 */
	public String getDestPath() {
		return destPath;
	}
	/**
	 * @param destPath the destPath to set
	 */
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}
	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public String getFullSourcePath(){
		StringBuilder sb = new StringBuilder();
		sb.append(sourcePath);
		if(!sourcePath.endsWith(File.separator)){
			sb.append(File.separator);
		}
		
		sb.append(sourcePath);
		return sb.toString();
	}
	
	public String getFullDestinationPath(){
		StringBuilder sb = new StringBuilder();
		sb.append(destPath);
		if(!destPath.endsWith(File.separator)){
			sb.append(File.separator);
		}
		sb.append(fileName);
		return sb.toString();
	}
	
	public String destinationPath(){
		return "Local:" + getFullDestinationPath();
	}
	
	
	
	
}
