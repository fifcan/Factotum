package net.riotopsys.factotum.compiler;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Created by afitzgerald on 8/27/14.
 */
public class Util {

    public static PackageElement getPackageElement(Element element) {

        while ( element.getKind() != ElementKind.PACKAGE ){
            element = element.getEnclosingElement();
        }

        return (PackageElement) element;

    }

    public static String ucaseFirstCharacter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static TypeElement getParameterElement(VariableElement parameterElement , ProcessingEnvironment processingEnv) {
        TypeMirror parameterType = parameterElement.asType();
        Types typeUtils = processingEnv.getTypeUtils();
        return (TypeElement) typeUtils.asElement(parameterType);
    }

    public static boolean hasVoidReturn(ExecutableElement element) {
        return element.getReturnType().getKind().equals(TypeKind.VOID);
    }

    public static TypeElement mirrorTypeToElementType(TypeMirror parameterType , ProcessingEnvironment processingEnv) {
        Types typeUtils = processingEnv.getTypeUtils();
        return (TypeElement) typeUtils.asElement(parameterType);
    }

}
