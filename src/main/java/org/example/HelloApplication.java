package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HelloApplication extends Application {
    public static final double WIDTH = 960,HEIGHT = 640;

    public static class Director {
        //单例模式
        private static final Director instance = new Director();

        private Director() {}
        //获取当前类的单例
        public static Director getInstance(){
            return instance;
        }

    }
    @Override
    public void start(Stage stage) {
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root,WIDTH,HEIGHT);
        stage.setTitle("坦克");
        String resource=getClass().getResource("/image/icon.png").toString();
        stage.getIcons().add(new Image(resource));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}