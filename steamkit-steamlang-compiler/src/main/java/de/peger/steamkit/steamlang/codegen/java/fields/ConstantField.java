package de.peger.steamkit.steamlang.codegen.java.fields;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdSerializationConstant;

/**
 * @author dpeger
 */
public class ConstantField extends PrimitiveField {

    public ConstantField(final SteamdCompileField pField) {
        super(pField);
    }

    public static boolean isConstantField(final SteamdCompileField pCompileField) {
        return SteamdSerializationConstant.CONST.getValue().equals(pCompileField.getSerializationModifier());
    }

    @Override
    public String getFieldName(final String pPrefix) {
        return getFieldName();
    }

    @Override
    public int getModifiers() {
        return JMod.PUBLIC | JMod.FINAL | JMod.STATIC;
    }

    @Override
    protected void addGetter(JDefinedClass pJClass, JFieldVar pTJField, final JCodeModel pModel) {
        // No getter
    }

    @Override
    protected void addSetter(JDefinedClass pJClass, JFieldVar pTJField, final JCodeModel pModel) {
        // No setter
    }

    @Override
    public IJExpression getSizeExpression(final JCodeModel pModel) {
        return null;
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel) {
        // Nothing to do
    }

    @Override
    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel) {
        // Nothing to do
    }

}
