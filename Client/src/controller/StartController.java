package controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartController {
	@FXML
	Button sendLogsButton;
	@FXML
	Button launchTaskButton;

	@FXML
	public void setSendLogsScene() {
		Main.setScene("sendLogs");
	}

	@FXML
	public void setLaunchTaskScene() {
		Main.setScene("launchTask");
	}
}
