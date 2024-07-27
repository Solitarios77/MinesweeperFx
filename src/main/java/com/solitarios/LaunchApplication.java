package com.solitarios;


import com.solitarios.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LaunchApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("font/DS-Digital.ttf"),14);
        FXMLLoader fxmlLoader = new FXMLLoader(LaunchApplication.class.getResource("view/main-view.fxml"));
        fxmlLoader.setControllerFactory(t -> new MainController(stage));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MinesweeperFx");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
