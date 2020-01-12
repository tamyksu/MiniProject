package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import translator.OptionsOfAction;
import translator.Translator;

public class DecisionController implements Initializable{

	public static DecisionController instance;
	
    @FXML
    private Button approve_btn;

    @FXML
    private Button request_more_btn;

    @FXML
    private Button back_btn;

    @FXML
    private Button deny_btn;
 
    @FXML
    private Label initiator_name_text;

    @FXML
    private Label initiator_email_text;

    @FXML
    private Label initiator_role_text;

    @FXML
    private Label information_system_text;

    @FXML
    private Label current_stage_text;

    @FXML
    private Label requested_change_text;

    @FXML
    private Label explanation_text;

    @FXML
    private Label notes_text;

    @FXML
    private Label request_date_text;

    @FXML
    private Label request_id_text;

    @FXML
    private Label documents_text;

    @FXML
    private Label status_text;

    @FXML
    private Label current_stage_due_time_text;
	Client client = Client.getInstance();
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
	}
    
    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }

    @FXML
    void deny_click(ActionEvent event) {
    	ArrayList <Object> processInfo = new ArrayList<Object>();

    	processInfo.add(ControllerProcessMain.getInstance().getRequestID());
		Translator translator= new Translator(OptionsOfAction.REJECTE_PROCESS,processInfo);
		client/*Client.getInstance()*/.handleMessageFromClientGUI(translator);
    }

    @FXML
    void approve_click(ActionEvent event) {

    }

    @FXML
    void request_more_click(ActionEvent event) {

    }

    public void updateProcessInformation()
    {
		UserProcess process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
		initiator_name_text.setText(process.getIntiatorId());
		initiator_email_text.setText(process.getEmail());
		initiator_role_text.setText(process.getRole());
		information_system_text.setText("" + process.getSystem_num());
		current_stage_text.setText(process.getProcess_stage());
		requested_change_text.setText(process.getRequest_description());
		explanation_text.setText(process.getExplanaton());
		notes_text.setText(process.getNotes());
		request_date_text.setText(process.getCreation_date());
		request_id_text.setText("" + process.getRequest_id());
		status_text.setText(process.getStatus());
		current_stage_due_time_text.setText(process.getCurrent_stage_due_date());
    }
}
