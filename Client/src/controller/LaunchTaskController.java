package controller;

import application.Main;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class LaunchTaskController {

	@FXML
	AnchorPane launchTask;
	@FXML
	TextArea taskOutputArea;
	@FXML
	Label taskNumberLabel;
	@FXML
	Button goToMenuButton;
	@FXML
	Button sendLogsButton;
	@FXML
	ToggleGroup taskGroup;
	@FXML
	Button launchTaskButton;

	@FXML
	public void initialize() {
		initializeUserData();
	}

	@FXML
	public void setStartScene() {
		Main.setScene("start");
	}

	@FXML
	public void setSendLogsScene() {
		Main.setScene("sendLogs");
	}

	@FXML
	public void launchTask() {
	}

	private void initializeUserData() {
		ObservableList<Toggle> toggles = taskGroup.getToggles();
		for (int index = 0; index < toggles.size(); ++index) {
			toggles.get(index).setUserData(index + 1);
		}
	}
}
