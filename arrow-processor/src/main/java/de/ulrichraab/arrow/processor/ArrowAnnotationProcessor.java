/*
 * Copyright (C) 2017 Ulrich Raab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ulrichraab.arrow.processor;


import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import dagger.Subcomponent;
import de.ulrichraab.arrow.ArrowConfiguration;
import de.ulrichraab.arrow.processor.model.BindingMethod;
import de.ulrichraab.arrow.processor.model.ModuleClass;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class ArrowAnnotationProcessor extends AbstractProcessor {

    private static Class<? extends Annotation> ARROW_CONFIGURATION_CLASS = ArrowConfiguration.class;
    private static Class<? extends Annotation> SUBCOMPONENT_CLASS = Subcomponent.class;
    private static Class<? extends Annotation> SUBCOMPONENT_BUILDER_CLASS = Subcomponent.Builder.class;

    private Messager messager;
    private VelocityEngine velocityEngine;

    private FastClasspathScanner fastClasspathScanner;
    private ScanResult classpathScanResult;

    public ArrowAnnotationProcessor () {
        try {
            initializeVelocity();

        }
        catch (Exception e) {
            throw new RuntimeException("Initializing velocity failed:" + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void init (ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        fastClasspathScanner = new FastClasspathScanner("-android");
        classpathScanResult = fastClasspathScanner.scan();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes () {

        Set<String> annotationTypes = new HashSet<>();
        annotationTypes.add(ARROW_CONFIGURATION_CLASS.getCanonicalName());
        annotationTypes.add(SUBCOMPONENT_CLASS.getCanonicalName());
        annotationTypes.add(SUBCOMPONENT_BUILDER_CLASS.getCanonicalName());

        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion () {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process (Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        ModuleClass.Builder moduleClassBuilder = createModuleClassBuilder(roundEnv);
        if (moduleClassBuilder == null) {
            return false;
        }

        List<String> subcomponentClasses = getSubcomponentClasses(roundEnv);
        moduleClassBuilder.subcomponentClasses(subcomponentClasses);

        List<BindingMethod> bindingMethods = getBindingMethods(roundEnv);
        moduleClassBuilder.bindingMethods(bindingMethods);

        ModuleClass moduleClass = moduleClassBuilder.build();

        VelocityContext vc = new VelocityContext();
        vc.put("model", moduleClass);

        writeSourceFile(vc, moduleClass.getSourceFileName());

        return true;
    }

    private ModuleClass.Builder createModuleClassBuilder (RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ArrowConfiguration.class);
        for (Element element : elements) {
            ArrowConfiguration annotation = element.getAnnotation(ArrowConfiguration.class);
            String value = annotation.module();
            int index = value.lastIndexOf(".");
            if (index <= 0 || index >= value.length()) {
                continue;
            }

            return new ModuleClass.Builder()
                .packageName(value.substring(0, index))
                .className(value.substring(index + 1));
        }

        return null;
    }

    private List<String> getSubcomponentClasses (RoundEnvironment roundEnv) {

        List<String> classNames = classpathScanResult.getNamesOfClassesWithAnnotation(SUBCOMPONENT_CLASS);

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SUBCOMPONENT_CLASS);
        for (Element element : elements) {
            String className = ElementUtils.asType(element).getQualifiedName().toString();
            classNames.add(className);
        }

        messager.printMessage(Diagnostic.Kind.NOTE, classNames.size() + " subcomponents found");

        Set<String> subcomponentClasses = new HashSet<>();
        for (String className : classNames) {
            subcomponentClasses.add(className + ".class");
        }

        // Sort the subcomponent classes for better readability of generated code
        List<String> temp = new ArrayList<>(subcomponentClasses);
        Collections.sort(temp);

        return temp;
    }

    private List<BindingMethod> getBindingMethods (RoundEnvironment roundEnv) {

        Set<BindingMethod> bindingMethods = new HashSet<>();

        List<String> classNames = classpathScanResult.getNamesOfClassesWithAnnotation(SUBCOMPONENT_BUILDER_CLASS);
        for (String className : classNames) {

            String builderClass = className.replaceAll("\\$", ".");
            String methodName = builderClass.replaceAll("\\.", "_");

            BindingMethod bindingMethod = new BindingMethod(builderClass, methodName);
            bindingMethods.add(bindingMethod);
        }


        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SUBCOMPONENT_BUILDER_CLASS);
        for (Element element : elements) {

            String builderClass = ElementUtils.asType(element).getQualifiedName().toString();
            String methodName = builderClass.replaceAll("\\.", "_");

            BindingMethod bindingMethod = new BindingMethod(builderClass, methodName);
            bindingMethods.add(bindingMethod);
        }

        messager.printMessage(Diagnostic.Kind.NOTE, bindingMethods.size() + " subcomponent builders found");

        // Sort the binding methods for better readability of generated code
        List<BindingMethod> temp = new ArrayList<>(bindingMethods);
        Collections.sort(temp, new Comparator<BindingMethod>() {
            @Override
            public int compare (BindingMethod l, BindingMethod r) {
                return l.getName().compareTo(r.getName());
            }
        });

        return new ArrayList<>(bindingMethods);
    }

    private void initializeVelocity () throws Exception {

        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        if (url == null) {
            return;
        }

        Properties props = new Properties();
        props.load(url.openStream());

        velocityEngine = new VelocityEngine(props);
        velocityEngine.init();
    }

    private void writeSourceFile (VelocityContext context, String sourceFileName) {
        try {
            JavaFileObject sourceFile = processingEnv
                .getFiler()
                .createSourceFile(sourceFileName);

            Writer writer = sourceFile.openWriter();
            Template vt = velocityEngine.getTemplate("arrow_multibinding_module_v2.vm");
            vt.merge(context, writer);
            writer.close();
        }
        catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // That occurs if the file already exists after its first run, this is normal.
        }
        catch (Exception e) {
            throw new RuntimeException("Writing source file failed", e);
        }
    }
}
