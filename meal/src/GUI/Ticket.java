package GUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;

import javax.imageio.*;
import javax.swing.*;
import java.io.*;

import GUI.MealMenu.back;
import GUI.Ticket.ticketPanel;

import java.util.*;
import java.sql.*;
public class Ticket extends JFrame{
	int count = 0;
	String ticketinfo;
	String sql;
	Statement st;
	JTable t;
	Vector<ticketPanel>ticket = new Vector<ticketPanel>();
	String name;
	int k;
	int price, n;
	int num;
	Ticket(JTable t, int k, int num){
		Container c = getContentPane();
		this.num = num;
		this.t = t;
		this.k = k;
		for(int i = 0; i < t.getRowCount(); i++) {
			count += Integer.parseInt((String) t.getValueAt(i, 3));
		}
		setBackground(Color.white);
		
		setLayout(new GridLayout(count,1,10,10));
		for(int i = 0; i < t.getRowCount(); i++) {
			name = (String)t.getValueAt(i, 1);
			n = Integer.parseInt((String)t.getValueAt(i, 3));
			price = Integer.parseInt((String)t.getValueAt(i, 2));
			for(int j = 0; j < n; j++) {
				ticketPanel tp = new ticketPanel(name, n, price, j+1);
				ticket.add(tp);
				
			}
		}
		for(int i = 0; i < ticket.size(); i++) {
			c.add(ticket.get(i));
		}
		setSize(500,800);
		setVisible(true);
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		this.paint(g2d);
		try{
			ImageIO.write(image,"png", new File("C:\\Users\\MS\\Desktop\\meal\\MealProject\\DataFiles\\" + ticketinfo + ".jpg"));
			JOptionPane.showMessageDialog(this, "파일저장 성공"); 
		}catch(Exception ex){
			ex.printStackTrace();
		}
		this.addWindowListener(new back());
		
	}
	class back extends WindowAdapter {//X버튼 클릭 시 처음 페이지로 이동
		public void windowClosing(WindowEvent e) {
			dispose();
			JOptionPane.showMessageDialog(null,"식권 출력을 완료했습니다. 처음 페이지로 돌아갑니다.","",JOptionPane.CLOSED_OPTION);
			new MainClass();
		}
	}
	int background = 0;
	class ticketPanel extends JPanel{
		String name; int n; int price;
		int flag;
		JLabel inform[] = new JLabel[2];
		JLabel infomation[] = new JLabel[2];
		Color color;
		ticketPanel(String name, int n, int price, int j){
			this.setBorder(BorderFactory.createLineBorder(Color.black, 5));
			this.name = name;
			this.n = n;
			this.price = price;
			this.flag = j++;
			setLayout(new BorderLayout());			
			if(flag == 1) {
				background++;
			}
			if(background % 2 == 0) {
				color = Color.pink;
			}
			else {
				color = Color.cyan;
			}
			Calendar c = Calendar.getInstance();
			int time[] = {c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DATE),c.get(Calendar.HOUR_OF_DAY)
					,c.get(Calendar.MINUTE),c.get(Calendar.SECOND)};
			String str = "";
			for(int i = 0; i < time.length; i++) {
				
				str += Integer.toString(time[i]);
			}
			ticketinfo = str + "-" + k + "-" + (num+1);
			JLabel info = new JLabel(ticketinfo);
			
			info.setOpaque(true);
			info.setBackground(color);
			this.add(info,BorderLayout.NORTH);
			info.setFont(new Font("Ariel",Font.BOLD,20));
			this.add(new TicketInfo(),BorderLayout.CENTER);
			this.add(new MenuInfo(),BorderLayout.SOUTH);
			
		}
		class TicketInfo extends JPanel{
			TicketInfo(){
				setLayout(new GridLayout(2,1));
				this.setBackground(color);
				String str[] = {"식 권",price/1000+","+price%1000+"원"};
				if(price%1000==0) {
					str[1] = price/1000+",000원";
				}
				for(int i = 0; i < inform.length; i++) {
					inform[i] = new JLabel(str[i]);
					add(inform[i]);
					inform[i].setHorizontalAlignment(JLabel.CENTER);
					inform[i].setFont(new Font("Ariel",Font.BOLD,40));
				}
			}
		}
		class MenuInfo extends JPanel{
			MenuInfo(){
				setLayout(new GridLayout(1,2));
				this.setBackground(color);
				String str[] = {"메뉴 : " + name,flag+"/"+n};
				for(int i = 0; i < infomation.length; i++) {
					infomation[i] = new JLabel(str[i]);
					if(i == 1) {
						infomation[1].setHorizontalAlignment(JLabel.RIGHT);
					}
					infomation[i].setFont(new Font("Ariel",Font.BOLD,20));
					add(infomation[i]);
				}
			}
		}
		
	}
	
}
