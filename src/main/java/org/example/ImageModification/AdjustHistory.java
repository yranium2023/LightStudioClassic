package org.example.ImageModification;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author 申雄全
 * @Description 本类实现历史记录
 * @date 2023/12/13 21:46
 */
public class AdjustHistory {
    private static LinkedHashMap<String,Double> adjustHistory=new LinkedHashMap<>();

    public static void addHistory(String adjustProperty,double adjustValue){
        adjustHistory.put(adjustProperty,adjustValue);
    }
    /**
     * @describle 获得调整的历史记录，用key值判断进行的调整类型，用value判断调整的value值
     * @author 申雄全
     * @updateTime 2023/12/13 22:23
     */
    public static LinkedHashMap<String, Double> getAdjustHistory() {
        return adjustHistory;
    }
}
