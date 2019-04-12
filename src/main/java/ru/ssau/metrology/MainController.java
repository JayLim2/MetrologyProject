package ru.ssau.metrology;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.math3.stat.inference.TestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainController {
    private static Random random = new Random();
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
    public TextField fieldValuesCount;
    public RadioButton radioRangeMethod;
    public TextField fieldRangeFrom;
    public TextField fieldRangeTo;
    public RadioButton radioParamsMethod;
    public TextField fieldMX;
    public TextField fieldDX;
    public TextField fieldAddText;
    public RadioButton radioSigmaCriteria;
    public RadioButton radioRomanovCriteria;
    public ComboBox<Double> comboRomanovRate;
    public RadioButton radioChauvenetCriteria;
    public TableView<TablePoint> tableOfPoints;
    public Label tCriteria;
    public CheckBox checkInnerRange;
    public TextField fieldInnerRangeFrom;
    public TextField fieldInnerRangeTo;
    private double lastMXValue = 0.0D;

    private enum Criteria {
        NONE, SIGMA, ROMANOV, CHAUVENET
    }


    public void initialize() {
        DoubleStringConverter converter = new DoubleStringConverter();
        fieldValuesCount.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        textFieldInit(radioRangeMethod.selectedProperty(), fieldRangeFrom, converter);
        textFieldInit(radioRangeMethod.selectedProperty(), fieldRangeTo, converter);
        textFieldInit(radioParamsMethod.selectedProperty(), fieldMX, converter);
        textFieldInit(radioParamsMethod.selectedProperty(), fieldDX, converter);
        textFieldInit(checkInnerRange.selectedProperty(), fieldInnerRangeFrom, converter);
        textFieldInit(checkInnerRange.selectedProperty(), fieldInnerRangeTo, converter);

        fieldInnerRangeFrom.disableProperty().bind(checkInnerRange.selectedProperty().not());
        fieldInnerRangeFrom.setTextFormatter(new TextFormatter<>(converter));

        fieldInnerRangeTo.disableProperty().bind(checkInnerRange.selectedProperty().not());
        fieldInnerRangeTo.setTextFormatter(new TextFormatter<>(converter));

        comboRomanovRate.disableProperty().bind(radioRomanovCriteria.selectedProperty().not());
        comboRomanovRate.getItems().addAll(Arrays.asList(ROMANOV_RATE));
        comboRomanovRate.getSelectionModel().selectFirst();
    }

    private void textFieldInit(BooleanProperty property, TextField field, DoubleStringConverter converter) {
        field.disableProperty().bind(property.not());
        field.setTextFormatter(new TextFormatter<>(converter));
    }

    public void onCalculateAction() {
        Integer valuesCount = (Integer) fieldValuesCount.getTextFormatter().getValue();
        Double rangeFrom = (Double) fieldRangeFrom.getTextFormatter().getValue();
        Double rangeTo = (Double) fieldRangeTo.getTextFormatter().getValue();
        Double innerRangeFrom = (Double) fieldInnerRangeFrom.getTextFormatter().getValue();
        Double innerRangeTo = (Double) fieldInnerRangeTo.getTextFormatter().getValue();
        Double mx = (Double) fieldMX.getTextFormatter().getValue();
        Double dx = (Double) fieldDX.getTextFormatter().getValue();
        if (valuesCount == null
                || (radioParamsMethod.isSelected() && (mx == null || dx == null))
                || (checkInnerRange.isSelected() && (innerRangeFrom == null || innerRangeTo == null))
                || (radioRangeMethod.isSelected() && (rangeFrom == null || rangeTo == null))) {
            return;
        }
        clear();
        if (radioRangeMethod.isSelected()) {
            mx = (rangeTo + rangeFrom) / 2;
            dx = (rangeTo - mx) / 3;
        }
        lastMXValue = mx;
        int j = 10000000;
        for (int i = 0; i < valuesCount && j > 0; --j) {
            double value = random.nextGaussian() * Math.sqrt(dx) + mx;
            if (checkInnerRange.isSelected() && (value < innerRangeFrom || value > innerRangeTo)) {
                continue;
            }
            tableOfPoints.getItems().add(new TablePoint(value));
            ++i;
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
        double[] pointsArray = toPrimitiveArray(points.stream().filter(TablePoint::isVerified).map(TablePoint::getDoubleValue).toArray(Double[]::new));
        if (pointsArray.length < 2) {
            return;
        }
        tCriteria.setText(String.format("%.3f", TestUtils.tTest(lastMXValue, pointsArray)));
    }

    private double[] toPrimitiveArray(Double[] doubles) {
        double[] array = new double[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            array[i] = doubles[i];
        }
        return array;
    }

    public void addPoint() {
        tableOfPoints.getItems().add(new TablePoint(Double.parseDouble(fieldAddText.getText())));
    }

    public void clear() {
        tableOfPoints.getItems().clear();
        tCriteria.setText("");
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