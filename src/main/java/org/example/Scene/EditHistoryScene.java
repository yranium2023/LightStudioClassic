package org.example.Scene;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.util.FXUtils;
import io.vproxy.vfx.util.MiscUtils;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import org.example.Curve.SplineCanvas.SplineBrightnessAdjustment;
import org.example.Curve.SplineCanvas.SplineCanvas;
import org.example.HSL.HSLColorAdjustment;
import org.example.ImageModification.ImageContrastAdjustment;
import org.example.ImageModification.ImageExposureAdjustment;
import org.example.ImageModification.ImageSaturationAdjustment;
import org.example.ImageModification.ImageTemperatureAdjustment;
import org.example.Obj.AdjustHistory;
import org.example.Obj.HSLColor;
import org.example.Obj.ImageObj;
import org.example.StaticValues;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 吴鹄远
 * @Description 这个场景用于显示图像编辑的历史记录，并添加撤回、重做操作等
 * @date 2023/12/17 10:50
 */
public class EditHistoryScene extends SuperScene{
    //创建一个表格用于显示历史记录
    public static VTableView historyTable=new VTableView<AdjustHistory>(){{
        getNode().setLayoutY(50);
       getNode().setPrefHeight(600);
       getNode().setPrefWidth(320);
    }};
    public static VTableColumn<AdjustHistory, String> labelCol=new VTableColumn<AdjustHistory,String>("修改内容", data->data.getAdjustProperty());
    public static VTableColumn<AdjustHistory, ZonedDateTime>timeCol=new VTableColumn<AdjustHistory, ZonedDateTime>("修改时间",data->
            ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(data.getTime()), ZoneId.systemDefault()
            ));
    public EditHistoryScene(){
        super(VSceneRole.DRAWER_VERTICAL);
        getNode().setPrefWidth(350);
        enableAutoContentWidth();
        getNode().setBackground(new Background(new BackgroundFill(
                Theme.current().subSceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        labelCol.setPrefWidth(170);
        historyTable.getColumns().addAll(labelCol,timeCol);
        FXUtils.observeWidthCenter(getContentPane(),historyTable.getNode());
        timeCol.setTextBuilder(MiscUtils.YYYYMMddHHiissDateTimeFormatter::format);
        getContentPane().getChildren().add(historyTable.getNode());



    }
    /**
     * @Description 该类实现历史记录的调整
     * @param history
     * @param tableView
     * @author 申雄全
     * @date 2023/12/23 23:30
     */
    public static void addLabel(AdjustHistory history,VTableView tableView){

        var key=history.getAdjustProperty();
        var newHis=new AdjustHistory(history);
        System.out.println(key);
        if ("点曲线调整".equals(key)) {
            newHis.setAdjustProperty(key);
        } else if ("对比度调整".equals(key)) {
            newHis.setAdjustProperty(key+" "+String.format("%.6f",newHis.getFirstValue()));
        } else if ("饱和度调整".equals(key)) {
            newHis.setAdjustProperty(key+" "+String.format("%.6f",newHis.getFirstValue()));
        } else if ("曝光度调整".equals(key)) {
            newHis.setAdjustProperty(key+" "+String.format("%.6f",newHis.getFirstValue()));
        } else if ("色温调整".equals(key)) {
            newHis.setAdjustProperty(key+" "+String.format("%.2f",newHis.getFirstValue()));
        } else if ("HSL色相调整".equals(key.substring(0,key.length()-1))) {
            int index=extractNumber(key);
            HSLColor color1=getColor(index);
            newHis.setAdjustProperty("HSL色相调整 "+color1);
        } else if ("HSL饱和度调整".equals(key.substring(0,key.length()-1))) {
            int index=extractNumber(key);
            HSLColor color1=getColor(index);
            newHis.setAdjustProperty("HSL饱和度调整 "+color1);
        } else if ("HSL明度调整".equals(key.substring(0,key.length()-1))) {
            int index=extractNumber(key);
            HSLColor color1=getColor(index);
            newHis.setAdjustProperty("HSL明度调整 "+color1);
        }
        tableView.getItems().add(newHis);
    }
    /**
     * @Description  截取正则表达式中的数字
     * @param input
     * @return int
     * @author 吴鹄远
     * @date 2023/12/18 17:08
    **/

    private static int extractNumber(String input) {
        // 定义匹配数字的正则表达式
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        // 查找匹配的数字
        if (matcher.find()) {
            // 将找到的数字字符串转换为整数
            return Integer.parseInt(matcher.group());
        } else {
            // 如果没有找到数字，可以返回默认值或抛出异常，这里返回 -1 作为示例
            return -1;
        }
    }
    public static HSLColor getColor(int index){
        HSLColor[] values=HSLColor.values();
        return values[index];
    }
    /**
     * @Description  在切换所选对象时更新表
     * @author 吴鹄远
     * @date 2023/12/18 19:24
    **/

    public static void renewEditHistoryScene(){
        ImageObj editingImageObj= StaticValues.editingImageObj;
        historyTable.getItems().clear();
        if(editingImageObj!=null&&!editingImageObj.getAdjustHistory().isEmpty()){
            for(var history:editingImageObj.getAdjustHistory()){
                addLabel(history,historyTable);
            }
        }
    }
    @Override
    public String title() {
        return "EditHistory";
    }
}
