
package com.example.proto.socket;

public interface PacketFactory {
	public PacketAgent choosePacketAgent(int type, int option) throws InvalidPacketAgentType; 
}
