package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			Parent root = FXMLLoader.load(getClass().getResource("/application/amirIdea.fxml"));
			Scene scene = new Scene(root, 600,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			ScreenController screenController = new ScreenController(scene);
			screenController.addScreen("login", FXMLLoader.load(getClass().getResource( "/application/Login.fxml" )));
			screenController.addScreen("processesMain", FXMLLoader.load(getClass().getResource( "/application/ProcessesMain.fxml" )));
			screenController.activate("login");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
