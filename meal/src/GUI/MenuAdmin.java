package GUI;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import DB.DriverConnector;

import java.util.*;
public class MenuAdmin extends JFrame{
	String str[] = {"종류","검색","수정","삭제","오늘의 메뉴 선정","닫기"};//검색, 수정, 삭제, 오늘의메뉴, 닫기 기능을 실행할 버튼
	String data[] = {"□","mealName","Price","maxCount","todayMeal"};//선택, 식사명, 가격, 조리가능수량, 오늘의 메뉴를 보여줄 String형 데이터
	int clickindex = 0;
	JButton btn [] = new JButton[5];
	JComboBox jc;//메뉴 정보를 입력받을 콤보박스
	JTable t;//메뉴를 수정할 테이블
	String sql;
	int index = 1;
	Connection c = DriverConnector.MakeConnection("mealproject");
	Statement st;
	ResultSet rs;
	Vector<Vector<Object>> rowData;
	DefaultTableModel model;
	
	MenuAdmin(){
		setTitle("메뉴 관리");
		add(new ButtonPanel(),BorderLayout.NORTH);
		add(new TablePanel(),BorderLayout.CENTER);
		this.addWindowListener(new back());
		setSize(600,600);
		setVisible(true);
	}
	class back extends WindowAdapter {//X 버튼을 클릭했을때, 창이 닫히지 않고 뒤로 이동
		public void windowClosing(WindowEvent e) {
			dispose();
			new Admin();
		}
	}
	String menu[] = {"한식","중식","일식","양식"};
	class ButtonPanel extends JPanel{
		ButtonPanel(){
			
			for(int i = 0; i < str.length; i++) {
				if(i < 1) {
					add(new JLabel(str[i]));
					jc = new JComboBox();
					add(jc);
				}
				else {
					btn[i-1] = new JButton(str[i]);
					add(btn[i-1]);//검색, 수정, 삭제, 오늘의메뉴, 닫기 기능을 실행할 버튼 추가
					btn[i-1].addActionListener(new Action());
				}
			}
			for(int i = 0; i < menu.length; i++) {
				jc.addItem(menu[i]);//콤보박스에 식사 종류 추가
				jc.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						// TODO Auto-generated method stub
						index = jc.getSelectedIndex()+1;//콤보박스를 선택하면 식사 종류별 번호를 받아옴
						value = (String)jc.getSelectedItem();
					}
				});
			}
		}
	}
	class Action implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String cmd = e.getActionCommand();
			switch(cmd) {
			case "검색": search(); break;//검색 기능
			case "수정": update(); search(); break;//수정 기능
			case "삭제": delete(); search(); break;//삭제 기능
			case "오늘의 메뉴 선정": today(); search(); break;//오늘의 메뉴 선정 기능
			case "닫기":dispose();new Admin();break;//닫기 기능
			}
		}
	}
	private void search() {
		sql = "select * from meal where cuisineNo = " + index;//식사 유형별 검색받아 식사명, 가격, 조리가능수량, 오늘의 메뉴 선정 여부를 테이블에 출력할 변수
		try {
			rowData.clear();
			st = c.createStatement();
			rs = st.executeQuery(sql);//SQL문 실행
			while(rs.next()) {
				Vector<Object>V = new Vector<Object>();
				V.add(false);
				String value[] = {rs.getString("mealName"),rs.getString("price"),rs.getString("maxCount"),rs.getString("todayMeal")};
				//SQL문을 실행하면 식사명, 가격, 조리가능수량, 오늘의 메뉴 선정 여부를 배열로 불러온다.
				for(int i = 0; i < data.length; i++) {//메뉴 정보를 입력받는 콤보
					if(i < 3) {//선택, 식사명, 가격, 조리가능수량이 출력될 열
						V.add(value[i]);
					}
					else {//오늘의 메뉴 선정 여부가 출력될 열
						if(value[3].equals("1")) {//오늘의 메뉴가 맞으면 Y
							V.add("Y");
						}
						else {//오늘의 메뉴가 아니면 N
							V.add("N");
						}
					}
				}
				rowData.add(V);//테이블에 해당 정보 등록
			}
			t.updateUI();//테이블 출력
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	int rowvalue = 0;//선택한 열을 입력받는 변수
	private void update() {//수정 버튼을 클릭했을 때
		int count = 0;//선택 갯수를 입력받는 변수
		
		for(int i = 0; i < model.getRowCount(); i++) {
			if((boolean) model.getValueAt(i, 0)) {//선택한 열의 정보 가져옴
				rowvalue = i;
				count++;
			}
		}
		if(count == 1) {//1개가 선택됐을 시
			new Update();//수정 페이지 실행
		}
		else if(count > 1) {//중복 선택 했을 시
			JOptionPane.showMessageDialog(null, "하나씩 수정가능합니다.", "Message", JOptionPane.ERROR_MESSAGE);
		}
		else {//수정할 메뉴를 선택 안했다면
			JOptionPane.showMessageDialog(null, "수정할 메뉴를 선택해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
		}
	}
	String value;
	class Update extends JFrame {//수정 페이지(등록 페이지(InsertMenu)와 비슷함)
		String str[] = {"종류","*메뉴명","가격","조리가능수량","등록","닫기"};
		String menus[] = {"한식","중식","일식","양식"};//식사 메뉴 유형 목록
		JLabel la[] = new JLabel[4];
		JComboBox jc[] = new JComboBox[3];//종류, 가격, 조리가능수량을 등록할 콤보박스
		JButton btn[] = new JButton[2];//확인, 취소 버튼
		JTextField jt = new JTextField();//메뉴명을 입력할 텍스트 필드
		Update(){
			setLayout(new GridLayout(5,2));
			for(int i = 0; i < la.length; i++) {
				la[i] = new JLabel(str[i]);
				add(la[i]);
				if(i < 2) {
					if(i == 0) {
						jc[i] = new JComboBox();//식사 종류 콤보박스 등록
						add(jc[i]);
					}
					else {
						jt = new JTextField();//메뉴명 텍스트필드 등록
						add(jt);
					}
				}
				else {
					jc[i-1] = new JComboBox();//가격, 조리가능수량 콤보박스 등록
					add(jc[i-1]);
				}
			}
			for(int i = 0; i < menus.length; i++) {//식사 유형을 가져올 콤보박스
				jc[0].addItem(menus[i]);//
				if(menus[i].equals(value)) {
					jc[0].setSelectedItem(menus[i]);//해당 메뉴의 식사 유형 가져오기
				}
			}
			jc[0].setEnabled(false);//식사 유형은 수정 불가능하게 비활성화
			for(int i = 2; i < 25; i++) {//수정할 가격을 가져올 콤보박스
				jc[1].addItem(i*500);//1000~12000원 사이의 가격을 추가하되
				if(i*500 == Integer.parseInt((String)model.getValueAt(rowvalue, 2))) {
					jc[1].setSelectedItem(i*500);//해당하는 메뉴의 가격을 Default로 가져옴
				}
			}
			for(int i = 0; i <= 50; i++) {
				jc[2].addItem(i);//수정할 조리가능수량을 가져올 콤보박스, 0~50개의 조리가능수량을 가져오되
				if(i == Integer.parseInt((String)model.getValueAt(rowvalue, 3))) {
					jc[2].setSelectedItem(i);//해당되는 메뉴의 조리가능수량을 가져옴
				}
			}
			jt.setText((String) model.getValueAt(rowvalue, 1));//메뉴명을 텍스트필드로 가져온다.
			for(int i = 0; i < btn.length; i++) {
				btn[i] = new JButton(str[i+4]);//확인, 닫기 버튼
				add(btn[i]);
				btn[i].addActionListener(new ActionListener() {//확인, 닫기 버튼 클릭 이벤트
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						JButton b = (JButton)e.getSource();
						String mealname = jt.getText();//식사명을 업데이트할 변수
						int intvalue[] = {(int) jc[1].getSelectedItem(),(int)jc[2].getSelectedItem()};//조리가능수량, 가격을 업데이트할 변수
						if(b.getText().equals(str[4])) {//확인 버튼 클릭시
							sql = "update meal set mealName = ?, price = ?, maxCount = ? where mealName = '" 
						+ (String) model.getValueAt(rowvalue, 1) + "'";//테이블 정보를 가져와 식사 메뉴를 Update할 SQL
							PreparedStatement pst;
							try {
								pst = c.prepareStatement(sql);
								for(int i = 0; i < 3; i++) {
									if(i == 0) {
										pst.setString(i+1, mealname);//업데이트할 식사명 등록
									}
									else {
										pst.setInt(i+1, intvalue[i-1]);//업데이트할 가격 및 조리가능수량 등록
									}
								}
								int re = pst.executeUpdate();//SQL문 실행
								if(re == 1) {//실행 결과
									JOptionPane.showMessageDialog(null, "수정 성공","메뉴가 업데이트 되었습니다.",JOptionPane.CLOSED_OPTION);
								}
								else {
									JOptionPane.showMessageDialog(null, "수정 실패","업데이트 실패.",JOptionPane.ERROR_MESSAGE);
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								JOptionPane.showMessageDialog(null, "수정 실패","SQL문 오류",JOptionPane.ERROR_MESSAGE);
							}
						}
						else {//닫기 버튼 클릭시
							dispose();//해당 창을 닫는다.
						}
					}
				});
			}
			
			setSize(300,240);
			setVisible(true);
		}
	}
	private void delete() {//메뉴 삭제
		sql = "delete from meal where mealName = '";//삭제할 메뉴를 실행할 SQL문
		int select = JOptionPane.showConfirmDialog(null,"해당 메뉴를 삭제하시겠습니까?","메뉴 삭제",JOptionPane.YES_NO_OPTION);//삭제할 다이얼로그 창
		if(select == JOptionPane.YES_OPTION) {//확인을 클릭하면
			try {
				st = c.createStatement();
				for(int i=0;i<model.getRowCount();i++) {
					if((boolean)model.getValueAt(i, 0)) {//체크박스가 선택된 열은
						sql += model.getValueAt(i, 1) + "'";//열에 해당되는 식사 이름을 가져온 다음
						int re = st.executeUpdate(sql);//SQL문 실행
						if(re == 1) {//삭제
							JOptionPane.showMessageDialog(null, "삭제 성공","성공적으로 삭제되었습니다.",JOptionPane.CLOSED_OPTION);
						}
						else {
							JOptionPane.showMessageDialog(null, "삭제 실패","삭제하는데 오류가 있습니다.",JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "삭제 실패","SQL문 오류",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	private void today() {//오늘의 메뉴 선정
		int count = 0;//선택 개수를 저장할 변수
		Vector<String> v = new Vector<String>();
		for(int i = 0; i < model.getRowCount(); i++) {
			if((boolean)model.getValueAt(i, 0)) {//테이블 내 체크박스가 선택된 만큼
				v.add((String)model.getValueAt(i, 1));//해당 테이블의 정보를 가져옴
				count++;//선택 갯수 증가
			}
		}
		if(count == 0) {//0개를 선택했을 때
			JOptionPane.showMessageDialog(null,  "1개 이상 선택하세요.","", JOptionPane.ERROR_MESSAGE);
		}
		else if(count > 25) {//25개이상 선정했을 경우 불가능한 기능
			JOptionPane.showMessageDialog(null,  "25개 이상 등록할 수 없습니다.","오늘의 메뉴 업데이트 실패", JOptionPane.ERROR_MESSAGE);
		}
		else {//1~24개 사이를 선택했을 때
				sql = "update meal set todayMeal = 0 where cuisineNo = " + index;//체크박스가 선택되지 않은 테이블의 오늘의 메뉴를 취소할 SQL문
			try {
				st = c.createStatement();
				st.executeUpdate(sql);//SQL문 실행
				for(int i = 0; i < v.size(); i++) {
					String sql2 = "update meal set todayMeal = 1 where mealName = '" + v.get(i) + "'";//체크박스가 선택된 테이블의 오늘의 메뉴 선정할 SQL
					st.executeUpdate(sql2);//SQL문 실행
				}
				JOptionPane.showMessageDialog(null, "오늘의 메뉴가 선정 되었습니다.","오늘의 메뉴 업데이트",JOptionPane.CLOSED_OPTION);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null,  "SQL문 에러","오늘의 메뉴 업데이트 실패", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	class TablePanel extends JPanel{//테이블을 등록할 JPanel
		TablePanel(){
			rowData = new Vector<Vector<Object>>();
			Vector<String>colData = new Vector<String>();//2차원 데이터로 테이블을 등록할 String형 벡터
			for(int i = 0; i < data.length; i++) {
				colData.add(data[i]);//선택, 식사명, 가격, 조리가능수량, 오늘의 메뉴를 테이블 최상단에 등록
			}
			model = new DefaultTableModel(rowData, colData) {
				@Override
				public Class<?> getColumnClass(int column) {
					switch(column) {
					case 0: return Boolean.class;//체크박스를 등록할 테이블 열
					default: return String.class;//그 외에는 식사명, 가격, 조리가능수량, 오늘의 메뉴를 등록함
					}
				}
			};
			t = new JTable(model);//테이블 등록
			//테이블 생성 후 가운데 정렬하는 작업
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); // 디폴트테이블셀렌더러를 생성
		    dtcr.setHorizontalAlignment(SwingConstants.CENTER); // 렌더러의 가로정렬을 CENTER로
		    TableColumnModel tcm = t.getColumnModel(); // 정렬할 테이블의 컬럼모델을 가져옴
			for(int i = 1 ; i < tcm.getColumnCount() ; i++){
		       tcm.getColumn(i).setCellRenderer(dtcr);  
		       //컬럼모델에서 컬럼의 갯수만큼 컬럼을 가져와 for문을 이용하여 각각의 셀렌더러를 아까 생성한 dtcr에 set해줌
			}
			//테이블 최종 추가하기
			JScrollPane jps = new JScrollPane(t);
			add(jps);
			t.getTableHeader().addMouseListener(new MouseAdapter() {//테이블을 클릭하면
				public void mousePressed(MouseEvent e) {
					if(e.getX()>=0 && e.getX() <= 86 && e.getY() >= 2 && e.getY() <= 16) {//전체 클릭
						if(clickindex == 0) {
							for(int i = 0; i < rowData.size(); i++) {
								model.setValueAt(true, i, 0);
							}
							clickindex = 1;//선택 안된 열들을 모두 선택됨
						}
						else {
							for(int i = 0; i < rowData.size(); i++) {
								model.setValueAt(false, i, 0);
							}
							clickindex = 0;//선택된 열들은 모두 선택 안됨
						}
					}
				}
			});
		}
	}
}
