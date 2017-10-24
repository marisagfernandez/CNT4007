package org.ufl.cnt4007.packets;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	private String asciiType;
	private String acsiiPayload;
	
	@JsonIgnore
	private byte[] type;
	
	@JsonIgnore
	private byte[] payload;
	
	private static Decoder decoder = Base64.getDecoder();
	private static Encoder encoder = Base64.getEncoder();
	
	

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getAsciiType() {
		return asciiType;
	}

	public void setAsciiType(String asciiType) {
		this.asciiType = asciiType;
	}

	public String getAcsiiPayload() {
		return acsiiPayload;
	}

	public void setAcsiiPayload(String acsiiPayload) {
		this.acsiiPayload = acsiiPayload;
	}

	public synchronized byte[] getType() {
		if(type != null){
			return type;
		}
		
		type = decoder.decode(this.asciiType);
		return type;
	}

	public synchronized void setType(byte[] type) {
		this.type = type;
		this.asciiType = encoder.encodeToString(type);
	}

	public synchronized byte[] getPayload() {
		if(payload != null){
			return payload;
		}
		payload = decoder.decode(this.acsiiPayload);
		return payload;
	}

	public synchronized void setPayload(byte[] payload) {
		this.payload = payload;
		this.acsiiPayload = encoder.encodeToString(payload);
	}


}

