package client;
import ocsf.client.*;
import translator.OptionsOfAction;
import translator.Translator;
import java.io.*;
import java.util.ArrayList;
import application.ControllerProcessMain;
import application.InformationSystem;
import application.LoginController;
import application.NewRequestContoroller;
import application.Processes;
import application.ScreenController;
import application.StaffMainController;
import application.Supervisor_ProcessMain_Controller;
import application.UserProcess;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Client extends AbstractClient {
	private String userID;
	private Processes processes = new Processes();
	private String role = "";
	public static Client instance;
	
	public Client(String host, int port) throws IOException {
		super(host, port); // Call the superclass constructor
		openConnection();
		instance = this;
	}

	public static Client getInstance() {
		return instance;
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	
	public void handleMessageFromServer(Object rs) {
		Translator result = (Translator)rs; 
		switch (result.getRequest()) {
		case LOGIN:
			handlerMessageFromServerLogin(result.getParmas());
			break;
		case GETRELATEDREQUESTS:
			handlerMessageFromServerProcesses(result.getParmas());
			break;
		case NEWREQUEST:
			handlerMessageFromServerNewRequest(result.getParmas());
			break;
		case GET_APPRAISER_AND_PERFORMANCE_LEADER_CB_DATA:
			handlerMessageFromServerAppointAppraiser(result.getParmas());
			break;
		case GETALLINFORMATIONSYSTEMS:
			//fillListForComboBox(result.getParmas());
		default:
			break;
		}
	
	}
	public void handlerMessageFromServerSelectChairMan(Object message)
	{
		ArrayList<String> arr= (ArrayList<String>)message;
		System.out.println(arr+"!");
		StaffMainController.instance.setDataChairMan(arr);
		
		
	}

	private void setName(String userID) {
		this.userID = userID;
	}

	
	public void handleMessageFromClientGUI(Object message) {
		try {
			sendToServer(message);
		} catch (IOException e) {
			System.out.println("Could not send massage to server");
			quit();
		}
	}

	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}


	public String getUserID() {
		return userID;
	}

	public Processes getProcesses() {
		return processes;
	}

	public void setProcesses(Processes processes) {
		this.processes = processes;
	}
	//get the processes related this client
	public void getProcessesFromServer() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(userID);
		Translator translator = new Translator(OptionsOfAction.GETRELATEDREQUESTS, ar);
		handleMessageFromClientGUI(translator);
	}


	// Handle in case a New Request was received in the database

/******************************************************************************************************/
	public void handlerMessageFromServerUpdatePermanent(Object message){
		
		ArrayList<String> arr= (ArrayList<String>)message;
		System.out.println(arr+"!");
		StaffMainController.instance.printMessage(arr);///
	}
	
	

	public void handlerMessageFromServerNewRequest(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;
		
		if(result.get(0).booleanValue()==true) {
			NewRequestContoroller.getInstance().setAnswerFromServer(true);
		}
		else {
			NewRequestContoroller.getInstance().setAnswerFromServer(false);
		}
	}
	
	//In case we want to tell you, what is the server's answer regarding the client's connection experience
	public void handlerMessageFromServerLogin(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<String> result = (ArrayList<String>) rs;
		switch (result.get(0)) {
		case "correct match":
			Client.getInstance().setName(result.get(1));
			ScreenController.getScreenController().activate("processesMain");
			getProcessesFromServer();
			break;
		case "Supervisor":
			Client.getInstance().setName(result.get(1));
			this.setRule(result.get(0));
			ScreenController.getScreenController().activate("processesMain");
			getAllProcessesFromServer();
			break;
		case "Manager":
//			Client.getInstance().setName(result.get(1));
//			ScreenController.getScreenController().activate("Supervisor_ProcessesMain");
//			getProcessesFromServer();
			break;
		case "Login failed, username and password did not match":
			LoginController.getInstance().getMessageField()
					.setText("Login failed, username and password did not match");
			break;
		default:
			break;
		}
	}

	public void getAllProcessesFromServer() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(userID);
		Translator translator = new Translator(OptionsOfAction.GETALLPROCESSES, ar);
		handleMessageFromClientGUI(translator);		
	}
	
	private void setRule(String role) {
		this.setRole(role);		
	}

	//In case we got processes to display from this database, this function will make sure to save them to the client
	//and also send them to the tag on the appropriate screen
	@SuppressWarnings("unchecked")
	public void handlerMessageFromServerProcesses(Object rs) {
		Processes processes = new Processes();
    	ArrayList<ArrayList<?>> result = new ArrayList<ArrayList<?>>();	
		result = (ArrayList<ArrayList<?>>) rs ;
		if(!(result.get(0).get(0).toString().equals("No processes")))
		{
			for (int i = 0; i < result.size(); i=i+2) {
				UserProcess process = new UserProcess();
				process.setRequest_id((int)result.get(i).get(0));
				process.setRole((String)result.get(i+1).get(0));
				process.setIntiatorId((String)result.get(i+1).get(1));
				process.setSystem_num((int)result.get(i).get(1));
				process.setProblem_description((String)result.get(i+1).get(2));
				process.setRequest_description((String)result.get(i+1).get(3));
				process.setExplanaton((String)result.get(i+1).get(4));
				process.setNotes((String)result.get(i+1).get(5));
				process.setStatus((String)result.get(i+1).get(6));
				process.setCreation_date((String)result.get(i+1).get(7));
				process.setHandler_id((String)result.get(i+1).get(8));
				process.setProcess_stage((String)result.get(i+1).get(9));
				process.setCurrent_stage_due_date((String)result.get(i+1).get(10));
				process.setInitiatorFirstName((String)result.get(i+1).get(11));
				process.setInitiatorLastName((String)result.get(i+1).get(12));
				process.setEmail((String)result.get(i+1).get(13));
				process.setDepartment((String)result.get(i+1).get(14));
				processes.getMyProcess().put(new Integer((int)result.get(i).get(0)), process);
				processes.getMyProcessesInArrayList().add(process);	
			}
		this.processes=processes;
		}
		//send processes information to specific controller
		ControllerProcessMain.getInstance().SetInTable(processes);
	}
	
	//function related to supervisor. get all the process exist 
	@SuppressWarnings("unchecked")
	private void handlerMessageFromServerGetAllProcesses(ArrayList<?> rs) {
		Processes processes = new Processes();
    	ArrayList<ArrayList<?>> result = new ArrayList<ArrayList<?>>();	
		result = (ArrayList<ArrayList<?>>) rs ;
		if(!(result.get(0).get(0).toString().equals("No processes")))
		{
			for (int i = 0; i < result.size(); i=i+2) {                                                                         
				UserProcess process = new UserProcess();
				//Get values from intarray	
				process.setRequest_id((int)result.get(i).get(0));
				process.setSystem_num((int)result.get(i).get(1));
				//Get values from string array
				process.setRole("Supervisor");
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
				processes.getMyProcess().put(new Integer((int)result.get(i).get(0)), process);
				processes.getMyProcessesInArrayList().add(process);	
			}
		this.processes=processes;
		}
		//send processes information to specific controller
		ControllerProcessMain.getInstance().SetInTable(processes);		
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	
	public void handlerMessageFromServerAppointAppraiser(Object rs) {
		ArrayList <String> result = (ArrayList<String>) rs;
		
		System.out.println("YESSS");
		System.out.println(result);
		
		Supervisor_ProcessMain_Controller.instance.setAppraiserOrPerformanceLeaderDataInCB(result);

	}
}
