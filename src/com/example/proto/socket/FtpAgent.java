
package com.example.proto.socket;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.example.proto.dto.ContentsDownDTO;
import com.example.proto.dto.SocketDTO;
import com.example.proto.util.MyLog;
import com.example.proto.util.Tools;

public class FtpAgent extends PacketAgent{	

private static final int CODE = 1300;
	
	protected FtpAgent(int option) {
		super(option);		
	}

	@Override
	protected void createPacketData(Object... params) {
		// TODO Auto-generated method stub
		
		//params를 확인해서 해당 코드의 패킷이 아닐 경우 exception 발생해야 함(InvalidPacketAgentParameter)
		
		dto = new SocketDTO();		
		
		//윈도우 서버의 해당 데이터 타입이 unsigned int이므로 
		dto.setCode(CODE & 0xffff);//code	
		
		//데이터 전달을 위한 DTO 작성
		ContentsDownDTO data = new ContentsDownDTO();
		int dataLength = data.getCertKey().length;
		dataLength += 16;//contentsId allocate size TODO datalength 길이 구하는 것 코딩할 것.
		dto.setDataLength(dataLength);//data length
		
		dto.setTotalLength(CODE_MEMORY_ALLOCATE +
								 DATA_LENGTH_MEMORY_ALLOCATE +
								 TOTAL_LENGTH_MEMORY_ALLOCATE +
								 dataLength +
								 TAIL_MEMORY_ALLOCATE);//total length		
		
		data.setCertKey(((String)params[0]).getBytes());
		data.setContentsId(((Long)params[1]).longValue());
		data.setFileSize(0); // TODO 실제로 로컬파일이 있으면 로컬 파일의 크기를 구해서 넘겨주도록.
		MyLog.d("1300 data : %s", data.toString());
		dto.setData(data);	
	}
	
	@Override
	protected <T> T convertDAO(Class<T> clazz, byte[] rcv) {
		
		int dataLength = Tools.byteArrayToInt(Tools.getBytes(rcv, 8, 4));
		String data = Tools.byteArrayToString(Tools.getBytes(rcv, 12, dataLength));
		
		// TODO array index out of bound error 처리 코드 삽입.
		MyLog.d("ftp info : %s", data);
		String[] splits = data.split("#<");
		int resultCode = Integer.parseInt(splits[0]);
		if (resultCode == 9) {
			String realData = splits[1];	
			long fileSize = Long.parseLong(splits[2].trim());
			
			setResult(resultCode);
			
			try {
				return clazz.getConstructor(String.class, Long.class, Integer.class).newInstance(realData.trim(), fileSize, resultCode);
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
		}
		else {
			// TODO 에러 메시지 표시?
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
		buffer.put(((ContentsDownDTO)dto.getData()).getCertKey());//cert key
		buffer.putLong(((ContentsDownDTO)dto.getData()).getContentsId() & 0xffffffff);//contents id	
		buffer.putLong(((ContentsDownDTO)dto.getData()).getFileSize() & 0xffffffff);
		buffer.put(dto.getTail());//tail		
		result = buffer.array();
		
		return result;
	}

}
