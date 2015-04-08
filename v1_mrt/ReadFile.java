package com.test6;

/*
 * ����������е���ʵ��
 * 1.�������������ϣ����ϳ�һ����������if(flag==?)�����ж�
 * 2.�޸�ÿ���жϵ��������ĳ��жϴ���Ĳ�����substring(0,(flag*8))�Ƿ���ȣ�
 *   ���뱣֤֮ǰ���е�λ�������
 * 3.�޸�jta1������������Ա����Ƿ���ȷ
 * 4.��ȡʵ�����ɿ�ѡ��ǰ׺���ȷ�������IP���ȷ���(haven't done yet)
 * 5.ʵ��FM��ER(haven't done yet)
 * 6.ʵ��DM(haven't done yet)
*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.*;

public class ReadFile extends JFrame implements ActionListener{
	
	JSplitPane jsp = null;
	JMenuBar jmb = null;
	JMenu jm1 = null;
	JMenuItem jmi1,jmi2;
	JTextArea jta1,jta2;
	JScrollPane jsp1,jsp2;
	OpenFile of = null;
	
	public static void main(String[] arg){
		new ReadFile();
	}
	
	public ReadFile(){
		
		of = new OpenFile();
		
		jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jsp.setDividerSize(2);
		jsp.setDividerLocation(260);
		jta1 = new JTextArea();
		jta2 = new JTextArea();
		jsp1 = new JScrollPane(jta1);
		jsp2 = new JScrollPane(jta2);
		jsp.add(jsp1);
		jsp.add(jsp2);
		
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
		
		this.add(jsp);
		this.setSize(600,600);
		this.setLocation(200,70);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
//		click the "Open" button
		if(e.getActionCommand().equals("open")){
			of.Open(jta1,jta2);
//		click the "Exit" button
		} else if(e.getActionCommand().equals("exit")){
			System.exit(0);
		}
	}	
}

class OpenFile{
	
//	Define a AnomalyDectect
	AnomalyDetect ad = new AnomalyDetect();
//	Define a flag to mark the iteration
//	0-the 1st iteration; 1-the 2nd iteration; 2-the 3rd iteration
	public int flag = 0;
//  When doing the first iteration, preSrcIP and preDstIP are both ""
	public String preSrcIP;
	public String preDstIP;
	
	
	
	public void Open(JTextArea jta1, JTextArea jta2){
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
               ad.IterationSearch(br, jta1, jta2, this.flag, preSrcIP, preDstIP);				
			
				
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
				}				
			}
		}
		else{
			jta1.append("Warning! You did not choose a legal file.");
		}
	}	
}





