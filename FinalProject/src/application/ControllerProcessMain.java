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


public class ControllerProcessMain  implements Initializable{

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
    
	public static ControllerProcessMain instance;
	

	public static void setInstance(ControllerProcessMain instance) {
		ControllerProcessMain.instance = instance;
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
		if (this.tableView.isEditable() == false) {
			this.tableView.setEditable(true);
		}
		for (int i = 0; i < processes.getMyProcessesInArrayList().size(); i++) {
			Person person = new Person(processes.getMyProcessesInArrayList().get(i).getRequest_id(),
			processes.getMyProcessesInArrayList().get(i).getSystem_num(), processes.getMyProcessesInArrayList().get(i).getStatus(),
			processes.getMyProcessesInArrayList().get(i).getCreation_date(), processes.getMyProcessesInArrayList().get(i).getRole());
			personList.add(person);
		}
		data = FXCollections.observableArrayList(personList);
		  this.processId.setCellValueFactory(new PropertyValueFactory<Person,Integer>("requestId"));
		  this.InformationSystemColumn.setCellValueFactory(new PropertyValueFactory<Person, Integer>("informationSystem"));
		  this.StatusColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("status"));
		  this.DateColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("date"));
		  this.Role.setCellValueFactory(new PropertyValueFactory<Person, String>("role"));
		  this.tableView.setItems(data);
		  setInfo();
	}
	
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
		
	public void updateFieldsOfRequestMarked(Person person){
		UserProcess process = Client.getInstance().getProcesses().getMyProcess().get(person.getRequestId());
		InitiatorName.setText(process.getIntiatorId());
		InitiatorEmail.setText(process.getEmail());
		InformationSystem.setText(""+process.getSystem_num());
		CurrentState.setText(process.getCurrent_stage_due_date());
		RequestedChange.setText(process.getRequest_description());
		Explanation.setText(process.getExplanaton());
		Notes.setText(process.getNotes());
		RequestDate.setText(process.getCreation_date());
		RequestID.setText(""+process.getRequest_id());
//		Documents.setText//
		currentStatus.setText(process.getStatus());
		RequestedChange.setText(process.getRequest_description());
	}

	@FXML
	public void getTheUpdateProcessesFromDB() {
		Client.getInstance().getProcessesFromServer();
	}

}



