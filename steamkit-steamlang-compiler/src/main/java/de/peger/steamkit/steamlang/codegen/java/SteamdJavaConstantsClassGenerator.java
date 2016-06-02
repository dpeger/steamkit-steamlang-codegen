package de.peger.steamkit.steamlang.codegen.java;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class SteamdJavaConstantsClassGenerator extends AbstractSteamdJavaCodeGenerator implements SteamdClassGenerator {

    /**
     * @param pSourceName
     * @param pContext
     */
    public SteamdJavaConstantsClassGenerator(final String pSourceName, final SteamdCodeGenContext pContext) {
        super(pSourceName, pContext);
    }

    @Override
    public void generate(final SteamdCompileClass pSteamdEnum, final JPackage pPackage) {

        try {
            final JDefinedClass tEnumClass = pPackage._class(JMod.PUBLIC | JMod.FINAL, pSteamdEnum.getName(),
                    EClassType.CLASS);
            generateClassHeader(tEnumClass);

            getEnumTypes().put(tEnumClass.fullName(), pSteamdEnum.getTypeReference());

            final AbstractJType tEnumJType = SteamdJTypeMapper.mapSteamdPrimitive(pSteamdEnum.getTypeReference(),
                    getModel());

            // create enum constants
            for (SteamdCompileField tSteamdEnumField : pSteamdEnum.getFields()) {
                final JFieldVar tConstant = tEnumClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, tEnumJType,
                        tSteamdEnumField.getName());

                final IJExpression tInitExpr = SteamdCodeGenUtils.getValueExpression(tSteamdEnumField.getDefaultValue(),
                        tEnumJType, getModel());
                tConstant.init(tInitExpr);

                if (tSteamdEnumField.isDeprecated()) {
                    tConstant.annotate(Deprecated.class);
                }
                final String tComment = tSteamdEnumField.getComment();
                if (StringUtils.isNotEmpty(tComment) && !StringUtils.startsWith(tComment, "//")) {
                    tConstant.javadoc().add(tComment.trim());
                }
            }

        } catch (final JClassAlreadyExistsException e) {
            throw new IllegalStateException("Duplicate class/enum: '" + pSteamdEnum.getName() + "'", e);
        } catch (final NoSuchFieldException | ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

    }
}
