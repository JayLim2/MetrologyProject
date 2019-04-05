package ru.ssau.metrology.view;

public class TablePoint {
    private double value;
    private boolean isVerified;

    public TablePoint(double value, boolean isVerified) {
        this.value = value;
        this.isVerified = isVerified;
    }

    public String getValue() {
        return String.valueOf(value);
    }

    public String getVerified() {
        return isVerified ? "Подходит" : "Не подходит";
    }
}