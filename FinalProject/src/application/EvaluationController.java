package application;

import java.net.URL;
import java.util.ResourceBundle;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EvaluationController implements Initializable{

public static EvaluationController instance;
	
	Client client = Client.getInstance();
	
	private int process_stage = 1;
	
	private int processID;
	
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


    @Override
   	public void initialize(URL location, ResourceBundle resources) {
   		instance = this;
   	}
    
    public void initializeChosenProcessScreen(String processStage)
    {
    	
    }
    
    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    	ControllerProcessMain.instance.getTheUpdateProcessesFromDB();
    }


    @FXML
    void submit_duetime_click(ActionEvent event) {

    }

    @FXML
    void submit_click(ActionEvent event) {

    }

}
