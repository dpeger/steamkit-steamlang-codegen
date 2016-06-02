package de.peger.steamkit.steamlang.codegen.java.jcodemodel;

import com.helger.jcodemodel.IJGenerable;
import com.helger.jcodemodel.IJStatement;
import com.helger.jcodemodel.JFormatter;

/**
 * @author dpeger
 *
 */
public class JNewLine implements IJStatement, IJGenerable {

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.codemodel.JGenerable#generate(com.sun.codemodel.JFormatter)
     */
    @Override
    public void generate(JFormatter pF) {
        pF.newline();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.codemodel.JStatement#state(com.sun.codemodel.JFormatter)
     */
    @Override
    public void state(JFormatter pF) {
        generate(pF);
    }

}
