package GUI;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import GUI.MainClass.close;
public class MealTicket extends JFrame{//식권 페이지
	ImageIcon im[] = new ImageIcon[4];//버튼에 출력할 이미지 아이콘 배열 초기화
	JButton btn[] = new JButton[4];//버튼 초기화
	String str[] = {"한식","중식","일식","양식"};//식사 유형 이름 배열
	String time;
	JLabel timeLabel;//현재시간을 출력할 JLabel
	MealTicket(){
		setTitle("식권 종류 선택");
		JLabel la = new JLabel("식권 발매 프로그램");
		la.setFont(new Font("Ariel",Font.BOLD,20));
		la.setHorizontalAlignment(JLabel.CENTER);
		add(la,BorderLayout.NORTH);
		JTabbedPane jtab = new JTabbedPane();
		jtab.addTab("메뉴", new tabPanel());
		add(jtab,BorderLayout.CENTER);
		add(new Time(),BorderLayout.SOUTH);
		this.addWindowListener(new back());//X 버튼 클릭 시 이벤트리스너
		setSize(700,800);
		setVisible(true);
	}
	class back extends WindowAdapter {
		public void windowClosing(WindowEvent e) {//X 버튼을 클릭했을 때
			dispose();
			new MainClass();//메인 페이지로 이동
		}
	}
	class tabPanel extends JPanel {
		tabPanel(){
			setLayout(new GridLayout(2,2));
			for(int i = 0; i < btn.length; i++) {
				im[i] = new ImageIcon("C:/Users/MS/Desktop/meal/MealProject/DataFiles/menu_" + (i+1) + ".png");
				btn[i] = new JButton(im[i]);
				btn[i].setToolTipText(str[i]);
				add(btn[i]);//유형별 식사 및 이미지를 버튼에 씌움
				btn[i].addActionListener(new Action());
			}
		}
	}
	class Action implements ActionListener{//유형별 식사 버튼을 클릭했을 때 유형별 식권 메뉴 페이지로 이동
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b = (JButton)e.getSource();
			for(int i = 0; i < btn.length; i++) {
				if(b == btn[i]) {
					dispose();
					new Menu(i);//식권메뉴 페이지를 이동할때 식사 인자값을 던져준다.0=한식, 1=중식, 2=일식, 3=양식
				}
			}
		}
	}
	class Time extends JPanel{//현재 시간 출력 패널
		Time(){
			timeLabel = new JLabel();
			setBackground(Color.BLACK);
			timeLabel.setForeground(Color.CYAN);
			timeLabel.setFont(new Font("",Font.BOLD,20));
			add(timeLabel);
			TimeThread t = new TimeThread();
			t.start();
		}
	
	class TimeThread extends Thread{//현재 시간을 매초마다 갱신하는 스레드
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				Calendar c = Calendar.getInstance();
				int year = c.get(Calendar.YEAR);
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DATE);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int min = c.get(Calendar.MINUTE);
				int seconds = c.get(Calendar.SECOND);
				timeLabel.setText("현재시간 : " + year + "년 " + month + "월 " + day + "일 " + hour + "시 " + min + "분 " + seconds + "초");
				try {
					sleep(1000);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	}
	/*public static void main(String[] args) {
		new MealTicket();
	}*/
}
