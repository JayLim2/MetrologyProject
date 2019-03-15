package ru.ssau.metrology.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import ru.ssau.metrology.models.Distribution;
import ru.ssau.metrology.utils.Forms;

import java.util.Arrays;

public class MainController {

    @FXML
    private RadioButton manualMode;
    @FXML
    private RadioButton automaticMode;
    @FXML
    private TextField valuesCountInput;
    @FXML
    private TextField rangeFromInput;
    @FXML
    private TextField rangeToInput;
    @FXML
    private ComboBox<Distribution> distributionsComboBox;

    public void initialize() {
        ToggleGroup modeSelectorsGroup = new ToggleGroup();
        manualMode.setToggleGroup(modeSelectorsGroup);
        automaticMode.setToggleGroup(modeSelectorsGroup);
        automaticMode.setSelected(true);

        ObservableList<Distribution> distributions = FXCollections.observableList(Arrays.asList(Distribution.values()));
        distributionsComboBox.setItems(distributions);
        distributionsComboBox.getSelectionModel().selectFirst();
    }

    public void onCalculateAction() {
        try {
            Forms.getForm("AutoCalculationsResult", "Рассчитать в автоматическом режиме").showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}