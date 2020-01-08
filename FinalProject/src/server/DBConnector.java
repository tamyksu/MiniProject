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

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import translator.*;
import application.MyFile;
import application.Request;
import application.UserProcess;
import client.Client;
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
		Translator translator = (Translator) data;
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
				
				// Add the new request ID with it's initator' Id to users_request 

						
	// ***************************** Recieve Files from Client and insert them to Data Base
				PreparedStatement stmt3 = conn.prepareStatement("insert into  users_requests (user_id,process_id,"
						+ "role)"
						+ "values(?,?,?)");
				stmt3.setString(1, nr.getUserID());
				stmt3.setInt(2, processID);
				stmt3.setString(3, "Initiator");
				stmt3.executeUpdate();


				// ***************************** Receive Files from Client and insert them to Data Base

				ArrayList<MyFile> filesToServer = (ArrayList<MyFile>) translator.getParmas().get(1);

				// Add every file from client to the Data Base
				for(int i=0;i<filesToServer.size();i++) {


					MyFile myfile = filesToServer.get(i);
					String newFileNamePath = ".\\Files_Server_Recieved\\"+processID+"_"+myfile.getFileName();

					FileOutputStream fos = new FileOutputStream(newFileNamePath);
					BufferedOutputStream bos = new BufferedOutputStream(fos);

					PreparedStatement stmt4 =conn.prepareStatement("insert into icmdb.files (request_id, user_id ,file) values(?,?,?)"); 
					stmt4.setInt(1,processID);
					stmt4.setString(2, nr.getUserID().toString());
					stmt4.setString(3, newFileNamePath);

					stmt4.executeUpdate();
					stmt4.close();

					/* The following code can save another version of the file in 
					 * the project's directory:*/
					bos.write(myfile.mybytearray, 0, myfile.getSize());
					bos.flush();
					fos.flush();
				}

				// ***************************** End of Receive Files from Client and insert them to Data Base 

				success.add(new Boolean(true));
				answer = new Translator(OptionsOfAction.NEWREQUEST,success);
				return answer;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL EXCEPTION!");
				failed.add(new Boolean(false));
				answer = new Translator(OptionsOfAction.NEWREQUEST,failed);
				return answer;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//	break;
	/***************************************INITIALIZE_COMBO_BOX***********************************************/		
		case INITIALIZE_COMBO_BOX:
		
			try {
				
				stmt = conn.prepareStatement("select first_name, last_name, id from icmdb.workers "
						+ "where( id NOT IN(select user_id from icmdb.users_requests))");
						//+ "and id NOT IN(select user_id from icmdb.permanent_roles))");
				
						
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
					System.out.println("SQL EXCEPTION INITIALIZE_COMBO_BOX!");
			}
			break;
			
		case checkNAMEParmenent:
		try {

			stmt = conn.prepareStatement("SELECT count(*) FROM icmdb.permanent_roles where user_id=? ");//1- this person already in posion
			stmt.setString(1, (String) translator.getParmas().get(0));
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
		
		
			ar.add( new String(rs.getString(1)));//result person if 1 or 0
			ar.add((String)translator.getParmas().get(0));

			Translator newTranslator = new Translator(translator.getRequest(), ar);
			return newTranslator;
		}
			catch(SQLException e)
			{
				System.out.println("SQL EXCEPTION checkNAMEParmenent!");
			}
			break;
	/*****************************************checkDB********************************************************/	
		case checkDB:
			try {
				
			
			stmt = conn.prepareStatement("SELECT count(*) FROM icmdb.permanent_roles where role=? ");
			stmt.setString(1, (String) translator.getParmas().get(0).toString());
			ResultSet rs = stmt.executeQuery();
			rs.next();
		
			ar.add( new String(rs.getString(1)));//result if 1 or 0
			ar.add((String)translator.getParmas().get(0).toString());//role
			ar.add((String)translator.getParmas().get(1).toString());//option
			Translator newTranslator = new Translator(translator.getRequest(), ar);
			return newTranslator;
		}
			catch(SQLException e)
			{
				System.out.println("SQL EXCEPTION checkDB!");
			}
			break;
	/***********************************************CHECK_ROLE**********************************************************/
	case DELETEPERMANENT:
			try {
				stmt = conn.prepareStatement("select role from icmdb.permanent_roles where role=? or user_id=? ");
				stmt.setString(1, (String) translator.getParmas().get(0).toString());
				stmt.setString(2, (String) translator.getParmas().get(0).toString());
				ResultSet rs = stmt.executeQuery();
				rs.previous();
				while (rs.next())  // get the processID from the Select query
					ar.add( new String(rs.getString(1)));//get role
				
		
				stmt = conn.prepareStatement("delete from icmdb.permanent_roles where role=? or user_id=? ");
				stmt.setString(1, (String) translator.getParmas().get(0).toString());
				stmt.setString(2, (String) translator.getParmas().get(0).toString());
				
				stmt.executeUpdate();	
		
				Translator newTranslator = new Translator(translator.getRequest(), ar);
				return newTranslator;
			
			}
			catch(SQLException e)
			{
				System.out.println("SQL EXCEPTION DELETEPERMANENT!");
				
			}
			break;
