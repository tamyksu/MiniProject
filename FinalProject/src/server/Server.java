package server;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import application.Processes;
import application.UserProcess;
import ocsf.server.*;

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
   */
  public Server(int port) 
  {
    super(port);
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
	    	Object rs = DBConnector.accessToDB(msg);
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
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
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
    
    DBConnector.establishDBConnection();
    
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
		  
  		 ArrayList<ArrayList<?>> result = DBConnector.getActiveProcesses();
		  
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
					Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(process.getCurrent_stage_due_date());
					Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
					
					long diff = dueDate.getTime() - currentDate.getTime();
				    int days = (int)(long)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
				    
				    //hander passed its due date
				    if(days < 0)
				    {
				    	//Send a notification to handler
						DBConnector.sendNotification(process.getRequest_id(), "You exceeded due time for this stage",Math.abs(days),DBConnector.getHandlerRole(process.getRequest_id()), "ICM", "");
				    	//Send a notification to supervisor
						DBConnector.sendNotification(process.getRequest_id(), DBConnector.getHandlerRole(process.getRequest_id())+" exceeded due time for this stage!",Math.abs(days),"Supervisor", "ICM", "");
				    	//Send a notification to manager
						DBConnector.sendNotification(process.getRequest_id(), DBConnector.getHandlerRole(process.getRequest_id())+" exceeded due time for this stage!",Math.abs(days),"Manager", "ICM", "");
				    }
				    //a couple of days left for this stage till due time
				    else
				    	if(days>=0 && days < 3)
				    	{
				    		//Send notification to handler
							DBConnector.sendNotification(process.getRequest_id(), "You have "+days+" days to complete the stage!",0,DBConnector.getHandlerRole(process.getRequest_id()), "ICM", "");
				    	}
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
