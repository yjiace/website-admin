package cn.smallyoung.websiteadmin.interfaces;

import java.lang.annotation.*;

/**
 * @author smallyoung
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface DataName {

    /**
     * @return 字段名称
     */
    String name() default "";

}
