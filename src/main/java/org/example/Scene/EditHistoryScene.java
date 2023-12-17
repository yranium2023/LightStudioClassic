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
import org.example.Obj.AdjustHistory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
        labelCol.setPrefWidth(130);
        historyTable.getColumns().addAll(labelCol,timeCol);
        FXUtils.observeWidthCenter(getContentPane(),historyTable.getNode());
        timeCol.setTextBuilder(MiscUtils.YYYYMMddHHiissDateTimeFormatter::format);
        getContentPane().getChildren().add(historyTable.getNode());



    }

    public static void addLabel(AdjustHistory history){
        historyTable.getItems().add(history);
    }
    @Override
    public String title() {
        return "EditHistory";
    }
}
