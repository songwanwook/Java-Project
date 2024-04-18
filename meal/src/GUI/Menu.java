package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import DB.DriverConnector;

import java.sql.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
public class Menu extends JFrame{
	String str[] = {"한식","중식","일식","양식"};//식사 종류
	String sql;
	Statement st;
	Connection c = DriverConnector.MakeConnection("mealproject");//식권 데이터베이스 연결
	int n; //식사 번호
	int values;//버튼 갯수
	JTable t;
	JScrollPane jps;//테이블을 추가할 스크롤페인
	JTextField jt[] = new JTextField[2];
	Vector<Vector<String>> rowData;//테이블 정보를 가져올 벡터 데이터
	int count;
	int price;
	Vector<MealButton>btnVector = new Vector<MealButton>();//식사 메뉴를 출력할 버튼
	JLabel la[] = new JLabel[2];
	DefaultTableModel model;
	JLabel laa;
	int maxCount;
	Menu(int n){
		this.n = n;
		laa = new JLabel(str[n] + " 메뉴");
		laa.setFont(new Font("Ariel",Font.BOLD,25));
		add(laa,BorderLayout.NORTH);
		laa.setHorizontalAlignment(JLabel.CENTER);
		setTitle("메뉴");
		setSize(1200,800);
		add(new MenuButton(),BorderLayout.CENTER);
		this.addWindowListener(new back());
		setVisible(true);
	}
	class back extends WindowAdapter {//X버튼 클릭 시 유형별 식사 선택 페이지로 이동
		public void windowClosing(WindowEvent e) {
			dispose();
			new MealTicket();
		}
	}
	class MenuButton extends JPanel{//유형별 식사 메뉴가 등록될 버튼
		MenuButton(){
			//setLayout(new GridLayout(1,2));
			setLayout(null);
			add(new MenuList());
			add(new OrderMenu());
		}
		class MenuList extends JPanel{//메뉴버튼 출력하기
			MenuList(){
				setSize(750,700);
				String sql2 = "select * from meal where cuisineNo = " + (n+1);//유형별 식사 번호로 식사 메뉴를 출력하는 SQL문
				try {
					st = c.createStatement();
					ResultSet rs2 = st.executeQuery(sql2);//ResultSet으로 결과값 받아오기
					while(rs2.next()) {
						String mealname = rs2.getString("mealName");//식사 이름
						int mealPrice = Integer.parseInt(rs2.getString("price"));//식사 가격
						int todayMeal = Integer.parseInt(rs2.getString("todayMeal"));//오늘의 메뉴
						maxCount = Integer.parseInt(rs2.getString("maxCount"));//오늘 조리 가능 갯수
						btnVector.add(new MealButton(mealname, mealPrice, todayMeal, maxCount));//버튼 벡터에 식사 정보를 입력한다.
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				values = btnVector.size();
				if(values%5>0) {
					setLayout(new GridLayout((values/5)+1,5));
				}
				else {
					setLayout(new GridLayout((values/5),5));
				}
				for(int i = 0; i < values; i++) {
					add(btnVector.get(i));
				}//버튼 갯수 만큼 화면에 출력하기
			}
		}
		class OrderMenu extends JPanel{
			OrderMenu(){
				setSize(400,700);
				setLocation(760,0);
				setLayout(new GridLayout(2,1));
				add(new TablePanel());
				add(new ButtonPanel());
			}
			class TablePanel extends JPanel{
				TablePanel(){
					setLayout(new BorderLayout());
					add(new CostPanel(),BorderLayout.NORTH);
					String data[] = {"상품번호","품명","금액","수량"};//식사 정보를 출력할 데이터
					rowData = new Vector<Vector<String>>();
					Vector<String>colData = new Vector<String>();
					for(int i = 0; i < data.length; i++) {
						colData.add(data[i]);
					}//테이블에 선택한 식사 정보를 출력
					model = new DefaultTableModel(rowData, colData);
					t = new JTable(model);
					jps = new JScrollPane(t);//테이블 추가
					add(jps);
					t.addMouseListener(new MouseAdapter() {//테이블 정보를 클릭할 때
						@Override
						public void mousePressed(MouseEvent e) {
							if(e.getClickCount() > 1) {
								int selection = t.getSelectedRow();
								Vector<String>V = rowData.get(selection);
								price -= Integer.parseInt(V.get(3))*Integer.parseInt(V.get(2));//테이블 정보에 입력된 가격 차감
								
								for(int i = 0; i < btnVector.size(); i++) {
									if(btnVector.get(i).getName().equals(V.get(1))) {//해당 테이블 정보의 음식 번호와 비활성화 된 버튼 음식 번호가 같으면
										btnVector.get(i).setEnabled(true);//다시 활성화
									}
								}
								model.removeRow(selection);//해당 테이블 열을 삭제하여 주문 취소 처리
								if(price%1000==0) {
									if(price/1000>0) {//n천원일때
										la[1].setText(price/1000 + ",000" + "원");
									}
									else {//0원일 떼
										la[1].setText("0원");
									}
								}
								else {//n천원이 아닌 n천 몇백원일때
									la[1].setText(price/1000 + "," + price%1000 + "원");
								}
								t.updateUI();
							}
						}
					});
				}
				class CostPanel extends JPanel{//총 결제 금액 창
					CostPanel(){
						setLayout(new BorderLayout());
						price = 0;
						String cost[] = {"총 결제금액 : ", price+"원"};//총 결제금액 정보 출력
						for(int i = 0; i < la.length; i++) {
							la[i] = new JLabel(cost[i]);
							la[i].setFont(new Font("Ariel",Font.BOLD,25));
						}
						add(la[0],BorderLayout.WEST);
						add(la[1],BorderLayout.EAST);
					}
				}
			}
			class ButtonPanel extends JPanel{//식사 메뉴 버튼
				String str[] = {"선택품명 : ", "수량 : "};//해당 버튼에 식품명과 수량 출력
				
				JLabel la2[] = new JLabel[2];
				ButtonPanel(){
					setLayout(new BorderLayout());
					add(new TextPanel(),BorderLayout.NORTH);
					add(new OrderNumberPanel(),BorderLayout.CENTER);
					add(new OKCancelPanel(),BorderLayout.SOUTH);
				}
				class TextPanel extends JPanel{//선택품명과 수량을 출력하는 패널
					TextPanel(){
						for(int i = 0; i < jt.length; i++) {
							la2[i] = new JLabel(str[i]);
							la2[i].setFont(new Font("Ariel",Font.BOLD,15));
							add(la2[i]);
							if(i == 0) {
								jt[i] = new JTextField(15);//식사 품명이 출력될 텍스트필드
							}
							else {
								jt[i] = new JTextField(5);//식사 수량이 출력될 텍스트필드
							}
							add(jt[i]);
							jt[i].setEnabled(false);//텍스트필드는 임의로 수정 불가능하게 설정
						}
					}
				}
				class OrderNumberPanel extends JPanel{
					OrderNumberPanel(){
						setLayout(new BorderLayout());
						add(new Numbers(),BorderLayout.CENTER);
						add(new PlainButton(),BorderLayout.EAST);
					}
					class Numbers extends JPanel{
						Numbers(){
							setLayout(new BorderLayout());
							add(new NumbersButton(),BorderLayout.CENTER);
							add(new Zero(),BorderLayout.SOUTH);
						}
						class NumbersButton extends JPanel{
							NumbersButton(){//1~9까지 입력할 버튼
								setLayout(new GridLayout(3,3));
								JButton Number[] = new JButton[9];
								for(int i = 0; i < Number.length; i++) {
									Number[i] = new JButton(Integer.toString(i+1));
									add(Number[i]);
									Number[i].addActionListener(new Action());
								}
							}
						}
						class Zero extends JPanel{
							Zero(){//0버튼
								setLayout(new BorderLayout());
								JButton b0 = new JButton("0");
								b0.setSize(this.getWidth(), 30);
								add(b0,BorderLayout.CENTER);
								b0.addActionListener(new Action());
							}
						}
						class Action implements ActionListener {
							@Override
							public void actionPerformed(ActionEvent e) {//숫자 버튼을 클릭할 때 이벤트
								// TODO Auto-generated method stub
								JButton b = (JButton)e.getSource();
								for(int i = 0; i < 10; i++) {
									if(b.getText().equals(Integer.toString(i))) {
										jt[1].setText(jt[1].getText()+Integer.toString(i));//수량 텍스트필드에 숫자가 입력된다.
									}
								}
							}
						}
					}
					class PlainButton extends JPanel{
						String plain[] = {"입력","초기화"};//주문 입력 및 초기화를 위한 텍스트
						PlainButton(){
							setLayout(new BorderLayout());
							
							JButton btn[] = new JButton[2];//입력 초기화 버튼
							for(int i = 0; i < btn.length; i++) {
								btn[i] = new JButton(plain[i]);
								btn[i].addActionListener(new Action());//입력 초기화 버튼을 클릭할 이벤트 등록
							}
							add(btn[0],BorderLayout.CENTER);//입력 버튼
							add(btn[1],BorderLayout.SOUTH);//초기화 버튼
						}
						class Action implements ActionListener {//입력 초기화 버튼 클릭 이벤트
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								JButton b = (JButton)e.getSource();
								if(b.getText().equals(plain[0])) {//입력 버튼을 클릭했을 때
									if(jt[0].getText().equals("")) {//선택풍명이 없을 경우 메시지
										JOptionPane.showMessageDialog(null, "품명을 지정해 주세요", "Message",JOptionPane.ERROR_MESSAGE);
									}
									else if(jt[1].getText().equals("")) {//선택수량이 없을 경우 메시지
										JOptionPane.showMessageDialog(null, "수량을 지정해 주세요", "Message",JOptionPane.ERROR_MESSAGE);
									}
									else {//품명이 지정되었을 때
										count = Integer.parseInt(jt[1].getText());
										if(count > 10) {//주문량이 10개를 초과했을 시 메시지
											JOptionPane.showMessageDialog(null, "최대 10개까지 조리할 수 있습니다.", "주문 실패",JOptionPane.ERROR_MESSAGE);
											jt[1].setText("");
										}
										else if(count > maxCount) {//조리가능 수량을 초과했을 시 메시지
											JOptionPane.showMessageDialog(null, "조리 가능 갯수를 초과하였습니다.", "주문 실패",JOptionPane.ERROR_MESSAGE);
											jt[1].setText("");
										}
										else {//품명, 적정한 선택수량이 모두 지정되었을 때
											Vector<String>V = new Vector<String>();//테이블에 등록할 벡터
											sql = "select * from meal where mealName = '" + jt[0].getText() + "'";//식사 번호로 식사 정보를 출력할 SQL문
											try {
												st = c.createStatement();
												ResultSet rs = st.executeQuery(sql);
												while(rs.next()) {
													String Vvalues[] = {"mealNo","mealName","price"};//식사 정보(식사 번호, 식사 이름, 가격)
													for(int i = 0; i < 4; i++) {
														if(i<3) {
															V.add(rs.getString(Vvalues[i]));//테이블 열에 식사 정보, 식사 이름, 가격 저장
														}
														else {
															V.add(jt[1].getText());//식사 수량 저장
														}
														
													}
													rowData.add(V);//테이블 열 정보 추가
													for(int i = 0; i < btnVector.size(); i++) {
														if(btnVector.get(i).getName().equals(rs.getString("mealName"))) {
															//테이블에 저장된 식사 이름과 버튼에 지정된 식사 이름이 같으면
															btnVector.get(i).setEnabled(false);//해당 버튼 클릭을 비활성화
														}
													}
												}
												t.updateUI();//테이블 갱신
											}
											catch(SQLException ee) {
												ee.printStackTrace();
											}
											price += Integer.parseInt(V.get(3))*Integer.parseInt(V.get(2));//총 결제 금액을 가격*수량으로 갱신
											if(price%1000==0) {//1000원으로 나누어 떨어질 경우 결제금액창에 1,000으로 표기하기
												la[1].setText(price/1000 + ",000" + "원");
											}
											else {//1000원으로 나누어 떨어지지 않을 경우 결제금액창에 표기하기
												la[1].setText(price/1000 + "," + price%1000 + "원");
											}
											for(int i = 0; i < jt.length; i++) {//식사명과 수량을 입력받는 텍스트필드 초기화
												jt[i].setText("");
											}
										}
									}
								}
								else if(b.getText().equals(plain[1])) {//초기화 버튼 클릭 시
									model.setRowCount(0);//테이블 정보 삭제 후 초기화
									t.updateUI();
									price = 0;//가격 0으로 초기화
									la[1].setText(price + "원");
									for(int i = 0; i < btnVector.size(); i++) {
										if(btnVector.get(i).getMeal() == 0 || btnVector.get(i).getCount() == 0) {//오늘의 식사가 아니거나 조리수량이 0인 경우
											btnVector.get(i).setEnabled(false);//버튼 초기화 유지
										}
										else {//오늘의 식사이거나 조리수량이 1개 이상일 경우
											btnVector.get(i).setEnabled(true);//버튼 다시 활성화
										}
									}
									for(int i = 0; i < jt.length; i++) {
										jt[i].setText("");//식사 이름, 조리수량을 받는 텍스트필드도 초기화
									}
								}
							}
						}
					}
				}
				class OKCancelPanel extends JPanel{//결제, 취소 버튼을 클릭하는 JPanel
					String OKsql = "select * from member where passwd = ?";//OK버튼 클릭 시 직원의 비밀번호를 찾는 SQL
					JLabel diala[] = new JLabel[2];
					String diastr[] = {"사원번호","패스워드"};
					JComboBox jc;//사원번호가 등록될 콤보박스
					JTextField jtf;
					OKCancelPanel(){
						setLayout(new GridLayout(1,2));
						String OKCancel[] = {"결제","취소"};
						JButton btn[] = new JButton[2];
						for(int i = 0; i < btn.length; i++) {
							btn[i] = new JButton(OKCancel[i]);//결제, 취소 버튼
							add(btn[i]);
						}
						btn[0].addActionListener(new ActionListener() {//결제 버튼에 등록할 버튼 클릭 이벤트
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								if(price == 0) {//전체가격이 0일 경우 결제 불가능 메시지 출력
									JOptionPane.showMessageDialog(null,"한 개 이상 주문하세요.","결제 실패",JOptionPane.ERROR_MESSAGE);
								}
								else {//1개이상 결제했을 때
								dialogFrame dialog = new dialogFrame();//결제자 인증 다이얼로그 띄움
								int result = JOptionPane.showConfirmDialog(null, dialog,"결제자 인증",JOptionPane.YES_NO_OPTION);
								if(result == JOptionPane.YES_OPTION) {//확인 버튼을 누르면
									if(jtf.getText().equals("")) {//비밀번호를 입력하지 않으면
										JOptionPane.showMessageDialog(null, "패스워드를 입력하세요","로그인 실패",JOptionPane.ERROR_MESSAGE);
									}
									String values = jc.getSelectedItem().toString();//콤보박스의 사원번호를 선택했을 때 저장될 String 변수
									int k = Integer.parseInt(values);//다시 int로 치환
									try {
										String pass = jtf.getText();//입력받은 패스워드를 가져올 String 변수
										PreparedStatement pst = c.prepareStatement(OKsql);
										pst.setString(1, pass);//입력된 패스워드를 받아와
										ResultSet rs = pst.executeQuery();//SQL 실행
										if(rs.next()) {
											if(pass.equals(rs.getString("passwd"))) {//패스워드가 일치하면
												JOptionPane.showMessageDialog(null, "결제가 완료되었습니다. \n 식권을 출력합니다.");//결제 완료
												//orderlist에 데이터 등록
												try {
String insertorderlist = "insert into orderlist(cuisineNo, mealNo, memberNo, orderCount, amount, orderDate) values(?, ?, ?, ?, ?, ?)";
													Date date = new Date();
													SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//현재시간 데이터 출력
													String formatedNow = formatter.format(date);
													PreparedStatement orderlistpst = c.prepareStatement(insertorderlist);
													int nowcount;
													for(int i = 0; i < t.getRowCount(); i++) {
														orderlistpst.setInt(1, n+1);//종류별 식사 번호, MealTicket에서 받아온 값이 1 적어 1 더해줌
														orderlistpst.setInt(2, Integer.parseInt(t.getValueAt(i, 0).toString()));//음식 번호
														orderlistpst.setInt(3, Integer.parseInt(jc.getSelectedItem().toString()));//사원 번호
														orderlistpst.setInt(4, Integer.parseInt(t.getValueAt(i, 3).toString()));//수량
														orderlistpst.setInt(5, Integer.parseInt(t.getValueAt(i, 2).toString()));//금액
														orderlistpst.setString(6, formatedNow);//현재시간
														orderlistpst.executeUpdate();//SQL 실행
													}
													//결제 후 조리 가능 갯수 차감
													String updateCount = "update meal set maxCount = ? where mealNo = ?";
													PreparedStatement updatePst = c.prepareStatement(updateCount);
													
													for(int i = 0; i < t.getRowCount(); i++) {
														//System.out.println(maxCount);
														nowcount = maxCount - Integer.parseInt(t.getValueAt(i, 3).toString());
														//System.out.println(nowcount);
														updatePst.setInt(1, nowcount);
														//System.out.println(Integer.parseInt(t.getValueAt(i, 0).toString()));
														updatePst.setInt(2, Integer.parseInt(t.getValueAt(i, 0).toString()));
														updatePst.executeUpdate();
													}
													new Ticket(t, k, n);//티켓 출력
													dispose();//창 닫음
												}
												catch(SQLException e2) {
													JOptionPane.showMessageDialog(null, "SQL문 오류","결제 실패",JOptionPane.ERROR_MESSAGE);
													e2.printStackTrace();
												}
											}
										}
										else {//선택한 사원의 비밀번호와 입력된 비밀번호가 다른 경우
											JOptionPane.showMessageDialog(null, "패스워드가 다릅니다.","결제 실패",JOptionPane.ERROR_MESSAGE);
											//break;
										}
									}
									catch(SQLException ee) {
										ee.printStackTrace();
									}
								}
								}
							}
						});
						btn[1].addActionListener(new ActionListener() {//취소버튼 액션 추가
							@Override
							public void actionPerformed(ActionEvent e) {//취소버튼을 클릭하면
								// TODO Auto-generated method stub
								for(int i = 0; i < jt.length; i++) {
									jt[i].setText("");//선택품명, 수량 초기화
								}
							}
						});
						
					}
					class dialogFrame extends JPanel{//결제 다이얼로그 창
						dialogFrame(){
							setLayout(new GridLayout(2,2));
							sql = "select * from member";//직원 정보를 출력하는 SQL
							for(int i = 0; i < la.length; i++) {
								diala[i] = new JLabel(diastr[i]);
								add(diala[i]);
								if(i == 0) {
									jc = new JComboBox();
									add(jc);
									try {
										st = c.createStatement();
										ResultSet rs = st.executeQuery(sql);//직원 정보 출력 SQL 실행
										while(rs.next()) {
											String memberno = rs.getString("memberNo");//직원 번호 받아와서
											jc.addItem(memberno);//콤보박스 안에 저장
										}
									}
									catch(SQLException e) {
										e.printStackTrace();
									}
								}
								else {
									jtf = new JTextField(5);//비밀번호를 입력하는 텍스트 필드
									add(jtf);
								}
							}
						}
					}
				}
			}
		}
	}
	
	class MealButton extends JButton{//식사메뉴 버튼
		String mealname;
		int todayMeal;
		int maxCount;
		MealButton(String mealname, int mealPrice, int todayMeal, int maxCount){
			this.mealname = mealname;
			this.todayMeal = todayMeal;
			this.maxCount = maxCount;
			String str = "<HTML><center>" +  mealname+"<br><br><br>" + mealPrice + "원</HTML>";//버튼에 식사 이름, 가격 정보 출력
			setText(str);
			if(todayMeal == 0 || maxCount == 0) {//오늘의 식단이 아니거나, 조리가능갯수가 없으면 버튼 클릭 비활성화
				this.setEnabled(false);
			}
			addActionListener(new ActionListener() {//버튼을 누르면
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					jt[0].setText(mealname);//선택품명 텍스트필드에 해당 버튼에 지정된 식사명 등록
				}
			});
		}
		public String getName(){//식사명
			return mealname;
		}
		public int getMeal() {//오늘의 식사
			return todayMeal;
		}
		public int getCount() {//조리가능 최대 수량
			return maxCount;
		}
	}
	/*public static void main(String[] args) {
		new Menu(0);
	}*/
}
