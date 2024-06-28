package DB;
import java.sql.*;
public class CreateAdmin {
	CreateAdmin(){
		Connection c = DriverConnector.MakeConnection();
		Statement st[] = new Statement[3];
		String sql[] = {"create table admin(name varchar(25) not null, passwd varchar(25) not null, position varchar(25), "
				+ "resident char(14), inputdate date, primary key(name, passwd))",
				"create table contract(customerCode char(7) not null, contractName varchar(25) not null, regPrice int, "
				+ "regDate date not null, monthPrice int, adminName varchar(25) not null)",
				"create table customer(code char(7) not null, name varchar(25) not null, birth date, tel varchar(25),"
				+ "address varchar(100), company varchar(25))"};
		try {
			for(int i = 0; i < st.length; i++) {
				st[i] = c.createStatement();
				st[i].executeUpdate(sql[i]);
			}
			System.out.println("success " + sql.length + " Tables affected.");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	//public static void main(String[] args) {
		//new CreateAdmin();
	//}
}
