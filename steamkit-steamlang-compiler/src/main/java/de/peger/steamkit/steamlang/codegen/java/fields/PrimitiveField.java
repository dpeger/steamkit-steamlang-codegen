package de.peger.steamkit.steamlang.codegen.java.fields;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.java.SteamdJTypeMapper;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdTypeConstant;

/**
 * @author dpeger
 *
 */
public class PrimitiveField extends AbstractCodeGenField {

    public PrimitiveField(final SteamdCompileField pField) {
        super(pField.getType(), pField.getName(), pField.getDefaultValue());
    }

    public static boolean isPrimitiveField(final SteamdCompileField pCompileField) {
        boolean isPrimitiveField = SteamdJTypeMapper.isPrimitive(pCompileField.getType());
        isPrimitiveField = isPrimitiveField && StringUtils.isEmpty(pCompileField.getSerializationModifier());
        return isPrimitiveField;
    }

    @Override
    public AbstractJType getJType(final JCodeModel pModel) {
        return SteamdJTypeMapper.mapSteamdPrimitive(getFieldType(), pModel);
    }

    @Override
    public IJExpression getInitExpression(final JCodeModel pModel) {

        final AbstractJType tJType = getJType(pModel);
        final String tValue = StringUtils.defaultString(getFieldDefaultValue(), "0");
        try {
            return SteamdCodeGenUtils.getValueExpression(tValue, tJType, pModel);
        } catch (final NoSuchFieldException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to compute init expression for field '" + getFieldName() + "'", e);
        }
    }

    @Override
    public IJExpression getSizeExpression(final JCodeModel pModel) {
        return SteamdJTypeMapper.mapSteamdPrimitive(getFieldSerializationType(), pModel).boxify().staticRef("BYTES");
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel) {

        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pSerialize.owningClass().fields().get(tMemberName);

        final JInvocation tByteBufferPut = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true)
                .staticInvoke(getSerializationMethod()).arg(pByteBuffer).arg(tMember);

        pSerialize.body().add(tByteBufferPut);
    }

    @Override
    protected String getFieldSerializationType() {
        final SteamdTypeConstant tFieldType = SteamdTypeConstant.forValue(getFieldType());
        switch (tFieldType) {
        case ULONG:
        case LONG:
            return "long";
        case UINT:
        case INT:
            return "int";
        case USHORT:
        case SHORT:
            return "short";
        case BYTE:
            return "byte";
        }
        throw new IllegalStateException(
                "Unable to determine de-serialization method for field type '" + getFieldSerializationType() + "'");
    }

    protected String getSerializationMethod() {
        final SteamdTypeConstant tFieldType = SteamdTypeConstant.forValue(getFieldType());
        switch (tFieldType) {
        case ULONG:
        case LONG:
            return "putLong";
        case UINT:
            return "putUnsignedInt";
        case INT:
            return "putInt";
        case USHORT:
            return "putUnsignedShort";
        case SHORT:
            return "putShort";
        case BYTE:
            return "putByte";
        }
        throw new IllegalStateException(
                "Unable to determine de-serialization method for field type '" + getFieldSerializationType() + "'");
    }

    @Override
    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel) {

        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pDeserialize.owningClass().fields().get(tMemberName);

        final JInvocation tByteBufferGet = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true)
                .staticInvoke(getDeserializationMethod()).arg(pByteBuffer);

        pDeserialize.body().add(tMember.assign(tByteBufferGet));
    }

    protected String getDeserializationMethod() {
        final SteamdTypeConstant tFieldType = SteamdTypeConstant.forValue(getFieldSerializationType());
        switch (tFieldType) {
        case ULONG:
        case LONG:
            return "getLong";
        case UINT:
            return "getUnsignedInt";
        case INT:
            return "getInt";
        case USHORT:
            return "getUnsignedShort";
        case SHORT:
            return "getShort";
        case BYTE:
            return "getByte";
        }
        throw new IllegalStateException(
                "Unable to determine de-serialization method for field type '" + getFieldSerializationType() + "'");
    }

}
