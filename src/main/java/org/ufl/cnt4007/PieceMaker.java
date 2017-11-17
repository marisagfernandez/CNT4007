package org.ufl.cnt4007;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PieceMaker {

	private File file;
	
	public PieceMaker (){
		
	}
	
	public static byte[][] makePieces(File file, int pieceSize, int numPieces) throws IOException{
		try{
			
			byte[][] pieces = new byte[numPieces][pieceSize];
			FileInputStream f = new FileInputStream(file);
			for(int i = 0; i < numPieces; i++){
				//byte[] piece = new byte[pieceSize];
				pieces[i] = new byte[pieceSize];
				f.read(pieces[i]);
			}
			return pieces;
			
		}catch (IOException e){
			System.out.println("ERROR in reading file");
			e.printStackTrace();
			throw new IOException();
		}
		
	}
	
	public static void writePiecesToFile(File file, byte[][] pieces) throws IOException{
		try{
			FileOutputStream f = new FileOutputStream(file);
			
			for(int i = 0; i < pieces.length; i++){
				f.write(pieces[i]);
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
			byte[][] pieces = makePieces(f, pieceSize, numPieces);
			writePiecesToFile(f2, pieces);
		}catch(IOException e){
			System.out.println("Error!");
			e.printStackTrace();
		}

	}

}
