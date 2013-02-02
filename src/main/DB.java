package main;

import java.sql.*;

public class DB
{
	private static Connection con;
	private static Statement statement;

	//init the db connection
	public static void init(String pw)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/NekoDB", "root", pw);
			statement = con.createStatement();
		} catch (Exception e) { }
	}
	
	//execute a query the doesnt return a resultset
	public static synchronized void exec(String query) throws SQLException
	{
		statement.execute(query);
	}
	
	//execute a query that returns a resultset
	public static synchronized ResultSet get(String query) throws SQLException
	{
		return statement.executeQuery(query);
	}
}
