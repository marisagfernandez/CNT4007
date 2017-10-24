/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ufl.cnt4007;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.BitSet;

import org.ufl.cnt4007.packets.*;


public class Utils {

	private static ObjectMapper jsonObjectMapper = new ObjectMapper();
	private static JsonFactory jsonFactory = new JsonFactory();

	
	/**
	 * Given a JSON object and Class, returns an instance of the Class that is
	 * represented by the JSON object
	 * 
	 * @param json
	 * @param c
	 * @return
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	public static Object parseJsonObject(String json, Class c)
			throws JsonProcessingException, Exception {
		if (jsonObjectMapper == null) {
			
			throw new Exception("generateJson: ERROR, jsonObjectMapper is null");
		}
		return jsonObjectMapper.readValue(json, c);
	}

	

	/**
	 * Given an Object, returns the JSON String representation of that object
	 * 
	 * @param list
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String generateJson(Object obj)
			throws JsonProcessingException, Exception {
		if (jsonObjectMapper == null) {
			
			throw new Exception("generateJson: ERROR, jsonObjectMapper is null");
		}
		return jsonObjectMapper.writeValueAsString(obj);
	}

	public static void main(String[] args) throws Exception {
		Handshake handshake = new Handshake();
		handshake.setHeader("My Header");
		handshake.setPeerID(101);
		handshake.setZeroBits("110101".getBytes());
		String s1 = generateJson(handshake);
		System.out.println(s1);
		
		handshake = null;
		handshake = (Handshake)parseJsonObject(s1, Handshake.class);
		System.out.println(handshake.getAsciiBits());
		byte[] zeroBits = handshake.getZeroBits();
		
		
		for(byte bit: zeroBits){
			System.out.print(bit+ " ");
		}
		
		System.out.println();
		
		BitSet bitset = new BitSet(10);
		bitset.set(5);
		handshake.setBitSet(bitset);
		bitset = null;
		bitset = handshake.getBitSet();
		System.out.println("bit 5 = " + bitset.get(5));
	}
		
}


