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

    public static void addHistory(String adjustProperty,double adjustPercentage){
        adjustHistory.put(adjustProperty,adjustPercentage);
    }
}
