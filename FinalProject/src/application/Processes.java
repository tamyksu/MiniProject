package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *Holds the processes that have been retrieved from DB
 */
public class Processes implements Serializable{

	private static final long serialVersionUID = 1L;
	private HashMap<Integer, UserProcess> myprocess = new HashMap<Integer, UserProcess>();
	private ArrayList<UserProcess> myProcessesInArrayList = new ArrayList<UserProcess>(); 	
	
	public HashMap<Integer, UserProcess> getMyProcess() {
		return myprocess;
	}

	public void setMyProcess(HashMap<Integer, UserProcess> myProcess) {
		this.myprocess = myProcess;
	}

	public ArrayList<UserProcess> getMyProcessesInArrayList() {
		return myProcessesInArrayList;
	}

	public void setMyProcessesInArrayList(ArrayList<UserProcess> myProcessesInArrayList) {
		this.myProcessesInArrayList = myProcessesInArrayList;
	}
	
}
