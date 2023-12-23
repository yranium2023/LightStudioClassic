package org.example;

import io.vproxy.vfx.theme.Theme;
import javafx.application.Application;
import org.example.Theme.ThemeSet;
/**
 *  该类为程序运行主类
 * @author 申雄全
 * Date 2023/12/24 1:53
 */
public class Main  {

    public static void main(String[] args) {
        Theme.setTheme(new ThemeSet());
        Application.launch(LSMain.class);
    }
}