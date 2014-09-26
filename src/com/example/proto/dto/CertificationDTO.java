
package com.example.proto.dto;

import java.io.Serializable;

public class CertificationDTO implements Serializable{

	private static final long serialVersionUID = 3652074020700017744L;	
	private byte[] certKey = new byte[65];//인증 key
	private int retCode;//결과 코드
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
	 * @return the retCode
	 */
	public int getRetCode() {
		return retCode;
	}
	/**
	 * @param retCode the retCode to set
	 */
	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}
}


