package org.example.Obj;

/**
 * @author 吴鹄远
 * @Description
 * @date 2023/12/16 22:57
 */ //新创一个HSLInfo类用于存hsl
public class HSLInfo {
    private double huePercent = 0.5;
    private double saturationPercent = 0.5;
    private double luminancePercent = 0.5;
    private HSLColor hslColor;

    /**
     * @Description 该类对应HSL的三种类型轴
     * @author 申雄全
     * @date 2023/12/24 1:05
     */
    public enum sliderType{
        /*
         *色相轴
         */
        HUE,
        /*
         *饱和度轴
         */
        SATURATION,
        /*
         *明度轴
         */
        LUMINANCE
    }
    private sliderType nowType=null;

    public HSLInfo(HSLColor hslColor) {
        this.hslColor = hslColor;
    }

    public double getHuePercent() {
        return huePercent;
    }

    public void setHuePercent(double huePercent) {
        this.huePercent = huePercent;
    }

    public double getSaturationPercent() {
        return saturationPercent;
    }

    public void setSaturationPercent(double saturationPercent) {
        this.saturationPercent = saturationPercent;
    }

    public double getLuminancePercent() {
        return luminancePercent;
    }

    public void setLuminancePercent(double luminancePercent) {
        this.luminancePercent = luminancePercent;
    }

    public sliderType getNowType() {
        return nowType;
    }

    public void setNowType(sliderType nowType) {
        this.nowType = nowType;
    }

    public HSLColor getHslColor() {
        return hslColor;
    }

}
