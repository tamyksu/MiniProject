package application;

import java.util.HashMap;

public class Processes {
	private HashMap<Integer, UserProcess> myProcess;

	public HashMap<Integer, UserProcess> getMyProcess() {
		return myProcess;
	}

	public void setMyProcess(HashMap<Integer, UserProcess> myProcess) {
		this.myProcess = myProcess;
	}
}
