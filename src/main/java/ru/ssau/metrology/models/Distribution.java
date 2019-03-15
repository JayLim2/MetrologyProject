package ru.ssau.metrology.models;

public enum Distribution {
    NORMAL("Нормальное"),
    UNIFORM("Равномерное");

    private String name;

    Distribution(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}