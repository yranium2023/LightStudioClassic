package org.example.Obj;

import org.example.Curve.SplineCanvas.LUT;

import java.time.LocalTime;
import java.util.*;

/**
 * @author 申雄全
 * @Description 本类实现历史记录
 * @date 2023/12/13 21:46
 */
public class AdjustHistory {

    private String adjustProperty;//类型

    private LocalTime time;//时间

    private double [] adjustValue;//对于HSL存在两个值
    private LUT  LUTValue;//对于曲线
    //每次调用历史记录，只要对相应的value进行赋值，设置相应的bufferimage和processedimage，后面直接调用每个调整类中的adjust的函数，函数执行完后获取相应类的processedimage就是我们需要的
    public AdjustHistory(String adjustProperty,LocalTime time,double... adjustValue){
        this.adjustProperty=adjustProperty;
        this.time=time;
        this.adjustValue=adjustValue;
    }

    public AdjustHistory(String adjustProperty,LocalTime time,LUT LUTValue){
        this.adjustProperty=adjustProperty;
        this.time=time;
        this.LUTValue=LUTValue;
    }
    private static final Stack<AdjustHistory> adjustHistory = new Stack<>();

    //如果不知道adjustProperty值，请直接点击方法的8个用法进行查看！！！
    public static void addHistory(AdjustHistory History){
        adjustHistory.push(History);
    }
    /**
     * @describle 获得调整的历史记录，用key值判断进行的调整类型，用value判断调整的value值
     * @author 申雄全
     * @updateTime 2023/12/13 22:23
     */
    public static Stack<AdjustHistory> getAdjustHistory() {
        return adjustHistory;
    }
    public LocalTime getTime() {
        return time;
    }
    public String getAdjustProperty() {
        return adjustProperty;
    }
    public double getFirstValue() {
        return adjustValue[0];
    }
    public LUT getLUTValue(){
        return LUTValue;
    }
    public double getSecondValue() {
        return adjustValue[1];
    }
}
