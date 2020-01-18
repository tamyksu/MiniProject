package application;

import client.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import application.StatisticReports;
import java.lang.Runnable;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import translator.OptionsOfAction;
import translator.Translator;
public class ActiveReportsController extends StatisticReports implements Initializable { 
	
	  ArrayList<LocalDate> date_graph;
		private static Connection conn;
		
    @FXML
    private DatePicker end_date_button;
    @FXML
    private Text sdActive_txt;
    @FXML
    private Text sdWorkdays_txt;
    @FXML
    private Text sdRejected_txt;
    @FXML
    private Text sdClosed_txt;
    @FXML
    private Text median_workdays_count;
    @FXML
    private Text sdWorkdays_count;
    @FXML
    private Text sdSuspended_txt;
    @FXML
    private Text total_workdays_txt;
    @FXML
    private Text rejected_median_txt;

    @FXML
    private Text closed_median;


    @FXML
    private Text suspended_median_txt;
    @FXML
    private Text active_median_txt;
    @FXML
    private DatePicker start_date_button;
    @FXML
    private TextField num_days;
    @FXML
    private CategoryAxis x;
	@FXML
	private Button back_btn;
    @FXML
    private NumberAxis y;
    @FXML
    private BarChart<?, ?> work_days_report;

    @FXML
    private NumberAxis y1;

    @FXML
    private CategoryAxis x1;
	long num;
    @FXML
    private BarChart<?, ?> active_reports;
    
	public static ActiveReportsController instance;
	
	LocalDate end_index;
	LocalDate start_index;
	LocalDate end_date;
	LocalDate start_date;
	
   

	@Override
	public void initialize(URL location, ResourceBundle resources) 
	{
		// TODO Auto-generated method stub

		instance = this;	

	}
	
