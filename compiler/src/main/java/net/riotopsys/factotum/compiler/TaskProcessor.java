/*
 * Copyright 2015 C. A. Fitzgerald
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.riotopsys.factotum.compiler;

import com.google.auto.service.AutoService;
import net.riotopsys.factotum.api.annotation.Task;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

/**
 * Created by afitzgerald on 8/27/14.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "net.riotopsys.factotum.api.annotation.Task"
})
public class TaskProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        try {
            for (Element elem : roundEnv.getElementsAnnotatedWith(Task.class)) {

                TypeElement typeElement = Util.getTypeElement(elem);
                if (typeElement.getKind() == ElementKind.INTERFACE) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) not supported on interfaces (%s)",
                                    Task.class.getCanonicalName(),
                                    typeElement.getQualifiedName().toString()),
                            elem);
                    continue;
                }

                if (!Util.hasDefaultConstructor(typeElement)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) requires a default non private constructor in class (%s)",
                                    Task.class.getCanonicalName(),
                                    typeElement.getQualifiedName().toString()),
                            elem);
                    continue;
                }

                if (typeElement.getTypeParameters().size() > 0) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) not supported on templatized type (%s)",
                                    Task.class.getCanonicalName(),
                                    typeElement.getQualifiedName().toString()),
                            elem);
                    continue;
                }

                if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) not supported on Abstract class (%s)",
                                    Task.class.getCanonicalName(),
                                    typeElement.getQualifiedName().toString()),
                            elem);
                    continue;
                }

                if (elem.getModifiers().contains(Modifier.PRIVATE)) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) not supported on private methods",
                                    Task.class.getCanonicalName()),
                            elem);
                    continue;
                }

                new RequestWriter(processingEnv).write((ExecutableElement) elem);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
