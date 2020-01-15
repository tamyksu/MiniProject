package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.scenario.effect.Effect.AccelType;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import translator.OptionsOfAction;
import translator.Translator;
import javafx.event.ActionEvent;

public class ControllerProcessMain implements Initializable {
	public static ControllerProcessMain instance;

	@FXML
	private TableView<Person> tableView;

	@FXML
	private TableColumn<Person, Integer> processId;

	@FXML
	private TableColumn<Person, Integer> InformationSystemColumn;

	@FXML
	private TableColumn<Person, String> StatusColumn;

	@FXML
	private TableColumn<Person, String> DateColumn;

	@FXML
	private TableColumn<Person, String> Role;

	@FXML
	private Button execution_btn;
	@FXML
	private Button evaluation_btn;
	@FXML
	private Label InitiatorName;

	@FXML
	private Label InitiatorEmail;

	@FXML
	private Label InformationSystem;

	@FXML
	private Label CurrentState;

	@FXML
	private Label RequestedChange;

	@FXML
	private Label Explanation;

	@FXML
	private Button decision_btn;
	@FXML
	private Label Notes;

	@FXML
	private Label RequestDate;

	@FXML
	private Label RequestID;

	@FXML
	private Label Documents;

	@FXML
	private Label currentStatus;

	@FXML
	private Label CurrentStageDueTime;

	@FXML
	private Button updateTable;

	@FXML
	private Button extension_btn;

	@FXML
	private Button defrost_btn;

	@FXML
	private Button examination_btn;

	@FXML
	private Button newRequestBtn;
	
	@FXML
	private Button supervisor_mode_btn;
	
	@FXML
	private Button director_btn;
	
	UserProcess process;
	
	private static EvaluationReport evaluationReports; // current evaluation report
	
	public static void setInstance(ControllerProcessMain instance) {
		ControllerProcessMain.instance = instance;
	}

	@FXML
	void newRequest(ActionEvent event) {
		NewRequestController.getInstance().loadPage();
		ScreenController.getScreenController().activate("newRequest");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		initializeButtons();
	}

	public static ControllerProcessMain getInstance() {
		return instance;
	}

