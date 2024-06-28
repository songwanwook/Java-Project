package UI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;

import DB.DriverConnector;
import UI.clientMenu.Bottom;
import UI.clientMenu.MenuPanel;

import javax.swing.*;
import java.sql.*;
import java.util.*;
public class CustomerView extends JFrame{//고객조회 화면
	String str[] = {"성명","조회","전체보기","수정","삭제","닫기"};
	String data[] = {"code","name","birth","tel","address","company"};//테이블 헤더에 등록할 데이터
	String index[] = new String[6];
	JTextField jt;
	JButton btn[] = new JButton[5];//조회, 전체보기, 수정, 삭제, 닫기 버튼
	Vector<Vector<String>> rowData;//테이블에 들어갈 2차원 벡터
	JTable t;//테이블
	DefaultTableModel model;
	String sql;
	Connection c = DriverConnector.MakeConnection();//데이터베이스 연결
	Statement st;
	PreparedStatement pst;
	ResultSet rs;
	CustomerView(){
		setTitle("고객 조회");
		add(new Menu(),BorderLayout.NORTH);
		add(new Table(),BorderLayout.CENTER);
		setSize(600,600);
		setLocation(650,250);
		setVisible(true);
		addWindowListener(new back());//뒤로가기
	}
	class back extends WindowAdapter{//X버튼을 클릭하면 메인페이지로 뒤로가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new InsuranceMain();
		}
	}
	class Menu extends JPanel{
		Menu(){
			for(int i = 0; i < str.length; i++) {
				if(i == 0) {
					add(new JLabel(str[i]));
					jt = new JTextField(10);//검색 기능 텍스트필드
					add(jt);
				}
				else {
					btn[i-1] = new JButton(str[i]);//조회 버튼
					add(btn[i-1]);
					btn[i-1].addActionListener(new Action());//조회 기능 이벤트
				}
			}
			jt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					SelectCustomer();//텍스트필드에 엔터를 누를 시 해당 사원 정보를 출력
				}
			});
		}
	}
	class Table extends JPanel{
		Table(){
			rowData = new Vector<Vector<String>>();//테이블 정보를 등록 2차원 데이터 벡터
			Vector<String>colData = new Vector<String>();//테이블 헤더에 등록할 데이터 벡터
			for(int i = 0; i < data.length; i++) {
				colData.add(data[i]);//테이블 헤더 등록
			}
			model = new DefaultTableModel(rowData, colData);
			t = new JTable(model);//테이블 생성
			JScrollPane jps = new JScrollPane(t);//스크롤팬에 테이블 등록
			add(jps);
			/*t.addMouseListener(new MouseAdapter() {//테이블 클릭 이벤트 등록
				public void MouseClicked(MouseEvent e) {
					int selection = t.getSelectedRow();
					Vector<String>vc = rowData.get(selection);//선택한 테이블 열 정보 가져옴
					for(int i = 0; i < index.length; i++) {
						index[i] = vc.get(i);//index 배열에다 저장.
					}
				}
			});*/
		}
	}
	class Action implements ActionListener {//버튼을 클릭할 때 추가되는 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String cmd = e.getActionCommand();
			switch(cmd) {
				case "조회":SelectCustomer();break;//조회버튼 클릭, 조회 실행
				case "전체보기":ListCustomer();break;//전체보기 버튼 클릭, 전체보기 실행
				case "수정"://수정하기 버튼 클릭
					if(t.getSelectedRowCount() == 0) {//수정할 테이블 열이 선택 안되있으면 메시지 출력
						JOptionPane.showMessageDialog(null,"하나 이상 선택하세요.","수정불가",JOptionPane.ERROR_MESSAGE);
					}
					else {//테이블 열이 선택되면 수정하기 실행
						UpdateCustomer();
					}
					break;
				case "삭제"://삭제하기 버튼 클릭
					if(t.getSelectedRowCount() == 0) {//삭제할 테이블 열이 선택 안되있으면 메시지 출력
						JOptionPane.showMessageDialog(null,"하나 이상 선택하세요.","삭제불가",JOptionPane.ERROR_MESSAGE);
					}
					else {//선택된 테이블 열 삭제 후 테이블 갱신
						DeleteCustomer();ListCustomer();
					}
					break;
				case "닫기":dispose();new InsuranceMain();break;//닫기버튼 클릭, 현재 페이지 닫고, 메인 페이지로 이동
			}
		}
	}
	private void ListCustomer() {//전체보기
		sql = "select * from customer";//전체보기를 할 SQL문
		try {
			rowData.clear();//테이블 초기화
			st = c.createStatement();
			rs = st.executeQuery(sql);//SQL문 실행
			while(rs.next()) {
				Vector<String>V = new Vector<String>();//결과값 내용을 담을 벡터
				for(int i = 0; i < data.length; i++) {
					V.add(rs.getString(i+1));//벡터에 SQL문 결과값을 추가
				}
				rowData.add(V);//결과값을 테이블 벡터에 추가
			}
			t.updateUI();//테이블 갱신
		}
		catch(Exception ee) {
			System.out.print(ee);
		}
	}
	private void SelectCustomer() {//조회
		sql = "select * from customer where name like '%" + jt.getText() + "%'";//텍스트필드 내용에 따라 이름 기준으로 검색할 SQL문
		try {
			rowData.clear();//테이블 초기화
			st = c.createStatement();
			rs = st.executeQuery(sql);//SQL문 실행
			while(rs.next()) {
				Vector<String>V = new Vector<String>();//결과값 내용을 담을 벡터
				for(int i = 0; i < data.length; i++) {
					V.add(rs.getString(i+1));//벡터에 SQL문 결과값을 추가
				}
				rowData.add(V);//결과값을 테이블 벡터에 추가
			}
			t.updateUI();//테이블 갱신
		}
		catch(Exception ee) {
			System.out.print(ee);
		}
	}
	private void UpdateCustomer() {//고객 수정
		new Update();//고객수정 창 실행
	}
	class Update extends JFrame {//고객수정 페이지
		String str[] = {"고객 코드 : ", "* 고객 명 : ", "* 생년월일(YYYY-MM-DD) : ", "* 연락처 : ", "주소 : ", "회사 : ","확인","취소"};
		JLabel la[] = new JLabel[6];
		JTextField jt[] = new JTextField[6];//고객코드, 고객명, 생년월일, 연락처, 주소, 회사 정보 텍스트필드
		JButton btn[] = new JButton[2];//확인 취소 버튼
		Calendar cal = Calendar.getInstance();//현재 날짜
		int year = cal.get(Calendar.YEAR)-2000;//현재년도 코드 int형 변수
		Update(){
			setTitle("고객 수정");
			setLayout(new BorderLayout());
			add(new MenuPanel(),BorderLayout.CENTER);
			add(new Bottom(), BorderLayout.SOUTH);
			setSize(400,300);
			setLocation(650,250);
			setVisible(true);
		}
		class MenuPanel extends JPanel{
			MenuPanel(){
				setLayout(new GridLayout(6,2));
				for(int i = 0; i < la.length; i++) {
					la[i] = new JLabel(str[i]);
					add(la[i]);
					jt[i] = new JTextField(10);//고객코드, 고객명, 생년월일, 연락처, 주소, 회사 정보가 입력된 텍스트필드 등록
					add(jt[i]);
					if(i < 2) {
						jt[i].setEnabled(false);//고객코드, 고객명은 수정 불가능
					}
					int selection = t.getSelectedRow();//현재 클릭된 테이블 번호 리턴
					Vector<String>vc = rowData.get(selection);//현재 클릭된 테이블 번호를 불러옴
					jt[i].setText(vc.get(i));//현재 클릭된 테이블 벡터에 등록
				}
			}
		}
		class Bottom extends JPanel{
			Bottom(){
				for(int i = 0; i < btn.length; i++) {
					btn[i] = new JButton(str[i+6]);//확인, 취소 버튼
					btn[i].setSize(60, 20);
					add(btn[i]);
					btn[i].addActionListener(new ActionListener() {//확인, 취소 버튼 클릭 이벤트
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							JButton b = (JButton)e.getSource();
							if(b.getText().equals(str[6])) {//확인 버튼 클릭
								if(jt[2].getText().equals("") || jt[3].getText().equals("")) {//필수 텍스트필드를 입력하지 않았을 때
									JOptionPane.showMessageDialog(null,"필수 항목을 모두 입력하세요.","수정불가",JOptionPane.ERROR_MESSAGE);
								}
								else {
									sql = "update customer set birth = ?, tel = ?, address = ?, company = ? where code = ?";//회원정보 수정할 SQL문
									String update[] = new String[5];
									try {
										pst = c.prepareStatement(sql);
										for(int i = 0; i < update.length; i++) {//회원정보 수정(고객코드, 생년월일, 연락처, 주소, 회사)
											if(i==4) {
												update[i] = jt[0].getText();//고객코드 수정
											}
											else {
												update[i] = jt[i+2].getText();//생년월일, 연락처, 주소, 회사 수정
											}
											pst.setString(i+1,update[i]);
										}				
										int re = pst.executeUpdate();//SQL문 실행
										if(re > 0) {
											JOptionPane.showMessageDialog(null,"수정 완료", "", JOptionPane.CLOSED_OPTION);
											dispose();
										}//수정 완료 알리고 창닫음
										//else {
											//JOptionPane.showMessageDialog(null,"수정 실패.","",JOptionPane.ERROR_MESSAGE);
										//}
									}
									catch(Exception ee) {
										System.out.println(ee);
									}
								}
							}
							else if(b.getText().equals(str[7])) {//수정취소 버튼 클릭
								JOptionPane.showMessageDialog(null,"수정을 취소합니다.","수정 실패",JOptionPane.ERROR_MESSAGE);
								dispose();//수정 실패를 알리고 창닫음
							}
						}
					});;
				}
			}
		}
	}
	private void DeleteCustomer() {
		int selection = t.getSelectedRow();
		Vector<String>vc = rowData.get(selection);
		sql = "delete from customer where name = ?";
		String dname = vc.get(1);
		int select = JOptionPane.showConfirmDialog(null, dname + "님을 삭제하시겠습니까?","고객정보 삭제",JOptionPane.YES_NO_OPTION);
		if(select == JOptionPane.YES_OPTION) {
			try {
				pst = c.prepareStatement(sql);
				pst.setString(1, dname);
				int re = pst.executeUpdate();
				if(re>0) {
					JOptionPane.showMessageDialog(this, "삭제 완료");
				}
				else {
					JOptionPane.showMessageDialog(this, "삭제 실패");
				}
			}
			catch(Exception ee) {
				System.out.print(ee);
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "삭제 실패");
		}
	}
	/*public static void main(String[] args) {
		new CustomerView();
	}*/
}
