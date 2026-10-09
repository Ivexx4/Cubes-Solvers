package pt.cubesolvers.ui;

import javafx.application.Application;

public final class CubeApplication {
    private CubeApplication() {
    }

    public static void main(String[] args) {
        Application.launch(CubeApplicationView.class, args);
    }
}
