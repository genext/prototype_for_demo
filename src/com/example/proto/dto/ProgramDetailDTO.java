
package com.example.proto.dto;

import java.io.Serializable;

public class ProgramDetailDTO implements Serializable{

	private static final long serialVersionUID = -6973153207733482529L;
	private static final int CERT_KEY_LENGTH = 64;
	
	private byte[] certKey = new byte[CERT_KEY_LENGTH + 1];//인증 key
	private long programCode;
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
	public long getProgramCode() {
		return programCode;
	}
	public void setProgramCode(long programCode) {
		this.programCode = programCode;
	}	
}
