package de.ulrichraab.arrow;


import dagger.MapKey;


/**
 * Key that identifies an injector builder by class.
 * @author Ulrich Raab
 */
@MapKey
@interface ClassKey {

    Class<?> value ();
}
