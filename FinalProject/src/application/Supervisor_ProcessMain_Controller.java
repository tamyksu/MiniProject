package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import sun.misc.Cleaner;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import translator.OptionsOfAction;
import translator.Translator;

/**
 * This class is the controller of supervisor screen
 */
public class Supervisor_ProcessMain_Controller implements Initializable{
	
	public static Supervisor_ProcessMain_Controller instance;
	
	ArrayList<String> apprairsID;
	
	ArrayList<String> appraisersNames;
	
	private String stringProcess_stage;
	
	private int intProcess_stage = 1;
	
	private int procID;
	
	private ArrayList<Object> notifications;
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
    private TextField  due_time_text;
    
    @FXML
    private TextField extension_time_text;
    
    @FXML
    private Label current_appraiser_text;
    
    @FXML
    private Label current_performance_leader_text;
    
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
    
    @FXML
    private Label add_extension_explanation_text;
    
    

    @Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
	}
    
    /**
     * Freeze a process (suspends the process)
     * @param event
     */
    @FXML
    void freeze_process_btn_click(ActionEvent event) {
    	ArrayList <Object> processInfo = new ArrayList<Object>();

    	processInfo.add(ControllerProcessMain.getInstance().getRequestID());//process/request id
    	
    	Translator translator = new Translator(OptionsOfAction.FREEZE_PROCESS, processInfo);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    }

    /**
     * Shutdown a process
     * @param event
     */
    @FXML
    void shut_down_process_btn_click(ActionEvent event) {
    	ArrayList <Object> processInfo = new ArrayList<Object>();

    	processInfo.add(ControllerProcessMain.getInstance().getRequestID());//process/request id
    	
    	Translator translator = new Translator(OptionsOfAction.SHUTDOWN_PROCESS, processInfo);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    }
    
    /**
     * Get appraiser/performance leader data to put in the combo-boxes
     */
    public void getAppraiserOrPerformanceLeaderCBData()
    {
    	int processID;
    	
    	try
    	{
    		this.procID = processID = ControllerProcessMain.instance.getSelectedRowProcID();//needs rows in the processes table
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
    	
    	if(this.intProcess_stage != Constants.STAGE_OF_APPOINTING_APPRAISER && this.intProcess_stage != Constants.STAGE_OF_EXECUTION)
    	{
    		return;
    	}

    	try {
    		ArrayList<Object> check = new ArrayList<Object>();
    		//check.add(2);//process id
    		check.add(processID);
    		Translator translator = new Translator(OptionsOfAction.GET_APPRAISER_AND_PERFORMANCE_LEADER_CB_DATA, check);
    		Client.getInstance().handleMessageFromClientGUI(translator);
    	}
    	catch(Exception e){
    		System.out.println("EXCEPTION: Supervisor_ProcessMain_Controller - get ComboBox Data");
    	}
    }
    
    /**
     * set appraiser/performance leader data in their combo boxes
     * @param appraisersList
     */
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
    	
    	if(intProcess_stage==Constants.STAGE_OF_APPOINTING_APPRAISER)
    	{
    		appoint_appraiser_comboBox.setDisable(false);
    		if(appraisersForCB != null)
    		{
    			Platform.runLater(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
		    			appoint_appraiser_comboBox.setItems(appraisersForCB);
		    			appoint_appraiser_comboBox.setValue(appraisersForCB.get(0));
		    			appoint_appraiser_btn.setDisable(false);
					}
    				// ...
    				});

    		}
    	}
    		
    	if(intProcess_stage == Constants.STAGE_OF_EXECUTION)
    	{
			appoint_performance_leader_comboBox.setDisable(false);
			if(appraisersForCB != null)
			{
				appoint_performance_leader_comboBox.setItems(appraisersForCB);
				appoint_performance_leader_comboBox.setValue(appraisersForCB.get(0));
		   		appoint_performance_leader_btn.setDisable(false);
			}
    	}
    	
    	if(listSize == 0)
    	{
    		appoint_appraiser_btn.setDisable(true);
    		appoint_performance_leader_btn.setDisable(true);
    	}
    	else
    		System.out.println("!" + appraisersForCB + "!");

    }
    
    /**
     * Appoint Appraiser or performance leader
     * @param e - event
     */
    public void appointAppraiserOrPerformanceLeader(ActionEvent e)
    {
    	//int processID = ControllerProcessMain.instance.getSelectedRowNumber();
    	String chosenID;
    	String chosenAppraiser;
    	try
    	{
    		if(this.intProcess_stage == Constants.STAGE_OF_APPOINTING_APPRAISER)
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
    		if(intProcess_stage == Constants.STAGE_OF_APPOINTING_APPRAISER)
    			check.add("Appraiser");
    		else
    			check.add("Performance Leader");
    		
    		Translator translator = new Translator(OptionsOfAction.APPOINT_APPRAISER_OR_PERFORMANCE_LEADER, check);
    		Client.getInstance().handleMessageFromClientGUI(translator);
    		
    		if(intProcess_stage == Constants.STAGE_OF_APPOINTING_APPRAISER)
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
        		intProcess_stage = 2;
    		}
    		else
    		if(intProcess_stage == Constants.STAGE_OF_EXECUTION)
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
        		intProcess_stage = Constants.STAGE_OF_EXECUTION;
    		}

    	}
    	catch(Exception ex)
    	{
    		System.out.println("No choice was made");
    		return;
    	}
    
    }
    
    /**
     * Gets the appraiser and the performance leader labels
     */
    public void getAppraiserAndPerformanceLeaderLabels()
    {
    	current_appraiser_text.setText("None");
    	current_performance_leader_text.setText("None");
    	
    	if(this.procID == -1)
    	{
    		System.out.println("getAppraiserAndPerformanceLeaderLabels - this.procID is wrong");
    		return;
    	}
    		
    	if(intProcess_stage > Constants.STAGE_OF_APPOINTING_APPRAISER)
    	{        	
        	ArrayList<Object> arr = new ArrayList<Object>();
        	arr.add(this.procID);
        	Translator translator = new Translator(OptionsOfAction.GET_APPRAISER_AND_PERFORMANCE_LEADER_OF_PROC, arr);
        	Client.instance.handleMessageFromClientGUI(translator);
    	}
    }
    
    /**
	* Sets the appraiser and the performance leader labels
     * @param arr - array of data to set
     */
    public void setAppraiserAndPerformanceLeaderLabels(ArrayList <String> arr)
    {
    	System.out.println("setLabels: process_stage = " + intProcess_stage);
    	
    	Platform.runLater(new Runnable() {  //avoiding java.lang.IllegalStateException “Not on FX application thread”
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
    
    /**
     * Set due time from a due time request from the appraiser or the performance leader
     * @param msgData
     */
    public void setDueTimeRequest(ArrayList<Object> msgData)
    {
    	try
    	{
    		

    	if(msgData==null)
    	{
    		System.out.println("msgData is empty");
    		due_time_text.setText("Not Set");
    	}
    	else
    	{
	    	this.notifications = msgData;
	    	
	    	if(due_time_text.isDisable() || msgData.size() == 0)
	    		return;
	    	
	    	System.out.println("setDueTimeExtension: msgData: " + msgData);
	    	for(int i=0 ; i<msgData.size() ; i = i+6)
	    	{
	    		if(this.procID == (int)(msgData.get(i)))
	    		{
	    			if(((String)msgData.get(i+1)).compareTo("define execution stage due time") == 0)
	    			{
	    				due_time_text.setText(String.valueOf(msgData.get(i+2)));
	    				return;
	    			}
	    		}
	    	}
    	}
    	}
    	catch(ClassCastException ex)
    	{
    		
    	}
    }
    
    
    /**
     * Approve the requested  due time from the appraiser or performance leader
     * @param e
     */
    public void ApproveDueTimeRequest(ActionEvent e)
    {
    	//int processID = ControllerProcessMain.instance.getSelectedRowNumber();

    	ArrayList <Object> arr = new ArrayList<Object>();
    	
    	arr.add(this.procID);//process/request id
    	arr.add(due_time_text.getText());
    	arr.add(this.intProcess_stage);
    	
    	Translator translator = new Translator(OptionsOfAction.SET_EVALUATION_OR_EXECUTION_DUE_TIME, arr);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	due_time_text.setText("");
    	approve_due_time_request_btn.setDisable(true);
		decline_due_time_request_btn.setDisable(true);
		due_time_text.setDisable(true);
    }
    
    /**
     * Decline a due time request from the appraiser or the performance leader
     * @param e
     */
    public void DeclineDueTimeRequest(ActionEvent e)
    {
    	ArrayList <Object> arr = new ArrayList<Object>();

    	arr.add(this.procID);//process/request id
    	arr.add(due_time_text.getText());
    	arr.add(this.intProcess_stage);
    	
    	Translator translator = new Translator(OptionsOfAction.DECLINE_EVALUATION_OR_EXECUTION_DUE_TIME, arr);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	due_time_text.setText("");
    	approve_due_time_request_btn.setDisable(true);
		decline_due_time_request_btn.setDisable(true);
		due_time_text.setDisable(true);
    }
    
    
    /**
     * Set a due time extension explanation in its text area
     */
    public void setDueTimeExtensionExplanation()
    {
    	if(add_extension__time_btn.isDisable())
    		return;
    	
    	ArrayList<Object> notes = this.notifications;
    	
    	for(int i=0 ; i<notes.size() ; i=i+6)
    	{
    		if(this.procID == (int)(notes.get(i)) && ((String)notes.get(i+1)).compareTo("add due time extension") == 0)
    		{
    			try
    			{
    				String str = ((String)notes.get(i+4)).toString();//can't use i in the thread
    				
    				Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException “Not on FX application thread”
    		    	    public void run() {
    		    	    	add_extension_explanation_text.setDisable(false);
    		    	    	add_extension_explanation_text.setText(str.toString());
    	    				if(add_extension_explanation_text.getText().compareTo("") == 0)
    	    					System.out.println("Supervisor_ProcessMain_Controller - setDueTimeExtensionExplanation - "
    	        						+ "the explanation for the due time extension request is empty");
    		    	    }
    		    	});
    				
        			break;
    			}
    			
    			catch(NullPointerException e)
    			{
    				System.out.println("Supervisor_ProcessMain_Controller - setDueTimeExtensionExplanation - "
    						+ "the explanation for the due time extension request is null");
    				System.out.println(e.getMessage());
    				add_extension_explanation_text.setText("");
    				return;
    			}
    		}
    	}
    	
    }
    
    /**
     * Add due time extention
     * @param e - event
     */
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
    	arr.add(this.stringProcess_stage);
    	
    	Translator translator = new Translator(OptionsOfAction.ADD_EVALUATION_OR_EXECUTION_EXTENSION_TIME, arr);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	extension_time_text.clear();
    }
    
    /** 
     * Decline Due Time Extension
     * @param e
     */
    public void declineDueTimeExtension(ActionEvent e)
    {
    	ArrayList <Object> arr = new ArrayList<Object>();

    	arr.add(this.procID);//process/request id
    	arr.add(this.stringProcess_stage);
    	
    	Translator translator = new Translator(OptionsOfAction.DECLINE_EVALUATION_OR_EXECUTION_EXTENSION_TIME, arr);
    	Client.getInstance().handleMessageFromClientGUI(translator);
    	extension_time_text.clear();
    	add_extension__time_btn.setDisable(true);
    	decline_extension_request_btn.setDisable(true);
		extension_time_text.setDisable(true);

    }
    
    
    /** 
     * Set process ID
     * @param proc
     */
    public void setProcessID(int proc)
    {
    	Supervisor_ProcessMain_Controller.instance.procID = proc;
    }
    
    /**
     * Initializes the chosen process screen with the relevant data.
     * @param processStage - stage number of the relevant process
     */
    void initializeChosenProcessScreen(String processStage)
    {
    	System.out.println("Supervisor_ProcessesMain: initializeChosenProcessScreen: Stage: " + processStage);
    	if(processStage == null)
    		return;
    	try
    	{
    		this.intProcess_stage = Integer.parseInt(processStage);
        	
    	}
    	catch(NumberFormatException e)
    	{
    		double temp = Double.parseDouble(processStage);
    		this.intProcess_stage = (int)temp;
    	}
    	
    	this.stringProcess_stage = processStage;
    	
    	switch(processStage)
    	{
    	case "2.5":
    	case "3":
    	case "7.5":
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
    		
    	case "4.2":
    	case "4.5":
    	case "5.2":
    	case "5.5":
    	case "9.2":
    	case "9.5":
    	case "11.2":
    	case "11.5":
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
    
    /**
     * Go back to the previous screen
     * @param event
     */
    @FXML
    public void back_click(ActionEvent event) {
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
    
    /**
     * Update the table with the process' information
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
    
    /**
     * Adjust Suspended Buttons
     */
    public void adjustSuspendedButtons()
    {
		freeze_process_btn.setDisable(true);
		add_extension__time_btn.setDisable(true);
		decline_extension_request_btn.setDisable(true);
		add_extension_explanation_text.setDisable(true);
		approve_due_time_request_btn.setDisable(true);
		decline_due_time_request_btn.setDisable(true);
		appoint_appraiser_btn.setDisable(true);
		appoint_performance_leader_btn.setDisable(true);
		extension_time_text.setDisable(true);
		due_time_text.setDisable(true);
    }
    
}
