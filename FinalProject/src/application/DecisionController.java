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
    private Label status_text;

    @FXML
    private TextArea resultText;
    
    @FXML
    private TextArea constraitsAndRisksText;

    @FXML
    private TextArea requestedChangeText;

    
    @FXML
    private Label current_stage_due_time_text;
    
	Client client = Client.getInstance();
	
	UserProcess process;
	
	public EvaluationReport evaluationReport;
	private boolean answerFromServerApprove;
	private boolean answerFromServerMoreInfo;
	
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		answerFromServerApprove=false;
		answerFromServerMoreInfo=false;
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
		disableAllButtons();
    }

    @FXML
    void approve_click(ActionEvent event) {
    	ArrayList<Integer> paramaeters = new ArrayList<>(); 
    	paramaeters.add(process.getRequest_id());
    	Translator translator = new Translator(OptionsOfAction.Approve_Decision, paramaeters);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	try { Thread.sleep(500); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
    	showActionApproveMessage(answerFromServerApprove);
    	
    }

    @FXML
    void request_more_click(ActionEvent event) {
    	ArrayList<Integer> paramaeters = new ArrayList<>(); 
    	paramaeters.add(process.getRequest_id());
    	Translator translator = new Translator(OptionsOfAction.More_Info_Decision, paramaeters);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	try { Thread.sleep(500); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
    	showActionMoreInfoMessage(answerFromServerMoreInfo);
    }

    public void updateProcessInformation()
    {
		process = Client.getInstance().getProcesses().getMyProcess().get(Integer.parseInt(ControllerProcessMain.getInstance().getRequestID()));
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
    
    public static DecisionController getInstance() {
		return instance;
    }
    
    
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
    
    public void showActionMoreInfoMessage(boolean val) {
    	if(val==true) {
    		Alert alert =new Alert(AlertType.INFORMATION, "Asked for more information.");
        	alert.setTitle("Approved!");
        	alert.show();
        	disableAllButtons();
    	}
    	else {
    		Alert alert =new Alert(AlertType.ERROR, "Could not ask for more information.");
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

	/**
     * Function that set the form in the way it needs to be
     * every time you load it.
     * @param er
     */
    public void loadPage(EvaluationReport er) {
    	answerFromServerApprove=false;
		answerFromServerMoreInfo=false;
    	evaluationReport = new EvaluationReport(er);
    	requestedChangeText.setText(er.getRequestedChange());
    	resultText.setText(er.getResult());
    	constraitsAndRisksText.setText(er.getConstraitsAndRisks());
    	enableAllButtons();
    }
    
    public void disableAllButtons() {
    	approve_btn.setDisable(true);
    	deny_btn.setDisable(true);
    	request_more_btn.setDisable(true);
    }
    public void enableAllButtons() {
    	approve_btn.setDisable(false);
    	deny_btn.setDisable(false);
    	request_more_btn.setDisable(false);
    }
}
