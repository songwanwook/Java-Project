package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DB.DriverConnector;

import java.sql.*;
public class Staff extends JFrame{//사원등록 페이지
	String str[] = {"사원번호 : ","* 사원명 : ", "* 패스워드 : ","* 패스워드 재입력 : ","등록","취소"};
	JLabel la[] = new JLabel[4];//텍스트필드 및 비밀번호 확인 필드를 설명하는 JLabel
	JTextField jt[] = new JTextField[2];//사원 번호 및 이름을 입력하는 텍스트필드
	JPasswordField jp[] = new JPasswordField[2];//비밀번호 입력 및 확인하는 텍스트필드
	JButton btn[] = new JButton[2];
	String sql;
	Connection c = DriverConnector.MakeConnection("mealproject");//데이터베이스를 연결하여 사원 등록 및 비밀번호 확인
	Statement st;
	ResultSet rs;
	JLabel tf;
	int n;//등록할 사원 번호
	int count = 0;
	int flag = 0;
	PreparedStatement pst;
	Staff(){
		setTitle("사원 등록");
		setLayout(new GridLayout(5,2));
		for(int i = 0; i < la.length; i++) {
			if(i < 3) {
				la[i] = new JLabel(str[i]);
				add(la[i]);
			}
			else {
				add(new p());
			}
			if(i < jt.length) {
				jt[i] = new JTextField();
				add(jt[i]);
			}
			else {
				jp[i-2] = new JPasswordField();
				add(jp[i-2]);
			}
		}
		jt[0].setEnabled(false);//사원 번호가 입력될 텍스트필드로 수정이 불가능하게 설정
		for(int i = 0; i < btn.length; i++) {
			btn[i] = new JButton(str[i+4]);//등록, 취소 버튼
			add(btn[i]);
			btn[i].addActionListener(new Action());//버튼에 클릭 이벤트 추가
		}
		sql = "select max(memberNo) from member";//현재 등록된 사원 번호에서 가장 큰 번호를 선택한다.
		try {
			st = c.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()) {
				n = rs.getInt("max(memberNo)") + 1;//새 번호는 가장 큰 번호 + 1
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		jt[0].setText(Integer.toString(n));
		jp[0].addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {//비밀번호 필드에 키보드를 입력하면
				if(e.getKeyChar() == '1' || e.getKeyChar() == '2' || e.getKeyChar() == '3' || e.getKeyChar() == '4' || 
					e.getKeyChar() == '5' || e.getKeyChar() == '6' || e.getKeyChar() == '7' || e.getKeyChar() == '8' ||
					e.getKeyChar() == '9' || e.getKeyChar() == '0') {
				count++;
					if(count > 4) {//비밀번호가 4자릿수를 넘어갔을 때
						JOptionPane.showMessageDialog(null,"적정 자릿수를 초과하였습니다.","비밀번호 등록 실패",JOptionPane.ERROR_MESSAGE);
						jp[0].setText("");//최대 자릿수를 초과했다는 알림과 함께 다시 입력하게 초기화
						count = 0;//입력한 자릿수 초기화
					}
				}
				else {//숫자 외 다른 값이 입력됐을 때
					JOptionPane.showMessageDialog(null,"숫자만 입력할 수 있습니다.","비밀번호 등록 실패",JOptionPane.ERROR_MESSAGE);
					jp[0].setText("");//숫자만 입력할 수 있다는 알림과 함께 다시 입력하게 초기화
					count = 0;//입력한 자릿수 초기화
				}
			}
		});
		jp[1].requestFocus();
		jp[1].setFocusable(true);//패스워드 확인 필드에도 이벤트 포커스가 가게 설정
		jp[1].addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {//패스워드 확인 필드에 비밀번호가 입력되었을 시
				password = new String(jp[0].getPassword());
				String pass = new String(jp[1].getPassword());
				if(pass.length() == 4 && pass.equals(password)) {//입력된 패스워드가 일치할 때
					tf.setForeground(Color.blue);
					tf.setText("일치  ");
					flag = 1;
				}
				else {//입력된 패스워드가 일치하지 않을 때
					tf.setForeground(Color.red);
					tf.setText("불일치 ");
					flag = 2;
				}
				
			}
		});
		this.addWindowListener(new back());//X버튼 클릭 이벤트
		setSize(350,200);
		setVisible(true);
	}
	class back extends WindowAdapter {//X버튼을 누를 시 종료하지 않고, 첫 페이지로 이동한다.
		public void windowClosing(WindowEvent e) {
			dispose();
			new MainClass();
		}
	}
	String password;
	class p extends JPanel{
		p() {
			setLayout(new BorderLayout());
			la[3] = new JLabel(str[3]);
			add(la[3],BorderLayout.WEST);
			tf = new JLabel();
			add(tf,BorderLayout.EAST);
		}
	}
	class Action implements ActionListener{//확인, 취소 버튼 클릭 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b = (JButton)e.getSource();
			if(b.getText().equals(str[4])) {//확인 버튼을 누를 시
				if(jt[1].getText().equals("")) {//이름 란이 비어있으면 오류 메시지 출력
					JOptionPane.showMessageDialog(null, "이름을 입력하세요","사원 등록 실패",JOptionPane.ERROR_MESSAGE);
				}
				else {//이름 란이 채워졌을 때
					if(flag == 1) {//패스워드가 일치하면
						sql = "insert into member values(?,?,?)";//사원 멤버 등록 SQL
						String values[] = {jt[0].getText(), jt[1].getText(),password};//사원이름, 사원 비밀번호를 받아온다.
						try {
							pst = c.prepareStatement(sql);
							for(int i = 0; i < 3; i++) {
								if(i == 0) {
									pst.setInt(i+1, Integer.parseInt(values[i]));//사원번호 등록
								}
								else {
									pst.setString(i+1, values[i]);//사원 이름 및 비밀번호 등록
								}
							}
							pst.executeUpdate();
							JOptionPane.showMessageDialog(null, "정상적으로 등록되었습니다.","사원 등록 성공",JOptionPane.CLOSED_OPTION);
							dispose();//사원 등록을 성공한 뒤 창을 닫고 첫 페이지로 이동
							new MainClass();
						} catch (SQLException e1) {//SQL문 오류
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "SQL문 오류","사원 등록 실패",JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
					}
					else if(flag == 2) {//비밀번호가 다를 시
						JOptionPane.showMessageDialog(null, "비밀번호가 다릅니다.","사원 등록 실패",JOptionPane.ERROR_MESSAGE);
					}
					else {//비밀번호 확인란에 입력하지 않을 시
						JOptionPane.showMessageDialog(null, "비밀번호를 확인하세요.","사원 등록 실패",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else {//취소버튼
				dispose();
				new MainClass();//종료 및 메인페이지로 이동
			}
		}
	}
}
