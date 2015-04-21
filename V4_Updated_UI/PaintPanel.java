/*
 * paint the process of recursion by the mode of matrix
 */
package com.test15;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PaintPanel extends JPanel implements Runnable{
	
	private static String SrcIP = null;
	private static String DstIP = null;
	private static String Length = null;
	private static String Times = null;
	private int i = 80;
	private int j = 40;
	private int width = 128;
	private int height = 128;
	private Font mf1 = new Font("Courier", Font.BOLD, 13);
	private Font mf2 = new Font("Aria", Font.BOLD, 12);
	private Font mf3 = new Font("Aria", Font.BOLD, 15);
	private Stroke dash = new BasicStroke(0.5f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,
			3.5f,new float[]{15,10,},0f);
	private Stroke dash2 = new BasicStroke(0.3f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,
			1.5f,new float[]{7,3,},0f);
	private static Image im1,im2;
	
	//use a static class to load images
	static{
		try {
			im1 = ImageIO.read(new File("image/destination.png"));
			im2 = ImageIO.read(new File("image/source.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g){
			
		
			super.paint(g);;
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 750, 700);

			if(SrcIP==null&&DstIP==null){
				
				if(AnomalyDetect.getAnomalySet().size()>=1){
					
					String sip = AnomalyDetect.getAnomalySet().get(0).get(0);
					String dip = AnomalyDetect.getAnomalySet().get(0).get(1);
					String len = AnomalyDetect.getAnomalySet().get(0).get(2);
					String times = AnomalyDetect.getAnomalySet().get(0).get(3);
					this.drawComponent(g, sip, dip);
					this.drawInfo(g, sip, dip, len, times);
				}
				
			}
			else{
				String sip = SrcIP;
				String dip = DstIP;
				String len = Length;
				String times = Times;
				this.drawComponent(g, sip, dip);
				this.drawInfo(g, sip, dip, len, times);
			}

	}
	
	
	public void drawInfo(Graphics g, String sip, String dip, String len, String times){
		g.setColor(Color.BLACK);
		g.setFont(mf3);
		g.drawRect(40, 360, 320, 250);
		g.drawString("DETAILED INFORMATION", 55, 400);
		g.setFont(mf1);
		g.drawString("Abnormal Flow:", 55, 430);
		g.drawString("Source Prefix -> "+ sip, 55,460);
		g.drawString("Destination Prefix -> "+ dip, 55,490);
		g.drawString("Size of this flow -> "+len, 55, 520);
		g.drawString("Took "+(Integer.parseInt(times)+1)+" time-slots to identify this abnormal flow.", 55,550);	
	}
	
	public void drawComponent(Graphics g, String sip, String dip){
		String[] sipApart = sip.split("\\.");
		String[] dipApart = dip.split("\\.");
		this.drawFirstPrefix(g,Integer.parseInt(sipApart[0]), Integer.parseInt(dipApart[0]));			
		this.drawSecondPrefix(g,Integer.parseInt(sipApart[1]), Integer.parseInt(dipApart[1]),sipApart[0],dipApart[0]);
		this.drawThirdPrefix(g,Integer.parseInt(sipApart[2]), Integer.parseInt(dipApart[2]),sipApart[0],dipApart[0],sipApart[1],dipApart[1]);
		this.drawDottedLine(g, i, j);
		this.drawProcessLine(g, Integer.parseInt(sipApart[0]),Integer.parseInt(dipApart[0]), 1);
		this.drawProcessLine(g, Integer.parseInt(sipApart[1]),Integer.parseInt(dipApart[1]), 2);
		this.drawProcessLine(g, Integer.parseInt(sipApart[2]),Integer.parseInt(dipApart[2]), 3);
	}
	
	public void drawFirstPrefix(Graphics g, int x, int y){
		g.setFont(mf2);
		if(x<=128&&y<=128){	
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i, j+128, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i, j, width+128, height);
			g.fillRect(i+128, j+128, width, height);
		}
		else if(x<128&&y>128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i, j, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i, j+128, width, height);
			g.fillRect(i+128, j, width, height+128);
		}
		else if(x>128&&y<128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+128, j+128, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i, j, width, height+128);
			g.fillRect(i+128, j, width, height);
		}
		else if(x>=128&&y>=128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+128, j, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i, j, width, height+128);
			g.fillRect(i+128, j+128, width, height);
		}
		

		
		g.setColor(Color.BLACK);
		g.drawImage(im1,i-20,j+20,this);
		g.drawImage(im2,i+128+33,j+255,this);
		g.drawLine(i, j-15, i, j+255+15);
		if(y<=128){
			g.drawString("1.0.0.0/8",i-65,j+250);
			g.drawString("128.0.0.0/8",i-65,j+128);
		}
		else if(y>128){
			g.drawString("254.0.0.0/8", i-65, j+5);
			g.drawString("128.0.0.0/8",i-65,j+128);
		}
		g.drawLine(i-15, j+255, i+255+15, j+255);
		if(x<=128){
			g.drawString("1.0.0.0/8", i-30, j+280);
			g.drawString("128.0.0.0/8", i+100, j+280);
		}
		else if(x>128){
			g.drawString("128.0.0.0/8", i+100, j+280);
			g.drawString("254.0.0.0/8",i+220,j+280);
		}
		this.drawTriangle(g, i, j-18, i-3,j-13, i+3, j-13);
		this.drawTriangle(g, i+255+15+3, j+255, i+255+15-2,j+253, i+255+15-2, j+257);
		g.setColor(Color.RED);
		g.setFont(mf1);
		g.drawLine(i+x-4, j+255-y-4, i+x+4, j+255-y+4);
		g.drawLine(i+x-4, j+255-y+4, i+x+4, j+255-y-4);
		g.drawString("( "+x+".0.0.0/8,",i+x-8,j+255-y-20);
		g.drawString(y+".0.0.0/8 )", i+x-5, j+255-y-7);
	
	}
	
	public void drawSecondPrefix(Graphics g,int x, int y, String pres,String pred){
		g.setFont(mf2);
		if(x<=128&&y<=128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+130, j+128, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j, width, height);
			g.fillRect(i+255+130+128, j, width, height+128);
		}
		else if(x<128&&y>128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+130, j, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j+128, width, height);
			g.fillRect(i+255+128+130, j, width, height+128);
		}
		else if(x>128&&y<128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+128+130, j+128, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j, width, height+128);
			g.fillRect(i+255+128+130, j, width, height);
		}
		else if(x>=128&&y>=128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+128+130, j, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j, width, height+128);
			g.fillRect(i+255+128+130, j+128, width, height);
		}
		
		
		
		g.setColor(Color.BLACK);
		g.drawImage(im1,i+255+130-20,j+20,this);
		g.drawImage(im2,i+255+130+128+33,j+255,this);
		g.drawLine(i+255+130, j-15, i+255+130, j+255+15);
		if(y<=128){
			g.drawString(pred+".1.0.0/16",i+255+130-80,j+250);
			g.drawString(pred+".128.0.0/16",i+255+130-80,j+128);
		}
		else if(y>128){
			g.drawString(pred+".254.0.0/16", i+255+130-80, j+5);
			g.drawString(pred+".128.0.0/16",i+255+130-80,j+128);
		}
		g.drawLine(i+255+130-15, j+255, i+255+130+255+15, j+255);
		if(x<=128){
			g.drawString(pres+".1.0.0/16", i+255+130-30, j+280);
			g.drawString(pres+".128.0.0/16", i+255+130+100, j+280);
		}
		else if(x>128){
			g.drawString(pres+".254.0.0/16",i+255+130+255-30,j+280);
			g.drawString(pres+".128.0.0/16", i+255+130+100, j+280);

		}
		this.drawTriangle(g, i+255+130, j-18, i+255+130-3,j-13, i+255+130+3, j-13);
		this.drawTriangle(g, i+255+130+255+15+3, j+255, i+255+130+255+15-2,j+253, i+255+130+255+15-2, j+257);
		g.setColor(Color.RED);
		g.setFont(mf1);
		g.drawLine(i+255+130+x-4, j+255-y-4, i+255+130+x+4, j+255-y+4);
		g.drawLine(i+255+130+x-4, j+255-y+4, i+255+130+x+4, j+255-y-4);
		g.drawString("( "+pres+"."+x+".0.0/16,",i+255+130+x-8,j+255-y-20);
		g.drawString(pred+"."+y+".0.0/16 )", i+255+130+x-5, j+255-y-7);
