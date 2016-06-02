package de.peger.steamkit.steamlang.codegen.java.fields;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDocComment;
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

/**
 * @author dpeger
 *
 */
public class ProtoMaskField extends PrimitiveField {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoMaskField.class);

    public static final String PROTO_FIELD_MSG = "EMsg";

    /**
     * @param pField
     */
    public ProtoMaskField(final SteamdCompileField pField) {
        super(pField);
    }

    public static boolean isProtoMaskField(final SteamdCompileField pField) {
        boolean isProtoMaskField = StringUtils.isNotEmpty(pField.getSerializationModifier());
        isProtoMaskField = isProtoMaskField && (SteamdSerializationConstant.PROTOMASK == SteamdSerializationConstant
                .forValue(pField.getSerializationModifier())
                || SteamdSerializationConstant.PROTOMASKGC == SteamdSerializationConstant
                        .forValue(pField.getSerializationModifier()));
        return isProtoMaskField;
    }

    @Override
    public String getFieldName(final String pPrefix) {
        return pPrefix + PROTO_FIELD_MSG;
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel) {

        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pSerialize.owningClass().fields().get(tMemberName);
        final IJExpression tSerializartionValue = new JCombinedExpression(tMember,
                new JDirectExpression(" | ProtoMask"));

        final JInvocation tByteBufferPut = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true)
                .staticInvoke(getSerializationMethod()).arg(pByteBuffer).arg(tSerializartionValue);

        pSerialize.body().add(tByteBufferPut);
    }

    private JDefinedClass findEmsgEnumJClass(final JCodeModel pModel) {
        try {
            return JCodeModelUtils.findExistingJClass(PROTO_FIELD_MSG, pModel);
        } catch (final ClassNotFoundException e) {
            LOGGER.warn("No existing class found for name '{}'.", PROTO_FIELD_MSG);
            return JCodeModelUtils.createJDefinedClass(PROTO_FIELD_MSG, pModel, true);
        }
    }

    @Override
    protected void addGetter(final JDefinedClass pJClass, final JFieldVar tJField, final JCodeModel pModel) {
        final JMethod tGetter = JCodeModelUtils.generateGetter(pJClass, tJField);
        if (JCodeModelUtils.implementsInterface(pJClass, "ISteamSerializableMessage")) {
            tGetter.annotate(Override.class);
        }
        final JDefinedClass tJEnumClass = findEmsgEnumJClass(pModel);
        tGetter.javadoc().addTag(JDocComment.TAG_SEE).append(tJEnumClass.fullName());
    }

    @Override
    protected void addSetter(final JDefinedClass pJClass, final JFieldVar tJField, final JCodeModel pModel) {
        final JMethod tSetter = JCodeModelUtils.generateSetter(pJClass, tJField);
        if (JCodeModelUtils.implementsInterface(pJClass, "ISteamSerializableHeader")) {
            tSetter.annotate(Override.class);
        }
        final JDefinedClass tJEnumClass = findEmsgEnumJClass(pModel);
        tSetter.javadoc().addTag(JDocComment.TAG_SEE).append(tJEnumClass.fullName());
    }

    @Override
    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel) {

        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pDeserialize.owningClass().fields().get(tMemberName);

        final JInvocation tByteBufferGet = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true)
                .staticInvoke(getDeserializationMethod()).arg(pByteBuffer);

        pDeserialize.body()
                .add(tMember.assign(new JCombinedExpression(tByteBufferGet, new JDirectExpression(" | EMsgMask"))));
    }

}