package application;

import java.util.ArrayList;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class ControllerProcessMain {

	@FXML
	private TableColumn<?, ?> StatusColumn;

	@FXML
	private TableColumn<?, ?> InformationSystemColumn;

	@FXML
	private TextArea RequestID;

	@FXML
	private TextArea Explanation;

	@FXML
	private Button execution_btn;

	@FXML
	private TextArea RequestDate;

	@FXML
	private Button evaluation_btn;

	@FXML
	private TextArea RequestedChange;

	@FXML
	private TextArea InitiatorProfession;

	@FXML
	private TextArea CurrentState;

	@FXML
	private TextArea InformationSystem;

	@FXML
	private TextArea AssociatedDocuments;

	@FXML
	private TextArea Notes;

	@FXML
	private Label CurrentProcessStatus;

	@FXML
	private Button freeze_btn;

	@FXML
	private Button decision_btn;

	@FXML
	private TextArea InitiatorName;

	@FXML
	private TableView<?> tableView;

	@FXML
	private TableColumn<?, ?> IdColumn1;

	@FXML
	private Button shutdown_btn;

	@FXML
	private Button extension_btn;

	@FXML
	private Label CurrentStageDueTime;

	@FXML
	private TableColumn<?, ?> DateColumn;

	@FXML
	private TextArea InitiatorEmail;

	@FXML
	private TableColumn<?, ?> IdColumn;

	@FXML
	private Button defrost_btn;

	@FXML
	private Button examination_btn;
	
	@FXML
    private Button newRequestBtn;

   
    @FXML
    void newRequest(ActionEvent event) {
    	ScreenController.getScreenController().activate("newRequest");
    }

	
	
	@FXML
	void freeze_click(ActionEvent event) {

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
	void shutdown_click(ActionEvent event) {

	}

	@FXML
	void initizlizeTable(ActionEvent event) {
		ArrayList<String> query = new ArrayList<String>();
		query.add("get all relaqueryted requests");
		query.add(Client.getInstance().getName());
		Client.getInstance().handleMessageFromClientGUI(query);
	}

}
