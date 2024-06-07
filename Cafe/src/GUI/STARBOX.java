package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import DB.DriverConnector;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
public class STARBOX extends JFrame{
	String name;//회원 이름
	int point;//점수
	String grade;//점수에 따른 등급
	String sql;//sql문
	JLabel information;//회원 정보
	Vector<String>v;
	Connection c = DriverConnector.MakeConnection("cafe");//데이터베이스 연결
	MenuPanel mp;
	JScrollPane jsp;
	int flag = 0;
	PreparedStatement pst;
	int number;//상품번호
	JTextField jt[] = new JTextField[3];//상품 정보 및 가격, 총 결제금액을 전달받을 텍스트필드
	STARBOX(String name){
		setTitle("STARBOX");
		this.name=name;//회원 이름
//		this.point=point;//회원 점수
//		this.grade=grade;//회원 등급
		this.addWindowListener(new back());
		add(new North(),BorderLayout.NORTH);
		add(new SideButton(), BorderLayout.WEST);
		mp = new MenuPanel();
		add(mp,BorderLayout.CENTER);
		getMenu("음료");
		ep = new EastPanel();
		mp.add(ep, BorderLayout.EAST);
		setSize(1400,600);
		setVisible(true);
	}
	class back extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			dispose();
			new Main();
		}
	}
	class North extends JPanel{
		North(){
			setLayout(new GridLayout(2,1));
			sql = "select * from user where u_name = '" + name + "'";//던져준 이름을 받아와 포인트, 등급 반환하는 SQL문
			try {
				Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(sql);//SQL문 실행
				while(rs.next()) {
					point = rs.getInt("u_point");//포인트
					grade = rs.getString("u_grade");//등급
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			information = new JLabel(" 회원명 : " + name + " / 회원등급 : " + grade + " / 총 누적 포인트 : " + point);
			//이전 클래스에서 넘겨온 정보들을 출력
			add(information);
			information.setHorizontalAlignment(JLabel.LEFT);
			add(new ButtonPanel());
		}
	}
	class ButtonPanel extends JPanel{//구매내역, 장바구니, TOP5를 확인하거나, 로그아웃을 실행할 JButton이 들어갈 JPanel
		String str[] = {"구매내역","장바구니","인기상품 TOP 5", "Logout"};
		JButton btn[] = new JButton[str.length];
		ButtonPanel(){
			setLayout(new FlowLayout(FlowLayout.LEFT));
			for(int i = 0; i < btn.length; i++) {
				btn[i] = new JButton(str[i]);
				add(btn[i]);
				btn[i].setHorizontalAlignment(JButton.LEFT);
				btn[i].addActionListener(new Event());
			}
		}
		class Event implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JButton b = (JButton)e.getSource();
				switch(b.getText()) {
					case "구매내역": new purchase(name); break;//구매내역 창 띄움
					case "장바구니": new basket(name); break;//장바구니 창 띄움
					case "인기상품 TOP 5": new Top5(name); break;//TOP 5 창 띄움
					case "Logout":new Main(); break;//로그아웃
				}
				dispose();//기존 창 닫음
			}
		}
	}
	MainMenu mn;//메인메뉴 JPanel을 자유자재로 다루기 위해 변수로 초기화
	class MainMenu extends JPanel {//이벤트리스너를 적용하기 위해 메인 페이지를 여기 추가함
		MainMenu(){
			setLayout(new GridLayout(v.size()/3+1,3));
			for(int i = 0; i < Menus.size(); i++) {
				add(Menus.get(i));
			}//메뉴별 상품 아이콘을 여기에다 올린다.
		}
	}
	Vector<Menus>Menus;//메뉴 아이콘 패널을 담아줄 벡터
	class Menus extends JPanel{//메뉴 아이콘 출력
		public Menus(String MenuName) {
			JLabel la;
			setLayout(new BorderLayout());
			ImageIcon img = new ImageIcon("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지\\" + MenuName + ".jpg");
			//해당 메뉴 이미지 담아오기
			Image image = img.getImage().getScaledInstance(img.getIconWidth()/2, img.getIconHeight()/2,
					java.awt.Image.SCALE_SMOOTH);//이미지 크기 줄여주기
			ImageIcon menuImage = new ImageIcon(image);//새로운 이미지 생성
			JLabel btn = new JLabel(menuImage);//원래 JButton이어야 되는데 크기가 꽉 차서 JLabel로 대신한다.
			add(btn, BorderLayout.CENTER);
			btn.setBorder(new BevelBorder(BevelBorder.RAISED));
			la = new JLabel(MenuName);
			la.setHorizontalAlignment(JLabel.CENTER);
			add(la, BorderLayout.SOUTH);
			btn.addMouseListener(new MouseAdapter() {//JLabel에 클릭 이벤트를 등록한다.
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					flag = 1;
					if(info != null) {//기존 정보창이 있을 시
						ep.remove(info);//해당 정보 창 제거
					}
					info = new Info(MenuName);//해당 메뉴를 클릭하였을 시 정보 창 출력
					ep.add(info);
					mp.revalidate();//화면 갱신
				}
			});
		}
	}
	Info info;
	class SideButton extends JPanel{//사이트 메뉴 버튼
		String str[] = {"음료","푸드","상품"};
		JButton btn[] = new JButton[str.length];
		SideButton(){
			setLayout(new GridLayout(12,1));
			for(int i = 0; i < btn.length; i++) {
				btn[i] = new JButton(str[i]);
				add(btn[i]);
				btn[i].addActionListener(new Event());//사이드 버튼 클릭 시 이벤트리스너
			}
		}
		class Event implements ActionListener {//사이드 버튼 이벤트리스너
			@Override
			public void actionPerformed(ActionEvent e) {//버튼을 클릭 시
				// TODO Auto-generated method stub
				String menu = e.getActionCommand();
				if(flag == 1) {//현재 메뉴 아이콘이 활성화 되면
					new ClearInfo();//메뉴를 닫음
				}
				switch(menu) {
					case "음료":mp.remove(jsp); menu = "음료"; getMenu(menu); break;
					case "푸드":mp.remove(jsp); menu = "푸드"; getMenu(menu); break;
					case "상품":mp.remove(jsp); menu = "상품"; getMenu(menu); break;
				}
				mp.revalidate();//화면 갱신
			}
		}
	}
	class MenuPanel extends JPanel{//메뉴를 결정하는 패널
		MenuPanel(){//해당 패널은 레이아웃만 구성하고, 세부적 구성은 getMenu함수에서 결정하게 한다.
			setLayout(new BorderLayout());
		}
	}
	int count = 0;
	public void getMenu(String menu){//메뉴 출력
		sql = "select * from menu where m_group = ?";//그룹별 메뉴를 찾는 SQL문
		v = new Vector<String>();//메뉴명을 담을 String형 Vector
		
		Menus = new Vector<Menus>();//메뉴 버튼 패널을 담을 JPanel을 상속한 Menus형 벡터
		if(!Menus.isEmpty()) {//메뉴 창이 비어있지 않으면
			Menus.removeAllElements();//메뉴창에 있는 메뉴들을 모두 삭제
		}
		try {
			PreparedStatement pst = c.prepareStatement(sql);
			pst.setString(1, menu);//현재 menu를 불러와
			ResultSet rs = pst.executeQuery();//SQL문 실행
			while(rs.next()) {
				v.add(rs.getString("m_name"));//벡터에 항목별 메뉴명 추가
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i = 0; i < v.size(); i++) {
			Menus.add(new Menus(v.get(i)));//벡터 사이즈 만큼 JPanel을 상속한 Menus 벡터에 해당 벡터명을 추가함
		}
		mn = new MainMenu();
		jsp = new JScrollPane(mn);//스크롤팬에 메뉴 패널 추가
		mp.add(jsp, BorderLayout.CENTER);
		
		revalidate();//화면 갱신
	}
	EastPanel ep;
	int totalprice;
	class EastPanel extends JPanel{
		EastPanel(){
			
		}
	}
	String getmenu;//메뉴명을 가져올 String형 변수
	JComboBox jc[] = new JComboBox[2];
	float getGrade;
	class Info extends JPanel{
		ImageIcon img;//이미지를 가져올 이미지 아이콘
		Info(String MenuName){
			setLayout(new BorderLayout());
			getmenu = MenuName;//메뉴명
			img = new ImageIcon("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지\\" + MenuName + ".jpg");//menu 이미지 생성
			add(new InfoPanel(), BorderLayout.CENTER);
			add(new ButtonPanel(), BorderLayout.SOUTH);
		}
		class InfoPanel extends JPanel {
			InfoPanel(){
				Image image = img.getImage().getScaledInstance(img.getIconWidth()*3/5, img.getIconHeight()*3/5,
						java.awt.Image.SCALE_SMOOTH);//이미지 크기 줄여주기
				ImageIcon menuImage = new ImageIcon(image);//새로운 이미지 생성
				setLayout(new GridLayout(1,2));
				add(new JLabel(menuImage));
				add(new MenuInfo());
			}
		}
		class MenuInfo extends JPanel{
			MenuInfo(){
				setLayout(new GridLayout(5,2,8,8));
				String str[] = {"주문메뉴:","가격:","수량:","사이즈:","총금액:"};
				JLabel la[] = new JLabel[str.length];
				for(int i = 0; i < str.length; i++) {
					la[i] = new JLabel(str[i]);
					la[i].setHorizontalAlignment(JLabel.RIGHT);
					add(la[i]);
					if(i < 2) {
						jt[i] = new JTextField(15);//메뉴명, 가격 텍스트필드
						add(jt[i]);
					}
					else if(i < 4) {
						jc[i-2] = new JComboBox();//수량, 사이즈 콤보박스
						add(jc[i-2]);
					}
					else {
						jt[2] = new JTextField(15);//최종 결제금액 텍스트필드
						add(jt[2]);
					}
				}
				for(int i = 0; i < jt.length; i++) {
					jt[i].setEditable(false);
				}
				for(int i = 1; i <= 10; i++) {
					jc[0].addItem(i);
				}
				String sql = "select m_group from menu where m_name = ?";//해당 상품이 음료 여부를 확인할 SQL구문
				try {
					pst = c.prepareStatement(sql);
					pst.setString(1, getmenu);
					ResultSet rs = pst.executeQuery();//SQL 실행
					if(rs.next()) {
						if(rs.getString("m_group").equals("음료")) {//음료이면
							jc[1].addItem("M");
							jc[1].addItem("L");
						}//M, L 추가
						else{
							jc[1].addItem("M");
							jc[1].setEnabled(false);
						}//아닐경우 M으로 고정
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				setSize(300,300);
				sql = "select * from menu where m_name = ?";//해당 메뉴명을 찾는 SQL문
				//c = DriverConnector.MakeConnection("cafe");
				try {
					pst = c.prepareStatement(sql);
					pst.setString(1, getmenu);//해당 패널의 메뉴명을 가져와
					ResultSet rs = pst.executeQuery();//SQL문 실행
					if(rs.next()) {
						jt[0].setText(rs.getString("m_name"));//해당 상품명 입력받음
						jt[1].setText(rs.getString("m_price"));//해당 상품 가격을 입력받음
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				getGrade = 0;//회원 등급에 따른 할인 제도
				if(grade.equals("일반")) {//일반회원
					getGrade = 1;
				}
				else if(grade.equals("Bronze")) {//브론즈회원
					getGrade = (float) 0.97;
				}
				else if(grade.equals("Silver")) {//실버회원
					getGrade = (float) 0.95;
				}
				else if(grade.equals("Gold")) {//골드회원
					getGrade = (float) 0.9;
				}
				int primaryCost = (int)(Integer.parseInt(jt[1].getText())*getGrade);//할인된금액
				int primaryCostNoDischarge = (int)Integer.parseInt(jt[1].getText());//원가
				jt[2].setText(Integer.toString(primaryCost));
				for(int i = 0; i < jc.length; i++) {
					jc[i].addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent e) {
							int finalprice;//최종 결제 금액
							int islarge = 0, primarylarge = 0;//할인 Large 사이즈, 원가 Large 사이즈
							// TODO Auto-generated method stub
							if(jc[1].getSelectedItem().equals("L")) {//Large 사이즈 선택
								primarylarge = 1000;
								islarge = (int) (1000*getGrade);
							}
							else {//Large 사이즈 해제
								primarylarge = 0;
								islarge = 0;
							}
							jt[1].setText(Integer.toString(primaryCostNoDischarge+primarylarge));//원가
							finalprice = (int)jc[0].getSelectedItem()*(primaryCost+islarge);//할인가격
							jt[2].setText(Integer.toString(finalprice));//최종가격
						}
					});
				}
			}
		}
		class ButtonPanel extends JPanel{
			ButtonPanel(){
				String str[] = {"장바구니에 담기", "구매하기"};
				JButton btn[] = new JButton[2];//장바구니 담기, 구매하기 버튼
				for(int i = 0; i < btn.length; i++) {
					btn[i] = new JButton(str[i]);
					add(btn[i]);
				}
				btn[0].addActionListener(new MyBasket());//장바구니 버튼 이벤트
				btn[1].addActionListener(new Purchase());//구매 버튼 이벤트
			}
		}
	}
	int u_no, m_no, m_price;//사용자번호, 메뉴번호, 메뉴 가격
	String m_group;//메뉴그룹
	class MyBasket implements ActionListener{//장바구니 버튼 클릭 이벤트리스너
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			new ClearInfo();//상품 정보 초기화
			String sql1 = "select u_no, m_no, m_price from user, menu where u_name = ? and m_name = ?";//회원정보와 메뉴명을 조인할 SQL문
			try {
				pst = c.prepareStatement(sql1);
				pst.setString(1,name);//회원정보
				pst.setString(2,getmenu);//메뉴명
				ResultSet rs = pst.executeQuery();//SQL문 실행
				if(rs.next()){
					u_no = rs.getInt("u_no");//회원번호
					m_no = rs.getInt("m_no");//메뉴번호
					m_price = rs.getInt("m_price");//메뉴가격
				}//사용자 번호, 메뉴 번호, 메뉴 가격을 리턴받음.
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			sql = "insert into shopping(m_no, s_price, s_count, s_size, s_amount, u_no) values(?,?,?,?,?,?)";//장바구니 데이터베이스에 추가할 SQL문
			try {
				pst = c.prepareStatement(sql);
				pst.setInt(1, m_no);//메뉴번호
				pst.setInt(2, m_price);//메뉴가격
				pst.setInt(3, (int) jc[0].getSelectedItem());//갯수
				pst.setString(4, String.valueOf(jc[1].getSelectedItem()));//사이즈
				pst.setInt(5, Integer.parseInt(jt[2].getText()));//결제 가격
				pst.setInt(6, u_no);//유저번호
				pst.executeUpdate();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "장바구니에 담았습니다","장바구니 담기",JOptionPane.PLAIN_MESSAGE);
		}
	}
	class Purchase implements ActionListener{//구매 버튼 클릭 이벤트리스너
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			int PurchasePrice = Integer.parseInt(jt[2].getText());
			if(point >= PurchasePrice) {//결제 방법 - 마일리지
				int result = JOptionPane.showConfirmDialog(null, 
				"회원님의 총 포인트 : " + point + "\n포인트로 결제하시겠습니까?\n(아니오, X를 클릭 시 현금결제가 됩니다.)",
				"결제 수단", JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "포인트로 결제완료되었습니다.\n남은 포인트 : " + (point-PurchasePrice),
					"마일리지 결제",JOptionPane.PLAIN_MESSAGE);
					//구매 및 마일리지 차감(구매내역 DB에는 등록 X)
					point = point - PurchasePrice;
					new UpdatePoints();//마일리지 차감을 위해 UpdatePoints()를 실행
				}
				else {
					point = (int)(point + 0.05*PurchasePrice);
					JOptionPane.showMessageDialog(null, "구매되었습니다.\n현재 포인트 : " + point,"일반 결제",JOptionPane.PLAIN_MESSAGE);
					new InsertOrderlist();//구매 내역 작성
					new UpdatePoints();//마일리지 적립을 위해 UpdatePoints()를 실행
				}
			}
			else {//일반 결제
				point = (int)(point + 0.05*PurchasePrice);
				JOptionPane.showMessageDialog(null, "구매되었습니다.\n현재 포인트 : " + point,"일반 결제",JOptionPane.PLAIN_MESSAGE);
				new InsertOrderlist();//구매 내역 작성
				new UpdatePoints();//마일리지 적립을 위해 UpdatePoints()를 실행
			}
			new ClearInfo();
			
		}
	}
	class ClearInfo{//메뉴 정보 초기화
		ClearInfo(){
			ep.remove(info);//상품정보 패널 삭제
			flag = 0;
			mp.revalidate();//화면 갱신
		}
	}
	
	class InsertOrderlist{//구매내역 작성
		InsertOrderlist(){//이 과정에서는 조인문이나 다중 SQL문이 필요함
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//현재시간 데이터 출력
			String formatedNow = formatter.format(date);
			String sql1 = "select u_no, m_no, m_group from user, menu where user.u_name = ? and m_name = ?";//사용자 번호, 메뉴번호, 메뉴그룹 검색
			//int u_no = 0, m_no = 0;
			//String m_group = "";//메뉴그룹
			try {
				pst = c.prepareStatement(sql1);
				pst.setString(1, name);
				pst.setString(2, getmenu);
				ResultSet rs = pst.executeQuery();//이름, 메뉴명으로 SQL문 실행
				while(rs.next()) {
					u_no = rs.getInt("u_no");//사용자번호
					m_group = rs.getString("m_group");//상품 메뉴 그룹
					m_no = rs.getInt("m_no");//메뉴 번호
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//System.out.println(u_no + " " + m_group + " " + m_no);
			String sql = "insert into orderlist(o_date, u_no, m_no, o_group, o_size, o_price, o_count, o_amount)"
					+ " values(?,?,?,?,?,?,?,?)";//날짜, 회원번호, 메뉴번호, 메뉴그룹, 사이즈, 원가, 갯수, 결제 금액을 구매내역에 등록할 SQL문
			try {
				pst = c.prepareStatement(sql);
				pst.setString(1, formatedNow);//날짜
				pst.setInt(2, u_no);//회원번호
				pst.setInt(3, m_no);//메뉴번호
				pst.setString(4, m_group);//메뉴그룹
				pst.setString(5, String.valueOf(jc[1].getSelectedItem()));//사이즈
				pst.setInt(6, Integer.parseInt(jt[1].getText()));//원가
				pst.setInt(7, (int) jc[0].getSelectedItem());//갯수
				pst.setInt(8, Integer.parseInt(jt[2].getText()));//결제금액
				pst.executeUpdate();//SQL문 실행
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			getGrade();//총 결제 금액 갱신에 따른 승급 여부 결정
		}
	}
	class UpdatePoints{//포인트 갱신
		UpdatePoints(){
			String sql = "update user set u_point = ? where u_name = ?";//회원 포인트를 갱신할 SQL문
			try {
				PreparedStatement pst = c.prepareStatement(sql);
				pst.setInt(1, point);//현재 포인트와
				pst.setString(2, name);//회원명을 가져와
				pst.executeUpdate();//SQL문 실행
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			information.setText(" 회원명 : " + name + " / 회원등급 : " + grade + " / 총 누적 포인트 : " + point);//회원정보 갱신
		}
	}
	
	private void getGrade() {//총 결제 금액에 따른 등급 확인
		sql = "select sum(o_amount) from user, orderlist where user.u_no = orderlist.u_no and u_name = ?";
		//회원 테이블과 구매내역 테이블을 조인하여 회원 이름에 따른 총 결제 금액을 검색할 SQL문
		int sum = 0;
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, name);//회원이름을 가져와
			ResultSet rs = pst.executeQuery();//SQL문 실행
			if(rs.next()) {
				sum = rs.getInt("sum(o_amount)");//해당 사용자의 총 결제금액 반환
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(sum >= 300000) {//브론즈 승급 조건
			if(grade.equals("일반")) {//일반등급일때 브론즈 승급 실행
				grade = "Bronze";
				GradeUpdate();
			}
		}
		else if(sum >= 500000) {//실버 승급 조건
			if(grade.equals("Bronze")) {//브론즈 등급일때 실버 승급 실행
				grade = "Silver";
				GradeUpdate();
			}
		}
		else if(sum >= 800000) {//골드 승급 조건
			if(grade.equals("Silver")) {//실버 등급일때 골드 승급 실행
				grade = "Gold";
				GradeUpdate();
			}
		}
	}
	private void GradeUpdate() {//승급
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
