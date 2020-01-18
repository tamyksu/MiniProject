package application;

import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class ScreenController {
	
	private static ScreenController screenController;
    private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;
    private String lastScreen;
    private String currentScreen;
    
    
    /**
     * Get the ScreenController instance
     * @return
     */
    public static ScreenController getScreenController() {
		return screenController;
	}

    /**
     * Set the instance of ScreenController
     * @param screenController
     */
	public static void setScreenController(ScreenController screenController) {
		ScreenController.screenController = screenController;
	}

	/**
	 * Constructor
	 * @param main
	 */
	public ScreenController(Scene main) {
        this.main = main;
        if(screenController == null)
        	screenController = this;
    }

	/**
	 * get the current scene
	 * @return
	 */
	public Scene getCurrentScene() {
		return main;
	}

	/**
	 * Add a screen to the ScreenController instance
	 * @param name
	 * @param pane
	 */
	public void addScreen(String name, Pane pane){
         screenMap.put(name, pane);
    }

	/**
	 * Remove a screen to the ScreenController instance
	 * @param name
	 * @param pane
	 */
    public void removeScreen(String name){
        screenMap.remove(name);
    }

    /**
     * Open the required screen
     * @param name
     */
    public void activate(String name){
        main.setRoot( screenMap.get(name) );
        lastScreen = currentScreen;
        currentScreen = name;
    }

    /**
     * Get the previous screen
     * @return
     */
	public String getLastScreen() {
		return lastScreen;
	}

    
}