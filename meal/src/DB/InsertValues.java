package DB;
import java.sql.*;
import java.util.*;
import java.io.*;
public class InsertValues {
	InsertValues(){
		Connection c = DriverConnector.MakeConnection("mealproject");
		PreparedStatement pst[] = new PreparedStatement[4];
		//String DB = "create database if not exists mealproject";
		String sql[] = {"insert into cuisine values(?,?)","insert into meal values(?,?,?,?,?,?)","insert into member values(?,?,?)",
				"insert into orderlist values(?,?,?,?,?,?,?)"};
		Scanner sc[] = new Scanner[6];
		String input[] = {"cuisine","meal","member","orderlist"};
		try {
			//c.prepareStatement(DB);
			for(int i = 0; i < pst.length; i++) {
				sc[i] = new Scanner(new FileInputStream("C:\\Users\\MS\\Desktop\\meal\\MealProject\\DataFiles\\" + input[i] + ".txt"));
				pst[i] = c.prepareStatement(sql[i]);
				sc[i].nextLine();
				while(sc[i].hasNext()) {
					String line = sc[i].nextLine();
					StringTokenizer st = new StringTokenizer(line, "\t");
					int n = st.countTokens();
					for(int j = 0; j < n; j++) {
						pst[i].setString(j+1, st.nextToken());
					}
					pst[i].executeUpdate();
				}
			}
			//System.out.println("OK, " + pst.length + "rows affected.");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new CreateTable();
		new InsertValues();
	}
}//DB = 총 97라인
