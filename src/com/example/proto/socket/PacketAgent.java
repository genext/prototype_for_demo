
package com.example.proto.socket;

import com.example.proto.dto.SocketDTO;


public abstract class PacketAgent {
	public static final int CODE_MEMORY_ALLOCATE = 4;//전송 패킷 중 code 영역의 메모리 크기
	public static final int DATA_LENGTH_MEMORY_ALLOCATE = 4;//전송 패킷 중 data length 영역의 메모리 크기
	public static final int TOTAL_LENGTH_MEMORY_ALLOCATE = 4;//전송 패킷 중 total length 영역의 메모리 크기
	public static final int RETURN_CODE_MEMORY_ALLOCATE = 4;//전송 패킷 중 retCode 영역의 메모리 크기	
	public static final int TAIL_MEMORY_ALLOCATE = 4;//전송 패킷 중 tail 영역의 메모리 크기
	
	
	protected SocketDTO dto;//전송 되는 패킷 정의 클래스	
	protected int option;
	protected int result;

	
	//기본 생성자
	protected PacketAgent(int option){ this.option = option; }
	
	/**
	 * <PRE>
	 * Comment : <br>
	 * 전송해야 할 패킷을 만듬(해당 데이터를 DTO로 만듬)	
	 * </PRE>
	 * @param params 패킷에 들어갈 데이터(코드 종류에 따라 데이터가 다르므로 가변인자로 정의)
	 */
	protected abstract void createPacketData(Object... params);
	
	/**
	 * <PRE>
	 * Comment : <br>	 
	 * 서버로부터 받은 데이터를 사용이 용이하도록 클래스로 변환 시켜주는 역할
	 * </PRE>
	 * @param clazz 결과값을 받을 클래스 타입
	 * @param rcv 서버로부터 받은 데이터
	 * @return
	 */
	protected abstract <T> T convertDAO(Class<T> clazz, byte[] rcv);	
	
	/**
	 * <PRE>
	 * Comment : <br>
	 * DTO를 socket 통신을 위해 byte[]로 변경	
	 * </PRE>
	 * @return
	 */
	protected abstract byte[] convertDTOToByteArray();	
	
	public int getResult(){
		return result;
	}
	
	public void setResult(int result){
		this.result = result;
	}
	

	

	
}
