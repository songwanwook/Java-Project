package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Admin extends JFrame{//관리자 메뉴
	Admin(){
		setLayout(new GridLayout(3,1));
		JButton btn[] = new JButton[3];
		String str[] = {"메뉴 등록","메뉴 관리","로그아웃"};
		for(int i = 0; i < btn.length; i++) {
			btn[i] = new JButton(str[i]);
			setTitle("관리자 메뉴");
			btn[i].addActionListener(new ActionListener() {//관리자 버튼 이벤트리스너
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String command = e.getActionCommand();
					switch(command) {
						case"메뉴 등록":new AddMenu();break;//메뉴 등록 실행
						case"메뉴 관리":new UpdateMenu();break;//메뉴 관리 실행
						case"로그아웃":new Main(); break;//로그아웃
					}
					dispose();
				}
			});
			add(btn[i]);
			setSize(270,150);
		}
		setVisible(true);
		this.addWindowFocusListener(new back());
	}
	class back extends WindowAdapter{//X를 누를 경우 바로 종료하지 않고 뒤로 가기
		public void windowClosing(WindowEvent e) {
			dispose();
			new Main();
		}
	}
}
