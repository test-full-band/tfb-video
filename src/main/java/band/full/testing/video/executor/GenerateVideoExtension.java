package band.full.testing.video.executor;

import static band.full.testing.video.executor.GenerateVideo.Type.ALL;
import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static java.lang.System.out;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.executor.GenerateVideo.Type;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import javafx.application.Platform;

/**
 * JUnit 5 Jupiter Extension for use on test classes generating video tests.
 *
 * @author Igor Malinin
 */
public class GenerateVideoExtension
        implements BeforeAllCallback, ExecutionCondition {
    private static final ConditionEvaluationResult ENABLED_NO_ANNOTATION =
            enabled("@GenerateVideo is not present");

    private static final ConditionEvaluationResult ENABLED_TYPE =
            enabled("@GenerateVideo enables current encode type");

    private static final ConditionEvaluationResult DISABLED_TYPE =
            disabled("@GenerateVideo disables current encode type");

    /** Guarantee that only one JavaFX thread will be started */
    private static boolean STARTED;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!STARTED) {
            out.println("Starting JavaFX");
            Platform.startup(() -> {});
            STARTED = true;
        }
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(
            ExtensionContext context) {
        return findAnnotation(context.getElement(), GenerateVideo.class)
                .map(this::isTypeEnabled).orElse(ENABLED_NO_ANNOTATION);
    }

    ConditionEvaluationResult isTypeEnabled(GenerateVideo gv) {
        Type type = gv.value();
        if (type == ALL) return ENABLED_TYPE;
        if (!EncoderY4M.LOSSLESS && type == MAIN) return ENABLED_TYPE;
        if (EncoderY4M.LOSSLESS && type == LOSSLESS) return ENABLED_TYPE;
        return DISABLED_TYPE;
    }
}
