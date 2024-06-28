package UI;
import java.sql.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.time.*;
import DB.DriverConnector;
import java.io.*;
public class Contract extends JFrame{//고객관리 화면
	String str[] = {"고객 코드 : ", "고객명 : ", "생년월일 : ", "전화번호 : ", "상품명 : ", "가입금액 : ", "월보험료 : ", "담당자", "가입", "삭제", "파일로 저장", "닫기"};
	JTextField jt[] = new JTextField[7];//고객코드, 생년월일, 전화번호, 가입금액, 월보험료를 입력받을 텍스트필드
	JLabel la[] = new JLabel[7];
	JComboBox jc[] = new JComboBox[3];//고객명, 종신보험, 담당자 목록을 만들 콤보박스 리스트
	JButton btn[] = new JButton[4];//가입, 삭제, 파일로저장, 닫기 기능을 실행할 버튼
	Vector<Vector<String>> rowData;//테이블 데이터가 들어갈 2차원 벡터
	String data[] = {"CustomerCode","CustomerName","regPrice","regDate","monthPrice","adminName"};//테이블 제목 데이터
	JTable t;
	DefaultTableModel model;//테이블
	String sql;
	PreparedStatement pst;
	Connection c = DriverConnector.MakeConnection();
	ResultSet rs;
	//Calendar cal;
	Contract(){
		add(new MenuPanel(),BorderLayout.NORTH);
		add(new TablePanel(),BorderLayout.CENTER);
		setSize(600,600);
		setLocation(650,250);
		setVisible(true);
		addWindowListener(new back());//뒤로가기
	}
	class back extends WindowAdapter{//X 버튼을 클릭할 시 보험 메인 페이지로 이동
		public void windowClosing(WindowEvent e) {
			dispose();
			new InsuranceMain();
		}
	}
	class MenuPanel extends JPanel{
		MenuPanel(){
			setLayout(new BorderLayout());
			add(new MenuText(),BorderLayout.CENTER);
			add(new MenuButton(),BorderLayout.SOUTH);
		}
	}
	class MenuText extends JPanel{
		MenuText(){
			setLayout(new GridLayout(1,2));
			add(new MenuTextLeft());
			add(new MenuTextRight());
		}
	}
	class MenuTextLeft extends JPanel{
		MenuTextLeft(){
			setLayout(new GridLayout(4,2));
			for(int i = 0; i < 4; i++) {
				la[i] = new JLabel(str[i]);
				add(la[i]);
				if(i == 1) {
					jc[0] = new JComboBox();//customer 목록을 담을 콤보박스
					sql = "select name from customer";//customer 이름을 출력할 SQL문
					try {
						pst = c.prepareStatement(sql);
						rs = pst.executeQuery();//sql문 실행
						while(rs.next()) {
							String name = rs.getString("name");
							jc[0].addItem(name);//콤보박스에 customer 이름 목록 추가
						}
					}
					catch(Exception ee) {
						ee.printStackTrace();
					}
					add(jc[0]);//customer 목록 콤보박스 추가
					jc[0].addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {//customer 콤보박스 아이템이 바뀔때 생성되는 이벤트
							// TODO Auto-generated method stub
							String name = jc[0].getSelectedItem().toString();//선택된 아이템 변수를 String형으로 변환
							String sql2 = "select distinct code, birth, tel from customer where name = '" + name + "'";//선택된 이름으로 코드, 생년월일, 전화번호를 찾는 SQL문
							try {
								pst = c.prepareStatement(sql2);
								rs = pst.executeQuery();//SQL문 실행
								while(rs.next()) {
									String code = rs.getString("code");
									jt[0].setText(code);jt[0].setEditable(false);//회원 코드값을 텍스트필드로 반환
									String birth = rs.getString("birth");
									jt[2].setText(birth);jt[2].setEditable(false);//회원 생년월일을 텍스트필드로 반환
									String tel = rs.getString("tel");
									jt[3].setText(tel);jt[3].setEditable(false);//회원 전화번호를 텍스트필드로 반환
								}//고유한 정보이기 때문에 텍스트필드는 수정이 불가능하다.
							}
							catch(SQLException ee) {
								ee.printStackTrace();
							}
							showTables();//해당 회원의 보험 목록 테이블 보여주기
						}
					});
				}
				else {
					jt[i] = new JTextField(10);//고객코드, 생년월일, 전화번호를 입력받을 텍스트필드 추가
					add(jt[i]);
				}
			}
		}
	}
	private void showTables(){//테이블 보여주기
		String sql3 = "select * from contract where customerCode = '" + jt[0].getText() + "' order by regDate DESC";//보험계약 테이블에서 고객코드를 통해 날짜별 내림차순으로 검색하는 SQL문
		Statement st;
		try {
			rowData.clear();//테이블 정보 제거
			st = c.createStatement();
			ResultSet rs2 = st.executeQuery(sql3);//SQL문 실행
			while(rs2.next()) {
				Vector<String>V = new Vector<String>();//테이블 정보를 닫을 벡터
				for(int i = 0; i < data.length; i++) {
					V.add(rs2.getString(i+1));//벡터에 해당 회원 보험계약 정보 추가
				}
				rowData.add(V);//테이블 데이터 벡터에 해당 계약내용 추가
			}
			t.updateUI();//테이블 갱신
		}
		catch(SQLException ee) {
			ee.printStackTrace();
		}
	}
	class MenuTextRight extends JPanel{
		MenuTextRight(){
			setLayout(new GridLayout(3,2));
			for(int i = 4; i < jt.length; i++) {
				la[i] = new JLabel(str[i]);
				add(la[i]);
				if(i == 4) {
					jc[1] = new JComboBox();//보험계약 목록을 담을 콤보박스
					sql = "select distinct contractName from contract";//보험계약 이름을 리턴받을 SQL문
					try {
						pst = c.prepareStatement(sql);
						ResultSet rs = pst.executeQuery();//SQL문 실행
						while(rs.next()) {
							String name = rs.getString("contractName");
							jc[1].addItem(name);//콤보박스에 보험계약명 목록 추가
						}
					}
					catch(Exception ee) {
						ee.printStackTrace();
					}
					add(jc[1]);//보험계약 목록 콤보박스 추가
				}
				else {
					jt[i] = new JTextField(10);//가입금액, 월보험료 텍스트필드 추가
					add(jt[i]);
				}
			}
		}
	}
	class MenuButton extends JPanel{
		MenuButton(){
			for(int i = 7; i < str.length; i++) {
				if(i == 7) {
					add(new JLabel(str[i]));
					jc[2] = new JComboBox();//담당자 목록 콤보박스
					sql = "select distinct adminName from contract";//보험계약 테이블에서 담당자 목록을 검색하는 SQL문
					try {
						pst = c.prepareStatement(sql);
						ResultSet rs = pst.executeQuery();//SQL문 실행
						while(rs.next()) {
							String name = rs.getString("adminName");
							jc[2].addItem(name);//콤보박스에 담당자 목록 추가
						}
					}
					catch(Exception ee) {
						ee.printStackTrace();
					}
					add(jc[2]);//담당자 콤보박스 추가
				}
				else {
					btn[i-8] = new JButton(str[i]);//가입, 삭제, 파일로저장, 닫기 버튼 추가
					add(btn[i-8]);
					btn[i-8].addActionListener(new Action());//가입, 삭제, 파일로저장, 닫기 기능 이벤트 추가
				}
			}
		}
	}
	class Action implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {//버튼 클릭 이벤트
			// TODO Auto-generated method stub
			JButton btn = (JButton)e.getSource();
			if(btn.getText().equals(str[8])) {//가입 버튼 클릭
				LocalDate now = LocalDate.now();//현재 시간 반환 변수
				if(jt[5].getText().equals("") || jt[6].getText().equals("")) {//가입금액, 월보험료가 빈칸일때 오류 메시지
					JOptionPane.showMessageDialog(null, "가입금액, 월보험료를 모두 입력하세요.", "등록 실패", JOptionPane.ERROR_MESSAGE);
				}
				else {//모두 입력
					sql = "insert into contract values(?,?,?,?,?,?)";//보험계약 등록 SQL문
					String values[] = new String[6];
					values[0] = jt[0].getText();//고객코드
					values[1] = jc[1].getSelectedItem().toString();//보험명
					values[2] = jt[5].getText();//가입금액
					values[3] = now.toString();//현재 날짜
					values[4] = jt[6].getText();//월보험료
					values[5] = jc[2].getSelectedItem().toString();//담당자
					try {
						pst = c.prepareStatement(sql);
						for(int i = 0; i < values.length; i++) {
							pst.setString(i+1, values[i]);
						}
						int re = pst.executeUpdate();//SQL문 실행
						if(re > 0) {//보험 가입
							JOptionPane.showMessageDialog(null, "가입 성공","",JOptionPane.CLOSED_OPTION);
						}
						else {
							JOptionPane.showMessageDialog(null, "가입 실패","",JOptionPane.ERROR_MESSAGE);
						}
						showTables();//가입 성공 후 해당 가입된 테이블 보여주기
					}
					catch(SQLException ee) {
						ee.printStackTrace();
					}
				}
			}
			else if(btn.getText().equals(str[9])) {//삭제 버튼 클릭
				if(t.getSelectedRowCount() == 0) {//삭제할 보험을 선택 안했을 때
					JOptionPane.showMessageDialog(null, "삭제할 보험을 선택하세요.", "삭제 실패", JOptionPane.ERROR_MESSAGE);
				}
				else {//삭제
					int selection = t.getSelectedRow();//선택된 열 반환
					Vector<String>vc = rowData.get(selection);//선택된 열의 요소들을 벡터에 저장
					sql = "delete from contract where customerCode = ? and contractName = ?";//고객코드와 담당자를 받아와 보험계약을 SQL문
					String dID = vc.get(0);//고객코드
					String dname = vc.get(1);//담당자
					int select = JOptionPane.showConfirmDialog(null, dID + "(" + dname
							+ ")" + " 삭제하시겠습니까?","고객정보 삭제",JOptionPane.YES_NO_OPTION);//삭제 질문 다이얼로그 생성
					if(select == JOptionPane.YES_OPTION) {//확인 버튼을 클릭할 경우
						try {
							pst = c.prepareStatement(sql);
							pst.setString(1, dID);
							pst.setString(2, dname);
							int re = pst.executeUpdate();//SQL문 실행
							if(re>0) {
								JOptionPane.showMessageDialog(null,"삭제 완료");
							}
							else {
								JOptionPane.showMessageDialog(null, "삭제 실패");
							}
							showTables();//삭제 후 테이블 갱신
						}
						catch(SQLException ee) {
							ee.printStackTrace();
						}
					}
				}
			}
			else if(btn.getText().equals(str[10])){//파일로 저장 버튼 클릭
				if(t.getRowCount() == 0) {
					JOptionPane.showMessageDialog(null, "해당 회원의 계약된 보험이 없습니다.", "저장 불가", JOptionPane.ERROR_MESSAGE);
				}
				else {
					FileDialog dialog = new FileDialog(new JFrame(), "저장", FileDialog.SAVE);//파일 저장 다이얼로그 실행
			        dialog.setVisible(true);
			        String path = dialog.getDirectory() + dialog.getFile();//저장 경로 설정
			        try {
						FileWriter fout = new FileWriter(path);//지정된 경로로 파일 씌우기
						String information = "고객명 : " + jc[0].getSelectedItem().toString() + "(" + jt[0].getText() + ")";//고객명
						information += "\n\n";
						information += "담당자명 : " + jc[2].getSelectedItem().toString();//담당자명
						information += "\n\n";
						information += "보험상품\t가입금액\t가입일\t월보험료\n";//보험상품, 가입금액, 가입일, 월보험료
						sql = "select contractName, regPrice, regDate, monthPrice from contract where customerCode = '"
						+ jt[0].getText() + "'";//보험계약 테이블에서 회원코드를 통해 보험계약명, 보험가격, 가입일자, 월보험료를 검색할 SQL문
						pst = c.prepareStatement(sql);
						String values[] = new String[4];//보험계약명, 보험가격, 가입일자, 월보험료를 저장할 String형 배열
						rs = pst.executeQuery();//SQL문 실행
						while(rs.next()) {
							for(int i = 0; i < values.length; i++) {
								values[i] = rs.getString(i+1);//결과값 반환
								information += values[i] + "\t";//결과값 반영하기
								//System.out.println(values[i] + "\t");
							}
							information += "\n";//한줄 결과값이 다 반환되면 줄바꿈
						}
						fout.write(information);//반환된 텍스트로 파일 덮어씌우기
						fout.close();
					}
			        catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			        catch (SQLException ee) {
			        	ee.printStackTrace();		        
			        }
				}
			}
			else if(btn.getText().equals(str[11])) {//닫기 버튼 클릭
				JOptionPane.showMessageDialog(null,"종료합니다.");
				dispose();
				new InsuranceMain();//해당 창 종료 및 메인 창 띄우기
			}
		}
	}
	class TablePanel extends JPanel{//테이블
		TablePanel(){
			add(new JLabel("< 고객 보험 계약현황 >"));
			rowData = new Vector<Vector<String>>();//테이블 정보를 담을 2차원 벡터
			Vector<String>colData = new Vector<String>();//테이블 제목을 담을 벡터
			for(int i = 0; i < data.length; i++) {
				colData.add(data[i]);//테이블 제목 등록
			}
			model = new DefaultTableModel(rowData, colData);//테이블 등록
			t = new JTable(model);
			JScrollPane jps = new JScrollPane(t);//스크롤펜에 테이블 등록하기
			add(jps);
		}
	}
}
