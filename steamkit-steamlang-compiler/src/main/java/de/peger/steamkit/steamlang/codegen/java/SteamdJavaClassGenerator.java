package de.peger.steamkit.steamlang.codegen.java;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JDirectClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.java.fields.CodeGenField;
import de.peger.steamkit.steamlang.codegen.java.fields.FieldFactory;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JCombinedExpression;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JDirectExpression;
import de.peger.steamkit.steamlang.codegen.java.jcodemodel.JNewLine;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class SteamdJavaClassGenerator extends AbstractSteamdJavaCodeGenerator implements SteamdClassGenerator {

    /**
     * @param pContext
     *            The code generation context
     */
    public SteamdJavaClassGenerator(final String pSourceName, final SteamdCodeGenContext pContext) {
        super(pSourceName, pContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.peger.steamkit.steamlang.codegen.SteamdClassGenerator#generate(de.
     * peger.steamkit.steamlang.parser.domain.SteamdParserClass,
     * com.sun.codemodel.JPackage, com.sun.codemodel.JCodeModel)
     */
    @Override
    public void generate(final SteamdCompileClass pSteamdClass, final JPackage pPackage) {

        try {
            final JDefinedClass tJClass = pPackage._class(JMod.PUBLIC, pSteamdClass.getName(), EClassType.CLASS);
            generateClassHeader(tJClass);

            final JDirectClass tInterface = SteamdCodeGenUtils.computeInterface(pSteamdClass, getModel());
            tJClass._implements(tInterface);

            final List<CodeGenField> tCodeGenFields = new ArrayList<>();
            for (final SteamdCompileField tField : pSteamdClass.getFields()) {
                final CodeGenField tCodeGenField = FieldFactory.getField(tField, pSteamdClass, getContext());
                tCodeGenFields.add(tCodeGenField);
                generateField(tCodeGenField, tJClass);
            }

            generateMsgGetter(pSteamdClass, tJClass);

            generateSerialize(tCodeGenFields, pSteamdClass, tJClass);
            generateDeserialize(tCodeGenFields, pSteamdClass, tJClass);

        } catch (final JClassAlreadyExistsException e) {
            throw new IllegalStateException("Duplicate class/enum: '" + pSteamdClass.getName() + "'", e);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Unknown class. Check spelling or import dependencies.", e);
        } catch (final NoSuchFieldException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void generateField(final CodeGenField pField, final JDefinedClass pJClass)
            throws ClassNotFoundException, NoSuchFieldException {

        pField.addField(pJClass, getModel());
    }

    /**
     * @param pSteamdClass
     * @param pJClass
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     */
    private void generateMsgGetter(final SteamdCompileClass pSteamdClass, final JDefinedClass pJClass)
            throws ClassNotFoundException, NoSuchFieldException {

        final String tTypeReference = pSteamdClass.getTypeReference();
        if (JCodeModelUtils.implementsInterface(pJClass, "ISteamSerializableMessage")
                && StringUtils.isNotEmpty(tTypeReference)) {
            if (pJClass.getMethod("getEMsg", new AbstractJType[0]) != null) {
                throw new IllegalStateException("Method 'getEMsg' already declared.");
            }

            final String tType = tTypeReference.split("::")[0];
            final AbstractJType tReferenceType = SteamdJTypeMapper.mapSteamdSerializationType(tType, getModel(),
                    getContext());
            final JMethod tGetEMsg = pJClass.method(JMod.PUBLIC, tReferenceType, "getEMsg");
            tGetEMsg.body()._return(SteamdCodeGenUtils.getValueExpression(tTypeReference, tReferenceType, getModel()));
            tGetEMsg.annotate(Override.class);
        }
    }

    /**
     * <pre>
     * &#64;Override
     * public ByteBuffer serialize() {
     * 
     *     final byte[] tProto = mProto.toByteArray();
     *     final int tHeaderLength = tProto.length;
     * 
     *     final ByteBuffer tBuffer = ByteBuffer.allocate(Integer.BYTES + Integer.BYTES + tHeaderLength);
     *     tBuffer.order(ByteOrder.LITTLE_ENDIAN);
     * 
     *     ByteBufferUtil.putUnsignedInt(tBuffer, mEMsg | ProtoMask);
     *     tBuffer.putInt(tHeaderLength);
     *     tBuffer.put(tProto);
     * 
     *     tBuffer.flip();
     *     return tBuffer;
     * }
     * </pre>
     * 
     * @param pSteamdClass
     * @param pJClass
     * @throws ClassNotFoundException
     */
    private void generateSerialize(final List<CodeGenField> tFields, final SteamdCompileClass pSteamdClass,
            final JDefinedClass pJClass) throws ClassNotFoundException {

        final JMethod tSerialize = pJClass.method(JMod.PUBLIC, ByteBuffer.class, "serialize");
        tSerialize.annotate(Override.class);

        final JBlock tSerializeBody = tSerialize.body();

        // create byte buffer
        final AbstractJClass tByteBufferJClass = getModel()._ref(ByteBuffer.class).boxify();
        final IJExpression tBufferSizeExpression = generateByteSizeExpression(tFields);
        final JInvocation tBufferInitialization = tByteBufferJClass.staticInvoke("allocate").arg(tBufferSizeExpression);
        final JVar tBuffer = tSerializeBody.decl(JMod.FINAL, tByteBufferJClass, JCodeModelUtils.PREFIX_LOCAL + "Buffer",
                tBufferInitialization);
        tSerializeBody
                .add(tBuffer.invoke("order").arg(getModel()._ref(ByteOrder.class).boxify().staticRef("LITTLE_ENDIAN")));

        tSerializeBody.add(new JNewLine());

        // write fields to buffer
        for (final CodeGenField tField : tFields) {
            tField.addSerializationStatement(tSerialize, tBuffer, getModel());
        }

        tSerializeBody.add(new JNewLine());

        // flush buffer and return
        tSerializeBody.add(tBuffer.invoke("flip"));
        tSerializeBody._return(tBuffer);

    }

    /**
     * <pre>
     * &#64;Override
     * public void deserialize(final ByteBuffer pBuffer) {
     * 
     *     mEMsg = ByteBufferUtil.getUnsignedInt(pBuffer) | EMsgMask;
     *     mHeaderLength = pBuffer.getInt();
     * 
     *     try {
     *         final byte[] tTempProto = new byte[mHeaderLength];
     *         pBuffer.get(tTempProto);
     *         mProto = CMsgProtoBufHeader.parseFrom(tTempProto);
     *     } catch (InvalidProtocolBufferException e) {
     *         throw new IllegalArgumentException(e);
     *     }
     * }
     * </pre>
     * 
     * @param pSteamdClass
     * @param pJClass
     * @throws ClassNotFoundException
     */
    private void generateDeserialize(final List<CodeGenField> tFields, final SteamdCompileClass pSteamdClass,
            final JDefinedClass pJClass) throws ClassNotFoundException {

        final JMethod tDeserialize = pJClass.method(JMod.PUBLIC, Void.TYPE, "deserialize");
        tDeserialize.annotate(Override.class);
        final JVar tParamBuffer = tDeserialize.param(JMod.FINAL, ByteBuffer.class,
                JCodeModelUtils.PREFIX_PARAMETER + "Buffer");

        for (final CodeGenField tField : tFields) {
            tField.addDeserializationStatement(tDeserialize, tParamBuffer, getModel());
        }
    }

    /**
     * @param pSteamdClass
     * @return
     */
    private IJExpression generateByteSizeExpression(final List<CodeGenField> tFields) {

        final List<CodeGenField> tFieldsWithSize = tFields.stream().filter(f -> f.getSizeExpression(getModel()) != null)
                .collect(Collectors.toList());

        IJExpression tSizeExpression = tFieldsWithSize.size() > 0 ? new JDirectExpression("") : JExpr.lit(0);
        for (int i = 0; i < tFieldsWithSize.size(); i++) {
            final CodeGenField tField = tFieldsWithSize.get(i);
            if (i > 0) {
                tSizeExpression = new JCombinedExpression(tSizeExpression, new JDirectExpression("+"));
            }
            tSizeExpression = new JCombinedExpression(tSizeExpression, tField.getSizeExpression(getModel()));
        }

        return tSizeExpression;
    }

}
