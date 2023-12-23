package org.example.Obj;

import org.example.Curve.SplineCanvas.LUT;
import org.example.Scene.EditHistoryScene;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;

/**
 *  本类实现历史记录
 * @author 申雄全
 * @author 吴鹄远
 * Date 2023/12/13 21:46
 */
public class AdjustHistory implements Serializable {

    private String adjustProperty;

    private long time;

    private double [] adjustValue;
    private LUT  LUTValue;
    public AdjustHistory(String adjustProperty,double... adjustValue){
        this.adjustProperty=adjustProperty;
        this.time=System.currentTimeMillis();
        this.adjustValue=adjustValue;
    }

    public AdjustHistory(String adjustProperty,LUT LUTValue){
        this.adjustProperty=adjustProperty;
        this.time=System.currentTimeMillis();
        this.LUTValue=LUTValue;
    }
    /**
     *   拷贝构造，仅仅用作显示历史记录
     * @param other
     * @author 吴鹄远
     * Date 2023/12/18 17:01
    **/

    public AdjustHistory(AdjustHistory other) {
        this.adjustProperty = other.adjustProperty;
        this.time = other.time;

        // 复制数组或对象，以防止引用问题
        if (other.adjustValue != null) {
            this.adjustValue = Arrays.copyOf(other.adjustValue, other.adjustValue.length);
        }

    }




    public long getTime() {
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

    public void setAdjustProperty(String adjustProperty) {
        this.adjustProperty = adjustProperty;
    }
}
