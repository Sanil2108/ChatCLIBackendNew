package com.sanilk.chatCliBackend;

import javax.servlet.http.HttpServlet;

public class JDBCController {
	
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL="jdbc:mysql://localhost/phonebook";
	
	final String DB_USER="root";
	final String DB_PASS="root";
	
	final String TABLE_NAME="MAIN";
	
	HttpServlet servlet;
	
	public JDBCController(HttpServlet servlet){
//		try{
//			Class.forName(JDBC_DRIVER);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		this.servlet=servlet;
//		this.servlet.getServletContext().setAttribute("id", 0);
	}
	
	public void newEntry(Client client){
		//Change this later
//		String defaultPass="root";
//		int newId=(int)servlet.getServletContext().getAttribute("id")+1;
//		String username=client.nick;
//		
//		Connection conn=null;
//		Statement stmnt=null;
//		
//		servlet.getServletContext().setAttribute("id", newId);
//		
//		String sql="INSERT INTO TABLE "+TABLE_NAME+" values("+newId+", "+username+", "+defaultPass+");";
//		
//		try{
//			conn=DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
//			stmnt=conn.createStatement();
//			stmnt.executeUpdate(sql);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//			
//		}
	}

}
