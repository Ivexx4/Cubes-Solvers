package pt.cubesolvers.ui;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

public final class CubeApplicationView extends Application {
    private static final char[][] AXES = {{'U', 'D'}, {'F', 'B'}, {'L', 'R'}};
    private static final Color[] COLORS = {
            Color.WHITE, Color.YELLOW, Color.GREEN,
            Color.BLUE, Color.ORANGE, Color.RED
    };
    private static final double STICKER_DEPTH = 0.035;
    private static final Pattern MOVE_PATTERN = Pattern.compile("^(\\d*)([UDFBLR])(w?)(2|')?$");

    private final Group cubeGroup = new Group();
    private final Rotate rotateX = new Rotate(25, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(35, Rotate.Y_AXIS);
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final PointLight pointLight = new PointLight(Color.WHITE);
    private final PhongMaterial[] stickerMaterials = createStickerMaterials();
    private final PhongMaterial cubieMaterial = new PhongMaterial(Color.rgb(12, 12, 12));
    private final TextField sequenceInput = new TextField();
    private final Label status = new Label("Ready");
    private final ComboBox<Integer> sizeSelector = new ComboBox<>();
    private final List<Button> moveButtons = new ArrayList<>();
    private Cube cube = new Cube(3);
    private SubScene cubeScene;
    private Button runButton;
    private Button shuffleButton;
    private Button solveButton;
    private boolean animationInProgress;
    private double dragX;
    private double dragY;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Rubik's Cube Solvers");
        stage.setMinWidth(620);
        stage.setMinHeight(560);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(createControls());
        root.setCenter(createCubeView());
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(8, 0, 0, 4));

