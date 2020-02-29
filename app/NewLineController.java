package application;

import java.net.URL;
import java.util.*;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class NewLineController implements Initializable {

	@FXML private ScrollPane scrollPane;
	@FXML private Button addButton;
	@FXML private Label resultMessage;
	@FXML private Label successLabel;
	@FXML private TextField routeName;

	private DatabaseConnector dbc = DatabaseConnector.getInstance();
	private ArrayList<Stop> stops = dbc.loadAllStops();
	private ArrayList<CheckBox> boxes;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Collections.sort(stops);
		VBox vbox = new VBox();
		boxes = new ArrayList<>();
		for (Stop s : stops) {
			boxes.add(new CheckBox(s.getName() + " " + s.getId()));
		}
		for (int i = 0; i < boxes.size(); i++) {
			vbox.getChildren().add(boxes.get(i));
		}
		scrollPane.setContent(vbox);
	}

	public void handleAddEvent(ActionEvent e) {
		ArrayList<Stop> selectedStops = new ArrayList<>();
		for (CheckBox b : boxes) {
			if (b.isSelected()) {
				selectedStops.add(dbc.getStopById(b.getText()));
			}
		}
		if (selectedStops.size() < 2) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Not enough stops");
			alert.setHeaderText("You have not selected enough stops for a new line");
			alert.setContentText("Select two or more stops");
			alert.showAndWait();
		} else {
			String name = routeName.getText();
			if (name.equals("")) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Empty name");
				alert.setHeaderText("You have not put in a name");
				alert.showAndWait();
			} else {
				dbc.insertRoute(name, selectedStops);
				successLabel.setVisible(true);
				PauseTransition pause = new PauseTransition(Duration.seconds(5));
				pause.setOnFinished(event -> successLabel.setVisible(false));
				pause.play();
			}
		}
		for (CheckBox b : boxes) {
			b.setSelected(false);
		}
	}
}
