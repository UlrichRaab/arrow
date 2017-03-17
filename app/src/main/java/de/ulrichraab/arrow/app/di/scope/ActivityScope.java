package de.ulrichraab.arrow.app.di.scope;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@Scope
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ActivityScope {}
