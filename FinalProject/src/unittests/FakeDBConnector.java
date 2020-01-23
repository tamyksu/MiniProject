package unittests;

import java.util.ArrayList;

import server.IDBConnector;
import translator.OptionsOfAction;
import translator.Translator;

/**
 * This is a mock class for DBConnector
 */
public class FakeDBConnector implements IDBConnector{

	@Override
	public Object accessToDB(Object data) {
		Translator translator = (Translator) data;
		ArrayList<Boolean> answer = new ArrayList<>();

		
		switch (translator.getRequest()) {
		case SaveReportToServer:
		{
			if(translator.getParmas() == null)
				answer.add(false);
			else	
				answer.add(true);
			return new Translator(OptionsOfAction.SaveReportToServer,answer);
		}
		default:
		{
			answer.add(false);
			return new Translator(OptionsOfAction.SaveReportToServer,answer);

		}
		}
	}

	@Override
	public void establishDBConnection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendNotification(int procID, String content, int days, String toWho, String fromWho, String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteNotification(int procID, String content, int days, String toWho, String fromWho) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<ArrayList<?>> getActiveProcesses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHandlerId(int requestID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHandlerRole(int requestID) {
		// TODO Auto-generated method stub
		return null;
	}

}
