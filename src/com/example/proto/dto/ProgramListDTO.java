
package com.example.proto.dto;

import java.io.Serializable;

public class ProgramListDTO implements Serializable{

	private static final long serialVersionUID = 3862022531337516719L;	
	private static final int CERT_KEY_LENGTH = 64;
	private static final int MENU_CODE_LENGTH = 10;
	
	private byte[] certKey = new byte[CERT_KEY_LENGTH + 1];//인증 key
	private byte[] menuCode = new byte[MENU_CODE_LENGTH + 1];//메뉴 코드
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
	 * @return the menuCode
	 */
	public byte[] getMenuCode() {
		return menuCode;
	}
	/**
	 * @param menuCode the menuCode to set
	 */
	public void setMenuCode(byte[] menuCode) {
		for(int i = 0; i < menuCode.length; i++){
			this.menuCode[i] = menuCode[i];
		}
	}
}
