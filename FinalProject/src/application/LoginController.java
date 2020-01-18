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

/**
 * The class of the Login screen controller
 */
public class LoginController {

	public static LoginController instance;

	String ip;
	int port;

	@FXML
	private Button login;

	@FXML
	private PasswordField password;

	@FXML
	private TextField userName;

//    @FXML
//    private TextField message;

    @FXML
    private TextField ip_text;

    @FXML
    private TextField port_text;
    
//	public TextField getMessageField() {
//		return message;
//	}
//
//	public void setMessage(TextField message) {
//		this.message = message;
//	}

	/**
	 * Initializes the controller
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		instance = this;
	    ip_text.setText(Main.ip);
	    port_text.setText(String.valueOf(Main.port));
	}
	/**
	 * 
	 * @param event
	 * event handler for login button
	 */
	@FXML
	void loginFunc(ActionEvent event) {
				
		try {
			
			this.ip = ip_text.getText();
			this.port = Integer.parseInt(port_text.getText());

			new Client(ip, port);
			
			ArrayList<String> check = new ArrayList<String>();
			check.add(userName.getText());
			check.add(password.getText());
			password.clear();
			
			//userName.clear();
			Translator translator = new Translator(OptionsOfAction.LOGIN, check);
			Client.getInstance().handleMessageFromClientGUI(translator);
			
		} catch (IOException e) {
			if(e.getMessage().equals("Connection refused: connect"))
				new Alert(AlertType.WARNING,"Can't connect to server").show();
			else
				new Alert(AlertType.WARNING,"Server Port Or IP are not in correct format").show();

		}
		catch (NumberFormatException e)
		{
			new Alert(AlertType.WARNING,"Server Port is not in correct format").show();
		}

	}
	


	 /**
     * Get the instance instance of LoginController
     * (The only one)
     * @return the only instance of LoginController
     */
	 public static LoginController getInstance() {
		 return instance; 
	}
	
	 /**
	  * Recover password from database
	  * @param event
	  */
	public void recoverPasswordFromDB(ActionEvent event)
	{
		try {
		ArrayList<String> check = new ArrayList<String>();
		check.add(userName.getText());

		Translator translator = new Translator(OptionsOfAction.RECOVER_PASSWORD, check);
		Client.getInstance().handleMessageFromClientGUI(translator);
		}
		catch(NullPointerException e)
		{
			new Alert(AlertType.WARNING,"Please enter your user name to recover the password").show();
		}
	}
	
	/**
	 * Send the recovered password throguh email
	 * @param emailAndPassword
	 */
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