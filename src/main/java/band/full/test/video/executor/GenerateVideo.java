package band.full.test.video.executor;

import static band.full.test.video.executor.GenerateVideo.Type.ALL;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
@Inherited
@ExtendWith(GenerateVideoExtension.class)
@Tag("GenerateVideo")
public @interface GenerateVideo {
    public enum Type {
        ALL, MAIN, LOSSLESS
    };

    Type value() default ALL;
}
