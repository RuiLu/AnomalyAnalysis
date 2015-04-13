package com.test9;

/*
 * ����������е���ʵ��
 * 1.�������������ϣ����ϳ�һ����������if(flag==?)�����ж�
 * 2.�޸�ÿ���жϵ��������ĳ��жϴ���Ĳ�����substring(0,(flag*8))�Ƿ���ȣ�
 *   ���뱣֤֮ǰ���е�λ�������
 * 3.�޸�jta1������������Ա����Ƿ���ȷ
 * 4.��ȡʵ�����ɿ�ѡ��ǰ׺���ȷ�������IP���ȷ���(haven't done yet)
 * 5.�Ѳ���temp��������������������¼�����Ĵ�����ԭ���������ǰ׺����preLength�����
 * 6.FM�㷨
 * 7.ER�㷨(�ݹ�ʵ��)
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
			System.out.println("����");
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




