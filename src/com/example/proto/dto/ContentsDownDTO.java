
package com.example.proto.dto;

import java.io.Serializable;

public class ContentsDownDTO implements Serializable{

	private static final long serialVersionUID = -5178571661027364370L;
	
	private byte[] certKey = new byte[65];//인증 key
	private long contentsId;//컨텐츠 id
	private long fileSize;
	
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the certKey
	 */
	public byte[] getCertKey() {
		return certKey;
	}

	/**
	 * @param certKey the certKey to set
	 */
	public void setCertKey(byte[] certKey) {
		for(int i = 0; i < certKey.length; i++){
			this.certKey[i] = certKey[i];
		}
	}

	/**
	 * @return the contentsId
	 */
	public long getContentsId() {
		return contentsId;
	}

	/**
	 * @param contentsId the contentsId to set
	 */
	public void setContentsId(long contentsId) {
		this.contentsId = contentsId;
	}
}
