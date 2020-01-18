package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
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
    private Button appointBtn;

    @FXML
    private ComboBox examinerCombobox;
    
    @FXML
    private Label status_text;

    @FXML
    private TextArea resultText;
    
    @FXML
    private TextArea constraitsAndRisksText;

    @FXML
    private TextArea requestedChangeText;

    
    @FXML
    private Label current_stage_due_time_text;
    	
	UserProcess process;
	
	public EvaluationReport evaluationReport;
	private boolean answerFromServerApprove;
	private boolean answerFromServerMoreInfo;
	private boolean answerFromServerAppointExaminer;
	
	private ArrayList<ChangeBoardMember> listForComboBox;
	
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		answerFromServerApprove=false;
		answerFromServerMoreInfo=false;
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
    		{
        		if(Client.instance.getRole().compareTo("Chairman") == 0)
            		Client.instance.getRelatedMessages("Chairman");
        		else
            		Client.instance.getRelatedMessages(Client.instance.getUserID());
    		}
    	}
    }

    /**
     * Deny Execution
     * @param event
     */
    @FXML
    void deny_click(ActionEvent event) {
    	ArrayList <Object> processInfo = new ArrayList<Object>();

    	processInfo.add(ControllerProcessMain.getInstance().getRequestID());
		Translator translator= new Translator(OptionsOfAction.REJECTE_PROCESS,processInfo);
		Client.getInstance().handleMessageFromClientGUI(translator);
		disableAllButtons();
    }

    /**
     * Approve Execution
     * @param event
     */
    @FXML
    void approve_click(ActionEvent event) {
    	ArrayList<Integer> paramaeters = new ArrayList<>(); 
    	paramaeters.add(process.getRequest_id());
    	Translator translator = new Translator(OptionsOfAction.Approve_Decision, paramaeters);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	//try { Thread.sleep(500); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
    	//showActionApproveMessage(answerFromServerApprove);
    	
    }

    /**
     * Request for more information
     * Set the process back to the evaluation stage
     * @param event
     */
    @FXML
    void request_more_click(ActionEvent event) {
    	ArrayList<Integer> paramaeters = new ArrayList<>(); 
    	paramaeters.add(process.getRequest_id());
    	Translator translator = new Translator(OptionsOfAction.More_Info_Decision, paramaeters);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	//try { Thread.sleep(500); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
    	//showActionMoreInfoMessage(answerFromServerMoreInfo);
    }
    

    /**
     * Appoint an Examiner
     * @param event
     */
    @FXML
    void appointExaminer(ActionEvent event) {
    	ArrayList<Object> arr = new ArrayList<>();
    	arr.add((int)process.getRequest_id()); // Process ID
    	String examinerName = examinerCombobox.getSelectionModel().getSelectedItem().toString();
    	String examinerID = getChangeBoardMemberIDFromList(examinerName);
    	arr.add(examinerID); // examinerID
    	Translator translator = new Translator(OptionsOfAction.Appoint_Examiner, arr);
    	Client.getInstance().handleMessageFromClientGUI(translator);

    	
    }

    
    /**
     * Update the table of process's information
     */
    public void updateProcessInformation()
    {
		
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
    
    
    /**
     * Get the instance instance of DecisionController
     * (The only one)
     * @return the only instance of DecisionController
     */
    public static DecisionController getInstance() {
		return instance;
    }
    
    
    /**
     * Show message from server about Approve the execution
     * @param val
     */
    public void showActionApproveMessage(boolean val) {
    	if(val==true) {
    		Alert alert =new Alert(AlertType.INFORMATION, "You approved the request.");
        	alert.setTitle("Approved!");
        	alert.show();
        	disableAllButtons();
    	}
    	else {
    		Alert alert =new Alert(AlertType.ERROR, "Could not approve the request.");
        	alert.setTitle("Error");
        	alert.show();
    	}
    }
    
    
    /**
     * Show message from server about Requesting more information
     * @param val
     */
    public void showActionMoreInfoMessage(boolean val) {
    	if(val==true) {
    		Alert alert =new Alert(AlertType.INFORMATION, "Asked for more information.");
        	alert.setTitle("Approved!");
        	alert.show();
        	disableAllButtons();
    	}
    	else {
    		Alert alert = new Alert(AlertType.ERROR, "Could not ask for more information.");
        	alert.setTitle("Error");
        	alert.show();
    	}
    }
    
    /**
     * Show message from server about Appointing an Examiner
     * @param val
     */
    public void showActionAppointExaminer(boolean val) {
    	if(val==true) {
    		Alert alert =new Alert(AlertType.INFORMATION, "Appointed Examiner.");
        	alert.setTitle("Approved!");
        	alert.show();
        	disableAllButtons();
    	}
    	else {
    		Alert alert = new Alert(AlertType.ERROR, "Could not appointed Examiner.");
        	alert.setTitle("Error");
        	alert.show();
    	}
    }
    
    
    public void setAnswerFromServerApprove(boolean answerFromServerApprove) {
		this.answerFromServerApprove = answerFromServerApprove;
	}

	public void setAnswerFromServerMoreInfo(boolean answerFromServerMoreInfo) {
		this.answerFromServerMoreInfo = answerFromServerMoreInfo;
	}

	
	
	public void setAnswerFromServerAppointExaminer(boolean answerFromServerAppointExaminer) {
		this.answerFromServerAppointExaminer = answerFromServerAppointExaminer;
	}

	/**
     * Function that set the form in the way it needs to be
     * every time you load it.
     * @param er
     */
    public void loadPage(EvaluationReport er) {
    	process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
    	answerFromServerApprove=false;
		answerFromServerMoreInfo=false;
		answerFromServerAppointExaminer=false;
		if(Double.parseDouble(process.getProcess_stage())>=5 && Double.parseDouble(process.getProcess_stage())<6) {
			evaluationReport = new EvaluationReport(er);
	    	requestedChangeText.setText(er.getRequestedChange());
	    	resultText.setText(er.getResult());
	    	constraitsAndRisksText.setText(er.getConstraitsAndRisks());
	    	enableDecisionMakingButtons();
		}
		else{
			if(Double.parseDouble(process.getProcess_stage())==10) {
				clearForm();
				enableAppointExaminerButton();
				
			}
			else {
				disableAllButtons();
			}
		}

    	
    }
    
    /**
     * Clear the form
     */
    public void clearForm() {
    	requestedChangeText.clear();
    	resultText.clear();
    	constraitsAndRisksText.clear();
    }
    
    /**
     * Disable all the buttons
     */
    public void disableAllButtons() {
    	approve_btn.setDisable(true);
    	deny_btn.setDisable(true);
    	request_more_btn.setDisable(true);
    	appointBtn.setDisable(true);
    	examinerCombobox.setDisable(true);
    	
    }
    
    /**
     * Enable the required buttons for making a decision
     */
    public void enableDecisionMakingButtons() {
    	approve_btn.setDisable(false);
    	deny_btn.setDisable(false);
    	request_more_btn.setDisable(false);
    	appointBtn.setDisable(true);
    	examinerCombobox.setDisable(true);
    }
    
    /**
     * Enable the required buttons for appointing an Examiner
     */
    public void enableAppointExaminerButton() {
    	approve_btn.setDisable(true);
    	deny_btn.setDisable(true);
    	request_more_btn.setDisable(true);
    	//appointBtn.setDisable(false);
    }
    
    /**
     * Set the combo box with required values
     * @param arr
     */
    public void setComboBox(ArrayList<ChangeBoardMember> arr) {
    	listForComboBox = new ArrayList<>(arr);
    	for(ChangeBoardMember i:listForComboBox) {
    		examinerCombobox.getItems().add(i.getName());
    	}
    }
    
    /**
     * Get the ID of the inserted name
     * (The name that was chosen on the combo box).
     * @param name1
     * @return The ID of the examiner
     */
    public String getChangeBoardMemberIDFromList(String name1) {
    	for(ChangeBoardMember i:listForComboBox) {
    		if(i.getName().equals(name1)) {
    			return i.getId();
    		}
    	}
    	return null;
    }
    
}
