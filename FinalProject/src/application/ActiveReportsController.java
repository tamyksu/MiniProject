package application;

import client.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.lang.Runnable;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import translator.OptionsOfAction;
import translator.Translator;
public class ActiveReportsController implements Initializable{ 
	Client client = Client.getInstance();
	  ArrayList<LocalDate> date_graph;
		private static Connection conn;
		
    @FXML
    private DatePicker end_date_button;

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
    	start_date_button.setValue(null);
    	end_date_button.setValue(null);
    	num_days.clear();
   	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
   	
	}
    @FXML
    void done_button(ActionEvent event) 
    {
    	active_reports.getData().clear();
    	 ArrayList<Object> all= new ArrayList<>();
    	 ArrayList<LocalDate> dates= new ArrayList<LocalDate>();
    	LocalDate start_date=start_date_button.getValue();//get start date
    	 end_date=end_date_button.getValue();//get end date
    	  System.out.println(" LocalDate start"+start_date );
    	   //num_days.getText();
    	 ArrayList<Long> days= new ArrayList<Long>();
    	  num = Long.parseLong(num_days.getText());
    	
    	 days.add(num);
    	   start_index=start_date;
			 end_index=start_index.plusDays(num);
    	 // Integer total_days=(Integer)(end_date- start_date);
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

 		Translator translator= new Translator(OptionsOfAction.Get_Active_Statistic,all);
 		Client.getInstance().handleMessageFromClientGUI(translator);
						
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void calaulate(ArrayList<ArrayList<Integer>> status_counter)
    {
    	
    	active_reports.getData().clear();
    	
    	System.out.println("calculate");
    
    //	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LLLL-yyyy");
    	//System.out.println(arr.size());
    
    
    	Platform.runLater(new Runnable(){

				@Override
				public void run() {
				
				ArrayList<Integer> arr= status_counter.get(0);
				ArrayList<Integer> median=new ArrayList<Integer>();
					System.out.println(arr.size()+"size ?7");
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-LLLL-yyyy");
				/*	for(int i=0;i<arr.size();i++) 
						{
						String formattedString = date_graph.get(i).format(formatter);
						System.out.println(formattedString); 
						}
/**************************************************************median		*/		
					
					for(int i=0;i<4;i++)	
					{
						 arr= status_counter.get(i);
							if(arr.size()%2==1)
							{
								
								median.add(arr.get(arr.size()/2));
								System.out.println(median.get(i));
							}
							else
							{
								
								median.add(arr.get((arr.size()/2)+arr.get(arr.size()/2-1)/2));
								System.out.println(median.get(i));
							}
							
							
						}
					//the total days dosent have the same arr size
					arr= status_counter.get(4);
					System.out.println("*******************");
				for(int i=0;i<arr.size();i++)
				{
					System.out.println(arr.get(i));
				}
				
						if(arr.size()%2==1)
						{
							
							median.add(arr.get(arr.size()/2));
							System.out.println((arr.size()/2)+"?");
							System.out.println(median.get(4));
						}
						else
						{
							
							median.add((arr.get(arr.size()/2)+arr.get(arr.size()/2-1))/2);
							//System.out.println((arr.get(arr.size()/2-1)+"?ff?6\2=3-1=2/2=1--1");
							System.out.println("****");
							System.out.println(median.get(4));
						}
						
						
			
					
		/*********************************************************************/   	
			
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
		int sum=0;
		double standard_deviation_suspend;
		double standard_deviation_active;
		double standard_deviation_shutdown;
		double standard_deviation_rejected;
		double standard_deviation_TotslDsys;
				arr= status_counter.get(0);
				
    		for(int i=0;i<arr.size();i++) 
    		{
    			
    			sum+=arr.get(i);
    		String formattedString = date_graph.get(i).format(formatter);
    		System.out.println(formattedString);
    	    xyActive.getData().add(new XYChart.Data(formattedString.toString(),arr.get(arr.size()-i-1)));
    		}
    		double active_avg=sum/arr.size();
    		System.out.println("active average"+active_avg);
    		sum=0;
    		for(int i=0;i<arr.size();i++) 
    		{
    			
    			sum+=Math.pow(arr.get(i)-active_avg,2);
    			System.out.println("active sum what in root"+sum);
    		}
    		 standard_deviation_active=(Math.sqrt(sum/arr.size()));
    /**************************************************Suspend****************************************************/
    		sum=0;
    		arr= status_counter.get(1);
    		for(int i=0;i<arr.size();i++)
    		{
    			sum+=arr.get(i);
    		String formattedString = date_graph.get(i).format(formatter);
    		xySuspend.getData().add(new XYChart.Data(formattedString,arr.get(arr.size()-i-1)));
    		}
    		double suspend_avg=sum/arr.size();
    		System.out.println("suspend average"+suspend_avg);
    		sum=0;
    		for(int i=0;i<arr.size();i++) 
    		{
    			
    			sum+=Math.pow(arr.get(i)-suspend_avg,2);
    			System.out.println("suspend sum what in root"+sum);
    		}
    		 standard_deviation_suspend=(Math.sqrt(sum/arr.size()));
  /***********************************************Shutdown****************************************************/  		
    		arr= status_counter.get(2);
    		sum=0;
    		for(int i=0;i<arr.size();i++)
    		{
    			sum+=arr.get(i);
    		String formattedString = date_graph.get(i).format(formatter);
    		xyShutdown.getData().add(new XYChart.Data(formattedString,arr.get(arr.size()-i-1)));
    		}
    		
    		double shutdown_avg=sum/arr.size();
    		sum=0;
    		for(int i=0;i<arr.size();i++) 
    		{
    			
    			sum+=Math.pow(arr.get(i)-shutdown_avg,2);
    		}
    		 standard_deviation_shutdown=(Math.sqrt(sum/arr.size()));
   /*****************************************Rejected***************************************************************/
    		arr= status_counter.get(3);
    		sum=0;
    		for(int i=0;i<arr.size();i++)
    		{
    			sum+=arr.get(i);
    		String formattedString = date_graph.get(i).format(formatter);
    		xyRejected.getData().add(new XYChart.Data(formattedString,arr.get(arr.size()-i-1)));
    		}
     		
    		double rejected_avg=sum/arr.size();
    		sum=0;
    		for(int i=0;i<arr.size();i++) 
    		{
    			
    			sum+=Math.pow(arr.get(i)-rejected_avg,2);
    		}
    		 standard_deviation_rejected=(Math.sqrt(sum/arr.size()));
   /**************************************************TotalDays***************************************************************/
    		arr= status_counter.get(4);
    		sum=0;
    		for(int i=0;i<arr.size();i++)
    		{
    			sum+=arr.get(i);
    		String formattedString = date_graph.get(i).format(formatter);
    		xyTotalDays.getData().add(new XYChart.Data(formattedString,arr.get(arr.size()-i-1)));
    		}
    		double TotslDsysd_avg=sum/arr.size();
    		System.out.println(TotslDsysd_avg+"TotslDsysd_avg");
    		System.out.println("suspend TotslDsysd_avg"+TotslDsysd_avg);
    		sum=0;
    		for(int i=0;i<arr.size();i++) 
    		{
    			
    			sum+=Math.pow(arr.get(i)-TotslDsysd_avg,2);
    			System.out.println("total days sum what in root"+sum);

    		}
    		 standard_deviation_TotslDsys=(Math.sqrt(sum/arr.size()));
    		 System.out.println("*****");
	
			System.out.println(standard_deviation_active+standard_deviation_rejected+standard_deviation_shutdown+standard_deviation_suspend);
		System.out.println(standard_deviation_TotslDsys);
    	/*****************************************************************************************************/
    		active_reports.getData().addAll(xyActive,xySuspend,xyShutdown,xyRejected,xyTotalDays);
    	}
    			
    
 	});
    }

	
}	

    	

	
	
	