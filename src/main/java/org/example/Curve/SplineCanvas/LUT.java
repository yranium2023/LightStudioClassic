package org.example.Curve.SplineCanvas;


import java.io.Serializable;
import java.util.Arrays;
/**
 * @Description 表示查找表，用于存储曲线调整的结果，实现曲线到rgb值的映射。
 * @author 申雄全
 * @date 2023/12/20 23:02
 */
public class LUT implements Serializable {

    private static final long serialVersionUID = 1L;
    private int[] Curve;
    public LUT(){
        Curve= new int[256];
        Arrays.fill(Curve, -1);
    }
    /**
     * @Description 将 x 映射到 y，并将结果存储在数组中
     * @param x
     * @param y
     * @author 申雄全
     * @date 2023/12/23 23:37
     */
    public void addXToY(int x,int y){
        x=x>255?255:x;
        x=x<0?0:x;
        y=y>255?255:y;
        y=y<0?0:y;
        Curve[x]=y;
    }
    public int getY(int x){
        return Curve[x];
    }
    /**
     * @Description 检查数组中的映射，确保没有未定义的值
     * @author 申雄全
     * @date 2023/12/23 23:36
     */
    public void checkLUT(){

        if(Curve[0]==-1){
            Curve[0]=0;
        }
        if(Curve[255]==-1){
            Curve[255]=255;
        }
        for(int i=0;i<=255;i++){

            if(Curve[i]==-1){
                for(int j=0;j+i<=255;j++){
                    if(Curve[j+i]!=-1){
                        Curve[i]= (int) (Curve[i-1]+(Curve[j+i]-Curve[i-1])*(1.0/(j+1.0)));
                    }
                }
            }

        }

    }

}
