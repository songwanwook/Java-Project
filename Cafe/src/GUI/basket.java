package GUI;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.*;
import DB.DriverConnector;
import java.awt.*;
import java.awt.event.*;
public class basket extends JFrame {//장바구니
	String name;
	JLabel basketInfo;
	String sql;
	PreparedStatement pst;
	Connection c = DriverConnector.MakeConnection("cafe");//데이터베이스 연결
	JTable t;
	Vector<String>TableVector;//테이블 정보 벡터
	Vector<Vector<String>>rowData;//열 데이터 벡터
	DefaultTableModel model;
	String str[] = {"주문번호","메뉴명","가격","수량","사이즈","금액"};//문제에 없었던 주문번호를 따로 추가
	JScrollPane jsp;
	int point;//점수
	int u_no;//사용자 번호
	String grade;
	basket(String name){
		this.name=name;
		setTitle("장바구니");
		setSize(700,400);
		setVisible(true);
		basketInfo = new JLabel(name + "회원님의 장바구니");
		basketInfo.setFont(new Font("gulim", Font.BOLD, 30));
		basketInfo.setHorizontalAlignment(JLabel.CENTER);
		add(basketInfo,BorderLayout.NORTH);
		add(new basketTable(),BorderLayout.CENTER);
		add(new ButtonPanel(),BorderLayout.SOUTH);
		this.addWindowListener(new back());
	}
	class back extends WindowAdapter{//X를 누를 경우 바로 종료하지 않고 뒤로 가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new STARBOX(name);
		}
	}
	class basketTable extends JPanel{
		basketTable(){
			TableVector = new Vector<String>();//테이블 정보
			rowData = new Vector<Vector<String>>();//테이블에 등록될 데이터
			model = new DefaultTableModel(rowData, TableVector);
			for(int i = 0; i < str.length; i++) {
				TableVector.add(str[i]);
			}//테이블 제목 등록
			TableRefresh();
			t = new JTable(model);
			jsp = new JScrollPane(t);//테이블을 스크롤팬에 등록
			t.getColumn("메뉴명").setPreferredWidth(180);
			jsp.setPreferredSize(new Dimension(600,270));
			add(jsp);
		}
	}
	private void TableRefresh(){
		String sql = "select * from shopping, user, menu where "//장바구니, 사용자, 메뉴 테이블을 조인하여
				+ "menu.m_no = shopping.m_no and user.u_no = shopping.u_no and u_name = ?";//회원 이름과 회원번호, 메뉴번호를 검색하는 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, name);//사용자 이름을 불러와서
			ResultSet rs = pst.executeQuery();//SQL문 실행
			while(rs.next()) {
				Vector<String>rsVector = new Vector<String>();//장바구니내역 데이터들을 담을 벡터
				String values[] = {Integer.toString(rs.getInt("s_no")),rs.getString("m_name"), 
						Integer.toString(rs.getInt("s_price")),Integer.toString(rs.getInt("s_count")),
						rs.getString("s_size"), Integer.toString(rs.getInt("s_amount"))};//장바구니 번호, 메뉴명, 가격, 수량, 사이즈, 결제 금액을 String형 배열에 저장
				for(int i = 0; i < str.length; i++) {
					rsVector.add(values[i]);
				}//저장된 배열을 벡터에 등록
				point = rs.getInt("u_point");//회원 포인트
				u_no = rs.getInt("u_no");//회원 번호
				grade = rs.getString("u_grade");//회원 등급을
				rowData.add(rsVector);//벡터에 등록
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class ButtonPanel extends JPanel{
		ButtonPanel(){
			String btnstr[] = {"구매","삭제","닫기"};
			JButton btn[] = new JButton[btnstr.length];//구매, 삭제, 닫기 버튼
			for(int i = 0; i < btn.length; i++) {
				btn[i] = new JButton(btnstr[i]);
				add(btn[i]);
				btn[i].addActionListener(new Event());//구매, 삭제, 닫기 기능을 실행할 버튼 이벤트 등록
			}
		}
		class Event implements ActionListener {//구매, 삭제, 닫기 기능을 실행할 버튼 이벤트
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JButton b = (JButton)e.getSource();
				switch(b.getText()) {
					case "닫기": dispose(); new STARBOX(name); break;//닫기
					case "삭제": //삭제
					if(t.getRowCount() == 0) {//장바구니 내역이 없을 때 삭제 불가능
						JOptionPane.showMessageDialog(null,"삭제할 내역이 없습니다.","삭제불가",JOptionPane.ERROR_MESSAGE);
					}
					else {//장바구니 내역이 있을 때
						Delete();//삭제 기능 실행
					}
					break;
					case "구매": //구매
						if(t.getRowCount() == 0) {//장바구니 내역이 없을 때 구매 불가능
							JOptionPane.showMessageDialog(null,"장바구니의 상품이 없습니다.","구매불가",JOptionPane.ERROR_MESSAGE);
						}
						else {//장바구니 내역이 있을 때
							Purchase(); dispose(); new STARBOX(name); break;//구매 기능 실행 뒤 장바구니 화면 닫고, 이전 화면 띄움
						}
				}
			}
		}
	}
	private void Delete(){
		int s_no;
		sql = "delete from shopping where s_no = ?";//주문내역 삭제
		try {
			s_no = Integer.parseInt((String) t.getValueAt(t.getSelectedRow(), 0));//선택된 테이블의 주문번호 불러오기
			pst = c.prepareStatement(sql);
			pst.setInt(1, s_no);//주문번호를 불러와
			pst.executeUpdate();//SQL문 실행
			rowData.clear();//테이블 모든 정보를 삭제 후
			TableRefresh();//재검색
			t.updateUI();//테이블갱신
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {//테이블 컬럼을 선택을 안했을 때 에러메시지 처리
			JOptionPane.showMessageDialog(null,"삭제할 메뉴를 선택하시오.","삭제불가",JOptionPane.ERROR_MESSAGE);
		}
	}
	private void Purchase() {//장바구니 전체 구매
		int total = 0;
		for(int i = 0; i < t.getRowCount(); i++) {//장바구니 열 갯수 만큼 가격 등록
			total += Integer.parseInt(String.valueOf(t.getValueAt(i, 5)));
		}
		if(point >= total) {//포인트가 결제 금액보다 많을 시에
			int result = JOptionPane.showConfirmDialog(null, 
				"회원님의 총 포인트 : " + point + "\n포인트로 결제하시겠습니까?\n(아니오, X를 클릭 시 현금결제가 됩니다.)",
				"결제 수단", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) {//포인트로 구매
				JOptionPane.showMessageDialog(null, "포인트로 결제완료되었습니다.\n남은 포인트 : " + (point-total),
				"마일리지 결제",JOptionPane.PLAIN_MESSAGE);
				//구매 및 마일리지 차감(구매내역 DB에는 등록 X)
				point = point - total;//포인트 차감
				UpdatePoints();//차감된 포인트 반영
			}
			else {//현금 구매
				point = (int)(point + 0.05*total);//구매된 금액 만큼 포인트 적립
				JOptionPane.showMessageDialog(null, "구매되었습니다.\n현재 포인트 : " + point,"일반 결제",JOptionPane.PLAIN_MESSAGE);
				InsertOrderlist();//구매내역 저장
				UpdatePoints();//적립된 포인트 반영
			}
		}
		else {//현금 구매
			point = (int)(point + 0.05*total);//구매된 금액 만큼 포인트 적립
			JOptionPane.showMessageDialog(null, "구매되었습니다.\n현재 포인트 : " + point,"일반 결제",JOptionPane.PLAIN_MESSAGE);
			InsertOrderlist();//구매내역 저장
			UpdatePoints();//적립된 포인트 반영
		}
	}
	//하위에서는 STARBOX 클래스에 있는 내용과 유사함.
	private void InsertOrderlist() {
		for(int i = 0; i < t.getRowCount(); i++) {//구매내역 테이블 갯수만큼 구매를 실행
			String sql1 = "select * from menu where m_name = ?";//테이블에서 명을 검색하는 SQL
			int m_no = 0;
			String m_group = "";
			try {
				pst = c.prepareStatement(sql1);
				pst.setString(1, String.valueOf(t.getValueAt(i, 1)));//반환할 메뉴명
				ResultSet rs = pst.executeQuery();//SQL문 실행
				while(rs.next()) {
					m_no = rs.getInt("m_no");//메뉴 번호 반환
					m_group = rs.getString("m_group");//메뉴 그룹 반환
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sql = "insert into orderlist(o_date, u_no, m_no, o_group, o_size, o_price, o_count, o_amount)"
					+ " values(?,?,?,?,?,?,?,?)";//주문내역에 날짜, 회원번호, 메뉴번호, 메뉴그룹, 사이즈, 원가, 수량, 결제금액을 등록할 SQL문
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//현재시간 데이터 출력
			String formatedNow = formatter.format(date);
			try {
				pst = c.prepareStatement(sql);
				pst.setString(1, formatedNow);//현재시간
				pst.setInt(2, u_no);//사용자번호
				pst.setInt(3, m_no);//메뉴번호
				pst.setString(4, m_group);//메뉴그룹
				pst.setString(5, String.valueOf(t.getValueAt(i, 4)));//사이즈
				if(String.valueOf(t.getValueAt(i, 4)).equals("L")) {//Large 사이즈면
					pst.setInt(6, Integer.parseInt(String.valueOf(t.getValueAt(i, 2)))+1000);//+1000원
				}
				else {//M 사이즈면
					pst.setInt(6, Integer.parseInt(String.valueOf(t.getValueAt(i, 2))));//원가
				}
				pst.setInt(7, Integer.parseInt(String.valueOf(t.getValueAt(i, 3))));//수량
				pst.setInt(8, Integer.parseInt(String.valueOf(t.getValueAt(i, 5))));//총금액
				pst.executeUpdate();//SQL문 실행
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int s_no;//장바구니 내역 삭제
			sql = "delete from shopping where s_no = ?";//주문번호로 장바구니 내역 삭제 SQL문
			try {
				s_no = Integer.parseInt((String) t.getValueAt(i, 0));//모든 상품을 구매했으니 모든 열이 삭제되게 한다.
				pst = c.prepareStatement(sql);
				pst.setInt(1, s_no);//삭제할 주문번호
				pst.executeUpdate();//SQL 실행 후 상품 삭제만 하고, Delete() 메서드에 있는 테이블 갱신은 하지 않는다.
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		getGrade();//승급
	}
	private void UpdatePoints() {//포인트 갱신
		sql = "update user set u_point = ? where u_name = ?";//해당 회원의 포인트를 갱신할 SQL문
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setInt(1, point);//갱신할 포인트
			pst.setString(2, name);//포인트를 갱신할 회원
			pst.executeUpdate();//SQL문 실행
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getGrade() {//포인트에 따른 등급 승급
		sql = "select sum(o_amount) from user, orderlist where user.u_no = orderlist.u_no and u_name = ?";
		//회원 테이블과 구매내역 테이블을 조인하여 회원 이름에 따른 총 결제 금액을 검색할 SQL문
		int sum = 0;
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, name);//회원 이름을 받아와
			ResultSet rs = pst.executeQuery();//SQL문 실행
			if(rs.next()) {
				sum = rs.getInt("sum(o_amount)");//총 결제 금액 리턴
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(sum >= 300000) {//30만원 브론즈 등급 승급
			if(grade == "일반") {
				grade = "Bronze";
				setGrade();
			}
		}
		else if(sum >= 500000) {//50만원 실버 등급 승급
			if(grade == "Bronze") {
				grade = "Silver";
				setGrade();
			}
		}
		else if(sum >= 800000) {//80만원 골드 등급 승급
			if(grade == "Silver") {
				grade = "Gold";
				setGrade();
			}
		}
	}
	private void setGrade() {//승급
		sql = "update user set u_grade = ? where u_name = ?";//해당 회원의 등급을 승급할 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, grade);
			pst.setString(2, name);//해당 사용자 이름과 등급을 찾아 등급을 승급
			pst.executeUpdate();//SQL 실행
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "축하합니다!\n회원님의 등급이 " + grade + "로 승격하였습니다.",
				grade + " 등급이 되었습니다.",JOptionPane.PLAIN_MESSAGE);//승급된 등급을 메시지로 띄우기
	}
}
