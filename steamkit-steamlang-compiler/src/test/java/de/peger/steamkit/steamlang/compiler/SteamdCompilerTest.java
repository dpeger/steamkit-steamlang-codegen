package de.peger.steamkit.steamlang.compiler;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileResult;

public class SteamdCompilerTest {

    private SteamdCompileResult compileSteamd(final String pSteamdName) throws Exception {
        final SteamdCompiler compiler = new SteamdCompiler();
        try (final InputStream steamdIn = getClass().getClassLoader().getResourceAsStream(pSteamdName);) {
            return compiler.compile(steamdIn, pSteamdName);
        }
    }

    @Test
    public void testCompileClassWithArrayField() throws Exception {

        final SteamdCompiler compiler = new SteamdCompiler();
        final SteamdCompileResult tResult = compiler.compile("class TestClass { byte<1234> data; }", null);

        final SteamdCompileClass tClass = tResult.getClasses().get(0);
        final SteamdCompileField tField = tClass.getFields().get(0);
        Assert.assertEquals("Field type did not match", tField.getType(), "byte<1234>");
    }

    @Test
    public void testSingleEnumHappyCase() throws Exception {
        final SteamdCompileResult tResult = compileSteamd("single_enum.steamd");
        System.out.println(tResult);
    }

    @Test
    public void testMultipleEnumsHappyCase() throws Exception {
        final SteamdCompileResult tResult = compileSteamd("multiple_enums.steamd");
        System.out.println(tResult);
    }

    @Test
    public void testClassWithImportHappyCase() throws Exception {
        final SteamdCompileResult tResult = compileSteamd("class_with_import.steamd");
        System.out.println(tResult);
    }

    @Test
    public void testEnumsAndClassesHappyCase() throws Exception {
        final SteamdCompileResult tResult = compileSteamd("classes_and_enum.steamd");
        System.out.println(tResult);
    }

    @Test
    public void testEnumWithCommentHappyCase() throws Exception {
        final SteamdCompileResult tResult = compileSteamd("enum_with_comment.steamd");
        System.out.println(tResult);
    }

}
