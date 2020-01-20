package unittests;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import server.DBConnector;
import server.Server;
import translator.OptionsOfAction;
import translator.Translator;

public class ServerTests {
	
	@Before
	public void init()
	{
		Server.main(new String[2]);
	}
	
	@Test
	public void getActiveStatisticTest() {

   	 	ArrayList<Object> input= new ArrayList<>();
   	 	ArrayList<LocalDate> dates= new ArrayList<LocalDate>();
   	 	ArrayList<Long> days = new ArrayList<Long>();
   	 	
   	 	dates.add(LocalDate.of(2020, 02, 01));
   	 	dates.add(LocalDate.of(2020, 02, 30));
   	 	days.add((long) 10);
   	 	
   	 	input.add(dates);
   	 	input.add(days);
   	 	
		Translator translator= new Translator(OptionsOfAction.Get_Active_Statistic,input);

    	Object rs = DBConnector.accessToDB(translator);
    	if(rs != null)	
    		System.out.println(rs);

	}

}
