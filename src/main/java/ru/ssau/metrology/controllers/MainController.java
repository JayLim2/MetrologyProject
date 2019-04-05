package ru.ssau.metrology.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ru.ssau.metrology.models.Distribution;
import ru.ssau.metrology.models.Validator;
import ru.ssau.metrology.view.TablePoint;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainController {
    private static final Double[] ROMANOV_RATE = {0.01, 0.02, 0.05, 0.10};
    private static final double[][] romanovTable = {
            {1.73, 1.72, 1.71, 1.69},
            {2.16, 2.13, 2.10, 2.00},
            {2.43, 2.37, 2.27, 2.17},
            {2.62, 2.54, 2.41, 2.29},
            {2.75, 2.66, 2.52, 2.39},
            {2.90, 2.80, 2.64, 2.49},
            {3.08, 2.96, 2.78, 2.62}
    };

    private enum Criteria {
        NONE, SIGMA, ROMANOV, CHAUVENET

    }

    public RadioButton radioSigmaCriteria;
    public RadioButton radioRomanovCriteria;
    public RadioButton radioChauvenetCriteria;
    public TextField addText;
    public ComboBox<Double> comboRomanovRate;
    public TableView<TablePoint> tableOfPoints;
    private Validator validator;

    @FXML
    private TextField valuesCountInput;
    @FXML
    private TextField rangeFromInput;
    @FXML
    private TextField rangeToInput;
    @FXML
    private ComboBox<Distribution> distributionsComboBox;

    public void initialize() {
        comboRomanovRate.getItems().addAll(Arrays.asList(ROMANOV_RATE));
        comboRomanovRate.getSelectionModel().selectFirst();
//        ObservableList<Distribution> distributions = FXCollections.observableList(Arrays.asList(Distribution.values()));
        //       distributionsComboBox.setItems(distributions);
        //      distributionsComboBox.getSelectionModel().selectFirst();

    }

    public void onCalculateAction() {
        if (valuesCountInput.getText() != null) {
            int coutnInputValues = Integer.parseInt(valuesCountInput.getText());
            if ((rangeFromInput.getText() != null) && (rangeToInput.getText() != null)) {
                double rangeFromInputDouble = Double.parseDouble(rangeFromInput.getText());
                double rangeToInputDouble = Double.parseDouble(rangeToInput.getText());
                tableOfPoints.getItems().clear();
                double step = (rangeToInputDouble - rangeFromInputDouble) / coutnInputValues;
                Random r = new Random();
                for (int i = 0; i < coutnInputValues; i++) {
                    double mx = rangeFromInputDouble + i * step + step / 2;
                    double dx = step / 2;
                    tableOfPoints.getItems().add(new TablePoint(((r.nextDouble() + r.nextDouble() + r.nextDouble() + r.nextDouble() + r.nextDouble() + r.nextDouble()) / 6 * dx + mx)));
                }
            }
        }
    }

    public void validatePoints() {
        List<TablePoint> points = tableOfPoints.getItems();
        Criteria activeCriteria = Criteria.NONE;
        if (radioSigmaCriteria.isSelected()) {
            activeCriteria = Criteria.SIGMA;
        } else if (radioRomanovCriteria.isSelected()) {
            activeCriteria = Criteria.ROMANOV;
        } else if (radioChauvenetCriteria.isSelected()) {
            activeCriteria = Criteria.CHAUVENET;
        }
        for (TablePoint point : points) {
            double mx = calculateMX(points, point);
            double sigma = calculateSigma(points, point, mx);
            Boolean isVerified = null;
            switch (activeCriteria) {
                case SIGMA:
                    isVerified = Math.abs(mx - point.getDoubleValue()) <= 3 * sigma;
                    break;
                case ROMANOV:
                    double value = getRomanovTableValue(comboRomanovRate.getValue(), points.size());
                    isVerified = Math.abs(mx - point.getDoubleValue()) / sigma <= value;
                    break;
                case CHAUVENET:
                    int size = points.size();
                    if (size <= 4) {
                        isVerified = Math.abs(mx - point.getDoubleValue()) <= 1.6 * sigma;
                    } else if (size <= 6) {
                        isVerified = Math.abs(mx - point.getDoubleValue()) <= 1.7 * sigma;
                    } else if (size <= 8) {
                        isVerified = Math.abs(mx - point.getDoubleValue()) <= 1.9 * sigma;
                    } else if (size <= 10) {
                        isVerified = Math.abs(mx - point.getDoubleValue()) <= 2 * sigma;
                    }
                    break;
            }
            point.setVerified(isVerified);
        }
        tableOfPoints.refresh();
    }

    public void addPoint() {
        tableOfPoints.getItems().add(new TablePoint(Double.parseDouble(addText.getText())));
    }

    public void clear() {
        tableOfPoints.getItems().clear();
    }

    private double calculateMX(List<TablePoint> points, TablePoint excluded) {
        double mx = 0.0D;
        for (TablePoint point : points) {
            if (point != excluded) {
                mx += point.getDoubleValue();
            }
        }
        return mx;
    }

    private double calculateSigma(List<TablePoint> points, TablePoint excluded, double mx) {
        double sigma = 0.0D;
        for (TablePoint point : points) {
            if (point != excluded) {
                sigma += Math.pow(point.getDoubleValue() - mx, 2);
            }
        }
        sigma = Math.sqrt(sigma / (points.size() - 1));
        return sigma;
    }

    private double getRomanovTableValue(double rate, int count) {
        double value = 0;
        //Тут я не уверена что в проверке именно меньше, потому что в таблице там равно именно, но тогда мы тип не то количество от пользователя отбарсываем или что?
        if (rate == 1) {
            if (count < 4) {
                value = romanovTable[0][0];
            } else if (count < 6) {
                value = romanovTable[1][0];
            } else if (count < 8) {
                value = romanovTable[2][0];
            } else if (count < 10) {
                value = romanovTable[3][0];
            } else if (count < 12) {
                value = romanovTable[4][0];
            } else if (count < 15) {
                value = romanovTable[5][0];
            } else {
                value = romanovTable[6][0];
            }
        } else if (rate == 2) {
            if (count < 4) {
                value = romanovTable[0][1];
            } else if (count < 6) {
                value = romanovTable[1][1];
            } else if (count < 8) {
                value = romanovTable[2][1];
            } else if (count < 10) {
                value = romanovTable[3][1];
            } else if (count < 12) {
                value = romanovTable[4][1];
            } else if (count < 15) {
                value = romanovTable[5][1];
            } else {
                value = romanovTable[6][1];
            }
        } else if (rate == 5) {
            if (count < 4) {
                value = romanovTable[0][2];
            } else if (count < 6) {
                value = romanovTable[1][2];
            } else if (count < 8) {
                value = romanovTable[2][2];
            } else if (count < 10) {
                value = romanovTable[3][2];
            } else if (count < 12) {
                value = romanovTable[4][2];
            } else if (count < 15) {
                value = romanovTable[5][2];
            } else {
                value = romanovTable[6][2];
            }
        } else if (rate == 10) {
            if (count < 4) {
                value = romanovTable[0][3];
            } else if (count < 6) {
                value = romanovTable[1][3];
            } else if (count < 8) {
                value = romanovTable[2][3];
            } else if (count < 10) {
                value = romanovTable[3][3];
            } else if (count < 12) {
                value = romanovTable[4][3];
            } else if (count < 15) {
                value = romanovTable[5][3];
            } else {
                value = romanovTable[6][3];
            }
        }
        return value;
    }
}