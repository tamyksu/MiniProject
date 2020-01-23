package unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import client.Client;
import application.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.IDBConnector;
import server.Server;
import translator.OptionsOfAction;
import translator.Translator;

//@RunWith(Suite.class)
//@SuiteClasses({})
public class ClientTests extends Application{
	StatisticReports statisticReport;
	ArrayList<Integer> status_counter;
	ArrayList<ArrayList<Integer>> checkData;
	
	static IDBConnector fakeDbConnector;
	static Server server;
	
	/**
	 * Initializes a server and a client for all of the tests
	 * @throws InterruptedException
	 */
	@BeforeClass
	public static void setUpClass() throws InterruptedException {
	    // Initialise Java FX

		fakeDbConnector = new FakeDBConnector();
		server = new Server(25565, fakeDbConnector);
		Server.main(new String[2]);		
		
		try {
			new Client("localhost", 25565);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	/**
	 *Initializes the required attributes for each test
	 */
	@Before
	public void init() {
		
		statisticReport = new StatisticReports();
		status_counter = new ArrayList<>();
		
	}
	
	/**
	 * Test calculate function in the controller with normal input
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
	
	/**
	 * Tests the median calculation when input is array of zeroes.
	 */
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

	/**
	 * Tests the median calculation when the input is an empty array.
	 */
	@Test
	public void testEmptyInputMedian() {
	
		double actual = statisticReport.medianALL(status_counter);
		assertEquals(0.0, actual,0);
	}
	
	/**
	 * Tests of median calculation with a normal input
	 */
	@Test
	public void testMedianNormalInput() {
		double expected = 3.0;
		status_counter.add(1);
		status_counter.add(2);
		status_counter.add(3);
		status_counter.add(4);
		status_counter.add(5);
		double result=statisticReport.medianALL(status_counter);
		
		assertEquals(expected,result,0);
	}

	/**
	 * Tests the standard deviation calculation when the input is null
	 */
	@Test(expected=NullPointerException.class)
	public void testStandardDeviationNull() {
		statisticReport.standard_deviation(null);
	}
	
	/**
	 * Tests the standard deviation when the input is an empty array.
	 */
	@Test
	public void testStandardDeviationEmpty() {
		statisticReport.standard_deviation(status_counter);
	}
	
	/**
	 * Tests the standardDeviation calculation with a normal input
	 */
	@Test
	public void testStandartDevisonNormalInput() {
	
		double expected = 1.41;
		status_counter.add(1);
		status_counter.add(2);
		status_counter.add(3);
		status_counter.add(4);
		status_counter.add(5);
		Double result=statisticReport.standard_deviation(status_counter);
		
		assertEquals(expected,result,0.2);
	}
	
	/**
	 * Tests the saveReportToServer function when recieves a null input, and expects a negative response from server.
	 */
	@Test
	public void saveReportToServerTestNullInput()
	{    	
    	assertFalse(ActiveReportsController.instance.saveReportToServer(null));
	}
	
	/**
	 * Test the saveReportToServer function is sending a report to the server and expect to get a positive response from server.
	 */
	@Test
	public void saveReportToServerTestNormalInput()
	{
		
		ArrayList<ArrayList<Double>> data = new ArrayList<>();
		
		ArrayList<Double> array = new ArrayList<>();
		
		array.add(0.2);
		array.add(0.7);
		array.add(2.7);
		array.add(2.4);
		array.add(3.1);
		
		data.add(array);
		data.add(array);
		data.add(array);
		data.add(array);
		
		boolean result = ActiveReportsController.instance.saveReportToServer(data);
    	assertTrue(result);
	}

	/**
	 * This function is called after launch of the main screen is finished.
	 * We are initializing and showing only one window - active reports because its the only window under test
	 */
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
