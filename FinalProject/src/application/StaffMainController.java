package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import java.sql.DriverManager;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.net.URL;
import java.sql.Connection;
import client.Client;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.xml.internal.ws.org.objectweb.asm.Label;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import java.sql.ResultSet;


import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import translator.OptionsOfAction;
import translator.Translator;


public class StaffMainController implements Initializable{
	
	Client client = Client.getInstance();
	public static StaffMainController instance;
	PreparedStatement stmt;
	private static Connection conn;
	public int flagChairMan=0;
	public int flagSupervisor=0;
	@FXML
	private Button back_btn;
    @FXML
    private Text print_message;
    @FXML
    private Text print_Chaiman;
    @FXML
    private Text print_IE1;
    @FXML
    private Text print_IE2;
    @FXML
    private Text print_supervisor;
	@FXML
	private Button AppointCangesControlBoard;
	@FXML
	private ComboBox<String>  chairman_comboBox;
	@FXML
	private ComboBox<String> firstIE_comboBox;
	@FXML
	private ComboBox<String> secondIE_comboBox;
	@FXML
	private ComboBox<String> supervisor_comboBox;
	@FXML
	private TableView<PermanentWorker> permanent_workers_table;
	@FXML
	private TableColumn<PermanentWorker, String> PERuserId;

	@FXML
	private TableColumn<PermanentWorker, String> PERuserFullName;

	@FXML
	private TableColumn<PermanentWorker, String> PERrole;

	@FXML
	private TableView<TemporaryWorker> temporary_workers_table;
	@FXML
	private TableColumn<TemporaryWorker, String> TEMPuserID;

	@FXML
	private TableColumn<TemporaryWorker, String> TEMPuserFullName;

	@FXML
	private TableColumn<TemporaryWorker, Integer> TEMPprocessID;

	@FXML
	private TableColumn<TemporaryWorker, String> TEMProle;
	@FXML
	String save_role="";
	int choosen_person_not_available=0;
	private Button AppointSupervisor;
	@FXML
	private Button log_out_btn;
     String  save_id="";
	private ArrayList<String> IDChosenStaff= new ArrayList<String>();
	private ArrayList<String> FullNameChosenStaff= new ArrayList<String>();
	ArrayList<String> workersList = new ArrayList<String>();
	// Event Listener on Button[#back_btn].onAction
	
	
	
	@FXML
	public void active_reports_button(ActionEvent event) {
	 	ScreenController.getScreenController().activate("active_reports");
	}
	/****************************************back_click******************************************************************/
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
	// Event Listener on Button[#AppointCangesControlBoard].onAction
	@FXML
/*******************************************AppointsecondIEclick****************************************************************/	
    void AppointsecondIEclick(ActionEvent event) {
	  	ArrayList<Object> params = new ArrayList<Object>();
			params.add("Change Board Member-2"); // Information Engineer

		checkBefore("Change Board Member-2","2"); // Information Engineer
    }
/*******************************************AppointfirstIEclick***********************************************************/
    @FXML
    void AppointfirstIEclick(ActionEvent event) {

    	ArrayList<Object> params = new ArrayList<Object>();
		params.add("Change Board Member-1"); // Information Engineer

	checkBefore("Change Board Member-1","2"); // Information Engineer
    }
/*****************************************Appoint_Chiarman_click**********************************************************/

	@FXML
	public void Appoint_Chiarman_click(ActionEvent event)
	{
		ArrayList<Object> params = new ArrayList<Object>();
		params.add("ChairMan");
	checkBefore("ChairMan","2");
	
	

}
		
	
	
	/*********************************AppointSupervisor_click*****************************************************************/
	// Event Listener on Button[#AppointSupervisor].onAction
	@FXML
	public void AppointSupervisor_click(ActionEvent event) {
		ArrayList<Object> params = new ArrayList<Object>();
		params.add("Supervisor");

	checkBefore("Supervisor","2");
	}
	//}
	/**************************************initialize*******************************************************************/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		instance = this;	
		print_message.setVisible(false);
	}
/*******************************************getChairManData****************************************************************/
	public void getChairManData() {
		try {
			print_message.setVisible(false);
		ArrayList<String> check= new ArrayList<String>();
		Translator translator= new Translator(OptionsOfAction.INITIALIZE_COMBO_BOX,check);
		client/*Client.getInstance()*/.handleMessageFromClientGUI(translator);
	}
		catch(Exception e){}
	}
