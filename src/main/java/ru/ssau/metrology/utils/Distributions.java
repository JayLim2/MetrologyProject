package ru.ssau.metrology.utils;

import ru.ssau.metrology.models.Distribution;
import ru.ssau.metrology.models.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Distributions {

    /**
     * Равномерное разбиение интервала. Вычисляется координата Х и на основе нее рассчитывается Y
     *
     * @param distribution распределение
     * @param from         начало интервала
     * @param to           конец интервала
     * @param count        количество точек равномерного разбиения
     * @return список точек
     */
    public static List<Point> getPointsByUniformPartition(Distribution distribution, double from, double to, int count) {
        List<Point> points = new ArrayList<>(count);
        if (Double.compare(from, to) >= 0) return points;

        double delta = (to - from) / count;
        double x = from + delta;
        while (Double.compare(x, to) <= 0) {
            Point point = getDistributionPoint(distribution, x);
            points.add(point);
            x += delta;
        }

        return points;
    }

    /**
     * Вычисляет N случайных точек на интервале по данному заокну распределения
     *
     * @param distribution распределение
     * @param from         начало интервала
     * @param to           конец интервала
     * @param count        количество точек разбиения
     * @return список точек
     */
    public static List<Point> getPoints(Distribution distribution, double from, double to, int count) {
        List<Point> points = new ArrayList<>(count);
        if (Double.compare(from, to) >= 0) return points;

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            double randomUniformValue = nextDouble(random.nextDouble(), from, to); //uniform [0; 1]
            Point point = getDistributionPoint(distribution, randomUniformValue);
            points.add(point);
        }

        return points;
    }

    private static Point getDistributionPoint(Distribution distribution, double x) {
        Point point = new Point();
        point.setX(x);
        double y = 0;
        switch (distribution) {
            case NORMAL:
                y = getNormalDistributionValue(x);
                break;
            case TRIANGULAR:
                y = getTriangularDistributionValue(x);
                break;
        }
        point.setY(y);
        return point;
    }

    // TODO: 15.03.2019 release method
    private static double getNormalDistributionValue(double x) {
        return getNormalDistributionValue(x, 1, 0);
    }

    private static double getNormalDistributionValue(double x, double sigma, double avg) {
        return (1F / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp((-1) * ((x - avg) * (x - avg)) / (2 * sigma * sigma));
    }

    // TODO: 15.03.2019 release method
    private static double getTriangularDistributionValue(double x) {
        return 0;
    }

    //Служебный метод вычисления значения равномерного распределения на интервале [from; to]
    private static double nextDouble(double defaultUniformValue, double from, double to) {
        return defaultUniformValue * (to - from) + from;
    }
}
