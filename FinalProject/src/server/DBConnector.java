package server;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
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
import java.util.concurrent.TimeUnit;
import translator.*;
import application.ActiveReportsController;
import application.Evaluation_Options;
import application.MyFile;
import application.Request;
import javafx.print.Collation;
import javafx.util.converter.LocalDateTimeStringConverter;
import application.MyHashMaps;

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
				System.out.println(e.getMessage());
				failed.add(new Boolean(false));
				answer = new Translator(OptionsOfAction.NEWREQUEST,failed);
				return answer;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case SelectExtensionReport:
			try {
				System.out.println("SelectExtensionReport");
				ArrayList<Integer> days= new ArrayList<Integer>();
				ArrayList<Integer> count_request= new ArrayList<Integer>();
			
				stmt= conn.prepareStatement("select DISTINCT number_days_extension from icmdb.extension_report order by number_days_extension");
				ResultSet rs = stmt.executeQuery();
				rs.previous();
				int i=0;
				while (rs.next())  // get the processID from the Select query
				{ 
					days.add(rs.getInt(1));
				}
				
				 stmt =conn.prepareStatement("select  count(*) from icmdb.extension_report group by number_days_extension order by number_days_extension");
				 rs = stmt.executeQuery();
				rs.previous();
					
					while (rs.next())  // get the processID from the Select query
					{ 
						count_request.add(rs.getInt(1));
					}
				ArrayList<ArrayList<Integer>> all =new ArrayList<ArrayList<Integer>>();
				all.add(days);
				all.add(count_request);
					Translator newTranslator = new Translator(translator.getRequest(),all);
					return newTranslator;
				}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL EXCEPTION SelectDelayReport!");
				
					}
			break;
			
			
		case SelectDelayReport:
			try {
				ArrayList<Integer> days_delay= new ArrayList<Integer>();
				ArrayList<Integer> count_request_delay= new ArrayList<Integer>();
			
				stmt= conn.prepareStatement("select DISTINCT number_days_delay from icmdb.delay_reports order by number_days_delay");
				ResultSet rs = stmt.executeQuery();
				rs.previous();
				int i=0;
				while (rs.next())  // get the processID from the Select query
				{ 
					days_delay.add(rs.getInt(1));
				}
				
				 stmt =conn.prepareStatement("select  count(*) from icmdb.delay_reports group by number_days_delay order by number_days_delay");
				 rs = stmt.executeQuery();
				rs.previous();
					
					while (rs.next())  // get the processID from the Select query
					{ 
						count_request_delay.add(rs.getInt(1));
					}
				ArrayList<ArrayList<Integer>> all =new ArrayList<ArrayList<Integer>>();
				all.add(days_delay);
				all.add(count_request_delay);
					Translator newTranslator = new Translator(translator.getRequest(),all);
					return newTranslator;
				}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("SQL EXCEPTION SelectDelayReport!");
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
			
				ArrayList<ArrayList<Integer>> arr=new ArrayList<>();
				ArrayList<Integer>active=new ArrayList<Integer>();
				LocalDate start_index=start_date;
				LocalDate end_index=start_index.plusDays(num_days);
				while(!start_index.isAfter(end_date))
				{
			
				stmt = conn.prepareStatement("SELECT COUNT(*) FROM icmdb.processes_state where status1='Active'"
				
						+"and date>=? and date<?");
								
			
				Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
				Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
				
				  stmt.setTimestamp(1,start);
				  stmt.setTimestamp(2,end);
			
				ResultSet rs = stmt.executeQuery();
				rs.previous();
				int i=0;
				while (rs.next())  // get the processID from the Select query
				{
				active.add(i, rs.getInt(1));
			
				i++;
				}
	
			
			start_index=end_index;
			end_index= end_index.plusDays(num_days);
			}
				
				arr.add(active);
		
			/**************************************************************************************/
				 start_index=start_date;
				 ArrayList<Integer>suspended=new ArrayList<Integer>();
				 end_index=start_index.plusDays(num_days);
				while(!start_index.isAfter(end_date))
				{
			
				stmt = conn.prepareStatement("SELECT COUNT(*) FROM icmdb.processes_state where status1='Suspended'"
				
						+"and date>=? and date<?");
	
				Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
				Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
				
				  stmt.setTimestamp(1,start);
				  stmt.setTimestamp(2,end);
				 
			
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
							
		
			Timestamp start=Timestamp.valueOf(start_index.atTime(LocalTime.MIDNIGHT));
			Timestamp end=Timestamp.valueOf(end_index.atTime(LocalTime.MIDNIGHT));
			
			  stmt.setTimestamp(1,start);
			  stmt.setTimestamp(2,end);
			 
	
			ResultSet rs = stmt.executeQuery();
			rs.previous();
			int i=0;
			while (rs.next())  // get the processID from the Select query
			{
				System.out.println("rejected"+rs.getInt(1));
				rejected.add(i, rs.getInt(1));
	
				i++;
			}

		
		start_index=end_index;
		end_index= end_index.plusDays(num_days);
		}
			
			arr.add(rejected);
