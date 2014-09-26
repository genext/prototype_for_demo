
package com.example.proto.dao;

public class ContentsDownDAO {
	private String downInfo;
	private long fileSize;
	private int result;
	
	public ContentsDownDAO(String downInfo, Long fileSize, Integer result){
		this.downInfo = downInfo;
		this.fileSize = fileSize;
		this.result = result;
	}	

	/**
	 * @return the downInfo
	 */
	public String getDownInfo() {
		return downInfo;
	}
	/**
	 * @param downInfo the downInfo to set
	 */
	public void setDownInfo(String downInfo) {
		this.downInfo = downInfo;
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
	 * @return the result
	 */
	public int getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(int result) {
		this.result = result;
	}	
}
