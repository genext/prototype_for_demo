
package com.example.proto.socket;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.proto.dto.CertificationDTO;
import com.example.proto.dto.SocketDTO;
import com.example.proto.util.Tools;

public class H2Agent extends PacketAgent{	

	private static final int CODE = 1001;
	
	protected H2Agent(int option) {
		super(option);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createPacketData(Object... params) {
		// TODO Auto-generated method stub
		
		//params를 확인해서 해당 코드의 패킷이 아닐 경우 exception 발생해야 함(InvalidPacketAgentParameter)
		
		dto = new SocketDTO();		
		
		//윈도우 서버의 해당 데이터 타입이 unsigned int이므로 
		dto.setCode(CODE & 0xffff);//code	
		
		//데이터 전달을 위한 DTO 작성
		CertificationDTO data = new CertificationDTO();
		int dataLength = data.getCertKey().length +  RETURN_CODE_MEMORY_ALLOCATE;
		dto.setDataLength(dataLength);//data length
		
		dto.setTotalLength(CODE_MEMORY_ALLOCATE +
								 DATA_LENGTH_MEMORY_ALLOCATE +
								 TOTAL_LENGTH_MEMORY_ALLOCATE +
								 dataLength +
								 TAIL_MEMORY_ALLOCATE);//total length		
		
		data.setCertKey(((String)params[0]).getBytes());		
		dto.setData(data);	
	}
	
	@Override
	protected <T> T convertDAO(Class<T> clazz, byte[] rcv) {
		// TODO Auto-generated method stub
		int resultCode = Tools.byteArrayToInt(Tools.getBytes(rcv, 77, 4));		
		setResult(resultCode);
		
		try {
			return clazz.getConstructor(Integer.class).newInstance(resultCode);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	protected byte[] convertDTOToByteArray() {
		// TODO Auto-generated method stub
		//Object 형태로 된 DTO를 socket 통신을 위해 byte[]로 변경
		ByteBuffer buffer = ByteBuffer.allocate(super.dto.getTotalLength());
		byte[] result = new byte[super.dto.getTotalLength()];
		buffer.order(ByteOrder.LITTLE_ENDIAN);		

		buffer.putInt(dto.getCode() & 0xffff);//code		
		buffer.putInt(dto.getTotalLength() & 0xffff);//total length	
		buffer.putInt(dto.getDataLength() & 0xffff);//data length
		buffer.put(((CertificationDTO)dto.getData()).getCertKey());//cert key
		buffer.putInt(((CertificationDTO)dto.getData()).getRetCode() & 0xffff);		
		buffer.put(dto.getTail());//tail		
		result = buffer.array();
		
		return result;
	}

}
