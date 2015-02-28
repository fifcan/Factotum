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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

/**
 * Created by afitzgerald on 8/27/14.
 */
public class Util {

    private Util() {
    }

    public static PackageElement getPackageElement(Element element) {

        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }

        return (PackageElement) element;
    }

    public static String ucaseFirstCharacter(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static TypeElement getTypeElement(Element element) {
        while (element != null
                && element.getKind() != ElementKind.CLASS
                && element.getKind() != ElementKind.INTERFACE) {
            element = element.getEnclosingElement();
        }
        return (TypeElement) element;
    }

    public static boolean isPublicEnough(Element element) {
        return !element.getModifiers().contains(Modifier.PRIVATE);
    }

    public static boolean hasDefaultConstructor(TypeElement type) {
        for (ExecutableElement cons : ElementFilter.constructorsIn(type.getEnclosedElements())) {
            if (cons.getParameters().isEmpty() && isPublicEnough(cons))
                return true;
        }
        return false;
    }


}
