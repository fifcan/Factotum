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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.annotation.Task;
import net.riotopsys.factotum.api.interfaces.ICallback;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by afitzgerald on 2/26/15.
 */
public class RequestWriter {

    private static class ParameterPair {
        public TypeMirror type;
        public String name;

        public ParameterPair(TypeMirror type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    private final ProcessingEnvironment processingEnv;

    public RequestWriter(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public void write(ExecutableElement element) throws IOException {

        String requestName = buildRequestName(element);
        String packageName = buildPackage(element);

        ClassName requestClassName = ClassName.get(packageName, requestName);

        MethodSpec constructor = buildConstructor(element);

        MethodSpec getTaskHandler = MethodSpec.methodBuilder("getTaskHandler")
                .addAnnotation(Override.class)
                .returns(TypeName.OBJECT)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return new $T()", element.getEnclosingElement())
                .build();


        MethodSpec execute = buildExecute(element);

        TypeName paramArg;
        TypeMirror returnType = element.getReturnType();
        if (returnType.getKind() == TypeKind.VOID) {
            paramArg = TypeName.get(Object.class);
        } else {
            paramArg = ParameterizedTypeName.get(element.getReturnType());
        }

        MethodSpec setCallback = MethodSpec.methodBuilder("setCallback")
                .addModifiers(Modifier.PUBLIC)
                .returns(requestClassName)
                .addParameter(ParameterizedTypeName.get(ClassName.get(ICallback.class), paramArg.box()), "callback")
                .addStatement("callbackRef = new $T<$T>(callback)", WeakReference.class, ICallback.class)
                .addStatement("return this")
                .build();

        MethodSpec setGroup = MethodSpec.methodBuilder("setGroup")
                .addModifiers(Modifier.PUBLIC)
                .returns(requestClassName)
                .addParameter(Object.class, "group")
                .addStatement("this.group = group")
                .addStatement("return this")
                .build();

        MethodSpec setPriority = MethodSpec.methodBuilder("setPriority")
                .addModifiers(Modifier.PUBLIC)
                .returns(requestClassName)
                .addParameter(TypeName.INT, "priority")
                .addStatement("this.priority = priority")
                .addStatement("return this")
                .build();


        TypeSpec.Builder requestType = TypeSpec.classBuilder(requestName)
                .superclass(AbstractRequest.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructor)
                .addMethod(getTaskHandler)
                .addMethod(execute)
                .addMethod(setCallback)
                .addMethod(setGroup)
                .addMethod(setPriority);

        addFields(requestType, element);


        JavaFile javaFile = JavaFile.builder(packageName, requestType.build()).build();

        javaFile.writeTo(processingEnv.getFiler());

    }

    private MethodSpec buildExecute(ExecutableElement element) {


        String args = Joiner.on(", ").join(
                Lists.transform(buildParameters(element), new Function<ParameterPair, String>() {
                    @Override
                    public String apply(ParameterPair parameterPair) {
                        return parameterPair.name;
                    }
                }));


        MethodSpec.Builder result = MethodSpec.methodBuilder("execute")
                .addAnnotation(Override.class)
                .addException(Exception.class)
                .returns(Object.class)
                .addParameter(Object.class, "handler")
                .addModifiers(Modifier.PUBLIC)
                .beginControlFlow("if ( isCanceled() )")
                .addStatement("return null")
                .endControlFlow();



        if (element.getReturnType().getKind() != TypeKind.VOID) {
            result.addStatement("return (($T)handler).$L($L)",
                    element.getEnclosingElement(), element.getSimpleName().toString(), args);
        } else {
            result.addStatement("(($T)handler).$L($L)",
                    element.getEnclosingElement(), element.getSimpleName().toString(), args);
            result.addStatement("return null");
        }

        return result.build();

    }

    private MethodSpec buildConstructor(ExecutableElement elem) {

        List<ParameterPair> parameters = buildParameters(elem);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        for (ParameterPair pair : parameters) {
            constructor
                    .addParameter(ParameterSpec.builder(TypeName.get(pair.type), pair.name).build())
                    .addStatement("this.$L = $L", pair.name, pair.name);
        }


        return constructor.build();
    }

    private void addFields(TypeSpec.Builder requestType, ExecutableElement elem) {
        for (ParameterPair pair : buildParameters(elem)) {
            requestType.addField(
                    FieldSpec.builder(TypeName.get(pair.type), pair.name)
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build()
            );
        }
    }

    private String buildRequestName(ExecutableElement elem) {
        Task taskAnnotation = elem.getAnnotation(Task.class);

        String requestName = taskAnnotation.requestName();
        if (requestName.equals(Task.DEFAULT)) {
            requestName = Util.ucaseFirstCharacter(elem.getSimpleName().toString()) + "Request";
        }

        return requestName;
    }

    private String buildPackage(ExecutableElement elem) {
        PackageElement packageElem = Util.getPackageElement(elem);
        return packageElem.getQualifiedName().toString();
    }

    private List<ParameterPair> buildParameters(ExecutableElement elem) {
        List<ParameterPair> parameters = new ArrayList<>();
        for (VariableElement parameter : elem.getParameters()) {

            TypeMirror parameterType = parameter.asType();

            parameters.add(new ParameterPair(parameterType, parameter.getSimpleName().toString()));
        }
        return parameters;
    }
}
