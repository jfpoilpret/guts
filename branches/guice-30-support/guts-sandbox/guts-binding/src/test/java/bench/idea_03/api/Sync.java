package bench.idea_03.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sync {

	SyncKind kind() default SyncKind.REPLICATE;

	Class<?> relm() default Object.class;

	SyncTopic topic() default SyncTopic.KEY_DEFAULT;

}