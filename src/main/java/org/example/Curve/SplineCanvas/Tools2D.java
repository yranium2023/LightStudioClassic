package org.example.Curve.SplineCanvas;

public class Tools2D {
    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double Xdistance(double x1, double x2) {
        double dx = x2 - x1;

        return dx>0?dx:-dx;
    }
    public static double Ydistance( double y1, double y2) {

        double dy = y2 - y1;
        return dy>0?dy:-dy;
    }
}
