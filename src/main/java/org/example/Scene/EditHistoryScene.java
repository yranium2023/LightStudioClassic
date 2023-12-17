package org.example.Scene;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import org.example.Obj.AdjustHistory;

/**
 * @author 吴鹄远
 * @Description 这个场景用于显示图像编辑的历史记录，并添加撤回、重做操作等
 * @date 2023/12/17 10:50
 */
public class EditHistoryScene extends SuperScene{
    //创建一个表格用于显示历史记录
    public static VTableView historyTable=new VTableView<AdjustHistory>(){{
       getNode().setPrefHeight(600);
       getNode().setPrefWidth(320);
    }};
    public EditHistoryScene(){
        super(VSceneRole.DRAWER_VERTICAL);
        getNode().setPrefWidth(350);
        enableAutoContentWidth();
        getNode().setBackground(new Background(new BackgroundFill(
                Theme.current().subSceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        var labelCol=new VTableColumn<AdjustHistory,String>("修改内容",)




    }
    @Override
    public String title() {
        return "EditHistory";
    }
}
