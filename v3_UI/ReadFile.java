package com.test9;

/*
 * 这个包将进行迭代实验
 * 1.将迭代进行整合，整合成一个方法，用if(flag==?)进行判断
 * 2.修改每次判断的条件，改成判断传入的参数和substring(0,(flag*8))是否相等，
 *   必须保证之前所有的位数都相等
 * 3.修改jta1的输出方法，以便检查是否正确
 * 4.争取实现做成可选择前缀粒度分析或者IP粒度分析(haven't done yet)
 * 5.把参数temp独立出来，单独用作记录迭代的次数。原来的最后用前缀长度preLength来替代
 * 6.FM算法
 * 7.ER算法(递归实现)
 * 8.
*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.*;

import java.util.*;

public class ReadFile extends JFrame implements ActionListener{
	
	private static JPanel jp;
	private static JSplitPane jsplit;
	private static JMenuBar jmb;
	private static JMenu jm1;
	private static JMenuItem jmi1,jmi2;
	private static JScrollPane jsp;
	public static JTable jt;
	private static OpenFile of = null;
	
	public static Vector<Vector<String>> rowData;
	private static Vector<String> columnNames;
	
	public static void main(String[] arg){
		new ReadFile();
	}
	
	
	public ReadFile(){
		
		of = new OpenFile();
		
		jp = new JPanel();
		
		jsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jsplit.setDividerSize(2);
		jsplit.setDividerLocation(500);
		
		rowData = new Vector();
		columnNames = new Vector<String>();
		columnNames.add("Source Prefix");
		columnNames.add("Destination Prefix");
		columnNames.add("Flow Size");
		
		
		jt = new JTable(rowData,columnNames);		
		
		jsp = new JScrollPane(jt);

		jmb = new JMenuBar();
		jm1 = new JMenu("File");
		
		jmi1 = new JMenuItem("Open");
		jmi1.addActionListener(this);
		jmi1.setActionCommand("open");
		
		jmi2 = new JMenuItem("Exit");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		
		this.setJMenuBar(jmb);
		jmb.add(jm1);
		jm1.add(jmi1);
		jm1.add(jmi2);
		
		
		jsplit.add(jp);
		jsplit.add(jsp);
		
		this.add(jsplit);
		this.setSize(900,600);
		this.setLocation(200,70);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
//		click the "Open" button
		if(e.getActionCommand().equals("open")){
			of.Open();
			System.out.println("返回");
//		click the "Exit" button
		} else if(e.getActionCommand().equals("exit")){
			System.exit(0);
		}
	}

}


class OpenFile{
	
//	Define a AnomalyDectect
	private static AnomalyDetect ad = new AnomalyDetect();
//	Define a flag to mark the iteration
//	0-the 1st iteration; 1-the 2nd iteration; 2-the 3rd iteration
	private static int flag = 0;
//  When doing the first iteration, preSrcIP and preDstIP are both ""
	private static String preSrcIP;
	private static String preDstIP;
	
	
	
	public void Open(){
//		start reading the .txt file	
		System.out.println("Open");
//		initial the open-file dialog
		
		JFileChooser jfc = new JFileChooser();
		jfc.setName("Choose file...");
		jfc.showOpenDialog(null);
		jfc.setVisible(true);
		String filePath = "";
//		get the absolute path of chosen file
		if(jfc.getSelectedFile().getAbsolutePath()!=null){
			filePath = jfc.getSelectedFile().getAbsolutePath();
			System.out.println(jfc.getSelectedFile().getAbsolutePath());	
//			read data from the chosen file
			FileReader fr = null;
			BufferedReader br = null;
			
			try {
				
				fr = new FileReader(filePath);
				br = new BufferedReader(fr);
				
//				Initializing parameters of the first iteration
				preSrcIP = "";
				preDstIP = "";
/*
 * 				I suppose that the iteration should be done here
*/							   
				
               ad.IterationSearch(br, this.flag, preSrcIP, preDstIP);
			
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} finally{
				try {
//					BufferedReader and FileReader are closed here
					br.close();
					fr.close();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally{
					System.out.println("The program is over.");
				}
			}
		}
		else{
//			jta.append("Warning! You did not choose a legal file.");
		}
	}
}