        stage.setScene(new Scene(root, 820, 650));
        stage.show();
        scramble();
    }

    private StackPane createCubeView() {
        cubeGroup.getTransforms().addAll(rotateX, rotateY);
        Group sceneRoot = new Group(
                cubeGroup,
                new AmbientLight(Color.rgb(190, 190, 190)),
                pointLight
        );
        cubeScene = new SubScene(sceneRoot, 700, 480, true, SceneAntialiasing.BALANCED);
        cubeScene.setFill(Color.rgb(32, 32, 32));
        camera.setNearClip(0.1);
        camera.setFarClip(1000);
        camera.setFieldOfView(35);
        cubeScene.setCamera(camera);

        StackPane view = new StackPane(cubeScene);
        cubeScene.widthProperty().bind(view.widthProperty());
        cubeScene.heightProperty().bind(view.heightProperty());
        view.widthProperty().addListener((observable, oldValue, newValue) -> updateCamera());
        view.heightProperty().addListener((observable, oldValue, newValue) -> updateCamera());

        cubeScene.setOnMousePressed(event -> {
            if (!animationInProgress && event.getButton() == MouseButton.PRIMARY) {
                dragX = event.getSceneX();
                dragY = event.getSceneY();
                view.setCursor(Cursor.CLOSED_HAND);
            }
        });
        cubeScene.setOnMouseDragged(event -> {
            if (!animationInProgress && event.isPrimaryButtonDown()) {
                rotateY.setAngle(rotateY.getAngle() + event.getSceneX() - dragX);
                rotateX.setAngle(rotateX.getAngle() - event.getSceneY() + dragY);
                dragX = event.getSceneX();
                dragY = event.getSceneY();
            }
        });
        cubeScene.setOnMouseReleased(event -> view.setCursor(Cursor.OPEN_HAND));
        view.setCursor(Cursor.OPEN_HAND);
        updateCamera();
        return view;
    }

    private void updateCamera() {
        double distance = Math.max(cube.size() * 3.2, 8);
        camera.setTranslateZ(-distance);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        pointLight.setTranslateX(-cube.size() * 1.5);
        pointLight.setTranslateY(-cube.size() * 2);
        pointLight.setTranslateZ(-cube.size() * 2);
    }

    private VBox createControls() {
        sequenceInput.setPromptText("Sequence, e.g. Rw U' 3Fw2");
        sequenceInput.setOnAction(event -> runSequence());
        runButton = new Button("Execute");
        runButton.setOnAction(event -> runSequence());

        sizeSelector.getItems().addAll(2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        sizeSelector.setValue(3);
        sizeSelector.setOnAction(event -> {
            Integer newSize = sizeSelector.getValue();
            if (newSize != null && newSize != cube.size()) {
                cube = new Cube(newSize);
                status.setText("Cube changed to " + newSize + "x" + newSize);
                updateCamera();
                drawCube();
            }
        });

        HBox sequenceRow = new HBox(8, new Label("Size:"), sizeSelector, sequenceInput, runButton);
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

        shuffleButton = new Button("Shuffle");
        shuffleButton.setOnAction(event -> scramble());
        solveButton = new Button("Find solution");
        solveButton.setOnAction(event -> findSolution(solveButton));
        HBox actionRow = new HBox(8, shuffleButton, solveButton);
        actionRow.setAlignment(Pos.CENTER);

        VBox controls = new VBox(8, sequenceRow, moveButtons, actionRow);
        controls.setPadding(new Insets(0, 0, 8, 0));
        return controls;
    }

    private void addMoveButton(GridPane grid, String move, int row, int column) {
        Button button = new Button(move);
        button.setMinWidth(58);
        button.setOnAction(event -> applyMove(move));
        moveButtons.add(button);
        grid.add(button, column, row);
    }

    private void runSequence() {
        String sequence = sequenceInput.getText().trim();
        if (sequence.isEmpty()) {
            return;
        }
        runMoves(sequence, true);
    }

    private void applyMove(String move) {
        runMoves(move, false);
    }

    private void runMoves(String sequence, boolean clearSequenceInput) {
        List<String> moves = List.of(sequence.trim().split("\\s+"));
        Cube validationCube = cube.copy();
        try {
            for (String move : moves) {
                validationCube.applyMove(move);
            }
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
            return;
        }
        if (clearSequenceInput) {
            sequenceInput.clear();
        }
        animationInProgress = true;
        setControlsDisabled(true);
        animateMove(moves, 0);
    }

    private void animateMove(List<String> moves, int index) {
        String command = moves.get(index);
        Matcher matcher = MOVE_PATTERN.matcher(command);
        if (!matcher.matches()) {
            throw new IllegalStateException("Move was not validated: " + command);
        }

        char face = matcher.group(2).charAt(0);
        int depth = matcher.group(1).isEmpty() ? 1 : Integer.parseInt(matcher.group(1));
        if (!matcher.group(3).isEmpty() && matcher.group(1).isEmpty()) {
            depth = 2;
        }
        String suffix = matcher.group(4);
        int turns = suffix == null ? 1 : suffix.equals("2") ? 2 : 3;
        Group turningLayer = new Group();
        Rotate rotation = new Rotate(0, axisFor(face));
        turningLayer.getTransforms().add(rotation);

        for (Cubie cubie : cubies) {
            if (isInTurningLayer(cubie, face, depth, cube.size())) {
                cubeGroup.getChildren().remove(cubie.node());
                turningLayer.getChildren().add(cubie.node());
            }
        }
        cubeGroup.getChildren().add(turningLayer);
        double sign = rotationSign(face);
        double quarterTurn = turns == 3 ? -1 : turns;
        Timeline animation = new Timeline(new KeyFrame(
                Duration.millis(260),
                new KeyValue(rotation.angleProperty(), sign * quarterTurn * 90, Interpolator.EASE_BOTH)
        ));
        status.setText("Move " + (index + 1) + "/" + moves.size() + ": " + command);
        animation.setOnFinished(event -> {
            cube.applyMove(command);
            drawCube();
            if (index + 1 < moves.size()) {
                animateMove(moves, index + 1);
            } else {
                animationInProgress = false;
                setControlsDisabled(false);
                status.setText("Executed: " + String.join(" ", moves));
                checkSolved();
            }
        });
        animation.play();
    }

    private void setControlsDisabled(boolean disabled) {
        sequenceInput.setDisable(disabled);
        sizeSelector.setDisable(disabled);
        runButton.setDisable(disabled);
        shuffleButton.setDisable(disabled);
        solveButton.setDisable(disabled);
        moveButtons.forEach(button -> button.setDisable(disabled));
    }

    private static javafx.geometry.Point3D axisFor(char face) {
        return switch (face) {
            case 'U', 'D' -> Rotate.Y_AXIS;
            case 'F', 'B' -> Rotate.Z_AXIS;
            case 'L', 'R' -> Rotate.X_AXIS;
            default -> throw new IllegalArgumentException("Unknown face: " + face);
        };
    }

    private static double rotationSign(char face) {
        return switch (face) {
            case 'U', 'F', 'L' -> 1;
            case 'D', 'B', 'R' -> -1;
            default -> throw new IllegalArgumentException("Unknown face: " + face);
        };
    }

    private static boolean isInTurningLayer(Cubie cubie, char face, int depth, int size) {
        return switch (face) {
            case 'U' -> cubie.row() < depth;
            case 'D' -> cubie.row() >= size - depth;
            case 'F' -> cubie.depth() >= size - depth;
            case 'B' -> cubie.depth() < depth;
            case 'R' -> cubie.column() >= size - depth;
            case 'L' -> cubie.column() < depth;
            default -> throw new IllegalArgumentException("Unknown face: " + face);
        };
    }

    private void scramble() {
        if (animationInProgress) {
            return;
        }
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
        if (animationInProgress) {
            return;
        }
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

    private void checkSolved() {
        if (cube.isSolved()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Solved");
            alert.setHeaderText(null);
            alert.setContentText("The " + cube.size() + "x" + cube.size() + " cube is solved.");
            alert.show();
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
        cubeGroup.getChildren().clear();
        cubies.clear();
        int size = cube.size();
        double cubieSize = 0.94;

        for (int depth = 0; depth < size; depth++) {
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    double x = column - (size - 1) / 2.0;
                    double y = row - (size - 1) / 2.0;
                    double z = (size - 1) / 2.0 - depth;
                    Group cubieNode = new Group();
                    Box cubie = new Box(cubieSize, cubieSize, cubieSize);
                    cubie.setMaterial(cubieMaterial);
                    cubieNode.getChildren().add(cubie);

                    if (depth == size - 1) {
                        addSticker(cubieNode, 'F', row, column,
                                0, 0, -0.5 - STICKER_DEPTH / 2);
                    }
                    if (depth == 0) {
                        addSticker(cubieNode, 'B', row, size - 1 - column,
                                0, 0, 0.5 + STICKER_DEPTH / 2);
                    }
                    if (row == 0) {
                        addSticker(cubieNode, 'U', depth, column,
                                0, -0.5 - STICKER_DEPTH / 2, 0);
                    }
                    if (row == size - 1) {
                        addSticker(cubieNode, 'D', size - 1 - depth, column,
                                0, 0.5 + STICKER_DEPTH / 2, 0);
                    }
                    if (column == size - 1) {
                        addSticker(cubieNode, 'R', row, size - 1 - depth,
                                0.5 + STICKER_DEPTH / 2, 0, 0);
                    }
                    if (column == 0) {
                        addSticker(cubieNode, 'L', row, depth,
                                -0.5 - STICKER_DEPTH / 2, 0, 0);
                    }
                    cubieNode.setTranslateX(x);
                    cubieNode.setTranslateY(y);
                    cubieNode.setTranslateZ(z);
                    cubeGroup.getChildren().add(cubieNode);
                    cubies.add(new Cubie(row, column, depth, cubieNode));
                }
            }
        }
    }

    private void addSticker(Group cubie, char face, int row, int column,
                            double x, double y, double z) {
        Box sticker;
        if (face == 'F' || face == 'B') {
            sticker = new Box(0.84, 0.84, STICKER_DEPTH);
        } else if (face == 'U' || face == 'D') {
            sticker = new Box(0.84, STICKER_DEPTH, 0.84);
        } else {
            sticker = new Box(STICKER_DEPTH, 0.84, 0.84);
        }
        sticker.setMaterial(stickerMaterials[cube.colorAt(face, row, column)]);
        sticker.setTranslateX(x);
        sticker.setTranslateY(y);
        sticker.setTranslateZ(z);
        cubie.getChildren().add(sticker);
    }

    private static PhongMaterial[] createStickerMaterials() {
        PhongMaterial[] materials = new PhongMaterial[COLORS.length];
        for (int index = 0; index < COLORS.length; index++) {
            materials[index] = new PhongMaterial(COLORS[index]);
            materials[index].setSpecularColor(Color.rgb(235, 235, 235));
        }
        return materials;
    }

    private final List<Cubie> cubies = new ArrayList<>();
    private record Cubie(int row, int column, int depth, Group node) {
    }
}
