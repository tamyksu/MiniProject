package application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import translator.OptionsOfAction;
import translator.Translator;

public class NewRequestController implements Initializable {

	private static NewRequestController instance;
	
    @FXML
    private Button sendRequestBtn;

    @FXML
    private TextArea ProblemDescription;

    @FXML
    private TextArea notes;

    @FXML
    private TextField informationSystemNumber;

    @FXML
    private TextArea Documents;

    @FXML
    private TextArea explanation;

    @FXML
    private TextArea requestDescription;

    @FXML
    private Button uploadFilesBtn;
    
    @FXML
    private ListView filesList;
    
    private List listOfFilesNames;
    @FXML
    private Button deleteAllBtn;
    
    @FXML
    private Button backBtn;
    
    @FXML
    private ComboBox<String> systemsCombobox;
    private ArrayList<InformationSystem> listForComboBox;
    
    /**
     * Delete all the fields in the form
     * @param event
     */
    @FXML
    void deleteAll(ActionEvent event) {
    	deleteAll();
    }
    
    public void deleteAll() {

		ProblemDescription.clear();
		requestDescription.clear();
		explanation.clear();
		notes.clear();
		filesList.getItems().clear();
    }
    
    private boolean answerFromServer;
    
    /**
     * Upload files from the user's computer
     * @param event
     */
    @FXML
    void uploadFiles(ActionEvent event) {
    	listOfFilesNames = new ArrayList<String>(); // List of the names of the selected files
    	FileChooser fc = new FileChooser(); // File chooser
    	List<File> selectedFiles = fc.showOpenMultipleDialog(null); // List if the selected files
    	
    	if(selectedFiles != null) {
    		for(int i = 0; i<selectedFiles.size();i++) {
    			filesList.getItems().add(selectedFiles.get(i).getAbsolutePath());
    			listOfFilesNames.add(selectedFiles.get(i).getName());
    		}
    	}
    	else {
    		new Alert(AlertType.ERROR, "Files are not valid!").show();
    	}
    	
    }
    
    /**
     * Back to the previous page
     * @param event
     */
    @FXML
    void backClick(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }

    
    /**
     * Send a new Change request to the Data Base (through the server)
     * @param event
     */
    @FXML
    void sendRequest(ActionEvent event) {
		if(formCheck()) {
			//instance=this;
    		//int sysNum = Integer.parseInt(informationSystemNumber.getText().toString());
			
			String sysName = systemsCombobox.getSelectionModel().getSelectedItem().toString();
			int sysNum = getInfoSysNumber(sysName);
			String probDesc = ProblemDescription.getText().toString();
    		String reqDesc = requestDescription.getText().toString();
    		String expla = explanation.getText().toString();
    		String notes1 = notes.getText().toString();

    		Request nr = new Request(Client.getInstance().getUserID(),sysNum, probDesc, reqDesc, expla, notes1);
    		
    		ArrayList<Object> params = new ArrayList<Object>();
    		params.add(nr);

    		
    		// **************************************************************** Send Files
    		int numberOfFiles = filesList.getItems().size();
    		
        	//System.out.println(numberOfFiles);
        	ArrayList<MyFile> filesToServer = new ArrayList<MyFile>();
        	for(int i=0;i<numberOfFiles;i++) {
        		MyFile msg= new MyFile(Client.getInstance().getUserID()+"_"+listOfFilesNames.get(i));
        		  String LocalfilePath=filesList.getItems().get(i).toString();
        		  try{

        			      File newFile = new File (LocalfilePath);      
        			      byte [] mybytearray  = new byte [(int)newFile.length()];
        			      FileInputStream fis = new FileInputStream(newFile);
        			      BufferedInputStream bis = new BufferedInputStream(fis);			  
        			      
        			      msg.initArray(mybytearray.length);
        			      msg.setSize(mybytearray.length);
        			      
        			      bis.read(msg.getMybytearray(),0,mybytearray.length);
        			      //filesToServer.add(msg);	
        			      filesToServer.add(msg);
        			    }
        			catch (Exception e) {
        				System.out.println("Error: Can't send files to Server");
        			}
        	}
    		
    		params.add(filesToServer);
    		
    		
    		// **************************************************************** End of Send Files
    		
    		Translator translator = new Translator(OptionsOfAction.NEWREQUEST, params);
    		Client.getInstance().handleMessageFromClientGUI(translator);
    		
    		try { Thread.sleep(1000); } catch (InterruptedException e) {System.out.println("Can't Sleep");}
    		
    		if(answerFromServer==true) {
    			showSeccessAlert();
    		}
    		else {
    			showFailureAlert();
    		}
    		
    		
    	}
    }
    
    /**
     * Form validation
     * @return  true/false
     */
    public boolean formCheck() {
    	// if any of the TextAreas/Textfields is empty:
    	if(ProblemDescription.getText().trim().isEmpty()
    			|| requestDescription.getText().trim().isEmpty() ||
    			explanation.getText().trim().isEmpty()) {

    		new Alert(AlertType.ERROR, "You must fill all the details!").show();
    		
    		return false;
    	}

    	//if Information System Number is not a valid number
    	/*if(!isNumeric(informationSystemNumber.getText().toString())) {
    		 

    		new Alert(AlertType.ERROR, "Information System must be a valid number!").show();
    		return false;
    	}*/

    	return true;
    }
    
    public void showSeccessAlert() {
    	deleteAll();
    	Alert alert =new Alert(AlertType.INFORMATION, "Your request was received.");
    	alert.setTitle("Request Received!");
    	alert.show();
    }
    
    public void showFailureAlert() {
    	new Alert(AlertType.ERROR, "Your request could not be recieved, please try again.").show();
    }

  /**
   * Check if a number that came out of a String is a valid number
   * @param strNum
   * @return true/false
   */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    
    public static NewRequestController getInstance() {
    	return instance;
    	
    }
    
    public void setAnswerFromServer(boolean answer) {
    	this.answerFromServer = answer;
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance=this;
		initializeComboBox();
		
	}
	
	public void loadPage() {
		this.answerFromServer = false;
		deleteAll();
	}
	
	public void initializeComboBox() {
		
		listForComboBox = new ArrayList<InformationSystem>();
		listForComboBox.add(new InformationSystem(1, "Information for Students Website"));
		listForComboBox.add(new InformationSystem(2, "Information for Lecturers Website"));
		listForComboBox.add(new InformationSystem(3, "Information for Workers Website"));
		listForComboBox.add(new InformationSystem(4, "Moodle"));
		listForComboBox.add(new InformationSystem(5, "Library Website"));
		listForComboBox.add(new InformationSystem(6, "Class Computers"));
		listForComboBox.add(new InformationSystem(7, "Laboratory Computers"));
		listForComboBox.add(new InformationSystem(8, "Computer Farm"));
		listForComboBox.add(new InformationSystem(9, "College Website"));
		
		for(InformationSystem i:listForComboBox) {
			systemsCombobox.getItems().add(i.getInfomationSystemName());
		}
		
	}
	
	public int getInfoSysNumber(String infoSysName) {
		for(InformationSystem i:listForComboBox) {
			if(infoSysName.equals(i.getInfomationSystemName())) {
				return i.getInfomationSystemNumber();
			}
		}
		return 0;
	}
	
	public void printList() {
		for(InformationSystem i:listForComboBox) {
			System.out.println(i.toString());
		}
	}
    
    
}