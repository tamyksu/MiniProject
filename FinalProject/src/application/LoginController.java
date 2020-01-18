package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import translator.OptionsOfAction;
import translator.Translator;
import client.Client;

public class LoginController {

	public static LoginController instance;

	Client client;

	@FXML
	private Button login;

	@FXML
	private PasswordField password;

	@FXML
	private TextField userName;

    @FXML
    private TextField message;
    
	public TextField getMessageField() {
		return message;
	}

	public void setMessage(TextField message) {
		this.message = message;
	}

	public void initialize() throws IOException {
		instance = this;
		client = new Client("localhost", 25565);
		//client = new Client("192.168.162.52", 25565);

	}

	@FXML
	void loginFunc(ActionEvent event) {
		ArrayList<String> check = new ArrayList<String>();
		check.add(userName.getText());
		check.add(password.getText());
		password.clear();
		//userName.clear();
		Translator translator = new Translator(OptionsOfAction.LOGIN, check);
		client.handleMessageFromClientGUI(translator);
	}
	


	 public static LoginController getInstance() {
		 return instance; 
	}
	
	public void recoverPasswordFromDB(ActionEvent event)
	{
		ArrayList<String> check = new ArrayList<String>();
		check.add(userName.getText());
		
		//password.clear();
		//userName.clear();
		
		Translator translator = new Translator(OptionsOfAction.RECOVER_PASSWORD, check);
		client.handleMessageFromClientGUI(translator);
	}
	
	public void sendRecoveredPasswordToUserEmail(ArrayList<String> emailAndPassword)
	{
		if(emailAndPassword.get(0).compareTo("No email was found") == 0)
		{
			System.out.println("LoginController - sendRecoveredPasswordToUserEmail - IF1");
			//return;
			Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException “Not on FX application thread”
	    	    public void run() {
	    	    	Alert alert = new Alert(AlertType.INFORMATION);
	            	
	                alert.setTitle("ERROR");
	                alert.setHeaderText("Password Recovery Failed");
	                alert.setContentText("The email of your ID was not found.\nPlease insert a registered user ID.");
	                alert.showAndWait();
	                return;
	    	    }
	    	});
			return;
		}
		
		if(emailAndPassword.get(0).compareTo("No password was found") == 0)
		{
			Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException “Not on FX application thread”
	    	    public void run() {
	    	    	Alert alert = new Alert(AlertType.INFORMATION);
	            	
	                alert.setTitle("ERROR");
	                alert.setHeaderText("Password Recovery Failed");
	                alert.setContentText("The password of your ID was not found");
	                alert.showAndWait();
	                return;
	    	    }
	    	});
			return;
		}
			
		System.out.println("LoginController - sendRecoveredPasswordToUserEmail: emailAndPassword = " + emailAndPassword);
		try {
			SendMail.sendMail(emailAndPassword.get(0), "Your password was recovered","Your password is: " + emailAndPassword.get(1));
			
			
			
			Platform.runLater(new Runnable() {//avoiding java.lang.IllegalStateException “Not on FX application thread”
	    	    public void run() {
	    	    	Alert alert = new Alert(AlertType.INFORMATION);
	            	
	                alert.setTitle("SUCCESS");
	                alert.setHeaderText("Password Recovery");
	                alert.setContentText("The password to your user was sent to your email");
	                alert.showAndWait();
	    	    }
	    	});
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}