//		g.drawString(x+","+y, i+255+130+x, j+255-y);
	}
	
	public void drawThirdPrefix(Graphics g,int x, int y,String pres1,String pred1,String pres2,String pred2){
		g.setFont(mf2);
		if(x<=128&&y<=128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+130, j+255+60+128, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j+255+60, width, height);
			g.fillRect(i+255+130+128, j+255+60, width, height+128);
		}
		else if(x<128&&y>128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+130, j+255+60, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j+255+60+128, width, height);
			g.fillRect(i+255+130+128, j+255+60, width, height+128);
		}
		else if(x>128&&y<128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+130+128, j+255+60+128, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j+255+60, width, height+128);
			g.fillRect(i+255+130+128, j+255+60, width, height);
		}
		else if(x>=128&&y>=128){
			//draw the abnormal zoom
			g.setColor(Color.PINK);
			g.fillRect(i+255+130+128, j+255+60, width, height);
			//draw the normal zoom
			g.setColor(Color.GREEN);
			g.fillRect(i+255+130, j+255+60, width, height+128);
			g.fillRect(i+255+130+128, j+255+60+128, width, height);
		}
		
		g.setColor(Color.BLACK);
		g.drawImage(im1,i+255+130-20,j+255+60+20,this);
		g.drawImage(im2,i+255+130+128+33,j+255+60+255,this);
		g.drawLine(i+255+130, j+255+60-15, i+255+130, j+255+60+255+15);
		if(y<=128){
			g.drawString(pred1+"."+pred2+".1.0/24",i+255+130-80,j+255+60+250);
			g.drawString(pred1+"."+pred2+".128.0/24",i+255+130-80,j+255+60+128);
		}
		else if(y>128){
			g.drawString(pred1+"."+pred2+".254.0/24", i+255+130-80, j+255+60+5);
			g.drawString(pred1+"."+pred2+".128.0/24",i+255+130-80,j+255+60+128);

		}
		g.drawLine(i+255+130-15, j+255+60+255, i+255+130+255+15, j+255+60+255);
		if(x<=128){
			g.drawString(pres1+"."+pres2+".1.0/24", i+255+130-30, j+255+60+280);
			g.drawString(pres1+"."+pres2+".128.0/24", i+255+130+100, j+255+60+280);
		}
		else if(x>128){
			g.drawString(pres1+"."+pres2+".254.0/24",i+255+130+220,j+255+60+280);
			g.drawString(pres1+"."+pres2+".128.0/24", i+255+130+100, j+255+60+280);
		}
		this.drawTriangle(g, i+255+130, j+255+60-18, i+255+130-3,j+255+60-13, i+255+130+3, j+255+60-13);
		this.drawTriangle(g, i+255+130+255+15+3, j+255+255+60, i+255+130+255+15-2,j+255+60+253, i+255+130+255+15-2, j+255+60+257);
		g.setColor(Color.RED);
		g.setFont(mf1);
		g.drawLine(i+255+130+x-4, j+255+60+255-y-4, i+255+130+x+4, j+255+60+255-y+4);
		g.drawLine(i+255+130+x-4, j+255+60+255-y+4, i+255+130+x+4, j+255+60+255-y-4);
		g.drawString("( "+pres1+"."+pres2+"."+x+".0/24,",i+255+130+x-8,j+255+60+255-y-20);
		g.drawString(pred1+"."+pred2+"."+y+".0/24 )", i+255+130+x-5, j+255+60+255-y-7);
	}
	
	public void drawTriangle(Graphics   g,int   x1,int   y1,int   x2,int   y2,int   x3,int   y3)     
	{     
	    
	  Polygon filledPolygon=new Polygon();     
	  filledPolygon.addPoint(x1,y1);     
	  filledPolygon.addPoint(x2,y2);     
	  filledPolygon.addPoint(x3,y3);     
	  g.setColor(Color.BLACK);     
	  g.fillPolygon(filledPolygon);     
	    
	} 
	
	public void drawDottedLine(Graphics g, int i, int j){
		//draw two crossed dotted lines
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(dash);
		g2d.setColor(Color.BLACK);
		g2d.drawLine(i+128, j-5, i+128, j+256+5);
		g2d.drawLine(i-5, j+128, i+255+5, j+128);
		g2d.drawLine(i+255+130+128, j-5, i+255+130+128, j+255+5);
		g2d.drawLine(i+255+130-5, j+128, i+255+130+255+5, j+128);
		g2d.drawLine(i+255+130+128, j+255+60-5, i+255+130+128, j+255+60+255+5);
		g2d.drawLine(i+255+130-5, j+255+60+128, i+255+130+255+5, j+255+60+128);
		
	}
	
	public void drawProcessLine(Graphics g, int x, int y, int flag){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(dash2);
		g2d.setColor(Color.BLACK);
		if(flag==1){
			g2d.drawLine(i+x,j+255-y,i+255+130,j);
			g2d.drawLine(i+x,j+255-y,i+255+130,j+255);
		}
		else if(flag==2){
			g2d.drawLine(i+255+130+x,j+255-y,i+255+130,j+255+60);
			g2d.drawLine(i+255+130+x,j+255-y,i+255+130+255,j+255+60);
		}
		else if(flag==3){
			g2d.drawLine(i+255+130+x, j+255+60+255-y, 360, 360);
			g2d.drawLine(i+255+130+x, j+255+60+255-y, 360, 610);
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			while(AnomalyDetect.isStop()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					this.repaint();
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public static String getSrcIP() {
		return SrcIP;
	}

	public static void setSrcIP(String srcIP) {
		SrcIP = srcIP;
	}

	public static String getDstIP() {
		return DstIP;
	}

	public static void setDstIP(String dstIP) {
		DstIP = dstIP;
	}

	public static String getLength() {
		return Length;
	}

	public static void setLength(String length) {
		Length = length;
	}

	public static String getTimes() {
		return Times;
	}

	public static void setTimes(String times) {
		Times = times;
	}




}
