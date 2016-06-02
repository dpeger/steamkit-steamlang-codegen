package de.peger.steamkit.steamlang.codegen.java.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDirectClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.java.SteamdJTypeMapper;
import de.peger.steamkit.steamlang.codegen.java.fields.ConstantField;
import de.peger.steamkit.steamlang.codegen.java.fields.ProtoBufferField;
import de.peger.steamkit.steamlang.codegen.java.fields.ProtoMaskField;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JDirectExpression;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdSerializationConstant;

/**
 * @author dpeger
 *
 */
public class SteamdCodeGenUtils {

    public static final String BYTE_BUFFER_UTIL = "de.peger.steamkit.steamlang.steamd.util.ByteBufferUtil";

    public static final String PROTOBUF_UTIL = "de.peger.steamkit.steamlang.steamd.util.ProtoBufUtil";

    public static List<IJExpression> getValueExpressionParts(final String pValue, final boolean pIsReferenceValue,
            final AbstractJType pJType) {

        if (pIsReferenceValue) {
            final List<IJExpression> tValues = new ArrayList<>();
            for (final String tValuePart : pValue.split("\\|")) {
                tValues.add(JExpr.direct(tValuePart.trim()));
            }
            return tValues;
        } else if (StringUtils.isNotEmpty(pValue)) {
            if (pJType.equals(pJType.owner().LONG)) {
                return Collections.singletonList(JExpr.direct(pValue + "L"));
            } else {
                return Collections.singletonList(JExpr.cast(pJType, JExpr.direct(pValue)));
            }
        } else {
            return Collections.singletonList(JExpr._null());
        }
    }

    public static IJExpression getValueExpression(final String pValue, final AbstractJType pJType,
            final JCodeModel pModel) throws NoSuchFieldException, ClassNotFoundException {

        if (pValue.matches("-?\\d+") || pValue.matches("0x\\w+")) {
            if (pJType.equals(pModel.LONG)) {
                return new JDirectExpression(pValue + "L");
            } else if (pJType.equals(pModel.INT)) {
                return new JDirectExpression(pValue);
            } else {
                return new JDirectExpression("(" + pJType.name() + ") " + pValue);
            }
        } else if (pValue.matches("\\w+( \\| \\w+)*")) {
            return new JDirectExpression(pValue);
        } else if (pValue.matches("\\w+::\\w+")) {
            return getFieldReference(pValue, pModel);
        } else if (pValue.matches("\\w+\\.MaxValue")) {
            final String tType = pValue.split("\\.")[0];
            return SteamdJTypeMapper.mapSteamdPrimitive(tType, pJType.owner()).boxify().staticRef("MAX_VALUE");
        } else {
            return JExpr._null();
        }
    }

    private static IJExpression getFieldReference(final String pValue, final JCodeModel pModel)
            throws ClassNotFoundException, NoSuchFieldException {
        final String[] tSplit = pValue.split("::");
        final String tType = tSplit[0];
        final String tField = tSplit[1];
        final JDefinedClass tJClass = JCodeModelUtils.findExistingJClass(tType, pModel);
        final JFieldVar tJField = Optional.ofNullable(tJClass.fields().get(tField))
                .orElseThrow(() -> new NoSuchFieldException("No field '" + tField + "' in class '" + tType + "'"));
        return tJClass.staticRef(tJField);
    }

    public static String computeFieldName(final SteamdCompileField pParserField, final String pPrefix) {

        if (ConstantField.isConstantField(pParserField)) {
            return pParserField.getName();
        }

        if (SteamdSerializationConstant.PROTOMASK.getValue().equals(pParserField.getSerializationModifier())
                || SteamdSerializationConstant.PROTOMASKGC.getValue().equals(pParserField.getSerializationModifier())) {
            return JCodeModelUtils.PREFIX_MEMBER + ProtoMaskField.PROTO_FIELD_MSG;
        }

        final String tFieldName = pPrefix + JCodeModelUtils.extractFieldName(pParserField.getName());
        return tFieldName;
    }

    /**
     * @param pSteamdClass
     * @param pModel
     * @return
     */
    public static JDirectClass computeInterface(final SteamdCompileClass pSteamdClass, final JCodeModel pModel) {

        if (pSteamdClass.getName().startsWith("MsgGCHdr")) {
            return pModel.directClass("de.peger.steamkit.steamlang.steamd.ISteamSerializableHeader");
        } else if (pSteamdClass.getName().startsWith("MsgHdr")) {
            return pModel.directClass("de.peger.steamkit.steamlang.steamd.ISteamSerializableHeader");
        } else if (pSteamdClass.getName().startsWith("Msg")) {
            return pModel.directClass("de.peger.steamkit.steamlang.steamd.ISteamSerializableMessage");
        } else {
            return pModel.directClass("de.peger.steamkit.steamlang.steamd.ISteamSerializable");
        }
    }

    /**
     * @param pField
     *            The to be initialized field
     * @param pModel
     *            The code model
     * 
     * @return Expression that is to be used to initialize <code>pField</code>
     * 
     * @throws ClassNotFoundException
     *             If the type of <code>pField</code> is not known
     * @throws NoSuchFieldException
     *             If the init expression references a non existing field
     */
    public static IJExpression computeFieldInitExpression(final SteamdCompileField pField, final JCodeModel pModel,
            final SteamdCodeGenContext pContext) throws ClassNotFoundException, NoSuchFieldException {

        if (pField.getDefaultValue() != null) {
            final AbstractJType tFieldType = SteamdJTypeMapper.mapSteamdType(pField, pModel, pContext);
            return getValueExpression(pField.getDefaultValue(), tFieldType, pModel);
        } else if (ProtoBufferField.isProtoBufferField(pField)) {
            final String[] tSplit = pField.getType().split("\\.");
            final String tProtoName = tSplit[tSplit.length - 1];
            return new JDirectExpression(tProtoName + ".getDefaultInstance()");
        } else if (SteamdJTypeMapper.mapSteamdType(pField, pModel, pContext) == pModel.BOOLEAN) {
            return new JDirectExpression("false");
        } else if (SteamdJTypeMapper.isPrimitive(pField.getType())) {
            return new JDirectExpression("0");
        } else {
            return JExpr._null();
        }
    }

    /**
     * @param pSteamdField
     * @return
     */
    public static SteamdCompileField extractProtoLengthField(final SteamdCompileField pSteamdField,
            final SteamdCompileClass pSteamdClass) {

        final Pattern tProtoPattern = Pattern.compile(ProtoBufferField.PROTO_PATTERN);
        final Matcher tMatcher = tProtoPattern.matcher(pSteamdField.getSerializationModifier());
        if (!tMatcher.matches()) {
            return null;
        }

        final String tLengthFieldName = tMatcher.group(ProtoBufferField.PROTO_PATTERN_GROUP_LENGTH);
        final SteamdCompileField tLengthField = pSteamdClass.getFields().stream().filter(p -> {
            return Objects.equals(p.getName(), tLengthFieldName);
        }).findFirst().get();

        return tLengthField;
    }

    public static JFieldVar getMember(final JDefinedClass pJClass, final SteamdCompileField tField) {
        final String tFieldNameMember = SteamdCodeGenUtils.computeFieldName(tField, JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pJClass.fields().get(tFieldNameMember);
        return tMember;
    }

}
