package server;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import org.omg.CORBA.INTERNAL;
import java.time.*;
import com.mysql.cj.exceptions.DataReadException;

import translator.*;
import application.ActiveReportsController;
import application.Evaluation_Options;
import application.MyFile;
import application.Request;
import javafx.print.Collation;
import javafx.util.converter.LocalDateTimeStringConverter;

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
				java.sql.Date date = new java.sql.Date(new java.util.Date().getTime()); // Current Date
				
				stmt = conn.prepareStatement("select request_id from icmdb.processes where initiator_id=? and creation_date=?");
				
				
				stmt = conn.prepareStatement("insert into icmdb.processes_state where status1='Active',request_id=?,date=?");
				stmt.setString(1, nr.getUserID());
				stmt.setDate(2, date);
				// Add new request to processes table in the Data Base:
				//java.sql.Date date = new java.sql.Date(new java.util.Date().getTime()); // Current Date
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
				stmt.setString(9, "1");
				stmt.executeUpdate();
		/******************************************add status process to table*****************************/		
			
				
				
				
/********************************************************end*****************************************/
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
/****************************************************update in process state******************************************************/
				PreparedStatement stmt8 = conn.prepareStatement("insert into  processes_state  (request_id,status1,date)"
						+ "values(?,?,CURRENT_TIMESTAMP)");
				stmt8.setInt(1, processID);
				stmt8.setString(2,"Active");
				stmt8.executeUpdate();
						
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
			break;
			
	/****************************************Get_Active_Statistic********************************************************/		
		case Get_Active_Statistic:
			try {
				
				 ArrayList<LocalDate> dates =( ArrayList<LocalDate>)translator.getParmas().get(0);
				 LocalDate start_date=dates.get(0);
					LocalDate end_date=dates.get(1);
					 ArrayList<Long> days=( ArrayList<Long>)translator.getParmas().get(1);
					 Long num_days=days.get(0);
					 Long daysBetween = ChronoUnit.DAYS.between(start_date, end_date);
					// long startTime = start_date.get
					 //if(daysBetween <num_days)return 0;
					/* if((daysBetween%num_days)==0)
					 {
					 Long size_array=(daysBetween/num_days);
					 }else
					 {
						 Long size_array=(daysBetween/num_days);
						 size_array++;
 
					 }*/
				//	 int size=size_array.intValue();
					 //int num_interval=num_days.intValue();
				//System.out.println("size Arrays"+size);
				//System.out.println("num days interval"+num_interval);
				ArrayList<ArrayList<Integer>> arr=new ArrayList<>();
				ArrayList<Integer>active=new ArrayList<Integer>();
				LocalDate start_index=start_date;
				LocalDate end_index=start_index.plusDays(num_days);
				while(!start_index.isAfter(end_date))
				{
			
				stmt = conn.prepareStatement("SELECT COUNT(*) FROM icmdb.processes_state where status1='Active'"
				
						+"and date>=? and date<?");
								
				//LocalDate local=(LocalDate)translator.getParmas().get(0);
				//LocalDate local_end=(LocalDate)translator.getParmas().get(1);
				Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
				Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
				
				  stmt.setTimestamp(1,start);
				  stmt.setTimestamp(2,end);
				 
				//stmt.setString(2,s_end);
				ResultSet rs = stmt.executeQuery();
				rs.previous();
				int i=0;
				while (rs.next())  // get the processID from the Select query
				{
				active.add(i, rs.getInt(1));
				System.out.println(active.get(i));
				i++;
				}
	
			
			start_index=end_index;
			end_index= end_index.plusDays(num_days);
			}
				
				arr.add(active);
					//System.out.println(arr.get(0));*/
				System.out.println("****************");
			/**************************************************************************************/
				 start_index=start_date;
				 ArrayList<Integer>suspended=new ArrayList<Integer>();
				 end_index=start_index.plusDays(num_days);
				while(!start_index.isAfter(end_date))
				{
			
				stmt = conn.prepareStatement("SELECT COUNT(*) FROM icmdb.processes_state where status1='Suspended'"
				
						+"and date>=? and date<?");
								
				//LocalDate local=(LocalDate)translator.getParmas().get(0);
				//LocalDate local_end=(LocalDate)translator.getParmas().get(1);
				Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
				Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
				
				  stmt.setTimestamp(1,start);
				  stmt.setTimestamp(2,end);
				 
				//stmt.setString(2,s_end);
				ResultSet rs = stmt.executeQuery();
				rs.previous();
				int i=0;
				while (rs.next())  // get the processID from the Select query
				{ 
					System.out.println("suspend"+rs.getInt(1));
					suspended.add(i, rs.getInt(1));
				System.out.println(suspended.get(i));
				i++;
				}
	
			
			start_index=end_index;
			end_index= end_index.plusDays(num_days);
			}
				
				arr.add(suspended);
			//*************************************************************************************
			System.out.println("********************************************");
			
				
			 start_index=start_date;
			 ArrayList<Integer>shutdown=new ArrayList<Integer>();
			 end_index=start_index.plusDays(num_days);
			while(!start_index.isAfter(end_date))
			{
		
			stmt = conn.prepareStatement("SELECT COUNT(*) FROM icmdb.processes_state where status1='Shutdown'"
			
					+"and date>=? and date<?");
							
			//LocalDate local=(LocalDate)translator.getParmas().get(0);
			//LocalDate local_end=(LocalDate)translator.getParmas().get(1);
			Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
			Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
			
			  stmt.setTimestamp(1,start);
			  stmt.setTimestamp(2,end);
			 
			//stmt.setString(2,s_end);
			ResultSet rs = stmt.executeQuery();
			rs.previous();
		int i=0;
			while (rs.next())  // get the processID from the Select query
			{
				System.out.println("shutdown"+rs.getInt(1));
				shutdown.add(i, rs.getInt(1));
				i++;
			}

		
		start_index=end_index;
		end_index= end_index.plusDays(num_days);
		}
			
			arr.add(shutdown);
			/********************************************************************************************/
			
			
			 start_index=start_date;
			 ArrayList<Integer>rejected=new ArrayList<Integer>();
			 end_index=start_index.plusDays(num_days);
			while(!start_index.isAfter(end_date))
			{
		
			stmt = conn.prepareStatement("SELECT COUNT(*) FROM icmdb.processes_state where status1='Rejected'"
			
					+"and date>=? and date<?");
							
			//LocalDate local=(LocalDate)translator.getParmas().get(0);
			//LocalDate local_end=(LocalDate)translator.getParmas().get(1);
			Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
			Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
			
			  stmt.setTimestamp(1,start);
			  stmt.setTimestamp(2,end);
			 
			//stmt.setString(2,s_end);
			ResultSet rs = stmt.executeQuery();
			rs.previous();
			int i=0;
			while (rs.next())  // get the processID from the Select query
			{
				System.out.println("rejected"+rs.getInt(1));
				rejected.add(i, rs.getInt(1));
			//System.out.println(active.get(i));
				i++;
			}

		
		start_index=end_index;
		end_index= end_index.plusDays(num_days);
		}
			
			arr.add(rejected);
			
			/***************************************************************************************/
		/*	System.out.println("**************************");
			
			 start_index=start_date;
			 ArrayList<Integer>TotalDays=new ArrayList<Integer>();
			 ArrayList<Date> CreateDate=new ArrayList<Date>();
			 ArrayList<String> FinalDate=new ArrayList<String>();
			 end_index=start_index.plusDays(num_days);
			
		
		
			/*stmt = conn.prepareStatement("SELECT COUNT(*) FROM icmdb.processes_state where "
			
					+"date>=? and date<? group by workdays");
							*/
				stmt = conn.prepareStatement("SELECT creation_date,IFNULL(current_stage_due_date, 0) FROM icmdb.processes");
			//LocalDate local=(LocalDate)translator.getParmas().get(0);
			//LocalDate local_end=(LocalDate)translator.getParmas().get(1);
			//Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
			//Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
			
			 // stmt.setTimestamp(1,start);
			  //stmt.setTimestamp(2,end);
			 
			//stmt.setString(2,s_end);
			/*ResultSet rs = stmt.executeQuery();
			rs.previous();
			int i=0;
			System.out.println("bfore re.next");
			while (rs.next())  // get the processID from the Select query
			{
				
				CreateDate.add(rs.getDate(1));
				FinalDate.add(rs.getString(2));
				System.out.println(CreateDate.get(i)+" "+CreateDate.get(i));
			//System.out.println(TotalDays.get(i));
				i++;
			}
			ArrayList<LocalDate> final_date= new ArrayList<>();
			ArrayList<LocalDate> create_date= new ArrayList<>();
			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			System.out.println(FinalDate.size()+"FinalDate.size()");
			for( i=0;i<FinalDate.size();i++) {
				System.out.println(FinalDate.get(i)+"FinalDate.get(i)");
				
			//	formatter = formatter.withLocale( FinalDate.get(i) );  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
				//LocalDate date = LocalDate.parse("2005-nov-12", formatter);
				
				 LocalDate fDate = formatter.parseLocalDate(FinalDate.get(i));
				//LocalDate fDate = LocalDate.parse(FinalDate.get(i),formatter);
				final_date.add(fDate);
				System.out.println(fDate+"final date");
				LocalDate date = CreateDate.get(i).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				create_date.add(date);
				System.out.println(create_date+"create_date");
				//System.out.println(final_date.get(i)+""+ create_date.get(i));
			}
			  LocalDate now = LocalDate.now();  
			  int total_days;
			  System.out.println("___________________________________________________");
			for( i=0;i<FinalDate.size();i++)
			{
				if(now.isAfter(final_date.get(i)))
				{
					
					TotalDays.add(total_days = (int) ChronoUnit.DAYS.between(create_date.get(i),final_date.get(i)));
					
				}
				else {
					TotalDays.add(total_days = (int) ChronoUnit.DAYS.between(create_date.get(i),now));
				}
			}

		
	
			
			arr.add(TotalDays);
			
			/***************************************************************************************/
		//0-active 1-suspend 2-shutdown 3-rejected 4-total days
				Translator newTranslator = new Translator(translator.getRequest(), arr);
				return newTranslator;
			}	
			
			catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL EXCEPTION Get_Active_Statistic!");
		}
		break;
	/**********************************************INITIALIZE_COMBO_BOX***********************************************************/
		case INITIALIZE_COMBO_BOX:
		
			try {
				
				stmt = conn.prepareStatement("select first_name, last_name, id from icmdb.workers "						+ "where( id NOT IN(select user_id from icmdb.users_requests) and role ='Information Engineer')");
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
					return new Translator(translator.getRequest(), ar);
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
					if(rs1.getString(1).equals("Chairman") ) {

						ans.add("Chairman");
					}
					if(rs1.getString(1).equals("Change Board Member-1") ) {

						ans.add("Change Board Member-1");
					}
					if(rs1.getString(1).equals("Change Board Member-2") ) {

						ans.add("Change Board Member-2");
					}
					else if(rs1.getString(1).equals("Manager") ){
						ans.add("Manager");
					}
					ans.add(ar.get(0));
					return new Translator(translator.getRequest(), ans);
				}
				ArrayList<String> ans = new ArrayList<String>();
				ans.add("correct match");
				ans.add(ar.get(0));
				return new Translator(translator.getRequest(), ans);

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

					return new Translator(translator.getRequest(), empty);
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
				
				return new Translator(translator.getRequest(), processes);
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

					return new Translator(translator.getRequest(), empty);
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
				
				return  new Translator(translator.getRequest(), workersWithoutRole);
				
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
				setNextStageByOne((int)translator.getParmas().get(1));
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL EXCEPTION on APPOINT_APPRAISER_OR_PERFORMANCE_LEADER");
			}
			break;
		case SET_EVALUATION_OR_EXECUTION_DUE_TIME:
			try {
				//////////////////////////////need to check-not delete/////////////////////////////
			/*	stmt = conn.prepareStatement("UPDATE icmdb.processes_state SET workdays = ? "
						+ "WHERE request_id = ?");
				stmt.setString(1, translator.getParmas().get(1).toString());
				stmt.setInt(2, (int)translator.getParmas().get(0));
				stmt.executeUpdate();*/
				///////////////////////////////////////////
				stmt = conn.prepareStatement("UPDATE icmdb.processes SET current_stage_due_date = ? "
						+ "WHERE request_id = ?");
				
				stmt.setString(1, translator.getParmas().get(1).toString());
				stmt.setInt(2, (int)translator.getParmas().get(0));
				
				stmt.executeUpdate();
				setNextStageByOne((int)translator.getParmas().get(0));
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
				////////////////////////////need to check-not delete//////////
				
				stmt = conn.prepareStatement("UPDATE icmdb.processes_state SET current_stage_due_date = ? "
						+ "WHERE request_id = ?");
				stmt.setString(1, String.valueOf(newDueTime).toString());
				stmt.setInt(2, (int)translator.getParmas().get(0));
				stmt.executeUpdate();
				//////////////////////////////
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
				stmt = conn.prepareStatement("SELECT * FROM icmdb.processes;");
				ResultSet rs = stmt.executeQuery();		
				if(rs.first() == false) {
					ar.add("No processes");
					ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
					empty.add(ar);
					return new Translator(translator.getRequest(), empty);
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
				
				return new Translator(translator.getRequest(), processes);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			break;
			
		case GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC:
			try {
				System.out.println("GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC 1");
				stmt = conn.prepareStatement("SELECT first_name, last_name, id, users_requests.role\r\n" + 
						"FROM icmdb.workers\r\n" + 
						"		JOIN icmdb.users_requests ON id = user_id\r\n" + 
						"						WHERE (users_requests.role = 'Appraiser' OR users_requests.role = 'Performance Leader')\r\n" + 
						"						AND process_id = ?	");
				stmt.setInt(1, (int)translator.getParmas().get(0));
				
				ResultSet rs = stmt.executeQuery();	
				System.out.println("GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC 2");
				if(rs.first() == false) {
					System.out.println("GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC - No employees were found");
					ar.add("No employees were found");
					ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
					empty.add(ar);
					
					return new Translator(translator.getRequest(), empty);
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
				return new Translator(translator.getRequest(), processes);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL Exception GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC");
			}	
			break;	
			
		case DEFROST_PROCESS:
		{
			try {
				java.sql.Date date = new java.sql.Date(new java.util.Date().getTime()); // Current Date



				stmt = conn.prepareStatement("UPDATE processes SET status1='Active' WHERE request_id=?");
				stmt.setString(1, (String) translator.getParmas().get(0));

				int rs = stmt.executeUpdate();
				
				if(rs == 1)
				{
					ar.add("Successfully Defrosted");
	/********************************************************************************/
					
					PreparedStatement stmt9 = conn.prepareStatement("insert into icmdb.processes_state (request_id,status1,date) "
							+"values(?,?,CURRENT_TIMESTAMP)");
					stmt9.setString(1, (String) translator.getParmas().get(0));
					stmt9.setString(2, "Active");
					stmt9.executeUpdate();
					
					
					/******************************************************************************/
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
		case INSERT_FAILURE_REPORT:
			try {
				stmt = conn.prepareStatement("insert into icmdb.failure_reports "
						+ "(failure_report_id,request_id, failure_explanation) "
						+ "values(?,?,?)");
						
				stmt.setInt(1, (int)translator.getParmas().get(0));
				stmt.setInt(2, (int)translator.getParmas().get(1));
				stmt.setString(3, translator.getParmas().get(2).toString());
				
				stmt.executeUpdate();
				setNextStageByInput((int)translator.getParmas().get(1), "7");
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				System.out.println("INSERT_FAILURE_REPORT: SQL EXCEPTION");

			}
			
			break;
			
		case EXAMINATION_COMPLETED:
			setNextStageByOne((int)translator.getParmas().get(0));
			break;
		case REJECTE_PROCESS:
			try {
				
				stmt = conn.prepareStatement("UPDATE processes SET status1='Rejected',process_stage='14' WHERE request_id=?");
				stmt.setString(1, (String) translator.getParmas().get(0));

				int rs = stmt.executeUpdate();
				
				if(rs == 1)
				{
					/*************************************************************************/
					
					PreparedStatement stmt9 = conn.prepareStatement("insert into icmdb.processes_state (request_id,status1,date) "
							+"values(?,?,CURRENT_TIMESTAMP)");
					stmt9.setString(1, (String) translator.getParmas().get(0));
					stmt9.setString(2, "Rejected");
					stmt9.executeUpdate();
					/***********************************************************/
					ar.add("Successfully rejected");
					return new Translator(translator.getRequest(),ar);
				}
				else
				{
					ar.add("Failed To reject");
				}
				
				return new Translator(translator.getRequest(),ar);

			}
		
		catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				ar.add("SQL Error- reject process");
				return new Translator(translator.getRequest(),ar);

			}
			
		case FREEZE_PROCESS:
		{
			try {
				stmt = conn.prepareStatement("UPDATE processes SET status1='Suspended' WHERE request_id=?");
				stmt.setString(1, (String) translator.getParmas().get(0));

				int rs = stmt.executeUpdate();
				
				if(rs == 1)
				{
					
					
					ar.add("Succesfully Suspended");
					
					/********************************************************************************/
					
					PreparedStatement stmt9 = conn.prepareStatement("insert into icmdb.processes_state (request_id,status1,date) "
							+"values(?,?,CURRENT_TIMESTAMP)");
					stmt9.setString(1, (String) translator.getParmas().get(0));
					stmt9.setString(2, "Suspended");
					stmt9.executeUpdate();
					
					
					/******************************************************************************/
					return new Translator(translator.getRequest(),ar);
				}
				else
				{
					ar.add("Failed To Suspend");
				}
		
				return new Translator(translator.getRequest(),ar);


			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				ar.add("SQL Error");
				return new Translator(translator.getRequest(),ar);

			}
		}
		
		case SHUTDOWN_PROCESS:
		{
			try {
				stmt = conn.prepareStatement("UPDATE processes SET status1='Shutdown',process_stage='13' WHERE request_id=?");
				stmt.setString(1, (String) translator.getParmas().get(0));

				int rs = stmt.executeUpdate();
				
				if(rs == 1)
				{
					/*************************************************************************/
					
					PreparedStatement stmt9 = conn.prepareStatement("insert into icmdb.processes_state (request_id,status1,date) "
							+"values(?,?,CURRENT_TIMESTAMP)");
					stmt9.setString(1, (String) translator.getParmas().get(0));
					stmt9.setString(2, "Shutdown");
					stmt9.executeUpdate();
					/***********************************************************/
					ar.add("Successfully Shutdown");
					return new Translator(translator.getRequest(),ar);
				}
				else
				{
					ar.add("Failed To Shutdown");
				}
				
				return new Translator(translator.getRequest(),ar);


			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				ar.add("SQL Error");
				return new Translator(translator.getRequest(),ar);
			}						
		}
			
		case FILL_FAILURE_REPORT_CLICK:
			setNextStageByInput((int)translator.getParmas().get(0), "11.5");
			break;

	case Fill_Evalution_Number_Of_Days: // Appraiser evaluate the required number of days.
			ArrayList<Boolean> evaluateNumberOfDaysAnswer = new ArrayList<>();
			Translator fillNumberOfDaysAnswer = new Translator(OptionsOfAction.Fill_Evalution_Number_Of_Days, evaluateNumberOfDaysAnswer);
			int processID = (int) translator.getParmas().get(0); // The process ID.
			String processStage = translator.getParmas().get(1).toString(); // The process ID.
			if(processStage.equals("2")) {
				try {
					stmt = conn.prepareStatement("insert into icmdb.evaluation_reports "
							+ "(process_id, appraiser_id, number_of_days, approval_result) "
							+ "values(?,?,?,?)");

					stmt.setInt(1, processID); // The process ID.
					stmt.setString(2, translator.getParmas().get(2).toString()); // The Appraiser's ID
					stmt.setInt(3,(int) translator.getParmas().get(3)); // The evaluated number of days
					stmt.setString(4, Evaluation_Options.Waiting.toString()); // Evaluation status (currently waiting)

					stmt.executeUpdate();
					setNextStageByOne(processID); // Process is set to next stage;
					evaluateNumberOfDaysAnswer.add(true);
					return fillNumberOfDaysAnswer;
				}
				catch (SQLException e) {
					e.printStackTrace();
					evaluateNumberOfDaysAnswer.add(false);

					System.out.println("Insert Evaluation Days: SQL EXCEPTION");
					return fillNumberOfDaysAnswer;
				}
			}
			if(processStage.equals("2.5")) {
				try {
					stmt = conn.prepareStatement("Update icmdb.evaluation_reports SET"
							+ " appraiser_id=?, number_of_days=?, approval_result=?"
							+ " WHERE process_id=?");


					stmt.setString(1, translator.getParmas().get(2).toString()); // The Appraiser's ID
					stmt.setInt(2,(int) translator.getParmas().get(3)); // The evaluated number of days
					stmt.setString(3, Evaluation_Options.Waiting.toString()); // Evaluation status (currently waiting)
					stmt.setInt(4, processID); // The process ID.

					stmt.executeUpdate();
					setNextStageByOne(processID); // Process is set to next stage;
					evaluateNumberOfDaysAnswer.add(true);
					return fillNumberOfDaysAnswer;
				}
				catch (SQLException e) {
					//e.printStackTrace();
					evaluateNumberOfDaysAnswer.add(false);

					System.out.println("Insert Evaluation Days: SQL EXCEPTION");
					return fillNumberOfDaysAnswer;
				}
			}
			break;
		case Fill_Evalution_Form: /*******   Fill Evaluation Form (Appraiser) ******/
			int processID1 = (int) translator.getParmas().get(0); // The process ID.
			String processStage1 = translator.getParmas().get(1).toString(); // The process ID.
			ArrayList<Boolean> evaluationFormInserted = new ArrayList<>();
			Translator evaluationFormTranslator = new Translator(OptionsOfAction.Fill_Evalution_Number_Of_Days, evaluationFormInserted);
			try {
				stmt = conn.prepareStatement("UPDATE icmdb.evaluation_reports SET"
						+ "requested_change=?, result=?, constraits_and_risks=?"
						+ " WHERE process_id=?"); // The Requested change

				stmt.setString(1,translator.getParmas().get(1).toString()); // The Requested change
				stmt.setString(2, translator.getParmas().get(2).toString()); // Result
				stmt.setString(3, translator.getParmas().get(3).toString()); // Constraints and risks
				stmt.setInt(4, processID1); // The process ID.
				stmt.executeUpdate();

				setNextStageByOne(processID1); // Process is set to next stage;
				evaluationFormInserted.add(true);
				return evaluationFormTranslator;
			}
			catch (SQLException e) {
				//e.printStackTrace();
				evaluationFormInserted.add(false);

				System.out.println("Insert Evaluation Days: SQL EXCEPTION");
				return evaluationFormTranslator;
			}
			
		case Get_Evaluation_Report_For_Process_ID:
			int procID = (int) translator.getParmas().get(0); // The process ID.
			ArrayList<Object> evaluationForm = new ArrayList<>();
			Translator getEvaluFormTranlator;
			try {
				stmt = conn.prepareStatement("SELECT appraiser_id, requested_change, "
						+ "result, "
						+ "constraits_and_risks FROM icmdb.evaluation_reports"
						+ " WHERE process_id=?;");
				stmt.setInt(1,procID); // The Requested change
				ResultSet rs = stmt.executeQuery();	
				while(rs.next()) {	
					evaluationForm.add(procID); // Process ID
					evaluationForm.add(rs.getString(1)); // Appraiser ID
					evaluationForm.add(rs.getString(2)); // requested change
					evaluationForm.add(rs.getString(3)); // result
					evaluationForm.add(rs.getString(4)); // constraints and risks
					break;
				}
				getEvaluFormTranlator = new Translator(OptionsOfAction.Get_Evaluation_Report_For_Process_ID, evaluationForm);
				return getEvaluFormTranlator;
			} catch (SQLException e) {
				e.printStackTrace();
				evaluationForm.add((int)-1); // Process ID
				getEvaluFormTranlator = new Translator(OptionsOfAction.Get_Evaluation_Report_For_Process_ID, evaluationForm);
				return getEvaluFormTranlator;
			}
			//break;
		case Approve_Decision:
			int processForDecisionAproval = (int) translator.getParmas().get(0); // The process ID. 
			setNextStageByOne(processForDecisionAproval);
			ArrayList<Boolean> decisionApprovalResult = new ArrayList<>();
			decisionApprovalResult.add(true);
			Translator decisionApprovaltranslator = new Translator(
					OptionsOfAction.Approve_Decision, decisionApprovalResult);
			return decisionApprovaltranslator;
			//break;
		case More_Info_Decision:
			int processForDecisionMoreInfo = (int) translator.getParmas().get(0); // The process ID. 
			setNextStageByInput(processForDecisionMoreInfo, "2");
			ArrayList<Boolean> decisionMoreInfoResult = new ArrayList<>();
			decisionMoreInfoResult.add(true);
			Translator decisionMoreInfotranslator = new Translator(
					OptionsOfAction.Approve_Decision, decisionMoreInfoResult);
			return decisionMoreInfotranslator;

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
	
	private static void setNextStageByOne(int procID)//for the "approve" buttons
	{
		PreparedStatement stmt;
		String procStage;
		String nextProcStage = "";
		
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
			
			procStage = rs.getString(1);
			System.out.println("strProcStage = " + procStage);
			
			switch(procStage)
			{
			case "1":
				nextProcStage = "2";
				break;
			case "2":
			case "2.5":
				nextProcStage = "3";
				break;
			case "3":
				nextProcStage = "4";
				break;
			case "4":
				nextProcStage = "5";
				break;
			case "5":
				nextProcStage = "6";
				break;
			case "6":
				nextProcStage = "7";
				break;
			case "7":
			case "7.5":
				nextProcStage = "8";
				break;
			case "8":
				nextProcStage = "9";
				break;
			case "9":
				nextProcStage = "10";
				break;
			case "10":
				nextProcStage = "11";
				break;
			case "11":
			case "11.5":
				nextProcStage = "12";
				break;
			case "12":
				nextProcStage = "13";
				break;
				default:
					break;
			}
			
			
			stmt = conn.prepareStatement("UPDATE icmdb.processes SET process_stage = ? WHERE request_id = ?");
		
			stmt.setString(1, nextProcStage);
			stmt.setInt(2, procID);
			
			stmt.executeUpdate();
			System.out.println("procStage++ = " + nextProcStage);
			
			System.out.println("update process stage by one succeeded");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("SQL Exception setNextStage()");
		}	
	}
	
	private static void setNextStageByInput(int procID, String nextStage)//for the non "approve" buttons
	{
		try
		{
			PreparedStatement stmt;
			
			stmt = conn.prepareStatement("UPDATE icmdb.processes SET process_stage = ? WHERE request_id = ?");
			
			stmt.setString(1, nextStage);
			stmt.setInt(2, procID);
			
			stmt.executeUpdate();
			
			System.out.println("update process stage by input succeeded");

		}
		
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("SQL Exception setNextStageByInput()");
		}	
		
	}
	
	public static ArrayList<ArrayList<?>> getActiveProcesses()
	{
		try {
			ArrayList<String> ar = new ArrayList<String>() ;

			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM icmdb.processes WHERE status1='Active';");
			ResultSet rs = stmt.executeQuery();		
			if(rs.first() == false) {
				ar.add("No processes");
				ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
				empty.add(ar);
				return null;
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
				processes.add(stringArray);;

			}
			
			return processes;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	
	}
}