	public void SetInTable(Object rs) {
		Processes processes = (Processes) rs;
		ObservableList<Person> data;
		ArrayList<Person> personList = new ArrayList<Person>();

		if (processes.getMyProcessesInArrayList().isEmpty()) {
			if (this.tableView.isEditable() == false) {
				this.tableView.setEditable(true);
			}
		}

		else {
			if (this.tableView.isEditable() == false) {
				this.tableView.setEditable(true);
			}
			for (int i = 0; i < processes.getMyProcessesInArrayList().size(); i++) {
				Person person = new Person(processes.getMyProcessesInArrayList().get(i).getRequest_id(),
						processes.getMyProcessesInArrayList().get(i).getSystem_num(),
						processes.getMyProcessesInArrayList().get(i).getStatus(),
						processes.getMyProcessesInArrayList().get(i).getCreation_date(),
						processes.getMyProcessesInArrayList().get(i).getRole());
				personList.add(person);
			}
		}

		data = FXCollections.observableArrayList(personList);
		this.processId.setCellValueFactory(new PropertyValueFactory<Person, Integer>("requestId"));
		this.InformationSystemColumn
				.setCellValueFactory(new PropertyValueFactory<Person, Integer>("informationSystem"));
		this.StatusColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("status"));
		this.DateColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("date"));
		this.Role.setCellValueFactory(new PropertyValueFactory<Person, String>("role"));
		this.tableView.setItems(data);
		setInfo();
	}
	//Identifies that you clicked on a process row in the table and invokes the function 
	//"updateFieldsOfRequestMarked" to match the table to the process
	public void setInfo() {
		tableView.setRowFactory(tv -> {
			TableRow<Person> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 1 && (!row.isEmpty())) {
					updateFieldsOfRequestMarked(tableView.getSelectionModel().getSelectedItem());
				}
			});
			return row;
		});
	}
	//Updates the relevant fields by the process that is specified in the process table
	public void updateFieldsOfRequestMarked(Person person) {
		process = Client.getInstance().getProcesses().getMyProcess().get(person.getRequestId());
		InitiatorName.setText(process.getIntiatorId());
		InitiatorEmail.setText(process.getEmail());
		InformationSystem.setText("" + process.getSystem_num());
		CurrentState.setText(MyHashMaps.getProcessStageText(Double.parseDouble(process.getProcess_stage())));
		RequestedChange.setText(process.getRequest_description());
		Explanation.setText(process.getExplanaton());
		Notes.setText(process.getNotes());
		RequestDate.setText(process.getCreation_date());
		RequestID.setText("" + process.getRequest_id());
		currentStatus.setText(process.getStatus());
		CurrentStageDueTime.setText(process.getCurrent_stage_due_date());
		if(process.getRole().toLowerCase().equals("supervisor") || process.getRole().toLowerCase().equals("manager"))
			ButtonAdjustmentSuperUser(process.getRole(), process.getStatus());
		else
			ButtonAdjustment(process.getRole());
		Supervisor_ProcessMain_Controller.instance.initializeChosenProcessScreen(process.getProcess_stage());//to initiate the flag
		ExaminationController.instance.initializeChosenProcessScreen(process.getProcess_stage());
	}

	//The function responsible for matching buttons to the process is indicated in the table
	private void ButtonAdjustment(String userRole) {
		System.out.println("ButtonAdjustment: userRole = " + userRole);
		switch (userRole.toLowerCase()) {
		case "initiator":
			fitInitiator();
			break;
		case "appraiser":
			fitAppraiser();
			break;
			
		case "chairman":
			fitChairman();
			break;
		case "performance leader":
			fitPerformanceLeaderDisabled();
			break;	
		default:
			//disable all but newRequest button
			initializeButtons();
			break;
		}
		
	}
	
	//The function responsible for matching buttons to the process is indicated in the table
	public void ButtonAdjustmentSuperUser(String userRole, String processStatus) {
		System.out.println("ButtonAdjustmentSuperUser: userRole = " + userRole);
		switch (userRole.toLowerCase())
		{
			case "manager":
				if(processStatus.toLowerCase().equals("suspended"))
					fitManagerDisabled();
				else if(processStatus.toLowerCase().equals("shutdown"))
					fitDirectorShutdown();
				else
					fitManager();
				break;
			case "supervisor":
				if(processStatus.toLowerCase().equals("suspended"))
					fitSupervisorDisabled();
				else if(processStatus.toLowerCase().equals("shutdown"))
					fitSupervisorShutdown();
				else
					fitSupervisor();
				break;	
			
			default:
				break;
		}
	
	}
	
	//change button disability in accordance to Chairman
	private void fitChairman() {
		System.out.println("chairman");
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(false);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}

	//change button disability in accordance to appraiser
	private void fitAppraiser() {
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(false);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}

	//change button disability in accordance to supervisor
	private void fitSupervisor() {
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(false);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}

	private void fitManager() {
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(false);
		defrost_btn.setDisable(true);
	}

	//Suitable for the initiator of the process the buttons allowed

	private void fitInitiator() {
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	private void fitManagerDisabled()
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(false);
		defrost_btn.setDisable(false);
	}
	
	private void fitChangeBoardDisabled()
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	private void fitPerformanceLeaderDisabled()
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(false);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	private void fitSupervisorDisabled() 
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(false);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	//disable all buttons on startup (before choosing a process from the table)
	private void initializeButtons() 
	{
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	private void fitDirectorShutdown() 
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(false);
		defrost_btn.setDisable(true);
	}
	
	
	
	private void fitSupervisorShutdown() 
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	

	@FXML
	void director_click(ActionEvent event) {
		ScreenController.getScreenController().activate("staffMain");
		StaffMainController.instance.getChairManData();
		
	}

	@FXML
	void extension_click(ActionEvent event) {

	}

	@FXML
	void evaluation_click(ActionEvent event) {
		int proc = getSelectedRowNumber();
		
		if(proc == -1)
			return;
		
		if(process.getProcess_stage().isEmpty()) {
			new Alert(AlertType.ERROR, "Error!").show();
			return;
		}
		if(Double.parseDouble(process.getProcess_stage())<2) {
			new Alert(AlertType.ERROR, "Unabble to evaluate, not yet!").show();
			return;
		}
		if(Double.parseDouble(process.getProcess_stage())>4) {
			new Alert(AlertType.ERROR, "Already evaluated!").show();
			return;
		}
		
		EvaluationController.getInstance().pageLoad(Double.parseDouble(process.getProcess_stage()));
		System.out.println(process.getProcess_stage() +" process stage");
		ScreenController.getScreenController().activate("evaluation");
		EvaluationController.instance.updateProcessInformation();
	}

	@FXML
	void defrost_click(ActionEvent event) {
		
		ArrayList<Object> processInfo = new ArrayList<Object>();
		
		processInfo.add(RequestID.getText());
		
		Translator translator = new Translator(OptionsOfAction.DEFROST_PROCESS, processInfo);
		Client.getInstance().handleMessageFromClientGUI(translator);
	}

	@FXML
	void decision_click(ActionEvent event) {
		int proc = getSelectedRowNumber();
		
		if(proc == -1)
			return;
		if(process.getProcess_stage().isEmpty()) {
			new Alert(AlertType.ERROR, "Error!").show();
			return;
		}
		if(Double.parseDouble(process.getProcess_stage())<5) {
			new Alert(AlertType.ERROR, "Unabble to make a decision, not yet!").show();
			return;
		}
		if(Double.parseDouble(process.getProcess_stage())>5) {
			new Alert(AlertType.ERROR, "Already made a decision!").show();
			return;
		}
		ArrayList<Integer> arr = new ArrayList<>();
		arr.add(process.getRequest_id()); 
		Translator translator = new Translator(OptionsOfAction.Get_Evaluation_Report_For_Process_ID, arr);
		Client.getInstance().handleMessageFromClientGUI(translator);
		try { Thread.sleep(500); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
		DecisionController.getInstance().loadPage(evaluationReports);
		ScreenController.getScreenController().activate("decisionMaking");
		DecisionController.instance.updateProcessInformation();
	}

	@FXML
	void execution_click(ActionEvent event) {
		int proc = getSelectedRowNumber();
		
		if(proc == -1)
			return;
		
		if(process.getProcess_stage().isEmpty()) {
			new Alert(AlertType.ERROR, "Error!").show();
			return;
		}
		if(Double.parseDouble(process.getProcess_stage())<7) {
			new Alert(AlertType.ERROR, "Too early for the execution stage.").show();
			return;
		}
		if(Double.parseDouble(process.getProcess_stage())>9) {
			new Alert(AlertType.ERROR, "This process has passed the execution stage.").show();
			return;
		}
		ExecutionController.getInstance().loadPage(Double.parseDouble(process.getProcess_stage()));
		ScreenController.getScreenController().activate("execution");
		ExecutionController.instance.updateProcessInformation();
	}

	@FXML
	void examination_click(ActionEvent event) {
		int proc = getSelectedRowNumber();
		
		if(proc == -1)
			return;
		ScreenController.getScreenController().activate("examination");
		ExaminationController.instance.setProcessID(proc);		
		ExaminationController.instance.updateProcessInformation();

	}

	@FXML
	void supervisorMode_click(ActionEvent event) {
		ScreenController.getScreenController().activate("supervisor_processesMain");
		Supervisor_ProcessMain_Controller.instance.getAppraiserOrPerformanceLeaderCBData();
		Supervisor_ProcessMain_Controller.instance.getAppraiserAndPerformanceLeaderLabels();
		Supervisor_ProcessMain_Controller.instance.updateProcessInformation();

	}
	
	@FXML
	void shutdown_click(ActionEvent event) {
		ArrayList<Object> processInfo = new ArrayList<Object>();
		
		processInfo.add(RequestID.getText());
		
		Translator translator = new Translator(OptionsOfAction.SHUTDOWN_PROCESS, processInfo);
		Client.getInstance().handleMessageFromClientGUI(translator);
	}

	@FXML
	public void getTheUpdateProcessesFromDB() {
		
		System.out.println("h"+Client.getInstance().getRole());
		switch (Client.getInstance().getRole()) {
		case "Supervisor":
	
			Client.getInstance().getAllProcessesFromServer();
			fitSupervisor();
			break;
		case "Manager":
			Client.getInstance().getAllProcessesFromServer();
			fitManager();
			break;
		default:
			Client.getInstance().getProcessesFromServer();
			initializeButtons();
			break;
		}
			
	}
	
	public int getSelectedRowNumber()
	{		
		try
    	{
    		int processID = tableView.getSelectionModel().getSelectedItem().getRequestId();//needs rows in the processes table
    		
    		return processID;
    	}
    	catch(NullPointerException e)
    	{
    		System.out.println("No row was chosen");
    		
    		Alert alert = new Alert(AlertType.INFORMATION);
        	
            alert.setTitle("ALERT");
            alert.setHeaderText("NO ROW WAS CHOSEN");
            alert.setContentText("Please select a row in the processes table");
            alert.showAndWait();
            return -1;
    	}
	}
	
	public String getRequestID()
	{
		return this.RequestID.getText();
	}
	
    @FXML
    void logout_click(ActionEvent event) {
    	try {
			Client.instance.closeConnection();
	    	System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public static void setEvaluationReports(EvaluationReport evaluationReports) {
		ControllerProcessMain.evaluationReports = evaluationReports;
	}
    
    

    
}
