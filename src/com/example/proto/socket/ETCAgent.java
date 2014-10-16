
package com.example.proto.socket;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.proto.dto.BasicDTO;
import com.example.proto.dto.SocketDTO;
import com.example.proto.util.MyLog;
import com.example.proto.util.Tools;

public class ETCAgent extends PacketAgent{

	private static final int CODE = 1100;
	
	protected ETCAgent(int option) {
		super(option);		
	}

	@Override
	protected void createPacketData(Object... params) {
		//params를 확인해서 해당 코드의 패킷이 아닐 경우 exception 발생해야 함(InvalidPacketAgentParameter)
		
		dto = new SocketDTO();		
		
		//윈도우 서버의 해당 데이터 타입이 unsigned int이므로 
		dto.setCode( (CODE + option) & 0xffff);//code	
		
		//데이터 전달을 위한 DTO 작성
		BasicDTO data = new BasicDTO();
		int dataLength = data.getCertKey().length;
		
		dto.setDataLength(dataLength);//data length
		
		dto.setTotalLength(CODE_MEMORY_ALLOCATE +
								 DATA_LENGTH_MEMORY_ALLOCATE +
								 TOTAL_LENGTH_MEMORY_ALLOCATE +
								 dataLength +
								 TAIL_MEMORY_ALLOCATE);//total length		
		
		data.setCertKey(((String)params[0]).getBytes());
			
		dto.setData(data);	
		//MyLog.d("code %s", dto.getCode());
		//MyLog.d("data %s", dto.getData().toString());
		//MyLog.d("length %d", dto.getDataLength());
		//MyLog.d("total len %d", dto.getTotalLength());
	}
	
	@Override
	protected <T> T convertDAO(Class<T> clazz, byte[] rcv) {
		int dataLength = Tools.byteArrayToInt(Tools.getBytes(rcv, 8, 4));
		String data = Tools.byteArrayToString(Tools.getBytes(rcv, 12, dataLength));
		String[] splits = data.split("#<");
		int resultCode = Integer.parseInt(splits[0]);
		String realData = splits[1].replaceAll("'", "\"");
		setResult(resultCode);
		
		try {
			return clazz.getConstructor(String.class, Integer.class).newInstance(realData.trim(), resultCode);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
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
		buffer.put(((BasicDTO)dto.getData()).getCertKey());//cert key
				
		buffer.put(dto.getTail());//tail		
		result = buffer.array();
		
		return result;
	}

}