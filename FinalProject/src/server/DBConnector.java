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
		case "check login":
			try {
				
				stmt = conn.prepareStatement("select * from users where id=?");	
				stmt.setString(1, data.get(1));
				ResultSet rs = stmt.executeQuery();
				if(rs.first() == false) {
					ar.add("The username does not exist");
					return ar;
				}
				rs.previous();
				while(rs.next())
				{					    
					    ar.add(rs.getString(1));
					    ar.add(rs.getString(2));
				}
				if(ar.get(1).equals(data.get(2)) ) {
					ArrayList<String> ans = new ArrayList<String>();
					ans.add("correct match");
					ans.add(ar.get(0));
					return ans;
				}
				ar.clear();
				ar.add(0, "Login failed, username and password did not match");
				return ar;
				}
			catch (SQLException e) {
				System.out.println("ERROR");
				e.printStackTrace();} 
			return null;
			
		case "get all related requests":
			try {
				
				stmt = conn.prepareStatement(""
					  + "SELECT users_requests.idProcess, users_requests.role, processes.* \r\n" + 
						"FROM users_requests\r\n" + 
						"INNER JOIN processes\r\n" + 
						"ON users_requests.idProcess = processes.request_id"+
						"WHERE users_requests.user=?");
				stmt.setString(1, data.get(1));
				ResultSet rs = stmt.executeQuery();				
				if(rs.first() == false) {
					ar.add("The user has no related process");
					return ar;
				}
				rs.previous();
				
				

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		
			
			break;
		default:
			System.out.println("default");
			break;
		}
		return null;
			
	  }

}
