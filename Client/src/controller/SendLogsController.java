package controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class SendLogsController {

	@FXML
	Button sendLogsButton;
	@FXML
	Button goToMenuButton;

	@FXML
	Pane sendLogs;
	@FXML
	Button goToLaunchTaskButton;

	@FXML
	public void sendLogs() {
	}

	@FXML
	public void setStartScene() {
		Main.setScene("start");
	}

	@FXML
	public void setLaunchTaskScene() {
		Main.setScene("launchTask");
	}
}
