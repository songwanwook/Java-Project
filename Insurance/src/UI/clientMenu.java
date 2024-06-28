package UI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DB.DriverConnector;

import java.util.*;
import java.sql.*;
public class clientMenu extends JFrame{//고객등록 화면
	String str[] = {"고객 코드 : ", "* 고객 명 : ", "* 생년월일(YYYY-MM-DD) : ", "* 연락처 : ", "주소 : ", "회사 : ","확인","취소"};
	JLabel la[] = new JLabel[6];
	JTextField jt[] = new JTextField[6];//고객코드, 고객명, 생년월일, 연락처, 주소, 회사를 입력받을 텍스트필드
	JButton btn[] = new JButton[2];//확인 취소 버튼
	Calendar cal = Calendar.getInstance();//현재시간을 입력받을 변수
	int year = cal.get(Calendar.YEAR)-2000;//년도를 입력받을 변수
	clientMenu(){
		setTitle("고객 등록");
		setLayout(new BorderLayout());
		add(new MenuPanel(),BorderLayout.CENTER);
		add(new Bottom(), BorderLayout.SOUTH);
		setSize(400,300);
		setLocation(750,400);
		setVisible(true);
		addWindowListener(new back());//뒤로가기
	}
	class back extends WindowAdapter{//X를 클릭시 보험메인으로 뒤로가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new InsuranceMain();
		}
	}
	class MenuPanel extends JPanel{
		MenuPanel(){
			setLayout(new GridLayout(6,2));
			for(int i = 0; i < str.length-2; i++) {
				la[i] = new JLabel(str[i]);
				add(la[i]);
				jt[i] = new JTextField();//고객코드, 고객명, 생년월일, 연락처, 주소, 회사를 입력받을 텍스트필드 추가
				add(jt[i]);
			}
			jt[0].setEnabled(false);//고객코드는 비활성화
			jt[2].addActionListener(new ActionListener() {//생년월일이 입력될때 이벤트가 발생한다.
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String birth[] = jt[2].getText().split("-");//생년월일을 - 단위로 자른다.
					int sum = Integer.valueOf(birth[0])+Integer.valueOf(birth[1])+Integer.valueOf(birth[2]);//자른 생년월일을 int형으로 변환하여 합친다.
					jt[0].setText("S"+year+sum);//고객코드를 S + 년도 + 합친 생년월일로 등록한다.
				}
			});
		}
	}
	class Bottom extends JPanel{
		Bottom(){
			for(int i = 0; i < btn.length; i++) {
				btn[i] = new JButton(str[i+6]);//확인, 취소 버튼
				btn[i].setSize(60, 20);
				add(btn[i]);
				btn[i].addActionListener(new ActionListener() {//확인 취소 버튼 시 이벤트
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						JButton b = (JButton)e.getSource();
						if(b.getText().equals(str[6])) {//확인 버튼
							if(jt[1].getText().equals("")||jt[2].getText().equals("")||jt[3].getText().equals("")) {//고객명, 생년월일, 연락처 중 하나라도 비어있는 경우
								JOptionPane.showMessageDialog(null, "필수 항목을 모두 입력하세요.","회원 등록 실패",JOptionPane.ERROR_MESSAGE);//등록 불가 메시지 출력
							}
							else {//모두 입력됨
								
								String birth[] = jt[2].getText().split("-");//생년월일을 - 단위로 쪼갬
								int sum = Integer.valueOf(birth[0])+Integer.valueOf(birth[1])+Integer.valueOf(birth[2]);//자른 생년월일을 int형으로 변환하여 합친다.
								jt[0].setText("S"+year+sum);//고객코드를 S + 년도 + 합친 생년월일로 등록한다.
								Connection c = DriverConnector.MakeConnection();//데이터베이스 연결
								String sql = "insert into customer values(?,?,?,?,?,?)";//customer 테이블에 회원정보를 등록할 SQL문
								try {
									PreparedStatement pst = c.prepareStatement(sql);
									for(int i = 0; i < jt.length; i++) {
										pst.setString(i+1, jt[i].getText());//텍스트필드에 있는 모든 데이터를 받아와서
									}
									pst.executeUpdate();//SQL문 실행
									JOptionPane.showMessageDialog(null, "정상적으로 등록이 되었습니다.","회원 등록 성공",JOptionPane.CLOSED_OPTION);//정상적으로 등록됬다는 알림 전송
									dispose();
									new InsuranceMain();//메인 화면으로 이동
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
						else if(b.getText().equals(str[7])) {//취소 버튼
							JOptionPane.showMessageDialog(null, "회원 등록을 취소합니다.","회원 등록 실패",JOptionPane.ERROR_MESSAGE);
							dispose();
							new InsuranceMain();//회원등록을 실패하고 다시 메인 페이지로 이동
						}
					}
				});
			}
		}
	}
	/*public static void main(String[] args) {
		new clientMenu();
	}*/
}
