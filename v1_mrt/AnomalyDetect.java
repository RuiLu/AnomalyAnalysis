package com.test6;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JTextArea;

public class AnomalyDetect {

//	FlowSet
	static Vector<FlowSet> vf = new Vector<FlowSet>();
	private static FlowSet fs = null;
//	Divide each line into subarray
	private static Partition par = new Partition();
//	Assume that the threshold is 400000;
	private static final int threshold = 100000;
//	Turn the format of decimal into the format of binary
	private static DecimalFormat df = new DecimalFormat("00000000");
	
	
	public static long[][] GetMatrix(Vector<String> src, Vector<String> dst, Vector<String> len, int flag, String preSrcIP, String preDstIP){
		
		
//		Define a 255x255 matrix to store the length corresponding each pair of prefixes 
		long[][] a = new long[256][256];
		
//		System.out.println(preSrcIP + "..." + preDstIP);
//		Define two strings to store source and destination IP respectively
		for(int i=0;i<src.size();i++){
			String srcAddr = src.get(i);
			String dstAddr = dst.get(i);

//			Need to judge if the current prefixes are the same with prefixes which produced in last iteration
			if(preSrcIP.equals(srcAddr.substring(0, flag*8))&&preDstIP.equals(dstAddr.substring(0, flag*8))){
				a[BinarytoDecimal(srcAddr, flag)][BinarytoDecimal(dstAddr, flag)] += Integer.parseInt(len.get(i));
			}			
		}	
		return a;
	}
	
	
	public static int BinarytoDecimal(String binary, int flag){
		
		int ipAddr = 0;
		
		String s = binary.substring(flag*8,flag*8+8);
		for(char c : s.toCharArray()){
			ipAddr = ipAddr*2 + (c=='1' ? 1: 0);
		}
		
		return ipAddr;
	}
	

	
	public void IterationSearch(BufferedReader br, JTextArea jta1, JTextArea jta2, int flag, String preSrcIP, String preDstIP){
		
		int counter = 0;
		
//		if the time of iteration is 3, which means the value of flag is 2, return
		if(flag > 2){
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
			try {
				par.partition(br, jta1, fs, temp);								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
//		Initializing a new two-dimension array to store traffic data
		long[][] flowsetSize = new long[256][256];
			
		flowsetSize = this.GetMatrix(vf.get(temp).src, vf.get(temp).dst, vf.get(temp).length, temp, preSrcIP, preDstIP);
		
		for(int i=0;i<flowsetSize.length;i++){
			for(int j=0;j<flowsetSize[i].length;j++){
//				If the length of some flows are over the threshold, the do next iteration
				if(flowsetSize[i][j]>=threshold){
					System.out.println("temp is: " + temp);
//					Count the number of abnormal flow					
					counter++;
//					Reaching the maximum of granularity
					if(flag==2){
						String prefixSrcIP = this.changeFormat(preSrcIP);
						String prefixDstIP = this.changeFormat(preDstIP);
						jta2.append("This is a HH.\r\n");
						jta2.append("SrcIP:"+prefixSrcIP+i+".0/24"+"\t" +"DstIP: "+prefixDstIP+j+".0/24"+"\t" + "The length is: " + Long.toString(flowsetSize[i][j]) +"\t"+"\r\n");
					}
//					preSrcIP = preSrcIP + df.format(Integer.parseInt(Integer.toBinaryString(i)));
//					preDstIP = preDstIP + df.format(Integer.parseInt(Integer.toBinaryString(j)));
					System.out.println(i + "..." + j);
					System.out.println(preSrcIP + "..." + preDstIP);
//					Zooming into a finer granularity here		
					this.IterationSearch(br, jta1, jta2, flag+1, preSrcIP + df.format(Integer.parseInt(Integer.toBinaryString(i))), preDstIP + df.format(Integer.parseInt(Integer.toBinaryString(j))));
					System.out.println("Do a return");
				}		
			}
		}
////		If there is no abnormal flow existing, then return
//		if(counter == 0){
//			return;
//		}
		
	}
	
	
	public String changeFormat(String preIP){
		
		String formatedIP = "";
		int ip_1 = 0;
		int ip_2 = 0;
		
		for(char c : preIP.substring(0, 8).toCharArray()){
			ip_1 = ip_1*2 + (c=='1' ? 1: 0);
		}
		
		for(char c : preIP.substring(8, 16).toCharArray()){
			ip_2 = ip_2*2 + (c=='1' ? 1: 0);
		}
		
		formatedIP = Integer.toString(ip_1)+"."+Integer.toString(ip_2)+".";
		
		return formatedIP;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

class Partition{
	
	DealWithIP dwi = null;
//	Set up an interval which equals to 1s
	double interval = 1E-1;
	
	public void partition(BufferedReader br, JTextArea jta1, FlowSet fs, int temp) throws IOException{
		
		String buf = "";
//		Partition 100 packets each time, for testing
		int size = 1000;
		int counter = 0;
		
//		Time counter
		double startTime = 0;
		double endTime = 0;

		
		while((buf=br.readLine())!=null){

			String[] s = buf.split("\t");
			
			if(temp==0){
				jta1.append("----------------This is the 1st iteration!--------------------------\r\n");
			}
			else if(temp==1){
				jta1.append("----------------This is the 2nd iteration!--------------------------\r\n");
			}
			else if(temp==2){
				jta1.append("----------------This is the 3rd iteration!--------------------------\r\n");
			}

			jta1.append("This is the " + counter + " packet: " + "\r\n");
			jta1.append("Timestamp: " + s[0] + "\r\n");
			jta1.append("Length: " + s[1] + "\r\n");
			jta1.append("SourceIP: " + s[2] + "\r\n");
			jta1.append("DestinationIP: " + s[3] + "\r\n");
			jta1.append("Protocol: " + s[4] + "\r\n\r\n");
			
			dwi = new DealWithIP();
			
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
