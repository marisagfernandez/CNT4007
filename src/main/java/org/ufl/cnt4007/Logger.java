package org.ufl.cnt4007;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
	FileOutputStream f;
	Logger(int id){
		//take peer_process id to know where to save the logs.
		try {
			this.f = new FileOutputStream("log_peer_" + id + ".log", false);
		} catch (FileNotFoundException e) {
			
			System.out.println("ERROR: Unable to create log file");
			e.printStackTrace();
		}
	}
	void log(String s) {
		String time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
			s = time + ": " + s + "\n";
		try {
			f.write(s.getBytes());
		} catch (IOException e) {
			//file not created.... create the file first.
			e.printStackTrace();
		}
	}
	void close() {
		try {
			f.close();
		} catch (Exception e) {
			
		}
	}
}
