package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import translator.OptionsOfAction;
import translator.Translator;

public class Supervisor_ProcessMain_Controller implements Initializable{
	
	public static Supervisor_ProcessMain_Controller instance;
	
	Client client = Client.getInstance();
	
	ArrayList<String> apprairsID;
	
	ArrayList<String> appraisersNames;
	
	private int process_stage = 1;
	
	private int procID;
	
	@FXML
    private ComboBox<String> appoint_appraiser_comboBox;

    @FXML
    private ComboBox<String> appoint_performance_leader_comboBox;

    @FXML
    private Button decline_extension_request_btn;

    @FXML
    private Button appoint_appraiser_btn;

    @FXML
    private Button approve_due_time_request_btn;

    @FXML
    private Button shut_down_process_btn;

    @FXML
    private Button decline_due_time_request_btn;

    @FXML
    private Button appoint_performance_leader_btn;

    @FXML
    private Button add_extension__time_btn;

    @FXML
    private Button back_btn;

    @FXML
    private Button freeze_process_btn;
    
    @FXML
    private TextField due_time_text;
    
    @FXML
    private TextField extension_time_text;
    
    @FXML
    private Label current_appraiser_text;
    
    @FXML
    private Label current_performance_leader_text;
    
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
	}
    
    public void getAppraiserOrPerformanceLeaderCBData()
    {
    	int processID;
    	
    	try
    	{
    		this.procID = processID = ControllerProcessMain.instance.getSelectedRowNumber();//needs rows in the processes table
    	}
    	catch(NullPointerException e)
    	{
    		System.out.println("No row was chosen");
    		
    		Alert alert = new Alert(AlertType.INFORMATION);
        	
            alert.setTitle("ALERT");
            alert.setHeaderText("NO ROW WAS CHOSEN");
            alert.setContentText("Please select a row in the processes table");
            alert.showAndWait();
            this.procID = -1;
    		return;
    	}
    	
    	

    	if(this.process_stage != 1 && this.process_stage != 6)
    	{
    		return;
    	}

    	try {
    		ArrayList<Object> check = new ArrayList<Object>();
    		//check.add(2);//process id
    		check.add(processID);
    		Translator translator = new Translator(OptionsOfAction.GET_APPRAISER_AND_PERFORMANCE_LEADER_CB_DATA, check);
    		client.handleMessageFromClientGUI(translator);
    	}
    	catch(Exception e){
    		System.out.println("EXCEPTION: Supervisor_ProcessMain_Controller - get ComboBox Data");
    	}
    }
    
    public void setAppraiserOrPerformanceLeaderDataInCB(ArrayList<String> appraisersList)
    {
    	int listSize = appraisersList.size()/3;
    	ObservableList<String> appraisersForCB = FXCollections.observableArrayList();
    	ArrayList<String> appraisersID = new ArrayList<String>();//save for insert to DB in the appoint function
    	ArrayList<String> appraisersNames = new ArrayList<String>();//save for insert to DB in the appoint function
    	
    	System.out.println("listSize: " + listSize);
    	for(int i = 0; i < listSize ; i++)
    	{
    		appraisersID.add(new String(appraisersList.get(i*3).toString()));
    		appraisersForCB.add(new String(appraisersList.get(i*3+1).toString() + " " + appraisersList.get(i*3+2).toString()));
    		appraisersNames.add(new String(appraisersList.get(i*3+1).toString() + " " + appraisersList.get(i*3+2).toString()));
    	}
    	
    	this.apprairsID = appraisersID;
    	this.appraisersNames = appraisersNames;
    	
    	if(process_stage == 1)
    	{
    		appoint_appraiser_comboBox.setDisable(false);
    		if(appraisersForCB != null)
    			appoint_appraiser_comboBox.setItems(appraisersForCB);
    		//appoint_appraiser_comboBox.setPromptText("Make a choice");
    		appoint_appraiser_btn.setDisable(false);
    	}
    		
    	if(process_stage == 6)
    	{
			appoint_performance_leader_comboBox.setDisable(false);
			if(appraisersForCB != null)
				appoint_performance_leader_comboBox.setItems(appraisersForCB);
    		//appoint_performance_leader_comboBox.setPromptText("Make a choice");
    		appoint_performance_leader_btn.setDisable(false);
    	}
    	
    	if(listSize == 0)
    	{
    		appoint_appraiser_btn.setDisable(true);
    		appoint_performance_leader_btn.setDisable(true);
    	}
    	else
    		System.out.println("!" + appraisersForCB + "!");

    }
    
    public void appointAppraiserOrPerformanceLeader(ActionEvent e)
    {
    	//int processID = ControllerProcessMain.instance.getSelectedRowNumber();
    	String chosenID;
    	String chosenAppraiser;
    	try
    	{
    		if(this.process_stage == 1)
    		{
        		chosenAppraiser = appoint_appraiser_comboBox.getValue();
        		current_appraiser_text.setText(chosenAppraiser);
        		appoint_appraiser_comboBox.valueProperty().set(null);
    		}

    		else
        	{
        		chosenAppraiser = appoint_performance_leader_comboBox.getValue();
        		current_performance_leader_text.setText(chosenAppraiser);
        		appoint_performance_leader_comboBox.valueProperty().set(null);
    		}
    		
    		chosenID = this.apprairsID.get(this.appraisersNames.indexOf(chosenAppraiser));
    		
    		ArrayList<Object> check = new ArrayList<Object>();
    		check.add(chosenID);
    		
    		check.add(this.procID);//check.add(2);//check.add(processID);
    		if(process_stage == 1)
    			check.add("Appraiser");
    		else
    			check.add("Performance Leader");
    		
    		Translator translator = new Translator(OptionsOfAction.APPOINT_APPRAISER_OR_PERFORMANCE_LEADER, check);
    		client.handleMessageFromClientGUI(translator);
    		
    		if(process_stage == 1)
    		{
    			add_extension__time_btn.setDisable(true);
        		decline_extension_request_btn.setDisable(true);
        		extension_time_text.setDisable(true);
        		approve_due_time_request_btn.setDisable(true);
        		decline_due_time_request_btn.setDisable(true);
        		due_time_text.setDisable(true);
        		appoint_appraiser_comboBox.setDisable(true);
        		appoint_appraiser_btn.setDisable(true);
    			appoint_performance_leader_comboBox.setDisable(true);
    			appoint_performance_leader_btn.setDisable(true);
        		process_stage = 2;
    		}
    		else
    		if(process_stage == 6)
    		{
    			add_extension__time_btn.setDisable(true);
        		decline_extension_request_btn.setDisable(true);
        		extension_time_text.setDisable(true);
        		approve_due_time_request_btn.setDisable(true);
        		decline_due_time_request_btn.setDisable(true);
        		due_time_text.setDisable(true);
        		appoint_appraiser_comboBox.setDisable(true);
        		appoint_appraiser_btn.setDisable(true);
    			appoint_performance_leader_comboBox.setDisable(true);
    			appoint_performance_leader_btn.setDisable(true);
        		process_stage = 6;
    		}

    	}
    	catch(Exception ex)
    	{
    		System.out.println("No choice was made");
    		return;
    	}
    
    }
    
    public void getAppraiserAndPerformanceLeaderLabels()
    {
    	current_appraiser_text.setText("None");
    	current_performance_leader_text.setText("None");
    	
    	if(this.procID == -1)
    	{
    		System.out.println("getAppraiserAndPerformanceLeaderLabels - this.procID is wrong");
    		return;
    	}
    	
    	if(process_stage == 1)
    		return;
        	
    		
    	if(process_stage > 1)
    	{
        	//current_performance_leader_text.setText("None");
        	
        	ArrayList<Object> arr = new ArrayList<Object>();
        	arr.add(this.procID);
        	Translator translator = new Translator(OptionsOfAction.GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC, arr);
        	Client.instance.handleMessageFromClientGUI(translator);
    	}
    }
    
    public void setAppraiserAndPerformanceLeaderLabels(ArrayList <String> arr)
    {
    	System.out.println("setLabels: process_stage = " + process_stage);
    	
    	Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException “Not on FX application thread”
    	    public void run() {
    	    	if(arr.size() == 0)
    	    	{
    	        	System.out.println("arr.size() = 0");
    	        	return;
    	    	}
    	    	
    	    	else
    	    	{
    	    		if(arr.size() == 1)
    	    		{
    	    			current_appraiser_text.setText(arr.get(0).toString());
    	    			current_performance_leader_text.setText("None");
    	    			System.out.println("arr.size() = 1");
    	    			return;
    	    		}
    	    			
    	    		
    	    		if(arr.size() == 2)
    	    		{
    	    			current_appraiser_text.setText(arr.get(0).toString());
    	    			current_performance_leader_text.setText(arr.get(1).toString());
    	    			System.out.println("arr.size() = 2");
    	    		}
    	    	}
    	    }
    	});
    	
    }
    
    public void ApproveDueTimeRequest(ActionEvent e)
    {
    	//int processID = ControllerProcessMain.instance.getSelectedRowNumber();

    	ArrayList <Object> arr = new ArrayList<Object>();
    	
    	arr.add(this.procID);//process/request id
    	arr.add(due_time_text.getText());
    	
    	Translator translator = new Translator(OptionsOfAction.SET_EVALUATION_OR_EXECUTION_DUE_TIME, arr);
    	client.handleMessageFromClientGUI(translator);
    	due_time_text.clear();
    	approve_due_time_request_btn.setDisable(true);
		decline_due_time_request_btn.setDisable(true);
		due_time_text.setDisable(true);
    }
    
    public void DeclineDueTimeRequest(ActionEvent e)
    {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	
    	due_time_text.clear();
        alert.setTitle("ALERT");
        alert.setHeaderText("THERE ARE NO MESSAGES");
        alert.setContentText("need to send a message to the appraiser or the performance leader to resend a request");
        alert.showAndWait();
    }
    
    public void addDueTimeExtension(ActionEvent e)
    {    	
    	try
    	{
    		int daysToAdd = Integer.parseInt(extension_time_text.getText());
    	}
    	catch(NumberFormatException ex)
    	{
    		System.out.println("Wrong input");
    		
    		Alert alert = new Alert(AlertType.INFORMATION);
        	
    		extension_time_text.clear();
        	alert.setTitle("ALERT");
            alert.setHeaderText("NO TIME WAS SET");
            alert.setContentText("Please insert a valid time to add (days)");
            alert.showAndWait();
            return;
    	}
    	
    	ArrayList <Object> arr = new ArrayList<Object>();
    	
    	arr.add(this.procID);//process/request id
    	arr.add(Integer.parseInt(extension_time_text.getText()));
    	
    	Translator translator = new Translator(OptionsOfAction.ADD_EVALUATION_OR_EXECUTION_EXTENSION_TIME, arr);
    	client.handleMessageFromClientGUI(translator);
    	extension_time_text.clear();
    }
    
    public void declineDueTimeExtension(ActionEvent e)
    {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	
    	extension_time_text.clear();
    	alert.setTitle("ALERT");
        alert.setHeaderText("THERE ARE NO MESSAGES");
        alert.setContentText("need to send a message to the appraiser or the performance leader to resend a request");
        alert.showAndWait();
    }
    
    void initializeChosenProcessScreen(String processStage)
    {
    	System.out.println("Supervisor_ProcessesMain: initializeFlag: Stage: " + processStage);
    	this.process_stage = Integer.parseInt(processStage);
    	
    	switch(processStage)
    	{
    	case "3":
    	case "8":
    		add_extension__time_btn.setDisable(true);
    		decline_extension_request_btn.setDisable(true);
    		extension_time_text.setDisable(true);
    		approve_due_time_request_btn.setDisable(false);
    		decline_due_time_request_btn.setDisable(false);
    		due_time_text.setDisable(false);
    		appoint_appraiser_comboBox.setDisable(true);
    		appoint_appraiser_btn.setDisable(true);
			appoint_performance_leader_comboBox.setDisable(true);
			appoint_performance_leader_btn.setDisable(true);
    		break;
    		
    	case "4":
    	case "9":
    		add_extension__time_btn.setDisable(false);
        	decline_extension_request_btn.setDisable(false);
        	extension_time_text.setDisable(false);
        	approve_due_time_request_btn.setDisable(true);
        	decline_due_time_request_btn.setDisable(true);
        	due_time_text.setDisable(true);
        	appoint_appraiser_comboBox.setDisable(true);
        	appoint_appraiser_btn.setDisable(true);
    		appoint_performance_leader_comboBox.setDisable(true);
    		appoint_performance_leader_btn.setDisable(true);
    		break;
    		
    	default:
    		add_extension__time_btn.setDisable(true);
        	decline_extension_request_btn.setDisable(true);
        	extension_time_text.setDisable(true);
        	approve_due_time_request_btn.setDisable(true);
        	decline_due_time_request_btn.setDisable(true);
        	due_time_text.setDisable(true);
        	appoint_appraiser_comboBox.setDisable(true);
        	appoint_appraiser_btn.setDisable(true);
    		appoint_performance_leader_comboBox.setDisable(true);
    		appoint_performance_leader_btn.setDisable(true);
        	break;
    	}
    }
    
    public static Supervisor_ProcessMain_Controller getInstance() {
		 return instance; 
	}
    
    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    	ControllerProcessMain.instance.getTheUpdateProcessesFromDB();
    }
}
