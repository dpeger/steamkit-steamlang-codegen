package de.peger.steamkit.steamlang.codegen.java.util;

import java.util.Iterator;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.java.fields.ConstantField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class JCodeModelUtils {

    public static final String PREFIX_MEMBER = "m";

    public static final String PREFIX_PARAMETER = "p";

    public static final String PREFIX_LOCAL = "t";

    /**
     * Safely creates a {@link JDefinedClass} by checking if the class already
     * exists.
     * 
     * @param pFullyQualifiedClassName
     *            Name of the to e generated class
     * @param pModel
     *            Java code model
     * @param pHide
     *            if <code>true</code> the new class is marked as hidden
     * 
     * @return New {@link JDefinedClass} or, if already present in
     *         <code>pModel</code>, the existing one.
     */
    public static JDefinedClass createJDefinedClass(final String pFullyQualifiedClassName, final JCodeModel pModel,
            boolean pHide) {

        final JDefinedClass tJClass = pModel._getClass(pFullyQualifiedClassName);
        if (tJClass != null) {
            return tJClass;
        }

        try {
            final JDefinedClass tNewClass = pModel._class(pFullyQualifiedClassName);
            if (pHide) {
                tNewClass.hide();
            }
            return tNewClass;
        } catch (final JClassAlreadyExistsException e) {
            return pModel._getClass(pFullyQualifiedClassName);
        }
    }

    public static @Nonnull JDefinedClass findExistingJClass(final String pClassName, final JCodeModel pModel)
            throws ClassNotFoundException {
    
        JDefinedClass tReturnJClass = null;
        package_loop: for (final Iterator<JPackage> tPackageIt = pModel.packages(); tPackageIt.hasNext();) {
            final JPackage tJPackage = tPackageIt.next();
            for (final JDefinedClass tJClass : tJPackage.classes()) {
                if (Objects.equals(pClassName, tJClass.name())) {
                    tReturnJClass = tJClass;
                    break package_loop;
                }
            }
        }
    
        if (tReturnJClass == null) {
            throw new ClassNotFoundException(pClassName);
        }
    
        return tReturnJClass;
    }

    public static String extractFieldName(final String pVarName) {

        String tName = pVarName;
        if (tName.matches("(" + PREFIX_LOCAL + "|" + PREFIX_MEMBER + "|" + PREFIX_PARAMETER + ")[A-Z]+.*")) {
            // prefix character stripped
            tName = tName.substring(1);
        } else if (tName.length() > 1) {
            // convert first letter to uppercase
            tName = Character.toUpperCase(tName.charAt(0)) + tName.substring(1);
        }

        return tName;
    }

    public static int computeFieldModifiers(final SteamdCompileField pField) {
        int tFieldModifiers = JMod.PRIVATE;
        if (ConstantField.isConstantField(pField)) {
            tFieldModifiers = JMod.PUBLIC | JMod.FINAL | JMod.STATIC;
        }
        return tFieldModifiers;
    }

    public static boolean implementsInterface(final AbstractJClass pJClass, final String pInterface) {

        for (final Iterator<AbstractJClass> tIterator = pJClass._implements(); tIterator.hasNext();) {
            final AbstractJClass tInterface = tIterator.next();
            if (Objects.equals(pInterface, tInterface.name())) {
                return true;
            }
        }

        return false;
    }

    public static JMethod generateGetter(final JDefinedClass pClass, final JFieldVar pJField) {
        final String tFieldName = JCodeModelUtils.extractFieldName(pJField.name());
        final JMethod tGetter = pClass.method(JMod.PUBLIC, pJField.type(), "get" + tFieldName);
        tGetter.body()._return(pJField);
        return tGetter;
    }

    public static JMethod generateSetter(final JDefinedClass pClass, final JFieldVar pJField) {
        final String tFieldName = JCodeModelUtils.extractFieldName(pJField.name());
        final JMethod tSetter = pClass.method(JMod.PUBLIC, Void.TYPE, "set" + tFieldName);
        final JVar tSetterParam = tSetter.param(JMod.FINAL, pJField.type(),
                JCodeModelUtils.PREFIX_PARAMETER + tFieldName);
        tSetter.body().assign(pJField, tSetterParam);
        return tSetter;
    }

}
