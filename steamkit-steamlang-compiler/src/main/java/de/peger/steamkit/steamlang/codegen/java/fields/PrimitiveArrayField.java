package de.peger.steamkit.steamlang.codegen.java.fields;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JArrayClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.java.SteamdJTypeMapper;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JDirectExpression;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class PrimitiveArrayField extends AbstractCodeGenField {

    private static final String PATTERN_GROUP_SIZE = "size";

    private static final String PATTERN_GROUP_TYPE = "type";

    private static final String PATTERN = "(?<" + PATTERN_GROUP_TYPE + ">\\w+)<(?<" + PATTERN_GROUP_SIZE + ">\\d+)>";

    private final Integer mFieldSize;

    public PrimitiveArrayField(final SteamdCompileField pField) {

        super(extractType(pField), pField.getName(), pField.getDefaultValue());

        final Pattern tPattern = Pattern.compile(PATTERN);
        final Matcher tMatcher = tPattern.matcher(pField.getType());
        tMatcher.matches();
        mFieldSize = Integer.valueOf(tMatcher.group(PATTERN_GROUP_SIZE));
    }

    private static String extractType(final SteamdCompileField pField) {

        final Pattern tPattern = Pattern.compile(PATTERN);
        final Matcher tMatcher = tPattern.matcher(pField.getType());
        if (!tMatcher.matches()) {
            throw new IllegalArgumentException(
                    "Type string of array type must be of the form 'type<size>', but was '" + pField.getType() + "'");
        }

        final String tFieldType = tMatcher.group(PATTERN_GROUP_TYPE);
        if (!SteamdJTypeMapper.isPrimitive(tFieldType)) {
            throw new IllegalArgumentException(
                    "Type type of primitve array must be a primitive, but was '" + tFieldType + "'");
        }

        return tFieldType;
    }

    public static boolean isPrimitiveArrayField(final SteamdCompileField pCompileField) {
        return pCompileField.getType().matches(PATTERN);
    }

    @Override
    public JArrayClass getJType(final JCodeModel pModel) {
        final AbstractJType tElementType = SteamdJTypeMapper.mapSteamdPrimitive(getFieldType(), pModel);
        return tElementType.array();
    }

    @Override
    public IJExpression getInitExpression(final JCodeModel pModel) {

        if (StringUtils.isNotEmpty(getFieldDefaultValue())) {
            return new JDirectExpression(getFieldDefaultValue());
        } else {
            final JArrayClass tJType = getJType(pModel);
            return JExpr.newArray(tJType.elementType(), mFieldSize);
        }
    }

    @Override
    public IJExpression getSizeExpression(final JCodeModel pModel) {
        return new JDirectExpression(mFieldSize.toString());
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel) {

        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pSerialize.owningClass().fields().get(tMemberName);

        final JInvocation tByteBufferPut = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true).staticInvoke("putBytes")
                .arg(pByteBuffer).arg(tMember);

        pSerialize.body().add(tByteBufferPut);
    }

    @Override
    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel) {

        final String tMemberName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final JFieldVar tMember = pDeserialize.owningClass().fields().get(tMemberName);

        final JInvocation tByteBufferGet = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true).staticInvoke("getBytes")
                .arg(pByteBuffer).arg(mFieldSize);

        pDeserialize.body().add(tMember.assign(tByteBufferGet));
    }

}
