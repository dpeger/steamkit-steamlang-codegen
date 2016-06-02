package de.peger.steamkit.steamlang.codegen.java.fields;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;

import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;

/**
 * @author dpeger
 *
 */
public abstract class AbstractCodeGenField implements CodeGenField {

    private final String mFieldType;
    private final String mFieldName;
    private final String mFieldDefaultValue;

    public AbstractCodeGenField(String pFieldType, String pFieldName, String pFieldDefaultValue) {
        mFieldType = pFieldType;
        mFieldName = pFieldName;
        mFieldDefaultValue = pFieldDefaultValue;
    }

    /**
     * @return the fieldType
     */
    protected String getFieldType() {
        return mFieldType;
    }

    /**
     * @return the fieldType
     */
    protected String getFieldSerializationType() {
        return getFieldType();
    }

    /**
     * @return the fieldName
     */
    protected String getFieldName() {
        return mFieldName;
    }

    /**
     * @return the fieldDefaultValue
     */
    protected String getFieldDefaultValue() {
        return mFieldDefaultValue;
    }

    @Override
    public void addField(final JDefinedClass pJClass, final JCodeModel pModel) {

        final AbstractJType tFieldType = getJType(pModel);
        final String tFieldName = getFieldName(JCodeModelUtils.PREFIX_MEMBER);
        final int tFieldModifiers = getModifiers();
        final IJExpression tFieldInitExpression = getInitExpression(pModel);

        final JFieldVar tJField = pJClass.field(tFieldModifiers, tFieldType, tFieldName, tFieldInitExpression);

        addGetter(pJClass, tJField, pModel);
        addSetter(pJClass, tJField, pModel);
    }

    protected void addGetter(final JDefinedClass pJClass, final JFieldVar pJField, final JCodeModel pModel) {
        JCodeModelUtils.generateGetter(pJClass, pJField);
    }

    protected void addSetter(final JDefinedClass pJClass, final JFieldVar pJField, final JCodeModel pModel) {
        JCodeModelUtils.generateSetter(pJClass, pJField);
    }

    @Override
    public String getFieldName(final String pPrefix) {
        final String tFieldName = pPrefix + JCodeModelUtils.extractFieldName(getFieldName());
        return tFieldName;
    }

    protected int getModifiers() {
        return JMod.PRIVATE;
    }

    protected IJExpression getInitExpression(final JCodeModel pModel) {
        return JExpr._null();
    }

}
