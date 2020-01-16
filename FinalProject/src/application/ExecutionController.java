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
import javafx.scene.control.TextField;
import translator.OptionsOfAction;
import translator.Translator;
import javafx.scene.control.Alert.AlertType;

public class ExecutionController implements Initializable{

	public static ExecutionController instance;
	
    @FXML
    private Button back_btn;

    @FXML
    private TextField days_textbox;

    @FXML
    private Button execution_completed_btn;

    @FXML
    private Button submit_btn;
    
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
    private boolean answerFromServerExecutionCompleted;
    UserProcess process;
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		
	}
    
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
    void submit_click(ActionEvent event) {
    	if(days_textbox.getText().trim().isEmpty() || !NewRequestController.isNumeric(days_textbox.getText())) {
    		new Alert(AlertType.ERROR, "Enter the number of days!").show();
    		return;
    	}
    	
    	ArrayList<Object> arr = new ArrayList<>();
    	arr.add((int) process.getRequest_id());
    	arr.add(process.getProcess_stage().toString());
    	arr.add(Client.getInstance().getUserID().toString()); // Executor's ID
    	arr.add(Integer.parseInt(days_textbox.getText().toString()));
    	Translator translator = new Translator(OptionsOfAction.Execution_Suggest_Number_Of_Days, arr);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	try { Thread.sleep(500); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
    	showActionSetDays();
    	
    }

    @FXML
    void execution_completed_click(ActionEvent event) {
    	ArrayList<Object> arr = new ArrayList<>();
    	arr.add((int) process.getRequest_id());
    	//
    	
    	Translator translator = new Translator(OptionsOfAction.Execution_Completed, arr);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	try { Thread.sleep(500); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
    	showActionExecutionCompleted();
    	
    }
    
    public void showActionSetDays() {
    	if(answerFromServerSubmitDays==true) {
    		Alert alert =new Alert(AlertType.INFORMATION, "You've sent your evaluation");
        	alert.setTitle("Approved!");
        	alert.show();
        	loadPage(8);
    	}
    	else {
    		Alert alert =new Alert(AlertType.ERROR, "Could not evaluate due date.");
        	alert.setTitle("Error");
        	alert.show();
    	}
    }
    
    public void showActionExecutionCompleted() {
    	if(answerFromServerExecutionCompleted==true) {
    		Alert alert = new Alert(AlertType.INFORMATION, "Execution completed.");
        	alert.setTitle("Completed!");
        	alert.show();
        	loadPage(10);
    	}
    	else {
    		Alert alert = new Alert(AlertType.ERROR, "Something went wrong.");
        	alert.setTitle("Error");
        	alert.show();
    	}
    }
    
    public void updateProcessInformation()
    {
		process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
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
    
    public static ExecutionController getInstance() {
    	return instance;
    }
    
    
    

    public void setAnswerFromServerSubmitDays(boolean answerFromServerSubmitDays) {
		this.answerFromServerSubmitDays = answerFromServerSubmitDays;
	}

	public void setAnswerFromServerExecutionCompleted(boolean answerFromServerExecutionCompleted) {
		this.answerFromServerExecutionCompleted = answerFromServerExecutionCompleted;
	}

	public void loadPage(double stage) { // How to load the page
		days_textbox.clear();
		answerFromServerSubmitDays = false;
		answerFromServerExecutionCompleted = false;
		if(stage==7 || stage==7.5) {
			days_textbox.setDisable(false);
			submit_btn.setDisable(false);
			execution_completed_btn.setDisable(true);
		}
		if(stage==9) {
			days_textbox.setDisable(true);
			submit_btn.setDisable(true);
			execution_completed_btn.setDisable(false);
		}
		if(stage<7 || stage>9 || stage==8) {
			days_textbox.setDisable(true);
			submit_btn.setDisable(true);
			execution_completed_btn.setDisable(true);
		}
	}

}
