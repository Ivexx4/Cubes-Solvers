package pt.cubesolvers.ui;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import pt.cubesolvers.model.Cube;
import pt.cubesolvers.solver.Solver2x2;
import pt.cubesolvers.solver.Solver3x3;
import pt.cubesolvers.solver.Solver4x4;
import pt.cubesolvers.solver.Solver5x5;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class CubeApplicationView extends Application {
    private static final char[][] AXES = {{'U', 'D'}, {'F', 'B'}, {'L', 'R'}};
    private static final Color[] COLORS = {
            Color.WHITE, Color.YELLOW, Color.GREEN,
            Color.BLUE, Color.ORANGE, Color.RED
    };

    private final Canvas canvas = new Canvas(700, 480);
    private final TextField sequenceInput = new TextField();
    private final Label status = new Label("Ready");
    private final ComboBox<Integer> sizeSelector = new ComboBox<>();
    private Cube cube = new Cube(3);

    @Override
    public void start(Stage stage) {
        stage.setTitle("Rubik's Cube Solvers");
        stage.setMinWidth(620);
        stage.setMinHeight(560);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createControls());
        root.setCenter(canvas);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(8, 0, 0, 4));

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> drawCube());
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> drawCube());
        stage.setScene(new Scene(root, 820, 650));
        stage.show();
        scramble();
    }

    private VBox createControls() {
        sequenceInput.setPromptText("Sequence, e.g. Rw U' 3Fw2");
        sequenceInput.setOnAction(event -> runSequence());
        Button run = new Button("Execute");
        run.setOnAction(event -> runSequence());

        sizeSelector.getItems().addAll(2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        sizeSelector.setValue(3);
        sizeSelector.setOnAction(event -> {
            Integer newSize = sizeSelector.getValue();
            if (newSize != null && newSize != cube.size()) {
                cube = new Cube(newSize);
                status.setText("Cube changed to " + newSize + "x" + newSize);
                drawCube();
            }
        });

        HBox sequenceRow = new HBox(8, new Label("Size:"), sizeSelector, sequenceInput, run);
        sequenceRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sequenceInput, Priority.ALWAYS);

        GridPane moveButtons = new GridPane();
        moveButtons.setHgap(5);
        moveButtons.setVgap(5);
        moveButtons.setAlignment(Pos.CENTER);
        char[] faces = {'U', 'F', 'R', 'L', 'B', 'D'};
        for (int index = 0; index < faces.length; index++) {
            char face = faces[index];
            int row = index / 3;
            int column = (index % 3) * 3;
            addMoveButton(moveButtons, String.valueOf(face), row, column);
            addMoveButton(moveButtons, face + "'", row, column + 1);
            addMoveButton(moveButtons, face + "2", row, column + 2);
        }

        Button shuffle = new Button("Shuffle");
        shuffle.setOnAction(event -> scramble());
        Button solve = new Button("Find solution");
        solve.setOnAction(event -> findSolution(solve));
        HBox actionRow = new HBox(8, shuffle, solve);
        actionRow.setAlignment(Pos.CENTER);

        VBox controls = new VBox(8, sequenceRow, moveButtons, actionRow);
        controls.setPadding(new Insets(0, 0, 8, 0));
        return controls;
    }

    private void addMoveButton(GridPane grid, String move, int row, int column) {
        Button button = new Button(move);
        button.setMinWidth(58);
        button.setOnAction(event -> applyMove(move));
        grid.add(button, column, row);
    }

    private void runSequence() {
        String sequence = sequenceInput.getText().trim();
        if (sequence.isEmpty()) {
            return;
        }
        try {
            cube.applySequence(sequence);
            sequenceInput.clear();
            status.setText("Executed: " + sequence);
            redrawAndCheckSolved();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void applyMove(String move) {
        try {
            cube.applyMove(move);
            status.setText("Executed: " + move);
            redrawAndCheckSolved();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void scramble() {
        List<String> moves = new ArrayList<>();
        int count = 20 + (cube.size() - 2) * 10;
        int previousAxis = -1;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int index = 0; index < count; index++) {
            int axis;
            do {
                axis = random.nextInt(AXES.length);
            } while (axis == previousAxis);
            previousAxis = axis;

            char face = AXES[axis][random.nextInt(2)];
            String move = String.valueOf(face);
            if (cube.size() >= 4 && random.nextBoolean()) {
                int depth = random.nextInt(2, cube.size() / 2 + 2);
                if (depth > 2) {
                    move = depth + move + "w";
                } else {
                    move += "w";
                }
            }
            int modifier = random.nextInt(3);
            if (modifier == 1) {
                move += "'";
            } else if (modifier == 2) {
                move += "2";
            }
            moves.add(move);
        }

        cube = new Cube(cube.size());
        String scramble = String.join(" ", moves);
        cube.applySequence(scramble);
        status.setText("Scramble: " + scramble);
        drawCube();
    }

    private void findSolution(Button solveButton) {
        if (cube.size() < 2 || cube.size() > 5) {
            showError("Solvers are available only for 2x2, 3x3, 4x4 and 5x5 cubes.");
            return;
        }
        solveButton.setDisable(true);
        status.setText("Searching for a solution...");
        Cube snapshot = cube.copy();
        Task<Optional<List<String>>> task = new Task<>() {
            @Override
            protected Optional<List<String>> call() {
                return switch (snapshot.size()) {
                    case 2 -> new Solver2x2(snapshot).solve();
                    case 3 -> new Solver3x3(snapshot).solve();
                    case 4 -> new Solver4x4(snapshot).solve();
                    case 5 -> new Solver5x5(snapshot).solve();
                    default -> throw new IllegalStateException("Unsupported cube size.");
                };
            }
        };
        task.setOnSucceeded(event -> {
            solveButton.setDisable(false);
            Optional<List<String>> solution = task.getValue();
            if (solution.isEmpty()) {
                status.setText("No solution found within the configured search limits.");
            } else {
                String text = String.join(" ", solution.get());
                sequenceInput.setText(text);
                status.setText("Solution (" + solution.get().size() + " moves); press Execute to apply.");
            }
        });
        task.setOnFailed(event -> {
            solveButton.setDisable(false);
            Throwable error = task.getException();
            showError(error == null ? "The solver failed." : error.getMessage());
        });
        Thread solverThread = new Thread(task, "cube-solver");
        solverThread.setDaemon(true);
        solverThread.start();
    }

    private void redrawAndCheckSolved() {
        drawCube();
        if (cube.isSolved()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Solved");
            alert.setHeaderText(null);
            alert.setContentText("The " + cube.size() + "x" + cube.size() + " cube is solved.");
            alert.showAndWait();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Cube error");
        alert.setHeaderText(null);
        alert.setContentText(message == null ? "An unexpected error occurred." : message);
        alert.showAndWait();
    }

    private void drawCube() {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.rgb(32, 32, 32));
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double cell = Math.min(canvas.getWidth() / (4.0 * cube.size()),
                canvas.getHeight() / (3.0 * cube.size())) * 0.94;
        double faceSize = cell * cube.size();
        double left = (canvas.getWidth() - faceSize * 4) / 2;
        double top = (canvas.getHeight() - faceSize * 3) / 2;
        drawFace(graphics, 'U', left + faceSize, top, cell);
        drawFace(graphics, 'L', left, top + faceSize, cell);
        drawFace(graphics, 'F', left + faceSize, top + faceSize, cell);
        drawFace(graphics, 'R', left + faceSize * 2, top + faceSize, cell);
        drawFace(graphics, 'B', left + faceSize * 3, top + faceSize, cell);
        drawFace(graphics, 'D', left + faceSize, top + faceSize * 2, cell);
    }

    private void drawFace(GraphicsContext graphics, char face, double x, double y, double cell) {
        for (int row = 0; row < cube.size(); row++) {
            for (int column = 0; column < cube.size(); column++) {
                graphics.setFill(COLORS[cube.colorAt(face, row, column)]);
                graphics.fillRect(x + column * cell, y + row * cell, cell, cell);
                graphics.setStroke(Color.BLACK);
                graphics.setLineWidth(1.5);
                graphics.strokeRect(x + column * cell, y + row * cell, cell, cell);
            }
        }
    }

}
