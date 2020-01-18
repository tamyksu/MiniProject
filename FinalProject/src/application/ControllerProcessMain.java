package application;

import java.awt.AWTException;

import java.awt.event.InputEvent;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import com.mysql.cj.result.Row;
import com.sun.scenario.effect.Effect.AccelType;

import client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import translator.OptionsOfAction;
import translator.Translator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler.*; 
import javafx.event.EventHandler;

public class ControllerProcessMain implements Initializable {
	public static ControllerProcessMain instance;
	
	private ArrayList<Object> messagesData;
	
	private String roleInProc;
	
	private String procStage = "";
	

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
	
    @FXML
    public Button logout_btn;
	
	 @FXML
	private TextArea notifications_text;

	@FXML
	private MenuButton DocumentsMenu;
	UserProcess process;
	
	@FXML
    private TextArea extension_request_text;
	
	private static EvaluationReport evaluationReports; // current evaluation report
	
	
	/**
	 * Set instance
	 * @param instance
	 */
	public static void setInstance(ControllerProcessMain instance) {
		ControllerProcessMain.instance = instance;
	}

	@FXML
	private Button DownloadFiles;
	
	/**
	 * Open "New Request" Screen
	 * @param event
	 */
	@FXML
	void newRequest(ActionEvent event) {
		NewRequestController.getInstance().loadPage();
		ScreenController.getScreenController().activate("newRequest"); 
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		initializeButtons();
		DocumentsMenu.getItems().clear();
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
	
	/**
	 * Identifies that you clicked on a process row in the table and invokes the function 
	 * "updateFieldsOfRequestMarked" to match the table to the process
	 */
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
	
	/**
	 * /Updates the relevant fields by the process that is specified in the process table
	 * @param person
	 */
	public void updateFieldsOfRequestMarked(Person person) {
        Documents.setText("Document selected");
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
		this.procStage = process.getProcess_stage();
		arrangesTheDocumentsAsClickableStrings(process.getRelatedDocuments());
		if(process.getRole().toLowerCase().equals("supervisor") || process.getRole().toLowerCase().equals("manager"))
			ButtonAdjustmentSuperUser(process.getRole(), process.getStatus());
		else
			ButtonAdjustment(process.getRole());
		Supervisor_ProcessMain_Controller.instance.initializeChosenProcessScreen(process.getProcess_stage());//to initiate the flag
		ExaminationController.instance.initializeChosenProcessScreen(process.getProcess_stage());
	}

	
	/////////////////func set documents in  DocumentsMenu//////////////////////////////////////////
	private void arrangesTheDocumentsAsClickableStrings(ArrayList<String> relatedDocuments) {
		DocumentsMenu.getItems().clear();
        if(relatedDocuments == null) return;
		// create action event 
        ArrayList<String> arrayList = new ArrayList<String>();
        ArrayList<String> arrayList1 = new ArrayList<String>();
		arrayList.addAll(relatedDocuments);
		for (int i = 0; i < arrayList.size(); i++) {
			arrayList1.add(arrayList.get(i).split("_")[4]);
		}
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() { 
            public void handle(ActionEvent e) 
            { 
                Documents.setText(((MenuItem)e.getSource()).getText() + " selected"); 
            } 
        }; 
		for (int i = 0; i < arrayList1.size(); i++) {
        	MenuItem m1 = new MenuItem(arrayList1.get(i)); 
            // add action events to the menuitems 
            m1.setOnAction(event1);
            DocumentsMenu.getItems().add(m1); 

		}
   	}
	
	/////////////////func related to documents//////////////////////////////////////////
	/**
	 * Download file
	 */
	@FXML
	public void downloadFiles() {
		//tableView.getSelectionModel().getSelectedItem()
		if(Documents.getText().equals("Document selected")) return;
		String path = getFilePath(Documents.getText().split(" ")[0]);
		ArrayList<Object> file = new ArrayList<Object>();
		file.add(path);
		Translator translator = new Translator(OptionsOfAction.DOWNLOADFILE, file);
		Client.getInstance().handleMessageFromClientGUI(translator);
		showSeccessAlert();
	}
    public void showSeccessAlert() {
    	Alert alert =new Alert(AlertType.INFORMATION, "File was received.");
    	alert.setTitle("Successfully downloaded");
    	alert.show();
    }

    /**
     * Get the path of the file
     * @param text
     * @return file's path
     */
	private String getFilePath(String text) {
		Person currentProcess = tableView.getSelectionModel().getSelectedItem();
		return ".\\Files_Server_Recieved\\"+currentProcess.getRequestId()+"_"+InitiatorName.getText()+"_"+text;
	}


	/**
	 * The function responsible for matching buttons to the process is indicated in the table
	 * @param userRole
	 */
	private void ButtonAdjustment(String userRole) {
		System.out.println("ButtonAdjustment: userRole1 = " + userRole);
		switch (userRole.toLowerCase()) {
		case "initiator":
			fitInitiator();
			break;
		case "appraiser":
			fitAppraiser();
			break;
		case "performance leader":
			fitPerformanceLeader();
			break;	
		case "examiner":
			fitExaminer();
			break;
		case "chairman":
			fitChairman();
			break;
		default:
			//disable all but newRequest button
			initializeButtons();
			break;
		}
		
	}
	
	/**
	 * The function responsible for matching buttons to the process is indicated in the table
	 * @param userRole
	 * @param processStatus
	 */
	public void ButtonAdjustmentSuperUser(String userRole, String processStatus) {
		System.out.println("ButtonAdjustmentSuperUser: userRole2 = " + userRole);
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
				
			case "chairman":
				if(processStatus.toLowerCase().equals("suspended"))
					fitChairmanDisabled();
				else if(processStatus.toLowerCase().equals("shutdown"))
					fitChairmanDisabled();
				else
					fitChairman();
				break;
		
			default:
				break;
		}
	
	}
	
	
	
