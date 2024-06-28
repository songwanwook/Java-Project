package DB;
import java.sql.*;
public class DriverConnector {
	public static Connection MakeConnection() {
		String url = "jdbc:mysql://localhost/customer?characterEncoding=utf8";
		String id = "root";
		String pass = "1234";
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			//System.out.println("Driver Connection");
			con = DriverManager.getConnection(url, id, pass);
			//System.out.println("Connection successed");
		}
		catch(ClassNotFoundException e) {
			System.out.println("Not found driver");
		}
		catch(SQLException e) {
			System.out.println("Connection failed");
			e.printStackTrace();
		}
		return con;	
	}
	public static void main(String[] args) {
		Connection con = MakeConnection();
		new CreateAdmin();
		new InsertValues();
	}
}
