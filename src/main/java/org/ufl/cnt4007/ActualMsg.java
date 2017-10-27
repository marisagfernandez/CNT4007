package org.ufl.cnt4007;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

public class ActualMsg {

	public enum Type{
		CHOKE(0), UNCHOKE(1), INTERESTED(2), NONINTERESTED(3), HAVE(4), BITFIELD(5), REQUEST(6), PIECE(7); 
		
		private final byte typeNum;
		Type(int typeNum){
			this.typeNum = (byte)typeNum;
		}
		
	}
	private final Type[] typeValues = Type.values();
	private int length;
	private Type msgType;
	private byte[] payload;
	
	
	public ActualMsg(byte[] payload) {
		//ByteBuffer bytes = ByteBuffer.wrap(payload);
		for (byte b : payload) {
			System.out.print(b + " ");
		}
		System.out.println();
		this.msgType = typeValues[payload[0]];
		if(payload.length > 1) {
			this.payload = Arrays.copyOfRange(payload, 1, payload.length - 1);
		} else {
			this.payload = null;
		}


	}
	public Type getMsgType() {
		return this.msgType;
	}
	public byte[] getPayload() {
		return this.payload;
	}

	
	public ActualMsg(int length, int type){
		this.length = length;
		//this.payload = payload;
	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

//	public synchronized byte[] getPayload() {
//		if(payload != null){
//			return payload;
//		}
//		return payload;
//	}
//
//	public synchronized void setPayload(byte[] payload) {
//		this.payload = payload;
//	}
	
	public static byte[] makeChoke() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		//Add length
		bytes.putInt(1);
		bytes.put(Type.CHOKE.typeNum);
		
		return bytes.array();
	}
	
	public static byte[] makeUnchoke() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		//Add length
		bytes.putInt(1);
		bytes.put(Type.UNCHOKE.typeNum);
		
		return bytes.array();
	}
	public static byte[] makeInterested() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		//Add length
		bytes.putInt(1);
		bytes.put(Type.INTERESTED.typeNum);
		
		return bytes.array();
	}
	public static byte[] makeNotInterested() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		//Add length
		bytes.putInt(1);
		bytes.put(Type.NONINTERESTED.typeNum);
		
		return bytes.array();
	}
	
	public static byte[] makeHave(int payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(9);
		//Add length
		bytes.putInt(5);
		bytes.put(Type.HAVE.typeNum);
		bytes.putInt(payload);
		
		return bytes.array();
	}
	public static byte[] makeBitfield(BitSet payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(5 + (payload.size() / 8));	
		//Add length
		bytes.putInt(1 + payload.size() / 8);
		bytes.put(Type.BITFIELD.typeNum);
		byte[] b = payload.toByteArray();
		bytes.put(b);
		
		return bytes.array();
	}
	public static byte[] makeRequest(int payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(5 + payload);
		//Add length
		bytes.putInt(5); //should be length of message
		bytes.put(Type.REQUEST.typeNum);
		bytes.putInt(payload);
		
		return bytes.array();
	}
	public static byte[] makePiece(int index, byte[] payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(5 + index + payload.length);
		//Add length
		bytes.putInt(1 + index + payload.length);
		bytes.put(Type.PIECE.typeNum);
		bytes.putInt(index);
		bytes.put(payload);
		
		return bytes.array();
	}

}

