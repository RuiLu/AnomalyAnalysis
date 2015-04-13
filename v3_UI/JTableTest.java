package com.test9;

import java.awt.*;
import javax.swing.*;
import java.util.*;


public class JTableTest extends JFrame {

	/**
	 * @param args
	 */
	
	public static JTable jt;
	private static JScrollPane jsp;
	private static Vector rowData;
	private static Vector<String> columnNames;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JTableTest jtt = new JTableTest();
	}
	
	public JTableTest(){
		
		rowData = new Vector();
		columnNames = new Vector<String>();
		columnNames.add("名字");
		columnNames.add("性别");
		columnNames.add("专业");
		
		jt = new JTable(rowData,columnNames);
		jsp = new JScrollPane(jt);
		
		
		this.add(jsp);
		this.setSize(400,300);
		this.setLocation(400, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		AddRow ar = new AddRow(rowData);
		
//		Vector<String> data;
//		
//		for(int i=0;i<5;i++){
//			System.out.println(i);
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			data = new Vector<String>();
//			data.add("Rui Lu");
//			data.add("male");
//			data.add("InfoSecurity");
//			rowData.add(data);
//			
//			jt.repaint();
//			
//		}
	}
}

class AddRow{
	
	public AddRow(Vector rowData){
		
		Vector<String> data;
		
		for(int i=0;i<5;i++){
			System.out.println(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			data = new Vector<String>();
			data.add("Rui Lu");
			data.add("male");
			data.add("InfoSecurity");
			rowData.add(data);
			
			JTableTest.jt.updateUI();
			
		}

	}
	
}
