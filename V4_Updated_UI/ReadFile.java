package com.test15;

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
 * 8.把递归单独为一个线程
 * 9.保存异常值，写入文件，结束之后再从文件中读取数据画图。存在时间差
 * 10.选择出每一条异常，然后画出过程图。原始的为第一条的过程。
 * 11.改善UI，并且显示出每次缩进是在第几个timeslot；
 * 12.先学习8位前缀的平均值，注意接口，timeslot，等等参数，新建一个button和JDialog来设置参数
*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import java.io.*;

import java.util.*;

public class ReadFile extends JFrame implements ActionListener{
	
	PaintPanel pp;
	private static JPanel jp;
	public static JButton jb1,jb2;
	private static JMenuBar jmb;
	private static JMenu jm1,jm2,jm3;
	private static JMenuItem jmi11,jmi12,jmi21,jmi31;
	private static JScrollPane jsp;
	public static JTable jt;
	private static OpenFile of = null;
	
	public static AnomalyModel am;

	//For JScrollPane
	Thread t1 = null;
	//For PaintPane (defined by myself)
	Thread t2 = null;
	
	static{
		try {
			UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String[] arg){
		new ReadFile();
	}
	
	
	public ReadFile(){
		
		of = new OpenFile();
	
		am = new AnomalyModel();
		jt = new JTable(am);			
		jsp = new JScrollPane(jt);
		jsp.setBounds(0, 0, 345, 620);
		
		jmb = new JMenuBar();

		jm1 = new JMenu("File");	
		jmi11 = new JMenuItem("Open");
		jmi11.addActionListener(this);
		jmi11.setActionCommand("open");		
		jmi12 = new JMenuItem("Exit");
		jmi12.addActionListener(this);
		jmi12.setActionCommand("exit");
		
		jm2 = new JMenu("Setting");
		jm2.addActionListener(this);
		
		jm3 = new JMenu("Help");
		jm3.addActionListener(this);
		
		
		jmb.add(jm1);
		jm1.add(jmi11);
		jm1.add(jmi12);
		jmb.add(jm2);
		jmb.add(jm3);
		this.setJMenuBar(jmb);

		//the panel that draws zooming process
		pp = new PaintPanel();
		pp.setBounds(345, 0, 900, 700);
		t2 = new Thread(pp);
		t2.start();
	
		//the panel that keeps two buttons
		jp = new JPanel();
		jb1 = new JButton("Check");
		jb1.addActionListener(this);
		jb1.setActionCommand("check");
		jb2 = new JButton("Update");
		jb2.addActionListener(this);
		jb2.setActionCommand("update");
		jp.setLayout(new GridLayout(1, 2));
		jb1.setEnabled(false);
		jp.add(jb1);
		jp.add(jb2);
		jp.setBounds(0, 620, 345, 50);
		
		this.setLayout(null);
		this.add(pp);
		this.add(jsp);
		this.add(jp);
		this.setSize(1100,720);
		
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		this.setLocation(width/2-550,height/2-380);
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
		} 
		else if(e.getActionCommand().equals("exit")){
			System.exit(0);
//		update current anomaly set, and remove the old anomaly set
		} 
		else if(e.getActionCommand().equals("update")){
			am = new AnomalyModel();
			AnomalyDetect.getAnomalySet().removeAllElements();
			jt.setModel(am);
			this.jb1.setEnabled(false);
			pp.setSrcIP(null);
			pp.setDstIP(null);
			pp.updateUI();
//		check zooming process of the chosen anomaly 
		} 
		else if(e.getActionCommand().equals("check")){
			int num = jt.getSelectedRow();
			if(num==-1){
				JOptionPane.showMessageDialog(this, "Please select one row that you want to update");
				return;
			}
			pp.setSrcIP(AnomalyModel.rowData.get(num).get(0));
			pp.setDstIP(AnomalyModel.rowData.get(num).get(1));
			pp.setLength(AnomalyModel.rowData.get(num).get(2));
			pp.setTimes(AnomalyModel.rowData.get(num).get(3));
			pp.updateUI();
		} 
	}
}


class OpenFile{
	
//	Define a AnomalyDectect
	private static AnomalyDetect ad = null;
//	Define a flag to mark the iteration
//	0-the 1st iteration; 1-the 2nd iteration; 2-the 3rd iteration
	private static int flag = 0;
//  When doing the first iteration, preSrcIP and preDstIP are both ""
	private static String preSrcIP;
	private static String preDstIP;
	
	private static FileReader fr = null;
	private static BufferedReader br = null;
	private static FileWriter fw = null;
	
	public void Open(){
//		start reading the .txt file	
		System.out.println("Open");
//		initial the open-file dialog
		
		JFileChooser jfc = new JFileChooser();
		jfc.setName("Choose file...");
		int result = jfc.showOpenDialog(null);
		jfc.setVisible(true);
		String filePath = "";
//		get the absolute path of chosen file
		if(result==JFileChooser.APPROVE_OPTION){
			filePath = jfc.getSelectedFile().getAbsolutePath();
			System.out.println(jfc.getSelectedFile().getAbsolutePath());	
			
			try {
				
				fr = new FileReader(filePath);
				br = new BufferedReader(fr);
				fw = new FileWriter("e:\\temp.txt",true);
				
//				Initializing parameters of the first iteration
				preSrcIP = "";
				preDstIP = "";
/*
 * 				I suppose that the iteration should be done here
*/							   
				
				//this interface is needed to be modified
			   ad = new AnomalyDetect(br,this.flag,preSrcIP,preDstIP);
               ad.start();
               
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
//			jta.append("Warning! You did not choose a legal file.");
		}
	}

	public static void closeFile(){
		try {
			if(br!=null) br.close();
			if(fr!=null) fr.close();
			System.out.println("关闭文件流");
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	public static FileReader getFr() {
		return fr;
	}

	public static void setFr(FileReader fr) {
		OpenFile.fr = fr;
	}

	public static BufferedReader getBr() {
		return br;
	}

	public static void setBr(BufferedReader br) {
		OpenFile.br = br;
	}
}




