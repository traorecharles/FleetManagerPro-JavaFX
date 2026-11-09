package com.miage.fleet.util;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ToastUtil {

    public static void show(StackPane overlay, String message) {
        overlay.getChildren().clear();
        overlay.setVisible(false);
        overlay.setManaged(false);

        Label toast = new Label("✓  " + message);
        toast.setStyle(
            "-fx-background-color: #10b981; -fx-text-fill: white; " +
            "-fx-padding: 12 24; -fx-background-radius: 12; " +
            "-fx-font-size: 13px; -fx-font-weight: 600;"
        );
        toast.setMouseTransparent(true);
        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        StackPane.setMargin(toast, new Insets(0, 0, 32, 0));

        overlay.getChildren().add(toast);
        overlay.setMouseTransparent(true);
        overlay.setStyle("-fx-background-color: transparent;");
        overlay.setVisible(true);

        PauseTransition pt = new PauseTransition(Duration.seconds(2.5));
        pt.setOnFinished(e -> {
            overlay.setVisible(false);
            overlay.setMouseTransparent(false);
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
            overlay.getChildren().clear();
        });
        pt.play();
    }
}
