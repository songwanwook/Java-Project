package GUI;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

import DB.DriverConnector;
import GUI.STARBOX.back;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
public class purchase extends JFrame {
	String name;
	JLabel purchaseInfo;
	String sql;
	PreparedStatement pst;
	Connection c = DriverConnector.MakeConnection("cafe");//데이터베이스 연결
	JTable t;
	Vector<String>TableVector;//테이블 정보 벡터
	Vector<Vector<String>>rowData;//열 데이터 벡터
	String pinfo[] = {"구매일자","메뉴명","가격","사이즈","수량","금액"};//구매정보
	DefaultTableModel model;
	JScrollPane jsp;
	purchase(String name){
		this.name=name;
		setTitle("구매내역");
		setSize(1000,400);
		setVisible(true);
		purchaseInfo = new JLabel(name + "회원님의 구매 내역");
		purchaseInfo.setFont(new Font("gulim", Font.BOLD, 30));
		purchaseInfo.setHorizontalAlignment(JLabel.CENTER);
		add(purchaseInfo,BorderLayout.NORTH);
		add(new PurchaseTable(),BorderLayout.CENTER);
		add(new TotalPrice(),BorderLayout.SOUTH);
		this.addWindowListener(new back());
	}
	class back extends WindowAdapter{//X를 누를 경우 바로 종료하지 않고 뒤로 가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new STARBOX(name);
		}
	}
	class PurchaseTable extends JPanel{
		PurchaseTable(){
			TableVector = new Vector<String>();//열 데이터를 저장할  벡터
			rowData = new Vector<Vector<String>>();//열 갯수에 행 데이터를 저장할 2차원 벡터
			for(int i = 0; i < pinfo.length; i++) {
				TableVector.add(pinfo[i]);//테이블 제목에 구매 정보 열 추가
			}
			model = new DefaultTableModel(rowData,TableVector);
			sql = "select * from user, orderlist, menu where"
					+ " menu.m_no = orderlist.m_no and orderlist.u_no = user.u_no and u_name = ?";
			//해당 회원 구매내역 검색 SQL문
			try {
				pst = c.prepareStatement(sql);
				pst.setString(1, name);
				ResultSet rs = pst.executeQuery();//SQL문 실행
				while(rs.next()) {
					Vector<String>rsVector = new Vector<String>();//구매내역 데이터들을 담을 벡터
					int aliasprice = rs.getInt("o_price");
					String returnprice, returntotal;
					if(aliasprice % 1000 == 0) {
						returnprice = "000"; 
					}
					else {
						returnprice = Integer.toString(aliasprice%1000);
					}
					String AliasedPrice = Integer.toString(aliasprice/1000) + "," + returnprice;
					int aliastotal = rs.getInt("o_amount");
					if(aliastotal % 1000 == 0) {
						returntotal = "000";
					}//숫자 천단위 , 씌우기
					else {
						returntotal = Integer.toString(aliastotal%1000);
					}
					String AliasedTotal = Integer.toString(aliastotal/1000) + "," + returntotal;
					String value[] = {rs.getString("o_date"), rs.getString("m_name"), AliasedPrice, 
					rs.getString("o_size"), Integer.toString(rs.getInt("o_count")), AliasedTotal};
					//구매일자, 메뉴명, 가격, 사이즈, 수량, 총금액을 벡터에 담을 String 배열
					for(int i = 0; i < pinfo.length; i++) {
						rsVector.add(value[i]);//구매내역 벡터에 구매내역 데이터들을 받아옴
					}
					rowData.add(rsVector);//열 데이터 벡터에 구매내역 정보 모두 담기
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t = new JTable(model);//테이블 생성
			t.getColumn("구매일자").setPreferredWidth(130);
			t.getColumn("메뉴명").setPreferredWidth(180);
			t.setEnabled(false);
		    jsp = new JScrollPane(t);
		    jsp.setPreferredSize(new Dimension(900,270));//스크롤팬에 테이블을 올리고 스크롤팬 사이즈 늘리기
		    add(jsp);
		}
	}
	JTextField totalprice;
	int total;
	class TotalPrice extends JPanel{
		TotalPrice(){
			add(new JLabel("총 결제 금액 : "));
			totalprice = new JTextField(15);//총 결제 금액을 보여줄 JTextField
			add(totalprice);
			totalprice.setEditable(false);//총 결제금액은 사용자 임의로 수정 불가능
			new getTotalPrice();
			JButton close = new JButton("닫기");
			close.addActionListener(new ActionListener() {//닫기버튼
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					dispose();
					new STARBOX(name);
				}
			});
			add(close);
		}
	}
	class getTotalPrice{//총 결제금액을 반환할 클래스 함수
		getTotalPrice(){
			sql = "select sum(o_amount) from user, orderlist where user.u_no = orderlist.u_no and u_name = ?";
			//이름에 따른 총 결제금액을 검색할 SQL문
			try {
				pst = c.prepareStatement(sql);
				pst.setString(1, name);//이름을 받아와
				ResultSet rs = pst.executeQuery();//SQL문 실행
				if(rs.next()) {
					total = rs.getInt("sum(o_amount)");//해당 사용자의 총 결제금액 반환
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			totalprice.setText(Integer.toString(total));//JTextField에 반환된 결제금액 설정
		}
	}
}
