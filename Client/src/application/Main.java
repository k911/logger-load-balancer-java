package application;

import java.io.IOException;

import java.util.HashMap;

import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import connections.SchedulerConnection;

public class Main extends Application {
	private static Stage stage;
	private static HashMap<String, Scene> scenes = new HashMap<>();
    private final static Logger logger = Logger.getLogger(Main.class.getName());
	private static SchedulerConnection schedulerConnection = new SchedulerConnection();
	
	@Override
	public void start(Stage stage) {
		Main.stage = stage;

		// Configure primary stage
		configureStage();

		// Configure all scenes
		configureAllScenes();

		// Show the start scene
		setScene("start");
	}

	public static void setScene(String sceneName) {
		stage.setScene(scenes.get(sceneName));
		stage.show();
	}
	
	public static SchedulerConnection getSchedulerConnection () {
		return schedulerConnection;
	}

	private void configureStage() {
		stage.setResizable(false);
	}

	private void configureAllScenes() {
		configureScene((Pane) loadResource("start"), 200, 100);
		configureScene((Pane) loadResource("sendLogs"), 500, 404);
		configureScene((Pane) loadResource("launchTask"), 500, 806);
	}

	private void configureScene(Pane pane, int width, int height) {
		logger.info("Configuring layout: " + pane.getId());
		scenes.put(pane.getId(), new Scene(pane, width, height));
	}

	private Object loadResource(String resourceName) {
		try {
			return FXMLLoader.load(getClass().getResource("../layouts/" + resourceName + ".fxml"));
		} catch (IOException err) {
			logger.severe("Error while trying to reach scene resource: " + err.getMessage());
			err.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
