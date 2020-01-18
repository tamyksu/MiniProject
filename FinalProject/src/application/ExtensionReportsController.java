package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import translator.OptionsOfAction;
import translator.Translator;
import javafx.fxml.Initializable;
import java.lang.Runnable;





public class ExtensionReportsController extends StatisticReports implements Initializable{ 
	Client client = Client.getInstance();
	public static ExtensionReportsController instance;
    @FXML
    private Text median_dayExtension;

    @FXML
    private Text median_number_requestExtension;

    @FXML
    private Text sd_dayExtension;

    @FXML
    private Text sd_number_request_extension;

    @FXML
    private CategoryAxis x;

    @FXML
    private BarChart<?, ?> extension_reports_display;

    @FXML
    private NumberAxis y;

    @FXML
    private Button back_btn;

    

    @FXML
    void back_click(ActionEvent event) {
    	extension_reports_display.getData().clear();
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
		Translator translator = new Translator(OptionsOfAction.SelectExtensionReport,params);
		Client.getInstance().handleMessageFromClientGUI(translator);
	}
	
	
	
	  @SuppressWarnings({ "rawtypes", "unchecked" })
	public void calculate(ArrayList<ArrayList<Integer>> data_delay) {
	    	Platform.runLater(new Runnable(){

					@Override
					public void run() {
					
		ArrayList<Integer> time_extension= data_delay.get(0);
		ArrayList<Integer> count_request_extension= data_delay.get(1);
		
		Double median_number_dayExtension;
		Double standard_devision_dayExtension;
		Double median_number_request_Extension;
		Double standard_devisiom_number_request_Extension;
		
		
		median_number_dayExtension=medianALL(time_extension);
		median_number_request_Extension=medianALL(count_request_extension);
		standard_devision_dayExtension=standard_deviation(time_extension);
		standard_devisiom_number_request_Extension=standard_deviation(count_request_extension);
		
		String day_Extension =String.format("%.2f", median_number_dayExtension);
		String number_request_Extension =String.format("%.2f", median_number_request_Extension);
		String sd_day_Extension =String.format("%.2f",standard_devision_dayExtension);
		String sd_number_request =String.format("%.2f", standard_devisiom_number_request_Extension);
		
		median_dayExtension.setText(day_Extension);
		median_number_requestExtension.setText(number_request_Extension);
		sd_dayExtension.setText(sd_day_Extension);
		sd_number_request_extension.setText(sd_number_request);
		
		XYChart.Series xyRequestExtension = new XYChart.Series();
		
		xyRequestExtension.setName("Extension  Requests");
	
		for(int i=0;i<time_extension.size();i++) 
		{
			
		System.out.println(count_request_extension.get(i)+"  "+time_extension.get(i));
			xyRequestExtension.getData().add(new XYChart.Data(Integer.toString( count_request_extension.get(i)),time_extension.get(i)));
			
		}
		extension_reports_display.getData().addAll(xyRequestExtension);
	}

	  });
}
}



