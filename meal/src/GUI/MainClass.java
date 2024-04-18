package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DB.DriverConnector;

import java.sql.*;
public class MainClass extends JFrame{//프로그램이 시작되는 클래스
	JButton btn[] = new JButton[4];//페이지에 추가할 버튼 초기화
	String str[] = {"사용자","관리자","사원등록","종료"};//버튼에 들어갈 이름
	int count = 0;
	MainClass(){
		setTitle("Main");
		setLayout(new GridLayout(4,1));//4열 1행으로 레이아웃
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for(int i = 0; i < btn.length; i++) {//4열 1행으로 버튼 추가
			btn[i] = new JButton(str[i]);
			add(btn[i]);
			btn[i].addActionListener(new Action());//버튼에 이벤트 리스너 등록
		}
		this.addWindowListener(new close());//X 버튼 윈도우 리스너 실행
		setSize(220,170);
		setVisible(true);
	}
	class close extends WindowAdapter{//X 버튼을 누를 시에 종료 알림창을 띄운 뒤 프로그램을 종료합니다.
		public void windowClosing(WindowEvent e) {
			JOptionPane.showMessageDialog(null,"프로그램을 종료합니다.","종료",JOptionPane.CLOSED_OPTION);
			System.exit(0);
		}
	}
	class Action implements ActionListener {//버튼에 적용될 이벤트 리스너
		@Override
		public void actionPerformed(ActionEvent e) {//버튼을 누를때
			// TODO Auto-generated method stub
			JButton b = (JButton)e.getSource();
			if(b.getText().equals(str[0])) {//사용자 버튼 클릭 시
				new MealTicket();//식권 페이지로 이동
				dispose();//창닫음
			}
			else if(b.getText().equals(str[1])) {//관리자 버튼 클릭 시
				AdminDialog ad = new AdminDialog();//관리자 인증 폼을 띄움
				int result = JOptionPane.showConfirmDialog(null, ad,"결제자 인증",JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION) {//확인 버튼을 누를 시
					count = 0;
					String password = new String(jp.getPassword());//비밀번호 창에 있는 비밀번호를 가져온다.
					String sql = "select memberName from member where passwd = ?";//비밀번호를 확인하는 SQL 구문
					Connection con = DriverConnector.MakeConnection("mealproject");//DB 연결
					try {
						PreparedStatement pst = con.prepareStatement(sql);
						pst.setString(1, password);
						ResultSet rs = pst.executeQuery();
						if(rs.next()) {//비밀번호가 맞을시에
							JOptionPane.showMessageDialog(null,rs.getString("memberName")+"님 환영합니다.","관리자 확인",JOptionPane.CLOSED_OPTION);
							new Admin();//관리자 확인 후 관리자 페이지 이동
							dispose();//창닫음
						}
						else {//비밀번호가 틀릴시에
							JOptionPane.showMessageDialog(null,"해당되는 관리자가 없습니다.","관리자 확인 실패",JOptionPane.ERROR_MESSAGE);
							jp.setText("");
						}
						
					} catch (SQLException e1) {//SQL에러가 날 시에
						e1.printStackTrace();
					}
				}
				else {
					jp.setText("");
					count = 0;
				}
			}
			else if(b.getText().equals(str[2])) {//사원 등록 버튼을 클릭 시
				new Staff();//사원등록 페이지 이동
				dispose();//창닫음
			}
			else {//종료 버튼을 클릭할 시
				JOptionPane.showMessageDialog(null,"프로그램을 종료합니다.","종료",JOptionPane.CLOSED_OPTION);
				System.exit(0);//시스템 종료
			}
		}
	}
	JPasswordField jp = new JPasswordField(4);
	JButton zero;
	JButton numbtn[] = new JButton[9];
	class AdminDialog extends JPanel{ //관리자 인증 다이얼로그 폼
		
		AdminDialog(){
			setLayout(new BorderLayout());
			
			add(jp,BorderLayout.NORTH);
			add(new NumberPanel(),BorderLayout.CENTER);
			zero = new JButton("0");
			add(zero,BorderLayout.SOUTH);//0버튼
			zero.addActionListener(new Action());//비밀번호 입력 이벤트 리스너 추가
		}
		class NumberPanel extends JPanel{//번호 입력 버튼
			NumberPanel(){
				setLayout(new GridLayout(3,3));
				
				for(int i = 0; i < 9; i++) {//1부터 9까지 번호 입력 버튼 배열
					numbtn[i] = new JButton(Integer.toString(i+1));
					add(numbtn[i]);
					numbtn[i].addActionListener(new Action());//비밀번호 입력 이벤트 리스너 추가
				}
			}
		}
		class Action implements ActionListener {//관리자 인증 폼 내 Action 리스너, 같은 이름의 클래스가 있어도 소속된 상위 클래스가 달라 중복이 안됨
			@Override
			public void actionPerformed(ActionEvent e) {//버튼을 클릭 시에 비밀번호를 입력함
				// TODO Auto-generated method stub
				JButton b = (JButton)e.getSource();
				count++;
				for(int i = 0; i < 10; i++) {
					if(count <= 4) {
						if(b.getText().equals(Integer.toString(i))) {
							jp.setText(new String(jp.getPassword())+Integer.toString(i));//비밀번호를 암호화 하여 텍스트필드에 출력
						}
					}
				}
			}
		}
	}
	public static void main(String[] args) {
		new MainClass();
	}
}
//UI = 1899라인, 합 1996라인