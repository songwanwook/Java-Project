package GUI;
import java.awt.*;
import javax.swing.*;

import DB.DriverConnector;

import java.awt.event.*;
import java.sql.*;
public class Main extends JFrame{
	JButton btn[] = new JButton[3];//로그인, 회원가입, 종료 버튼
	String str[] = {"STARBOX", "ID : ", "PW : ", "로그인", "회원가입", "종료"};
	JTextField jt;//로그인 텍스트필드
	JPasswordField jp;
	JLabel la[] = new JLabel[3];
	Main(){
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		la[0] = new JLabel(str[0]);
		add(la[0], BorderLayout.NORTH);
		la[0].setFont(new Font("SansSerif",1, 30));
		la[0].setHorizontalAlignment(JLabel.CENTER);//STARBOX 대문
		add(new Login(), BorderLayout.CENTER);
		add(new signinButton(),BorderLayout.SOUTH);
		setSize(370,200);
		setVisible(true);
	}
	class Login extends JPanel{//로그인 패널
		Login(){
			setLayout(null);
			for(int i = 0; i < 2; i++) {
				la[i+1] = new JLabel(str[i+1]);
				la[i+1].setSize(30,20);
				la[i+1].setLocation(90, i*30);
				la[i+1].setHorizontalAlignment(JLabel.RIGHT);
				add(la[i+1]);
				if(i == 0) {
					jt = new JTextField();
					jt.setSize(150, 20);
					jt.setLocation(120, i*30);
					add(jt);//로그인을 입력받을 텍스트필드
				}
				else {
					jp = new JPasswordField();
					jp.setSize(150, 20);
					jp.setLocation(120, i*30);
					add(jp);//로그인을 입력받을 텍스트필드
				}
			}
			btn[0] = new JButton(str[3]);
			btn[0].setSize(70, 70);
			add(btn[0]);
			btn[0].setLocation(280,0);//로그인 확인 버튼
			btn[0].addActionListener(new Action());//버튼 클릭 이벤트 등록
			btn[0].setFocusable(true);
		}
	}
	class signinButton extends JPanel {//회원가입 종료 버튼 패널
		signinButton(){
			for(int i = 1; i < btn.length; i++) {
				btn[i] = new JButton(str[i+3]);
				add(btn[i]);//회원가입, 종료 버튼
				btn[i].addActionListener(new Action());//클릭 이벤트 등록
			}
		}
	}
	class Action implements ActionListener {//버튼 클릭 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b = (JButton)e.getSource();
			String btnstr = b.getText();
			if(btnstr.equals(str[3])) {//로그인 버튼 클릭
				if(jt.getText().equals("") || jp.getPassword().length==0){//입력란이 둘중 하나라도 비어있을 때
					JOptionPane.showMessageDialog(null, "아이디, 비밀번호를 모두 입력하세요", "로그인 실패", JOptionPane.ERROR_MESSAGE);
				}
				else {
					String id = jt.getText();//ID
					String pw = new String(jp.getPassword());//패스워드
					if(id.equals("admin")) {//관리자 로그인
						if(pw.equals("1234")) {
							JOptionPane.showMessageDialog(null, "관리자 로그인");
							dispose();
							new Admin();
						}
						else {//관리자 로그인 실패
							JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀립니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						Connection c = DriverConnector.MakeConnection("Cafe");//로그인을 할 버튼 클릭
						String sql = "select * from user where u_id = ?";//로그인 정보를 찾을 SQL문
						try {
							PreparedStatement pst = c.prepareStatement(sql);
							pst.setString(1, id);
							ResultSet rs = pst.executeQuery();//SQL문 시행
							while(rs.next()) {
								if(id.equals(rs.getString("u_id")) && pw.equals(rs.getString("u_pw"))){//로그인과 비밀번호가 맞으면
									String name = rs.getString("u_name");//이름을 받아와서
	//								int point = rs.getInt("u_point");
	//								String grade = rs.getString("u_grade");
									new STARBOX(name);//메인페이지 실행, 이름을 메인페이지로 던져줌
									dispose();
								}
								else {//로그인 정보가 틀리면
									JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀립니다.", "로그인 실패", JOptionPane.ERROR_MESSAGE);
								}
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
			else if(btnstr.equals(str[4])) {//회원가입 버튼 클릭
				new Signin();//회원가입 창 실행
				dispose();
			}
			else if(btnstr.equals(str[5])){//종료
				System.exit(0);
			}
			//모든 실행 버튼을 클릭하면 창 닫힘
		}
	}
	public static void main(String[] args) {
		new Main();
	}
}
//유지보수 방안 : 동명이인 문제가 생길 경우 이름이 아닌 ID, u_no로 로그인 및 STARBOX를 실행