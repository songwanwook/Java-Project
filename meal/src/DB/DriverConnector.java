package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DriverConnector {
	public static Connection MakeConnection(String db) {
		String id = "root";
		String pass = "1234";
		String url = "";
		Connection con = null;
		
		if(db.equals(""))
		url = "jdbc:mysql://localhost/";
		else
		url = "jdbc:mysql://localhost/" + db;

		try {
		
			Class.forName("com.mysql.cj.jdbc.Driver");
			//System.out.println("Driver Connected");
			con = DriverManager.getConnection(url, id, pass);
			//System.out.println("Connection succeed");
		}
		catch(ClassNotFoundException e) {
			//System.out.println("Not found driver");
		}
		catch(SQLException e) {
			//System.out.println("Connection failed");
			e.printStackTrace();
		}
		return con;
	}
//	public static void main(String[] args) {
//		Connection con = MakeConnection("mealproject");
//	}
}
