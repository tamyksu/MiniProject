package application;
	import java.util.ArrayList;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
	import javafx.scene.control.Label;
	import javafx.scene.control.TableColumn;
	import javafx.scene.control.TableView;
	import javafx.scene.control.TextArea;


public class ControlerProcessMain {


	   @FXML
	    private TableView<?> tableView;

	    @FXML
	    private TableColumn<?, ?> IdColumn;

	    @FXML
	    private TableColumn<?, ?> InformationSystemColumn;

	    @FXML
	    private TableColumn<?, ?> StatusColumn;

	    @FXML
	    private TableColumn<?, ?> DateColumn;

	    @FXML
	    private TableColumn<?, ?> IdColumn1;

	    @FXML
	    private Label CurrentProcessStatus;

	    @FXML
	    private Label CurrentStageDueTime;

	    @FXML
	    private TextArea InitiatorName;

	    @FXML
	    private TextArea InitiatorEmail;

	    @FXML
	    private TextArea InitiatorProfession;

	    @FXML
	    private TextArea InformationSystem;

	    @FXML
	    private TextArea CurrentState;

	    @FXML
	    private TextArea RequestedChange;

	    @FXML
	    private TextArea Explanation;

	    @FXML
	    private TextArea Notes;

	    @FXML
	    private TextArea RequestDate;

	    @FXML
	    private TextArea RequestID;

	    @FXML
	    private TextArea AssociatedDocuments;

	    @FXML
	    void initizlizeTable(ActionEvent event) {
	    	ArrayList<String> query = new ArrayList<String>();
	    	query.add("get all relaqueryted requests");
	    	query.add(Client.getInstance().getName());
			Client.getInstance().handleMessageFromClientGUI(query);
	    }

	
}
