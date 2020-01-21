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
import application.StatisticReports;

//@RunWith(Suite.class)
//@SuiteClasses({})
public class ClientTests {
	
	StatisticReports statisticReport;
	ArrayList<Integer> status_counter;
	ArrayList<ArrayList<Integer>> checkData;
	@Before
	public void init() {
		 statisticReport = new StatisticReports();
	
		status_counter = new ArrayList<>();
		
	}
	
	/**
	 * calling calculate method with a null input, expected NullPointerException to be thrown
	 */
	@Test(expected=NullPointerException.class)
	public void testNullInput() {	
		statisticReport.medianALL(null);		
	}
	
	@Test
	public void testEmptyInput() {
	
		statisticReport.medianALL(status_counter);

	}
	
	@Test
	public void testDateIsNull() {
	
		statisticReport.medianALL(status_counter);

	}
	
	@Test
	public void testDateIsWrong() {
	
		statisticReport.medianALL(status_counter);
		
		

	}
	
	@Test
	public void testMediansZero() {
		String expected = "0";
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		Double result=statisticReport.medianALL(status_counter);
		
		assertEquals(expected,result);

	}
	
	@Test
	public void testStandartDevisionZero() {
		String expected = "0";
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		status_counter.add(0);
		Double result=statisticReport.standard_deviation(status_counter);
		assertEquals(expected,result);

	}
	/**
	 * Check the calculate function when days input  is null
	 */
	@Test(expected=NullPointerException.class)
	public void testMedianNull() {
		
		statisticReport.medianALL(status_counter);
	}
	@Test(expected=NullPointerException.class)
	public void testStandartDevisonNull() {
		
	
		statisticReport.standard_deviation(status_counter);
	}

	@Test
	public void testMedianInput() {
		String expected = "3";
		status_counter.add(1);
		status_counter.add(2);
		status_counter.add(3);
		status_counter.add(4);
		status_counter.add(5);
		Double result=statisticReport.medianALL(status_counter);
		
		assertEquals(expected,result);
	}

	public void testStandartDevisonInput() {
	
		String expected = "3";
		status_counter.add(1);
		status_counter.add(2);
		status_counter.add(3);
		status_counter.add(4);
		status_counter.add(5);
		Double result=statisticReport.standard_deviation(status_counter);
		
		assertEquals(expected,result);
	}
	


}
