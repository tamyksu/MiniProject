package application;

import java.net.URL;
import java.util.ResourceBundle;
import client.Client;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import translator.OptionsOfAction;
import translator.Translator;
import javafx.scene.control.Alert.AlertType;

public class EvaluationController implements Initializable{
	
	public static EvaluationController instance;

    @FXML
    private TextArea constraints_textbox;

    @FXML
    private Button back_btn;

    @FXML
    private TextField days_textbox;

    @FXML
    private TextArea request_change_textbox;

    @FXML
    private Button submit_duetime_btn;

    @FXML
    private Button submit_btn;

    @FXML
    private TextArea result_textbox;
    
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
    
    private boolean answerFromServerSubmitDays;
    private boolean answerFromServerSubmitForm;
    
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
    }


    @FXML
    /**
     * Appraiser submit evaluated number of days.
     * @param event
     */
    void submit_duetime_click(ActionEvent event) {
    	UserProcess process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
    	/**
    	 * Form validation:
    	 */
    	if(days_textbox.getText().trim().isEmpty() || !NewRequestController.isNumeric(days_textbox.getText())) {
    		new Alert(AlertType.ERROR, "You must fill all the details!").show();
    	}
    	else {
    		ArrayList<Object> arr = new ArrayList<>();
    		arr.add((new Integer(process.getRequest_id()))); // The process ID   0.
    		arr.add(process.getProcess_stage()); // Process Stage   1
    		arr.add(Client.getInstance().getUserID()); // The Appraiser's ID   2
    		
    		arr.add(new Integer
    				(Integer.parseInt(days_textbox.getText()))); // The evaluated number of days  3
    		

    		Translator translator = new Translator(OptionsOfAction.Fill_Evalution_Number_Of_Days, arr); // new Translator
    		Client.getInstance().handleMessageFromClientGUI(translator);

    		try { Thread.sleep(1000); } catch (InterruptedException e) {System.out.println("Can't Sleep");}

    		if(answerFromServerSubmitDays==true) {
    			pageLoad(3);
    		}
    	}
    }

    /**
     * Fill evaluation form and submit
     * @param event
     */
    @FXML
    void submit_click(ActionEvent event) {
    	if(formCheck()) {
    		UserProcess process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
    		ArrayList<Object> arrForm = new ArrayList<>();
    		arrForm.add(new Integer(process.getRequest_id())); // Process ID   0
    		arrForm.add(process.getProcess_stage()); // Process Stage   1
    		arrForm.add(request_change_textbox.getText().toString()); // The Requested change  2
    		arrForm.add(result_textbox.getText().toString()); // Result  3
    		arrForm.add(constraints_textbox.getText().toString()); // Constraints and risks  4
    		
    		Translator translator = new Translator(OptionsOfAction.Fill_Evalution_Form, arrForm);
    		Client.getInstance().handleMessageFromClientGUI(translator);
    		
    		if(answerFromServerSubmitForm==true) {
    			pageLoad(3);
    		}
    	}
    }
    
    /**
     * Form validation
     * @return  true/false
     */
    public boolean formCheck() {
    	// if any of the TextAreas/Textfields is empty:
    	if(request_change_textbox.getText().trim().isEmpty()
    			|| result_textbox.getText().trim().isEmpty() ||
    			constraints_textbox.getText().trim().isEmpty()) {

    		new Alert(AlertType.ERROR, "You must fill all the details!").show();
    		return false;
    	}	
    	return true;

    }
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

    public static EvaluationController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		this.answerFromServerSubmitDays=false;
		this.answerFromServerSubmitForm=false;
	}
	
	public void submitionSuccesseded() {
		new Alert(AlertType.INFORMATION, "Submition received.");
	}

	public void submitionFailed() {
		new Alert(AlertType.ERROR, "Failed, please try again.").show();
	}
	public void setAnswerFromServerSubmitDays(boolean answer) {
		this.answerFromServerSubmitDays = answer;
	}
	public void setAnswerFromServerSubmitForm(boolean answer) {
		this.answerFromServerSubmitForm = answer;
	}
	
	/**
	 * How to load the page, depends on the stage of the process
	 * @param stage
	 */
	public void pageLoad(double stage) { // How to load the page
		days_textbox.clear();
		if(stage==2 || stage==2.5) {
			days_textbox.setDisable(false);
			submit_duetime_btn.setDisable(false);
			constraints_textbox.setDisable(true);
			submit_btn.setDisable(true);
			result_textbox.setDisable(true);
			request_change_textbox.setDisable(true);
		}
		if(stage==4) {
			days_textbox.setDisable(true);
			submit_duetime_btn.setDisable(true);
			constraints_textbox.setDisable(false);
			submit_btn.setDisable(false);
			result_textbox.setDisable(false);
			request_change_textbox.setDisable(false);
		}
		if(stage<2 || stage>4 || stage==3) {
			days_textbox.setDisable(true);
			submit_duetime_btn.setDisable(true);
			constraints_textbox.setDisable(true);
			submit_btn.setDisable(true);
			result_textbox.setDisable(true);
			request_change_textbox.setDisable(true);
		}
	}

}
