package controller;

import application.Main;
import connections.SchedulerConnection;
import connections.WorkerServerConnection;
import items.Worker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.RadioButton;

public class LaunchTaskController {
	private static int currTaskChosen = 1;

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
	RadioButton t1;
	@FXML
	RadioButton t3;
	@FXML
	RadioButton t4;
	@FXML
	RadioButton t5;
	@FXML
	RadioButton t6;
	@FXML
	RadioButton t8;
	@FXML
	RadioButton t9;
	@FXML
	RadioButton t2;
	@FXML
	RadioButton t7;

	@FXML
	public void initialize() {
		initializeUserData();
		initializeEventListeners();
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
		Worker worker = (Worker) Main.getSchedulerConnection().getWorker();
	    WorkerServerConnection workerServerConnection = new WorkerServerConnection(worker.getHost(), worker.getPort());
	}

	public static void setCurrTaskChosen(ObservableValue<? extends Toggle> toggleObservableValue) {
		currTaskChosen = (Integer) toggleObservableValue.getValue().getUserData();
	}

	private void initializeUserData() {
		// 1-3
		t1.setUserData(1);
		t2.setUserData(2);
		t3.setUserData(3);

		// 4-6
		t4.setUserData(4);
		t5.setUserData(5);
		t6.setUserData(6);

		// 7-9
		t7.setUserData(7);
		t8.setUserData(8);
		t9.setUserData(9);
	}

	private void initializeEventListeners() {
		taskGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> toggleObservableValue, Toggle toggle,
					Toggle newToggle) {

				setCurrTaskChosen(toggleObservableValue);
				changeTextTaskNumberLabel();
			}
		});
	}

	private void changeTextTaskNumberLabel() {
		taskNumberLabel.setText("Wynik taska nr. " + currTaskChosen + ": ");
	}
}
