package DB;
import java.sql.*;
import java.util.*;
import java.io.*;
public class InsertValues {
	InsertValues(){
		Connection c = DriverConnector.MakeConnection("Cafe");
		PreparedStatement pst[] = new PreparedStatement[3];
		String sql[] = {"insert into menu values(?,?,?,?);",
				"insert into user values(?,?,?,?,?,?,?);",
				"insert into orderlist values(?,?,?,?,?,?,?,?,?);"};
		String input[] = {"menu.txt", "user.txt", "orderlist.txt"};
		Scanner sc[] = new Scanner[3];
		for(int i = 0; i < pst.length; i++) {
			try {
				sc[i] = new Scanner(new FileInputStream("C:\\Users\\MS\\Desktop\\CafeProject\\Cafe\\DataFiles\\" + input[i]));
				pst[i] = c.prepareStatement(sql[i]); 
				sc[i].nextLine();
				while(sc[i].hasNextLine()) {
					String line = sc[i].nextLine();
					StringTokenizer st = new StringTokenizer(line, "\t");
					int n = st.countTokens();
					for(int j = 0; j < n; j++) {
						String str = st.nextToken();
						System.out.println(str + " ");
						if((i == 2 && j == 5) && (str.equals("L"))) {
							pst[i].setString(j+1,"L");
						}
						else if((i == 2 && j == 5) && (!str.equals("L"))) {
							pst[i].setString(j+1,"M");
						}
						else {
							pst[i].setString(j+1, str);
						}
					}
					pst[i].executeUpdate();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		new CreateTable();
		new InsertValues();
	}
}