/*********************************************setDataChairMan***********************************************************/	
	public void setDataChairMan(ArrayList<String> WorkersName)
	{
		print_message.setVisible(false);
		ObservableList<String> data = FXCollections.observableArrayList();
		
		for (int i=0;i<WorkersName.size()/3;i++) { // get the processID from the Select query
			
			data.add(new String (WorkersName.get(i*3)+" "+WorkersName.get(i*3+1)));
			this.IDChosenStaff.add(new String(WorkersName.get(i*3+2)));
			}
	
		chairman_comboBox.setItems(data);
		firstIE_comboBox.setItems(data);
		secondIE_comboBox.setItems(data);
		supervisor_comboBox.setItems(data);
		this.FullNameChosenStaff.addAll(data);
		System.out.println(FullNameChosenStaff);
		checkBefore("ChairMan","1");
		checkBefore("Supervisor","1");
		checkBefore("Change Board Member-1","1"); // Information Engineer
		checkBefore("Change Board Member-2","1"); // Information Engineer
	}
	public void afterSet(ArrayList<String> WorkersName)//get name of current chairman
	{
		ArrayList<Object> params = new ArrayList<Object>();
		Integer result = Integer.valueOf(WorkersName.get(0));
		if(result==1) {//there some one in parmenent table
			
		params.add(WorkersName.get(1));
		
		Translator translator = new Translator(OptionsOfAction.CURRENT_IN_ROLE,params);
		Client.getInstance().handleMessageFromClientGUI(translator);
		}
	}
	
	public void printMessage1(ArrayList<String> WorkersName)
	{ 
		System.out.println("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));

		if(WorkersName.get(3).equals("2")&& WorkersName.get(2).equals("ChairMan"))
		{
			
			print_Chaiman.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		else if(WorkersName.get(3).equals("3") && WorkersName.get(2).equals("Supervisor"))
		{
			
			print_supervisor.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		else if(WorkersName.get(3).equals("4") && WorkersName.get(2).equals("Change Board Member-1")) // Information Engineer
		{
			System.out.println("Change Board Member-1 not empty"); // Information Engineer
			print_IE1.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		else if(WorkersName.get(3).equals("5") && WorkersName.get(2).equals("Change Board Member-2")) // Information Engineer
		{
		
			print_IE2.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		
		
	}
	
/*******************************************printMessage***********************************************************/
	public void printMessage(ArrayList<String> WorkersName)
	{	
		
	
		if(WorkersName.get(3).equals("2")&& WorkersName.get(2).equals("ChairMan"))
		{
			
			print_Chaiman.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		else if(WorkersName.get(3).equals("3") && WorkersName.get(2).equals("Supervisor"))
		{
			
			
			print_supervisor.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		else if(WorkersName.get(3).equals("4") && WorkersName.get(2).equals("Change Board Member-1")) // Information Engineer
		{
			System.out.println("Change Board Member-1 not empty"); // Information Engineer
			print_IE1.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		else if(WorkersName.get(3).equals("5") && WorkersName.get(2).equals("Change Board Member-2")) // Information Engineer
		{
		
			print_IE2.setText("Current in " + WorkersName.get(2) + "position:\n" + (WorkersName.get(0)+" " +WorkersName.get(1)));
		}
		
		
		if((WorkersName.get(4).equals("7")))
		{
		print_message.setVisible(true);
		
		print_message.setText(WorkersName.get(0)+" " +WorkersName.get(1)+"is now: "+WorkersName.get(2));
		}

		
	}
/**************************************************checkApoint**************************************************************/
	public void checkApoint(ArrayList<String> WorkersName) {
	
		Integer result = Integer.valueOf(WorkersName.get(0));	
		

			ArrayList<Object> params = new ArrayList<Object>();
			save_role=WorkersName.get(1);
			String name="";
			if(WorkersName.get(1).equals("ChairMan"))
			 name =chairman_comboBox.getValue();
			else if (WorkersName.get(1).equals("Supervisor"))
				 name =supervisor_comboBox.getValue();
			else if(WorkersName.get(1).equals("Change Board Member-1")) // Information Engineer
				 name =firstIE_comboBox.getValue();
			else if(WorkersName.get(1).equals("Change Board Member-2")) // Information Engineer
				 name =secondIE_comboBox.getValue();
			
		
			for(int i=0;i<FullNameChosenStaff.size();i++)
				if(FullNameChosenStaff.get(i).equals(name))//find the name in array and get his id
				{
					params.add(IDChosenStaff.get(i));
				}
			
			Translator translator1 = new Translator(OptionsOfAction.checkNAMEParmenent, params);
			Client.getInstance().handleMessageFromClientGUI(translator1);
			params.remove(0);
		
			if(result==1) {//if the place im from empty
		
				params.add(WorkersName.get(1));
				Translator translator = new Translator(OptionsOfAction.DELETEPERMANENT, params);
				Client.getInstance().handleMessageFromClientGUI(translator);
				params.remove(0);
			}
	

		}
	
	
		
	public void check_if_this_man_available(ArrayList<String> WorkersName)
	{			
		ArrayList<Object> params = new ArrayList<Object>();

		if(WorkersName.get(0).equals("1"))
			{
		

				params.add(WorkersName.get(1));//send id
				Translator translator = new Translator(OptionsOfAction.DELETEPERMANENT, params);
				Client.getInstance().handleMessageFromClientGUI(translator);
				params.remove(0);
			}
				//sparams.remove(0);
		    params.add(WorkersName.get(1));
				params.add(save_role);
		
					
					Translator translator1 = new Translator(OptionsOfAction.UPDATEPERMANENT, params);
					Client.getInstance().handleMessageFromClientGUI(translator1);
		
		
		
	}
/**************************************************SET_DELETEPERMANENT**************************************************************/	
	public void SET_DELETEPERMANENT(ArrayList<String> WorkersName) {
		if(WorkersName.get(0).equals("ChairMan"))
			print_Chaiman.setText("empty position");
		
	else if(WorkersName.get(0).equals("Supervisor"))
		print_supervisor.setText("empty position");
	
	else if(WorkersName.get(0).equals("Change Board Member-1")) // Information Engineer
		print_IE1.setText("empty position");
	
	else if(WorkersName.get(0).equals("Change Board Member-2")) // Information Engineer
		print_IE2.setText("empty position");
	}

/*********************************************checkBefore**********************************************************************/	
	public void checkBefore(String role,String option)
	{
	
	
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(role);
		params.add(option);
	
		Translator translator = new Translator(OptionsOfAction.checkDB, params);//check in db if this role empty
		Client.getInstance().handleMessageFromClientGUI(translator);//it will return flag of chair man
	}

	
	@FXML
	void active_reports(ActionEvent event) {
	
	}
	
	@FXML
	void execution_reports(ActionEvent event) {
	
	}
	
	@FXML
	void delay_execution(ActionEvent event) {
	
	}

	public void getTemporaryWorkersFromDB()
	{
		ArrayList<Object> check = new ArrayList<Object>();
		
		Translator translator = new Translator(OptionsOfAction.GET_TEMPORARY_WORKERS_FROM_DB, check);
		client.handleMessageFromClientGUI(translator);
	}
	
	public void setTemporaryWorkersInTable(ArrayList <Object> rs)
	{
		//temporary_workers_table
		
		//System.out.println(rs);
		ObservableList<TemporaryWorker> data;
		ArrayList<TemporaryWorker> workersList = new ArrayList<TemporaryWorker>();

		if (this.temporary_workers_table.isEditable() == false) {
			this.temporary_workers_table.setEditable(true);
		}
		
		for (int i = 0; i < rs.size(); i=i+4) {
			TemporaryWorker tempworker = new TemporaryWorker((String)rs.get(i).toString(), 
					(String)rs.get(i+1).toString(),(int)(rs.get(i+2)),(String)rs.get(i+3).toString());
			workersList.add(tempworker);
		}
		
		//System.out.println("StaffMainController - setTemporaryWorkersInTable - workersList:\n" + workersList);
		
		data = FXCollections.observableArrayList(workersList);
		this.TEMPuserID.setCellValueFactory(new PropertyValueFactory<TemporaryWorker, String>("userID"));
		this.TEMPuserFullName.setCellValueFactory(new PropertyValueFactory<TemporaryWorker, String>("userFullName"));
		this.TEMPprocessID.setCellValueFactory(new PropertyValueFactory<TemporaryWorker, Integer>("processID"));
		this.TEMProle.setCellValueFactory(new PropertyValueFactory<TemporaryWorker, String>("role"));
		this.temporary_workers_table.setItems(data);
	}
	
	public void getPermanentWorkersFromDB()
	{
		ArrayList<Object> check = new ArrayList<Object>();
		
		Translator translator = new Translator(OptionsOfAction.GET_PERMANENT_WORKERS_FROM_DB, check);
		client.handleMessageFromClientGUI(translator);
	}
	
	public void setPermanentWorkersInTable(ArrayList <Object> rs)
	{
		//permanent_workers_table
		
		//System.out.println(rs);
		ObservableList<PermanentWorker> data;
		ArrayList<PermanentWorker> workersList = new ArrayList<PermanentWorker>();

		if (this.permanent_workers_table.isEditable() == false) {
			this.permanent_workers_table.setEditable(true);
		}

		for (int i = 0; i < rs.size(); i=i+3) {
			PermanentWorker tempworker = new PermanentWorker((String)rs.get(i).toString(), 
					(String)rs.get(i+1).toString(),(String)rs.get(i+2).toString());
			workersList.add(tempworker);
		}
		
		//System.out.println("StaffMainController - setTemporaryWorkersInTable - workersList:\n" + workersList);
		
		data = FXCollections.observableArrayList(workersList);
		this.PERuserId.setCellValueFactory(new PropertyValueFactory<PermanentWorker, String>("userID"));
		this.PERuserFullName.setCellValueFactory(new PropertyValueFactory<PermanentWorker, String>("userFullName"));
		this.PERrole.setCellValueFactory(new PropertyValueFactory<PermanentWorker, String>("role"));
		this.permanent_workers_table.setItems(data);
	}
}
	


    

