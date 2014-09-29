package com.example.proto.socket;

public class Packet implements PacketFactory{
	
	//agent type 정의(코드값 정의)
	public final static int CERT = 0;//셋탑 설치 후 첫 인증 요청(코드 : 1000)
	public final static int H2 = 1;//인증 요청(코드 : 1001)
	public final static int H3 = 2;//데이터 요청(menu, program list, program detail, ad list)
	public final static int PAY = 3;
	public final static int FTP = 4;
	
	//H3과 같이 동일한 패킷 구조에 데이터만 다를 경우 옵션 형태로 처리하기 위해 정의
	public final static int OPTION_NONE = -1;//옵션이 없을 경우
	public final static int OPTION_H3_MENU_VERSION = 0;//코드 : 1100
	public final static int OPTION_H3_MENU = 1;//코드 : 1101
	public final static int OPTION_H3_PROGRAM = 2;//코드 : 1102
	public final static int OPTION_H3_PROGRAM_DETAIL = 3;//코드 : 1103
	public final static int OPEION_H3_AD = 4;//코드 : 1104
	
	private int agentType;//프로토콜 종류(코드 종류-h1, h2, h3)
	private int option;//프로토콜 옵션(h3의 옵션이 존재하므로..ex)1100, 1101, 1102)
	
	private PacketAgent agent;
	
	public Packet(int whitch, int option){//인자값에 대한 복잡도가 늘어날 경우 Builder pattenr으로 변경 요망
		
		this.agentType = whitch;
		this.option = option;
		
		try {
			this.choosePacketAgent(whitch, option);
		} catch (InvalidPacketAgentType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	@Override
	public PacketAgent choosePacketAgent(int type, int option)
			throws InvalidPacketAgentType {
		// TODO Auto-generated method stub		
		switch(agentType){
		case CERT :
			return agent = new CertAgent(option);
		case H2 :
			return agent = new H2Agent(option);
		case H3 :
			return agent = new H3Agent(option);
		case PAY :
			return agent = new PayAgent(option);
		case FTP :
			return agent = new FtpAgent(option);
		default :
			throw new InvalidPacketAgentType(type);
				
		}		
	}
	
	public PacketAgent getPacketAgent(){
		if(agent != null){
			return agent;
		}
		return null;
	}
	
	public int getAgentType(){
		return this.agentType;
	}
	
	public int getOption(){
		return this.option;
	}
	
	public void createPacketData(Object... params){
		if(agent != null){
			agent.createPacketData(params);
		}
	}
	
	public byte[] convertByteArray(){
		if(agent != null){
			return agent.convertDTOToByteArray();
		}
		return null;
	}
	
	public <T> T convertDAO(Class<T> clazz, byte[] rcv){
		if(agent != null){
			return agent.convertDAO(clazz, rcv);
		}
		return null;
	}
	
	public int getResult(){
		if(agent != null){
			return agent.getResult();
		}
		return -1;
	}
	
}
