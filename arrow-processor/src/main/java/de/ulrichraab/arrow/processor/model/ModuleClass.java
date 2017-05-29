package de.ulrichraab.arrow.processor.model;


import java.util.ArrayList;
import java.util.List;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class ModuleClass {

    private final String packageName;
    private final String className;

    private final List<String> injectorClasses = new ArrayList<>();
    private final List<BindsInjectorBuilderMethod> bindsInjectorBuilderMethods = new ArrayList<>();

    public ModuleClass (String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public String getPackageName () {
        return packageName;
    }

    public String getClassName () {
        return className;
    }

    public List<String> getInjectorClasses () {
        return injectorClasses;
    }

    public List<BindsInjectorBuilderMethod> getBindsInjectorBuilderMethods () {
        return bindsInjectorBuilderMethods;
    }
}
