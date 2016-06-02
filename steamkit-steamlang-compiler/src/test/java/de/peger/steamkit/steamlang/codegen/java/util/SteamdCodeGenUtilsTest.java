package de.peger.steamkit.steamlang.codegen.java.util;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JFormatter;

import de.peger.steamkit.steamlang.codegen.java.util.SteamdCodeGenUtils;

public class SteamdCodeGenUtilsTest {

    @Test
    public void testGetValueExpressionWithXOR() throws Exception {

        final JCodeModel tModel = new JCodeModel();

        final String tValue = "sein | nicht | sein";
        final String tExpected = tValue;
        final IJExpression tActualExpression = SteamdCodeGenUtils.getValueExpression(tValue, tModel.LONG, tModel);

        try (final StringWriter tWriter = new StringWriter();) {
            final JFormatter tFormatter = new JFormatter(tWriter);
            tActualExpression.generate(tFormatter);
            final String tActual = tWriter.toString();
            Assert.assertEquals("Generated expression did not match expectation", tExpected, tActual);
        }
    }

    @Test
    public void testGetValueExpressionWithMaxValue() throws Exception {

        final JCodeModel tModel = new JCodeModel();

        final String tValue = "ulong.MaxValue";
        final String tExpected = "java.lang.Long.MAX_VALUE";
        final IJExpression tActualExpression = SteamdCodeGenUtils.getValueExpression(tValue, tModel.LONG, tModel);

        try (final StringWriter tWriter = new StringWriter();) {
            final JFormatter tFormatter = new JFormatter(tWriter);
            tActualExpression.generate(tFormatter);
            final String tActual = tWriter.toString();
            Assert.assertEquals("Generated expression did not match expectation", tExpected, tActual);
        }
    }

}
