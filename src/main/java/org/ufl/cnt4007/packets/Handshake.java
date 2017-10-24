package org.ufl.cnt4007.packets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Base64.Encoder;
import java.util.BitSet;
import java.util.Base64;
import java.util.Base64.Decoder;

public class Handshake {

	private String header;
	private String asciiBits;
	private int peerID;
	
	
	private static Decoder decoder = Base64.getDecoder();
	private static Encoder encoder = Base64.getEncoder();
	
	@JsonIgnore
	private byte[] zeroBits;
	
	public Handshake(){
		
	}
	
	public Handshake(String header, String asciiBits, int peerID){
		this.header = header;
		this.peerID = peerID;
		this.asciiBits = asciiBits;
	}


	public String getHeader() {
		return header;
	}


	public void setHeader(String header) {
		this.header = header;
	}


	public int getPeerID() {
		return peerID;
	}


	public void setPeerID(int peerID) {
		this.peerID = peerID;
	}


	public String getAsciiBits() {
		return asciiBits;
	}


	public void setAsciiBits(String asciiBits) {
		this.asciiBits = asciiBits;
	}


	public synchronized byte[] getZeroBits() {
		if (zeroBits != null){
			return zeroBits;
		}
		
		zeroBits = decoder.decode(this.asciiBits);
		return zeroBits;
	}


	public synchronized void setZeroBits(byte[] zeroBits) {
		this.zeroBits = zeroBits;
		
		this.asciiBits = encoder.encodeToString(this.zeroBits);
		
		
	}
	@JsonIgnore
	public synchronized void setBitSet(BitSet bitset){
		this.setZeroBits(bitset.toByteArray());
	}
	@JsonIgnore
	public synchronized BitSet getBitSet(){
		return BitSet.valueOf(getZeroBits());
	}


}
