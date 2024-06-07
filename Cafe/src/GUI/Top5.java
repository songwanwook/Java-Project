package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import DB.DriverConnector;
import java.sql.*;
import java.util.*;
public class Top5 extends JFrame{
	String name;//name을 받는 이유 - X버튼을 클릭시 STARBOX로 이동하는데, STARBOX의 name정보를 받아와야 하기 때문
	JComboBox jc;
	String menu[] = {"음료","푸드","상품"};
	String sql;
	PreparedStatement pst;
	Connection c = DriverConnector.MakeConnection("cafe");//데이터베이스 연결
	Chart chart;
	String getmenu = menu[0];
	Top5(String name){
		this.name=name;
		this.addWindowListener(new back());
		setTitle("인기상품 TOP 5");
		add(new TOP(), BorderLayout.NORTH);
		chart = new Chart();
		add(chart,BorderLayout.CENTER);
		setSize(400,400);
		setVisible(true);
	}
	class back extends WindowAdapter{//X를 누를 경우 바로 종료하지 않고 뒤로 가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new STARBOX(name);
		}
	}
	class TOP extends JPanel {
		TOP(){
			setBackground(Color.LIGHT_GRAY);
			jc = new JComboBox();
			add(jc);
			for(int i = 0; i < menu.length; i++) {
				jc.addItem(menu[i]);
			}
			add(new JLabel("인기상품 TOP 5"));
			jc.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					top5.removeAllElements();
					m_name.removeAllElements();
					getChart();
					getParent().repaint();
				}
			});
		}
	}
	Vector<Integer>top5;
	Vector<String>m_name;
	
	class Chart extends JPanel{
		Chart(){
			getChart();
		}
		Color color[] = {Color.red, Color.orange, Color.yellow, Color.green, Color.blue};
		@Override
		public void paint(Graphics g) {
			g.drawLine(10, 10, 10, getHeight());
			for(int i = 0; i < top5.size(); i++) {
				g.setColor(Color.black);
				g.drawString(m_name.get(i) + "-" + top5.get(i) + "개", 20, 60*(i+1));
				g.setColor(color[i]);
				g.fillRect(10, 20+60*(i), 10*top5.get(i), 30);
			}	
		}
	}
	private void getChart() {
		top5 = new Vector<Integer>();
		m_name = new Vector<String>();
		sql = "select sum(orderlist.o_count) as top5, m_name from orderlist, menu where orderlist.m_no"
				+ " = menu.m_no and menu.m_group = ? group by orderlist.m_no order by top5 desc limit 5";
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, (String) jc.getSelectedItem());
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				top5.add(rs.getInt("top5"));
				m_name.add(rs.getString("m_name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
