package ru.ssau.metrology.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.ssau.metrology.models.Distribution;
import ru.ssau.metrology.models.Validator;
import ru.ssau.metrology.models.toTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainController {

    public TableColumn pointTable;
    public TableView<toTable> tableArray;
    public TableColumn tableBoolean;
    public TextArea textArea;
    public TextArea textAreaAllow;
    public RadioButton sigmMetod;
    public RadioButton romanovMetod;
    public RadioButton shevineMetod;
    public TextField addText;
    public TextField romanovParametr;
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
        inputArray = new ArrayList<toTable>();
//        ObservableList<Distribution> distributions = FXCollections.observableList(Arrays.asList(Distribution.values()));
        //       distributionsComboBox.setItems(distributions);
        //      distributionsComboBox.getSelectionModel().selectFirst();

    }

    List<toTable> inputArray;

    public void onCalculateAction() {
        if (valuesCountInput.getText() != null) {
            int coutnInputValues = Integer.parseInt(valuesCountInput.getText());
            if ((rangeFromInput.getText() != null) && (rangeToInput.getText() != null)) {
                textArea.clear();
                textAreaAllow.clear();
                double rangeFromInputDouble = Double.parseDouble(rangeFromInput.getText());
                double rangeToInputDouble = Double.parseDouble(rangeToInput.getText());
                inputArray.clear();
                double step = (rangeToInputDouble - rangeFromInputDouble) / coutnInputValues;
                Random r = new Random();
                for (int i = 0; i < coutnInputValues; i++) {
                    double mx = rangeFromInputDouble + i * step + step / 2;
                    double dx = step / 2;
                    // inputArray.add(new toTable(((r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() - 3) / Math.sqrt(0.25)) * dx + mx));
                    // textArea.insertText(0,String.valueOf(((r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() - 3) / Math.sqrt(0.25)) * dx + mx)+"\n");
                    //textArea.insertText(0,String.valueOf((r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian())/6*dx+mx)+"\n");
                    inputArray.add(new toTable(((r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian() + r.nextGaussian()) / 6 * dx + mx)));
                    // inputArray.add(new toTable(((r.nextDouble()*dx+mx))));
                    textArea.insertText(0, String.valueOf(inputArray.get(inputArray.size() - 1).getPoint()) + "\n");
                    textAreaAllow.insertText(0, inputArray.get(inputArray.size() - 1).getAllow() + "\n");
                }
                // pointTable.getColumns().add(inputArray);
            }

        }
    }

    public void onWork(ActionEvent actionEvent) {
        //textArea.clear();
        textAreaAllow.clear();
        if (sigmMetod.isSelected()) {
            for (toTable t : inputArray) {
                double mx = 0;//матожидание
                double sigma = 0;
                for (toTable t2 : inputArray) {
                    if (t != t2) mx += t2.getPoint();
                }
                for (toTable t2 : inputArray) {
                    if (t != t2) sigma += Math.pow(t2.getPoint() - mx, 2);
                }
                sigma = Math.sqrt(sigma / (inputArray.size() - 1));
                if (Math.abs(mx - t.getPoint()) > 3 * sigma) t.setAllow("не подходит");
                else t.setAllow("подходит");
                //textArea.insertText(0,String.valueOf(t.getPoint())+"\n");
                textAreaAllow.insertText(0, String.valueOf(t.getAllow()) + "\n");
            }

        }
        if (romanovMetod.isSelected()) {

            Double u = (Double.valueOf(romanovParametr.getText()) * 100);//тут на самом деле значение 0.01,0.02,0.05 или 0.1 вводимые пользователем на форме умноженные на 100(?) или витсчкейс перепишите
            int p = u.intValue();
            double[][] tableRomavski = {{1.73, 1.72, 1.71, 1.69},
                    {2.16, 2.13, 2.10, 2.00},
                    {2.43, 2.37, 2.27, 2.17},
                    {2.62, 2.54, 2.41, 2.29},
                    {2.75, 2.66, 2.52, 2.39},
                    {2.90, 2.80, 2.64, 2.49},
                    {3.08, 2.96, 2.78, 2.62}};
            for (toTable t : inputArray) {
                double mx = 0;//матожидание
                double sigma = 0;
                for (toTable t2 : inputArray) {
                    if (t2 != t) mx += t2.getPoint();
                }
                for (toTable t2 : inputArray) {
                    if (t2 != t) sigma += Math.pow(t2.getPoint() - mx, 2);
                }
                sigma = Math.sqrt(sigma / (inputArray.size() - 1));
                int size = inputArray.size();
                double b = 0;
                switch (p) { //Тут я не уверена что в проверке именно меньше, потому что в таблице там равно именно, но тогда мы тип не то количество от пользователя отбарсываем или что?
                    case (1): {
                        if (size < 4) b = tableRomavski[0][0];
                        else if (size < 6) b = tableRomavski[1][0];
                        else if (size < 8) b = tableRomavski[2][0];
                        else if (size < 10) b = tableRomavski[3][0];
                        else if (size < 12) b = tableRomavski[4][0];
                        else if (size < 15) b = tableRomavski[5][0];
                        else b = tableRomavski[6][0];
                    }
                    case (2): {
                        if (size < 4) b = tableRomavski[0][1];
                        else if (size < 6) b = tableRomavski[1][1];
                        else if (size < 8) b = tableRomavski[2][1];
                        else if (size < 10) b = tableRomavski[3][1];
                        else if (size < 12) b = tableRomavski[4][1];
                        else if (size < 15) b = tableRomavski[5][1];
                        else b = tableRomavski[6][1];
                    }
                    case (5): {
                        if (size < 4) b = tableRomavski[0][2];
                        else if (size < 6) b = tableRomavski[1][2];
                        else if (size < 8) b = tableRomavski[2][2];
                        else if (size < 10) b = tableRomavski[3][2];
                        else if (size < 12) b = tableRomavski[4][2];
                        else if (size < 15) b = tableRomavski[5][2];
                        else b = tableRomavski[6][2];
                    }
                    case (10): {
                        if (size < 4) b = tableRomavski[0][3];
                        else if (size < 6) b = tableRomavski[1][3];
                        else if (size < 8) b = tableRomavski[2][3];
                        else if (size < 10) b = tableRomavski[3][3];
                        else if (size < 12) b = tableRomavski[4][3];
                        else if (size < 15) b = tableRomavski[5][3];
                        else b = tableRomavski[6][3];
                    }
                }
                if ((Math.abs(mx - t.getPoint()) / sigma) > b) t.setAllow("не подходит");
                else t.setAllow("подходит");
                textAreaAllow.insertText(0, String.valueOf(t.getAllow()) + "\n");
            }


        }
        if (shevineMetod.isSelected()) {
            for (toTable t : inputArray) {
                double mx = 0;//матожидание
                double sigma = 0;
                for (toTable t2 : inputArray) {
                    if (t != t2) mx += t2.getPoint();
                }
                for (toTable t2 : inputArray) {
                    if (t != t2) sigma += Math.pow(t2.getPoint() - mx, 2);
                }
                sigma = Math.sqrt(sigma / (inputArray.size() - 1));
                int size = inputArray.size();
                if (size <= 4) {
                    if (Math.abs(mx - t.getPoint()) > 1.6 * sigma) t.setAllow("не подходит");
                    else t.setAllow("подходит");
                } else {
                    if (size <= 6) {
                        if (Math.abs(mx - t.getPoint()) > 1.7 * sigma) t.setAllow("не подходит");
                        else t.setAllow("подходит");
                    } else {
                        if (size <= 8) {
                            if (Math.abs(mx - t.getPoint()) > 1.9 * sigma) t.setAllow("не подходит");
                            else t.setAllow("подходит");
                        } else {
                            if (size <= 10) {
                                if (Math.abs(mx - t.getPoint()) > 2 * sigma) t.setAllow("не подходит");
                                else t.setAllow("подходит");
                            }

                        }
                    }
                }

                textAreaAllow.insertText(0, String.valueOf(t.getAllow()) + "\n");
            }
        }
    }

    public void toAdd(ActionEvent actionEvent) {
        inputArray.add(new toTable(Integer.parseInt(addText.getText())));
        textArea.insertText(0, String.valueOf(inputArray.get(inputArray.size() - 1).getPoint()) + "\n");
        textAreaAllow.insertText(0, inputArray.get(inputArray.size() - 1).getAllow() + "\n");
    }

    public void clear(ActionEvent actionEvent) {
        inputArray.clear();
        textArea.clear();
        textAreaAllow.clear();
    }
}