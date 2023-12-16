package org.example.Obj;

import java.util.*;

/**
 * @author 申雄全
 * @Description 本类实现历史记录
 * @date 2023/12/13 21:46
 */
public class AdjustHistory {
    private static final Stack<Map.Entry<String, String>> adjustHistory = new Stack<>();

    public static void addHistory(String adjustProperty,String adjustValue){
        adjustHistory.push(new HashMap.SimpleEntry<>(adjustProperty,adjustValue));
        //说明
        //对于adjustProperty，加入的是类型判断字符传，比如“HSL色相调整”这类字符串，对于Value，就是算法中需要用到的关键值
        //value比较特殊的有hsl和曲线，曲线中，存的value为x+“ ”+y，hsl中，存的value为selectedcolor+“ ”+selectedproperty，其余均可直接赋值为value，同时x和y是int类型，其余均为double类型。
        //对于曲线的调整请获得x，y后，对LUT进行操作，调用addXoY函数！！！关键是通x和y获得LUT！！！
        //所以每次调用历史记录，只要对相应的value进行赋值，再改变image，调用相应的modification函数即可。
    }
    /**
     * @describle 获得调整的历史记录，用key值判断进行的调整类型，用value判断调整的value值
     * @author 申雄全
     * @updateTime 2023/12/13 22:23
     */
    public static Stack<Map.Entry<String, String>> getAdjustHistory() {
        return adjustHistory;
    }
}