	//change button disability in accordance to appraiser
	private void fitChairman() {
		if(this.procStage.compareTo("5") == 0 || this.procStage.compareTo("5.6") == 0 || this.procStage.compareTo("10") == 0)
		{
			newRequestBtn.setDisable(true);
			extension_btn.setDisable(false);
			extension_request_text.setDisable(false);
			evaluation_btn.setDisable(true);
			decision_btn.setDisable(false);
			execution_btn.setDisable(true);
			examination_btn.setDisable(true);
			supervisor_mode_btn.setDisable(true);
			director_btn.setDisable(true);
			defrost_btn.setDisable(true);
		}
		
		else
		{
			if(this.procStage.compareTo("5.5") == 0 || this.procStage.compareTo("5.6") == 0)
			{
				newRequestBtn.setDisable(true);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(true);
				decision_btn.setDisable(false);
				execution_btn.setDisable(true);
				examination_btn.setDisable(true);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
			
			else
			{
				newRequestBtn.setDisable(true);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(true);
				decision_btn.setDisable(true);
				execution_btn.setDisable(true);
				examination_btn.setDisable(true);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
		}
	}


	/**
	 * Change button disability in accordance to Appraiser
	 */
	private void fitAppraiser() {
	
		if(Integer.parseInt(this.procStage) == Constants.STAGE_OF_APPRAISER_EVALUATION)
		{
			newRequestBtn.setDisable(false);
			extension_btn.setDisable(false);
			extension_request_text.setDisable(false);
			evaluation_btn.setDisable(false);
			decision_btn.setDisable(true);
			execution_btn.setDisable(true);
			examination_btn.setDisable(true);
			supervisor_mode_btn.setDisable(true);
			director_btn.setDisable(true);
			defrost_btn.setDisable(true);
		}
		else
		{
			if(procStage.compareTo("4.5") == 0 || procStage.compareTo("4.6") == 0 || 
					Integer.parseInt(this.procStage) == Constants.STAGE_OF_APPRAISER_EVALUATION_DUE_TIME ||
					procStage.compareTo("2.5") == 0)
			{
				newRequestBtn.setDisable(false);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(false);
				decision_btn.setDisable(true);
				execution_btn.setDisable(true);
				examination_btn.setDisable(true);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
			
			else
			{
				newRequestBtn.setDisable(false);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(true);
				decision_btn.setDisable(true);
				execution_btn.setDisable(true);
				examination_btn.setDisable(true);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
		}
		
		
	}

	/**
	 * Change button disability in accordance to Supervisor
	 */
	private void fitSupervisor() {
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(false);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	/**
	 * Change button disability in accordance to Manager
	 */
	private void fitManager() {
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(false);
		defrost_btn.setDisable(true);
	}

	//
	/**
	 * 
	 */
	private void fitInitiator() {
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	
	private void fitChairmanDisabled() {

		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	
	}
	
	/**
	 * 
	 */
	private void fitManagerDisabled()
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
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
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	/**
	 * Performance Leader Options
	 */
	private void fitPerformanceLeader()
	{
		if(this.procStage.compareTo("9") == 0)
		{
			newRequestBtn.setDisable(false);
			extension_btn.setDisable(false);
			extension_request_text.setDisable(false);
			evaluation_btn.setDisable(true);
			decision_btn.setDisable(true);
			execution_btn.setDisable(false);
			examination_btn.setDisable(true);
			supervisor_mode_btn.setDisable(true);
			director_btn.setDisable(true);
			defrost_btn.setDisable(true);
		}
		
		else
		{
			if(this.procStage.compareTo("7") == 0 || this.procStage.compareTo("7.5") == 0 ||
					this.procStage.compareTo("9.5") == 0 || this.procStage.compareTo("9.6") == 0)
			{
				newRequestBtn.setDisable(false);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(true);
				decision_btn.setDisable(true);
				execution_btn.setDisable(false);
				examination_btn.setDisable(true);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
			else
			{
				newRequestBtn.setDisable(false);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(true);
				decision_btn.setDisable(true);
				execution_btn.setDisable(true);
				examination_btn.setDisable(true);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
			
		}
	}
	
	/**
	 * Examiner Options
	 */
	private void fitExaminer()
	{
		if(this.procStage.compareTo("11") == 0 || this.procStage.compareTo("11.1") == 0)
		{
			newRequestBtn.setDisable(false);
			extension_btn.setDisable(false);
			extension_request_text.setDisable(false);
			evaluation_btn.setDisable(true);
			decision_btn.setDisable(true);
			execution_btn.setDisable(true);
			examination_btn.setDisable(false);
			supervisor_mode_btn.setDisable(true);
			director_btn.setDisable(true);
			defrost_btn.setDisable(true);
		}
		
		else
		{
			if(this.procStage.compareTo("11.2") == 0 || this.procStage.compareTo("11.3") == 0 ||
					this.procStage.compareTo("11.5") == 0 || this.procStage.compareTo("11.6") == 0)
			{
				newRequestBtn.setDisable(false);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(true);
				decision_btn.setDisable(true);
				execution_btn.setDisable(true);
				examination_btn.setDisable(false);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
			
			else
			{
				newRequestBtn.setDisable(false);
				extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
				evaluation_btn.setDisable(true);
				decision_btn.setDisable(true);
				execution_btn.setDisable(true);
				examination_btn.setDisable(true);
				supervisor_mode_btn.setDisable(true);
				director_btn.setDisable(true);
				defrost_btn.setDisable(true);
			}
			
		}
	}
	
	
	/* Supervisor options
	 */
	private void fitSupervisorDisabled() 
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(false);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	/**
	 * disable all buttons on startup (before choosing a process from the table)
	 */
	private void initializeButtons() 
	{
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	
	/**
	 * Director options when process is on Shutdown
	 */
	private void fitDirectorShutdown() 
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(false);
		defrost_btn.setDisable(true);
	}
	
	
	/**
	 * Supervisor options when process is on Shutdown
	 */
	private void fitSupervisorShutdown() 
	{
		newRequestBtn.setDisable(true);
		extension_btn.setDisable(true);
		extension_request_text.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		supervisor_mode_btn.setDisable(true);
		director_btn.setDisable(true);
		defrost_btn.setDisable(true);
	}
	
	/**
	 * Open Director screen
	 * @param event
	 */
	@FXML
	void director_click(ActionEvent event) {
		ScreenController.getScreenController().activate("staffMain");
		StaffMainController.instance.getChairManData();
		StaffMainController.instance.getTemporaryWorkersFromDB();
		StaffMainController.instance.getPermanentWorkersFromDB();
		
	}
	
	/**
	 * Extention
	 * @param event
	 */
	@FXML
	void extension_click(ActionEvent event) {
		//Check if days to due time <= 3
		Date dueDate;
		try {
			dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(CurrentStageDueTime.getText());
			Date currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
			
			long diff = dueDate.getTime() - currentDate.getTime();
		    long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		    
		    if(days>3)
		    {
		    	new Alert(AlertType.INFORMATION,"You can ask for extention only when 3 or less days left until due date").show();
		    }
		    
		    else
		    {
		    	if(extension_request_text.getText().compareTo("") == 0)
		    	{
			    	new Alert(AlertType.INFORMATION,"You must fill explanation for the extension request").show();
			    	return;
		    	}
		    	ArrayList <Object> arr = new ArrayList<Object>();
		    	
		    	arr.add(getSelectedRowProcID());//process id
		    	arr.add(extension_request_text.getText());//reason
		    	arr.add(getSelectedRowRole());//role
		    	arr.add(this.procStage);//process stage
		    	
		    	Translator translator = new Translator(OptionsOfAction.SEND_EXTENSION_REQUEST, arr);
		    	Client.getInstance().handleMessageFromClientGUI(translator);
		    	extension_request_text.clear();
		    	getTheUpdateProcessesFromDB();
		    	
		    	extension_btn.setDisable(true);
				extension_request_text.setDisable(true);
		    }
		} catch (ParseException e) {
			e.printStackTrace();
	    	new Alert(AlertType.ERROR,"This process doesn't have a due date").show();
		}
	    
	}

	/**
	 * Open evaluation screen
	 * @param event
	 */
	@FXML
	void evaluation_click(ActionEvent event) {
		int proc = getSelectedRowProcID();
		
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
		if(Double.parseDouble(process.getProcess_stage())>=5) {
			new Alert(AlertType.ERROR, "Already evaluated!").show();
			return;
		}
		
		EvaluationController.getInstance().pageLoad(Double.parseDouble(process.getProcess_stage()));
		System.out.println(process.getProcess_stage() +" process stage");
		ScreenController.getScreenController().activate("evaluation");
		EvaluationController.instance.updateProcessInformation();
	}

	/**
	 * Defrost process
	 * @param event
	 */
	@FXML
	void defrost_click(ActionEvent event) {
		
		ArrayList<Object> processInfo = new ArrayList<Object>();
		
		processInfo.add(RequestID.getText());
		
		Translator translator = new Translator(OptionsOfAction.DEFROST_PROCESS, processInfo);
		Client.getInstance().handleMessageFromClientGUI(translator);
	}
	

	/**
	 * Chairman Screen
	 * @param event
	 */
	@FXML
	void decision_click(ActionEvent event) {
		int proc = getSelectedRowProcID();
		
		if(proc == -1)
			return;
			
		if(process.getProcess_stage().isEmpty()) {
			new Alert(AlertType.ERROR, "Error!").show();
			return;
		}
		
		if(Double.parseDouble(process.getProcess_stage())<5) {
			new Alert(AlertType.ERROR, "Unable to make a decision, not yet!").show();
			return;
		}
		
		if(Double.parseDouble(process.getProcess_stage())>=6 && Double.parseDouble(process.getProcess_stage())<10) {
			new Alert(AlertType.ERROR, "Already made a decision, still early to appoint examnier.").show();
			return;
		}
		
		if(Double.parseDouble(process.getProcess_stage())>10) {
			new Alert(AlertType.ERROR, "Already made a decision appointed examiner").show();
			return;
		}
		
		
		if(Double.parseDouble(process.getProcess_stage())>=5 && Double.parseDouble(process.getProcess_stage())<6) {
			ArrayList<Integer> arr = new ArrayList<>();
			arr.add(process.getRequest_id()); 
			Translator translator = new Translator(OptionsOfAction.Get_Evaluation_Report_For_Process_ID, arr);
			Client.getInstance().handleMessageFromClientGUI(translator);
			//try { Thread.sleep(750); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
			//DecisionController.getInstance().loadPage(evaluationReports);
		}
		if(Double.parseDouble(process.getProcess_stage())==10) {
			DecisionController.getInstance().loadPage(new EvaluationReport());
			ArrayList<Object> arr = new ArrayList<>();
			Translator translator = new Translator(OptionsOfAction.Get_All_Change_Board_Members, arr);
			Client.getInstance().handleMessageFromClientGUI(translator);
		}
		
		//ScreenController.getScreenController().activate("decisionMaking");
		//DecisionController.instance.updateProcessInformation();
	}
	
	/**
	 * Continue Chairman options when need to Make a decision
	 */
	public void continueChairman1() {
		DecisionController.getInstance().loadPage(evaluationReports);
		ScreenController.getScreenController().activate("decisionMaking");
		DecisionController.instance.updateProcessInformation();
	}
	
	/**
	 * Continue Chairman options when need to appoint an examiner
	 */
	public void continueChairman2() {
		
		ScreenController.getScreenController().activate("decisionMaking");
		DecisionController.instance.updateProcessInformation();
	}
	

	/**
	 * Execution Screen
	 * @param event
	 */
	@FXML
	void execution_click(ActionEvent event) {
		int proc = getSelectedRowProcID();
		
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

	/**
	 * Examination Screen
	 * @param event
	 */
	@FXML
	void examination_click(ActionEvent event) {
		int proc = getSelectedRowProcID();
		
		if(proc == -1)
			return;
		ScreenController.getScreenController().activate("examination");
		ExaminationController.instance.setProcessID(proc);		
		ExaminationController.instance.updateProcessInformation();

	}

	/**
	 * Supervisor options
	 * @param event
	 */
	@FXML
	void supervisorMode_click(ActionEvent event) {
		int proc = getSelectedRowProcID();
		
		if(proc == -1)
			return;
		ScreenController.getScreenController().activate("supervisor_processesMain");
		Supervisor_ProcessMain_Controller.instance.getAppraiserOrPerformanceLeaderCBData();
		Supervisor_ProcessMain_Controller.instance.getAppraiserAndPerformanceLeaderLabels();
		Supervisor_ProcessMain_Controller.instance.updateProcessInformation();
		Supervisor_ProcessMain_Controller.instance.setProcessID(proc);
		Supervisor_ProcessMain_Controller.instance.setDueTimeRequest(this.messagesData);
		Supervisor_ProcessMain_Controller.instance.setDueTimeExtensionExplanation();
		
	}

	/**
	 * Shut down
	 * @param event
	 */
	@FXML
	void shutdown_click(ActionEvent event) {
		ArrayList<Object> processInfo = new ArrayList<Object>();
		
		processInfo.add(RequestID.getText());
		
		Translator translator = new Translator(OptionsOfAction.SHUTDOWN_PROCESS, processInfo);
		Client.getInstance().handleMessageFromClientGUI(translator);
	}

	/**
	 * Update tables
	 */
	@FXML
	public void getTheUpdateProcessesFromDB() {
		switch (Client.getInstance().getRole()) {
		case "Supervisor":
			Client.getInstance().getAllProcessesFromServer();
			fitSupervisor();
			break;
		case "Manager":
			Client.getInstance().getAllProcessesFromServer();
			fitManager();
			break;
		case "Chairman":
			Client.getInstance().getAllProcessesFromServer();
			fitChairman();
			break;
		default:
			Client.getInstance().getProcessesFromServer();
			initializeButtons();
			break;
		}
			
	}
	
	public String getSelectedRowRole()
	{
		return tableView.getSelectionModel().getSelectedItem().getRole();
	}
	
	public int getSelectedRowProcID()
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
	
	
	/**
	 * Get the ID of the user
	 * @return
	 */
	public String getRequestID()
	{
		return this.RequestID.getText();
	}
    
    public void setRelatedMessages(ArrayList <String> messages, ArrayList<Object> msgData)
    {
    	notifications_text.setText("");
    	this.messagesData = msgData;
    	
    	for(int i=messages.size()-1; i >= 0 ; i--)
    		notifications_text.setText(notifications_text.getText() + messages.get(i));
    	if(messages.size() == 0)
    		notifications_text.setText("No Notifications");
    }
    
    /**
     * Set Evaluation Report
     * @param evaluationReports
     */
	public static void setEvaluationReports(EvaluationReport evaluationReports) {
		ControllerProcessMain.evaluationReports = evaluationReports;
	}

}
