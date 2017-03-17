package de.ulrichraab.arrow;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.MembersInjector;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public final class Arrow {

    private static final Arrow INSTANCE = new Arrow();

    @Inject
    Map<Class<?>, Provider<Injector.Builder<?>>> providers;

    /**
     * Private constructor. Instance creation not allowed
     */
    private Arrow () {}

    /**
     * Initializes arrow using the specified injector.
     */
    public static void initialize (MembersInjector<Arrow> injector) {
        if (injector == null) {
            throw new NullPointerException("injector == null");
        }
        injector.injectMembers(INSTANCE);
    }

    /**
     * Creates a new injector builder.
     * @param target The target in which to inject dependencies.
     * @param builderClass The class of the injector builder.
     * @param <B> The concrete type of the injector builder.
     * @throws IllegalArgumentException If {@code target} or {@code builderClass} are {@code null}.
     * @throws IllegalStateException If arrow is not initialized.
     */
    @SuppressWarnings("ConstantConditions")
    public static <B extends Injector.Builder<?>> B createInjectorBuilder (Object target, Class<B> builderClass) {
        // Ensure arguments are not null
        List<String> missing = new ArrayList<>();
        if (target == null) {
            missing.add("target");
        }
        if (builderClass == null) {
            missing.add(" builderClass");
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "Missing required arguments %1$s",
                Arrays.toString(missing.toArray())
            ));
        }
        Map<Class<?>, Provider<Injector.Builder<?>>> providers = INSTANCE.providers;
        if (providers == null || providers.isEmpty()) {
            throw new IllegalStateException("Arrow is not initialized");
        }
        Class<?> key = target.getClass();
        Provider<Injector.Builder<?>> provider = INSTANCE.providers.get(key);
        Injector.Builder<?> builder = provider.get().target(target);
        return builderClass.cast(builder);
    }
}
