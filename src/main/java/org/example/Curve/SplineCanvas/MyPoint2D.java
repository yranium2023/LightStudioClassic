package org.example.Curve.SplineCanvas;

/**
 * 该表示二维坐标中的点。
 *
 * @author 申雄全
 * @author 吴鹄远
 * Date 2023/12/23 22:59
 */
public class MyPoint2D {

    private double x;
    private double y;

    public MyPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }
}
