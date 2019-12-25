package application;

import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ScreenController {
	
	private static ScreenController screenController;
    private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;

    
    public static ScreenController getScreenController() {
		return screenController;
	}

	public static void setScreenController(ScreenController screenController) {
		ScreenController.screenController = screenController;
	}

	public ScreenController(Scene main) {
        this.main = main;
        if(screenController == null)
        	screenController = this;
    }

	public Scene getCurrentScene() {
		return main;
	}

	public void addScreen(String name, Pane pane){
         screenMap.put(name, pane);
    }

    public void removeScreen(String name){
        screenMap.remove(name);
    }

    public void activate(String name){
        main.setRoot( screenMap.get(name) );
    }
    
}