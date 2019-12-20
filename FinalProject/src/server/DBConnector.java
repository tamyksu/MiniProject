package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import application.Processes;
import application.UserProcess;
import sun.net.www.content.text.plain;

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
			
	
	  protected static Object accessToDB(ArrayList<String> data)
	  {
		
		PreparedStatement stmt;
		ArrayList<String> ar=new ArrayList<String>();//return the result of the select query
		switch (data.get(0)) {
		case "check login":
			try {
				
				stmt = conn.prepareStatement("select * from users where user_id=?");	
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
					  + "SELECT users_requests.process_id, users_requests.role, processes.* \r\n" + 
						"FROM users_requests\r\n" + 
						"INNER JOIN processes\r\n" + 
						"ON users_requests.process_id = processes.request_id\r\n"+
						"WHERE users_requests.user_id=?");
				stmt.setString(1, data.get(1));
				ResultSet rs = stmt.executeQuery();				
				if(rs.first() == false) {
					ar.add("The user has no related process");
					return ar;
				}
				rs.previous();
				HashMap<Integer, UserProcess> processesHashMap = new HashMap<Integer, UserProcess>();
				while(rs.next()) {	
					UserProcess process = new UserProcess();
					process.setRole(rs.getString(2));
					process.setIntiatorId(rs.getString(4));
					process.setSystem_num(rs.getInt(5));
					process.setProblem_description(rs.getString(6));
					process.setRequest_description(rs.getString(7));
					process.setExplanaton(rs.getString(8));
					process.setNotes(rs.getString(9));
					process.setStatus(rs.getString(10));
					process.setCreation_date(rs.getDate(11));
					process.setHandler_id(rs.getString(12));
					process.setProcess_stage(rs.getString(13));
					process.setCurrent_stage_due_date(rs.getString(14));
					processesHashMap.put(rs.getInt(1), process);
				}
				Processes processes = new Processes();
				processes.setMyProcess(processesHashMap);
				return processes;
				

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
