package de.peger.steamkit.steamlang.codegen.java.jcodemodel;

import com.helger.jcodemodel.AbstractJExpressionImpl;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JFormatter;

/**
 * @author dpeger
 *
 */
public class JCombinedExpression extends AbstractJExpressionImpl {

    private IJExpression mFirstExpression;
    private IJExpression mSecondExpression;

    public JCombinedExpression(final IJExpression pFirstExpression, final IJExpression pSecondExpression) {
        mFirstExpression = pFirstExpression;
        mSecondExpression = pSecondExpression;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.codemodel.JGenerable#generate(com.sun.codemodel.JFormatter)
     */
    @Override
    public void generate(JFormatter pF) {
        mFirstExpression.generate(pF);
        mSecondExpression.generate(pF);
    }

}
