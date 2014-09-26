
package com.example.proto.dto;

import java.io.Serializable;

/* H1 코드(인증 요청)의 전송 패킷 중 데이터 영역의 DTO */
public class CertificationKeyDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private byte[] id = new byte[35];//인증 요청 id
	private byte[] pw = new byte[35];//인증 요청 password
	private byte[] mac = new byte[18];//인증 요청 mac address
	private byte[] certKey = new byte[65];//인증 key
	private int retCode;//결과 코드
	
	public CertificationKeyDTO(){}
	
	public CertificationKeyDTO(byte[] id, byte[] pw, byte[] mac, byte[] certKey, int retCode){
		this.id = id;
		this.pw = pw;
		this.mac = mac;
		this.certKey = certKey;
		this.retCode = retCode;
	}

	/**
	 * @return the id
	 */
	public byte[] getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(byte[] id) {
		for(int i = 0; i < id.length; i++){
			this.id[i] = id[i];
		}		
	}

	/**
	 * @return the pw
	 */
	public byte[] getPw() {
		return pw;
	}

	/**
	 * @param pw the pw to set
	 */
	public void setPw(byte[] pw) {
		for(int i = 0; i < pw.length; i++){
			this.pw[i] = pw[i];
		}
	}

	/**
	 * @return the mac
	 */
	public byte[] getMac() {
		return mac;
	}

	/**
	 * @param mac the mac to set
	 */
	public void setMac(byte[] mac) {
		for(int i = 0; i < mac.length; i++){
			this.mac[i] = mac[i];
		}
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
	 * @return the retCode
	 */
	public int getRetCode() {
		return retCode;
	}

	/**
	 * @param retCode the retCode to set
	 */
	public void setRetCode(int retCode) {
		this.retCode = retCode & 0xffff;
	}
	
	
}
