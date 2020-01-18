package application;

import client.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import translator.OptionsOfAction;
import translator.Translator;
import java.lang.Runnable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
public class DelayReportsController extends StatisticReports implements Initializable{ 
	
	public static DelayReportsController instance;

    @FXML
    private Button back_btn;

    @FXML
    private CategoryAxis x;

    @FXML
    private BarChart<?, ?> delay_reports;

    @FXML
    private NumberAxis y;
    @FXML
    private Text median_dayDelay;

    @FXML
    private Text sd_number_request_delay;

    @FXML
    private Text sd_dayDelay;

    @FXML
    private Text median_number_requestDelay;

    
    @FXML
    void back_click(ActionEvent event) {
    	delay_reports.getData().clear();
      	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		instance = this;	
	}
	public void get_information()
	{
		ArrayList<Integer> params = new ArrayList<>();
		Translator translator = new Translator(OptionsOfAction.SelectDelayReport,params);
		Client.getInstance().handleMessageFromClientGUI(translator);
	}
	
	
	
	
	  @SuppressWarnings({ "rawtypes", "unchecked" })
	public void calculate(ArrayList<ArrayList<Integer>> data_delay) {
	    	Platform.runLater(new Runnable(){

					@Override
					public void run() {
					
		ArrayList<Integer> time_delay= data_delay.get(0);
		ArrayList<Integer> count_request_delay= data_delay.get(1);
		
		Double median_number_dayDelay;
		Double standard_devision_dayDelay;
		Double median_number_request_Delay;
		Double standard_devisiom_number_request_Delay;
		
		median_number_dayDelay=medianALL(time_delay);
		median_number_request_Delay=medianALL(count_request_delay);
		standard_devision_dayDelay=standard_deviation(time_delay);
		standard_devisiom_number_request_Delay=standard_deviation(count_request_delay);
		
		String day_Delay =String.format("%.2f", median_number_dayDelay);
		String number_request_Delay =String.format("%.2f", median_number_request_Delay);
		String sd_day_Delay =String.format("%.2f",standard_devision_dayDelay);
		String sd_number_request =String.format("%.2f", standard_devisiom_number_request_Delay);
		
		
		median_dayDelay.setText(day_Delay);
		median_number_requestDelay.setText(number_request_Delay);
		sd_dayDelay.setText(sd_day_Delay);
		sd_number_request_delay.setText(sd_number_request);
		
		XYChart.Series xyNumberRequestDelay = new XYChart.Series();
		xyNumberRequestDelay.setName("Delay Requests");
		
		for(int i=0;i<time_delay.size();i++) 
		{
			
		
			xyNumberRequestDelay.getData().add(new XYChart.Data(Integer.toString( count_request_delay.get(i)),time_delay.get(i)));
			
		}
		delay_reports.getData().addAll(xyNumberRequestDelay);
	}
					});
	    	}
}
