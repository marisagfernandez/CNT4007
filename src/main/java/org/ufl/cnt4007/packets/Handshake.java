package org.ufl.cnt4007.packets;

public class Handshake {

	private String header;
	private char[] bits;
	private char[] peerID;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


	public String getHeader() {
		return header;
	}


	public void setHeader(String header) {
		this.header = header;
	}


	public char[] getBits() {
		return bits;
	}


	public void setBits(char[] bits) {
		this.bits = bits;
	}


	public char[] getPeerID() {
		return peerID;
	}


	public void setPeerID(char[] peerID) {
		this.peerID = peerID;
	}

}
