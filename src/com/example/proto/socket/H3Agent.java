
package com.example.proto.socket;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.proto.dto.SimpleDTO;
import com.example.proto.dto.SocketDTO;
import com.example.proto.util.MyLog;
import com.example.proto.util.Tools;

public class H3Agent extends PacketAgent{

	private static final int CODE = 1100;
	
	protected H3Agent(int option) {
		super(option);		
	}

	@Override
	protected void createPacketData(Object... params) {
		// TODO Auto-generated method stub
		
		//params를 확인해서 해당 코드의 패킷이 아닐 경우 exception 발생해야 함(InvalidPacketAgentParameter)
		
		dto = new SocketDTO();		
		
		//윈도우 서버의 해당 데이터 타입이 unsigned int이므로 
		dto.setCode( (CODE + option) & 0xffff);//code	
		
		//데이터 전달을 위한 DTO 작성
		SimpleDTO data = new SimpleDTO();
		int dataLength = data.getCertKey().length;
		
		//h3탭의 경우 전송 내용이 초기에 같아서 하나의 클래스로 구현하였으나 전송 내용이 달라지면서 해당 내용 추가(h3의 경우도 옵션없이 각각의 클래스로 분리 하면 좋을듯)		
		if(option == Packet.OPTION_H3_PROGRAM || option == Packet.OPTION_H3_PROGRAM_DETAIL){
			dataLength += data.getMenuCode().length;
			MyLog.d("menucode : %s length : %d datalength : %d", params[1].toString(), params[1].toString().length(), dataLength);
		}	
		
		dto.setDataLength(dataLength);//data length
		
		dto.setTotalLength(CODE_MEMORY_ALLOCATE +
								 DATA_LENGTH_MEMORY_ALLOCATE +
								 TOTAL_LENGTH_MEMORY_ALLOCATE +
								 dataLength +
								 TAIL_MEMORY_ALLOCATE);//total length		
		
		data.setCertKey(((String)params[0]).getBytes());
			
		if(dto.getCode() == CODE + Packet.OPTION_H3_PROGRAM || dto.getCode() == CODE + Packet.OPTION_H3_PROGRAM_DETAIL){
			data.setMenuCode(((String)params[1]).getBytes());	
		}
		
		dto.setData(data);	
		//MyLog.d("code %s", dto.getCode());
		//MyLog.d("data %s", dto.getData().toString());
		//MyLog.d("length %d", dto.getDataLength());
		//MyLog.d("total len %d", dto.getTotalLength());
	}
	
	@Override
	protected <T> T convertDAO(Class<T> clazz, byte[] rcv) {
		// TODO Auto-generated method stub
		
		int dataLength = Tools.byteArrayToInt(Tools.getBytes(rcv, 8, 4));
		String data = Tools.byteArrayToString(Tools.getBytes(rcv, 12, dataLength));
		String[] splits = data.split("#<");
		int resultCode = Integer.parseInt(splits[0]);
		String realData = splits[1].replaceAll("'", "\"");
		setResult(resultCode);
		
		try {
			return clazz.getConstructor(String.class, Integer.class).newInstance(realData.trim(), resultCode);
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
		buffer.put(((SimpleDTO)dto.getData()).getCertKey());//cert key
		
		//1102, 1103(program list reuqest, program detail request 의 경우 요청하는 컨텐츠의 코드값을 넣어 줘야 함
		if(dto.getCode() == CODE + Packet.OPTION_H3_PROGRAM || dto.getCode() == CODE + Packet.OPTION_H3_PROGRAM_DETAIL){
			buffer.put(((SimpleDTO)dto.getData()).getMenuCode());//menu code
		}
		
		buffer.put(dto.getTail());//tail		
		result = buffer.array();
		
		return result;
	}

}
