package de.peger.steamkit.steamlang.codegen.java.fields;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JCombinedExpression;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JDirectExpression;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdSerializationConstant;
import de.peger.steamkit.steamlang.compiler.domain.SteamdTypeConstant;

/**
 * @author dpeger
 *
 */
public class BooleanField extends AbstractCodeGenField {

    public BooleanField(final SteamdCompileField pField) {
        super(pField.getType(), pField.getName(), pField.getDefaultValue());
    }

    public static boolean isBooleanField(final SteamdCompileField pCompileField) {
        boolean isBooleanField = StringUtils.isNotEmpty(pCompileField.getSerializationModifier());
        isBooleanField = isBooleanField && (SteamdSerializationConstant.BOOL == SteamdSerializationConstant
                .forValue(pCompileField.getSerializationModifier()));
        isBooleanField = isBooleanField
                && (SteamdTypeConstant.BYTE == SteamdTypeConstant.forValue(pCompileField.getType()));
        return isBooleanField;
    }

    @Override
    public AbstractJType getJType(final JCodeModel pModel) {
        return pModel.BOOLEAN;
    }

    @Override
    public IJExpression getInitExpression(final JCodeModel pModel) {

        if (StringUtils.isNotEmpty(getFieldDefaultValue())) {
            return pModel.BOOLEAN.boxify().staticInvoke("valueOf").arg(getFieldDefaultValue());
        } else {
            return new JDirectExpression("false");
        }
    }

    @Override
    public IJExpression getSizeExpression(final JCodeModel pModel) {
        return pModel.BYTE.boxify().staticRef("BYTES");
    }

    @Override
    protected void addGetter(JDefinedClass pJClass, JFieldVar pJField, final JCodeModel pModel) {
        final JMethod tGetter = JCodeModelUtils.generateGetter(pJClass, pJField);
        tGetter.name("is" + StringUtils.removeStart(tGetter.name(), "get"));
    }

    @Override
    public void addSerializationStatement(final JMethod pDeserialize, final JVar pByteBuffer, final JCodeModel pModel) {
        // private byte validated;
        // public bool Validated { get { return ( validated == 1 ); } set {
        // validated = ( byte )( value ? 1 : 0 ); } }
        final String tBoolFieldNameMember = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final IJExpression tSerializartionValue = new JDirectExpression(
                "(byte) (" + tBoolFieldNameMember + " ? 1 : 0)");
        final JInvocation tSerializationInvocation = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true).staticInvoke("putByte")
                .arg(pByteBuffer).arg(tSerializartionValue);
        pDeserialize.body().add(tSerializationInvocation);
    }

    @Override
    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel) {
        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pDeserialize.owningClass().fields().get(tMemberName);

        final JInvocation tDeserializationInvocation = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true).staticInvoke("getByte")
                .arg(pByteBuffer);
        final IJExpression tValueExpression = new JCombinedExpression(tDeserializationInvocation,
                new JDirectExpression(" == 1 ? true : false"));

        pDeserialize.body().add(tMember.assign(tValueExpression));
    }

}
