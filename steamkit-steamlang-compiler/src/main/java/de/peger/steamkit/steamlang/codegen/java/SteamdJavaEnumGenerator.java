package de.peger.steamkit.steamlang.codegen.java;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JEnumConstant;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JForEach;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class SteamdJavaEnumGenerator extends AbstractSteamdJavaCodeGenerator implements SteamdClassGenerator {

    /**
     * @param pContext
     */
    public SteamdJavaEnumGenerator(final String pSourceName, final SteamdCodeGenContext pContext) {
        super(pSourceName, pContext);
    }

    @Override
    public void generate(final SteamdCompileClass pSteamdEnum, final JPackage pPackage) {

        try {
            final JDefinedClass tEnumClass = pPackage._class(JMod.PUBLIC, pSteamdEnum.getName(), EClassType.ENUM);
            generateClassHeader(tEnumClass);

            getEnumTypes().put(tEnumClass.fullName(), pSteamdEnum.getTypeReference());

            final AbstractJType tEnumJType = SteamdJTypeMapper.mapSteamdPrimitive(pSteamdEnum.getTypeReference(),
                    getModel());

            final JFieldVar tValueMember = tEnumClass.field(JMod.PRIVATE | JMod.FINAL, tEnumJType,
                    JCodeModelUtils.PREFIX_MEMBER + "value");

            // create getter for mValue
            final JMethod tValueGetter = JCodeModelUtils.generateGetter(tEnumClass, tValueMember);

            // create constructors
            {
                final JMethod tEnumConstuctor = tEnumClass.constructor(JMod.PRIVATE);
                final JVar tConstValueParam = tEnumConstuctor.param(JMod.FINAL, tEnumJType, "pValue");
                tEnumConstuctor.body().assign(tValueMember, tConstValueParam);
            }

            if (pSteamdEnum.getModifiers().contains("flags")) {
                final JMethod tEnumConstuctor = tEnumClass.constructor(JMod.PRIVATE);
                final JVar tEnumsParam = tEnumConstuctor.varParam(tEnumClass, "pValues");
                final JVar tValue = tEnumConstuctor.body().decl(tEnumJType, "tValue",
                        SteamdCodeGenUtils.getValueExpression("0", tEnumJType, getModel()));
                final JForEach tForEach = tEnumConstuctor.body().forEach(tEnumClass, "tCurrVal", tEnumsParam);
                tForEach.body().assign(tValue, JExpr.cast(tEnumJType, tValue.bor(tForEach.var().invoke(tValueGetter))));
                tEnumConstuctor.body().assign(tValueMember, tValue);
            }

            // create enum constants
            for (SteamdCompileField tSteamdEnumField : pSteamdEnum.getFields()) {
                JEnumConstant tConstant = tEnumClass.enumConstant(tSteamdEnumField.getName());
                for (final IJExpression tValueExpr : SteamdCodeGenUtils.getValueExpressionParts(
                        tSteamdEnumField.getDefaultValue(), tSteamdEnumField.isReferenceValue(), tEnumJType)) {
                    tConstant.arg(tValueExpr);
                }
                if (tSteamdEnumField.isDeprecated()) {
                    tConstant.annotate(Deprecated.class);
                }
                if (StringUtils.isNotEmpty(tSteamdEnumField.getComment())) {
                    tConstant.javadoc().add(tSteamdEnumField.getComment());
                }
            }

            // create enum map for value-based lookup
            final AbstractJClass tJMap = getModel().ref(Map.class).narrow(tEnumJType.boxify(), tEnumClass);
            final JFieldVar tValuesMap = tEnumClass.field(JMod.PRIVATE | JMod.FINAL | JMod.STATIC, tJMap, "VALUES");

            {
                final JVar tInitMap = tEnumClass.init().decl(JMod.FINAL, tJMap, "tMap",
                        JExpr._new(getModel().ref(ConcurrentHashMap.class).narrow(tEnumJType.boxify(), tEnumClass)));
                final JForEach tInitLoop = tEnumClass.init().forEach(tEnumClass, "tEnum",
                        tEnumClass.staticInvoke("values"));
                tInitLoop.body()
                        .add(tInitMap.invoke("put").arg(tInitLoop.var().invoke(tValueGetter)).arg(tInitLoop.var()));
                tEnumClass.init().assign(tValuesMap,
                        getModel().ref(Collections.class).staticInvoke("unmodifiableMap").arg(tInitMap));
            }

            {
                final JMethod tValueOf = tEnumClass.method(JMod.PUBLIC | JMod.STATIC, tEnumClass, "valueOf");
                final JVar tValueOfParam = tValueOf.param(JMod.FINAL, tEnumJType, "pValue");
                tValueOf.body()._return(tValuesMap.invoke("get").arg(tValueOfParam));
            }

        } catch (final JClassAlreadyExistsException e) {
            throw new IllegalStateException("Duplicate class/enum: '" + pSteamdEnum.getName() + "'", e);
        } catch (final NoSuchFieldException | ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
