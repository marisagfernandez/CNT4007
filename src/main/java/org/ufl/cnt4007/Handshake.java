package org.ufl.cnt4007;

import java.util.Arrays;
import java.util.BitSet;
import java.nio.ByteBuffer;


public class Handshake {
	
	private static final byte[] header = "P2PFILESHARINGPROJ".getBytes();
	private static final byte[] zeroBytes = {0,0,0,0,0,0,0,0,0,0};
	

	
	private byte[] zeroBits;
	private int peerID;
	
	
	public Handshake(){
		
		zeroBits = new byte[10];
	}
	
	public Handshake(byte[] header, byte[] zeroBits, int peerID){
		
		this.zeroBits = zeroBits;
		this.peerID = peerID;
		
	}


	public byte[] getHeader() {
		return header;
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
	
	public ByteBuffer toByteBuffer() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		byteBuffer.put(header);
		byteBuffer.put(zeroBits);
		byteBuffer.putInt(peerID);
		return byteBuffer;
		
	}
	public static byte[] makeHandshake(int id) {
		ByteBuffer bytes = ByteBuffer.allocate(32);
		bytes.put(header);
		bytes.put(zeroBytes);
		bytes.putInt(id);
		return bytes.array();
	}
	
	public static boolean verifyHandshake(byte[] b, int peerID){
		
		byte[] id = new byte[4];
		System.arraycopy(b, b.length - 4, id, 0, id.length);
		ByteBuffer bbID = ByteBuffer.wrap(id);
		int pId = bbID.getInt();
		
		byte[] headerCheck = new byte[18];
		System.arraycopy(b, 0, headerCheck, 0, headerCheck.length);
		
		Arrays.equals(header, headerCheck);
		
		if (Arrays.equals(header, headerCheck) && pId == peerID){
			return true;
		}
		return false;
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
