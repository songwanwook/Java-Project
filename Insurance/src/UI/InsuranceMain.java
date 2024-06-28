package UI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class InsuranceMain extends JFrame{//보험 메인화면
	String menu[] = {"고객등록","고객조회","고객관리","종료"};
	JButton btn[] = new JButton[4];//메뉴 기능(고객등록, 고객조회, 고객관리, 종료)을 실행할 버튼
	ImageIcon img = new ImageIcon("img/img.jpg");//메인 창에 메뉴 버튼 밑에 등록될 이미지
	InsuranceMain(){
		setTitle("보험계약 관리 화면");
		setLayout(new BorderLayout());
		add(new insuranceMenu(), BorderLayout.NORTH);
		add(new insuranceImg(),BorderLayout.CENTER);
		setSize(500,500);
		setLocation(700,300);
		setVisible(true);
		addWindowListener(new back());//뒤로가기
	}
	class back extends WindowAdapter{//X버튼을 누를 시 뒤로감
		public void windowClosing(WindowEvent e) {
			dispose();
			new Login();
		}
	}
	class insuranceImg extends JPanel{
		insuranceImg(){
			JLabel la = new JLabel(img);
			add(la);
			setSize(400,400);//메인창 이미지 등록
		}
	}
	class insuranceMenu extends JPanel{
		insuranceMenu(){
			for(int i = 0; i < btn.length; i++) {
				btn[i] = new JButton(menu[i]);//고객등록, 고객조회, 고객관리, 종료 기능을 실행할 버튼
				btn[i].setSize(70,30);
				add(btn[i]);
				btn[i].addActionListener(new action());//해당 기능을 실행할 이벤트리스너
			}
			setSize(500,40);
		}
	}
	class action implements ActionListener {//고객등록, 고객조회, 고객관리, 종료 기능 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b = (JButton)e.getSource();
			dispose();
			if(b.getText().equals(menu[0])) {//고객등록 기능
				new clientMenu();//고객등록 실행
			}
			else if(b.getText().equals(menu[1])) {//고객조회 기능
				new CustomerView();//고객조회 실행
			}
			else if(b.getText().equals(menu[2])) {//고객관리 기능
				new Contract();//고객관리 실행
			}
			else if(b.getText().equals(menu[3])) {//종료 기능
				JOptionPane.showMessageDialog(null, "종료", "프로그램을 종료합니다.",JOptionPane.CLOSED_OPTION);
				new Login();//로그아웃
			}
		}
	}
	/*public static void main(String[] args) {
		new InsuranceMain();
	}*/
}
