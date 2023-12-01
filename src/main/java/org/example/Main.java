package org.example;

import io.vproxy.vfx.theme.Theme;
import javafx.scene.image.Image;
import org.example.Theme.ThemeSet;

public class Main {
    public static void main(String[] args) {

        Theme.setTheme(new ThemeSet());
        Image image=new Image("image/icon.png");

    }
}