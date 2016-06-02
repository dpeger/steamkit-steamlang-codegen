package de.peger.steamkit.steamlang.codegen.java.jcodemodel;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JArrayClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFormatter;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JMods;
import com.helger.jcodemodel.JVar;

/**
 * @author dpeger
 *
 */
public class JNewLineVar extends JVar {

    private final static class DummyJType extends AbstractJType {

        @Override
        public JCodeModel owner() {
            return null;
        }

        @Override
        public void generate(JFormatter pF) {
        }

        @Override
        public AbstractJType unboxify() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public String fullName() {
            return null;
        }

        @Override
        public AbstractJClass boxify() {
            return null;
        }

        @Override
        public JArrayClass array() {
            return null;
        }
    }

    /**
     * @param pAMods
     * @param pAType
     * @param pSName
     * @param pAInitExpr
     */
    public JNewLineVar() {
        super(JMods.forVar(JMod.NONE), new DummyJType(), "JNewLineVar", JExpr._null());
    }

    @Override
    public void declare(JFormatter pF) {
        pF.newline();
    }

    @Override
    public void generate(JFormatter pF) {
        // nothing
    }

}
