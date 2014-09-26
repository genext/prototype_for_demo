
package com.example.proto.dto;

import java.io.Serializable;

public class SimpleDTO implements Serializable{

	private static final long serialVersionUID = 3862022531337516719L;	
	private static final int MENU_CODE_LENGTH = 8;
	
	private byte[] certKey = new byte[65];//인증 key
	private byte[] menuCode = new byte[MENU_CODE_LENGTH];//메뉴 코드
	private int contentsId;
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
	/**
	 * @return the contentsId
	 */
	public int getContentsId() {
		return contentsId;
	}
	/**
	 * @param contentsId the contentsId to set
	 */
	public void setContentsId(int contentsId) {
		this.contentsId = contentsId;
	}	
	
}
