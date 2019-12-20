package client;

import ocsf.client.*;
import java.io.*;
import java.util.ArrayList;
import application.LoginController;
import application.ScreenController;

public class Client extends AbstractClient
{
  ArrayList<String> data = new ArrayList<String>();
  private String name;
  public static Client instance;
  public Client(String host, int port) throws IOException {
    super(host, port); //Call the superclass constructor
    openConnection();
    instance =  this;
  }
  public static Client getInstance() {
		return instance;
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
		case "The username does not exist":
			LoginController.getInstance().getMessageField().setText("The username does not exist");
			break;
			
		case "correct match":
				Client.getInstance().setName(result.get(1));
				ScreenController.getScreenController().activate("processesMain");				
				break;
		case "Login failed, username and password did not match":
			LoginController.getInstance().getMessageField().setText("Login failed, username and password did not match");

		break;
		default:
			break;
		}
		
	   	
	}
	else {
		System.out.println("Succeeded");
	}
	
  }

  private void setName(String name) {
		this.name=name;	
}
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
  public String getName() {
		return name;
	}
 
}
