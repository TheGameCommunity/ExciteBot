package com.gamebuster19901.excite.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to indicate that a particular method or parameter is vulenrable to SQL injection
 */

@Retention(SOURCE)
@Target({ METHOD, PARAMETER })
public @interface Vulnerable {

}
