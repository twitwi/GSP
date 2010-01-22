/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author emonet
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleParameter {
    String name() default "";
    String change() default "";
    boolean initOnly() default false;
}
