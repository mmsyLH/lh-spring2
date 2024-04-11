package asia.lhweb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件扫描
 * 1 @Target(ElementType.TYPE)：
 * 指定ComponentScan注解可以修饰type类型
 * 2 @Retention(RetentionPolicy.RUNTIME)
 *  指定ComponentScan存活范围
 * 3 表示ComponentScan可以传入value属性
 * @author 罗汉
 * @date 2023/07/17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {
    String[] value();
}
