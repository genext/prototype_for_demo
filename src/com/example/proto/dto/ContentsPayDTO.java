
package com.example.proto.dto;

import java.io.Serializable;

public class ContentsPayDTO implements Serializable{

	private static final long serialVersionUID = 5504318310608382385L;
	
	private byte[] certKey = new byte[65];//인증 key
	private long contentsId;//컨텐츠 id
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
