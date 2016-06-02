package de.peger.steamkit.steamlang.codegen.java.fields;

import javax.annotation.Nullable;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

/**
 * @author dpeger
 *
 */
public interface CodeGenField {

    public AbstractJType getJType(final JCodeModel pModel);

    public String getFieldName(final String pPrefix);

    @Nullable
    public IJExpression getSizeExpression(final JCodeModel pModel);

    public void addField(final JDefinedClass pJClass, final JCodeModel pModel);

    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel);

    public void addDeserializationStatement(final JMethod pDeserialize, final JVar pByteBuffer,
            final JCodeModel pModel);

}
