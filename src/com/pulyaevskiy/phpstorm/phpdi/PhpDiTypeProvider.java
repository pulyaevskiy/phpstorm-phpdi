package com.pulyaevskiy.phpstorm.phpdi;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.PhpReference;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.Nullable;

/**
 * Type provider for PHP-DI container.
 *
 * Though not particularly tied to PHP-DI this class provides type information for all method calls that looks like:
 *      $someVar->get(MyService::class)
 *
 * It will just take class name from the method argument and pass it as return type for this particular method call
 * which enables all standard code completion features.
 */
public class PhpDiTypeProvider implements PhpTypeProvider2 {
    @Override
    public char getKey() {
        return '\u0230';
    }

    @Nullable
    @Override
    public String getType(PsiElement psiElement) {
        if (DumbService.getInstance(psiElement.getProject()).isDumb()) {
            return null;
        }

        if (!(psiElement instanceof MethodReference)) {
            return null;
        }

        MethodReference methodRef = ((MethodReference) psiElement);

        if (!"get".equals(methodRef.getName())) {
            return null;
        }

        if (methodRef.getParameters().length == 0) {
            return null;
        }

        PsiElement firstParam = methodRef.getParameters()[0];

        if (firstParam instanceof PhpReference) {
            PhpReference ref = (PhpReference)firstParam;
            if (ref.getText().toLowerCase().contains("::class")) {
                return methodRef.getSignature() + "%" + ref.getSignature();
            }
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        int endIndex = s.lastIndexOf("%");
        if (endIndex == -1) {
            return Collections.emptySet();
        }

        // Get FQN from parameter string.
        // Example (PhpStorm 8): #K#C\Foo\Bar::get()%#K#C\Bar\Baz. -> \Bar\Baz.
        // Example (PhpStorm 9): #K#C\Foo\Bar::get()%#K#C\Bar\Baz.class -> \Bar\Baz.class
        String parameter = s.substring(endIndex + 5, s.length());

        if (parameter.contains(".class")) { // for PhpStorm 9
            parameter = parameter.replace(".class", "");
        }

        if (parameter.contains(".")) {
            parameter = parameter.replace(".", "");
        }

        return PhpIndex.getInstance(project).getAnyByFQN(parameter);
    }
}
