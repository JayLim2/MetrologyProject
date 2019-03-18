package ru.ssau.metrology.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.ssau.metrology.models.Distribution;
import ru.ssau.metrology.models.Validator;
import ru.ssau.metrology.utils.Forms;

import java.util.Arrays;

public class MainController {

    private Validator validator;

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
        if(validator.checkFieldCountInputValues(valuesCountInput.getText())){
            int coutnInputValues = Integer.parseInt(valuesCountInput.getText());
            if((validator.checkRange(rangeFromInput.getText()))&&(validator.checkRange(rangeToInput.getText()))){
                double rangeFromInputDouble = Double.parseDouble(rangeFromInput.getText());
                double rangeToInputDouble = Double.parseDouble(rangeToInput.getText());

                //Далее переходим на форму и передаём в её контроллер все полученные значения, ну или подсчитать что то.
                try {
                    Forms.getForm("AutoCalculationsResult", "Рассчитать в автоматическом режиме").showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}