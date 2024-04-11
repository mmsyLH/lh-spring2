package asia.lhweb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以指定bean的作用范围【singleton,prototype】
 *
 * @author 罗汉
 * @date 2023/07/23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    //通过value可以指定是singleton还是prototype
    String value() default "";
}
