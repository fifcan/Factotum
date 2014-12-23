package net.riotopsys.factotum.compiler;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.squareup.javawriter.JavaWriter;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.annotation.Task;
import net.riotopsys.factotum.api.concurent.ICallback;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Created by afitzgerald on 8/27/14.
 */
public class RequestWriter {

    private final ProcessingEnvironment processingEnv;

    public RequestWriter(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public void write(ExecutableElement elem) throws IOException {

        PackageElement packageElem = Util.getPackageElement(elem);
        String packageName = packageElem.getQualifiedName().toString();
        String parentClass = elem.getEnclosingElement().getSimpleName().toString();



        Task taskAnnotation = elem.getAnnotation(Task.class);

        String requestName = taskAnnotation.requestName();
        if ( requestName.equals(Task.DEFAULT) ){
            requestName = Util.ucaseFirstCharacter( elem.getSimpleName().toString() ) + "Request";
        }

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName +'.'+ requestName);

        List<String> constructorParameters = new ArrayList<String>();
        List<String> parameterNames = new ArrayList<String>();

        Set<String> imports = new HashSet<String>();

        imports.add(AbstractRequest.class.getCanonicalName());
        imports.add(ICallback.class.getCanonicalName());
        imports.add(WeakReference.class.getCanonicalName());

        for ( VariableElement parameter: elem.getParameters()){
            TypeElement paramType = Util.getParameterElement(parameter, processingEnv);

            constructorParameters.add(paramType.getSimpleName().toString());
            constructorParameters.add(parameter.getSimpleName().toString());

            parameterNames.add(parameter.getSimpleName().toString());

            PackageElement paramaterPackage = Util.getPackageElement(paramType);
            if ( !packageElem.equals(paramaterPackage) && !paramaterPackage.getQualifiedName().toString().equals("java.lang")){
                imports.add(paramType.getQualifiedName().toString());
            }
        }

        String returnDef = null;
        if ( !Util.hasVoidReturn(elem ) ) {

            TypeMirror mirrorReturnType = elem.getReturnType();
            TypeElement returnType = Util.mirrorTypeToElementType(mirrorReturnType, processingEnv);

            PackageElement paramaterPackage = Util.getPackageElement(returnType);
            if (!packageElem.equals(paramaterPackage) && !paramaterPackage.getQualifiedName().toString().equals("java.lang")) {
                imports.add(returnType.getQualifiedName().toString());
            }

            ArrayList<String> argTypes = new ArrayList<>();

            for ( TypeMirror arg: ((DeclaredType)mirrorReturnType).getTypeArguments()){
                TypeElement argElem = Util.mirrorTypeToElementType(arg, processingEnv);

                PackageElement argPackage = Util.getPackageElement(argElem);
                if (!argPackage.equals(paramaterPackage) && !argPackage.getQualifiedName().toString().equals("java.lang")) {
                    imports.add(argElem.getQualifiedName().toString());
                }

                argTypes.add( argElem.getSimpleName().toString() );

            }

            if ( argTypes.size() > 0 ){
                returnDef = String.format("%s<%s>", returnType.getSimpleName().toString(), Joiner.on(", ").join(argTypes) );
            } else {
                returnDef = returnType.getSimpleName().toString();
            }

        }


        JavaWriter jw = new JavaWriter(jfo.openWriter());

        jw.emitPackage(packageName)
                .emitImports(imports)
                .beginType(requestName, "class", EnumSet.of(Modifier.PUBLIC, Modifier.FINAL), AbstractRequest.class.getSimpleName());

        for ( VariableElement parameter: elem.getParameters() ){
            TypeElement paramType = Util.getParameterElement(parameter, processingEnv);

            jw.emitField(paramType.getSimpleName().toString(),parameter.getSimpleName().toString(),EnumSet.of(Modifier.PRIVATE,Modifier.FINAL));
        }

        jw.beginConstructor(EnumSet.of(Modifier.PUBLIC), constructorParameters.toArray(new String[constructorParameters.size()]));


        for ( VariableElement parameter: elem.getParameters()){
            jw.emitStatement("this.%1$s = %1$s", parameter.getSimpleName().toString());
        }

        jw.endConstructor()

                //write handleingClass method
                .emitAnnotation(Override.class)
                .beginMethod("Object", "getTaskHandler", EnumSet.of(Modifier.PUBLIC))
                .emitStatement("return new %s()", parentClass)
                .endMethod();

                //write execute method
        if ( Util.hasVoidReturn(elem ) ){
            createVoidExecute(elem, parentClass, parameterNames, jw);
            //TODO: handle set callback
        } else {
            createNormalExecute(elem, parentClass, parameterNames, jw);
            jw.beginMethod(requestName,"setCallback", EnumSet.of(Modifier.PUBLIC), String.format("%s<%s>", ICallback.class.getSimpleName(), returnDef ), "callback" )
                    .emitStatement("callbackRef = new WeakReference<ICallback>(callback)")
                    .emitStatement("return this")
                    .endMethod();
        }

        jw.beginMethod(requestName, "setGroup", EnumSet.of(Modifier.PUBLIC), "Object", "group" )
                .emitStatement("this.group = group")
                .emitStatement("return this")
                .endMethod();

        jw.beginMethod(requestName, "setPriority", EnumSet.of(Modifier.PUBLIC), "int", "priority" )
                .emitStatement("this.priority = priority")
                .emitStatement("return this")
                .endMethod();

        jw.endType()
                .close();

    }

    private void createNormalExecute(ExecutableElement elem, String parentClass, List<String> parameterNames, JavaWriter jw) throws IOException {
        jw.emitAnnotation(Override.class)
                .beginMethod("Object", "execute", EnumSet.of(Modifier.PUBLIC), Lists.newArrayList("Object", "handler"), Lists.newArrayList("Exception") )
                .beginControlFlow("if ( isCanceled() )")
                .emitStatement("return null")
                .endControlFlow()
                .emitStatement("return ((%s)handler).%s(%s)", parentClass, elem.getSimpleName().toString(), Joiner.on(", ").join(parameterNames))
                .endMethod();
    }

    private void createVoidExecute(ExecutableElement elem, String parentClass, List<String> parameterNames, JavaWriter jw) throws IOException {
        jw.emitAnnotation(Override.class)
                .beginMethod("Object", "execute", EnumSet.of(Modifier.PUBLIC), Lists.newArrayList("Object", "handler"), Lists.newArrayList("Exception"))
                .beginControlFlow("if ( isCanceled() )")
                .emitStatement("return null")
                .endControlFlow()
                .emitStatement("((%s)handler).%s(%s)", parentClass, elem.getSimpleName().toString(), Joiner.on(", ").join(parameterNames))
                .emitStatement("return null")
                .endMethod();
    }
}
