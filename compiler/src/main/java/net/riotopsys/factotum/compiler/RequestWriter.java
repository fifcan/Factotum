package net.riotopsys.factotum.compiler;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.squareup.javawriter.JavaWriter;
import net.riotopsys.factotum.api.AbstractRequest;
import net.riotopsys.factotum.api.annotation.Task;
import net.riotopsys.factotum.api.concurent.ICallback;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Created by afitzgerald on 8/27/14.
 */
public class RequestWriter {

    private final ProcessingEnvironment processingEnv;
    private final ExecutableElement elem;

    private String packageName;
    private String handlerClass;
    private String requestName;
    private ArrayList<String> constructorParameters = new ArrayList<>();
    private ArrayList<String> parameterNames = new ArrayList<>();
    private String returnDef;

    private HashSet<String> imports = new HashSet<>();

    private boolean isVoidReturn;


    public RequestWriter(ProcessingEnvironment processingEnv, ExecutableElement elem) {
        this.processingEnv = processingEnv;
        this.elem = elem;

        imports.add(AbstractRequest.class.getCanonicalName());
        imports.add(ICallback.class.getCanonicalName());
        imports.add(WeakReference.class.getCanonicalName());

        isVoidReturn = Util.hasVoidReturn(elem);

        buildPackage(elem);

        buildHandlerClass();

        buildRequestName();

        buildConstructorParams();

        buildParamNames();

        if (!isVoidReturn) {
            returnDef = buildRetrunDef(elem.getReturnType());
        }

    }

    public void write() throws IOException {

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName +'.'+ requestName);

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
                .emitStatement("return new %s()", handlerClass)
                .endMethod();

                //write execute method
        if ( Util.hasVoidReturn(elem ) ){
            createVoidExecute(elem, handlerClass, parameterNames, jw);
            //TODO: handle set callback
        } else {
            createNormalExecute(elem, handlerClass, parameterNames, jw);
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

    private String buildRetrunDef(TypeMirror mirrorReturnType) {
        String result;

        if ( mirrorReturnType.getKind() == TypeKind.WILDCARD){

            TypeMirror wildType = ((WildcardType) mirrorReturnType).getSuperBound();

            if ( wildType != null ){
                return String.format("? super %s", buildRetrunDef(wildType));
            } else {

                wildType = ((WildcardType) mirrorReturnType).getExtendsBound();

                if (wildType != null) {
                    return String.format("? extends %s", buildRetrunDef(wildType));
                } else {
                    throw new RuntimeException("unsupported wildcard?");
                }
            }
        }

        TypeElement returnType = Util.mirrorTypeToElementType(mirrorReturnType, processingEnv);

        addImport(returnType);

        ArrayList<String> argTypes = new ArrayList<>();

        for ( TypeMirror arg: ((DeclaredType)mirrorReturnType).getTypeArguments()){

            argTypes.add( buildRetrunDef(arg) );

        }

        if ( argTypes.size() > 0 ){
            result = String.format("%s<%s>", returnType.getSimpleName().toString(), Joiner.on(", ").join(argTypes) );
        } else {
            result = returnType.getSimpleName().toString();
        }

        return result;
    }

    private void buildHandlerClass() {
        handlerClass = elem.getEnclosingElement().getSimpleName().toString();
    }

    private void buildConstructorParams() {
        for ( VariableElement parameter: elem.getParameters()){
            TypeElement paramType = Util.getParameterElement(parameter, processingEnv);

            constructorParameters.add(paramType.getSimpleName().toString());
            constructorParameters.add(parameter.getSimpleName().toString());

            addImport(paramType);
        }
    }

    private void buildParamNames() {
        for ( VariableElement parameter: elem.getParameters()){
            TypeElement paramType = Util.getParameterElement(parameter, processingEnv);

            parameterNames.add(parameter.getSimpleName().toString());
        }
    }

    private void addImport(TypeElement paramType) {
        PackageElement paramaterPackage = Util.getPackageElement(paramType);
        if ( !packageName.equals(paramaterPackage.getQualifiedName().toString()) && !paramaterPackage.getQualifiedName().toString().equals("java.lang")){
            imports.add(paramType.getQualifiedName().toString());
        }
    }

    private void buildRequestName() {
        Task taskAnnotation = elem.getAnnotation(Task.class);

        requestName = taskAnnotation.requestName();
        if ( requestName.equals(Task.DEFAULT) ){
            requestName = Util.ucaseFirstCharacter(elem.getSimpleName().toString()) + "Request";
        }
    }

    private void buildPackage(ExecutableElement elem) {
        PackageElement packageElem = Util.getPackageElement(elem);
        packageName = packageElem.getQualifiedName().toString();
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
