package application;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import utils.AppUtils;
import utils.RandomLogGenerator;

public class Main extends Application {
	private static Stage stage;
	private static HashMap<String, Scene> scenes = new HashMap<>();
	private static Gson gson = AppUtils.buildGson();
	private static RandomLogGenerator logGenerator = new RandomLogGenerator(gson);

	@Override
	public void start(Stage stage) {
		this.stage = stage;

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

	private void configureStage() {
		stage.setResizable(false);
	}

	private void configureAllScenes() {
		configureScene((Pane) loadResource("start"), 200, 100);
		configureScene((Pane) loadResource("sendLogs"), 500, 806);
		configureScene((Pane) loadResource("launchTask"), 500, 806);
	}

	private void configureScene(Pane pane, int width, int height) {
		System.out.println("Configuring layout: " + pane.getId());
		scenes.put(pane.getId(), new Scene(pane, width, height));
	}

	private Object loadResource(String resourceName) {
		try {
			return FXMLLoader.load(getClass().getResource("../layouts/" + resourceName + ".fxml"));
		} catch (IOException err) {
			System.out.println("Error while trying to reach scene resource");
			err.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
