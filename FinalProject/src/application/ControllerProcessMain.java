package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private Button freeze_btn;

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
	private Button shutdown_btn;

	@FXML
	private Button extension_btn;

	@FXML
	private Button defrost_btn;

	@FXML
	private Button examination_btn;

	@FXML
	private Button newRequestBtn;

	public static void setInstance(ControllerProcessMain instance) {
		ControllerProcessMain.instance = instance;
	}

	@FXML
	void newRequest(ActionEvent event) {
		ScreenController.getScreenController().activate("newRequest");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
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
		UserProcess process = Client.getInstance().getProcesses().getMyProcess().get(person.getRequestId());
		InitiatorName.setText(process.getIntiatorId());
		InitiatorEmail.setText(process.getEmail());
		InformationSystem.setText("" + process.getSystem_num());
		CurrentState.setText(process.getCurrent_stage_due_date());
		RequestedChange.setText(process.getRequest_description());
		Explanation.setText(process.getExplanaton());
		Notes.setText(process.getNotes());
		RequestDate.setText(process.getCreation_date());
		RequestID.setText("" + process.getRequest_id());
//		Documents.setText//
		currentStatus.setText(process.getStatus());
		RequestedChange.setText(process.getRequest_description());
		ButtonAdjustment(process.getRole());
		Supervisor_ProcessMain_Controller.instance.initializeChosenProcessScreen(process.getProcess_stage());//to initiate the flag
	}

	//The function responsible for matching buttons to the process is indicated in the table
	private void ButtonAdjustment(String userRole) {
		switch (userRole) {
		case "initiator":
			fitInitiator();
			break;
		case "manager":
			fitManager();
			break;
		case "supervisor":
			fitSupervisor();
			break;	
		case "appraiser":
			fitAppraiser();
			break;
		case "Chairman":
			fitChairman();
			break;
			
		default:
			break;
		}
		
	}
	//change button disability in accordance to appraiser
	private void fitChairman() {
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(false);
		decision_btn.setDisable(false);
		execution_btn.setDisable(true);
		examination_btn.setDisable(false);
		shutdown_btn.setDisable(true);
		freeze_btn.setDisable(true);
		defrost_btn.setDisable(false);		
	}

	//change button disability in accordance to appraiser
	private void fitAppraiser() {
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(false);
		decision_btn.setDisable(false);
		execution_btn.setDisable(true);
		examination_btn.setDisable(false);
		shutdown_btn.setDisable(true);
		freeze_btn.setDisable(true);
		defrost_btn.setDisable(false);			
	}

	//change button disability in accordance to supervisor
	private void fitSupervisor() {
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(false);
		decision_btn.setDisable(false);
		execution_btn.setDisable(true);
		examination_btn.setDisable(false);
		shutdown_btn.setDisable(true);
		freeze_btn.setDisable(true);
		defrost_btn.setDisable(false);	
	}

	private void fitManager() {
		newRequestBtn.setDisable(false);
		extension_btn.setDisable(true);
		evaluation_btn.setDisable(true);
		decision_btn.setDisable(true);
		execution_btn.setDisable(true);
		examination_btn.setDisable(true);
		shutdown_btn.setDisable(true);
		freeze_btn.setDisable(true);
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
		shutdown_btn.setDisable(true);
		freeze_btn.setDisable(true);
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
		ScreenController.getScreenController().activate("evaluation");
	}

	@FXML
	void defrost_click(ActionEvent event) {

	}

	@FXML
	void decision_click(ActionEvent event) {
		ScreenController.getScreenController().activate("decisionMaking");
	}

	@FXML
	void execution_click(ActionEvent event) {
		ScreenController.getScreenController().activate("execution");
	}

	@FXML
	void examination_click(ActionEvent event) {
		ScreenController.getScreenController().activate("examination");
	}

	@FXML
	void supervisorMode_click(ActionEvent event) {
		ScreenController.getScreenController().activate("supervisor_processesMain");
		Supervisor_ProcessMain_Controller.instance.getAppraiserOrPerformanceLeaderCBData();
		Supervisor_ProcessMain_Controller.instance.getAppraiserAndPerformanceLeaderLabels();
	}
	
	@FXML
	void shutdown_click(ActionEvent event) {

	}

	@FXML
	public void getTheUpdateProcessesFromDB() {
		switch (Client.getInstance().getRole()) {
		case "Supervisor":
			Client.getInstance().getAllProcessesFromServer();
			break;
		case "Manager":
			Client.getInstance().getAllProcessesFromServer();
			break;
		default:
			Client.getInstance().getProcessesFromServer();
			break;
		}
			
			
			
	}
	
	public int getSelectedRowNumber()
	{
		return tableView.getSelectionModel().getSelectedItem().getRequestId();
	}

}
