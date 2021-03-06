package client;
import ocsf.client.*;
import server.DBConnector;
import translator.OptionsOfAction;
import translator.Translator;
import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import application.ExtensionReportsController;
import application.ActiveReportsController;
import application.ChangeBoardMember;
import application.ControllerProcessMain;
import application.DecisionController;
import application.DelayReportsController;
import application.EvaluationController;
import application.EvaluationReport;
import application.ExecutionController;
import application.LoginController;
import application.MyFile;
import application.NewRequestController;
import application.Processes;
import application.ScreenController;
import application.StaffMainController;
import application.Supervisor_ProcessMain_Controller;
import application.UserProcess;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * The client that holds the connection to the server and handles messages
 */
public class Client extends AbstractClient {
	private String userID;
	private Processes processes = new Processes();
	private String role = "";
	public static Client instance;

	/**
	 * Constructor of client
	 * @param host - the IP of the connection to the server
	 * @param port - the port of the connection to the server
	 * @throws IOException
	 */
	public Client(String host, int port) throws IOException {
		super(host, port); // Call the superclass constructor
		openConnection();
		instance = this;
	}

	public static Client getInstance() {
		return instance;
	}


	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param rs The message from the server.
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
		case Get_Active_Statistic:
			handleMessageFromServerGet_Active_Statistic(result.getParmas());
			break;
		case DEFROST_PROCESS:
			handleMessageFromServerDefrostProcess(result.getParmas());
			break;
		case Fill_Evalution_Number_Of_Days:
			handleMessageFromServerFillEvalutionNumberOfDays(result.getParmas());
			break;
		case Fill_Evalution_Form:
			handleMessageFromServerFillEvalutionForm(result.getParmas());
			break;	
		case Get_Evaluation_Report_For_Process_ID:
			handleMessageFromServerGetEvalutionForm(result.getParmas());
			break;
		case Approve_Decision:
			handleMessageFromServerApproveDecision(result.getParmas());
			break;
		case More_Info_Decision:
			handleMessageFromServerMoreInfoDecision(result.getParmas());
			break;
		case Execution_Suggest_Number_Of_Days:
			handleMessageFromServerExecutionSuggestNumberOfDays(result.getParmas());
			break;
		case Execution_Completed:
			handleMessageFromServerExecutionCompleted(result.getParmas());
			break;	
		case FREEZE_PROCESS:
			handleMessageFromServerFreezeProcess(result.getParmas());
			break;
		case SHUTDOWN_PROCESS:
			handleMessageFromServerShutdownProcess(result.getParmas());
			break;
		case GET_RELATED_MESSAGES:
			setRelatedMessages(result.getParmas());
			break;
		case RECOVER_PASSWORD:
			handleMessageFromServerSendRecoveredPassword(result.getParmas());
			break;
		case REJECTE_PROCESS:
			handleMessageFromServerREJECTE_PROCESS(result.getParmas());
			break;
		case DOWNLOADFILE:
			handleMessageFromServerDownloadFile(result.getParmas());
			break;
		case GET_TEMPORARY_WORKERS_FROM_DB:
			handleMessageFromServerGetTemporaryWorkersFromDB(result.getParmas());
			break;
		case GET_PERMANENT_WORKERS_FROM_DB:
			handleMessageFromServerGetPermanentWorkersFromDB(result.getParmas());
			break;
		case Get_All_Change_Board_Members:
			handleMessageFromServerGetAllChangeBoardMembers(result.getParmas());
			break;
		case Appoint_Examiner:
			handleMessageFromServerAppointExaminer(result.getParmas());
			break;	
		case SelectDelayReport:
			handleMessageSelectDelayReport(result.getParmas());
			break;
		case SelectExtensionReport:
			handleMessageSelectExtensionReport(result.getParmas());
		case SaveReportToServer:
			handleSaveReportToServer(result.getParmas());
		default:
			break;
		}

	}

	private void handleSaveReportToServer(Object rs) {
		ArrayList<Boolean> answer = (ArrayList<Boolean>) rs;
		ActiveReportsController.instance.dataSaved = answer.get(0);
	}

	/**
	 * @param rs - the result that was received from server after the appointment
	 */
	public void handleMessageFromServerAppointExaminer(Object rs) {
		ArrayList<Boolean> answer = (ArrayList<Boolean>) rs;
		DecisionController.getInstance().setAnswerFromServerAppointExaminer(answer.get(0));
		boolean val = answer.get(0);
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				DecisionController.getInstance().showActionAppointExaminer(val);
			}

		});
	}

	/**
	 * @param rs - the change board members that were received from server
	 */
	public void handleMessageFromServerGetAllChangeBoardMembers(Object rs) {
		ArrayList<ChangeBoardMember> changeBoardMembers = (ArrayList<ChangeBoardMember>) rs;
		DecisionController.getInstance().setComboBox(changeBoardMembers);
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				ControllerProcessMain.getInstance().continueChairman2();
			}

		});
	}

	/**
	 * @param rs - information that has been received from server for further calculations
	 */
	public void handleMessageSelectExtensionReport(Object rs)
	{
		
		ArrayList<ArrayList<Integer>> result = (ArrayList<ArrayList<Integer>> ) rs;
		ExtensionReportsController.instance.calculate(result);
	}
	
	/**
	 * @param rs - information that has been received from server for further calculations
	 */
	public void handleMessageSelectDelayReport(Object rs)
	{
		ArrayList<ArrayList<Integer>> result = (ArrayList<ArrayList<Integer>> ) rs;
		DelayReportsController.instance.calculate(result);
	}
	
	/**
	 * @param rs - information that has been received from server for further calculations
	 */
	public void handleMessageFromServerExecutionCompleted(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;
		boolean val = result.get(0).booleanValue();

		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				ExecutionController.getInstance().showActionExecutionCompleted(val);
			}

		});
	}

	public void handleMessageFromServerExecutionSuggestNumberOfDays(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;
		boolean val = result.get(0).booleanValue();
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				ExecutionController.getInstance().showActionSetDays(val);
			}

		});
	}

	public void handleMessageFromServerMoreInfoDecision(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;
		boolean val = result.get(0).booleanValue();
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				DecisionController.getInstance().showActionMoreInfoMessage(val);
			}

		});
	}
	public void handleMessageFromServerApproveDecision(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;
		boolean val = result.get(0).booleanValue();
		//DecisionController.getInstance().setAnswerFromServerApprove(val);
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				DecisionController.getInstance().showActionApproveMessage(val);
			}

		});
	}
	public void handleMessageFromServerGetEvalutionForm(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Object> result = (ArrayList<Object>) rs;
		int processID = (int) result.get(0);
		String appraiserID = result.get(1).toString();
		String requestedChange = result.get(2).toString();
		String result1 = result.get(3).toString();
		String constraitsAndRisks = result.get(4).toString();

		EvaluationReport er = new EvaluationReport(processID, appraiserID, requestedChange,
				result1, constraitsAndRisks);
		ControllerProcessMain.setEvaluationReports(er);
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				ControllerProcessMain.getInstance().continueChairman1();
			}

		});
		
		
	}
	public void handleMessageFromServerFillEvalutionForm(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;
		boolean val = result.get(0).booleanValue();
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				EvaluationController.getInstance().showActionSubmitEvaluation(val);
			}

		});
	}

	public void handleMessageFromServerFillEvalutionNumberOfDays(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;

		boolean val = result.get(0).booleanValue();
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				EvaluationController.getInstance().showActionSubmiDays(val);
			}

		});
	}
	public void handleMessageFromServerGet_Active_Statistic(Object message)
	{
		System.out.println("go to calculate");
		ArrayList<ArrayList<Integer>> arr= (ArrayList<ArrayList<Integer>>) message;
		ActiveReportsController.instance.calaulate(arr);

	}

	private void handleMessageFromServerDownloadFile(ArrayList<?> parmas) {
		System.out.println("File received");
		MyFile myfile = (MyFile) parmas.get(0);

		String newFileNamePath = ".\\File_Download\\"+myfile.getFileName().split("_")[4];

		try {
			FileOutputStream fos;
			fos = new FileOutputStream(newFileNamePath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			/* The following code can save another version of the file in 
			 * the project's directory:*/
			try {
				bos.write(myfile.mybytearray, 0, myfile.getSize());
				bos.flush();
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void handleMessageFromServerREJECTE_PROCESS(Object message)
	{
		ArrayList<String> arr= (ArrayList<String>) message;

		if(arr.get(0).equals("Successfully rejected"))
		{
		}
		else
			new Alert(AlertType.ERROR,"There was an issue to reject this process").show();


	}
	
	private void handleMessageFromServerFreezeProcess(Object message) {
	ArrayList<String> arr= (ArrayList<String>) message;
	
	if(arr.get(0).equals("Succesfully Suspended"))
	{
		
		Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException �Not on FX application thread�
    	    public void run() {
    			Optional<ButtonType> result = new Alert(AlertType.CONFIRMATION,"Process Successfully suspended",ButtonType.OK).showAndWait();
    			Supervisor_ProcessMain_Controller.getInstance().updateProcessInformation();
    			Supervisor_ProcessMain_Controller.getInstance().adjustSuspendedButtons();
    	    }
    	});
	}
	else
		new Alert(AlertType.ERROR,"There was an issue to suspend this process").show();
}
	
	/**
	 * 
	 * @param message - the result of the shutdown action
	 */
	private void handleMessageFromServerShutdownProcess(Object message) {
		ArrayList<String> arr= (ArrayList<String>) message;

		if(arr.get(0).equals("Successfully Shutdown"))
		{
		}
		else
			new Alert(AlertType.ERROR,"There was an issue to shutdown this process").show();
		
		Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException �Not on FX application thread�
    	    public void run() {
    			Optional<ButtonType> result = new Alert(AlertType.CONFIRMATION,"Process Successfully shutdown , going back to previous page",ButtonType.OK).showAndWait();
	   			 if(result.get() == ButtonType.OK)
						Supervisor_ProcessMain_Controller.getInstance().back_click(null);
    	    }
    	});
	}

	/**
	 * 
	 * @param message - the result of the defrost action
	 */
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

		StaffMainController.instance.setData(arr);


	}


	public void handlerMessageFromServerCURRENT_IN_ROLE(Object message)
	{
		ArrayList<String> arr= (ArrayList<String>)message;
		System.out.println("Role"+arr.get(2)+"  "+arr.size());

		if(arr.get(2).equals("Chairman"))
			arr.add("2");///its make it index 3
		else if(arr.get(2).equals("Supervisor"))
			arr.add("3");
		else if(arr.get(2).equals("Change Board Member-1")) // Information Engineer
			arr.add("4");
		else if(arr.get(2).equals("Change Board Member-2")) // Information Engineer
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


	public void handleMessageFromClientGUI(Object message) {
		try {
			sendToServer(message);
		} catch (IOException e) {
			System.out.println("Could not perform action to server");
			System.out.println(e.getMessage());
			quit();
		}
	}

	/**
	 * Handles client quit
	 */
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
	
	/*******************************************getProcessesFromServer*******************************************************/
	/**
	 * Gets the processes that related to client from server
	 */
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


		if(arr.get(2).equals("Chairman"))
			arr.add("2");///its make it index 3
		else if(arr.get(2).equals("Supervisor"))
			arr.add("3");
		else if(arr.get(2).equals("Change Board Member-1")) // Information Engineer
			arr.add("4");
		else if(arr.get(2).equals("Change Board Member-2")) // Information Engineer
			arr.add("5");

		arr.add("7");

		System.out.println("handlerMessageFromServerUpdatePermanent"+arr.get(0));
		StaffMainController.instance.printMessage(arr);
	}
	/*****************************************handlerMessageFromServerNewRequest*************************************************************/	

	/**
	 * Handles messages from server when a new request was submitted
	 * @param rs - the server's result of the new request
	 */
	public void handlerMessageFromServerNewRequest(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<Boolean> result = (ArrayList<Boolean>) rs;

		boolean val = result.get(0).booleanValue();
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				NewRequestController.getInstance().showAllert(val);
			}

		});
	}
	/*********************************************handlerMessageFromServerLogin*************************************************/	
	/**
	 * handles the login result from the server
	 * @param rs - result of the login to the server, holds the answer of the role of the logged in user
	 */
	public void handlerMessageFromServerLogin(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<String> result = (ArrayList<String>) rs;

		switch (result.get(0)) {
		case "correct match":
			Client.getInstance().setName(result.get(1));
			ScreenController.getScreenController().activate("processesMain");
			getProcessesFromServer();
			getRelatedMessages(result.get(1));
			break;
		case "Supervisor":

			Client.getInstance().setName(result.get(1));
			this.setRule(result.get(0));
			ScreenController.getScreenController().activate("processesMain");
			ControllerProcessMain.instance.ButtonAdjustmentSuperUser(result.get(0),"Active");
			System.out.println("hhhcheck");
			getAllProcessesFromServer();
			getRelatedMessages("Supervisor");
			break;
		case "Manager":
			Client.getInstance().setName(result.get(1));
			this.setRule(result.get(0));
			ScreenController.getScreenController().activate("processesMain");
			ControllerProcessMain.instance.ButtonAdjustmentSuperUser(result.get(0),"Active");
			getAllProcessesFromServer();
			getRelatedMessages("Manager");
			break;
		case "Chairman":
			Client.getInstance().setName(result.get(1));
			this.setRule(result.get(0));
			ScreenController.getScreenController().activate("processesMain");
			ControllerProcessMain.instance.ButtonAdjustmentSuperUser(result.get(0),"Active");
			getAllProcessesFromServer();
			getRelatedMessages("Chairman");
			break;
		case "Login failed, username and password did not match":
			Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException �Not on FX application thread�
				public void run() {
					Alert alert = new Alert(AlertType.INFORMATION);

					alert.setTitle("ERROR");
					alert.setHeaderText("Login failed");
					alert.setContentText("Username and password did not match");
					alert.showAndWait();
					return;
				}
			});
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
	/*****************************************	*****************************************************/
	public void handlerMessageFromServerProcesses(Object rs) {
		Processes processes = new Processes();
		ArrayList<ArrayList<?>> result = new ArrayList<ArrayList<?>>();	
		result = (ArrayList<ArrayList<?>>) rs ;
		if(!(result.get(0).get(0).toString().equals("No processes")))
		{
			for (int i = 0; i < result.size(); i=i+3) {
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
				try{
					if(result.get(i+2) != null) process.setRelatedDocuments((ArrayList<String>) result.get(i+2));
				}
				catch(IndexOutOfBoundsException ex)
				{
					System.out.println("no files?");
				}
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
			for (int i = 0; i < result.size(); i=i+3) {                                                                         
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
				if(result.get(i+2) != null)
					process.setRelatedDocuments((ArrayList<String>) result.get(i+2));
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

		//TODO: SEND NOTIFICATION TO APPRAISER , get appraiser in rs

	}

	public void handlerMessageFromServerGetAppOrPLofProc(Object rs)
	{

		ArrayList <String> names = (ArrayList<String>)rs;
		ArrayList <String> fullNames = new ArrayList <String>();

		if((names.get(0)).toString().equals("NO EMPLOYEES WERE FOUND"))
		{
			fullNames.add(new String("Not Selected"));//appraiser
			fullNames.add(new String("Not Selected"));//performance leader
		}
		else
		{
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
		}

		System.out.println("Client full names: " + fullNames);
		Supervisor_ProcessMain_Controller.instance.setAppraiserAndPerformanceLeaderLabels(fullNames);
	}

	public void getRelatedMessages(String str)
	{
		ArrayList<Object> check = new ArrayList<Object>();

		check.add(str);

		Translator translator = new Translator(OptionsOfAction.GET_RELATED_MESSAGES, check);
		Client.getInstance().handleMessageFromClientGUI(translator);
	}

	public void setRelatedMessages(Object rs)
	{
		System.out.println("setRelatedMessages");
		ArrayList<Object> result = (ArrayList<Object>)rs;
		ArrayList<String> messages = new ArrayList<String>();


		System.out.println("setRelatedMessages" + result.size());

		for(int i=0 ; i < result.size()/6 ; i++)
		{
			messages.add(new String((Date)result.get(6*i+5) + "  Process ID  " + (int)result.get(6*i) + ":  "
					+ (String)result.get(6*i+3) + " - " + (String)result.get(6*i+1)));

			if((int)result.get(6*i+2) == 0)
			{
				messages.set(i, messages.get(i) + ".\n");
			}
			else
			{
				messages.set(i, messages.get(i) + ", " + (int)result.get(6*i+2) + " days.\n");
			}
		}
		System.out.println("!" + messages + "!");
		ControllerProcessMain.instance.setRelatedMessages(messages, result);
	}

	private void handleMessageFromServerSendRecoveredPassword(Object arr)
	{
		ArrayList <String> emailAndPassword = (ArrayList <String>)arr;
		
		System.out.println("Client - sendRecoveredPassword - emailAndPassword = " + emailAndPassword);
		LoginController.instance.sendRecoveredPasswordToUserEmail(emailAndPassword);
	}
	
	private void handleMessageFromServerGetTemporaryWorkersFromDB(Object rs)
	{
		ArrayList<Object> result = (ArrayList<Object>) rs;
		ArrayList<Object> fixedName = new ArrayList<Object>();
		
		if(result.size() == 1)
		{
			System.out.println("Client - handleMessageFromServerGetTemporaryWorkersFromDB - result.size() = 1");
			return;
		}
		
		System.out.println("Client - handleMessageFromServerGetTemporaryWorkersFromDB 1");
		
		for(int i=0; i<result.size() ; i++)
		{
			if(i%5 != 1)
				fixedName.add(result.get(i));
			else
			{
				fixedName.add(new String ((String)result.get(i) + " " + (String)result.get(i+1)));
				i++;
			}
		}
		System.out.println(fixedName);
		
		StaffMainController.instance.setTemporaryWorkersInTable(fixedName);
	}
	
	private void handleMessageFromServerGetPermanentWorkersFromDB(Object rs)
	{
		ArrayList<Object> result = (ArrayList<Object>) rs;
		ArrayList<Object> fixedName = new ArrayList<Object>();
		
		if(result.size() == 1)
		{
			System.out.println("Client - handleMessageFromServerGetPermanentWorkersFromDB - result.size() = 1");
			return;
		}
		
		System.out.println("Client - handleMessageFromServerGetPermanentWorkersFromDB 1");
		
		for(int i=0; i<result.size() ; i++)
		{
			if(i%4 != 1)
				fixedName.add(result.get(i));
			else
			{
				fixedName.add(new String ((String)result.get(i) + " " + (String)result.get(i+1)));
				i++;
			}
		}
		System.out.println(fixedName);
		
		StaffMainController.instance.setPermanentWorkersInTable(fixedName);
	}
	
}
