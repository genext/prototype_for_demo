
package com.example.proto.dao;

public class ContentsPayResultDAO {
	private long fileID;
	private int result;
	
	public ContentsPayResultDAO(Long amount, Integer result){
		this.fileID = fileID;
		this.result = result;
	}	
	

	public long getFileID() {
		return fileID;
	}


	public void setFileID(long fileID) {
		this.fileID = fileID;
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
