package org.ufl.cnt4007;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileManager {

	private File file;
	
	
	public FileManager (){
		
	}
	
	public static void makePieces (File file, int pieceSize, int numPieces) throws IOException{

		try{
			byte[] b = new byte[pieceSize];
			FileInputStream f = new FileInputStream(file);
			
			for(int i = 0; i < numPieces; i++){
				//Read in the data from main file
				f.read(b);
				//Create new file for the piece
				File piece = new File(file.getParent(), "piece" + String.valueOf(i+1));
				try{
					FileOutputStream outputStream = new FileOutputStream(piece);
					//Write the data to the file representing the piece
					outputStream.write(b);
								
				}catch (IOException e){
					System.out.println("ERROR in creating piece");
					e.printStackTrace();
					throw new IOException();
				}
			}
			
		}catch (IOException e){
			System.out.println("ERROR in reading file");
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	public static void writePiecesToFile(File file, int pieceSize, int numPieces) throws IOException{
		try{
			FileOutputStream f = new FileOutputStream(file);
			byte[] b = new byte[pieceSize];
			
			for(int i = 0; i < numPieces; i++){
				//Find the file representing the piece
				FileInputStream inputStream = new FileInputStream("piece"+String.valueOf(i+1));
				//Read its data
				inputStream.read(b);
				//Then write it to the main file
				f.write(b);
				
				}
			
		}catch (IOException e){
			System.out.println("ERROR in writing to file");
			e.printStackTrace();
			throw new IOException();
		}
	
	}

	
	
	public static void main(String[] args) {
		
		File f = new File("Hello.rtf");
		
		File f2 = new File("Hello2.rtf");
		
		
		int pieceSize = 119;
		int numPieces = 3;
		try{
			makePieces(f, pieceSize, numPieces);
			writePiecesToFile(f2, pieceSize, numPieces);
		}catch(IOException e){
			System.out.println("Error!");
			e.printStackTrace();
		}

	}

}