/*********************************************************************************/	
			 start_index=start_date;
			 ArrayList<Integer>TotalDays=new ArrayList<Integer>();
			 ArrayList<Date> CreateDate=new ArrayList<Date>();
			 ArrayList<String> FinalDate=new ArrayList<String>();
			 end_index=start_index.plusDays(num_days);
			 
		
	
				stmt = conn.prepareStatement("SELECT creation_date,IFNULL(current_stage_due_date, 0) FROM icmdb.processes");
	
			ResultSet rs = stmt.executeQuery();
			rs.previous();
			int i=0;
			System.out.println("bfore re.next");
			while (rs.next())  // get the processID from the Select query
			{
				
				CreateDate.add(rs.getDate(1));
				FinalDate.add(rs.getString(2));
			
			//	i++;
			}
			ArrayList<LocalDate> final_date= new ArrayList<>();
			ArrayList<LocalDate> create_date= new ArrayList<>();
	
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			System.out.println(FinalDate.size()+"FinalDate.size()");//convert to local date
			for( i=0;i<FinalDate.size();i++) {
		
			
				 LocalDate fDate = LocalDate.parse(FinalDate.get(i),formatter);
				final_date.add(fDate);
	

			}
			
		
			
			 LocalDate now = LocalDate.now();  
			  long total_days;
	
			  
			 Date f_Date = null;
			
			for( i=0;i<FinalDate.size();i++)
			{
				try {
					f_Date =(Date) new  SimpleDateFormat("yyyy-MM-dd").parse(FinalDate.get(i));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				if(now.isAfter(final_date.get(i)))
				{
					
					Date satrt_date=CreateDate.get(i);
					long startTime = satrt_date.getTime();
					long endTime = f_Date.getTime();
					long diffTime = endTime - startTime;
					long diffDays = diffTime / (1000 * 60 * 60 * 24);
					System.out.println(diffDays);
					System.out.println(f_Date.getTime()+ " "+satrt_date.getTime());
							int convert_to_int=(int)diffDays;
							System.out.println(convert_to_int+"now we after end date");
							TotalDays.add(convert_to_int);
					
				}
				else {
					
					Date satrt_date=CreateDate.get(i);
					
					Date nowGet =new Date();
					
					long startTime = satrt_date.getTime();
					long endTime = nowGet.getTime();
					long diffTime = endTime - startTime;
					long diffDays = diffTime / (1000 * 60 * 60 * 24);
					
							int convert_to_int=(int)diffDays;
				
							TotalDays.add(convert_to_int);
					
				}
			}
			

			int arr_counter[] =new int[	Collections.max(TotalDays)];
	
			for( i=0;i<arr_counter.length;i++)
			{
				arr_counter[i]=0;
			}
		
			for( i=0;i<TotalDays.size();i++)
			{
				
				arr_counter[TotalDays.get(i)-1]=arr_counter[TotalDays.get(i)-1]+1;
			
			}
			ArrayList<Integer> counter_arr=new ArrayList<>();
			ArrayList<Integer> total_days_arr=new ArrayList<>();
			for( i=0;i<Collections.max(TotalDays);i++)
			{
				if(arr_counter[i]!=0)
				{
					counter_arr.add((arr_counter[i]));
					total_days_arr.add(i+1);
				}
				
			}
	
				
				arr.add(counter_arr);
				arr.add(total_days_arr);
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
				
				stmt = conn.prepareStatement("select first_name, last_name, id from icmdb.workers "	
				+ "where( id NOT IN(select user_id from icmdb.users_requests) "
				+ "and role ='Information Engineer')");
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
		stmt = conn.prepareStatement("select user_id from icmdb.permanent_roles "
			+"where role=?");
			stmt.setString(1, (String) translator.getParmas().get(0));
		//stmt.setString(1, (String) translator.getParmas().get(0));
		ResultSet rs = stmt.executeQuery();
		rs.previous();
		String id="";
		while (rs.next()) { // get the user id
			 id= new String(rs.getString(1));//
			
		}
		stmt = conn.prepareStatement("select first_name, last_name from icmdb.workers "
				+ "where icmdb.workers.id = ?");
				
		stmt.setString(1, id);
		 rs = stmt.executeQuery();
System.out.println(id+"id current in role");
		if(rs.first() == false) {
			ar.add("Select chairman failled");
			Translator newTranslator = new Translator(translator.getRequest(), ar);
			return newTranslator;
		}
		rs.previous();
		while (rs.next()) { // get the processID from the Select query
			ar.add( new String(rs.getString(1)));//name
			ar.add( new String(rs.getString(2)));//last name
	System.out.println("%%");
			
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
			
			
			stmt = conn.prepareStatement("insert into icmdb.permanent_roles (user_id,role) values(?,?)");
			stmt.setString(1, (String) translator.getParmas().get(0));
			stmt.setString(2, (String) translator.getParmas().get(1));
System.out.println("id "+translator.getParmas().get(0));

			stmt.executeUpdate();
			stmt = conn.prepareStatement("select first_name, last_name from icmdb.workers "
					+ "where icmdb.workers.id=? ");
					
		
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
					processes.add(getRelatedFilesName(rs.getInt(1)));
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
				java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
				/*********************tamy***********************************/
				
				stmt = conn.prepareStatement(" insert into icmdb.extension_report (request_id,number_days_extension) values(?,?)");
				stmt.setInt(1,  (int)translator.getParmas().get(0));
				stmt.setInt(2, (Integer.parseInt(translator.getParmas().get(1).toString())));
				stmt.executeUpdate();
				
				/*************************tamy********************************/
			
				date = date.valueOf(date.toLocalDate().plusDays(Integer.parseInt(translator.getParmas().get(1).toString())));
				
				System.out.println("SET_EVALUATION_OR_EXECUTION_DUE_TIME 1");
				
				stmt = conn.prepareStatement("UPDATE icmdb.processes SET current_stage_due_date = ? "
						+ "WHERE request_id = ?");
				
				stmt.setDate(1, date);
				stmt.setInt(2, (int)translator.getParmas().get(0));
				
				stmt.executeUpdate();
				
				System.out.println("SET_EVALUATION_OR_EXECUTION_DUE_TIME 2");

				
				String toWho="";
				if((int)translator.getParmas().get(2) == 3)
					toWho = "Appraiser";
				else
					toWho = "Performance Leader";
				
				sendNotification((int)translator.getParmas().get(0), "execution due stage time was approved", 
						Integer.parseInt(translator.getParmas().get(1).toString()),
						toWho, "Supervisor", null);
				
				deleteNotification((int)translator.getParmas().get(0), "define execution stage due time",
						Integer.parseInt(translator.getParmas().get(1).toString()), "Supervisor", toWho);
				
				setNextStageByOne((int)translator.getParmas().get(0));
			}
			catch(SQLException e) {
				 //TODO Auto-generated catch block
				System.out.println("Catch SET_EVALUATION_OR_EXECUTION_DUE_TIME");
				System.out.println(e.getMessage());
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

				
				//System.out.println("HERE 2");
				java.sql.Date date = null;
				
				while(rs.next()) {
					date = rs.getDate(1);
					}
				System.out.println(date);
				/*********************tamy***********************************/
				
				
			     stmt = conn.prepareStatement(" select number_days_extension from  icmdb.extension_report where request_id=?");
			     stmt.setInt(1, (int)translator.getParmas().get(0));
			      rs = stmt.executeQuery();
			     rs.previous();
			     int days=0;
			     while(rs.next()) {
						days = rs.getInt(1);
						}
			     days+=(int)translator.getParmas().get(1);
			     
				stmt = conn.prepareStatement(" update icmdb.extension_report set  number_days_extension=?"
						+ "where request_id =?");
				stmt.setInt(1, days);
				stmt.setInt(2, (int)translator.getParmas().get(0));
				stmt.executeUpdate();
				
		/*************************tamy********************************/
				
				date = date.valueOf(date.toLocalDate().plusDays((int)translator.getParmas().get(1)));
			
				stmt = conn.prepareStatement("UPDATE icmdb.processes SET current_stage_due_date = ? "
						+ "WHERE request_id = ?");
				
				stmt.setDate(1, date);
				stmt.setInt(2, (int)translator.getParmas().get(0));
				
				stmt.executeUpdate();
				System.out.println("Extension Due Time Update Was Succeeded!");
				
				String toWho="";
				String procStage = translator.getParmas().get(2).toString();
				
				switch(procStage)
				{
				case "4.5":
					procStage = "4.6";
					toWho = "Appraiser";
					break;
				case "5.5":
					procStage = "5.6";
					toWho = "Chairman";
					break;
				case "9.5":
					procStage = "9.6";
					toWho = "Performance Leader";
					break;
				case "11.2":
					procStage = "11.3";
					toWho = "Examiner";
					break;
				case "11.5":
					procStage = "11.6";
					toWho = "Examiner";
					break;
				}
				
				sendNotification((int)translator.getParmas().get(0), "due time extension was added", (int)translator.getParmas().get(1),
						toWho, "Supervisor", null);
				deleteNotification((int)translator.getParmas().get(0), "add due time extension", 0,
						"Supervisor", toWho);
				setNextStageByInput((int)translator.getParmas().get(0), procStage);
			}
			catch(SQLException e) {
				 //TODO Auto-generated catch block
				System.out.println("Catch ADD_EVALUATION_OR_EXECUTION_EXTENSION_TIME");
				System.out.println(e.getMessage());
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
					processes.add(getRelatedFilesName(rs.getInt(1)));
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
						+ "(request_id, failure_explanation) "
						+ "values(?,?)");
						
				stmt.setInt(1, (int)translator.getParmas().get(0));
				stmt.setString(2, translator.getParmas().get(1).toString());
				
				stmt.executeUpdate();
				
				System.out.println("INSERT_FAILURE_REPORT - insert failure report succeeded");
				
				setNextStageByInput((int)translator.getParmas().get(0), "7");
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				System.out.println("INSERT_FAILURE_REPORT: SQL EXCEPTION");
				System.out.println(e.getMessage());

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
			
			String pStage = translator.getParmas().get(1).toString();
			
			System.out.println("FILL_FAILURE_REPORT_CLICK: pStage = " + pStage);
			
			switch(pStage)
			{
			case "11":
				pStage = "11.1";
				break;
			case "11.5":
				pStage = "11.2";
				break;

			}
			setNextStageByInput((int)translator.getParmas().get(0), pStage);
			
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
					sendNotification(processID, "define execution stage due time",
							(int) translator.getParmas().get(3), "Supervisor", "Appraiser", null);
					setNextStageByOne(processID); // Process is set to next stage;
					evaluateNumberOfDaysAnswer.add(true);
					
					sendNotification((int)translator.getParmas().get(0), "Evaluation Due time was set by Appraiser",(int)translator.getParmas().get(3) ,
							"Supervisor", "Appraiser", null);
					
					return fillNumberOfDaysAnswer;
					
				}
				catch (SQLException e) {
					e.printStackTrace();
					evaluateNumberOfDaysAnswer.add(false);

					System.out.println("Insert Evaluation Days: SQL EXCEPTION 1");
					System.out.println(e.getMessage());
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
					sendNotification(processID, "define execution stage due time",
							(int) translator.getParmas().get(3), "Supervisor", "Appraiser", null);
					setNextStageByOne(processID); // Process is set to next stage;
					evaluateNumberOfDaysAnswer.add(true);
					
					sendNotification((int)translator.getParmas().get(0), "Evaluation Due time was set by Appraiser",(int)translator.getParmas().get(3) ,
							"Supervisor", "Appraiser", null);
					
					return fillNumberOfDaysAnswer;
				}
				catch (SQLException e) {
					//e.printStackTrace();
					evaluateNumberOfDaysAnswer.add(false);

					System.out.println("Insert Evaluation Days: SQL EXCEPTION 2");
					System.out.println(e.getMessage());
					return fillNumberOfDaysAnswer;
				}
			}
			break;
		case Fill_Evalution_Form: /*******   Fill Evaluation Form (Appraiser) ******/
			int processID1 = (int) translator.getParmas().get(0); // The process ID.
			
			//String processStage1 = translator.getParmas().get(1).toString(); // The process ID.
			ArrayList<Boolean> evaluationFormInserted = new ArrayList<>();
			Translator evaluationFormTranslator = new Translator(OptionsOfAction.Fill_Evalution_Form, evaluationFormInserted);
			try {
				stmt = conn.prepareStatement("UPDATE icmdb.evaluation_reports SET"
						+ " requested_change=?, result=?, constraits_and_risks=?"
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

				System.out.println("Insert Evaluation Days: SQL EXCEPTION 3");
				System.out.println(e.getMessage());
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
			ArrayList<Boolean> decisionMoreInfoResult = new ArrayList<>();
			Translator decisionMoreInfotranslator = new Translator(
					OptionsOfAction.More_Info_Decision, decisionMoreInfoResult);
			try {
				setNextStageByInput(processForDecisionMoreInfo, "2");
				/*
				 *  Delete the row of the Process from the Evaluation Report table,
				 *  because the process is going back to the Evaluation stage
				 *   all over again
				 */
				stmt = conn.prepareStatement("DELETE FROM icmdb.evaluation_reports"
						+ " WHERE process_id=?;");
				stmt.setInt(1,processForDecisionMoreInfo); // The Requested change
				stmt.executeUpdate();	
				

				
				decisionMoreInfoResult.add(true);
				
				return decisionMoreInfotranslator;
			}
			catch (SQLException e) {
				decisionMoreInfoResult.add(false);
				return decisionMoreInfotranslator;
			}
		
		case Execution_Suggest_Number_Of_Days:
			ArrayList<Boolean> executeNumberOfDaysAnswer = new ArrayList<>();
			Translator executeNumberOfDaysTranslator = new Translator(OptionsOfAction.Execution_Suggest_Number_Of_Days, executeNumberOfDaysAnswer);
			int processID2 = (int) translator.getParmas().get(0); // The process ID.
			String processStageExec = translator.getParmas().get(1).toString(); // The process stage.
			if(processStageExec.equals("7")) {
				try {
					stmt = conn.prepareStatement("insert into icmdb.execution "
							+ "(process_id, executor_id, number_of_days) "
							+ "values(?,?,?)");
					/**    I stopped here!!!   **/
					stmt.setInt(1, processID2); // The process ID.
					stmt.setString(2, translator.getParmas().get(2).toString()); // The Appraiser's ID
					stmt.setInt(3,(int) translator.getParmas().get(3)); // The evaluated number of days for execution
					
					stmt.executeUpdate();
					sendNotification(processID2, "define execution stage due time",
							(int) translator.getParmas().get(3), "Supervisor", "Performance Leader", null);
					setNextStageByOne(processID2); // Process is set to next stage;
					executeNumberOfDaysAnswer.add(true);
					return executeNumberOfDaysTranslator;
				}
				catch (SQLException e) {
					
					executeNumberOfDaysAnswer.add(false);

					System.out.println("Insert Evaluation Days: SQL EXCEPTION");
					System.out.println(e.getMessage());
					return executeNumberOfDaysTranslator;
				}
			}
			if(processStageExec.equals("7.5")) {
				try {
					stmt = conn.prepareStatement("Update icmdb.execution SET"
							+ " executor_id=?, number_of_days=?"
							+ " WHERE process_id=?");


					stmt.setString(1, translator.getParmas().get(2).toString()); // The Appraiser's ID
					stmt.setInt(2,(int) translator.getParmas().get(3)); // The evaluated number of days
					
					stmt.setInt(3, processID2); // The process ID.

					stmt.executeUpdate();
					sendNotification(processID2, "define execution stage due time",
							(int) translator.getParmas().get(3), "Supervisor", "Performance Leader", null);
					setNextStageByOne(processID2); // Process is set to next stage;
					executeNumberOfDaysAnswer.add(true);
					return executeNumberOfDaysTranslator;
				}
				catch (SQLException e) {
					//e.printStackTrace();
					executeNumberOfDaysAnswer.add(false);

					System.out.println("Insert Evaluation Days: SQL EXCEPTION");
					System.out.println(e.getMessage());
					return executeNumberOfDaysTranslator;
				}
			}
			
			case Execution_Completed:
				int processIDCompleteExec = (int) translator.getParmas().get(0); // The process ID. 
				setNextStageByOne(processIDCompleteExec);
				ArrayList<Boolean> completeExecutionAnswer = new ArrayList<>();
				completeExecutionAnswer.add(true);
				Translator completeExecutionTranslator = new Translator(
						OptionsOfAction.Execution_Completed, completeExecutionAnswer);
				return completeExecutionTranslator;
			
		
		case GET_RELATED_MESSAGES:
			String role = (String)translator.getParmas().get(0);
			
			
				System.out.println("GET_RELATED_MESSAGES 1");
			try {
					if(role.compareTo("Manager") != 0 && role.compareTo("Supervisor") != 0 && role.compareTo("Chairman") != 0)//role = ID of someone
					{
						stmt = conn.prepareStatement("SELECT *\r\n" + 
								"FROM icmdb.messages\r\n" + 
								"JOIN icmdb.users_requests \r\n" + 
								"ON users_requests.process_id = messages.process_id && messages.to_who = users_requests.role\r\n" + 
								"WHERE user_id = ?");
						
						System.out.println((String)translator.getParmas().get(0));
						stmt.setString(1, (String)translator.getParmas().get(0));
						
						ResultSet rs = stmt.executeQuery();	
						System.out.println("GET_RELATED_MESSAGES 2");
						if(rs.first() == false) {
							System.out.println("GET_RELATED_MESSAGES - No messages were found");
							//ar.add("No messages were found");
							ArrayList<String> empty = new ArrayList<String>();
							empty.add("No messages were found");
							Translator newTranslator = new Translator(OptionsOfAction.GET_RELATED_MESSAGES, empty);
							return newTranslator;
						}
						
						rs.previous();
						
						ArrayList<Object> messages = new ArrayList<Object>();
					
						while(rs.next()) {	
							messages.add(rs.getInt(2));
							messages.add(rs.getString(3));
							messages.add(rs.getInt(4));
							messages.add(rs.getString(6));
							messages.add(rs.getString(7));
							messages.add((java.sql.Date)rs.getDate(8));
						}
						
						System.out.println("GET_RELATED_MESSAGES: " + messages);
						Translator newTranslator = new Translator(translator.getRequest(), messages);
						return newTranslator;
					}
					
					else
					{
						stmt = conn.prepareStatement("SELECT *\r\n" + 
								"FROM icmdb.messages\r\n" + 
								"WHERE messages.to_who = ?");
						stmt.setString(1, role);					
						
						ResultSet rs = stmt.executeQuery();	
						System.out.println("GET_RELATED_MESSAGES 3");
						if(rs.first() == false) {
							System.out.println("GET_RELATED_MESSAGES - No messages were found");
							ar.add("No messages were found");
							ArrayList<ArrayList<?>> empty = new ArrayList<ArrayList<?>>();
							empty.add(ar);
							Translator newTranslator = new Translator(translator.getRequest(), empty);
							return newTranslator;
						}
						
						rs.previous();
						
						ArrayList<Object> messages = new ArrayList<Object>();
					
						while(rs.next()) {	
							messages.add(rs.getInt(2));
							messages.add(rs.getString(3));
							messages.add(rs.getInt(4));
							messages.add(rs.getString(6));
							messages.add(rs.getString(7));
							messages.add((java.sql.Date)rs.getDate(8));
						}
						
						System.out.println(messages);
						Translator newTranslator = new Translator(translator.getRequest(), messages);
						return newTranslator;
					}
				} 
			catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			break;
		case DECLINE_EVALUATION_OR_EXECUTION_DUE_TIME:
			try 
			{
				
				System.out.println("DECLINE_EVALUATION_OR_EXECUTION_DUE_TIME 1");

				
				String toWho="";
				String nextStage="";
				
				if((int)translator.getParmas().get(2) == 3)
				{
					toWho = "Appraiser";
					nextStage = "2.5";
				}
					
				else
				{
					toWho = "Performance Leader";
					nextStage = "7.5";
				}
					
				sendNotification((int)translator.getParmas().get(0), "execution stage due time was declined", 0,
						toWho, "Supervisor", null);
				
				deleteNotification((int)translator.getParmas().get(0), "define execution stage due time",
						Integer.parseInt(translator.getParmas().get(1).toString()), "Supervisor", toWho);
				
				setNextStageByInput((int)translator.getParmas().get(0), nextStage);
			}
			catch(Exception e) {
				 //TODO Auto-generated catch block
				System.out.println("Catch DECLINE_EVALUATION_OR_EXECUTION_DUE_TIME");
				System.out.println(e.getMessage());
			}

			break;
		case DECLINE_EVALUATION_OR_EXECUTION_EXTENSION_TIME://for all stages (not just evaluation and execution)
			try
			{
				String toWho="";
				String procStage = translator.getParmas().get(1).toString();
				
				switch(procStage)
				{
				case "4.5":
					procStage = "4";
					toWho = "Appraiser";
					break;
				case "5.5":
					procStage = "5";
					toWho = "Chairman";
					break;
				case "9.5":
					procStage = "9";
					toWho = "Performance Leader";
					break;
					
				case "11.2":
					procStage = "11.1";
					toWho = "Examiner";
					break;
				case "11.5":
					procStage = "11";
					toWho = "Examiner";
					break;
				}
			System.out.println("DECLINE_EVALUATION_OR_EXECUTION_EXTENSION_TIME: toWho = " + toWho);
			
				sendNotification((int)translator.getParmas().get(0), "due time extension was declined", 0,
						toWho, "Supervisor", null);
				
				deleteNotification((int)translator.getParmas().get(0), "add due time extension", 0,
						"Supervisor", toWho);
				
				setNextStageByInput((int)translator.getParmas().get(0), procStage);
			
				
			}
		catch(Exception e) {
			 //TODO Auto-generated catch block
			System.out.println("Catch ADD_EVALUATION_OR_EXECUTION_EXTENSION_TIME");
			System.out.println(e.getMessage());
		}
			break;
		case RECOVER_PASSWORD:
			try
			{
				ArrayList<Object> passwordAndEmail = new ArrayList<Object>();

				System.out.println(translator.getParmas());
				
				stmt = conn.prepareStatement("SELECT email FROM icmdb.workers WHERE id=?");
										
				stmt.setString(1, (String)translator.getParmas().get(0));
				
				ResultSet rs = stmt.executeQuery();	
				
				System.out.println("RECOVER_PASSWORD 1");
				
				if(rs.first() == false) {
					stmt = conn.prepareStatement("SELECT email FROM icmdb.students WHERE id=?");
					
					stmt.setString(1, (String)translator.getParmas().get(0));
					
					ResultSet rs2 = stmt.executeQuery();	
					
					System.out.println("RECOVER_PASSWORD 1");
					
					if(rs2.first() == false) {
						System.out.println("RECOVER_PASSWORD - No email was found");
						ar.add("No email was found");
						
						Translator newTranslator = new Translator(translator.getRequest(), ar);
						return newTranslator;
					}
					rs2.previous();
					
					while(rs2.next()) {	
						passwordAndEmail.add(rs2.getString(1));
					}
				}
				
				else
				{
					rs.previous();
					
					while(rs.next()) {	
						passwordAndEmail.add(rs.getString(1));
					}
				}
				
				
				System.out.println(passwordAndEmail);
				
				stmt = conn.prepareStatement("SELECT password FROM icmdb.users WHERE user_id=?");
				
				stmt.setString(1, (String)translator.getParmas().get(0));
				
				ResultSet rs3 = stmt.executeQuery();	
				
				System.out.println("RECOVER_PASSWORD 2");
				
				if(rs3.first() == false) {
					System.out.println("RECOVER_PASSWORD - No password was found");
					ar.add("No password was found");
					
					Translator newTranslator = new Translator(translator.getRequest(), ar);
					return newTranslator;
				}
				
				rs3.previous();
							
				while(rs3.next()) {	
					passwordAndEmail.add(rs3.getString(1));
				}
				
				System.out.println("DBConnector - RECOVER_PASSWORD - passwordAndEmail: " + passwordAndEmail);
				
				Translator newTranslator = new Translator(translator.getRequest(), passwordAndEmail);
				return newTranslator;
			} 
			catch (SQLException e) {
			// TODO Auto-generated catch block
				System.out.println("RECOVER_PASSWORD - SQL EXCEPTION");
			e.printStackTrace();
			}
			break;
			
		case DOWNLOADFILE:
			{
	        	ArrayList<MyFile> fileToServer = new ArrayList<MyFile>();
	    		MyFile msg= new MyFile((String) translator.getParmas().get(0));
	    		  try{
	    			      File newFile = new File ((String) translator.getParmas().get(0));      
	    			      byte [] mybytearray  = new byte [(int)newFile.length()];
	    			      FileInputStream fis = new FileInputStream(newFile);
	    			      BufferedInputStream bis = new BufferedInputStream(fis);			  
	    			      
	    			      msg.initArray(mybytearray.length);
	    			      msg.setSize(mybytearray.length);
	    			      
	    			      bis.read(msg.getMybytearray(),0,mybytearray.length);
	    			      fileToServer.add(msg);	
	    			    Translator newTranslator = new Translator(translator.getRequest(), fileToServer);
	    				return newTranslator;
	    			    }
	    			catch (Exception e) {
	    				System.out.println("Error: Can't send files to Server");
	    				System.out.println(e.getMessage());
	    			}
					
			}

		break;
		case SEND_EXTENSION_REQUEST:
			sendNotification((int)translator.getParmas().get(0), "add due time extension", 0,
					"Supervisor", translator.getParmas().get(2).toString(), translator.getParmas().get(1).toString());
			
			String procStage = translator.getParmas().get(3).toString();
			
			switch(procStage)
			{
			case "4":
				procStage = "4.5";
				break;
			case "5":
				procStage = "5.5";
				break;
			case "9":
				procStage = "9.5";
				break;
			case "11":
				procStage = "11.5";
				break;
			case "11.1":
				procStage = "11.2";
				break;
			}
			
			setNextStageByInput((int)translator.getParmas().get(0), procStage);
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
			case "4.1":
			case "4.2":
			case "4.3":
			case "4.5":
			case "4.6":
				nextProcStage = "5";
				break;
			case "5":
			case "5.1":
			case "5.2":
			case "5.3":
			case "5.5":
			case "5.6":
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
			case "9.1":
			case "9.2":
			case "9.3":
			case "9.5":
			case "9.6":
				nextProcStage = "10";
				break;
			case "10":
				nextProcStage = "11";
				break;
			case "11":
			case "11.1":
			case "11.2":
			case "11.3":
			case "11.5":
			case "11.6":
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
			
			System.out.println("setNextStageByInput: procID = " + procID + ", nextStage = " + nextStage);
			
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
	
	public static void sendNotification(int procID, String content, int days, String toWho, String fromWho, String reason)
	{
		System.out.println(procID+" "+content+" "+days+" "+toWho+" "+fromWho+" "+reason);
		
		try
		{
			java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
			
			PreparedStatement stmt;
			
			stmt = conn.prepareStatement("INSERT INTO icmdb.messages (process_id, content, days, to_who, from_who, reason_for_extension, date)"
					+ "  VALUES (?,?,?,?,?,?,?)");
			
			stmt.setInt(1, procID);
			stmt.setString(2, content);
			stmt.setInt(3, days);
			stmt.setString(4, toWho);
			stmt.setString(5, fromWho);
			
			if(reason == null)
				reason = "";
			
			stmt.setString(6, reason);
			stmt.setDate(7, date);
			
			stmt.executeUpdate();
			
			System.out.println("DBController - sendNotification - insert succeeded");

		}
		
		catch (SQLException e) {
		// TODO Auto-generated catch block
		System.out.println("SQL Exception DBController - sendNotification()");
		System.out.println(e.getMessage());
		}	
		
		
	}
	
	public static void deleteNotification(int procID, String content, int days, String toWho, String fromWho)
	{
		try
		{
			System.out.println("procID, content, days, toWho, fromWho");
			System.out.println(procID + ", " + content + ", " + days + ", " + toWho + ", " + fromWho);
			

			PreparedStatement stmt;
			
			stmt = conn.prepareStatement("DELETE FROM icmdb.messages\r\n" + 
					"					WHERE process_id=? AND content=? AND days=? AND to_who=? AND from_who=?");
			
			stmt.setInt(1, procID);
			stmt.setString(2, content);
			stmt.setInt(3, days);
			stmt.setString(4, toWho);
			stmt.setString(5, fromWho);
			
			stmt.executeUpdate();
			
			System.out.println("DBController - deleteNotification - deletion succeeded");

		}
		
		catch (SQLException e) {
		// TODO Auto-generated catch block
		System.out.println("SQL Exception DBController - deleteNotification()");
		System.out.println(e.getMessage());
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
	
	private static ArrayList<String> getRelatedFilesName(int requestID) {
		ArrayList<String> filesNames = new ArrayList<String>();
		String tempString;
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("SELECT file FROM files WHERE files.request_id=?");
			stmt.setInt(1, requestID);
			ResultSet rs = stmt.executeQuery();
			if(rs.first() == false) {
				return null;
			}
			rs.previous();
			while (rs.next()) {
				tempString =rs.getString(1);
				filesNames.add(tempString);
			}
			return filesNames;

		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	public static int getHandlerId(int requestID)
	{
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT process_stage FROM processes WHERE request_id=?");
			stmt.setInt(1, requestID);
			String stage = "";
			int handlerID;
			
			ResultSet rs = stmt.executeQuery();
			if(rs.first() == false) {
				return -1;
			}
			rs.previous();
			while (rs.next()) {
				 stage = rs.getString(1);
			}
			
			String role = MyHashMaps.getInstance().stageHandlers.get(Double.parseDouble(stage));
			if(role != null)
			{
				stmt = conn.prepareStatement("SELECT user_id FROM users_requests WHERE process_id=? and users_requests.role=?");
				stmt.setInt(1, requestID);
				stmt.setString(2, role);
				
				rs = stmt.executeQuery();
				
				if(rs.first() == false) {
					
					stmt = conn.prepareStatement("SELECT user_id FROM permanent_roles WHERE role=?");
					stmt.setString(1, role);
					rs = stmt.executeQuery();
					
					if(rs.first() == false) {
						return -1;
					}
					rs.previous();
					while (rs.next()) {
						 return rs.getInt(1);
					}
					
					}
				
				rs.previous();
				while (rs.next()) {
					return rs.getInt(1);
				}
					
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String getHandlerRole(int requestID)
	{
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT process_stage FROM processes WHERE request_id=?");
			stmt.setInt(1, requestID);
			String stage = "";
			int handlerID;
			
			ResultSet rs = stmt.executeQuery();
			if(rs.first() == false) {
				return null;
			}
			rs.previous();
			while (rs.next()) {
				 stage = rs.getString(1);
			}
			
			return MyHashMaps.getInstance().stageHandlers.get(Double.parseDouble(stage));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
