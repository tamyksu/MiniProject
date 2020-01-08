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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import translator.OptionsOfAction;
import translator.Translator;

public class ExaminationController implements Initializable{

	public static ExaminationController instance;
	
	Client client = Client.getInstance();
	
	private int process_stage = 1;
	
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

    @Override
   	public void initialize(URL location, ResourceBundle resources) {
   		instance = this;
   	}
    
    public void initializeChosenProcessScreen(String processStage)
    {
    	System.out.println("ExaminationController: initializeChosenProcessScreen: Stage: " + processStage);
    	if(processStage == null)
    		return;
    	
    	try
    	{
    		this.process_stage = Integer.parseInt(processStage);
        	
    	}
    	catch(NumberFormatException e)
    	{
    		double temp = Double.parseDouble(processStage);
    		this.process_stage = (int)temp;
    	}
    	
    	switch(processStage)
    	{
    	case "11":
    		examination_completed_btn.setDisable(false);
        	fill_failure_report_btn.setDisable(false);
        	failure_explanation.setDisable(true);
        	request_id_textbox.setDisable(true);
        	submit_failure_report_btn.setDisable(true);
    		break;
    		
    	case "11.5":
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
    
    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    	request_id_textbox.clear();
    	failure_explanation.clear();
    	
    	request_id_textbox.setDisable(true);
    	failure_explanation.setDisable(true);
    	submit_failure_report_btn.setDisable(true);
    }

    @FXML
    void submit_failure_report_click(ActionEvent event) {
    	ArrayList<Object> check = new ArrayList<Object>();
    	int requesrID;
    	
    	try
    	{
    		requesrID = Integer.parseInt(request_id_textbox.getText());
    	}
    	catch(NumberFormatException ex)
    	{
    		System.out.println("Wrong input");
    		
    		Alert alert = new Alert(AlertType.INFORMATION);
        	
    		request_id_textbox.clear();
        	alert.setTitle("ALERT");
            alert.setHeaderText("NO FAILURE REPORT ID WAS SET");
            alert.setContentText("Please insert a valid ID");
            alert.showAndWait();
            return;
    	}
    	
    	if(failure_explanation.hasProperties() == false)
    	{
    		System.out.println("failure_explanation text is empty");
    		
    		Alert alert = new Alert(AlertType.INFORMATION);
        	
        	alert.setTitle("ALERT");
            alert.setHeaderText("NO FAILURE REPORT EXPLANATION WAS SET");
            alert.setContentText("Please insert an explanation");
            alert.showAndWait();
            return;
    	}
    	
    	check.add(request_id_textbox.getText());
    	check.add(this.processID);
		check.add(failure_explanation.getText());
		
		request_id_textbox.clear();
    	failure_explanation.clear();
    	
		Translator translator = new Translator(OptionsOfAction.INSERT_FAILURE_REPORT, check);
		client.handleMessageFromClientGUI(translator);
    }

    @FXML
    void fill_failure_report_click(ActionEvent event) {
    	
    	request_id_textbox.setDisable(false);
    	failure_explanation.setDisable(false);
    	submit_failure_report_btn.setDisable(false);
    }


    @FXML
    void examination_completed_click(ActionEvent event) {

    }
    
    void setProcessID(int id)
    {
    	if(id != -1)
    		this.processID = id;
    	else
    		System.out.println("ExaminationController: setProcessID: Wrong process ID was given.");
    }
}
