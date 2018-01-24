package controller;

import java.util.Date;

import com.google.gson.Gson;
import utils.AppUtils;
import utils.RandomLogGenerator;
import application.Main;
import items.Log;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;

public class SendLogsController {
	private static Gson gson = AppUtils.buildGson();
	private static RandomLogGenerator logGenerator = new RandomLogGenerator(gson);
	private static Date date = new Date();
	private static int currentLogNumber = 0;
    private static SendLogsController sendsLogsController;
    
	@FXML
	Button sendLogsButton;
	@FXML
	Button goToMenuButton;
	@FXML
	Pane sendLogs;
	@FXML
	Button goToLaunchTaskButton;
	@FXML
	Button generateRandomLogButton;
	@FXML
	TextArea logsTextArea;
	@FXML
	Label logsStatusLabel;

	@FXML
	void initialize() {
		sendsLogsController = this;
		logsStatusLabel.setText("idle: Czekam na logi");
	}

	@FXML
	public void sendLogs() {
		setLogsStatusLabel("working", "Przesyłam logi...");

		Main.getSchedulerConnection().sendLogs("Log: " + date.toString() + " " + incrementLogNumber(),
				logsTextArea.getText());
	}

	@FXML
	public void setStartScene() {
		Main.setScene("start");
	}

	@FXML
	public void setLaunchTaskScene() {
		Main.setScene("launchTask");
	}

	@FXML
	public void generateRandomLog() {
		setLogsStatusLabel("success", "Wygenerowałem poprawnie losowy log!");
		Log randomLog = logGenerator.generate();
		logsTextArea.setText(gson.toJson(randomLog));
	}
	
	public static SendLogsController getController() {
		return sendsLogsController;
	}

	public void setLogsStatusLabel(String type, String message) {
		String typeMessage = type.toUpperCase();
		logsStatusLabel.setText(typeMessage + ": " + message);
		changeLogsStatusLabelTextColor(typeMessage);
	}

	private void changeLogsStatusLabelTextColor(String type) {
		if (type == "ERROR") {
			logsStatusLabel.setTextFill(Color.RED);
		} else if (type.equals("WARNING")) {
			logsStatusLabel.setTextFill(Color.YELLOW);
		} else if (type.equals("SUCCESS")) {
			logsStatusLabel.setTextFill(Color.GREEN);
		} else if (type.equals("WORKING")) {
			logsStatusLabel.setTextFill(Color.BLUE);
		}
	}

	private int incrementLogNumber() {
		return ++currentLogNumber;
	}
}