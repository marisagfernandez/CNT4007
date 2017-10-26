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
	
	private byte[] length;
	private byte[] type;
	private byte[] payload;
	

	public ActualMsg(){
		length = new byte[4];
		type = new byte[1];
		//payload = newbyte[]
	}
	
	public ActualMsg(byte[] length, byte[] type, byte[] payload){
		this.length = length;
		this.type = type;
		this.payload = payload;
	}
	
	public byte[] getLength() {
		return length;
	}

	public void setLength(byte[] length) {
		this.length = length;
	}

	public synchronized byte[] getType() {
		return type;
	}

	public synchronized void setType(byte[] type) {
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
		byteBuffer.put(length);
		byteBuffer.put(type);
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

