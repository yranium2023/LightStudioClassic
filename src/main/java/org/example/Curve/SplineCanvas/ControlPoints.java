package org.example.Curve.SplineCanvas;


import java.util.LinkedList;
import java.util.ListIterator;

public class ControlPoints {


    final MyPoint2D LeftBottom;
    final MyPoint2D RightTop;
    final double MIN_DISTANCE_BETWEEN_POINTS;

    private  LinkedList<MyPoint2D> points;

    public LinkedList<MyPoint2D> getPoints() {
        return points;
    }
    public MyPoint2D getPoint(int index ){
        return points.get(index);
    }

    public ControlPoints(MyPoint2D start, MyPoint2D end, double minDistanceBetweenPoints){
        this.MIN_DISTANCE_BETWEEN_POINTS = minDistanceBetweenPoints;
        points=new LinkedList<>();
        points.add(start);
        points.add(end);
        LeftBottom=new MyPoint2D(start.getX(),start.getY());
        RightTop=new MyPoint2D(end.getX(),end.getY());
    }
    public int getSize(){
        return points.size();
    }
    //按x轴从左->右进行插入
    private boolean XsamePoint(MyPoint2D point1,MyPoint2D point2){
            return Tools2D.Xdistance(point1.getX(),point2.getX())<MIN_DISTANCE_BETWEEN_POINTS;
    }
    private boolean XYsamePoint(MyPoint2D point1,MyPoint2D point2){
        return Tools2D.distance(point1.getX(),point1.getY(),point2.getX(),point2.getY())<MIN_DISTANCE_BETWEEN_POINTS;
    }
    public int selectPoint(MyPoint2D point){
        ListIterator<MyPoint2D> iterator=points.listIterator();
        while (iterator.hasNext()) {
            MyPoint2D current = iterator.next();
            if(XYsamePoint(point,current)){
                return iterator.nextIndex() - 1;
            }

        }
        return -1;
    }
    public boolean addPoint(MyPoint2D point){
        if(points.size()<=1){
            if(points.size()<=0){
                points.add(point);
            }else {
                if (points.get(0).getX()>point.getX()){
                    points.add(0,point);
                }else {
                    points.add(1,point);
                }
            }
            return true;
        }else {
            MyPoint2D LastPont=LeftBottom;
            ListIterator<MyPoint2D> iterator=points.listIterator();
            while (iterator.hasNext()) {
                MyPoint2D current = iterator.next();
                //找到位置
                if(current.getX()>point.getX()){
                    //如果左右位置不合适，返回，不添加
                    if(XsamePoint(current,point)||XsamePoint(LastPont,point)){
                        return false;
                    }
                    iterator.previous();
                    iterator.add(point);
                    return true;
                }
                LastPont=current;
            }
        }
        return false;
    }
    public boolean removePoint(MyPoint2D tryPoint){
        ListIterator<MyPoint2D> iterator=points.listIterator();
        if(points.size()==2){
            while (iterator.hasNext()) {
                MyPoint2D current = iterator.next();
                if(XYsamePoint(tryPoint,current)){
                    if(iterator.hasNext()){
                        points.remove(current);
                        addPoint(new MyPoint2D(LeftBottom.getX(), LeftBottom.getY()));
                    }else {
                        points.remove(current);
                        addPoint(new MyPoint2D(RightTop.getX(),RightTop.getY()));

                    }
                    return true;
                }

            }
        }
        while (iterator.hasNext()) {
            MyPoint2D current = iterator.next();
            if(XYsamePoint(tryPoint,current)){

               points.remove(current);
               return true;
            }

        }
        return false;
    }
    public boolean movePoint(int id,MyPoint2D toPoint){
        double MIN_DIS=1.0;
        if(id<0||id>=points.size()||Tools2D.distance(points.get(id).getX(),toPoint.getX(),points.get(id).getY(),toPoint.getY())<MIN_DIS){
            return  false;
        }
        //确保不出边界
            if (toPoint.getX()>RightTop.getX()){
                toPoint.setX(RightTop.getX());
            }
            if(toPoint.getX()<LeftBottom.getX()){
                toPoint.setX(LeftBottom.getX());
            }
            if(toPoint.getY()< RightTop.getY()){
                toPoint.setY(RightTop.getY());
            }
            if(toPoint.getY()> LeftBottom.getY()){
                toPoint.setY(LeftBottom.getY());
            }
            //不过右边点
        if(id!= points.size()-1){
            if(toPoint.getX()+MIN_DISTANCE_BETWEEN_POINTS>points.get(id+1).getX()){
                toPoint.setX(points.get(id+1).getX()-MIN_DISTANCE_BETWEEN_POINTS);
            }
        }
        //不过左边点
        if(id>0){
            if(toPoint.getX()+MIN_DISTANCE_BETWEEN_POINTS<points.get(id-1).getX()){
                toPoint.setX(points.get(id-1).getX()+MIN_DISTANCE_BETWEEN_POINTS);
            }
        }

        points.get(id).setX(toPoint.getX());
        points.get(id).setY(toPoint.getY());

        return true;
    }



}
