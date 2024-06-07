package GUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import DB.DriverConnector;
import java.io.*;
import java.sql.*;
public class AddMenu extends JFrame{
	String Menu[] = {"음료","푸드","상품"};
	String sql;
	JTextField jt[] = new JTextField[2];
	JComboBox jc;
	ImageIcon img; Image image;//이미지를 저장할 이미지 변수
	PreparedStatement pst;
	Connection c = DriverConnector.MakeConnection("cafe");//데이터베이스 연결
	Picture picture = new Picture();
	String filePath = null;//불러온 이미지 경로를 저장할 String형 변수
	AddMenu(){
		setTitle("메뉴추가");
		add(new AddMenuInfo(),BorderLayout.CENTER);
		add(new OKCancel(),BorderLayout.SOUTH);
		add(picture,BorderLayout.EAST);//이미지 띄워질 패널을 오른쪽에 부착
		this.addWindowListener(new back());
		setSize(330,220);
		setVisible(true);
	}
	class back extends WindowAdapter{//X를 누를 경우 바로 종료하지 않고 뒤로 가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new Admin();
		}
	}
	class AddMenuInfo extends JPanel{
		AddMenuInfo(){
			setLayout(new BorderLayout());
			add(new AddMenuInfoLabel(),BorderLayout.WEST);
			add(new AddMenuInfoCenter(),BorderLayout.CENTER);
		}
		class AddMenuInfoLabel extends JPanel{//콤보박스, 텍스트필드 정보를 알려줄 JLabel
			AddMenuInfoLabel(){
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
		class AddMenuInfoCenter extends JPanel{
			AddMenuInfoCenter(){
				setLayout(new GridLayout(3,1,0,0));
				JPanel p[] = new JPanel[3];
				for(int i = 0; i < p.length; i++) {
					p[i] = new JPanel();
					p[i].setLayout(new FlowLayout(FlowLayout.LEFT,20,0));
					if(i == 0) {
						jc = new JComboBox();//메뉴 그룹을 결정할 콤보박스
						p[i].add(jc);
					}
					else {
						jt[i-1] = new JTextField(15);//메뉴명과 가격을 설정할 텍스트필드
						p[i].add(jt[i-1]);
					}
					add(p[i]);
				}
				for(int i = 0; i < Menu.length; i++) {
					jc.addItem(Menu[i]);//콤보박스에 메뉴 추가
				}
			}
		}
	}
	JLabel PictureLabel;//상품 이미지를 담을 JLabel
	class Picture extends JPanel{
		Picture(){
			setLayout(new BorderLayout());
			PictureLabel = new JLabel();
			add(PictureLabel,BorderLayout.CENTER);
			PictureLabel.setBorder(new LineBorder(Color.BLACK));
			JButton Picture = new JButton("사진 등록");
			add(Picture,BorderLayout.SOUTH);
			Picture.addActionListener(new ActionListener() {//사진 등록 버튼 클릭
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
	public void MenuImage() {//메뉴 사진이 등록된것을 먼저 확인하는 메소드
		Image saveImg;//저장할 이미지
		try {
			saveImg = ImageIO.read(new File(filePath));//이미지 파일 가져오기
			Image reimg = saveImg.getScaledInstance(300, 313, Image.SCALE_SMOOTH);//이미지 크기 조정
			BufferedImage newImage = new BufferedImage(300, 313, BufferedImage.TYPE_INT_RGB);//저장할 이미지 생성
			Graphics g = newImage.getGraphics();
			g.drawImage(reimg, 0, 0, null);//이미지 파일 그리기
			g.dispose();
			ImageIO.write(newImage,"jpg", new File("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\이미지\\"
					+ jt[0].getText() + ".jpg"));//현재 파일 위치에 저장할 경로를 지정하여 덮어씌운다.
			addMenu();//등록된게 확인 되면 새 메뉴 등록 실행
		} catch (Exception e) {//이미지가 등록되지 않았을 때 처리하는 에러메시지
			JOptionPane.showMessageDialog(null,"이미지가 등록되지 않았습니다.","상품 등록 실패",JOptionPane.ERROR_MESSAGE);
		}
	}
	class OKCancel extends JPanel{//상품 등록, 취소
		OKCancel(){
			JButton OKCancel[] = new JButton[2];//상품등록, 취소 버튼
			String str[] = {"확인","취소"};
			for(int i = 0; i < OKCancel.length; i++) {
				OKCancel[i] = new JButton(str[i]);
				add(OKCancel[i]);
				OKCancel[i].addActionListener(new ActionListener() {//상품등록, 취소를 클릭 시 액션 이벤트
					@Override
					public void actionPerformed(ActionEvent e){
						// TODO Auto-generated method stub
						if(e.getActionCommand().equals(str[0])) {//확인 버튼 클릭시
							if(jt[0].getText().equals("") || jt[1].getText().equals("")) {//빈칸 처리
								JOptionPane.showMessageDialog(null,"빈 칸을 모두 입력하세요","상품 등록 실패",JOptionPane.ERROR_MESSAGE);
							}
							else {
								try {
									int price = Integer.parseInt(jt[1].getText());//JTextField에서 가격을 받아옴
									if(price < 1000) {//1000원 이하일때 등록 불가
										JOptionPane.showMessageDialog(null,"상품의 최소가격은 1000원 입니다.","상품 등록 실패",JOptionPane.ERROR_MESSAGE);
										jt[1].setText(null);
									}
									else {
										MenuImage();//메뉴 사진이 등록된것을 먼저 확인하고 새 메뉴 등록을 실행한다.
									}
								}
								catch(NumberFormatException e1) {
									JOptionPane.showMessageDialog(null,"가격은 숫자만 입력할 수 있습니다.","상품 등록 실패",JOptionPane.ERROR_MESSAGE);
									jt[1].setText(null);
								}
							}
						}
						else {
							dispose();
							new Admin();
						}
					}
				});
			}
		}
	}
	private void addMenu() {//메뉴 중복 여부 확인
		String selectMenu = jt[0].getText();//등록할 메뉴 이름
		sql = "select * from menu where m_name = ?";//해당 메뉴 이름을 검색하는 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, selectMenu);
			ResultSet rs = pst.executeQuery();//SQL문 실행
			if(rs.next()) {//중복되는 메뉴명이 존재
				JOptionPane.showMessageDialog(null,"이미 존재하는 메뉴명입니다.","상품 등록 실패",JOptionPane.ERROR_MESSAGE);
			}
			else {//중복되는 메뉴명이 존재하지 않음
				insertMenu();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void insertMenu() {//메뉴 등록하기
		sql = "insert into menu(m_group, m_name, m_price) values(?,?,?)";//해당 메뉴를 등록하는 SQL문
		try {
			pst = c.prepareStatement(sql);
			pst.setString(1, String.valueOf(jc.getSelectedItem()));//메뉴 그룹
			pst.setString(2, jt[0].getText());//메뉴명
			pst.setInt(3, Integer.parseInt(jt[1].getText()));//메뉴 가격
			pst.executeUpdate();//SQL문 실행
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null,"메뉴가 등록되었습니다.","상품 등록 성공",JOptionPane.PLAIN_MESSAGE);
		dispose();
		new Admin();//메뉴 등록이 성공하면 창을 닫고 다시 관리자 페이지로 이동
	}
}
