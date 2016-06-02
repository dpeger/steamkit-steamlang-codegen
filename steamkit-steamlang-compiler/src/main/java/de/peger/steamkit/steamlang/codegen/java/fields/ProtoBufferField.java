package de.peger.steamkit.steamlang.codegen.java.fields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JDirectExpression;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JNewLineVar;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdSerializationConstant;

/**
 * @author dpeger
 *
 */
public class ProtoBufferField extends AbstractCodeGenField {

    public static final String PROTO_PATTERN_GROUP_LENGTH = "length";
    public static final String PROTO_PATTERN = SteamdSerializationConstant.PROTO.getValue() + "<(?<"
            + PROTO_PATTERN_GROUP_LENGTH + ">\\w+)>";

    private final String mLengthField;
    private final String mMappedFieldType;

    /**
     * @param pField
     */
    public ProtoBufferField(final SteamdCompileField pField, final SteamdCodeGenContext pContext) {
        super(pField.getType(), pField.getName(), pField.getDefaultValue());

        final Pattern tPattern = Pattern.compile(PROTO_PATTERN);
        final Matcher tMatcher = tPattern.matcher(pField.getSerializationModifier());
        if (!tMatcher.matches()) {
            throw new IllegalArgumentException(
                    "Serialization modifier protobuf fields must be of the form 'proto<lengthField>', but was '"
                            + pField.getSerializationModifier() + "'");
        }

        final String tTypePackage = getFieldType().substring(0, getFieldType().lastIndexOf('.'));
        final String tPlainType = getFieldType().substring(getFieldType().lastIndexOf('.'), getFieldType().length());
        mMappedFieldType = pContext.getImportPackageMapping(tTypePackage) + tPlainType;
        mLengthField = tMatcher.group(PROTO_PATTERN_GROUP_LENGTH);
    }

    /**
     * @param pField
     * @return
     */
    public static boolean isProtoBufferField(final SteamdCompileField pField) {
        final boolean isProtoBufferField = pField.getSerializationModifier() == null ? false
                : pField.getSerializationModifier().matches(PROTO_PATTERN);
        return isProtoBufferField;
    }

    @Override
    public AbstractJType getJType(final JCodeModel pModel) {
        return JCodeModelUtils.createJDefinedClass(mMappedFieldType, pModel, true);
    }

    @Override
    public IJExpression getInitExpression(final JCodeModel pModel) {
        return getJType(pModel).boxify().staticInvoke("getDefaultInstance");
    }

    @Override
    public IJExpression getSizeExpression(final JCodeModel pModel) {
        final String tLengthFieldNameLocal = JCodeModelUtils.PREFIX_LOCAL
                + JCodeModelUtils.extractFieldName(mLengthField);
        return new JDirectExpression(tLengthFieldNameLocal);
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel) {

        final String tLocalName = getFieldName(JCodeModelUtils.PREFIX_LOCAL);
        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JVar tLocalVar = new JVar(JMods.forVar(JMod.FINAL), pModel._ref(byte[].class), tLocalName,
                new JDirectExpression(tMemberName + ".toByteArray()"));

        final String tLengthFieldLocalName = JCodeModelUtils.PREFIX_LOCAL
                + JCodeModelUtils.extractFieldName(mLengthField);
        final JVar tLengthFieldLocalVar = new JVar(JMods.forVar(JMod.FINAL), pModel.INT, tLengthFieldLocalName,
                new JDirectExpression(tLocalName + ".length"));

        pSerialize.body().insertBefore(tLocalVar, pByteBuffer);
        pSerialize.body().insertBefore(tLengthFieldLocalVar, pByteBuffer);
        pSerialize.body().insertBefore(new JNewLineVar(), pByteBuffer);

        final JInvocation tByteBufferPut = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true).staticInvoke("putBytes")
                .arg(pByteBuffer).arg(tLocalVar);

        pSerialize.body().add(tByteBufferPut);
    }

    @Override
    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel) {

        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pDeserialize.owningClass().fields().get(tMemberName);

        final String tLengthFieldLocalName = JCodeModelUtils.PREFIX_LOCAL
                + JCodeModelUtils.extractFieldName(mLengthField);
        final IJExpression tLengthExpression = new JDirectExpression(tLengthFieldLocalName);

        final JInvocation tProtoUtilsParse = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.PROTOBUF_UTIL, pModel, true).staticInvoke("parseFrom")
                .arg(pByteBuffer).arg(tLengthExpression).arg(getJType(pModel).boxify().staticRef("class"));

        pDeserialize.body().add(tMember.assign(tProtoUtilsParse));
    }

}
