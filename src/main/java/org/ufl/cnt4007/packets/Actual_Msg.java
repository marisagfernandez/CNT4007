package org.ufl.cnt4007.packets;

public class Actual_Msg {

	private char[] length;
	private char[] type;
	private char[] payload;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public char[] getLength() {
		return length;
	}

	public void setLength(char[] length) {
		this.length = length;
	}

	public char[] getPayload() {
		return payload;
	}

	public void setPayload(char[] payload) {
		this.payload = payload;
	}

}

class Choke_Msg extends Actual_Msg{
	
	
}

class Unchoke_Msg extends Actual_Msg{
	
	
}

class Inter_Msg extends Actual_Msg{
	
	
}

class Noninter_Msg extends Actual_Msg{
	
	
}

class Have_Msg extends Actual_Msg{
	
	
}

class Bitfield_Msg extends Actual_Msg{
	
	
}

class Request_Msg extends Actual_Msg{
	
	
}

class Piece_Msg extends Actual_Msg{
	
	
}

