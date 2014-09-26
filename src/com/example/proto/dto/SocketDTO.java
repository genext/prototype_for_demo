
package com.example.proto.dto;

import java.io.Serializable;

/* 인증 요청 전체 패킷 DTO */
public class SocketDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int code;;//코드
	private int dataLength;//데이터 영역의 길이
	private int totalLength;//전문 전체 길이
	private Object data;//데이터 영역
	private byte[] tail = new byte[]{65, 78, 68, 0};//tail(ANDR에서 AND0로 변경)

	public SocketDTO() {}
	
	public SocketDTO(int code, int dataLength, int totalLength, Object data) {
		// TODO Auto-generated constructor stub
		this.code = code;
		this.dataLength = dataLength;
		this.totalLength = totalLength;
		this.data = data;
	}
	
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return the dataLength
	 */
	public int getDataLength() {
		return dataLength;
	}
	/**
	 * @param dataLength the dataLength to set
	 */
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}
	/**
	 * @return the totalLength
	 */
	public int getTotalLength() {
		return totalLength;
	}
	/**
	 * @param totalLength the totalLength to set
	 */
	public void setTotalLength(int totalLength) {
		this.totalLength = totalLength;
	}
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the tail
	 */
	public byte[] getTail() {
		return tail;
	}

	/**
	 * @param tail the tail to set
	 */
	public void setTail(byte[] tail) {
		this.tail = tail;
	}

	
	
}
