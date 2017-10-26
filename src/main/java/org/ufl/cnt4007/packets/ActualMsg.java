package org.ufl.cnt4007.packets;

import java.nio.ByteBuffer;

public class ActualMsg {

	public static final byte CHOKE = 0;
	public static final byte UNCHOKE = 1;
	public static final byte INTERESTED = 2;
	public static final byte NONINTERESTED = 3;
	public static final byte HAVE = 4;
	public static final byte BITFIELD = 5;
	public static final byte REQUEST = 6;
	public static final byte PIECE = 7;
	
	private int length;
	private int type;
	private byte[] payload;
	

	public ActualMsg(){
		
	}
	
	public ActualMsg(int length, int type, byte[] payload){
		this.length = length;
		this.type = type;
		this.payload = payload;
	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public synchronized int getType() {
		return type;
	}

	public synchronized void setType(int type) {
		this.type = type;
	}

	public synchronized byte[] getPayload() {
		if(payload != null){
			return payload;
		}
		return payload;
	}

	public synchronized void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public ByteBuffer toByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		byteBuffer.putInt(length);
		byteBuffer.putInt(type);
		byteBuffer.put(payload);
		return byteBuffer;
		
	}
	
	public static ActualMsg getActualMsg(ByteBuffer byteBuffer){
		ActualMsg actualMsg = new ActualMsg();
		byte[] b = byteBuffer.array();
		//Take byte[] and set actualMsg
		
		
		
		
		
		return actualMsg;
	}
	
	

}

