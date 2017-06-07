package band.full.testing.video.executor;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * @author Igor Malinin
 */
public class GenerateVideoRunner extends BlockJUnit4ClassRunner {
    private static final String ERROR_ON_TIMEOUT = "GenerateVideoRunner"
            + " does not work with timeouts in the @Test Annotation."
            + " A possible Workaround might be a timeouted CompletableFuture.";

    public static class FxBgApp extends Application {
        /** Guarantee that only one JavaFX thread will be started */
        private static final ReentrantLock LOCK = new ReentrantLock();
        private static final AtomicBoolean STARTED = new AtomicBoolean();

        public static void startJavaFx() throws InitializationError {
            try {
                // Lock or wait. This gives another call to this method time to
                // finish and release the lock before another one has a go
                LOCK.lock();

                if (!STARTED.get()) {
                    ExecutorService executor = newSingleThreadExecutor();
                    Future<?> launch = executor.submit(() -> launch());

                    while (!STARTED.get()) {
                        try {
                            launch.get(1, MILLISECONDS);
                        } catch (InterruptedException | TimeoutException e) {
                            // continue waiting until success or error
                        }
                        Thread.yield();
                    }
                }
            } catch (ExecutionException e) {
                throw new InitializationError(e);
            } finally {
                LOCK.unlock();
            }
        }

        @Override
        public void start(Stage stage) {
            STARTED.set(true);
        }
    }

    public GenerateVideoRunner(Class<?> clazz)
            throws InitializationError {
        super(clazz);

        FxBgApp.startJavaFx();
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        CountDownLatch latch = new CountDownLatch(1);

        Test annotation = method.getAnnotation(Test.class);
        long timeout = annotation.timeout();

        if (timeout > 0) {
            System.err.println(ERROR_ON_TIMEOUT);
            throw new UnsupportedOperationException(ERROR_ON_TIMEOUT);
        }

        RuntimeException[] exception = {null};

        Platform.runLater(() -> {
            try {
                GenerateVideoRunner.super.runChild(method, notifier);
            } catch (RuntimeException e) {
                exception[0] = e;
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {}

        if (exception[0] != null) throw exception[0];
    }
}
