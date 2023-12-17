package org.example.Curve.SplineCanvas;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.example.Obj.AdjustHistory;
import org.example.StaticValues;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class SplineCanvas extends StackPane{
    private ControlPoints controlPoints;
    private ArrayList<MyPoint2D> curvePoints=new ArrayList<>();
    public  static LUT ResultLUT=new LUT();
    private static final Color CURVE_COLOR = Color.GRAY;
    private static final Color CONTROL_POINT_COLOR = Color.WHITE;
    private static final double CANVAS_POINT_RADIUS=3.0;
    private static final double CURVE_THICKNESS = 1.2;
    private static final double step = 1.0;

    private static final double Grid_THICKNESS =0.7;
    private static final Color GRID_COLOR = Color.WHITE;
    private  double CANVAS_WIDTH = 300;
    int DraggingIndex=-1;
    Canvas canvas;
    GraphicsContext gc ;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> currentTask = null; // 当前任务
    public SplineCanvas(double prefWidth) {
        this.setPrefWidth(prefWidth);
        this.setPrefHeight(prefWidth);

        this.CANVAS_WIDTH=prefWidth;

        controlPoints=new ControlPoints(new MyPoint2D(0, CANVAS_WIDTH),new MyPoint2D(CANVAS_WIDTH, 0),3*CANVAS_POINT_RADIUS);

        canvas=new Canvas(CANVAS_WIDTH,CANVAS_WIDTH);
        gc=canvas.getGraphicsContext2D();
        redraw();

        canvas.setOnMouseReleased(mouseEvent -> {
            DraggingIndex=-1;
            StaticValues.editingImageObj.addHistory(new AdjustHistory("点曲线调整",ResultLUT));
        });
        canvas.setOnMousePressed(event -> {
            double x=event.getX();
            double y= event.getY();
            MyPoint2D point = new MyPoint2D(x, y);
            if(event.getButton()==MouseButton.PRIMARY){
                if(controlPoints.addPoint(point)){
                    DraggingIndex=controlPoints.selectPoint(point);
                    redraw();
                }else {
                    DraggingIndex=controlPoints.selectPoint(point);
                }

            }
            else if(event.getButton()==MouseButton.SECONDARY){
                if(controlPoints.removePoint(point)){
                    redraw();
                }
            }
        });
        // 添加鼠标拖动处理程序
        canvas.setOnMouseDragged(event -> {
            if(DraggingIndex!=-1){
                double x = event.getX();
                double y = event.getY();
                MyPoint2D point = new MyPoint2D(x, y);
                if(controlPoints.movePoint(DraggingIndex,point)){
                    redraw();
                }
            }

        });
        this.getChildren().add(canvas);
        StackPane.setAlignment(canvas, Pos.CENTER);
        StackPane.setMargin(canvas, new Insets(20));
    }

    private void redraw(){

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawGrids();
        drawOutLine();

        gc.setStroke(CURVE_COLOR);
        gc.setLineWidth(CURVE_THICKNESS);
        if (controlPoints.getSize() > 2) {

            drawSplineCurve();

        }else{
            //如果少于两个点，画直线
            drawStraightLine();

        }
        drawControlPoints();
    }

    private void drawStraightLine() {


        int n = controlPoints.getSize() - 1;
        double step = 0.1;
        addLeftStraight();
        for (int i = 0; i < n; i++) {

            MyPoint2D p1 = controlPoints.getPoint(i);
            MyPoint2D p2 = controlPoints.getPoint(i + 1);
            addLine(step, p1, p2);
        }
        //补直线
        addRightStraight(n);
        DrawCurve();
    }

    private void addLine(double step, MyPoint2D p1, MyPoint2D p2) {
        double step1=step/(255.0*Tools2D.Xdistance(p1.getX(),p2.getX())/CANVAS_WIDTH);
        if(step1< 1.0/255.0){
            step1=1.0/255.0;
        };
        for (double t = 0; t < 1; t += step1) {
            MyPoint2D point = new MyPoint2D(t*(p2.getX()-p1.getX())+p1.getX(),t*(p2.getY()-p1.getY())+p1.getY());
            curvePoints.add(point);
        }
    }

    private void DrawCurve() {
        ResultLUT=new LUT();
        double[] curveXPoints = new double[curvePoints.size()];
        double[] curveYPoints = new double[curvePoints.size()];
        for (int i = 0; i < curvePoints.size(); i++) {
            MyPoint2D point = curvePoints.get(i);
            curveXPoints[i] = point.getX()>255?255:point.getX();
            curveXPoints[i] = point.getX()<0?0:point.getX();
            curveYPoints[i] = point.getY()>255?255:point.getY();
            curveYPoints[i] = point.getY()<0?0:point.getY();
            int x=(int)((point.getX()/CANVAS_WIDTH)*255.0);
            int y=(int)((1-point.getY()/CANVAS_WIDTH)*255.0);
            ResultLUT.addXToY(x,y);
        }
        if (currentTask == null || currentTask.isDone()) {
            // 如果当前任务为空或者已经完成，则提交新任务
            currentTask = executor.submit(SplineBrightnessAdjustment::applyLUTToImage);
        }else{
            currentTask.cancel(true);
            currentTask = executor.submit(SplineBrightnessAdjustment::applyLUTToImage);
        }

        gc.strokePolyline(curveXPoints, curveYPoints, curvePoints.size());
        curvePoints.clear();
    }

    private void drawGrids(){
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(Grid_THICKNESS);
        double gridWidth = CANVAS_WIDTH / 4;
        double gridHeight =CANVAS_WIDTH / 4;
        for (int i = 0; i < 5; i++) {
            double x = i * gridWidth;
            double y = i * gridHeight;
            gc.strokeLine(x, 0, x, CANVAS_WIDTH );
            gc.strokeLine(0, y, CANVAS_WIDTH , y);
        }
    }
    private void drawOutLine(){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(Grid_THICKNESS);
        gc.strokeLine(0, CANVAS_WIDTH, CANVAS_WIDTH, CANVAS_WIDTH );
        gc.strokeLine(0, 0, 0, CANVAS_WIDTH );
        gc.strokeLine(CANVAS_WIDTH, 0, CANVAS_WIDTH , CANVAS_WIDTH);
        gc.strokeLine(0, 0, CANVAS_WIDTH , 0);

    }
    private void drawControlPoints() {
        for (MyPoint2D point : controlPoints.getPoints()) {
            drawControlPoint(point);
        }
    }

    private void drawControlPoint(MyPoint2D point) {
        gc.setFill(CONTROL_POINT_COLOR);
        gc.fillOval(point.getX() - CANVAS_POINT_RADIUS, point.getY() - CANVAS_POINT_RADIUS, 2*CANVAS_POINT_RADIUS, 2*CANVAS_POINT_RADIUS);
    }


    private void drawSplineCurve(){

        int n = controlPoints.getSize();//控制点的个数

        double[] x = new double[n];
        double[] y = new double[n];
        int m= (int) (controlPoints.getPoint(n-1).getX()-controlPoints.getPoint(0).getX());
        double[] t=new double[m]; // 参数t
        double[] z=new double[m]; // 样条函数值
        for ( int i = 0; i< m; ++i ) {
            t[i] = i;
        }
        for (int i = 0; i < n; i++) {
            x[i] = controlPoints.getPoint(i).getX();
            y[i] = controlPoints.getPoint(i).getY();
        }

        double[] dy = new double[n];
        double[] ddy = new double[n];
        double[] s = new double[n];

        // 初始化dy[0]，对于第一个节点 i=0
        dy[0] = -0.5;

        // 计算dy表示样条曲线在每个节点处的一阶导数
        // s表示每个控制点之间的水平距离
        double h0 = x[1] - x[0];
        s[0] = 3.0 * (y[1] - y[0]) / (2.0 * h0) - ddy[0] * h0 / 4.0;
        double h1=1.0;
        for( int j = 1; j <= n - 2; ++j )
        {
            h1 = x[j + 1] - x[j];
            double alpha = h0 / (h0 + h1);
            double beta = (1.0 - alpha) * (y[j] - y[j - 1]) / h0;
            beta = 3.0 * (beta + alpha * ( y[j + 1] - y[j] ) / h1);
            dy[j] = -alpha / (2.0 + (1.0 - alpha) * dy[j - 1]);
            s[j] = (beta - (1.0 - alpha) * s[j - 1]);
            s[j] = s[j] / (2.0 + (1.0 - alpha) * dy[j - 1]);
            h0 = h1;
        }

        dy[n-1] = (3.0*(y[n-1] - y[n-2]) / h1 + ddy[n-1] * h1/2.0 - s[n-2]) / (2.0 + dy[n-2]);
        for( int j = n - 2; j >= 0; --j )
        {
            dy[j] = dy[j] * dy[j + 1] + s[j];
        }
        for( int j = 0; j <= n - 2; ++j )
        {
            s[j] = x[j + 1] - x[j];
        }
        for( int j = 0; j <= n - 2; ++j )
        {
            h1 = s[j] * s[j];
            ddy[j] = 6.0 * (y[j+1] - y[j]) / h1 - 2.0 * (2.0 * dy[j] + dy[j+1]) / s[j];
        }
        h1 = s[n-2] * s[n-2];
        ddy[n-1] = 6.0 * (y[n-2] - y[n-1]) / h1 + 2.0 * (2.0 * dy[n-1] + dy[n-2]) / s[n-2];

        double g = 0.0;
        for(int i=0; i<=n-2; i++)
        {
            h1 = 0.5 * s[i] * (y[i] + y[i+1]);
            h1 = h1 - s[i] * s[i] * s[i] * (ddy[i] + ddy[i+1]) / 24.0;
            g = g + h1;
        }

        for(int j=0; j<=m-1; j++)
        {
            int i;
            if( t[j] >= x[n-1] ) {
                i = n - 2;
            } else {
                i = 0;
                while(t[j] > x[i+1]) {
                    i = i + 1;
                }
            }
            h1 = (x[i+1] - t[j]) / s[i];
            h0 = h1 * h1;
            z[j] = (3.0 * h0 - 2.0 * h0 * h1) * y[i];
            z[j] = z[j] + s[i] * (h0 - h0 * h1) * dy[i];
            h1 = (t[j] - x[i]) / s[i];
            h0 = h1 * h1;
            z[j] = z[j] + (3.0 * h0 - 2.0 * h0 * h1) * y[i+1];
            z[j] = z[j] - s[i] * (h0 - h0 * h1) * dy[i+1];
        }
        addLeftStraight();
        for(int i=0;i<m;i++){
            if(t[i]>controlPoints.getPoint(0).getX()&&t[i]<controlPoints.getPoint(n-1).getX()){
                curvePoints.add(new MyPoint2D(t[i],CLIP_RANGE(z[i],0,255)));
            }
        }
        addRightStraight(n-1);
        DrawCurve();

}
    private  double CLIP_RANGE(double value, int min, int max) {
    return (value) > (max) ? (max) :  (((value) < (min)) ? (min) : (value));
}
    private void addRightStraight(int n) {


        if(controlPoints.getPoint(n).getX()<CANVAS_WIDTH){
            MyPoint2D p1 =  controlPoints.getPoint(n);
            MyPoint2D p2 =new MyPoint2D(CANVAS_WIDTH,controlPoints.getPoint(n).getY());
            addLine(step, p1, p2);
        }
        curvePoints.add(controlPoints.getPoint(n));

    }
    private void addLeftStraight() {

        //补左边直线
        if(controlPoints.getPoint(0).getX()>0){
            MyPoint2D p1 = new MyPoint2D(0,controlPoints.getPoint(0).getY());
            MyPoint2D p2 = controlPoints.getPoint(0);
            addLine(step, p1, p2);
        }

    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }

    public static void setResultLUT(LUT resultLUT) {
        ResultLUT = resultLUT;
    }
}
