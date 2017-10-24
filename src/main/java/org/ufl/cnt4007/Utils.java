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

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.fasterxml.jackson.core.JsonToken.*;


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
		char[] id = { '1', '2', '3', '4', '5', '6', '7', '8' };
		
		Student student = new Student("Marisa", 23, id);

		String studentStr = generateJson(student);
		System.out.println(studentStr);

		student = null;

		Student studentObj = (Student)parseJsonObject(studentStr, Student.class);

		System.out.println("Student id = " + String.copyValueOf(studentObj.getId()));
	}
		
}

class Student {
	private String name;
	private int age;
	private char[] id;
	
	@JsonIgnore
	private String lastName;

	public Student() {
	}

	public Student(String name, int age, char[] id) {
		this.name = name;
		this.age = age;
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the id
	 */
	public char[] getId() {
		return id;
	}
	
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(char[] id) {
		this.id = id;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
