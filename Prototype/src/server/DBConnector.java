package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
public class DBConnector {
	
	  private static Connection conn;
	
	  protected static void establishDBConnection() {
			try 
			{
		      Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		      System.out.println("Driver definition succeed");
		    } 
			catch (Exception ex) {
		  	/* handle the error*/
		  	 System.out.println("Driver definition failed");
		  	}
			
			try 
		    {
		        conn = DriverManager.getConnection("jdbc:mysql://localhost/icmdb?serverTimezone=IST","root","Aa123456");
		        System.out.println("SQL connection succeed");
		        
		 	} catch (SQLException ex) 
		 	    {/* handle any errors*/
		        System.out.println("SQLException: " + ex.getMessage());
		        System.out.println("SQLState: " + ex.getSQLState());
		        System.out.println("VendorError: " + ex.getErrorCode());
		        }
			}
			
	
	  protected static ArrayList<String> accessToDB(ArrayList<String> data)
	  {
		
		PreparedStatement stmt;
		ArrayList<String> ar=new ArrayList<String>();//return the result of the select query
		switch (data.get(0)) {
		case "table":
			try {
				stmt = conn.prepareStatement("select request_id,status from requirement");
				ResultSet rs = stmt.executeQuery();
				if(rs.first() == false) {
					ar.add("failed select");
					return ar;
				}
				rs.previous();
				while(rs.next())
				{					    
					    ar.add(rs.getString(1));
					    ar.add(rs.getString(2));
				}
				ar.add(0, "successful table select");
				return ar;
				}
			catch (SQLException e) {
				System.out.println("2");
				System.out.println("ERROR");
				e.printStackTrace();} 
			return null;
		case "insert":
			try {
				stmt = conn.prepareStatement("insert into requirement (initiator_name,system_num,current_status,request_description,status,handler_name) values(?,?,?,?,?,?)");
				stmt.setString(1, data.get(1));
				stmt.setString(2, data.get(2));
				stmt.setString(3, data.get(3));
				stmt.setString(4, data.get(4));
				stmt.setString(5, data.get(5));
				stmt.setString(6, data.get(6));
				stmt.executeUpdate();
				ar.add(0, "successful insert");
				return ar;
			} catch (SQLException e) {
				System.out.println("ERROR");
				e.printStackTrace();} 
			return null;
		case "select":
			
			try {
				stmt = conn.prepareStatement("select * from requirement where request_id=?");
				int idConvert=Integer.parseInt(data.get(1).trim());
				stmt.setInt(1,idConvert);
				ResultSet rs = stmt.executeQuery();
				if(rs.first() == false) {
					ar.add("failed select");
					return ar;
				}
				rs.previous();
				while(rs.next())
				{					    
					    ar.add(rs.getString(1));
					    ar.add(rs.getString(2));
					    ar.add(rs.getString(3));
					    ar.add(rs.getString(4));
					    ar.add(rs.getString(5));
					    ar.add(rs.getString(6));
					    ar.add(rs.getString(7));
				}
				
				ar.add(0, "successful select");
				return ar;
				}
			catch (SQLException e) {
				System.out.println("2");
				System.out.println("ERROR");
				e.printStackTrace();} 
			return null;
			
		case "update":
			try {
				
				stmt = conn.prepareStatement("update requirement  set status=? where request_id=?");
				int idConvert=Integer.parseInt(data.get(1).trim());
				stmt.setInt(2,idConvert);
				stmt.setString(1, data.get(2));
				stmt.executeUpdate();
				ar.add("successful update");
				return ar;
			} catch (SQLException e) {
				System.out.println("ERROR");
				e.printStackTrace();} 
			return null;
			
		default:
			break;
		}
		return null;
			
	  }

}
