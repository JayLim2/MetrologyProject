package ru.ssau.metrology.models;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class Validator {
    @FXML
    private Alert alert;

    //Проверка кол-ва значений, на null и цифры
    public boolean checkFieldCountInputValues(String inputString){
        boolean flag = true;
        if(!inputString.equals("")){
            if(checkForIntValue(inputString)){
                //todo: Сделать тут проверку на длину числа
            }else{
                addAlter("Не корректное число","Ошибка");
            }
        }else{
            addAlter("Введите количесвто значений","Ошибка");
        }
        return flag;
    }

    //Проверка на то, что сткора int
    private boolean checkForIntValue(String inputIntValue){
        boolean flag = true;
        char[] charsInputString = inputIntValue.toCharArray();
        for(int i=0;i<charsInputString.length;i++){
            if((charsInputString[i]<'0')||(charsInputString[i]>'9')){
                flag=false;
                break;
            }
        }
        return flag;
    }

    //Вывод уведомлений
    private void addAlter(String strOutput, String identification) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(identification);
        alert.setHeaderText(null);
        alert.setContentText(strOutput);
        alert.showAndWait();
    }

    //Проверка интервала
    public boolean checkRange(String inputStringRange){
        boolean flag = false;
        if(!inputStringRange.equals("")){
            if(checkForDoubleValue(inputStringRange)){
                //todo: Сделать тут проверку на длину числа
            }else{
                addAlter("Не корректное число интервала","Ошибка");
            }
        }else{
            addAlter("Введите интервал","Ошибка");
        }
        return flag;
    }

    //Проверка на то, что сткора double
    private boolean checkForDoubleValue(String inputDoubleValue){
        boolean flag = false;
        int countDot = 0;
        char[] charsInputString = inputDoubleValue.toCharArray();
        for(int i=0;i<charsInputString.length;i++){
            if((charsInputString[i]<'0')||(charsInputString[i]>'9')){
                if((charsInputString[i]=='.')&&(countDot==0)){
                    countDot++;
                }else{
                    flag=false;
                    break;
                }
            }
        }
        return flag;
    }
}