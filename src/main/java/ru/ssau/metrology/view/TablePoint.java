package ru.ssau.metrology.view;

public class TablePoint {
    private double value;
    private Boolean isVerified;

    public TablePoint(double value) {
        this(value, null);
    }

    public TablePoint(double value, Boolean isVerified) {
        this.value = value;
        this.isVerified = isVerified;
    }

    public String getValue() {
        return String.valueOf(value);
    }

    public double getDoubleValue() {
        return value;
    }


    public String getVerified() {
        if (isVerified == null) {
            return "Не определено";
        }
        return isVerified ? "Подходит" : "Не подходит";
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }
}