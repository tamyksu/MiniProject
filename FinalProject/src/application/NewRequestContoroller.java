package application;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import translator.OptionsOfAction;
import translator.Translator;

public class NewRequestContoroller {

	private static NewRequestContoroller instance;
	
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
    
    
    /**
     * Delete all the fields in the form
     * @param event
     */
    @FXML
    void deleteAll(ActionEvent event) {
    	deleteAll();
    }
    
    void deleteAll() {
    	informationSystemNumber.clear();
		ProblemDescription.clear();
		requestDescription.clear();
		explanation.clear();
		notes.clear();
		filesList.getItems().clear();
    }
    
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
			instance = this;
    		int sysNum = Integer.parseInt(informationSystemNumber.getText().toString());
    		String probDesc = ProblemDescription.getText().toString();
    		String reqDesc = requestDescription.getText().toString();
    		String expla = explanation.getText().toString();
    		String notes1 = notes.getText().toString();

    		Request nr = new Request(Client.getInstance().getUserID(),sysNum, probDesc, reqDesc, expla, notes1);
    		
    		ArrayList<Object> params = new ArrayList<Object>();
    		params.add(nr);

    		
    		// **************************************************************** Send Files
    		int numberOfFiles = filesList.getItems().size();
    		
        	System.out.println(numberOfFiles);
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
    	}
    }
    
    /**
     * Form validation
     * @return  true/false
     */
    public boolean formCheck() {

    	// if any of the TextAreas/Textfields is empty:
    	if(informationSystemNumber.getText().trim().isEmpty() || ProblemDescription.getText().trim().isEmpty()
    			|| requestDescription.getText().trim().isEmpty() ||
    			explanation.getText().trim().isEmpty() ||
    			notes.getText().trim().isEmpty()) {
    		//frame=new JFrame();  

    		new Alert(AlertType.ERROR, "You must fill all the details!").show();
    		
    		return false;
    	}

    	//if Information System Number is not a valid number
    	if(!isNumeric(informationSystemNumber.getText().toString())) {
    		 

    		new Alert(AlertType.ERROR, "Information System must be a valid number!").show();
    		return false;
    	}

    	return true;
    }
    
    public void showSeccessAlert() {
    	System.out.println("Good Alert 2");
    	new Alert(AlertType.CONFIRMATION, "Your request was recieved.").show();
    	System.out.println("Good Alert 2");
    	deleteAll();
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
    
    public static NewRequestContoroller getInstance() {
    	return instance;
    }
    
    
}