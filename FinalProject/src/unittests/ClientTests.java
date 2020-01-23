package unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import application.ActiveReportsController;
import application.ScreenController;
import application.StatisticReports;
import application.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//@RunWith(Suite.class)
//@SuiteClasses({})
public class ClientTests extends Application{
	StatisticReports statisticReport;
	ArrayList<Integer> status_counter;
	ArrayList<ArrayList<Integer>> checkData;
	
	@BeforeClass
	public static void setUpClass() throws InterruptedException {
	    // Initialise Java FX

	    System.out.printf("About to launch FX App\n");

	    new Thread() {
	    	public void run()
	    	{
	    	    Application.launch(ClientTests.class,new String[0]);
	    	}
	    }.start();

	    System.out.printf("FX App thread started\n");
	    Thread.sleep(4000);
	}
	
	@Before
	public void init() {
		
		statisticReport = new StatisticReports();
		status_counter = new ArrayList<>();
		
	}
	
	
	/**
	 * Test calculate function with normal input
	 */
	@Test
	public void testCalculateNormal()
	{
		status_counter.add(1);
		status_counter.add(2);
		status_counter.add(3);
		status_counter.add(4);
		status_counter.add(5);
		
		
		ActiveReportsController.instance.updateDateGraph(LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 30),(long)7);
		
		ArrayList<ArrayList<Integer>> inputArray = new ArrayList<>();
		
		inputArray.add(status_counter);
		inputArray.add(status_counter);
		inputArray.add(status_counter);
		inputArray.add(status_counter);

		ActiveReportsController.instance.calaulate(inputArray);
		
		String expectedStandardDeviation = "1.41";
		String actualStandardDeviation = ActiveReportsController.instance.sdActive_txt.getText();
		
		String expectedMedian = "3.00";
		String actualMedian = ActiveReportsController.instance.active_median_txt.getText();
		
		assertEquals(expectedStandardDeviation, actualStandardDeviation);
		assertEquals(expectedMedian, actualMedian);

	}
	
	/**
	 * calling calculate method with a null input, expected NullPointerException to be thrown
	 */
	@Test(expected=NullPointerException.class)
	public void testNullInputMedian() {	
		statisticReport.medianALL(null);		
	}
	
	@Test
	public void testEmptyInputMedian() {
	
		double actual = statisticReport.medianALL(status_counter);
		assertEquals(0.0, actual,0);
	}
	
	@Test(expected=NullPointerException.class)
	public void testStandardDeviationNull() {
		statisticReport.standard_deviation(null);
	}
	
	@Test
	public void testStandardDeviationEmpty() {
		statisticReport.standard_deviation(status_counter);
	}
	
	@Test
	public void testMediansZero() {
		double expected = 0.0;
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		Double result=statisticReport.medianALL(status_counter);
		
		assertEquals(expected,result,0);

	}
	
	@Test
	public void testStandartDevisionZero() {
		double expected = 0.0;
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		Double result=statisticReport.standard_deviation(status_counter);
		assertEquals(expected,result,0);

	}
	/**
	 * Check the calculate function when days input  is null
	 */
	@Test(expected=NullPointerException.class)
	public void testMedianNull() {
		statisticReport.medianALL(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testStandartDevisonNull() {
		
		statisticReport.standard_deviation(null);
	}

	@Test
	public void testMedianInput() {
		double expected = 3.0;
		status_counter.add(1);
		status_counter.add(2);
		status_counter.add(3);
		status_counter.add(4);
		status_counter.add(5);
		double result=statisticReport.medianALL(status_counter);
		
		assertEquals(expected,result,0);
	}

	public void testStandartDevisonInput() {
	
		double expected = 1.41;
		status_counter.add(1);
		status_counter.add(2);
		status_counter.add(3);
		status_counter.add(4);
		status_counter.add(5);
		Double result=statisticReport.standard_deviation(status_counter);
		
		assertEquals(expected,result,0);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
		
		Parent root = FXMLLoader.load(getClass().getResource("../application/basePanel.fxml"));
		Scene baseScene = new Scene(root, 1500,900);
		baseScene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
		primaryStage.setScene(baseScene);
		new ScreenController(baseScene);

		ScreenController.getScreenController().addScreen("active_reports", FXMLLoader.load(getClass().getResource("../application/ActiveReports.fxml")));		
		ScreenController.getScreenController().activate("active_reports");
		
		primaryStage.show();
		
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
	}
	


}
