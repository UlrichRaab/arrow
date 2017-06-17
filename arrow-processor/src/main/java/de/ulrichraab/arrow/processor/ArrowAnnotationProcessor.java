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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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

    private Messager messager;
    private VelocityEngine velocityEngine;

    private FastClasspathScanner fastClasspathScanner;
    private ScanResult classpathScanResult;

    private final ModuleClass.Builder moduleClassBuilder = new ModuleClass.Builder();
    private final List<String> subcomponentClasses = new ArrayList<>();
    private final List<BindingMethod> bindingMethods = new ArrayList<>();


    public ArrowAnnotationProcessor () {

        try {
            initializeVelocity();
        }
        catch (Exception e) {
            throw new RuntimeException("Initializing velocity failed:" + e.getMessage(), e);
        }
    }

    @Override
    public synchronized void init (ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnvironment.getMessager();
        fastClasspathScanner = new FastClasspathScanner("-android");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes () {

        Set<String> annotations = new HashSet<>();
        annotations.add(ArrowConfiguration.class.getName());
        annotations.add(Subcomponent.class.getName());
        annotations.add(Subcomponent.Builder.class.getName());

        return Collections.unmodifiableSet(annotations);
    }

    @Override
    public SourceVersion getSupportedSourceVersion () {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process (Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        classpathScanResult = fastClasspathScanner.scan();

        processArrowConfigurationAnnotation(roundEnv);
        processSubcomponentAnnotations(roundEnv);
        processSubcomponentBuilderAnnotations(roundEnv);

        moduleClassBuilder.subcomponentClasses(subcomponentClasses);
        moduleClassBuilder.bindingMethods(bindingMethods);
        ModuleClass moduleClass = moduleClassBuilder.build();

        VelocityContext vc = new VelocityContext();
        vc.put("model", moduleClass);

        writeSourceFile(vc, moduleClass.getSourceFileName());

        return true;
    }

    private void processArrowConfigurationAnnotation (RoundEnvironment roundEnv) {

        Class<ArrowConfiguration> annotationClass = ArrowConfiguration.class;

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotationClass);
        for (Element element : elements) {

            ArrowConfiguration annotation = element.getAnnotation(annotationClass);
            String value = annotation.module();
            int index = value.lastIndexOf(".");
            if (index <= 0 || index >= value.length()) {
                continue;
            }

            moduleClassBuilder
                .packageName(value.substring(0, index))
                .className(value.substring(index + 1));
        }
    }

    private void processSubcomponentAnnotations (RoundEnvironment roundEnv) {

        Class<Subcomponent> annotationClass = Subcomponent.class;
        Set<String> subcomponentClasses = new HashSet<>();

        // Find classes annotated with @Subcomponent in libs
        if (classpathScanResult != null) {
            List<String> temp = classpathScanResult.getNamesOfClassesWithAnnotation(annotationClass);
            for (String subcomponentClass : temp) {
                subcomponentClass = subcomponentClass + ".class";
                subcomponentClasses.add(subcomponentClass);
            }
        }

        // Find classes annotated with @Subcomponent in module that uses this annotation processor
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotationClass);
        for (Element element : elements) {
            String subcomponentClass = ElementUtils.asType(element).getQualifiedName().toString();
            subcomponentClass = subcomponentClass + ".class";
            subcomponentClasses.add(subcomponentClass);
        }

        // Sort the subcomponent classes for better readability of generated code
        this.subcomponentClasses.addAll(subcomponentClasses);
        Collections.sort(this.subcomponentClasses);
    }

    private void processSubcomponentBuilderAnnotations (RoundEnvironment roundEnv) {

        Class<Subcomponent.Builder> annotationClass = Subcomponent.Builder.class;
        Set<BindingMethod> bindingMethods = new HashSet<>();

        // Find classes annotated with @Subcomponent in libs
        if (classpathScanResult != null) {
            List<String> temp = classpathScanResult.getNamesOfClassesWithAnnotation(annotationClass);
            for (String builderClass : temp) {

                builderClass = builderClass.replaceAll("\\$", ".");

                String builderKey = builderClass + ".class";
                String methodName = builderClass.replaceAll("\\.", "_");

                BindingMethod bindingMethod = new BindingMethod(builderClass, methodName, builderKey);
                bindingMethods.add(bindingMethod);
            }
        }

        // Find classes annotated with @Subcomponent.Builder in module that uses this annotation processor
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotationClass);
        for (Element element : elements) {

            String builderClass = ElementUtils.asType(element).getQualifiedName().toString();
            String builderKey = builderClass + ".class";
            String methodName = builderClass.replaceAll("\\.", "_");

            BindingMethod bindingMethod = new BindingMethod(builderClass, methodName, builderKey);
            bindingMethods.add(bindingMethod);
        }

        // Sort the binding methods for better readability of generated code
        this.bindingMethods.addAll(bindingMethods);
        Collections.sort(this.bindingMethods, new BindingMethod.NameComparator());
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
