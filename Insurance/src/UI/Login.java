package UI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import DB.DriverConnector;
public class Login extends JFrame{
	String text[] = {"로그인", "이름 : ", "비밀번호 : ", "확인", "취소"};
	JTextField jt; JPasswordField jp;//이름과 비밀번호를 입력받을 텍스트필드, 패스워드 필드
	JButton btn[] = new JButton[2];//로그인 확인, 취소 버튼
	Login(){
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//X를 클릭할 시 종료
		setLayout(null);
		JLabel la = new JLabel(text[0]);
		la.setSize(60, 15);
		la.setLocation(120, 10);
		la.setFont(new Font("",Font.BOLD,15));
		add(la);
		jt = new JTextField(5);//이름 텍스트필드
		jt.setSize(60, 18);
		jp = new JPasswordField(5);//비밀번호 패스워드필드
		jp.setSize(60, 18);
		JLabel login[] = new JLabel[2];
		for(int i = 0; i < 2; i++) {
			login[i] = new JLabel(text[i+1]);
			login[i].setLocation(i*110+10,50);
			if(i==0) {
				login[i].setSize(40, 12);
				jt.setLocation(login[i].getX()+login[i].getWidth(), login[i].getY());
				add(jt);
			}
			else {
				login[i].setSize(80, 12);
				jp.setLocation(login[i].getX()+login[i].getWidth()-10, login[i].getY());
				add(jp);
			}//로그인 텍스트필드와 비밀번호 텍스트필드를 지정된 위치에 배정한다.
			add(login[i]);
			btn[i] = new JButton(text[3+i]);//로그인 확인, 취소 버튼
			btn[i].setSize(60,20);
			btn[i].setLocation(75*(i+1), 90);
			add(btn[i]);
			btn[i].addActionListener(new action());//확인, 취소 버튼을 클릭하면 다음 이벤트를 실행한다.
		}
		setSize(300,160);
		setLocation(800,400);
		setVisible(true);
	}
	class action implements ActionListener{//로그인 확인 취소 버튼을 클릭할 시 실행할 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b = (JButton) e.getSource();
			if(b.getText().equals(text[3])) {//확인 버튼 클릭
				Connection c = DriverConnector.MakeConnection();//데이터베이스 연결
				try {
					PreparedStatement pst = c.prepareStatement("select name from admin where name = ? and passwd = ?");//이름과 패스워드 확인하는 SQL문
					String name = jt.getText();
					String passwd = new String(jp.getPassword());
					pst.setString(1, name);//이름 : 텍스트필드 받아옴
					pst.setString(2, passwd);//패스워드 : 패스워드 필드 받아옴
					ResultSet rs = pst.executeQuery();//SQL문 실행
					if(rs.next()) {//정보 일치
						JOptionPane.showMessageDialog(null, "로그인 성공.","",JOptionPane.CLOSED_OPTION);
						new InsuranceMain();//보험 메인화면으로 이동
						dispose();
					}
					else {//정보 불일치
						JOptionPane.showMessageDialog(null, "ID 또는 비밀번호가 다릅니다.","로그인 실패",JOptionPane.ERROR_MESSAGE);//불일치 메시지 띄움
						jt.setText("");jp.setText("");//이름, 비밀번호 텍스트필드 초기화
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(b.getText().equals(text[4])) {//취소 버튼 클릭
				JOptionPane.showMessageDialog(null, "프로그램을 종료합니다.","메시지",JOptionPane.CLOSED_OPTION);
				System.exit(0);//프로그램 종료
			}
		}
	}
	public static void main(String[] args) {
		new Login();
	}
}
