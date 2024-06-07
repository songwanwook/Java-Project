package GUI;
import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import DB.DriverConnector;
import java.awt.event.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
public class UpdateMenu extends JFrame{
	JComboBox menu;//검색용 콤보박스
	JComboBox menu1;//수정용 콤보박스
	JTextField jt[] = new JTextField[3];//jt[0] = 검색, jt[1] = 메뉴명, jt[2] = 가격
	JButton search;//검색 버튼
	String sql;
	PreparedStatement pst;
	Statement st;
	Connection c = DriverConnector.MakeConnection("cafe");//데이터베이스 연결
	JTable t;
	Vector<String>TableVector;//테이블 정보 벡터
	Vector<Vector<String>>rowData;//열 데이터 벡터
	String minfo[] = {"분류","메뉴명","가격"};//메뉴 정보 불러오는 테이블 제목
	DefaultTableModel model;
	JScrollPane jsp;
	String str[] = {"전체","음료","푸드","상품"};//콤보박스 안에 들어갈 메뉴명
	UpdateMenu(){
		setTitle("메뉴 수정");
		setLayout(new GridLayout(1,2));
		setSize(800,400);
		add(new TablePanel());
		add(new UpdateMenuPanel());
		setVisible(true);
		this.addWindowListener(new back());
	}
	class back extends WindowAdapter{//X를 누를 경우 바로 종료하지 않고 뒤로 가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new Admin();
		}
	}
	class TablePanel extends JPanel{
		TablePanel(){
			setLayout(new BorderLayout());
			add(new TableMenu(),BorderLayout.NORTH);
			add(new Table(),BorderLayout.CENTER);
		}
	}
	class TableMenu extends JPanel{
		TableMenu(){
			add(new JLabel("검색"));
			menu = new JComboBox();
			add(menu);//메뉴 선택 콤보박스
			for(int i = 0; i < str.length; i++) {
				menu.addItem(str[i]);
			}//메뉴명 추가
			jt[0] = new JTextField(20);//검색 텍스트필드
			add(jt[0]);
			search = new JButton("검색");//검색 버튼
			add(search);
			search.addActionListener(new Search());//검색 버튼을 클릭했을때 검색 이벤트 추가
		}
	}
	class Table extends JPanel{
		Table(){
			TableVector = new Vector<String>();//열 데이터를 저장할 벡터
			rowData = new Vector<Vector<String>>();//열 갯수에 행 데이터를 저장할 2차원 벡터
			for(int i = 0; i < minfo.length; i++) {
				TableVector.add(minfo[i]);
			}
			selectAll();
			model = new DefaultTableModel(rowData,TableVector);
			t = new JTable(model);
			t.getColumn("메뉴명").setPreferredWidth(180);
			jsp = new JScrollPane(t);
			jsp.setPreferredSize(new Dimension(370,300));//스크롤팬에 테이블을 올리고 스크롤팬 사이즈 늘리기
			add(jsp);
			t.addMouseListener(new TableEvent());
		}
	}
	int clickcount = 0;
	ImageIcon img;
	class TableEvent extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			clickcount++;
			if(clickcount == 1) {
				for(int i = 1; i < str.length; i++) {
					menu1.addItem(str[i]);
				}
			}
			String menu = (String) t.getValueAt(t.getSelectedRow(), 0);
			menu1.setSelectedItem(menu);
			String menuname = (String) t.getValueAt(t.getSelectedRow(), 1);
			jt[1].setText((String) menuname);
			jt[2].setText((String) t.getValueAt(t.getSelectedRow(), 2));
			img = new ImageIcon("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지\\" + menuname + ".jpg");
			Image image = img.getImage().getScaledInstance(PictureLabel.getWidth()-2, PictureLabel.getHeight()-2, Image.SCALE_DEFAULT);
			img = new ImageIcon(image);
			PictureLabel.setIcon(img);
		}
	}
	class Search implements ActionListener {//검색 버튼을 클릭했을때 이벤트
		@Override
		public void actionPerformed(ActionEvent e) {
			rowData.clear();//테이블 모든 정보를 삭제 후
			t.updateUI();//테이블갱신
			// TODO Auto-generated method stub
			if(menu.getSelectedItem().equals("전체") && jt[0].getText().equals("")) {//전체검색
				selectAll();//모두검색
			}
			else if(jt[0].getText().equals("")) {
				selectByGroup();//그룹별 모두 검색
			}
			else if(menu.getSelectedItem().equals("전체")&& jt[0].getText().length() != 0) {
				SelectByMname();//이름별 검색
			}
			else if(!menu.getSelectedItem().equals("전체")&& jt[0].getText().length() != 0){
				SelectSearch();//그룹별, 이름별 검색
			}
		}
	}
	private void selectAll(){//모든 결과 검색(전체, 텍스트필드 입력 X)
		sql = "select * from menu";//모든결과 검색 SQL문
		try {
			st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);//SQL문 실행
			while(rs.next()) {
				Vector<String>rsVector = new Vector<String>();//상품정보 데이터들을 담을 벡터
				int price = rs.getInt("m_price");
				String value[] = {rs.getString("m_group"),rs.getString("m_name"),Integer.toString(price)};//그룹, 이름, 가격
				for(int i = 0; i < minfo.length; i++) {
					rsVector.add(value[i]);//그룹, 이름, 가격을 벡터에 추가
				}
				rowData.add(rsVector);//열 데이터 벡터에 구매내역 정보 모두 담기
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//Statement를 썼을 경우 굳이 4개의 클래스로 안나눠도 될듯... 어떤 상태에 따라 Statement를 쓸지 PreparedStatement를 쓸지 생각해야 할듯
	private void selectByGroup() {//메뉴 그룹별로 검색
		sql = "select * from menu where m_group = ?";//그룹별 결과 검색 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, String.valueOf(menu.getSelectedItem()));//메뉴 콤보박스로 검색 실행
			ResultSet rs = pst.executeQuery();//SQL실행
			while(rs.next()) {
				Vector<String>rsVector = new Vector<String>();//상품정보 데이터들을 담을 벡터
				int price = rs.getInt("m_price");
				String value[] = {rs.getString("m_group"),rs.getString("m_name"),Integer.toString(price)};
				for(int i = 0; i < minfo.length; i++) {
					rsVector.add(value[i]);//그룹, 이름, 가격을 벡터에 추가
				}
				rowData.add(rsVector);//열 데이터 벡터에 구매내역 정보 모두 담기
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void SelectByMname() {//텍스트 필드에 입력된 키워드가 있는 상품을 검색
		sql = "select * from menu where m_name LIKE ?";//텍스트필드에 있는 키워드로 메뉴 검색을 하는 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, "%"+jt[0].getText()+"%");//텍스트필드 키워드로 검색 실행
			ResultSet rs = pst.executeQuery();//SQL문 실행
			while(rs.next()) {
				Vector<String>rsVector = new Vector<String>();//상품정보 데이터들을 담을 벡터
				int price = rs.getInt("m_price");
				String value[] = {rs.getString("m_group"),rs.getString("m_name"),Integer.toString(price)};
				for(int i = 0; i < minfo.length; i++) {
					rsVector.add(value[i]);//그룹, 이름, 가격을 벡터에 추가
				}
				rowData.add(rsVector);//열 데이터 벡터에 구매내역 정보 모두 담기
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void SelectSearch() {//해당 그룹의 입력된 키워드로 검색
		sql = "select * from menu where m_name LIKE ? and m_group = ?";//메뉴 그룹과 텍스트필드 키워드를 모두 검색하는 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, "%"+jt[0].getText()+"%");//텍스트필드 키워드 받아옴
			pst.setString(2, String.valueOf(menu.getSelectedItem()));//콤보박스 메뉴 받아옴
			ResultSet rs = pst.executeQuery();//SQL문 실행
			while(rs.next()) {
				Vector<String>rsVector = new Vector<String>();//상품정보 데이터들을 담을 벡터
				int price = rs.getInt("m_price");
				String value[] = {rs.getString("m_group"),rs.getString("m_name"),Integer.toString(price)};
				for(int i = 0; i < minfo.length; i++) {
					rsVector.add(value[i]);//그룹, 이름, 가격을 벡터에 추가
				}
				rowData.add(rsVector);//열 데이터 벡터에 구매내역 정보 모두 담기
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	Picture picture = new Picture();//메뉴마다 사진을 갱신해야 되기 때문에 전역번수로 선언
	String filePath = null;//불러온 이미지 경로를 저장할 String형 변수
	JLabel PictureLabel;//상품 이미지를 담을 JLabel
	class MenuPanel extends JPanel{//메뉴 정보를 수정할 JPanel
		MenuPanel(){
			setLayout(null);
			UpdateMenuInfo uinfo = new UpdateMenuInfo();
			add(uinfo);
			uinfo.setSize(230, 300);
			uinfo.setLocation(20, 50);
			picture.setSize(120,150);
			picture.setLocation(260, 100);
			add(picture);
		}
	}
	class UpdateMenuPanel extends JPanel{
		UpdateMenuPanel(){
			setLayout(new BorderLayout());
			add(new MenuPanel(),BorderLayout.CENTER);
			add(new UpdateButtonPanel(),BorderLayout.SOUTH);
		}
	}
	class UpdateMenuInfo extends JPanel{
		UpdateMenuInfo(){
			setLayout(new BorderLayout());
			add(new UpdateMenuInfoLabel(),BorderLayout.WEST);
			add(new UpdateMenuInfoCenter(),BorderLayout.CENTER);
			add(picture,BorderLayout.EAST);
		}
		class UpdateMenuInfoLabel extends JPanel{//콤보박스, 텍스트필드 정보를 알려줄 JLabel
			UpdateMenuInfoLabel(){
				String str[] = {"분류","메뉴명","가격"};
				setLayout(new GridLayout(3,1));
				JLabel la[] = new JLabel[3];
				for(int i = 0; i < la.length; i++) {
					la[i] = new JLabel(str[i]);
					add(la[i]);
					la[i].setVerticalAlignment(JLabel.NORTH);
				}
			}
		}
		class UpdateMenuInfoCenter extends JPanel{
			UpdateMenuInfoCenter(){
				setLayout(new GridLayout(3,1,0,0));
				JPanel p[] = new JPanel[3];
				for(int i = 0; i < p.length; i++) {
					p[i] = new JPanel();
					p[i].setLayout(new FlowLayout(FlowLayout.LEFT,20,0));
					if(i == 0) {
						menu1 = new JComboBox();//메뉴 그룹을 결정할 콤보박스
						p[i].add(menu1);
					}
					else {
						jt[i] = new JTextField(15);//메뉴명과 가격을 설정할 텍스트필드
						p[i].add(jt[i]);
					}
					add(p[i]);
				}
			}
		}
	}
	class UpdateButtonPanel extends JPanel{
		UpdateButtonPanel(){
			JButton OKCancel[] = new JButton[3];
			String str[] = {"삭제","수정","취소"};
			for(int i = 0; i < str.length; i++) {
				OKCancel[i] = new JButton(str[i]);//삭제, 수정, 취소 버튼
				add(OKCancel[i]);
				OKCancel[i].addActionListener(new ActionListener() {//버튼 클릭 시 이벤트
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						if(e.getActionCommand().equals(str[2])) {//취소 버튼 클릭
							dispose();new Admin();
						}
						else if(t.getSelectedRowCount() == 0) {//선택이 안됐을때 삭제, 수정 버튼 클릭
							JOptionPane.showMessageDialog(null, e.getActionCommand()+"할 메뉴를 선택하세요.",
									e.getActionCommand()+" 불가",JOptionPane.ERROR_MESSAGE);
						}
						else if(jt[1].getText().equals("") || jt[2].getText().equals("")) {//텍스트필드에 빈칸이 있을 때 삭제, 수정 버튼 클릭
							JOptionPane.showMessageDialog(null, "빈칸이 존재합니다.",e.getActionCommand()+" 불가",JOptionPane.ERROR_MESSAGE);
						}
						else {
							switch(e.getActionCommand()) {
								case"삭제":delete();break;//삭제 버튼 클릭
								case"수정":Update();break;//수정 버튼 클릭
							}
						}
					}
				});
			}
		}
	}
	class Picture extends JPanel{//메뉴 사진
		Picture(){
			setLayout(new BorderLayout());
			PictureLabel = new JLabel();
			add(PictureLabel,BorderLayout.CENTER);
			PictureLabel.setBorder(new LineBorder(Color.BLACK));
			JButton Picture = new JButton("사진 선택");
			add(Picture,BorderLayout.SOUTH);
			Picture.addActionListener(new ActionListener() {//사진 선택 버튼 클릭
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JFileChooser js = new JFileChooser("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지");//파일 다이얼로그
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");//이미지 파일만 열게 설정
					js.setFileFilter(filter);//파일 다이얼로그에 이미지 파일만 열게 등록
					js.showOpenDialog(null);
					filePath = js.getSelectedFile().getPath();//등록된 파일 경로 불러오기
					img = new ImageIcon(filePath);//파일 경로로 이미지 불러오기
					Image image = img.getImage().getScaledInstance(PictureLabel.getWidth()-2, PictureLabel.getHeight()-2, Image.SCALE_DEFAULT);
					//이미지 사이즈 조절
					img = new ImageIcon(image);
					PictureLabel.setIcon(img);//불러온 이미지 등록
				}
			});
		}
	}
	private void delete() {//메뉴 삭제
		sql = "delete from menu where m_name = ?";//삭제할 메뉴 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, jt[1].getText());//메뉴명 가져오기
			pst.executeUpdate();//SQL문 실행
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "해당 메뉴를 삭제하였습니다.","삭제 성공",JOptionPane.PLAIN_MESSAGE);//삭제 성공
		
		File deletefile = new File("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지\\" + jt[1].getText() +".jpg");//삭제할 파일 찾음
		deletefile.delete();//찾은 파일 삭제
		Refresh();//테이블 갱신
	}
	int newprice;//새 가격
	private void Update() {//메뉴 수정 여부 확인
		try {
			newprice = Integer.parseInt(jt[2].getText());//새 가격 받아오기
		}
		catch(NumberFormatException e1) {//새 가격이 숫자가 아닌 경우
			JOptionPane.showMessageDialog(null,"가격은 숫자만 입력할 수 있습니다.","상품 수정 실패",JOptionPane.ERROR_MESSAGE);
			jt[2].setText(null);
		}
		if(newprice < 1000) {//새 가격이 1000원 미만
			JOptionPane.showMessageDialog(null,"상품의 최소가격은 1000원 입니다.","상품 수정 실패",JOptionPane.ERROR_MESSAGE);
		}
		else {
			sql = "select * from menu where m_name != ?";//해당 메뉴 외 중복 메뉴 있는지 확인하는 SQL문
			try {
				pst = c.prepareStatement(sql);
				pst.setString(1, jt[1].getText());//텍스트필드 메뉴명 받아와서
				ResultSet rs = pst.executeQuery();//SQL문 실행
				if(rs.next()) {
					if(rs.getString("m_name").equals(jt[1].getText())) {//자신 메뉴명 외 중복 메뉴명 발견
						JOptionPane.showMessageDialog(null,"해당 메뉴 이름 외 중복되는 메뉴 이름으로는 수정할 수 없습니다.","상품 수정 실패",JOptionPane.ERROR_MESSAGE);
					}
					else {//수정 가능
						updateMenu();//메뉴 수정
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void updateMenu() {//메뉴 수정 기능
		sql = "update menu set m_group = ?, m_name = ?, m_price = ? where m_name = ?";//메뉴 수정 SQL문
		String olderfile = (String) t.getValueAt(t.getSelectedRow(), 1);//현재 메뉴명
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, (String)menu1.getSelectedItem());//수정할 메뉴 그룹
			pst.setString(2, jt[1].getText());//수정할 메뉴명
			pst.setInt(3, newprice);//수정할 가격
			pst.setString(4, (String) t.getValueAt(t.getSelectedRow(), 1));//수정할 현재 메뉴명
			pst.executeUpdate();//SQL문 실행
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(olderfile!=jt[1].getText()) {//메뉴명이 바뀔 경우 파일명 변경
			File oldfile = new File("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지\\" + olderfile +".jpg");//현재 메뉴명 파일
			File newfile = new File("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지\\" + jt[1].getText() +".jpg");//새 파일
			oldfile.renameTo(newfile);//경로 변경
		}
		JOptionPane.showMessageDialog(null, "수정되었습니다.");
		Refresh();//테이블 초기화
	}
	private void Refresh() {//테이블 초기화
		PictureLabel.setIcon(null);//메뉴 이미지 삭제
		rowData.clear();//테이블 모든 정보를 삭제 후
		selectAll();//모든 정보 출력
		t.updateUI();
		for(int i = 0; i < jt.length; i++) {
			jt[i].setText("");//텍스트필드 입력정보 초기화
		}
		menu.setSelectedItem(null);
		menu1.setSelectedItem(null);//메뉴 콤보박스 선택 초기화
	}
}
//2147라인