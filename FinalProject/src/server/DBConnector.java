package server;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import translator.*;
import application.MyFile;
import application.Request;
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

	public static Object accessToDB(Object data) {
		Translator translator =(Translator)data;
		PreparedStatement stmt;
		ArrayList<String> ar = new ArrayList<String>() ;
		
		
		
		
		switch (translator.getRequest()) {
		case NEWREQUEST:
		
			ArrayList<Boolean> failed = new ArrayList<Boolean>();
			ArrayList<Boolean> success = new ArrayList<Boolean>();
			Translator answer;
			
			Request nr = (Request) translator.getParmas().get(0);
			try {
				
				// Add new request to processes table in the Data Base:
				java.sql.Date date = new java.sql.Date(new java.util.Date().getTime()); // Current Date
				stmt = conn.prepareStatement("insert into icmdb.processes (initiator_id,system_num,"
						+ "problem_description,"
						+ "		request_description,explanaton,"
						+ "notes,status1,creation_date, process_stage) values(?,?,?,?,?,?,?,?,?)");
				stmt.setString(1, nr.getUserID());
				stmt.setInt(2, nr.getInformationSystemNumber());
				stmt.setString(3, nr.getProblemDescription());
				stmt.setString(4, nr.getRequestDescription());
				stmt.setString(5, nr.getExplanation());
				stmt.setString(6, nr.getNotes());
				stmt.setString(7, "Active");
				stmt.setDate(8, date);
				stmt.setInt(9, 1);
				stmt.executeUpdate();

				// Get the ID newly inserted request:
				try {
					PreparedStatement stmt2 = conn.prepareStatement("select request_id from processes where"
							+ " initiator_id=? and system_num=? and "
							+ "problem_description=? and request_description=? "
							+ "and explanaton=? and notes=?");	
					stmt2.setString(1, nr.getUserID());
					stmt2.setInt(2, nr.getInformationSystemNumber());
					stmt2.setString(3, nr.getProblemDescription());
					stmt2.setString(4, nr.getRequestDescription());
					stmt2.setString(5, nr.getExplanation());
					stmt2.setString(6, nr.getNotes());

					ResultSet rs = stmt2.executeQuery();
					int processID=0;
					while (rs.next()) { // get the processID from the Select query
						processID = rs.getInt("request_id");
						break;
					}
					System.out.println(processID);
					// Add the new request ID with it's initator' Id to users_request 
					try {
						PreparedStatement stmt3 = conn.prepareStatement("insert into  users_requests (user_id,process_id,"
								+ "role)"
								+ "values(?,?,?)");
						stmt3.setString(1, nr.getUserID());
						stmt3.setInt(2, processID);
						stmt3.setString(3, "Initiator");
						stmt3.executeUpdate();

						
						// ***************************** Recieve Files from Client and insert them to Data Base

						/*
						 *  PreparedStatement ps=conn.prepareStatement("insert into icmdb.files (request_id, file) values(?,?)"); 
					        ps.setInt(1,1);
					        ps.setBinaryStream(2, fis);

					        ps.executeUpdate();
						    ps.close();
						 */
						ArrayList<MyFile> filesToServer = (ArrayList<MyFile>) translator.getParmas().get(1);

						// Add every file from client to the Data Base
						for(int i=0;i<filesToServer.size();i++) {
							try {
								
								MyFile myfile = filesToServer.get(i);
								String newFileName = ".\\Files_Server_Recieved\\"+processID+"_"+myfile.getFileName();

								FileOutputStream fos = new FileOutputStream(newFileName);
								BufferedOutputStream bos = new BufferedOutputStream(fos);

								try {

									PreparedStatement stmt4 =conn.prepareStatement("insert into icmdb.files (request_id, file) values(?,?)"); 
									stmt4.setInt(1,processID);
									stmt4.setString(2, newFileName);
									
									
									
									/*PreparedStatement stmt4 =conn.prepareStatement("insert into icmdb.files (request_id, file) values(?,?)"); 
									stmt4.setInt(1,processID);
									stmt4.setBytes(2, myfile.mybytearray);
									// The File is saved as array of bytes in the database
									// BLOB*/
									
									stmt4.executeUpdate();
									stmt4.close();
									
									/* The following code can save another version of the file in 
									 * the project's directory:*/
									bos.write(myfile.mybytearray, 0, myfile.getSize());
									bos.flush();
									fos.flush();
									
								}
								catch (SQLException e) {
									// TODO Auto-generated catch block
									System.out.println("SQL EXCEPTION: Can't insert the file");
									failed.add(new Boolean(false));
									answer = new Translator(OptionsOfAction.NEWREQUEST,failed);
									return answer;
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								System.out.println("Error while sacing file");
								failed.add(new Boolean(false));
								answer = new Translator(OptionsOfAction.NEWREQUEST,failed);
								return answer;
							}
						}



						// ***************************** End of Recieve Files from Client and insert them to Data Base 

					}
					catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("SQL EXCEPTION on 3rd query");
						failed.add(new Boolean(false));
						answer = new Translator(OptionsOfAction.NEWREQUEST,failed);
						return answer;
					}
				}
				catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("SQL EXCEPTION!");
					failed.add(new Boolean(false));
					answer = new Translator(OptionsOfAction.NEWREQUEST,failed);
					return answer;
					
				}

				System.out.println("Insert is working!!!");
				
				success.add(new Boolean(true));
				answer = new Translator(OptionsOfAction.NEWREQUEST,success);
				return answer;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL EXCEPTION!");
				failed.add(new Boolean(false));
				answer = new Translator(OptionsOfAction.NEWREQUEST,failed);
				return answer;
			}
			
		case SELECTCHAIRMAN:
		
			try {
				stmt = conn.prepareStatement("select first_name, last_name, id from icmdb.workers "
						+ "where( id SNOT IN(select user_id from icmdb.users_requests))"
						+ "AND id NOT IN (select user_id from icmdb.permanent_roles)");
				ResultSet rs = stmt.executeQuery();
				String nameWorker;
				if(rs.first() == false) {
					ar.add("Select chairman failled");
					Translator newTranslator = new Translator(translator.getRequest(), ar);
					return newTranslator;
				}
				rs.previous();
				while (rs.next()) { // get the processID from the Select query
					ar.add( new String(rs.getString(1)));
					ar.add( new String(rs.getString(2)));
					ar.add( new String(rs.getString(3)));
				}
			
				Translator newTranslator = new Translator(translator.getRequest(), ar);
				return newTranslator;
				
				}
			catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("SQL EXCEPTION SELECTCHAIRMAN!");
			}
		
