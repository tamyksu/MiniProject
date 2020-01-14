package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;

import client.Client;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;


import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import translator.OptionsOfAction;
import translator.Translator;

public class ActiveReportsController implements Initializable{ 

	Client client = Client.getInstance();
		public static ActiveReportsController instance;
    @FXML
    private TextField frozen_median_text;

    @FXML
    private TextField total_days_median_text;

    @FXML
    private DatePicker start_date_button;

    @FXML
    private TextField close_median_text;

    @FXML
    private TextField active_fd_text;

    @FXML
    private TextField active_sd_text;

    @FXML
    private TextField total_days_sd_text;

    @FXML
    private TextField total_days_fd_text;

    @FXML
    private TextField frozen_fd_text;

    @FXML
    private TextField frozen_sd_text;

    @FXML
    private DatePicker end_date_button;

    @FXML
    private TextField active_median_text;

    @FXML
    private TextField rejected_median_text;

    @FXML
    private TextField rejected_fd_text;

    @FXML
    private TextField closed_sd_text;

    @FXML
    private TextField rejected_sd_text;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		instance = this;	

	}
    @FXML
    void done_button(ActionEvent event) {
    	
    	LocalDate start_date=start_date_button.getValue();//get start date
    	LocalDate end_date=end_date_button.getValue();//get end date
    	  System.out.println(" LocalDate start"+start_date );
    	
    	 ArrayList<LocalDate> dates= new ArrayList<LocalDate>();
    	 dates.add(start_date);
    	 dates.add(end_date);
 		Translator translator= new Translator(OptionsOfAction.Get_Active_Statistic,dates);
 		client/*Client.getInstance()*/.handleMessageFromClientGUI(translator);
    }
    
    public void calaulate(ArrayList<ArrayList<Integer>> status_counter)
    {
    	//0-active 1-suspend 2-shutdown 3-rejected
   // 	for(String i:status_counter.get(0))
    	//for(Integer i:status_counter.get(0))
    	//System.out.println(status_counter.get(0).get(i));
    }
	
	
	
	
}
