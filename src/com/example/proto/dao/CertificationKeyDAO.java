
package com.example.proto.dao;

//H1 코드(인증 요청)의 서버 요청 결과 DAO
public class CertificationKeyDAO {
	private String certKey;
	private int resultCode;
	
	public CertificationKeyDAO(String certKey, Integer resultCode){
		this.certKey = certKey;
		this.resultCode = resultCode;
	}
	/**
	 * @return the certKey
	 */
	public String getCertKey() {
		return certKey;
	}
	/**
	 * @param certKey the certKey to set
	 */
	public void setCertKey(String certKey) {
		this.certKey = certKey;
	}
	/**
	 * @return the resultCode
	 */
	public int getResultCode() {
		return resultCode;
	}
	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	
	
}