	@FXML
	public void back_click(ActionEvent event) {
    	active_reports.getData().clear();
    	work_days_report.getData().clear();
    	start_date_button.setValue(null);
    	end_date_button.setValue(null);
    	num_days.clear();
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
    @FXML
    void done_button(ActionEvent event) 
    {
    	active_reports.getData().clear();
    	work_days_report.getData().clear();
    	 ArrayList<Object> all= new ArrayList<>();
    	 ArrayList<LocalDate> dates= new ArrayList<LocalDate>();


				
					// TODO Auto-generated method stub
    	 ArrayList<Long> days;
    		try
        	{
    		
    				LocalDate start_date=start_date_button.getValue();//get start date
    		    	 end_date=end_date_button.getValue();//get end date
    		    	  System.out.println(" LocalDate start"+start_date );
    		    	   //num_days.getText();
    		    	 days= new ArrayList<Long>();
    			 num = Long.parseLong(num_days.getText());
    			
    	 			
    			 if(num<1)
    			 {
    				
    				 Platform.runLater(new Runnable(){
    			@Override
				public void run() {
    				 Alert alert = new Alert(AlertType.INFORMATION);
    	            	
    	                alert.setTitle("ALERT");
    	                alert.setHeaderText("incorrect input!");
    	                alert.setContentText("Please fill with positive number");
    	                alert.showAndWait();
    					// ...
    		}
    			}); 
    				 return;
    				 }
    			
    			
    			 days.add(num);
    	    	   start_index=start_date;
    				 end_index=start_index.plusDays(num);
    	    	
    	    	 dates.add(start_date);
    	    	 dates.add(end_date);
    	    	   all.add(dates);
    	    	   all.add(days);
    	    		date_graph= new ArrayList<LocalDate>();
    				    	    start_index=start_date;
    							 end_index=start_index.plusDays(num);
    							 System.out.println("start date" +start_date );
    							while(!start_index.isAfter(end_date))
    							{
    								 date_graph.add(start_index);
    									start_index=end_index;
    									end_index= end_index.plusDays(num);
    							}
    							System.out.println(date_graph+"date graph size");
    							 date_graph.add(end_date);

    	 		Translator translator= new Translator(OptionsOfAction.Get_Active_Statistic,all);
    	 		Client.getInstance().handleMessageFromClientGUI(translator);
    			
    			 
    		
   
    			
        	}
     		
        	catch(NullPointerException e)
        	{
        		Platform.runLater(new Runnable(){
        			@Override
    				public void run() {
        		Alert alert = new Alert(AlertType.INFORMATION);
            	
                alert.setTitle("ALERT");
                alert.setHeaderText("No data was input!");
                alert.setContentText("Please select date and number of days");
                alert.showAndWait();
        			}
        			});
        		return;
        		}
        			catch(NumberFormatException e)
                	{
                		Platform.runLater(new Runnable(){
                			@Override
            				public void run() {
                		Alert alert = new Alert(AlertType.INFORMATION);
                    	
                        alert.setTitle("ALERT");
                        alert.setHeaderText("No data was input!");
                        alert.setContentText("Please select date and number of days");
                        alert.showAndWait();
                     
                		}
    		});
        		
        		   return;
        	}
    	  
    		
    	
    
 		/***********************************************************/
    }
    
    
   /*****************************************calaulate**************************************************/ 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void calaulate(ArrayList<ArrayList<Integer>> status_counter)
    {
    	
    //	active_reports.getData().clear();
  
    	Platform.runLater(new Runnable(){

				@Override
				public void run() {
				
				ArrayList<Integer> arr= status_counter.get(0);
			
				ArrayList<Double> median=new ArrayList<>();
			
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LLLL-yyyy");
		
					 arr= status_counter.get(4);
				
/**************************************************************median		*/		

					for(int i=0;i<6;i++)	
					{
						 arr= status_counter.get(i);
				
					Double value=medianALL(arr);
							median.add(value);
						}
				
					String active =String.format("%.2f", median.get(0));
					String suspend =String.format("%.2f", median.get(1));
					String closed =String.format("%.2f", median.get(2));
					String rejected =String.format("%.2f", median.get(3));
					String count_work =String.format("%.2f", median.get(4));
					String days_work =String.format("%.2f", median.get(5));
					active_median_txt.setText(active );
					suspended_median_txt.setText(suspend);
					closed_median.setText(closed);
					rejected_median_txt.setText(rejected);
					total_workdays_txt.setText(days_work);
					median_workdays_count.setText(count_work);
					//the total days dosent have the same arr size
					
			
			
					//0-active 1-suspend 2-shutdown 3-rejected

					XYChart.Series xyActive = new XYChart.Series();
					XYChart.Series xySuspend = new XYChart.Series();
					XYChart.Series xyShutdown = new XYChart.Series();
					XYChart.Series xyRejected = new XYChart.Series();
					XYChart.Series xyTotalDays = new XYChart.Series();
					xyActive.setName("Active Requests");
					xySuspend.setName("Suspended Requests");
					xyShutdown.setName("Closed Requests");
					xyRejected.setName("Rejected Requests");
					xyTotalDays.setName("Workdays");
					 formatter = DateTimeFormatter.ofPattern("dd-LLLL-yyyy");
	/*****************************************Active***************************************************/	
	
		double standard_deviation_suspend;
		double standard_deviation_active;
		double standard_deviation_shutdown;
		double standard_deviation_rejected;
		double standard_deviation_TotslDsys;
		double standard_deviation_TotslDsys_count;
				arr= status_counter.get(0);
				
    		for(int i=0;i<arr.size();i++) 
    		{
    		
    		String formattedString = date_graph.get(i+1).format(formatter);
    		System.out.println(formattedString);
    	    xyActive.getData().add(new XYChart.Data(formattedString.toString(),arr.get(arr.size()-i-1)));
    		}
   
    		standard_deviation_active=standard_deviation(arr);
    		 active =String.format("%.2f",standard_deviation_active);
    		 sdActive_txt.setText(active);
    /**************************************************Suspend****************************************************/
    		
    		arr= status_counter.get(1);
    		for(int i=0;i<arr.size();i++)
    		{
    	//		sum+=arr.get(i);
    		String formattedString = date_graph.get(i+1).format(formatter);
    		xySuspend.getData().add(new XYChart.Data(formattedString,arr.get(arr.size()-i-1)));
    		}
    
    		 standard_deviation_suspend= standard_deviation(arr);
    		// sdSuspended_txt.setText(Double. toString(standard_deviation_suspend));
    		 suspend =String.format("%.2f",standard_deviation_suspend);
    		 sdSuspended_txt.setText(suspend);
  /***********************************************Shutdown****************************************************/  		
    		arr= status_counter.get(2);
    		
    		for(int i=0;i<arr.size();i++)
    		{
    		//	sum+=arr.get(i);
    		String formattedString = date_graph.get(i+1).format(formatter);
    		xyShutdown.getData().add(new XYChart.Data(formattedString,arr.get(arr.size()-i-1)));
    		}
    
    		 standard_deviation_shutdown= standard_deviation(arr);
    		// sdClosed_txt.setText(Double. toString(standard_deviation_shutdown));
    		 closed =String.format("%.2f",standard_deviation_shutdown);
    		 sdClosed_txt.setText(closed);
   /*****************************************Rejected***************************************************************/
    		arr= status_counter.get(3);
    	
    		for(int i=0;i<arr.size();i++)
    		{
    		//	sum+=arr.get(i);
    		String formattedString = date_graph.get(i+1).format(formatter);
    		xyRejected.getData().add(new XYChart.Data(formattedString,arr.get(arr.size()-i-1)));
    		}
     		

    		standard_deviation_rejected= standard_deviation(arr);
    		// sdRejected_txt.setText(Double. toString(standard_deviation_rejected));
    		rejected =String.format("%.2f",standard_deviation_rejected);
    		sdRejected_txt.setText(rejected);
   /**************************************************TotalDays***************************************************************/
    		arr= status_counter.get(4);
    		ArrayList<Integer>days_workdays= status_counter.get(5);
    		
    		for(int i=0;i<arr.size();i++)
    		{
    		
    		
    		
    		xyTotalDays.getData().add(new XYChart.Data(Integer.toString( days_workdays.get(i)),arr.get(i)));
    		}
    		standard_deviation_TotslDsys= standard_deviation(arr);
    	//	sdWorkdays_txt.setText(Double. toString(standard_deviation_TotslDsys));
    		days_work =String.format("%.2f",standard_deviation_TotslDsys);
    		sdWorkdays_txt.setText(days_work);
    		
    		
   		standard_deviation_TotslDsys_count= standard_deviation(days_workdays);
   		//sdWorkdays_count.setText(Double. toString(standard_deviation_TotslDsys_count));
   		count_work =String.format("%.2f",standard_deviation_TotslDsys_count);
   		sdWorkdays_count.setText(count_work);
    	/*****************************************************************************************************/
    		active_reports.getData().addAll(xyActive,xySuspend,xyShutdown,xyRejected);
    		work_days_report.getData().addAll(xyTotalDays);
    	}
    			
    
 	});
    }

	
}	

    	

	
	
	