/*********************************************UPDATEPERMANENT*********************************************************/		
	case CURRENT_IN_ROLE:
		
	try{
		
		stmt = conn.prepareStatement("select first_name, last_name from icmdb.workers "
				+ "inner join icmdb.permanent_roles ON( icmdb.workers.id=icmdb.permanent_roles.user_id )"
				+ "and icmdb.permanent_roles.role = ? ");
		stmt.setString(1, (String) translator.getParmas().get(0));
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
	
			
		}
				ar.add( (String) translator.getParmas().get(0));//role
		
		Translator newTranslator = new Translator(translator.getRequest(), ar);
		return newTranslator;
	}
	catch(SQLException e) {
		System.out.println("SQL Exception: Failed CURRENT_IN_ROLE ");
	}
	
	break;
	case UPDATEPERMANENT:
		try {
			
			System.out.println("add new person to role");
			stmt = conn.prepareStatement("insert into icmdb.permanent_roles (user_id,role) values(?,?)");
			stmt.setString(1, (String) translator.getParmas().get(0));
			stmt.setString(2, (String) translator.getParmas().get(1));
System.out.println("id "+translator.getParmas().get(0));

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
		break;
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
					else if(rs1.getString(1).equals("Manager") ){
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
				e.printStackTrace();
				
			} 
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
			
		case GET_APPRAISER_AND_PERFORMANCE_LEADER_CB_DATA:
			System.out.println("made it1");
			try {

				stmt = conn.prepareStatement("SELECT DISTINCT id, first_name, last_name\r\n" + 
						"FROM icmdb.workers\r\n" + 
						"LEFT JOIN icmdb.users_requests\r\n" + 
						"ON users_requests.user_id = workers.id\r\n" + 
						"WHERE (id NOT IN (SELECT user_id\r\n" + 
						"							FROM icmdb.users_requests\r\n" + 
						"							WHERE process_id = ?) "
						+ "							OR id NOT IN (SELECT users_requests.user_id FROM icmdb.users_requests)) "
						+ "AND id NOT IN (SELECT user_id FROM icmdb.permanent_roles)");
				
				
				stmt.setInt(1, (int)translator.getParmas().get(0));
				ResultSet rs = stmt.executeQuery();		
				System.out.println("made it2");

				if(rs.first() == false) {
					System.out.println("No appraisers or performance leaders to appoint");
					ar.add("No Appraisers To Appoint");
					ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
					empty.add(ar);

					Translator newTranslator = new Translator(translator.getRequest(), empty);

					return newTranslator;
				}
				rs.previous();
				System.out.println("Yes appraisers or performance leaders");

				ArrayList<String> workersWithoutRole = new ArrayList<String>();
				while(rs.next()) {	
					workersWithoutRole.add(rs.getString(1));
					workersWithoutRole.add(rs.getString(2));
					workersWithoutRole.add(rs.getString(3));
				}
				System.out.println("workersWithoutRole:");
				System.out.println(workersWithoutRole);
				Translator newTranslator = new Translator(translator.getRequest(), workersWithoutRole);
				return newTranslator;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Catch");
				e.printStackTrace();
			}
			break;
		case APPOINT_APPRAISER_OR_PERFORMANCE_LEADER:
				
				try {
					stmt = conn.prepareStatement("INSERT INTO users_requests(user_id, process_id, role)" +
				"VALUES (?, ?, ?)");
					stmt.setString(1, translator.getParmas().get(0).toString());
					stmt.setInt(2, (int)translator.getParmas().get(1));
					stmt.setString(3, translator.getParmas().get(2).toString());
					
					stmt.executeUpdate();
					
					System.out.println("APPOINT_APPRAISER_OR_PERFORMANCE_LEADER Insert is working");
					setNextStage((int)translator.getParmas().get(1));
				}
				catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("SQL EXCEPTION on APPOINT_APPRAISER_OR_PERFORMANCE_LEADER");
				}
			break;
		case SET_EVALUATION_OR_EXECUTION_DUE_TIME:
			try {
				stmt = conn.prepareStatement("UPDATE icmdb.processes SET current_stage_due_date = ? "
						+ "WHERE request_id = ?");
				
				stmt.setString(1, translator.getParmas().get(1).toString());
				stmt.setInt(2, (int)translator.getParmas().get(0));
				
				stmt.executeUpdate();
				setNextStage((int)translator.getParmas().get(0));
			}
			catch(SQLException e) {
				 //TODO Auto-generated catch block
				System.out.println("Catch SET_EVALUATION_OR_EXECUTION_DUE_TIME");
			}
			break;
		case ADD_EVALUATION_OR_EXECUTION_EXTENSION_TIME:
			try {
				stmt = conn.prepareStatement("SELECT current_stage_due_date FROM icmdb.processes WHERE request_id = ?");
				stmt.setInt(1, (int)translator.getParmas().get(0));
				
				ResultSet rs = stmt.executeQuery();		
				
				//System.out.println("HERE 1");
				
				if(rs.first() == false) {
					System.out.println("No Process Was Found");
					return null;
				}
				//System.out.println("SSSS");
				rs.previous();

				String currentDueTime="";
				//System.out.println("HERE 2");

				while(rs.next()) {
					currentDueTime = rs.getString(1);
					}
				System.out.println(currentDueTime);
				int newDueTime = Integer.parseInt(currentDueTime);
				
				newDueTime += (int)translator.getParmas().get(1);
				
				stmt = conn.prepareStatement("UPDATE icmdb.processes SET current_stage_due_date = ? "
						+ "WHERE request_id = ?");
				
				stmt.setString(1, String.valueOf(newDueTime).toString());
				stmt.setInt(2, (int)translator.getParmas().get(0));
				
				stmt.executeUpdate();
				System.out.println("Due Time Update Was Succeeded!");
			}
			catch(SQLException e) {
				 //TODO Auto-generated catch block
				System.out.println("Catch ADD_EVALUATION_OR_EXECUTION_EXTENSION_TIME");
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
			
		case GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC:
			try {
				System.out.println("GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC 1");
				stmt = conn.prepareStatement("SELECT first_name, last_name, id, role\r\n" + 
						"FROM icmdb.workers\r\n" + 
						"		JOIN icmdb.users_requests ON id = user_id\r\n" + 
						"						WHERE (role = 'Appraiser' OR role = 'Performance Leader')\r\n" + 
						"						AND process_id = ?	");
				stmt.setInt(1, (int)translator.getParmas().get(0));
				
				ResultSet rs = stmt.executeQuery();	
				System.out.println("GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC 2");
				if(rs.first() == false) {
					System.out.println("GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC - No employees were found");
					ar.add("No employees were found");
					ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
					empty.add(ar);
					Translator newTranslator = new Translator(translator.getRequest(), empty);
					return newTranslator;
				}
				rs.previous();
				ArrayList<Object> processes = new ArrayList<Object>();
			
				processes.add(String.valueOf((int)translator.getParmas().get(0)));
				
				while(rs.next()) {	
					processes.add(rs.getString(1));
					processes.add(rs.getString(2));
					processes.add(rs.getString(4));
				}
				System.out.println("OMG");
				System.out.println(processes);
				Translator newTranslator = new Translator(translator.getRequest(), processes);
				return newTranslator;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL Exception GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC");
			}	
			break;	
			
		case DEFROST_PROCESS:
		{
			try {
				stmt = conn.prepareStatement("UPDATE processes SET status1='Active' WHERE request_id=?");
				stmt.setString(1, (String) translator.getParmas().get(0));

				int rs = stmt.executeUpdate();
				
				if(rs == 1)
				{
					ar.add("Succesfully Defrosted");
					return new Translator(translator.getRequest(),ar);
				}
				else
				{
					ar.add("Failed To Defrosted");
				}
				
				return new Translator(translator.getRequest(),ar);


			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				ar.add("SQL Error");
				return new Translator(translator.getRequest(),ar);

			}

		}
		
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
				stmt1 = conn.prepareStatement(""
						+ "SELECT * FROM workers\r\n" + 
						"WHERE workers.id=?");
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
	
	private static void setNextStage(int procID)
	{
		PreparedStatement stmt;
		String strProcStage;
		int numProcStage;
		
		System.out.println("procID: " + procID);
		try {
			stmt = conn.prepareStatement("SELECT process_stage FROM icmdb.processes\r\n" + 
					"					WHERE request_id = ?");
			
			stmt.setInt(1, procID);
			
			ResultSet rs = stmt.executeQuery();	
			System.out.println("SET NEXT STAGE 1");
			if(rs.first() == false) {
				System.out.println("setNextStage() Failed - There is no process ID %d" + procID);
				return;
			}
			
			strProcStage = rs.getString(1);
			System.out.println("strProcStage = " + strProcStage);
			
			numProcStage = Integer.parseInt(strProcStage);
			numProcStage++;
			strProcStage = String.valueOf(numProcStage);
			
			stmt = conn.prepareStatement("UPDATE icmdb.processes SET process_stage = ? WHERE request_id = ?");
		
			stmt.setString(1, strProcStage);
			stmt.setInt(2, procID);
			
			stmt.executeUpdate();
			System.out.println("strProcStage++ = " + strProcStage);
			
			System.out.println("update process stage succeeded");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("SQL Exception setNextStage()");
		}	
	}
}
