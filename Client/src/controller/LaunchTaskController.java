package controller;

import java.util.HashMap;
import java.util.Map;

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
import worker.communication.job.Job;
import worker.communication.job.JobType;
import javafx.scene.control.RadioButton;

public class LaunchTaskController {
	private static Long currTaskChosen = (long) 1;
	private Map<Long, Job> jobs = new HashMap<>();

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
		// Create job then
		Job job = createJobWithType();

		// Put the job to Map
		putJob((long) 0, job);

		// Get available worker
		Worker worker = (Worker) Main.getSchedulerConnection().getWorker();

		// Establish connection
		WorkerServerConnection workerServerConnection = new WorkerServerConnection(worker.getHost(), worker.getPort(),
				jobs);

		// Send request
		// @TODO: SEND REQUEST HERE
		// workerSeverConnection.sendRequest();
		
		// Get results and show them
		// @TODO: FUNCTION FOR GETTING RESULTS	
		taskOutputArea.setText("RESULT HERE");
	}

	public static void setCurrTaskChosen(ObservableValue<? extends Toggle> toggleObservableValue) {
		currTaskChosen = (Long) toggleObservableValue.getValue().getUserData();
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

	private void putJob(Long key, Job job) {
		jobs.put(key, job);
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

	private Job createJobWithType() {
		JobType jobType = null;

		switch (currTaskChosen.intValue()) {
		case 1:
			jobType = JobType.CALC_ARITHMETIC_MEAN;
			break;
		case 2:
			jobType = JobType.CALC_GEOMETRIC_MEAN;
			break;
		case 3:
			jobType = JobType.CALC_MEDIAN;
			break;
		case 4:
			jobType = JobType.CALC_NUMBER_OCCURENCES;
			break;
		case 5:
			jobType = JobType.CALC_PHRASE_OCCURENCES;
			break;
		case 6:
			jobType = JobType.CALC_STANDARD_DEVIATION;
			break;
		case 7:
			jobType = JobType.CALC_VARIANCE;
			break;
		case 8:
			jobType = JobType.FIND_MAX;
			break;
		case 9:
			jobType = JobType.FIND_MIN;
			break;
		}

		return new Job(jobType, null);
	}

	private void changeTextTaskNumberLabel() {
		taskNumberLabel.setText("Wynik taska nr. " + currTaskChosen + ": ");
	}
}
