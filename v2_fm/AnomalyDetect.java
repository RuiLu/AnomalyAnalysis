package com.test7;

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
	
//	Assume that the threshold of link rate is 190000;
	private static final int linkRate = 70000;
	
	
	public static long[][] GetMatrix(Vector<String> src, Vector<String> dst, Vector<String> len, int preLength, String preSrcIP, String preDstIP){
		
		
//		Define a 255x255 matrix to store the length corresponding each pair of prefixes 
		long[][] a = new long[256][256];
		
//		System.out.println(preSrcIP + "..." + preDstIP);
//		Define two strings to store source and destination IP respectively
		for(int i=0;i<src.size();i++){
			String srcAddr = src.get(i);
			String dstAddr = dst.get(i);

//			Need to judge if the current prefixes are the same with prefixes which produced in last iteration
			if(preSrcIP.equals(srcAddr.substring(0, preLength))&&preDstIP.equals(dstAddr.substring(0, preLength))){
				a[BinarytoDecimal(srcAddr, preLength)][BinarytoDecimal(dstAddr, preLength)] += Integer.parseInt(len.get(i));
			}			
		}	
		return a;
	}
	
	
	public static int BinarytoDecimal(String binary, int preLength){
		
		int ipAddr = 0;
		
		String s = binary.substring(preLength, preLength+8);
		for(char c : s.toCharArray()){
			ipAddr = ipAddr*2 + (c=='1' ? 1: 0);
		}
		
		return ipAddr;
	}
	
//	This is the function which accomplishes the FM algorithm
	public void FMHold(BufferedReader br, JTextArea jta1, JTextArea jta2, int flag, String preSrcIP, String preDstIP){
		
//		To store the total length of packets with the same prefix
		long length = 0;
//		To mark the number of iteration
		int temp = flag;
//		To store the length of prefix from last iteration
		int preLength = preSrcIP.length();
		
		fs = new FlowSet();
		
		System.out.println("Fm Temp is: "+temp);

//		If the number of iteration is bigger than the size of vf, which means that
//		program must read next flowset
		if(vf.size()<(temp+1)){		
			try {
				par.partition(br, jta1, fs, preLength);
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
		
		String prefixSrcIP = this.changeFormat(preSrcIP);
		String prefixDstIP = this.changeFormat(preDstIP);
			
//		If length is greater or equal to the threshold
		if(length>=threshold){
//			If reaching the finest granularity, print the result, which is detected by FM.
			if(preLength==24){
				prefixSrcIP = this.changeFormat(preSrcIP);
				prefixDstIP = this.changeFormat(preDstIP);
				jta2.append("Temp:"+temp+"\t"+"This is a HH.\tDetected by FM\r\n");
				jta2.append("SrcIP:"+prefixSrcIP+".0/24"+"\t" +"DstIP: "+prefixDstIP+".0/24"+"\t" + "The length is: " + Long.toString(length) +"\t"+"\r\n\n");
			}
//			If the granularity doesn't get to maximum, zooming here which hasn't accomplished yet.
			else{
				jta2.append("This prefix:"+prefixSrcIP+"\t" + prefixDstIP + " is : " + length+"\tDetected by FM.\r\n\n");
				return;
			}
		}
//		If the length is greater or equal to linkrate which should be set flexibly,
//		then hold it, and call FMHold().
		else if(length>=linkRate){
			jta2.append("This prefix:"+prefixSrcIP+"\t" + prefixDstIP + " is : " + length+"\r\n");
			this.FMHold(br, jta1, jta2, flag+1, preSrcIP, preDstIP);
		}
//		Else, drop it.
		else{
			jta2.append("This prefix:"+prefixSrcIP+"\t" + prefixDstIP + " doesn't have anomaly\r\n\n");
			System.out.println("Less than both threshold and linkrate.");
			return;
		}
		
	}
	
	
	public void IterationSearch(BufferedReader br, JTextArea jta1, JTextArea jta2, int flag, String preSrcIP, String preDstIP){
		
		int counter = 0;
		
////		if the time of iteration is 3, which means the value of flag is 2, return
//		if(flag > 2){
//			return;
//		}
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
				par.partition(br, jta1, fs, preLength);								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
//		Initializing a new two-dimension array to store traffic data
		long[][] flowsetSize = new long[256][256];
		
		flowsetSize = this.GetMatrix(vf.get(temp).src, vf.get(temp).dst, vf.get(temp).length, preLength, preSrcIP, preDstIP);
		
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
						String prefixSrcIP = this.changeFormat(preSrcIP);
						String prefixDstIP = this.changeFormat(preDstIP);
						jta2.append("Temp:"+temp+"\t"+"This is a HH.\tDectected by MRT.\r\n");
						jta2.append("SrcIP:"+prefixSrcIP+i+".0/24"+"\t" +"DstIP: "+prefixDstIP+j+".0/24"+"\t" + "The length is: " + Long.toString(flowsetSize[i][j]) +"\t"+"\r\n\n");
					}
					else{
//						Zooming into a finer granularity here		
						this.IterationSearch(br, jta1, jta2, flag+1, preSrcIP + df.format(Integer.parseInt(Integer.toBinaryString(i))), preDstIP + df.format(Integer.parseInt(Integer.toBinaryString(j))));
					}

//					System.out.println("Do a return");
					
				}
//				If the size is less then the threshold, do FM and hold it
				else if(flowsetSize[i][j]>=linkRate){
					System.out.println("This is a FM -> temp is: " + temp);
					
					
					String prefixSrcIP = this.changeFormat(preSrcIP);
					String prefixDstIP = this.changeFormat(preDstIP);
					jta2.append("Temp:"+ temp +"\t"+ "This is a suspectable flowset.\r\n");
					jta2.append("SrcIP:"+prefixSrcIP+i+".0/"+(preSrcIP.length()+8)+"\t" +"DstIP: "+prefixDstIP+j+".0/"+(preDstIP.length()+8)+"\t" + "The length is: " + Long.toString(flowsetSize[i][j]) +"\t"+"\r\n");
					this.FMHold(br, jta1, jta2, flag+1, preSrcIP + df.format(Integer.parseInt(Integer.toBinaryString(i))), preDstIP + df.format(Integer.parseInt(Integer.toBinaryString(j))));
//					this.IterationSearch(br, jta1, jta2, flag+1, preSrcIP, preDstIP);
				}
//				Drop this flowset
				else{
					flowsetSize[i][j]=0;
				}
			}
		}		
	}
	
	
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
	
	public void partition(BufferedReader br, JTextArea jta1, FlowSet fs, int preLength) throws IOException{
		
		String buf = "";

		int counter = 0;
		
//		Time counter
		double startTime = 0;
		double endTime = 0;

		
		while((buf=br.readLine())!=null){

			String[] s = buf.split("\t");
			
//			if(preLength==0){
//				jta1.append("----------------This is the 1st iteration!--------------------------\r\n");
//			}
//			else if(preLength==8){
//				jta1.append("----------------This is the 2nd iteration!--------------------------\r\n");
//			}
//			else if(preLength==16){
//				jta1.append("----------------This is the 3rd iteration!--------------------------\r\n");
//			}

//			jta1.append("This is the " + counter + " packet: " + "\r\n");
//			jta1.append("Timestamp: " + s[0] + "\r\n");
//			jta1.append("Length: " + s[1] + "\r\n");
//			jta1.append("SourceIP: " + s[2] + "\r\n");
//			jta1.append("DestinationIP: " + s[3] + "\r\n");
//			jta1.append("Protocol: " + s[4] + "\r\n\r\n");
			
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
