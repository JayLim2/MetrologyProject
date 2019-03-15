package ru.ssau.metrology.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.ssau.metrology.Main;

import java.io.IOException;

public class Forms {
    public static Stage getForm(String name, String title) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/" + name + ".fxml"));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle(title);
        return stage;
    }
}
