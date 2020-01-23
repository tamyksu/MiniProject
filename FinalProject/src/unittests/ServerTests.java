package unittests;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import server.IDBConnector;
import server.Server;

public class ServerTests {

	static Server server;
	static IDBConnector fakeDbConnector;

	/**
	 * Initialization of the fake DB connector and injecting it to the server
	 * To be used in the following tests
	 */
	@BeforeClass
	public static void init() {
		fakeDbConnector = new FakeDBConnector();
		server = new Server(25565, fakeDbConnector);
	}
	
	/**
	 * Tests the function that gets reports of specific date from server with normal input
	 */
	@Test
	public void GetReportsFromServerNormalInput()
	{
		ArrayList<ArrayList<Double>> actual = server.getReportsFromServer(LocalDate.of(2020, 03, 01));
		
		assertFalse(actual == null);
		assertFalse(actual.isEmpty());
		
	}
	
	/**
	 * Tests the function that gets reports of specific date from server with null date
	 */
	@Test
	public void GetReportsFromServerNullDate()
	{
		ArrayList<ArrayList<Double>> actual = server.getReportsFromServer(null);
		assertEquals(null, actual);
	}
	
	/**
	 * Tests the function that saves reports to server with null input
	 */
	@Test
	public void SaveReportsToServerNullInput()
	{
		assertFalse(server.saveReportsToServer(null));
	}
	
	/**
	 * Tests the function that saves reports to server with normal input
	 */
	@Test
	public void SaveReportsToServerNormalnput()
	{
		ArrayList<ArrayList<Double>> data = new ArrayList<>();
		ArrayList<Double> arr  = new  ArrayList<>();
		
		arr.add(2.6);
		arr.add(2.9);
		arr.add(0.3);
		arr.add(1.4);
		
		data.add(arr);
		data.add(arr);
		data.add(arr);
		data.add(arr);
		
		assertTrue(server.saveReportsToServer(data));
	}

}
