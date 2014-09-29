
package com.example.proto.socket;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.proto.dto.CertificationKeyDTO;
import com.example.proto.dto.SocketDTO;
import com.example.proto.util.Tools;


public class CertAgent extends PacketAgent{	
	
	private static final int CODE = 1000;

	protected CertAgent(int option) {
		super(option);
		
	}

	@Override
	protected void createPacketData(Object... params) {
		
		//params를 확인해서 해당 코드의 패킷이 아닐 경우 exception 발생해야 함(InvalidPacketAgentParameter)
		
		dto = new SocketDTO();		
		
		//윈도우 서버의 해당 데이터 타입이 unsigned int이므로 
		dto.setCode(CODE & 0xffff);//code	
		
		//데이터 전달을 위한 DTO 작성
		CertificationKeyDTO data = new CertificationKeyDTO();
		int dataLength = data.getId().length + data.getPw().length + data.getMac().length + data.getCertKey().length +  RETURN_CODE_MEMORY_ALLOCATE;
		dto.setDataLength(dataLength);//data length
		
		dto.setTotalLength(CODE_MEMORY_ALLOCATE +
								 DATA_LENGTH_MEMORY_ALLOCATE +
								 TOTAL_LENGTH_MEMORY_ALLOCATE +
								 dataLength +
								 TAIL_MEMORY_ALLOCATE);//total length		
		
		data.setId(((String)params[0]).getBytes());
		data.setPw(((String)params[1]).getBytes());
		
		//mac 주소를 가져오지 못하는 단말기는 우짜실껀지..
		data.setMac(((String)params[2]).getBytes());			
		dto.setData(data);		
	}
	
	@Override
	protected <T> T convertDAO(Class<T> clazz, byte[] rcv) {
		// TODO Auto-generated method stub
		
		String certKey = Tools.byteArrayToString(Tools.getBytes(rcv, 100, 65));
		int resultCode = Tools.byteArrayToInt(Tools.getBytes(rcv, 165, 4));
		setResult(resultCode);
		
		try {
			return clazz.getConstructor(String.class, Integer.class).newInstance(certKey.trim(), resultCode);
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
		
		//Object 형태로 된 DTO를 socket 통신을 위해 byte[]로 변경
		ByteBuffer buffer = ByteBuffer.allocate(super.dto.getTotalLength());
		byte[] result = new byte[super.dto.getTotalLength()];
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		buffer.putInt(dto.getCode() & 0xffff);//code		
		buffer.putInt(dto.getTotalLength() & 0xffff);//total length	
		buffer.putInt(dto.getDataLength() & 0xffff);//data length
		buffer.put(((CertificationKeyDTO)dto.getData()).getId());//id
		buffer.put(((CertificationKeyDTO)dto.getData()).getPw());//pw
		buffer.put(((CertificationKeyDTO)dto.getData()).getMac());//mac
		buffer.put(((CertificationKeyDTO)dto.getData()).getCertKey());//cert key
		buffer.putInt(((CertificationKeyDTO)dto.getData()).getRetCode() & 0xffff);		
		buffer.put(dto.getTail());//tail
		result = buffer.array();
		return result;
	}	
	
	
}
