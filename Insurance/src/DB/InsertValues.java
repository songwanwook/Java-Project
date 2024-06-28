package DB;
import java.sql.*;
import java.util.*;
import java.io.*;
public class InsertValues {
	InsertValues(){
		Connection c = DriverConnector.MakeConnection();
		PreparedStatement pst[] = new PreparedStatement[3];
		String sql[] = {"insert into admin values(?,?,?,?,?)","insert into contract values(?,?,?,?,?,?)",
				"insert into customer values(?,?,?,?,?,?)"};
		Scanner sc[] = new Scanner[3];
		String InputTxt[] = {"admin","contract","customer"};
		try {
			for(int i = 0; i < pst.length; i++) {
				sc[i] = new Scanner(new FileInputStream("txt\\" + InputTxt[i] + ".txt"));
				pst[i] = c.prepareStatement(sql[i]);
				sc[i].nextLine();
				while(sc[i].hasNext()) {
					String line = sc[i].nextLine();
					StringTokenizer stk = new StringTokenizer(line, "\t");
					int n = stk.countTokens();
					for(int j = 0; j < n; j++) {
						pst[i].setString(j+1, stk.nextToken());
					}
					pst[i].executeUpdate();
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*public static void main(String args[]) {
		new InsertValues();
	}*/
}
