package de.peger.steamkit.steamlang.codegen.java.fields;

import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDocComment;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.java.SteamdJTypeMapper;
import de.peger.steamkit.steamlang.codegen.java.SteamdJavaCodeGenerator;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class EnumField extends PrimitiveField {

    private final String mFieldSerializationType;

    private final String mFieldEnumType;

    public EnumField(final SteamdCompileField pField, final SteamdCodeGenContext pContext) {
        super(pField);

        mFieldEnumType = pField.getType();

        final String tEnumClassName = findEnumJClass(
                (JCodeModel) pContext.getExtraSetting(SteamdJavaCodeGenerator.SETTING_CODE_MODEL)).name();
        @SuppressWarnings("unchecked")
        final Map<String, String> tEnumTypeMappings = ((Map<String, String>) pContext
                .getExtraSetting(SteamdJavaCodeGenerator.SETTING_ENUM_TYPES));
        mFieldSerializationType = Optional.ofNullable(tEnumTypeMappings.get(tEnumClassName)).orElse("long");

    }

    public static boolean isEnumField(final SteamdCompileField pCompileField) {
        boolean isEnumField = pCompileField.getType().matches("\\w+")
                && !SteamdJTypeMapper.isPrimitive(pCompileField.getType());
        return isEnumField;
    }

    @Override
    protected String getFieldType() {
        return mFieldSerializationType;
    }

    @Override
    public AbstractJType getJType(final JCodeModel pModel) {

        final JDefinedClass tJClass = findEnumJClass(pModel);
        if (tJClass.getClassType() == EClassType.ENUM) {
            return tJClass;
        } else {
            return SteamdJTypeMapper.mapSteamdPrimitive(mFieldSerializationType, pModel);
        }
    }

    @Nonnull
    private JDefinedClass findEnumJClass(final JCodeModel pModel) {
        try {
            return JCodeModelUtils.findExistingJClass(mFieldEnumType, pModel);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("No existing class '" + getFieldType() + "'.", e);
        }
    }

    @Override
    protected void addGetter(final JDefinedClass pJClass, final JFieldVar pJField, final JCodeModel pModel) {
        final JMethod tGetter = JCodeModelUtils.generateGetter(pJClass, pJField);
        final JDefinedClass tJEnumClass = findEnumJClass(pModel);
        tGetter.javadoc().addTag(JDocComment.TAG_SEE).append(tJEnumClass.fullName());
    }

    @Override
    protected void addSetter(final JDefinedClass pJClass, final JFieldVar pJField, final JCodeModel pModel) {
        final JMethod tSetter = JCodeModelUtils.generateSetter(pJClass, pJField);
        final JDefinedClass tJEnumClass = findEnumJClass(pModel);
        tSetter.javadoc().addTag(JDocComment.TAG_SEE).append(tJEnumClass.fullName());
    }
}
