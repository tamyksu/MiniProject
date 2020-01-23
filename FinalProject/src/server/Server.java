package server;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import application.Processes;
import application.UserProcess;
import client.Client;
import ocsf.server.*;
import translator.OptionsOfAction;
import translator.Translator;
import client.Client;


/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class Server extends AbstractServer 
{
	Client client = Client.getInstance();
	private static Connection conn;
	private static IDBConnector dbConnector;
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 25565;  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param dbConnector - injection of a fake dbConnector for testing
   */
  public Server(int port, IDBConnector dbConnector) 
  {
    super(port);
    Server.dbConnector = dbConnector;
  }
  
  public Server(int port) 
  {
    super(port);
	  if(Server.dbConnector==null)
	  {
    Server.dbConnector = new DBConnector();
	  }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  	{
	    System.out.println("Message received: " + msg + " from " + client);
	    try {
	    	Object rs = dbConnector.accessToDB(msg);
	    	if(rs != null)	
	    		client.sendToClient(rs);
	    	}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
	    catch (IOException q)
	    {
	    	
	    }
	  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println("Server has stopped listening for connections.");
  }

  //Class methods ***************************************************
  
  /**
   * This function should return the reports of a specific date  - NO YET IMPLEMENTED
   * So it returns null
   * @param date
   * @return
   */
 public ArrayList<ArrayList<Double>> getReportsFromServer(LocalDate date)
 {
	 //Go to DB and ask for reports of a specific date
	 return null;
 }
 
 /**
  * This function should save the reports to the DB - NO YET IMPLEMENTED
  * @param data - the reports to save
  * @return answer if the reports were saved successfully or not.
  */
 public boolean saveReportsToServer(ArrayList<ArrayList<Double>> data)
 {
	 return false;
 }
 
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public  static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    Server sv = new Server(port);
    
    dbConnector.establishDBConnection();
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
	Timer timer = new Timer();
	
	Calendar calendar = Calendar.getInstance();
	calendar.set(Calendar.HOUR_OF_DAY, 23);
	calendar.set(Calendar.MINUTE, 01);
	calendar.set(Calendar.SECOND, 0);
	
	Date time = calendar.getTime();
	
	timer.schedule(new checkDueDateOfProcessesTask(), time);
    
  }
  
  /**
   * This function is running a check for each process if it passed its due date using thread that runs once a day
   * And notifies the handler and the people that should be updated.
   * @author amirgroi
   *
   */
   static class checkDueDateOfProcessesTask extends TimerTask{
	  @Override
	public void run() {
		  
  		 ArrayList<ArrayList<?>> result = dbConnector.getActiveProcesses();
		  
  		Processes processes = new Processes();
		if(!(result.get(0).get(0).toString().equals("No processes")))
		{
			for (int i = 0; i < result.size(); i=i+2) {                                                                         
				UserProcess process = new UserProcess();

				process.setRequest_id((int)result.get(i).get(0));
				process.setSystem_num((int)result.get(i).get(1));
				process.setIntiatorId((String)result.get(i+1).get(0));
				process.setProblem_description((String)result.get(i+1).get(1));
				process.setRequest_description((String)result.get(i+1).get(2));
				process.setExplanaton((String)result.get(i+1).get(3));
				process.setNotes((String)result.get(i+1).get(4));
				process.setStatus((String)result.get(i+1).get(5));
				process.setCreation_date((String)result.get(i+1).get(6));
				process.setHandler_id((String)result.get(i+1).get(7));
				process.setProcess_stage((String)result.get(i+1).get(8));
				process.setCurrent_stage_due_date((String)result.get(i+1).get(9));
				process.setInitiatorFirstName((String)result.get(i+1).get(10));
				process.setInitiatorLastName((String)result.get(i+1).get(11));
				process.setEmail((String)result.get(i+1).get(12));
				process.setDepartment((String)result.get(i+1).get(13));
				
				try {
					if(process.getCurrent_stage_due_date() == null)
						continue;
					Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(process.getCurrent_stage_due_date());
					Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
					
					long diff = dueDate.getTime() - currentDate.getTime();
				    int days = (int)(long)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
				    
				    //hander passed its due date
				    if(days < 0)
				    {
				    	/************************tamy****************************/
				    try {
				    	PreparedStatement stmt = conn.prepareStatement("insert into icmdb.delay_reports (request_id,number_days_delay)"
				    			+"values(?,?)");
				    	stmt.setInt(1, (int)result.get(i).get(0));
				    	stmt.setInt(2, (int)(days));
				    			stmt.executeUpdate();
				    }catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("SQL EXCEPTION!-in server");
				    }
				    /********************************tamy***********************/
				    	//Send a notification to handler
						dbConnector.sendNotification(process.getRequest_id(), "You exceeded due time for this stage",Math.abs(days),dbConnector.getHandlerRole(process.getRequest_id()), "ICM", "");
				    	//Send a notification to supervisor
						dbConnector.sendNotification(process.getRequest_id(), dbConnector.getHandlerRole(process.getRequest_id())+" exceeded due time for this stage!",Math.abs(days),"Supervisor", "ICM", "");
				    	//Send a notification to manager
						dbConnector.sendNotification(process.getRequest_id(), dbConnector.getHandlerRole(process.getRequest_id())+" exceeded due time for this stage!",Math.abs(days),"Manager", "ICM", "");
				    }
				    
				    //a couple of days left for this stage till due time
				    else
				    	if(days>=0 && days < 3)
				    	{
				    		//Send notification to handler
				    		dbConnector.sendNotification(process.getRequest_id(), "You have "+days+" days to complete the stage!",0,dbConnector.getHandlerRole(process.getRequest_id()), "ICM", "");
				    	}
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
				catch(NullPointerException ex)
				{
					continue;
				}

				processes.getMyProcess().put(new Integer((int)result.get(i).get(0)), process);
				processes.getMyProcessesInArrayList().add(process);	
			}

		}
  }
  }
}
//End of EchoServer class
