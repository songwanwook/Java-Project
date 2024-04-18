package GUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

import DB.DriverConnector;

import java.sql.*;
import java.util.*;
public class Chart extends JFrame{
	String str[] = {"종류별 결제건수 통계 차트","차트이미지저장","닫기"};
	JButton btn[] = new JButton[2];
	Chart(){
		setTitle("종류별 차트");
		add(new Header(),BorderLayout.NORTH);
		add(new ChartGraph(),BorderLayout.CENTER);
		this.addWindowListener(new back());
		setSize(600,600);
		setVisible(true);
	}
	class back extends WindowAdapter {//X 버튼 클릭했을때 강제 종료 방지
		public void windowClosing(WindowEvent e) {
			dispose();
			new Admin();
		}
	}
	class Header extends JPanel{
		Header(){
			for(int i = 0; i < str.length; i++) {
				if(i == 0) {
					add(new JLabel(str[i]));
				}
				else {
					btn[i-1] = new JButton(str[i]);
					add(btn[i-1]);
					btn[i-1].addActionListener(new Action());//차트 이미지 저장, 닫기 기능을 실행할 버튼
				}
			}
		}
	}
	class Action implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JButton b = (JButton) e.getSource();
			if(b.getText().equals(str[1])) {//차트 이미지 저장하기
				Calendar c = Calendar.getInstance();
				int time[] = {c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE),c.get(Calendar.HOUR_OF_DAY)
						,c.get(Calendar.MINUTE),c.get(Calendar.SECOND)};//현재 시간 받아오기
				String times[] = new String[6];
				String filename = "";//차트 이미지 파일 명을 지정할 String형 변수
				for(int i = 0; i < time.length; i++) {
					if(time[i] < 10) {
						times[i] = "0" + Integer.toString(i);
					}
					else {
						times[i] = Integer.toString(time[i]);
					}
					filename += times[i];
				}//파일명에 시간 저장하기
				BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);//이미지 파일 가져오기
				Graphics2D g2d = image.createGraphics();
				paint(g2d);//이미지 파일 그리기
				try{
					ImageIO.write(image,"jpg", new File("C:\\Users\\MS\\Desktop\\meal\\MealProject\\DataFiles\\"
				+ filename + "-종류별결제현황차트.jpg"));//현재 파일 위치에 저장할 경로를 지정하여 덮어씌운다.
					JOptionPane.showMessageDialog(null, "이미지 저장 성공"); 
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "이미지 저장 실패", "파일 오류", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
			else {//닫기
				dispose();
				new Admin();//창 닫고, 관리자 창으로 이동
			}
		}
	}
	Color color[] = {Color.red,Color.yellow,Color.blue,Color.magenta};//색상값
	int max;//총 결제수를 저장할 변수
	int values[] = new int[4];//식사 유형별 총 결제 수를 저장할 int형 배열
	String meal[] = {"한식","중식","일식","양식"};//식사 유형
	class ChartGraph extends JPanel{
		String sql = "select sum(orderCount) from orderlist";//식사 유형별로 총 결제 수를 출력할 SQL문
		Connection c = DriverConnector.MakeConnection("mealproject");//데이터베이스 연결
		Statement st[] = new Statement[5];
		ResultSet rs;//실행결과
		ChartGraph(){
			try {
				for(int i = 0; i < st.length; i++) {
					if(i > 0) {
						sql = "select sum(orderCount) from orderlist where cuisineNo = " + i;
					}//i가 1~4일때 식사 유형별 총 결제수를 검색하는 SQL문을 실행
					st[i] = c.createStatement();
					rs = st[i].executeQuery(sql);//결과문 반환
					while(rs.next()) {//결과문 실행
						if(i == 0) {//i가 0일때 총 결제수 저장
							max = rs.getInt("sum(orderCount)");
						}
						else {//i가 1~4일때 식사 유형별 총 결제수 저장
							values[i-1] = rs.getInt("sum(orderCount)");
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void paint(Graphics g) {	//파이차트 그리기
			int angle[] = new int[4];
			int thisangle = 0;
			for(int i = 0; i < color.length; i++) {
				g.setColor(color[i]);
				angle[i] = (int) Math.ceil((values[i]*100.0/max)*3.6);// 유형별 총 결제수/총 결제수로 색을 바꿔가며 차트를 그림
				g.fillArc(50, 50, 350, 350, thisangle, angle[i]);
				thisangle += angle[i];
				g.fillRect(450, 165+(i*20), 15, 15);
				g.setColor(Color.BLACK);
				g.drawString(meal[i] + " (" +values[i] + "건)", 480, 174+(i*20));//유형별 총 결제수를 우측에 출력
			}
		}
	}
}
