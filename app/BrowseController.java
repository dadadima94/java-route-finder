package application;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

public class BrowseController implements Initializable {

	@FXML private TableView<Stop> stopList;
	@FXML private TableColumn<Stop, String> nameCol;
	@FXML private TableColumn<Stop, Double> latCol;
	@FXML private TableColumn<Stop, Double> lonCol;
	@FXML private TableColumn<Stop, Integer> idCol;
	@FXML private CheckBox nameCheck;
	@FXML private TextField nameInput;
	@FXML private CheckBox longitudeCheck;
	@FXML private TextField longitudeInput;
	@FXML private RadioButton longitudeAbove;
	@FXML private CheckBox latitudeCheck;
	@FXML private TextField latitudeInput;
	@FXML private RadioButton latitudeAbove;
	@FXML private Button refreshButton;

	private DatabaseConnector dbc = DatabaseConnector.getInstance();
	private ArrayList<Stop> stops = dbc.loadAllStops();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<Stop> data = FXCollections.observableArrayList(stops);
		nameCol.setCellValueFactory(new PropertyValueFactory<Stop, String>("name"));
		lonCol.setCellValueFactory(new PropertyValueFactory<Stop, Double>("lon"));
		latCol.setCellValueFactory(new PropertyValueFactory<Stop, Double>("lat"));
		idCol.setCellValueFactory(new PropertyValueFactory<Stop, Integer>("id"));
		stopList.setItems(data);
	}

	/**
	 * 
	 * @param name
	 *            The name of the stop, or "" if it is empty
	 * @param latitude
	 *            The latitude String: "", "above", or "below"
	 * @param longitude
	 *            The longitude String: "", "above", or "below"
	 * @param lat
	 *            Latitude double value, ignored if latitude equals ""
	 * @param long
	 *            Longitude double value, ignored if longitude equals ""
	 */
	private void refreshStops(String name, String latitude, String longitude, double lat, double lon) {
		CopyOnWriteArrayList<Stop> clone = new CopyOnWriteArrayList<>(stops);
		if (nameCheck.isSelected() && !name.equals("")) {
			for (Stop s : clone) {
				if (!s.getName().matches("(?i).*" + name + ".*")) { // case-insensitive
					clone.remove(s);
				}
			}
		}
		if (longitude.equals("above")) {
			for (Stop s : clone) {
				if (s.getLon() < lon) {
					clone.remove(s);
				}
			}
		} else if (longitude.equals("below")) {
			for (Stop s : clone) {
				if (s.getLon() > lon) {
					clone.remove(s);
				}
			}
		}
		if (latitude.equals("above")) {
			for (Stop s : clone) {
				if (s.getLat() < lat) {
					clone.remove(s);
				}
			}
		} else if (latitude.equals("below")) {
			for (Stop s : clone) {
				if (s.getLat() > lon) {
					clone.remove(s);
				}
			}
		}
		stopList.setItems(FXCollections.observableArrayList(clone));
	}

	public void liveRefreshStops(KeyEvent event) {
		String name = nameInput.getText();
		refreshStops(name, "", "", 0.0, 0.0);
	}

	public void handleRefreshAction(ActionEvent event) {
		String name = "";
		String latitude = "";
		String longitude = "";
		double lat = 0d;
		double lon = 0d;
		name = nameInput.getText();
		if (latitudeCheck.isSelected()) {
			if (!latitudeInput.getText().equals("")) {
				try {
					lat = Double.parseDouble(latitudeInput.getText());
				} catch (NumberFormatException e) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Invalid input");
					alert.setHeaderText("The input to field latitude is not valid.");
					alert.setContentText("Make sure to use a period for decimals.");
					alert.showAndWait();
				}
			}
			if (latitudeAbove.isSelected()) {
				latitude = "above";
			} else {
				latitude = "below";
			}
		}
		if (longitudeCheck.isSelected()) {
			if (!longitudeInput.getText().equals("")) {
				try {
					lon = Double.parseDouble(longitudeInput.getText());
				} catch (NumberFormatException e) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Invalid input");
					alert.setHeaderText("The input to field longitude is not valid.");
					alert.setContentText("Make sure to use a period for decimals.");
					alert.showAndWait();
				}
			}
			if (longitudeAbove.isSelected()) {
				longitude = "above";
			} else {
				longitude = "below";
			}
		}
		refreshStops(name, latitude, longitude, lat, lon);
	}
}
