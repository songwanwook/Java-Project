package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import DB.DriverConnector;

import java.sql.*;
public class InsertMenu extends JFrame{//관리자가 메뉴 등록하는 페이지
	String str[] = {"종류","*메뉴명","가격","조리가능수량","등록","닫기"};
	String menu[] = {"한식","중식","일식","양식"};//식사 메뉴 유형 목록
	JLabel la[] = new JLabel[4];
	JComboBox jc[] = new JComboBox[3];//종류, 가격, 조리가능수량을 등록할 콤보박스
	JButton btn[] = new JButton[2];//확인, 취소 버튼
	JTextField jt = new JTextField();//메뉴명을 입력할 텍스트 필드
	int total;//식사 번호를 받을 변수
	int index;//식사 유형 종류 번호를 입력받을 변수
	int price;//가격을 입력받을 변수
	int maxcount;//조리가능수량을 입력받을 변수
	String mealName;//식사명을 입력받을 변수
	InsertMenu(){
		setTitle("신규 메뉴 추가");
		setLayout(new GridLayout(5,2));
		for(int i = 0; i < la.length; i++) {
			la[i] = new JLabel(str[i]);
			add(la[i]);
			if(i == 0) {
				jc[i] = new JComboBox();//식사 종류 콤보박스 등록
				add(jc[i]);
			}
			else {
				if(i == 1) {
					add(jt);//메뉴명 텍스트필드 등록
				}
				else if(i > 1) {
					jc[i-1] = new JComboBox();//가격, 조리가능수량 콤보박스 등록
					add(jc[i-1]);
				}
			}
		}
		for(int i = 0; i < menu.length; i++) {
			jc[0].addItem(menu[i]);//식사 종류 콤보박스
		}
		
		for(int i = 2; i < 25; i++) {
			jc[1].addItem(i*500);//가격 콤보박스(1000~12000원)
		}
		
		for(int i = 0; i <= 50; i++) {
			jc[2].addItem(i);//조리가능수량 콤보박스(1~50개)
		}
		for(int i = 0; i < jc.length; i++) {
			jc[i].addItemListener(new ItemListener() {//콤보박스를 선택했을 시 이벤트
				@Override
				public void itemStateChanged(ItemEvent e) {//선택된 콤보박스에
					// TODO Auto-generated method stub
					index = jc[0].getSelectedIndex()+1;//식사 유형 번호 입력받음
					price = Integer.parseInt(jc[1].getSelectedItem().toString());//가격을 입력받음
					maxcount = Integer.parseInt(jc[2].getSelectedItem().toString());//조리가능수량을 입력받음
				}
			});
		}
		
		for(int i = 0; i < btn.length; i++) {//확인, 취소 버튼
			btn[i] = new JButton(str[i+4]);
			add(btn[i]);
			btn[i].addActionListener(new Action());
		}
		setSize(300,240);
		setVisible(true);
		Connection c = DriverConnector.MakeConnection("mealproject");
		String sql = "select max(mealNo) from meal";//번호를 자동 등록할 SQL문
		try {
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);//SQL 실행
			while(rs.next()) {
				total = rs.getInt("max(mealNo)");//등록받을 식사 새 번호
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		total++;//등록받은 새 식사 번호는 종전 메뉴의 최댓값 +1의 번호를 받는다.
		this.addWindowListener(new back());//X창 눌렀을 시에 종료하지 말고 종전 창으로 이동
	}
	
	class back extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
			new Admin();
		}
	}
	
	class Action implements ActionListener{//버튼 클릭 시 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b = (JButton)e.getSource();
			if(b.getText().equals(str[4])) {//등록 버튼을 클릭할 시
				mealName = jt.getText();
				if(mealName.equals("")) {//메뉴명 텍스트필드가 빌 때
					JOptionPane.showMessageDialog(null,"메뉴명을 입력하세요.", "등록 실패",JOptionPane.ERROR_MESSAGE);
				}
				else {//메뉴명을 입력했을 때
					Connection c = DriverConnector.MakeConnection("mealproject");//DB 연결
					int sqlint[] = {total,index,price,maxcount,1};//메뉴변호, 식사 유형 번호, 가격, 조리가능수량, 오늘의메뉴 선정 변수 배열
					
					String sql = "insert into meal values(?,?,?,?,?,?)";//메뉴 등록할 SQL문
					try {
						PreparedStatement psmt = c.prepareStatement(sql);
						for(int i = 0; i < 6; i++) {
							if(i == 2) {
								psmt.setString(i+1, mealName);//메뉴명 등록
							}
							else {//메뉴변호, 식사 유형 번호, 가격, 조리가능수량, 오늘의메뉴 선정여부 등록
								if(i < 2) {
									psmt.setInt(i+1, sqlint[i]);
								}
								else if(i > 2) {
									psmt.setInt(i+1, sqlint[i-1]);
								}
							}
						}
						int re = psmt.executeUpdate();//SQL문 실행
						if(re == 1) {//등록 성공
							JOptionPane.showMessageDialog(null, "등록 성공","새 메뉴가 등록되었습니다.",JOptionPane.CLOSED_OPTION);
							dispose();
							new Admin();//창닫기, 이전 창 실행
						}
						else {
							JOptionPane.showMessageDialog(null, "등록 실패","시스템 오류",JOptionPane.ERROR_MESSAGE);
						}
					} catch (SQLException e1) {//SQL문 에러났을 때
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "등록 실패","SQL문 오류",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			}
			else if(b.getText().equals(str[5])) {//취소 버튼 클릭
				dispose();
				new Admin();//창닫기, 이전 창 실행
			}
		}
	}
}
