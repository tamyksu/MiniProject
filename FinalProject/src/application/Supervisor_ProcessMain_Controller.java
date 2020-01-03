package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import translator.OptionsOfAction;
import translator.Translator;

public class Supervisor_ProcessMain_Controller implements Initializable{
	
	public static Supervisor_ProcessMain_Controller instance;
	
	Client client = Client.getInstance();
	
	ArrayList<String> apprairsID;
	
	ArrayList<String> appraisersNames;
	
	private int appraiser1_performance2_flag = 1;
	
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
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
	}
    
    public void getAppraiserOrPerformanceLeaderCBData()
    {
    	int processID = ControllerProcessMain.instance.getSelectedRowNumber();//needs rows in the processes table

    	if(appraiser1_performance2_flag == 3)
    	{
    		appoint_appraiser_comboBox.setDisable(true);
    		appoint_appraiser_btn.setDisable(true);
			appoint_performance_leader_comboBox.setDisable(true);
			appoint_performance_leader_btn.setDisable(true);
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
    		System.out.println("not good");
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
    	
    	if(appraiser1_performance2_flag == 1)
    	{
    		appoint_appraiser_comboBox.setDisable(false);
    		if(appraisersForCB != null)
    			appoint_appraiser_comboBox.setItems(appraisersForCB);
    		//appoint_appraiser_comboBox.setPromptText("Make a choice");
    		appoint_appraiser_btn.setDisable(false);
    		appoint_performance_leader_btn.setDisable(true);
    	}
    		
    	if(appraiser1_performance2_flag == 2)
    	{
			appoint_performance_leader_comboBox.setDisable(false);
			if(appraisersForCB != null)
				appoint_performance_leader_comboBox.setItems(appraisersForCB);
    		//appoint_performance_leader_comboBox.setPromptText("Make a choice");
    		appoint_performance_leader_btn.setDisable(false);
    		appoint_appraiser_btn.setDisable(true);
    	}
    	
    	if(listSize == 0)
    	{
    		appoint_appraiser_btn.setDisable(true);
    		appoint_performance_leader_btn.setDisable(true);
    	}

    }
    
    public void appointAppraiserOrPerformanceLeader(ActionEvent e)
    {
    	int processID = ControllerProcessMain.instance.getSelectedRowNumber();
    	String chosenID;
    	String chosenAppraiser;
    	try
    	{
    		if(appraiser1_performance2_flag == 1)
        		chosenAppraiser = appoint_appraiser_comboBox.getValue();
        	else
        		chosenAppraiser = appoint_performance_leader_comboBox.getValue();
    		
    		chosenID = this.apprairsID.get(this.appraisersNames.indexOf(chosenAppraiser));
    		
    		ArrayList<Object> check = new ArrayList<Object>();
    		check.add(chosenID);
    		
    		check.add(processID);//check.add(2);//check.add(processID);
    		if(appraiser1_performance2_flag == 1)
    			check.add("Appraiser");
    		else
    			check.add("Performance Leader");
    		
    		Translator translator = new Translator(OptionsOfAction.APPOINT_APPRAISER_OR_PERFORMANCE_LEADER, check);
    		client.handleMessageFromClientGUI(translator);
    		
    		if(appraiser1_performance2_flag == 1)
    		{
    			appoint_appraiser_comboBox.setDisable(true);
        		appoint_appraiser_btn.setDisable(true);
    			appraiser1_performance2_flag = 2;
    		}

    		if(appraiser1_performance2_flag == 2)
    		{
    			appoint_performance_leader_comboBox.setDisable(true);
    			appoint_performance_leader_btn.setDisable(true);
    			appraiser1_performance2_flag = 3;
    		}

    		if(appraiser1_performance2_flag == 2)
    			getAppraiserOrPerformanceLeaderCBData();
    		
    	}
    	catch(Exception ex)
    	{
    		System.out.println("No choice was made");
    		return;
    	}
    
    }
    
    public void ApproveDueTimeRequest(ActionEvent e)
    {
    	//int processID = ControllerProcessMain.instance.getSelectedRowNumber();

    	ArrayList <Object> arr = new ArrayList<Object>();
    	
    	arr.add(2);//process/request id
    	arr.add(due_time_text.getText());
    	
    	Translator translator = new Translator(OptionsOfAction.SET_EVALUATION_OR_EXECUTION_DUE_TIME, arr);
    	client.handleMessageFromClientGUI(translator);
    }
    
    public void DeclineDueTimeRequest(ActionEvent e)
    {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	
        alert.setTitle("ALERT");
        alert.setHeaderText("THERE ARE NO MESSAGES");
        alert.setContentText("need to send a message to the appraiser or the performance leader to resend a request");
        alert.showAndWait();
    }
    
    void initializeFlag(String processStage)
    {
    	System.out.println("Stage: " + processStage);
    	if(processStage.compareTo("1") == 0)
    		appraiser1_performance2_flag = 1;
    	else if(processStage.compareTo("4") == 0)
    			appraiser1_performance2_flag = 2;
    	else
			appraiser1_performance2_flag = 3;
    	
    }
    public static Supervisor_ProcessMain_Controller getInstance() {
		 return instance; 
	}
    
    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }
}
