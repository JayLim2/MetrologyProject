package ru.ssau.metrology.models;

public class toTable{
    private double point;
    private String allow;

    public toTable(double point) {
        this.point = point;
        this.allow="не определено";
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }
}