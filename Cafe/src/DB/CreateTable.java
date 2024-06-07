package DB;
import java.sql.*;
public class CreateTable {
	CreateTable(){
		Connection c = DriverConnector.MakeConnection("");
		Statement st;
		String refresh = "drop database if exists Cafe";
		try {
			st = c.createStatement();
			st.executeUpdate(refresh);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		String create = "create database if not exists Cafe";
		try {
			st = c.createStatement();
			st.executeUpdate(create);
			c = DriverConnector.MakeConnection("Cafe");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Statement tablest[] = new Statement[4];
		String CreateTable[] = {"create table menu(m_no int primary key not null auto_increment, m_group varchar(10), m_name varchar(30), m_price int);",
				"create table user(u_no int primary key not null auto_increment, u_id varchar(20), "
				+ "u_pw varchar(4), u_name varchar(5), u_bd varchar(14), u_point int(11), u_grade varchar(10));"
				,"create table orderlist(o_no int primary key auto_increment, o_date date, u_no int, m_no int, "
				+ "o_group varchar(10), o_size varchar(1), o_price int, o_count int, o_amount int, "
				+ "foreign key(u_no) references user(u_no), foreign key(m_no) references menu(m_no));",
				"create table shopping(s_no int primary key not null auto_increment, m_no int, s_price int, s_count int, s_size varchar(1), s_amount int, u_no int)"
		};
		for(int i = 0; i < tablest.length; i++) {
			try {
				tablest[i] = c.createStatement();
				tablest[i].executeUpdate(CreateTable[i]);
				System.out.println(i+1 + " table");
			} catch (SQLException e) {
				System.out.println("SQL error");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}
