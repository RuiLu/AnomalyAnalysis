package com.test9;

import java.text.DecimalFormat;

public class DealWithIP {

//	Initial positions of "." in each IP
//	There are three "." in each IPv4 address
	private static int pos1,pos2,pos3;
	
//	Set the format of IPv4 address 
	DecimalFormat df = new DecimalFormat("00000000");
	
	public String DecimalToBinary(String DecimalIP){
		
		String binaryIP = "";
		
		this.pos1 = DecimalIP.indexOf(".");
		this.pos2 = DecimalIP.indexOf(".", this.pos1+1);
		this.pos3 = DecimalIP.indexOf(".", this.pos2+1);
		
		binaryIP = df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(DecimalIP.substring(0, pos1)))))+
		 df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(DecimalIP.substring(pos1+1, pos2)))))+
		 df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(DecimalIP.substring(pos2+1, pos3)))))+
		 df.format(Integer.parseInt(Integer.toBinaryString(Integer.parseInt(DecimalIP.substring(pos3+1)))));
		
		return binaryIP;
		
	}
	
}
