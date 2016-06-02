package de.peger.steamkit.steamlang.codegen.java.fields;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JDirectExpression;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class ProtoBufferLengthField extends PrimitiveField {

    public ProtoBufferLengthField(final SteamdCompileField pField) {
        super(pField);
    }

    /**
     * @param pSteamdField
     * @return
     */
    public static boolean isProtoBufferLengthField(final SteamdCompileField pSteamdField,
            final SteamdCompileClass pSteamdClass) {

        final List<SteamdCompileField> tProtoFields = pSteamdClass.getFields().stream()
                .filter(ProtoBufferField::isProtoBufferField).collect(Collectors.toList());
        for (final SteamdCompileField tProtoField : tProtoFields) {
            final SteamdCompileField tLengthField = SteamdCodeGenUtils.extractProtoLengthField(tProtoField,
                    pSteamdClass);
            if (Objects.equals(tLengthField.getName(), pSteamdField.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addField(JDefinedClass pJClass, JCodeModel pModel) {
        // No class member
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel) {

        final String tLocalName = getFieldName(JCodeModelUtils.PREFIX_LOCAL);

        final JInvocation tByteBufferPut = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true)
                .staticInvoke(getSerializationMethod()).arg(pByteBuffer).arg(new JDirectExpression(tLocalName));

        pSerialize.body().add(tByteBufferPut);
    }

    @Override
    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel) {

        final JInvocation tByteBufferGet = JCodeModelUtils
                .createJDefinedClass(SteamdCodeGenUtils.BYTE_BUFFER_UTIL, pModel, true)
                .staticInvoke(getDeserializationMethod()).arg(pByteBuffer);

        final String tLocalName = getFieldName(JCodeModelUtils.PREFIX_LOCAL);
        pDeserialize.body().decl(JMod.FINAL, getJType(pModel), tLocalName).init(tByteBufferGet);
    }
}
