package org.example.Curve.SplineCanvas;
/**
 *  该类实现坐标参数的计算
 * @author 申雄全
 * @author 吴鹄远
 * Date 2023/12/20 22:58
 */
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
}
