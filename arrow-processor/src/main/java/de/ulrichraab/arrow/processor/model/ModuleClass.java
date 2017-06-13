package de.ulrichraab.arrow.processor.model;


import java.util.List;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class ModuleClass {

    private final String packageName;
    private final String className;
    private final List<String> subcomponentClasses;
    private final List<BindingMethod> bindingMethods;

    private ModuleClass (Builder builder) {
        packageName = builder.packageName;
        className = builder.className;
        subcomponentClasses = builder.subcomponentClasses;
        bindingMethods = builder.bindingMethods;
    }

    public String getPackageName () {
        return packageName;
    }

    public String getClassName () {
        return className;
    }

    public List<String> getSubcomponentClasses () {
        return subcomponentClasses;
    }

    public List<BindingMethod> getBindingMethods () {
        return bindingMethods;
    }

    public String getSourceFileName () {
        return packageName + "." + className;
    }

    public static final class Builder {

        private String packageName;
        private String className;
        private List<String> subcomponentClasses;
        private List<BindingMethod> bindingMethods;

        public Builder () {}

        public Builder packageName (String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder className (String className) {
            this.className = className;
            return this;
        }

        public Builder subcomponentClasses (List<String> subcomponentClasses) {
            this.subcomponentClasses = subcomponentClasses;
            return this;
        }

        public Builder bindingMethods (List<BindingMethod> bindingMethods) {
            this.bindingMethods = bindingMethods;
            return this;
        }

        public ModuleClass build () {
            return new ModuleClass(this);
        }
    }
}
