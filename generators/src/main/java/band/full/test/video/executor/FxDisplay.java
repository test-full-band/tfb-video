package band.full.test.video.executor;

import static band.full.test.video.encoder.EncoderParameters.HD_MAIN;
import static javafx.scene.layout.Priority.ALWAYS;
import static javafx.scene.paint.Color.BLACK;

import band.full.core.Resolution;
import band.full.test.video.encoder.EncoderParameters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.application.Platform;
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

        double width = size.width();
        double height = size.height();

        VBox box = new VBox(pane);
        VBox.setVgrow(pane, ALWAYS);
        box.setMinSize(width, height);
        box.setPrefSize(width, height);
        box.setMaxSize(width, height);

        box.setBackground(new Background(new BackgroundFill(fill, null, null)));

        var scroll = new ScrollPane(box);
        var scene = new Scene(scroll);

        stage.setScene(scene);
        stage.show();
    }

    public static void show(Function<EncoderParameters, Parent> overlay) {
        show(HD_MAIN, overlay);
    }

    public static void show(EncoderParameters params,
            Function<EncoderParameters, Parent> overlay) {
        show(params.resolution, BLACK, () -> overlay.apply(params));
    }

    public static void show(Resolution resolution, Supplier<Parent> overlay) {
        show(resolution, BLACK, overlay);
    }

    public static void show(Resolution resolution, Paint background,
            Supplier<Parent> overlay) {
        size = resolution;
        root = overlay;
        fill = background;

        launch();
    }

    public static void runAndWait(Runnable runnable) {
        var future = new CompletableFuture<>();

        Platform.runLater(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                future.completeExceptionally(t);
                return;
            }

            future.complete(null);
        });

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
