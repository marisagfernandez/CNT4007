package org.ufl.cnt4007.packets;

import java.util.BitSet;
import java.nio.ByteBuffer;


public class Handshake {

	private byte[] header;
	private byte[] zeroBits;
	private int peerID;
	
	
	public Handshake(){
		header = new byte[18];
		zeroBits = new byte[10];
	}
	
	public Handshake(byte[] header, byte[] zeroBits, int peerID){
		this.header = header;
		this.zeroBits = zeroBits;
		this.peerID = peerID;
		
	}


	public byte[] getHeader() {
		return header;
	}


	public void setHeader(byte[] header) {
		this.header = header;
	}


	public int getPeerID() {
		return peerID;
	}


	public void setPeerID(int peerID) {
		this.peerID = peerID;
	}


	public synchronized byte[] getZeroBits() {
		if (zeroBits != null){
			return zeroBits;
		}
		return zeroBits;
	}


	public synchronized void setZeroBits(byte[] zeroBits) {
		this.zeroBits = zeroBits;

	}
	
	public synchronized void setBitSet(BitSet bitset){
		this.setZeroBits(bitset.toByteArray());
	}
	
	public synchronized BitSet getBitSet(){
		return BitSet.valueOf(getZeroBits());
	}
	
	public ByteBuffer toByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		byteBuffer.put(header);
		byteBuffer.put(zeroBits);
		byteBuffer.putInt(peerID);
		return byteBuffer;
		
	}
	
	public static Handshake getHandshake(ByteBuffer byteBuffer){
		Handshake handshake = new Handshake();
		byte[] b = byteBuffer.array();
		System.arraycopy(b, 0, handshake.getHeader(), 0, handshake.getHeader().length);
		System.arraycopy(b, handshake.getHeader().length, handshake.getZeroBits(), 0, handshake.getZeroBits().length);
		handshake.setPeerID(byteBuffer.getInt(handshake.getHeader().length + handshake.getZeroBits().length));
		return handshake;
	}


}
