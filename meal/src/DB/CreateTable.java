package DB;
import java.sql.*;
public class CreateTable {
	CreateTable(){
		Connection c = DriverConnector.MakeConnection("");
		Statement st0;
		String dropDatabase = "drop database if exists mealproject";
		try {
			st0 = c.createStatement();
			st0.executeUpdate(dropDatabase);
			//System.out.println("Database Droped");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println("Query Failed");
			e1.printStackTrace();
		}
		
		String createDatabase = "create database if not exists mealproject";
		try {
			st0 = c.createStatement();
			st0.executeUpdate(createDatabase);
			//System.out.println("Database Created");
			c = DriverConnector.MakeConnection("mealproject");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("Query Failed");
		}
		Statement st[] = new Statement[4];
		String sql[] = {"create table member(memberNo int not null auto_increment primary key, memberName varchar(25), passwd varchar(4))",
		"create table cuisine(cuisineNo int not null auto_increment primary key, cuisineName varchar(10))",
		"create table meal(mealNo int not null auto_increment primary key, cuisineNo int, mealName varchar(25), price int, maxCount int,"
		+ " todayMeal int)",
		"create table orderlist(orderNo int not null auto_increment primary key, cuisineNo int, mealNo int,"
		+ " memberNo int, orderCount int, amount int, orderDate datetime)"};
		try {
			for(int i = 0; i < sql.length; i++) {
				st[i] = c.createStatement();
				st[i].executeUpdate(sql[i]);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	/*public static void main(String[] args) {
		new CreateTable();
	}*/
}
//데이터베이스 자동 세팅하게 수정.