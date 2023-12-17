package org.example.Curve.SplineCanvas;


import java.io.Serializable;
import java.util.Arrays;

public class LUT implements Serializable {

    private static final long serialVersionUID = 1L;
    private int[] Curve;
    public LUT(){
        Curve= new int[256];
        Arrays.fill(Curve, -1);
    }
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
