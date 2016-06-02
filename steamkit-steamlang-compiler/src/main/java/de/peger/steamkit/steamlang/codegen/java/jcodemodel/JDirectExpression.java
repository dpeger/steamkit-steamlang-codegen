package de.peger.steamkit.steamlang.codegen.java.jcodemodel;

import com.helger.jcodemodel.AbstractJExpressionImpl;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JFormatter;

/**
 * @author dpeger
 *
 */
public class JDirectExpression extends AbstractJExpressionImpl implements IJStatement {

    private final String mExpression;

    public JDirectExpression(final String pExpression) {
        mExpression = pExpression;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.codemodel.JGenerable#generate(com.sun.codemodel.JFormatter)
     */
    @Override
    public void generate(JFormatter pF) {
        pF.print(mExpression);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.helger.jcodemodel.IJStatement#state(com.helger.jcodemodel.JFormatter)
     */
    @Override
    public void state(JFormatter pF) {
        pF.print(mExpression);
    }

}
