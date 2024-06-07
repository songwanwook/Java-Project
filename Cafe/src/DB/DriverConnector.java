package DB;
import java.sql.*;
public class DriverConnector {
	public static Connection MakeConnection(String db) {
		String info[] = {"root","1234"};
		String url = "";
		Connection connection = null;
		
		if(db.equals("")) 
			url = "jdbc:mysql://localhost/";
		else 
			url = "jdbc:mysql://localhost/" + db;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(url, info[0], info[1]);
		}
		catch(ClassNotFoundException e) {
			System.out.println("Not found driver");
			e.printStackTrace();
		}
		catch(SQLException e) {
			System.out.println("Connection failed");
			e.printStackTrace();
		}
		return connection;
	}
}
