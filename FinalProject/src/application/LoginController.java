package application;

import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
		client = new Client("localhost", 5555);
	}

	@FXML
	void loginFunc(ActionEvent event) {
		ArrayList<String> check = new ArrayList<String>();
		check.add(userName.getText());
		check.add(password.getText());
		password.clear();
		userName.clear();
		Translator translator = new Translator(OptionsOfAction.LOGIN, check);
		client.handleMessageFromClientGUI(translator);
	}
	


	 public static LoginController getInstance() {
		 return instance; 
	}
	
	
	
}