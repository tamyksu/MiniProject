package server;

import java.sql.ResultSet;
import java.util.ArrayList;

public interface IDBConnector {

	Object accessToDB(Object data);
	
	void establishDBConnection();
	
	public  void sendNotification(int procID, String content, int days, String toWho, String fromWho, String reason);
	
	public  void deleteNotification(int procID, String content, int days, String toWho, String fromWho);
	
	public  ArrayList<ArrayList<?>> getActiveProcesses();
	
	public  int getHandlerId(int requestID);
	
	public  String getHandlerRole(int requestID);
	
	
	
}
