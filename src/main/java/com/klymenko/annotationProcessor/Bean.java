package com.klymenko.annotationProcessor;

import java.lang.annotation.*;

@Target(java.lang.annotation.ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Bean {}
