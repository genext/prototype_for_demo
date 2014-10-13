
package com.example.proto.dto;

import java.io.Serializable;

public class BasicDTO implements Serializable{

	private static final long serialVersionUID = 346925984774273628L;
	private static final int CERT_KEY_LENGTH = 64;
	
	private byte[] certKey = new byte[CERT_KEY_LENGTH + 1];//인증 key
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
}
