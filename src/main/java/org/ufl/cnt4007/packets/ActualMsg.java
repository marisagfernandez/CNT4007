package org.ufl.cnt4007.packets;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class ActualMsg {

	public enum Type{
		CHOKE(0), UNCHOKE(1), INTERESTED(2), NONINTERESTED(3), HAVE(4), BITFIELD(5), REQUEST(6), PIECE(7); 
		
		private final byte typeNum;
		Type(int typeNum){
			this.typeNum = (byte)typeNum;
		}
		
	}
	
	private int length;
	//private byte[] type;
	//private byte[] payload;

	
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
	
	public byte[] makeChoke() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		bytes.putInt(1);
		bytes.put(Type.CHOKE.typeNum);
		
		return bytes.array();
	}
	
	public byte[] makeUnchoke() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		bytes.putInt(1);
		bytes.put(Type.UNCHOKE.typeNum);
		
		return bytes.array();
	}
	public byte[] makeInterested() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		bytes.putInt(1);
		bytes.put(Type.INTERESTED.typeNum);
		
		return bytes.array();
	}
	public byte[] makeNotInterested() {
		ByteBuffer bytes = ByteBuffer.allocate(5);
		bytes.putInt(1);
		bytes.put(Type.NONINTERESTED.typeNum);
		
		return bytes.array();
	}
	
	public byte[] makeHave(int payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(4 + length);
		bytes.putInt(5);
		bytes.put(Type.HAVE.typeNum);
		bytes.putInt(payload);
		
		return bytes.array();
	}
	public byte[] makeBitfield(BitSet payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(4 + length);	
		bytes.putInt(1 + payload.length());
		bytes.put(Type.BITFIELD.typeNum);
		byte[] b = payload.toByteArray();
		bytes.put(b);
		
		return bytes.array();
	}
	public byte[] makeRequest(int payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(4 + length);
		bytes.putInt(1 + payload);
		bytes.put(Type.REQUEST.typeNum);
		bytes.putInt(payload);
		
		return bytes.array();
	}
	public byte[] makePiece(int index, byte[] payload) {
		
		ByteBuffer bytes = ByteBuffer.allocate(4 + length);
		bytes.putInt(1 + index + payload.length);
		bytes.put(Type.PIECE.typeNum);
		bytes.putInt(index);
		bytes.put(payload);
		
		return bytes.array();
	}

}

