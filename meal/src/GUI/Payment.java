package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.*;
import java.io.*;
import DB.DriverConnector;
public class Payment extends JFrame{
	String str[] = {"메뉴명","조회","새로고침","파일로 저장","닫기"};
	String data[] = {"종류","메뉴명","사원명","결제수량","총결제금액","결제일"};
	JButton btn[] = new JButton[4];//조회, 새로고침, 파일 저장, 닫기 기능을 실행할 버튼 배열
	JTextField jt;//메뉴명을 검색할 텍스트필드
	String sql;
	DefaultTableModel model;//테이블을 등록할 모델
	JTable t;
	Vector<Vector<String>> rowData;
	Connection c = DriverConnector.MakeConnection("mealproject");//연결할 데이터베이스 커넥션
	Payment(){
		setTitle("결제 조회");
		add(new Menu(),BorderLayout.NORTH);
		add(new TablePanel(),BorderLayout.CENTER);
		setSize(600,600);
		this.addWindowListener(new back());//X버튼을 클릭할 경우 바로 끝내지 않고, 이전 창으로 되돌아감
		setVisible(true);
	}
	class back extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
			new Admin();
		}
	}
	class Menu extends JPanel{
		Menu(){
			for(int i = 0; i < str.length; i++) {
				if(i == 0) {
					add(new JLabel(str[i]));
					jt = new JTextField(11);
					add(jt);//메뉴 검색하는 텍스트필드 추가
				}
				else {
					btn[i-1] = new JButton(str[i]);
					add(btn[i-1]);
					btn[i-1].addActionListener(new Action());//조회, 새로고침, 파일 저장, 닫기 기능을 실행할 버튼 추가
				}
			}
		}
	}
	class Action implements ActionListener{//버튼을 클릭하면
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String cmd = e.getActionCommand();
			switch(cmd) {
				case "조회": search(); break;//조회 기능 실행
				case "새로고침": clear(); break;//새로고침 기능 실행
				case "파일로 저장": file(); break;//파일 저장 기능 실행
				case "닫기": dispose(); new Admin(); break;//닫기 기능 실행
			}
		}
	}
	private void search() {//조회 기능
		rowData.clear();//테이블 초기화
		if(jt.getText().equals("")) {//메뉴명이 비었을 경우 모든 데이터 검색하는 SQL문
			 sql = "select cuisineName, mealName, memberName, orderCount, amount, orderDate from "
			 + "cuisine, meal, member, orderlist where orderlist.cuisineNo = cuisine.cuisineNo "
			 + "and meal.mealNo = orderlist.mealNo and orderlist.memberNo = member.memberNo";
		}
		else {//메뉴명을 검색했을 경우 특정 글자를 포함한 메뉴명 및 식사 유형, 결제자, 수량, 가격, 주문 날짜를 검색하는 SQL문
			sql = "select cuisineName, mealName, memberName, orderCount, amount, orderDate from "
			+ "cuisine, meal, member, orderlist where orderlist.cuisineNo = cuisine.cuisineNo "
			+ "and meal.mealNo = orderlist.mealNo and orderlist.memberNo = member.memberNo and mealName like '%" + jt.getText() + "%'";
		}
		try {
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);//SQL문 실행
			while(rs.next()) {
				Vector<String>V = new Vector<String>();//테이블에 저장할 String형 벡터
				String value[] = {rs.getString("cuisineName"),rs.getString("mealName"),rs.getString("memberName")
						,rs.getString("orderCount"),rs.getString("amount"),rs.getString("orderDate").substring(0,10)};
				//SQL문으로 검색한 식사 유형, 식사명, 결제자, 수량, 가격, 주문 날짜를 String 배열에 담아온다.
				for(int i = 0; i < data.length; i++) {
					V.add(value[i]);
				}//벡터에 String배열 추가
				rowData.add(V);//테이블 열에 벡터 추가
			}
			t.updateUI();//테이블 데이터 갱신
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "SQL문 에러","검색 실패",JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	private void clear() {//새로 고침을 클릭했을때
		jt.setText("");//검색 텍스트필드 초기화
		search();//조회 실행, 텍스트필드가 초기화 된 상태에서 SQL문을 실행하기 때문에 모든 데이터가 검색된다.
	}
	private void file() {//파일로 저장하기
		FileDialog dialog = new FileDialog(new JFrame(), "저장", FileDialog.SAVE);//저장 기능 다이얼로그를 불러온다.
		dialog.setVisible(true);        
        String path = dialog.getDirectory() + dialog.getFile();//해당 파일 위치 경로를 보내준다.
        try {
			FileWriter fout = new FileWriter(path);//파일로 덮어씌우는 객체
			String information = "";//파일 텍스트에 저장할 String형 변수
			for(int i = 0; i < data.length; i++) {
				information += data[i] + "\t";//해당 정보를 텍스트로 저장
			}
			for(int i = 0; i < model.getRowCount(); i++) {
				information += "\n";//1줄이 찰 때 마다 줄바꾸기
				for(int j = 0; j < model.getColumnCount(); j++) {
					information += (String)model.getValueAt(i, j) + "\t";
				}//테이블의 열 수 만큼 해당 데이터에 있는 식사 유형, 식사명, 결제자, 수량, 가격, 주문 날짜를 저장함.
			}
			fout.write(information);//해당 정보를 파일로 덮어씀
			fout.close();//파일 객체 닫음
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class TablePanel extends JPanel{
		TablePanel(){
			Vector<String>colData = new Vector<String>();//열 데이터를 저장할 String형 벡터
			rowData = new Vector<Vector<String>>();//행 데이터를 저장할 String형 이중 벡터
			for(int i = 0; i < data.length; i++) {//"종류","메뉴명","사원명","결제수량","총결제금액","결제일"을 테이블 최상단에 지정
				colData.add(data[i]);
				//t.getColumn(i).setCellRenderer(dtcr);
			}
			model = new DefaultTableModel(rowData, colData);
			t = new JTable(model);//테이블 생성
			t.getColumn("결제일").setPreferredWidth(100);//결제일 데이터 열을 길게 함
			//테이블 생성 후 가운데 정렬하는 작업
			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); // 디폴트테이블셀렌더러를 생성
		    dtcr.setHorizontalAlignment(SwingConstants.CENTER); // 렌더러의 가로정렬을 CENTER로
		    TableColumnModel tcm = t.getColumnModel(); // 정렬할 테이블의 컬럼모델을 가져옴
			for(int i = 0 ; i < tcm.getColumnCount() ; i++){
		       tcm.getColumn(i).setCellRenderer(dtcr);  
		       //컬럼모델에서 컬럼의 갯수만큼 컬럼을 가져와 for문을 이용하여 각각의 셀렌더러를 아까 생성한 dtcr에 set해줌
			}
			//테이블 최종 추가하기
			JScrollPane jps = new JScrollPane(t);
			add(jps);
		}
	}
}