/*********************************************UPDATEPERMANENT*********************************************************/		
		case UPDATEPERMANENT:
		try {
			stmt = conn.prepareStatement("insert into icmdb.permanent_roles (user_id,role) values(?,?)");
			stmt.setString(1, (String) translator.getParmas().get(0));
			stmt.setString(2, (String) translator.getParmas().get(1));
			
			System.out.println((String) translator.getParmas().get(0));

			System.out.println((String) translator.getParmas().get(1));

			stmt.executeUpdate();
			stmt = conn.prepareStatement("select first_name, last_name from icmdb.workers "
					+ "inner join icmdb.permanent_roles ON icmdb.workers.id=icmdb.permanent_roles.user_id "
					+ "and icmdb.permanent_roles.role = ? ");
			stmt.setString(1, (String) translator.getParmas().get(1));
			ResultSet rs = stmt.executeQuery();
	
			if(rs.first() == false) {
				ar.add("Select chairman failled");
				Translator newTranslator = new Translator(translator.getRequest(), ar);
				return newTranslator;
			}
			rs.previous();
			while (rs.next()) { // get the processID from the Select query
				ar.add( new String(rs.getString(1)));//name
				ar.add( new String(rs.getString(2)));//last name
				ar.add( (String) translator.getParmas().get(1));//role
				
			}
		
			Translator newTranslator = new Translator(translator.getRequest(), ar);
			return newTranslator;
			
		}
		catch(SQLException e) {
			System.out.println("SQL Exception: Failed UPDATEPERMANENT ");
		}
