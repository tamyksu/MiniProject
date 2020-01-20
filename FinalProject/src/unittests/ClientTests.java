package unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import application.ActiveReportsController;

//@RunWith(Suite.class)
//@SuiteClasses({})
public class ClientTests {
	
	ActiveReportsController activeReports;
	ArrayList<ArrayList<Integer>> status_counter;
	
	@Before
	public void init() {
		activeReports = new ActiveReportsController();
		activeReports.initialize();
		status_counter = new ArrayList<>();
	}
	
	/**
	 * calling calculate method with a null input, expected NullPointerException to be thrown
	 */
	@Test(expected=NullPointerException.class)
	public void testNullInput() {	
			activeReports.calaulate(null);		
	}
	
	@Test
	public void testEmptyInput() {
	
		activeReports.calaulate(status_counter);

	}
	
	@Test
	public void testDateIsNull() {
	
		activeReports.calaulate(status_counter);

	}
	
	@Test
	public void testDateIsWrong() {
	
		activeReports.calaulate(status_counter);

	}
	
	@Test
	public void testNumOfDaysZero() {
		activeReports.calaulate(status_counter);
	}
	
	/**
	 * Check the calculate function when days input  is null
	 */
	@Test(expected=NullPointerException.class)
	public void testNumOfDaysNull() {
			activeReports.calaulate(status_counter);
	}

	@Test
	public void testNormalInput() {
		//TODO: ADD regular data to status_counter before calling the calculate 
		//status_counter.add(...);
		
		//TODO: make here the calculation of the expected value of "activeReports.sdActive_txt" label:
		String expected = "0";
		
		activeReports.calaulate(status_counter);
		
		assertEquals(expected,activeReports.sdActive_txt.getText());
	}
}
