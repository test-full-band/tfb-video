package band.full.testing.video.executor;

import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.scene.paint.Color.BLACK;

import band.full.testing.video.core.Resolution;

import java.util.function.Supplier;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class FxDisplay extends Application {
    private static Resolution size;
    private static Paint fill;
    private static Supplier<Parent> root;

    @Override
    public void start(Stage stage) throws Exception {
        Parent pane = root.get();

        double width = size.width;
        double height = size.height;

        VBox box = new VBox(pane);
        VBox.setVgrow(pane, ALWAYS);
        box.setMinSize(width, height);
        box.setPrefSize(width, height);
        box.setMaxSize(width, height);

        box.setBackground(new Background(new BackgroundFill(fill, null, null)));

        ScrollPane scroll = new ScrollPane(box);
        Scene scene = new Scene(scroll);

        stage.setScene(scene);
        stage.show();
    }

    public static void show(Resolution resolution, Supplier<Parent> parent) {
        show(resolution, BLACK, parent);
    }

    public static void show(Resolution resolution, Paint background,
            Supplier<Parent> parent) {
        size = resolution;
        root = parent;
        fill = background;

        launch();
    }
}
