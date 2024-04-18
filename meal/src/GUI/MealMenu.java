package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import DB.DriverConnector;

import java.util.*;
import java.sql.*;
//여기 클래스는 이제 안씁니다.
public class MealMenu extends JFrame{
	JComboBox cb = new JComboBox();//메뉴 정보를 입력받을 콤보박스
	JButton bt[] = new JButton[5];//검색, 수정, 삭제, 오늘의메뉴, 닫기 기능을 실행할 버튼
	JTable jt;//메뉴를 수정할 테이블
	DefaultTableModel model;
	Vector<Vector<Object>> row = new Vector<Vector<Object>>();;
	Vector<String> col = new Vector<String>();
	Vector<Object> v;
	String btname[] = {"검색", "수정", "삭제", "오늘의메뉴 선정", "닫기"};
	String cbname[] = {"한식", "중식", "일식", "양식"};
	Connection con;
	Statement st;
	
	public MealMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("메뉴 관리");
		setSize(600, 700);
		Dimension dm = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((int)(dm.getWidth()/2-getWidth()/2), (int)(dm.getHeight()/2-getHeight()/2));
		con = DriverConnector.MakeConnection("mealproject");
		add(new NP(), BorderLayout.NORTH);
		add(new CP(), BorderLayout.CENTER);
		this.addWindowListener(new back());
		setVisible(true);
	}
	class back extends WindowAdapter {//X 버튼을 클릭했을때, 창이 닫히지 않고 뒤로 이동
		public void windowClosing(WindowEvent e) {
			dispose();
			new Admin();
		}
	}
	class NP extends JPanel{
		public NP() {
			add(new JLabel("종류"));
			
			for(int i=0;i<cbname.length;i++)
				cb.addItem(cbname[i]);//콤보박스에 식사 종류 추가
			
			add(cb);
			
			for(int i=0;i<bt.length;i++) {
				bt[i] = new JButton(btname[i]);//검색, 수정, 삭제, 오늘의메뉴, 닫기 기능을 실행할 버튼 추가
				bt[i].addActionListener(new btAL());
				add(bt[i]);
			}
		}
	}
	
	class CP extends JPanel{
		public CP() {//테이블을 등록할 JPanel
			col.add("□");//전체선택
			col.add("menuName");//메뉴명
			col.add("price");//가격
			col.add("maxCount");//조리가능수량
			col.add("todayMeal");//오늘의메뉴
			model = new DefaultTableModel(row, col) {
				public Class<?> getColumnClass(int column){
					switch(column) {
						case 0:	return Boolean.class;
						default: return String.class;
					}
				}
			};
			jt = new JTable(model);//테이블 등록
			JScrollPane jps = new JScrollPane(jt);
			add(jps);
			isSet();
		}
	}
	
	class btAL implements ActionListener{//버튼 클릭 시 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			switch(e.getActionCommand()) {
				case "검색": isSet(); break;//검색 기능
				case "수정": isModify(); break;//수정 기능
				case "삭제": isDelete(); break;//삭제 기능
				case "오늘의메뉴 선정": isToday(); break;//오늘의 메뉴 선정 기능
				case "닫기": dispose(); break;//닫기 기능
			}
		}
	}
	
	public void isSet() {//검색 기능
		int nb = 0;
		switch((String)cb.getSelectedItem()) {
			case "한식": nb = 1; break;
			case "중식": nb = 2; break;
			case "일식": nb = 3; break;
			case "양식": nb = 4; break;
		}//식사 유형별 검색받을 변수
		
		row.clear();
		
		try {
			st = con.createStatement();
			String selectmeal = "select mealName, price, maxCount, todayMeal from meal where cuisineNo = '" + nb + "'";
			//식사 유형별 검색받아 식사명, 가격, 조리가능수량, 오늘의 메뉴 선정 여부를 테이블에 출력할 변수
			ResultSet rs = st.executeQuery(selectmeal);//SQL 실행
			while(rs.next()) {
				v = new Vector<Object>();
				v.add(false);//체크박스로 이 열은 등록하지 않음
				v.add(rs.getString(1));//식사명이 추가될 열
				v.add(rs.getInt(2));//가격이 출력될 열
				v.add(rs.getInt(3));//조리가능수량이 추가될 열
				if(rs.getInt(4) == 1) {//오늘의 메뉴가 출력될 열
					v.add("Y");//오늘의 메뉴가 맞으면 Y
				}else
					v.add("N");//아니면 N
				row.add(v);
			}
			jt.updateUI();//테이블 갱신
		}catch(SQLException e) {
			System.out.println("SQL오류");
		}
		
		// 셀 가운데 정렬
		DefaultTableCellRenderer x = new DefaultTableCellRenderer();
		x.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcmSchedule = jt.getColumnModel();
		for (int i = 1; i < tcmSchedule.getColumnCount(); i++)
			tcmSchedule.getColumn(i).setCellRenderer(x);
	}
	
	public void isModify() {//수정 버튼을 클릭했을 때
		int cnt = 0;//선택 갯수를 입력받는 변수
		int srow = 0;//선택한 열을 입력받는 변수
		String b[] = new String[4];
		for(int i=0;i<model.getRowCount();i++) {
			if((boolean)model.getValueAt(i, 0)) {//선택한 열의 정보 가져옴
				srow = i;
				cnt++;
			}
		}
		
		if(cnt == 1) {//1개가 선택됐을 시
			b[0] = (String)cb.getSelectedItem();
			b[1] = (String)jt.getValueAt(srow, 1);
			b[2] = Integer.toString((Integer)jt.getValueAt(srow, 2));
			b[3] = Integer.toString((Integer)jt.getValueAt(srow, 3));
			//콤보박에 선택 식사 유형 정보와, 메뉴이름, 가격, 조리가능수량을 받아옴.
//			new MealModify(b);
		}else if(cnt >= 2) {//중복 선택 했을 시
			JOptionPane.showMessageDialog(null, "하나씩 수정가능합니다.", "Message", JOptionPane.ERROR_MESSAGE);
		}else {//수정할 메뉴를 선택 안했다면
			JOptionPane.showMessageDialog(null, "수정할 메뉴를 선택해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void isDelete() {
		String deletemeal = "delete from meal where mealName = '";
		try {
			st = con.createStatement();
			for(int i=0;i<model.getRowCount();i++) {
				if((boolean)model.getValueAt(i, 0)) {
					String g = deletemeal + model.getValueAt(i, 1) + "'";
					st.executeUpdate(g);
				}
			}
		}catch(SQLException e) {
			System.out.println("SQL오류");
		}
		isSet();
	}
	
	public void isToday() {
		int cnt = 0;
		Vector<String> v = new Vector<String>();
		for(int i=0;i<model.getRowCount();i++) {
			if((boolean)model.getValueAt(i, 0)) {
				v.add((String)model.getValueAt(i, 1));
				cnt++;
			}
		}
		
		if(cnt > 25)
			JOptionPane.showMessageDialog(null, "25개를 초과할 수 없습니다.", "Message", JOptionPane.ERROR_MESSAGE);
		else {
			String updatemeal = "update meal set todayMeal = 0";
			String updatemeal2 = "update meal set todayMeal = 1 where mealName = '";
			try {
				st = con.createStatement();
				st.executeUpdate(updatemeal);
				for(int i=0;i<v.size();i++) {
					String g = updatemeal2 + v.get(i) + "'";
					st.executeUpdate(g);
				}
			}catch(SQLException e) {
				System.out.println("SQL오류");
			}
		}
		isSet();
	}
	
	/*public static void main(String[] args) {
		new MealMenu();
	}*/
}
