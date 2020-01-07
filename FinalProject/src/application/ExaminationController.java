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
    	
    	check.add(request_id_textbox);
		check.add(this.processID);
		Translator translator = new Translator(OptionsOfAction.GET_APPRAISER_AND_PERFORMANCE_LEADER_CB_DATA, check);
		client.handleMessageFromClientGUI(translator);
    }

    @FXML
    void fill_failure_report_click(ActionEvent event) {
    	
    	request_id_textbox.setDisable(false);
    	failure_explanation.setDisable(false);
    	submit_failure_report_btn.setDisable(false);
    	
    	request_id_textbox.setText(String.valueOf(this.processID).toString());
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
