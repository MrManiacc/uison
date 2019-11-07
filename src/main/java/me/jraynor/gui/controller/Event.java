package me.jraynor.gui.controller;

import me.jraynor.gui.parser.UIType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {
    public String id() default "NO_ID";

    public String action();

    public String group() default "NO_GROUP";

    UIType tag() default UIType.UIUNKOWN;
}
