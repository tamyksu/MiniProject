package application;


import java.io.File;
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

	
	Client client = Client.getInstance();
	
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
    
    @FXML
    private Button deleteAllBtn;
    
    @FXML
    private Button backBtn;
    
    
    @FXML
    void deleteAll(ActionEvent event) {
    	informationSystemNumber.clear();
		ProblemDescription.clear();
		requestDescription.clear();
		explanation.clear();
		notes.clear();
		
    }
    
    
    @FXML
    void uploadFiles(ActionEvent event) {

    	FileChooser fc = new FileChooser();
    	List<File> selectedFiles = fc.showOpenMultipleDialog(null);
    	
    	if(selectedFiles != null) {
    		for(int i = 0; i<selectedFiles.size();i++) {
    			filesList.getItems().add(selectedFiles.get(i).getAbsolutePath());
    		}
    	}
    	else {
    		new Alert(AlertType.ERROR, "Files are not valid!").show();
    	}
    	
    }
    
    
    @FXML
    void backClick(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }

    
    
    @FXML
    void sendRequest(ActionEvent event) {
    	
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Date format
    	LocalDate localDate = LocalDate.now(); // Current Date
		if(formCheck()) {
    		int sysNum = Integer.parseInt(informationSystemNumber.getText().toString());
    		String probDesc = ProblemDescription.getText().toString();
    		String reqDesc = requestDescription.getText().toString();
    		String expla = explanation.getText().toString();
    		String notes1 = notes.getText().toString();

    		Request nr = new Request(Client.getInstance().getUserID(),sysNum, probDesc, reqDesc, expla, notes1);
    		//System.out.println(nr.toString());
    		ArrayList<Request> params = new ArrayList<Request>();
    		params.add(nr);
    		Translator translator = new Translator(OptionsOfAction.NEWREQUEST, params);
    		client/*Client.getInstance()*/.handleMessageFromClientGUI(translator);
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
}