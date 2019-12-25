package client;
import ocsf.client.*;
import translator.OptionsOfAction;
import translator.Translator;
import java.io.*;
import java.util.ArrayList;
import application.ControllerProcessMain;
import application.LoginController;
import application.Processes;
import application.ScreenController;
import application.UserProcess;

public class Client extends AbstractClient {
	private String name;
	private Processes processes = new Processes();
	public static Client instance;
	
	public Client(String host, int port) throws IOException {
		super(host, port); // Call the superclass constructor
		openConnection();
		instance = this;
	}

	public static Client getInstance() {
		return instance;
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object rs) {
		Translator result = (Translator)rs; 
		switch (result.getRequest()) {
		case LOGIN:
			handlerMessageFromServerLogin(result.getParmas());
			break;
		case GETRELATEDREQUESTS:
			handlerMessageFromServerProcesses(result.getParmas());
			break;
		default:
			break;
		}
	
	}

	private void setName(String name) {
		this.name = name;
	}

	public void handleMessageFromClientGUI(Object message) {
		try {
			sendToServer(message);
		} catch (IOException e) {
			System.out.println("Could not send massage to server");
			quit();
		}
	}

	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}


	public String getName() {
		return name;
	}

	public Processes getProcesses() {
		return processes;
	}

	public void setProcesses(Processes processes) {
		this.processes = processes;
	}
	
	public void getProcessesFromServer() {
		ArrayList<String> ar = new ArrayList<String>();
		ar.add(name);
		Translator translator = new Translator(OptionsOfAction.GETRELATEDREQUESTS, ar);
		handleMessageFromClientGUI(translator);
	}

	public void handlerMessageFromServerLogin(Object rs) {
		@SuppressWarnings("unchecked")
		ArrayList<String> result = (ArrayList<String>) rs;
		switch (result.get(0)) {
		case "correct match":
			Client.getInstance().setName(result.get(1));
			ScreenController.getScreenController().activate("processesMain");
			getProcessesFromServer();
			break;
		case "Login failed, username and password did not match":
			LoginController.getInstance().getMessageField()
					.setText("Login failed, username and password did not match");
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public void handlerMessageFromServerProcesses(Object rs) {
		
    	ArrayList<ArrayList<?>> result = new ArrayList<ArrayList<?>>();
		result = (ArrayList<ArrayList<?>>) rs ;
		Processes processes = new Processes();
		for (int i = 0; i < result.size(); i=i+2) {
			UserProcess process = new UserProcess();
			process.setRequest_id((int)result.get(i).get(0));
			process.setRole((String)result.get(i+1).get(0));
			process.setIntiatorId((String)result.get(i+1).get(1));
			process.setSystem_num((int)result.get(i).get(1));
			process.setProblem_description((String)result.get(i+1).get(2));
			process.setRequest_description((String)result.get(i+1).get(3));
			process.setExplanaton((String)result.get(i+1).get(4));
			process.setNotes((String)result.get(i+1).get(5));
			process.setStatus((String)result.get(i+1).get(6));
			process.setCreation_date((String)result.get(i+1).get(7));
			process.setHandler_id((String)result.get(i+1).get(8));
			process.setProcess_stage((String)result.get(i+1).get(9));
			process.setCurrent_stage_due_date((String)result.get(i+1).get(10));
			process.setInitiatorFirstName((String)result.get(i+1).get(11));
			process.setInitiatorLastName((String)result.get(i+1).get(12));
			process.setEmail((String)result.get(i+1).get(13));
			process.setDepartment((String)result.get(i+1).get(14));
			processes.getMyProcess().put(new Integer((int)result.get(i).get(0)), process);
			processes.getMyProcessesInArrayList().add(process);	
		}
		this.processes=processes;
		//sending all processes related to Controllers in order to set them in their table
		ControllerProcessMain.getInstance().SetInTable(processes );
	}

	
}
