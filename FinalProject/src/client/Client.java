package client;
import ocsf.client.*;
import translator.OptionsOfAction;
import translator.Translator;
import java.io.*;
import java.util.ArrayList;
import application.ControllerProcessMain;
import application.LoginController;
import application.NewRequestController;
import application.Processes;
import application.ScreenController;
import application.StaffMainController;
import application.Supervisor_ProcessMain_Controller;
import application.UserProcess;
import javafx.application.Platform;
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
		case GETALLPROCESSES:
			handlerMessageFromServerGetAllProcesses(result.getParmas());
			break;
		case GET_APPRAISER_AND_PERFORMANCE_LEADER_CB_DATA:
			handlerMessageFromServerAppointAppraiser(result.getParmas());
			break;
		case GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC:
			handlerMessageFromServerGetAppOrPLofProc(result.getParmas());
			break;
		case GETALLINFORMATIONSYSTEMS:
			//fillListForComboBox(result.getParmas());	
			break;
		case INITIALIZE_COMBO_BOX:
			handlerMessageFromServerSelectChairMan(result.getParmas());
			break;
		case UPDATEPERMANENT:
			handlerMessageFromServerUpdatePermanent(result.getParmas());
			break;
		case DELETEPERMANENT:
			handlerMessageFromServerDELETEPERMANENT(result.getParmas());
			break;
		case CURRENT_IN_ROLE:
			handlerMessageFromServerCURRENT_IN_ROLE(result.getParmas());
			break;
		case checkDB:
			handlerMessageFromServercheckDB(result.getParmas());
			break;
		case checkNAMEParmenent:
			handlerMessageFromServercheckNAMEParmenent(result.getParmas());
		break;
		case DEFROST_PROCESS:
			handleMessageFromServerDefrostProcess(result.getParmas());
			break;
		case SHUTDOWN_PROCESS:
			handleMessageFromServerShutdownProcess(result.getParmas());
		default:
			break;
		}
	
	}
	
	private void handleMessageFromServerShutdownProcess(Object message) {
	ArrayList<String> arr= (ArrayList<String>) message;
	
	if(arr.get(0).equals("Successfully Shutdown"))
	{
	}
	else
		new Alert(AlertType.ERROR,"There was an issue to shutdown this process").show();
}
	
		private void handleMessageFromServerDefrostProcess(Object message) {
		ArrayList<String> arr= (ArrayList<String>) message;
		
		if(arr.get(0).equals("Successfully Defrosted"))
		{
			ControllerProcessMain.getInstance().getTheUpdateProcessesFromDB();
			ControllerProcessMain.getInstance().ButtonAdjustmentSuperUser(Client.instance.role, ControllerProcessMain.getInstance().getRequestID());
		}
		else
			new Alert(AlertType.ERROR,"There was an issue to defrost this process").show();
	}
	
	public void handlerMessageFromServercheckNAMEParmenent(Object message)
	{
		ArrayList<String> arr= (ArrayList<String>)message;
		System.out.println("just go to func"+arr.get(0));
		StaffMainController.instance.check_if_this_man_available(arr);
		
	}
	/******************************************handlerMessageFromServerSelectChairMan************************************************************/
	public void handlerMessageFromServerSelectChairMan(Object message)
	{
		ArrayList<String> arr= (ArrayList<String>)message;
		StaffMainController.instance.setDataChairMan(arr);	
	}
	
	public void handlerMessageFromServerCURRENT_IN_ROLE(Object message)
	{
		ArrayList<String> arr= (ArrayList<String>)message;
		
		if(arr.get(2).equals("ChairMan"))
		arr.add("2");///its make it index 3
		else if(arr.get(2).equals("Supervisor"))
			arr.add("3");
		else if(arr.get(2).equals("Information Engineer-1"))
			arr.add("4");
		else if(arr.get(2).equals("Information Engineer-2"))
			arr.add("5");
		
	
		StaffMainController.instance.printMessage1(arr);
	}
	public void handlerMessageFromServercheckDB(Object message)
	{
	ArrayList<String> arr= (ArrayList<String>)message;
	//System.out.println(arr.get(2)+"checkkkkkkkkkkkkk");
	System.out.println("in option: "+arr.get(2)+" this place empty if it 0 :"+arr.get(0)+" in role "+ arr.get(1));
	
		if(arr.get(2).equals("1"))//first thing
		{
			
		StaffMainController.instance.afterSet(arr);
		}	
		else //chair man check in parmenent
	{
		
			StaffMainController.instance.checkApoint(arr);
	}		
	}

 public void handlerMessageFromServerDELETEPERMANENT(Object message){
		ArrayList<String> arr= (ArrayList<String>)message;
	
		StaffMainController.instance.SET_DELETEPERMANENT(arr);
		
	}
	private void setName(String userID) {
		this.userID = userID;
	}
	
	public void handleMessageFromClientGUINewRequest(Object message) {
		try {
			super.
			sendToServer(message);
		} catch (IOException e) {
			System.out.println("Could not insert new request");
			quit();
		}
	}
	public void handleMessageFromClientGUI(Object message) {
		try {
			sendToServer(message);
		} catch (IOException e) {
			System.out.println("Could not perform action to server");
		//	System.out.println(e.getMessage());
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
	/*******************************************getProcessesFromServer*******************************************************/
	public void getProcessesFromServer() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(userID);
		Translator translator = new Translator(OptionsOfAction.GETRELATEDREQUESTS, ar);
		handleMessageFromClientGUI(translator);
	}
/*******************************************handlerMessageFromServerUpdatePermanent***********************************************************/
	public void handlerMessageFromServerUpdatePermanent(Object message){
		ArrayList<String> arr= (ArrayList<String>)message;
		System.out.println("update permanent");
		
		
		if(arr.get(2).equals("ChairMan"))
			arr.add("2");///its make it index 3
			else if(arr.get(2).equals("Supervisor"))
				arr.add("3");
			else if(arr.get(2).equals("Information Engineer-1"))
				arr.add("4");
			else if(arr.get(2).equals("Information Engineer-2"))
				arr.add("5");
	
		arr.add("7");
	
		System.out.println("handlerMessageFromServerUpdatePermanent"+arr.get(0));
		StaffMainController.instance.printMessage(arr);
	}
/*****************************************handlerMessageFromServerNewRequest*************************************************************/	
	
	public void handlerMessageFromServerNewRequest(Object rs) {
	@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;
		
		if(result.get(0).booleanValue()==true) {
			NewRequestController.getInstance().setAnswerFromServer(true);
		}
		else {
			NewRequestController.getInstance().setAnswerFromServer(false);
		}
	}
/*********************************************handlerMessageFromServerLogin*************************************************/	
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
			ControllerProcessMain.instance.ButtonAdjustmentSuperUser(result.get(0),"Active");
			getAllProcessesFromServer();
			break;
		case "Manager":
			Client.getInstance().setName(result.get(1));
			this.setRule(result.get(0));
			ScreenController.getScreenController().activate("processesMain");
			ControllerProcessMain.instance.ButtonAdjustmentSuperUser(result.get(0),"Active");
			getAllProcessesFromServer();
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
	/*****************************************handlerMessageFromServerProcesses*****************************************************/
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
				process.setRole(Client.getInstance().getRole());
				//process.setRole((String)result.get(i+1).get(0));
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
	
	public void handlerMessageFromServerGetAppOrPLofProc(Object rs)
	{
		
		//TODO: HANDLE A SCENARIO WHERE RS="NO EMPLOYEES WERE FOUND"
		ArrayList <String> names = (ArrayList<String>)rs;
		ArrayList <String> fullNames = new ArrayList <String>();
		int procID = Integer.parseInt(names.get(0));
		
		if(names.size() == 4)//must be Appraiser because you can't have Performance LeaderL without Appraiser
		{
			fullNames.add(new String (names.get(1) + " " + names.get(2)));
		}
		if(names.size() == 7)
		{
			if(names.get(3).compareTo("Appraiser") == 0)
			{
				fullNames.add(new String (names.get(1) + " " + names.get(2)));//appraiser
				fullNames.add(new String (names.get(4) + " " + names.get(5)));//performance leader
			}
			else
			{
				fullNames.add(new String (names.get(4) + " " + names.get(5)));//appraiser
				fullNames.add(new String (names.get(1) + " " + names.get(2)));//performance leader
			}
		}
		
		System.out.println("Client full names: " + fullNames);
		Supervisor_ProcessMain_Controller.instance.setAppraiserAndPerformanceLeaderLabels(fullNames);
	}
}
