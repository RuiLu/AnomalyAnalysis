package com.test9;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import java.io.*;;

public class AnomalyDetect{

//	FlowSet
	static Vector<FlowSet> vf = new Vector<FlowSet>();
	private static FlowSet fs = null;
//	Divide each line into subarray
	private static Partition par = new Partition();
//	Assume that the threshold is 400000;
	private static final int threshold = 200000;
//	Assume that the threshold of link rate is 190000;
	private final int linkRate = 170000;
//	Turn the format of decimal into the format of binary
	private static DecimalFormat df = new DecimalFormat("00000000");
//	Deal with format
	private static FormatTransform ft = new FormatTransform();
//	Store the information of abnormal flow
	Vector<String> anomaly;
	


	
//	This is the function which accomplishes the FM algorithm
	public void FMHold(BufferedReader br,int flag, String preSrcIP, String preDstIP){
		
		
//		To store the total length of packets with the same prefix
		long length = 0;
//		To mark the number of iteration
		int temp = flag;
//		To store the length of prefix from last iteration
		int preLength = preSrcIP.length();
		
		if(temp>=5){
			return;
		}
		
		fs = new FlowSet();
		
		System.out.println("Fm Temp is: "+temp);

//		If the number of iteration is bigger than the size of vf, which means that
//		program must read next flowset
		if(vf.size()<(temp+1)){		
			try {
				par.Partition(br,fs, preLength);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
//		If both source and destination prefixes are the same with those in the current iteration
//		accumulate the length of packets
		for(int i=0;i<vf.get(temp).src.size();i++){
			if(vf.get(temp).src.get(i).substring(0,preLength).equals(preSrcIP)&&vf.get(temp).dst.get(i).substring(0,preLength).equals(preDstIP)){
				length += Long.parseLong(vf.get(temp).length.get(i));
			}
		}
		
		String prefixSrcIP = ft.changeFormat(preSrcIP);
		String prefixDstIP = ft.changeFormat(preDstIP);
			
//		If length is greater or equal to the threshold
		if(length>=threshold){
//			If reaching the finest granularity, print the result, which is detected by FM.
			if(preLength==24){
				
				anomaly = new Vector<String>();
				anomaly.add(prefixSrcIP+".0/24");
				anomaly.add(prefixDstIP+".0/24");
				anomaly.add(Long.toString(length));
				
				ReadFile.rowData.add(anomaly);
				
				ReadFile.jt.updateUI();
			}
//			If the granularity doesn't get to maximum, zooming here which hasn't accomplished yet.
			else{
				
				anomaly = new Vector<String>();
				anomaly.add(prefixSrcIP);
				anomaly.add(prefixDstIP);
				anomaly.add(Long.toString(length));
				
				ReadFile.rowData.add(anomaly);
				
				
				return;
			}
		}
//		If the length is greater or equal to linkrate which should be set flexibly,
//		then hold it, and call FMHold().
		else if(length>=linkRate){
			this.FMHold(br,flag+1, preSrcIP, preDstIP);
		} 
//		Else, drop it.
		else{
			System.out.println("Less than both threshold and linkrate.");
			return;
		}
		
	}
	
	
	public void IterationSearch(BufferedReader br,int flag, String preSrcIP, String preDstIP){
		
		
		int counter = 0;
		
//		if the length of prefix is 24, return		
		int preLength =  preSrcIP.length();
		
		if(preLength==24){
			return;
		}
		
//		initializing a new object of FlowSet
		fs = new FlowSet();
		
//		to store the value of flag
		int temp = flag;

//		vf.isEmpty()	
//		To judge if the data have been read 
		if(vf.size()<(temp+1)){
//			initializing a new object of FlowSet
//			Update: change the temp into the length of preSrcIP, therefore I can use the value of temp to
//			mark the time of iteration independently.
			try {
				par.Partition(br,fs, preLength);								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
//		Initializing a new two-dimension array to store traffic data
		long[][] flowsetSize = ft.GetMatrix(vf.get(temp).src, vf.get(temp).dst, vf.get(temp).length, preLength, preSrcIP, preDstIP);
		
//		flowsetSize = this.GetMatrix(vf.get(temp).src, vf.get(temp).dst, vf.get(temp).length, preLength, preSrcIP, preDstIP);
		
		for(int i=0;i<flowsetSize.length;i++){
			for(int j=0;j<flowsetSize[i].length;j++){
//				Computing the momentum of each flowset here
				
				
//				If the length of some flows are over the threshold, the do next iteration
				if(flowsetSize[i][j]>=threshold){
					System.out.println("temp is: " + temp);
//					Count the number of abnormal flow					
					counter++;
//					Reaching the maximum of granularity
					if(preLength==16){
						String prefixSrcIP = ft.changeFormat(preSrcIP);
						String prefixDstIP = ft.changeFormat(preDstIP);
						
						anomaly = new Vector<String>();
						anomaly.add(prefixSrcIP+i+".0/24");
						anomaly.add(prefixDstIP+j+".0/24");
						anomaly.add(Long.toString(flowsetSize[i][j]));
						
						ReadFile.rowData.add(anomaly);						
							
						ReadFile.jt.updateUI();
						
					}
					else{
//						Zooming into a finer granularity here		
						this.IterationSearch(br,flag+1, preSrcIP + df.format(Integer.parseInt(Integer.toBinaryString(i))), preDstIP + df.format(Integer.parseInt(Integer.toBinaryString(j))));
					}

					
				}
//				If the size is less then the threshold, do FM and hold it
				else if(flowsetSize[i][j]>=linkRate){
					System.out.println("This is a FM -> temp is: " + temp);
										
					String prefixSrcIP = ft.changeFormat(preSrcIP);
					String prefixDstIP = ft.changeFormat(preDstIP);
					this.FMHold(br,flag+1, preSrcIP + df.format(Integer.parseInt(Integer.toBinaryString(i))), preDstIP + df.format(Integer.parseInt(Integer.toBinaryString(j))));
				}
//				Drop this flowset
				else{
					flowsetSize[i][j]=0;
				}
			}
		}		
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

class Partition{
	
	private DealWithIP dwi = new DealWithIP();
//	Set up an interval which equals to 1s
	private double interval = 1;
	
	public void Partition(BufferedReader br,FlowSet fs, int preLength) throws IOException{
		
		String buf = "";

		int counter = 0;
		
//		Time counter
		double startTime = 0;
		double endTime = 0;

		

		
		while((buf=br.readLine())!=null){

			String[] s = buf.split("\t");
				
			fs.time.add(s[0]);
			fs.length.add(s[1]);
			fs.src.add(dwi.DecimalToBinary(s[2]));
			fs.dst.add(dwi.DecimalToBinary(s[3]));
			fs.protocol.add(s[4]);	
//			To get the start time
			
			if(counter==0){
				startTime = Double.parseDouble(s[0]);
			}
//			To get the current time
			endTime = Double.parseDouble(s[0]);
//			Judging if the time is up
			if((endTime-startTime)>=interval){
				break;
			}

			counter++;
		}
		
		
		AnomalyDetect.vf.add(fs);
	}	
}

class FormatTransform{
	
	/*
	 * generate a 256x256 matrix to store length of flow
	 */
	public long[][] GetMatrix(Vector<String> src, Vector<String> dst, Vector<String> len, int preLength, String preSrcIP, String preDstIP){
		
		
//		Define a 255x255 matrix to store the length corresponding each pair of prefixes 
		long[][] matrix = new long[256][256];
		
//		System.out.println(preSrcIP + "..." + preDstIP);
//		Define two strings to store source and destination IP respectively
		for(int i=0;i<src.size();i++){
			String srcAddr = src.get(i);
			String dstAddr = dst.get(i);

//			Need to judge if the current prefixes are the same with prefixes which produced in last iteration
			if(preSrcIP.equals(srcAddr.substring(0, preLength))&&preDstIP.equals(dstAddr.substring(0, preLength))){
				matrix[BinarytoDecimal(srcAddr, preLength)][BinarytoDecimal(dstAddr, preLength)] += Integer.parseInt(len.get(i));
			}			
		}	
		return matrix;
	}
	
	/*
	 * Only being used when generating matrix
	 */
	public static int BinarytoDecimal(String binary, int preLength){
		
		int ipAddr = 0;
		
		String s = binary.substring(preLength, preLength+8);
		for(char c : s.toCharArray()){
			ipAddr = ipAddr*2 + (c=='1' ? 1: 0);
		}
		
		return ipAddr;
	}
	
	/*
	 * Deal with the IP address, convert binary to decimal 
	 */
	public String changeFormat(String preIP){
		
		String formatedIP = "";
		int ip_1 = 0;
		int ip_2 = 0;
		int ip_3 = 0;
		
		if(preIP.length()==0){
			formatedIP = "";
		}
		else if(preIP.length()==8){
			for(char c : preIP.substring(0, 8).toCharArray()){
				ip_1 = ip_1*2 + (c=='1' ? 1: 0);
			}
			formatedIP = Integer.toString(ip_1)+".";
;
		}
		else if(preIP.length()==16){
			for(char c : preIP.substring(0, 8).toCharArray()){
				ip_1 = ip_1*2 + (c=='1' ? 1: 0);
			}
			
			for(char c : preIP.substring(8, 16).toCharArray()){
				ip_2 = ip_2*2 + (c=='1' ? 1: 0);
			}
			
			formatedIP = Integer.toString(ip_1)+"."+Integer.toString(ip_2)+".";
			
		}
		else if(preIP.length()==24){
			for(char c : preIP.substring(0, 8).toCharArray()){
				ip_1 = ip_1*2 + (c=='1' ? 1: 0);
			}
			
			for(char c : preIP.substring(8, 16).toCharArray()){
				ip_2 = ip_2*2 + (c=='1' ? 1: 0);
			}
			
			for(char c : preIP.substring(16, 24).toCharArray()){
				ip_3 = ip_3*2 + (c=='1' ? 1: 0);
			}
			
			formatedIP = Integer.toString(ip_1)+"."+Integer.toString(ip_2)+"."+Integer.toString(ip_3)+".";
			
		}
		return formatedIP;
	}
	
}
