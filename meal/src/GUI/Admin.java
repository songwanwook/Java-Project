package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Admin extends JFrame{//관리자 페이지
	String menu[] = {"메뉴 등록","메뉴 관리","결제 조회","종류별 차트","종료"};//버튼 이름
	JButton btn[] = new JButton[5];//버튼 배열 초기화
	ImageIcon img = new ImageIcon("C:\\Users\\MS\\Desktop\\meal\\MealProject\\DataFiles\\main.jpg");//이미지 패널에 띄어질 이미지
	Admin(){
		setTitle("관리자 페이지");
		setSize(600,600);
		setVisible(true);
		add(new ButtonPanel(),BorderLayout.NORTH);//창 북쪽에 버튼 패널 배치
		JLabel la = new JLabel(img);
		add(la,BorderLayout.CENTER);//중앙에 이미지 띄우기
		this.addWindowListener(new back());//X창 클릭 이벤트 등록
	}
	class back extends WindowAdapter {//X버튼을 클릭할 시 강제 종료 방지를 위한 코드
		public void windowClosing(WindowEvent e) {
			dispose();
			new MainClass();//메인 페이지로 이동
		}
	}
	class ButtonPanel extends JPanel{
		ButtonPanel(){
			for(int i = 0; i < btn.length; i++) {
				btn[i] = new JButton(menu[i]);//버튼 등록하기
				add(btn[i]);
				btn[i].addActionListener(new Action());//버튼에 클릭 이벤트 추가하기
			}
		}
	}
	class Action implements ActionListener {//버튼 클릭 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String cmd = e.getActionCommand();
			switch(cmd) {
				case "메뉴 등록": new InsertMenu(); break;//메뉴등록 버튼 클릭 시 메뉴 등록 창 이동
				case "메뉴 관리" : new MenuAdmin(); break;//메뉴관리 버튼 클리 시 메뉴 관리 페이지 이동
				case "결제 조회" : new Payment(); break;//결제 조회 버튼 클릭 시 결제 조회 페이지 이동
				case "종류별 차트" : new Chart(); break;//종류별 차트 버튼 클릭 시 종류별 차트 페이지 이동
				case "종료" : new MainClass(); break;//종료 버튼 클릭시 첫 페이지로 이동
			}
			dispose();
		}
	}
	/*public static void main(String[] args) {
		new Admin();
	}*/
}
