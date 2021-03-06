package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import translator.OptionsOfAction;
import translator.Translator;

public class ExaminationController implements Initializable{

	public static ExaminationController instance;
		
	private String process_stage;
	
	private int processID;
	
    @FXML
    private Button examination_completed_btn;

    @FXML
    private Button fill_failure_report_btn;

    @FXML
    private TextArea failure_explanation;

    @FXML
    private Button back_btn;

    @FXML
    private TextField request_id_textbox;

    @FXML
    private Button submit_failure_report_btn;
    
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

    @Override
   	public void initialize(URL location, ResourceBundle resources) {
   		instance = this;
   	}
    
    
    
    public void initializeChosenProcessScreen(String processStage)
    {
    	System.out.println("ExaminationController: initializeChosenProcessScreen: Stage: " + processStage);
    	
    	if(processStage == null)
    		return;
    	
    	this.process_stage = processStage;        	

    	switch(processStage)
    	{
    	case "11":
    	case "11.5":
    	case "11.6":
    		examination_completed_btn.setDisable(false);
        	fill_failure_report_btn.setDisable(false);
        	failure_explanation.setDisable(true);
        	request_id_textbox.setDisable(true);
        	submit_failure_report_btn.setDisable(true);
    		break;
    		
    	case "11.1":
    	case "11.2":
    	case "11.3":
    		examination_completed_btn.setDisable(true);
        	fill_failure_report_btn.setDisable(false);
        	failure_explanation.setDisable(false);
        	request_id_textbox.setDisable(false);
        	submit_failure_report_btn.setDisable(false);
    		break;
    		
    	default:
    		examination_completed_btn.setDisable(true);
    	    fill_failure_report_btn.setDisable(true);
    	    failure_explanation.setDisable(true);
    	    request_id_textbox.setDisable(true);
    	    submit_failure_report_btn.setDisable(true);
    		break;
    	}
    	
    }
    
    /**
     * Go back to previous screen
     * @param event
     */
    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    	ControllerProcessMain.instance.getTheUpdateProcessesFromDB();
    	System.out.println("Client.instance.getUserID() = " + Client.instance.getUserID());
    	if(Client.instance.getRole().compareTo("Supervisor") == 0)
    		Client.instance.getRelatedMessages("Supervisor");
    	else
    	{
    		if(Client.instance.getRole().compareTo("Manager") == 0)
        		Client.instance.getRelatedMessages("Manager");
    		else
        		Client.instance.getRelatedMessages(Client.instance.getUserID());

    	}
    	request_id_textbox.clear();
    	failure_explanation.clear();
    	
    	request_id_textbox.setDisable(true);
    	failure_explanation.setDisable(true);
    	submit_failure_report_btn.setDisable(true);
    }

    /**
     * Submit the failure report
     * @param event
     */
    @FXML
    void submit_failure_report_click(ActionEvent event) {
    	
    	ArrayList<Object> check = new ArrayList<Object>();
    	
    	if(failure_explanation.getText().compareTo("") == 0)
    	{
    		System.out.println("failure_explanation text is empty");
    		failure_explanation.clear();

    		Alert alert = new Alert(AlertType.INFORMATION);
        	
        	alert.setTitle("ALERT");
            alert.setHeaderText("NO FAILURE REPORT EXPLANATION WAS SET");
            alert.setContentText("Please insert an explanation");
            alert.showAndWait();
            return;
    	}
    	
    	check.add(this.processID);
		check.add(failure_explanation.getText());
		
		request_id_textbox.clear();
    	failure_explanation.clear();
    	
    	request_id_textbox.setDisable(true);
    	failure_explanation.setDisable(true);
    	submit_failure_report_btn.setDisable(true);
    	
		Translator translator = new Translator(OptionsOfAction.INSERT_FAILURE_REPORT, check);
		Client.getInstance().handleMessageFromClientGUI(translator);
    }

    /**
     * Enable filling the failure report
     * @param event
     */
    @FXML
    void fill_failure_report_click(ActionEvent event) {
    	
    	request_id_textbox.setDisable(false);
    	failure_explanation.setDisable(false);
    	submit_failure_report_btn.setDisable(false);
    	examination_completed_btn.setDisable(true);
    	
    	ArrayList<Object> check = new ArrayList<Object>();
    	
    	check.add(this.processID);
    	check.add(this.process_stage);
    	
    	Translator translator = new Translator(OptionsOfAction.FILL_FAILURE_REPORT_CLICK, check);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    }


    
    /**
     * Examination completed
     * @param event
     */
    @FXML
    void examination_completed_click(ActionEvent event) {
    	
    	examination_completed_btn.setDisable(true);
    	fill_failure_report_btn.setDisable(true);

    	ArrayList<Object> check = new ArrayList<Object>();
    	check.add(this.processID);
    	check.add(Client.getInstance().getUserID());
    	Translator translator = new Translator(OptionsOfAction.EXAMINATION_COMPLETED, check);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    }
    
    
    void setProcessID(int id)
    {
    	if(id != -1)
    		this.processID = id;
    	else
    		System.out.println("ExaminationController: setProcessID: Wrong process ID was given.");
    }
    
    /**
     * Update the table of process's information
     */
    public void updateProcessInformation()
    {
		UserProcess process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
		initiator_name_text.setText(process.getIntiatorId());
		initiator_email_text.setText(process.getEmail());
		initiator_role_text.setText(process.getRole());
		information_system_text.setText("" + process.getSystem_num());
		current_stage_text.setText(MyHashMaps.getProcessStageText(Double.parseDouble(process.getProcess_stage())));
		requested_change_text.setText(process.getRequest_description());
		explanation_text.setText(process.getExplanaton());
		notes_text.setText(process.getNotes());
		request_date_text.setText(process.getCreation_date());
		request_id_text.setText("" + process.getRequest_id());
		status_text.setText(process.getStatus());
		current_stage_due_time_text.setText(process.getCurrent_stage_due_date());
    }
}
