package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import java.sql.DriverManager;

import javafx.scene.control.Alert.AlertType;
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
	private Button AppointSupervisor;
	@FXML
	private Button log_out_btn;
	private ArrayList<String> IDChosenStaff= new ArrayList<String>();
	private ArrayList<String> FullNameChosenStaff= new ArrayList<String>();
	ArrayList<String> workersList = new ArrayList<String>();
	// Event Listener on Button[#back_btn].onAction
	@FXML
	public void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
	}
	// Event Listener on Button[#AppointCangesControlBoard].onAction
	@FXML
/*******************************************AppointsecondIEclick****************************************************************/	
    void AppointsecondIEclick(ActionEvent event) {
		ArrayList<Object> params = new ArrayList<Object>();
		
		String name =secondIE_comboBox.getValue();
		for(int i=0;i<FullNameChosenStaff.size();i++)
			if(FullNameChosenStaff.get(i).equals(name))//find the name in array and get his id
			{
				params.add(IDChosenStaff.get(i));
			}
		params.add("Information Engineer");
		
		Translator translator = new Translator(OptionsOfAction.UPDATEPERMANENT, params);
		Client.getInstance().handleMessageFromClientGUI(translator);
    }
/*******************************************AppointfirstIEclick***********************************************************/
    @FXML
    void AppointfirstIEclick(ActionEvent event) {
		ArrayList<Object> params = new ArrayList<Object>();
		
		String name =firstIE_comboBox.getValue();
		for(int i=0;i<FullNameChosenStaff.size();i++)
			if(FullNameChosenStaff.get(i).equals(name))//find the name in array and get his id
			{
				params.add(IDChosenStaff.get(i));
			}
		params.add("Information Engineer");
		
		Translator translator = new Translator(OptionsOfAction.UPDATEPERMANENT, params);
		Client.getInstance().handleMessageFromClientGUI(translator);
    }
/*****************************************Appoint_Chiarman_click**********************************************************/


	@FXML
	public void Appoint_Chiarman_click(ActionEvent event)
	{
		ArrayList<Object> params = new ArrayList<Object>();
		params.add("ChairMan");

		checkBefore("ChairMan");
	
	
	
	
	}
	
	
	/*********************************AppointSupervisor_click*****************************************************************/
	// Event Listener on Button[#AppointSupervisor].onAction
	@FXML
	public void AppointSupervisor_click(ActionEvent event) {
	ArrayList<Object> params = new ArrayList<Object>();
	//if(checkApoint("Supervisor")==true) {
		String name =supervisor_comboBox.getValue();
	
		for(int i=0;i<FullNameChosenStaff.size();i++)
			if(FullNameChosenStaff.get(i).equals(name))//find the name in array and get his id
			{
				params.add(IDChosenStaff.get(i));
			}
		params.add("Supervisor");
		
		Translator translator = new Translator(OptionsOfAction.UPDATEPERMANENT, params);
		Client.getInstance().handleMessageFromClientGUI(translator);
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
		Translator translator= new Translator(OptionsOfAction.SELECTCHAIRMAN,check);
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
		
	

	}
/*******************************************printMessage***********************************************************/
	public void printMessage(ArrayList<String> WorkersName)
	{	print_message.setVisible(true);
	System.out.println("print message");
		print_message.setText(WorkersName.get(0)+" " +WorkersName.get(1)+"is now: "+WorkersName.get(2));
		print_Chaiman.setText("Current in Chairnam position: " + (WorkersName.get(0)+" " +WorkersName.get(1)));
	/*	if(WorkersName.get(2)=="ChairMan")
		flagChairMan=1;
		else if(WorkersName.get(2)=="Supervisor")
			flagSupervisor=1;*/
		//getChairManData();
	}
/**************************************************checkApoint**************************************************************/
	public void checkApoint(ArrayList<String> WorkersName) {
		System.out.println(WorkersName.get(1)+"  "+WorkersName.get(0));
		Integer result = Integer.valueOf(WorkersName.get(0));	
		System.out.println("**2"+ result);
		if(WorkersName.get(1)=="ChairMan") {
		
			
			if(result==1) 
			{
				
				flagChairMan=1;
			}
		else {
			System.out.println("check appoint if 0");
			flagChairMan=0;
		     }
		}
			ArrayList<Object> params = new ArrayList<Object>();
			
			System.out.println("**3 flag:"+result);
			if(result==1) {//need to delete from db before put selected worker in role
				//flagChairMan=0;
				
				params.add("ChairMan");
				Translator translator = new Translator(OptionsOfAction.DELETEPERMANENT, params);
				Client.getInstance().handleMessageFromClientGUI(translator);
				params.remove(0);
			}
	
			
			
			String name =chairman_comboBox.getValue();
			System.out.println(name);//V
			for(int i=0;i<FullNameChosenStaff.size();i++)
				if(FullNameChosenStaff.get(i).equals(name))//find the name in array and get his id
				{
					params.add(IDChosenStaff.get(i));
				}
			params.add("ChairMan");
			
			Translator translator = new Translator(OptionsOfAction.UPDATEPERMANENT, params);
			Client.getInstance().handleMessageFromClientGUI(translator);
			
		}
	
		//flagChairMan=1;//apointed worker to role ChairMan
		//else if(WorkersName.get(2)=="Supervisor")
		
	
/**************************************************SET_DELETEPERMANENT**************************************************************/	
	public void SET_DELETEPERMANENT(ArrayList<String> WorkersName) {
		System.out.println("4-sucssed"+WorkersName.get(0));
		if(WorkersName.get(0)=="ChairMan")
		flagChairMan=0;
		}
/*********************************************checkBefore**********************************************************************/	
public void checkBefore(String role)
{
	ArrayList<Object> params = new ArrayList<Object>();
	params.add(role);

	Translator translator = new Translator(OptionsOfAction.checkDB, params);//check in db if this role empty
	Client.getInstance().handleMessageFromClientGUI(translator);//it will return flag of chair man
}
}
	

    

