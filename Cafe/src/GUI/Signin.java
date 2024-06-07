package GUI;
import java.sql.*;
import javax.swing.*;

import DB.DriverConnector;
import GUI.purchase.back;

import java.awt.*;
import java.awt.event.*;
public class Signin extends JFrame{
	String str[] = {"이름","아이디","비밀번호","생년월일","년","월","일","가입완료","취소"};
	JTextField jt[] = new JTextField[2];//이름, 아이디를 입력받을 텍스트필드
	JLabel la[] = new JLabel[7];
	JPasswordField jp;//비밀번호를 받을 패스워드필드
	JComboBox jc[] = new JComboBox[3];//년 월 일을 입력받을 콤보박스
	JButton btn[] = new JButton[2];//가입완료, 취소를 클릭할 버튼
	Connection c;//DB 연결
	Signin(){
		setTitle("회원가입");
		setSize(300,250);
		setVisible(true);
		add(new MainPanel(),BorderLayout.CENTER);
		add(new BottomPanel(), BorderLayout.SOUTH);
		this.addWindowListener(new back());
	}
	class MainPanel extends JPanel {//이름, 아이디, 패스워드를 받을 패널
		MainPanel(){
			setLayout(null);
			for(int i = 0; i < 3; i++) {
				la[i] = new JLabel(str[i]);
				add(la[i]);
				la[i].setSize(90,20);
				la[i].setLocation(0, 30*(i+1));
				la[i].setHorizontalAlignment(JLabel.RIGHT);
				add(la[i]);
				if(i != 2) {//이름과 아이디를 입력받을 텍스트필드
					jt[i] = new JTextField(18);
					jt[i].setSize(150,20);
					jt[i].setLocation(100, 30*(i+1));
					add(jt[i]);
				}
				else {//비밀번호를 입력받을 패스워드필드
					jp = new JPasswordField(18);
					jp.setSize(150,20);
					jp.setLocation(100, 30*(i+1));
					add(jp);
				}
			}
		}
	}
	class back extends WindowAdapter{//X 버튼을 클릭 시 종료가 아닌 뒷화면으로 가는 이벤트, EXIT ON CLOSE을 하면 GUI화면 실행 안됨.
		public void windowClosing(WindowEvent e) {
			dispose();
			new Main();
		}
	}
	class BottomPanel extends JPanel{//생년월일과 확인 취소 버튼이 등록될 패널
		BottomPanel(){
			setLayout(new GridLayout(2,1));
			add(new BirthPanel());
			add(new ButtonPanel());
		}
	}
	class BirthPanel extends JPanel{//생년월일을 입력받는 패널
		BirthPanel(){
			for(int i = 3; i < 7; i++) {
				la[i] = new JLabel(str[i]);
				add(la[i]);
				if(i-3 < 3) {
					jc[i-3] = new JComboBox();
					add(jc[i-3]);
				}
			}
			for(int i = 1900; i <= 2024; i++) {
				jc[0].addItem(i);//년
			}
			for(int i = 1; i <= 12; i++) {
				jc[1].addItem(i);//월
			}
			for(int i = 0; i < 2; i++) {
				jc[i].addItemListener(new days());
			}
			for(int i = 1; i <= day; i++) {
				jc[2].addItem(i);//일
			}
		}
	}
	int day = 31;//일, 1월부터 시작하여 31로 초기화
	int count = 0;
	class days implements ItemListener {//년월별로 일수를 기록할 아이템 이벤트
		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			jc[2].removeAllItems();
			int month = (int)jc[1].getSelectedItem();//달로 일을 구분할 변수
			int year = (int)jc[0].getSelectedItem();//윤년을 구분할 년도
			if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {//31일이 될 조건
				day = 31;
			}
			else if(month == 4 || month == 6 || month == 9 || month == 11) {//30일이 될 조건
				day = 30;
			}
			else {//2월
				if(year % 4 == 0 && year != 1900) {//윤년일때(1900년은 윤년이 아님)
					day = 29;
				}
				else {//윤년이 아닐 때
					day = 28;
				}
			}
			for(int i = 1; i <= day; i++) {
				jc[2].addItem(i);//일
			}
			
		}
	}
	class ButtonPanel extends JPanel {
		ButtonPanel(){
			for(int i = 0; i < btn.length; i++) {//가입완료, 취소 버튼
				btn[i] = new JButton(str[str.length-2+i]);
				add(btn[i]);
				btn[i].addActionListener(new Action());
			}
		}
		class Action implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JButton b = (JButton)e.getSource();
				if(b.getText().equals(str[str.length-2])) {//가입하기 버튼 클릭
					if(jt[0].getText().equals("")||jt[1].getText().equals("")||jp.getPassword().length==0) {//이름, 아이디, 비밀번호를 다 입력하지 않음
						JOptionPane.showMessageDialog(null,"이름, 아이디, 비밀번호를 모두 입력하세요" ,"회원가입 실패" ,JOptionPane.ERROR_MESSAGE);
					}
					else{
						if(jp.getPassword().length>4) {//비밀번호 자릿수 초과
							JOptionPane.showMessageDialog(null,"비밀번호 자릿수를 초과하였습니다." ,"회원가입 실패" ,JOptionPane.ERROR_MESSAGE);
						}
						else {//월, 일 설정
							String month, days;
							if((int)jc[1].getSelectedItem() < 10) {
								month = "0" + String.valueOf(jc[1].getSelectedItem());
							}//1~9월 YYYY-0M-DD
							else {
								month = (String)jc[1].getSelectedItem();
							}
							if((int)jc[2].getSelectedItem() < 10) {
								days = "0" + String.valueOf(jc[2].getSelectedItem());
							}//1~9일 YYYY-MM-0D
							else {
								days = (String)jc[2].getSelectedItem();
							}
							String data[] = {jt[1].getText(), new String(jp.getPassword()),jt[0].getText(),
									jc[0].getSelectedItem()+"-"+month+"-"+days};//회원가입할 String형 데이터(ID, 이름, 생년월일)
							String selectsql = "select u_id from user";//ID로 중복 체크할 SQL문
							try {
								c = DriverConnector.MakeConnection("cafe");//데이터베이스 연결
								Statement st = c.createStatement();
								ResultSet rs = st.executeQuery(selectsql);//중복체크 SQL 실행
								if(rs.next()) {
									String u_id = rs.getString("u_id");//중복체크할 ID값
									if(data[1].equals(u_id)) {//중복체크
										JOptionPane.showMessageDialog(null,"중복된 아이디가 있습니다." ,"회원가입 실패" ,JOptionPane.ERROR_MESSAGE);
									}
									else {//중복체크 성공
										String sql = "insert into user(u_id, u_pw, u_name, u_bd, u_point, u_grade) values(?,?,?,?,0,'일반');";//회원가입 SQL문
										try {
											PreparedStatement pst = c.prepareStatement(sql);
											for(int i = 1; i <= 4; i++) {
												pst.setString(i, data[i-1]);//String형 데이터에 회원가입할 정보(ID, 비번, 이름, 생년월일)를 삽입
											}
											pst.executeUpdate();//SQL문 실행
											JOptionPane.showMessageDialog(null, "회원가입 성공");//회원가입 성공
											dispose();
											new Main();//로그인 창으로 돌아가기
										} catch (SQLException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									}
								}
							} catch (SQLException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
						}
					}
				}
				else {//취소(로그인 창으로 돌아가기)
					dispose();
					new Main();
				}
			}
		}
	}
}