/*************************************************LOGIN***************************************************************/
		case LOGIN:
			try {
				stmt = conn.prepareStatement("select * from users where user_id=? and password=?");	
				stmt.setString(1, (String) translator.getParmas().get(0));
				stmt.setString(2, (String) translator.getParmas().get(1));

				ResultSet rs = stmt.executeQuery();
				if(rs.first() == false) {
					ar.add("Login failed, username and password did not match");
					Translator newTranslator = new Translator(translator.getRequest(), ar);
					return newTranslator;
				}
				rs.previous();
				while(rs.next())
				{					    
					ar.add(rs.getString(1));
				}
				stmt = conn.prepareStatement("select role from permanent_roles where user_id=?");	
				stmt.setString(1, ar.get(0));
				ResultSet rs1 = stmt.executeQuery();
				if(rs1.first() != false) {
					rs1.previous();
					rs1.next();
					ArrayList<String> ans = new ArrayList<String>();
					if(rs1.getString(1).equals("Supervisor") ) {
						ans.add("Supervisor");
					}
					else if(rs1.getString(1).equals( "Manager") ){
						ans.add("Manager");
					}
					ans.add(ar.get(0));
					Translator newTranslator = new Translator(translator.getRequest(), ans);
					return newTranslator;
				}
				ArrayList<String> ans = new ArrayList<String>();
				ans.add("correct match");
				ans.add(ar.get(0));
				Translator newTranslator = new Translator(translator.getRequest(), ans);
				return newTranslator;
			}
			catch (SQLException e) {
				System.out.println("ERROR");
				e.printStackTrace();} 
			return null;

		case GETRELATEDREQUESTS:
			try {
				stmt = conn.prepareStatement(""
						+ "SELECT users_requests.process_id, users_requests.role, processes.* \r\n" + 
						"FROM users_requests\r\n" + 
						"INNER JOIN processes\r\n" + 
						"ON users_requests.process_id = processes.request_id\r\n"+
						"WHERE users_requests.user_id=?");
				stmt.setString(1, (String) translator.getParmas().get(0));
				ResultSet rs = stmt.executeQuery();		
				if(rs.first() == false) {
					ar.add("No processes");
					ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
					empty.add(ar);

					Translator newTranslator = new Translator(translator.getRequest(), empty);

					return newTranslator;
				}
				rs.previous();
				ArrayList<ArrayList<?>> processes = new ArrayList<ArrayList<?>>();
				while(rs.next()) {	
					ArrayList<Integer> intArray= new ArrayList<Integer>();
					ArrayList<String> stringArray= new ArrayList<String>();
					intArray.add(rs.getInt(1));
					stringArray.add(rs.getString(2));
					stringArray.add(rs.getString(4));
					intArray.add(rs.getInt(5));
					stringArray.add(rs.getString(6));
					stringArray.add(rs.getString(7));
					stringArray.add(rs.getString(8));
					stringArray.add(rs.getString(9));
					stringArray.add(rs.getString(10));
					stringArray.add(rs.getString(11));
					stringArray.add(rs.getString(12));
					stringArray.add(rs.getString(13));
					stringArray.add(rs.getString(14));
					ResultSet initiatorInfo = getInitiatorInfo(rs.getString(4));
					if (initiatorInfo != null) {
						while(initiatorInfo.next()) {
							stringArray.add(initiatorInfo.getString(3));
							stringArray.add(initiatorInfo.getString(4));
							stringArray.add(initiatorInfo.getString(5));
							stringArray.add(initiatorInfo.getString(6));
						}
					}
					else {
						return null;
					}
					processes.add(intArray);
					processes.add(stringArray);
				}
				Translator newTranslator = new Translator(translator.getRequest(), processes);
				return newTranslator;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			break;
			
		case GETALLPROCESSES:
			try {
				stmt = conn.prepareStatement("SELECT * FROM processes;");
				ResultSet rs = stmt.executeQuery();		
				if(rs.first() == false) {
					ar.add("No processes");
					ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
					empty.add(ar);
					Translator newTranslator = new Translator(translator.getRequest(), empty);
					return newTranslator;
				}
				rs.previous();
				ArrayList<ArrayList<?>> processes = new ArrayList<ArrayList<?>>();
				while(rs.next()) {	
					ArrayList<Integer> intArray= new ArrayList<Integer>();
					ArrayList<String> stringArray= new ArrayList<String>();
					intArray.add(rs.getInt(1));
					stringArray.add(rs.getString(2));
					intArray.add(rs.getInt(3));
					stringArray.add(rs.getString(4));
					stringArray.add(rs.getString(5));
					stringArray.add(rs.getString(6));
					stringArray.add(rs.getString(7));
					stringArray.add(rs.getString(8));
					stringArray.add(rs.getString(9));
					stringArray.add(rs.getString(10));
					stringArray.add(rs.getString(11));
					stringArray.add(rs.getString(12));
					ResultSet initiatorInfo = getInitiatorInfo(rs.getString(2));
					if (initiatorInfo != null) {
						while(initiatorInfo.next()) {
							stringArray.add(initiatorInfo.getString(3));
							stringArray.add(initiatorInfo.getString(4));
							stringArray.add(initiatorInfo.getString(5));
							stringArray.add(initiatorInfo.getString(6));
						}
					}
					else {
						return null;
					}
					processes.add(intArray);
					processes.add(stringArray);
				}
				Translator newTranslator = new Translator(translator.getRequest(), processes);
				return newTranslator;
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

	//Another function aimed at getting the initiator information from the database
	protected static ResultSet getInitiatorInfo(String initiatorId)  {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement(""
					+ "SELECT * FROM students\r\n" + 
					"WHERE students.id=?");
			stmt.setString(1, initiatorId);
			ResultSet rs1 = stmt.executeQuery();
			if(rs1.first() == false) {
				PreparedStatement stmt1;
				stmt1 = conn.prepareStatement("SELECT * FROM icmdb.workers where id=?");
				stmt1.setString(1, initiatorId);
				ResultSet rs2 = stmt1.executeQuery();
				if(rs2.first() == false) return null;
				rs2.previous();
				return rs2;
			}
			rs1.previous();

			return rs1;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The SQL query of initiator info has faile;");
		}
		return null;
	}
	
}
