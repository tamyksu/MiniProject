// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import sun.security.jca.GetInstance.Instance;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import application.Controller;
import application.Person;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */

public class ChatClient extends AbstractClient
{
	
  //Instance variables **********************************************
  ArrayList<String> data = new ArrayList<String>();
 

  public static final String INSERT_FUNC = "insert";
  public static final String SELECT_FUNC = "select";
  public static final String UPDATE_FUNC = "update";
  

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port) 
    throws IOException, ConnectException
  {
    super(host, port); //Call the superclass constructor
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  @SuppressWarnings("unchecked")
public void handleMessageFromServer(Object rs) 
  {
	
	if (rs instanceof ArrayList)  {
		ArrayList<String> result = (ArrayList<String>)rs;
		switch (result.get(0)) {
		case "successful table select":
			result.remove("successful table select");
			Controller.setInTable(result);
			break;
			
		case "successful update":
			Controller.instance.getUpdateAanswer().setText(result.get(0));
			Controller.instance.initializeTable();
			break;
		case "successful select":
			Controller.instance.getInitiator_name1().setText(result.get(1));
			Controller.instance.getSystem_num1().setText(result.get(2));
			Controller.instance.getCurrent_status1().setText(result.get(3));
			Controller.instance.getRequest_description1().setText(result.get(4));
			Controller.instance.getStatus2().setText(result.get(5));
			Controller.instance.getHandler_name1().setText(result.get(6));
			Controller.instance.getSelectAanswer().setText(result.get(0));
		break;
		
		case "successful insert":
			Controller.instance.getInsertAanswer().setText(result.get(0));
			Controller.instance.initializeTable();
			break;
		case "failed select":
			Controller.instance.getSelectAanswer().setText(result.get(0)+".\nThe ID probably does not exist.");
		break;
		default:
			break;
		}
		
	   	
	}
	else {
		System.out.println("Succeeded");
	}
	
  }

  	
  public void select(ArrayList<String> data) {
	  data.add(0, SELECT_FUNC);
	  handleMessageFromClientGUI(data); 
  }
  public void insert(ArrayList<String> data) {
	  data.add(0, INSERT_FUNC);
	  handleMessageFromClientGUI(data);
  }
  public void update(ArrayList<String> data) {
	  data.add(0, UPDATE_FUNC );
	  handleMessageFromClientGUI(data);
  }
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  /*public void getInfo(String id) {
	   
    	try {
			sendToServer(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
  }
  public void setInfo(String id) {
	   
  	try {
			sendToServer(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
  }*/
  
  public void handleMessageFromClientGUI(ArrayList<String> message)  
  {

    try
    {
    	sendToServer(message);
     }
    catch(IOException e)
    {
    System.out.println("Could not send massage to server");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

public void updateTable() {
	ArrayList<String> data = new ArrayList<String>();
	data.add("table");
	handleMessageFromClientGUI(data);	
}
 
}
//End of ChatClient class
