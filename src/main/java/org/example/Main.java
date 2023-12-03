package org.example;

import io.vproxy.vfx.theme.Theme;
import javafx.application.Application;
import org.example.Theme.ThemeSet;

public class Main  {

    public static void main(String[] args) {
        Theme.setTheme(new ThemeSet());
        Application.launch(LSMain.class);
    }
}