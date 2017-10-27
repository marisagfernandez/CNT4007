//
//package org.ufl.cnt4007;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.JsonProcessingException;
//
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.BitSet;
//
//import org.ufl.cnt4007.packets.*;
//
//
//public class Utils {
//
//	private static ObjectMapper jsonObjectMapper = new ObjectMapper();
//
//	
//	/**
//	 * Given a JSON object and Class, returns an instance of the Class that is
//	 * represented by the JSON object
//	 */
//	public static Object parseJsonObject(String json, Class c)
//			throws JsonProcessingException, Exception {
//		if (jsonObjectMapper == null) {
//			
//			throw new Exception("generateJson: ERROR, jsonObjectMapper is null");
//		}
//		return jsonObjectMapper.readValue(json, c);
//	}
//
//	
//
//	/**
//	 * Given an Object, returns the JSON String representation of that object
//	 */
//	public static String generateJson(Object obj)
//			throws JsonProcessingException, Exception {
//		if (jsonObjectMapper == null) {
//			
//			throw new Exception("generateJson: ERROR, jsonObjectMapper is null");
//		}
//		return jsonObjectMapper.writeValueAsString(obj);
//	}
//
//	public static void main(String[] args) throws Exception {
//		Handshake handshake = new Handshake();
//		handshake.setHeader("My Headersssssssss".getBytes());
//		handshake.setZeroBits("0000000000".getBytes());
//		handshake.setPeerID(1000000);
//		
//		
//		ByteBuffer b = handshake.toByteBuffer();
//		Handshake h = Handshake.getHandshake(b);
//		System.out.println(h.getHeader().toString());
//		for(byte b2 : h.getHeader()){
//			System.out.print(b2 + " ");
//		}
//		System.out.println(h.getPeerID());
//		
//	}
//		
//}
//
//
