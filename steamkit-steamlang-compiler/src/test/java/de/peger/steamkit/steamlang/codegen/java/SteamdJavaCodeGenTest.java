package de.peger.steamkit.steamlang.codegen.java;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.compiler.SteamdCompiler;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileResult;

public class SteamdJavaCodeGenTest {

    private SteamdCompileResult compileSteamd(final String pSteamdName) throws Exception {
        final SteamdCompiler compiler = new SteamdCompiler();
        try (final InputStream steamdIn = getClass().getClassLoader().getResourceAsStream(pSteamdName);) {
            return compiler.compile(steamdIn, pSteamdName);
        }
    }

    @Test
    public void testEnum() throws Exception {

        File tOutputBaseDir = null;
        try {
            final SteamdCodeGenContext tContext = new SteamdCodeGenContext();
            tOutputBaseDir = Files.createTempDirectory("SteamdJavaCodeGenTest").toFile();
            tContext.setOutputBaseDir(tOutputBaseDir);
            tContext.addOutputPackageMapping("multiple_enums.steamd", "org.opensteamworks.steamkit.steamlang.enums");

            final SteamdJavaCodeGenerator tCut = new SteamdJavaCodeGenerator(tContext);
            tCut.generate(compileSteamd("multiple_enums.steamd"));
        } finally {
            FileUtils.deleteQuietly(tOutputBaseDir);
        }
    }

    @Test
    public void testClass() throws Exception {

        File tOutputBaseDir = null;
        try {
            // @formatter:off
            final SteamdCodeGenContext tContext = new SteamdCodeGenContext();
            tOutputBaseDir = Files.createTempDirectory("SteamdJavaCodeGenTest").toFile();
            tContext.setOutputBaseDir(tOutputBaseDir);

            tContext.addOutputPackageMapping("multiple_enums.steamd", "org.opensteamworks.steamkit.steamlang.enums");
            tContext.addOutputPackageMapping("class_with_fqn_types.steamd",
                    "org.opensteamworks.steamkit.steamlang.internal.gc");
            tContext.addOutputPackageMapping("class_with_import.steamd",
                    "org.opensteamworks.steamkit.steamlang.internal");
            tContext.addOutputPackageMapping("single_class.steamd", "org.opensteamworks.steamkit.steamlang.internal");

            tContext.addImportPackageMapping("SteamKit2.GC.Internal", "org.opensteamworks.steamkit.base.proto.gc");
            // @formatter:on

            SteamdJavaCodeGenerator tCut = new SteamdJavaCodeGenerator(tContext);
            tCut.generate(compileSteamd("multiple_enums.steamd"));
            tCut.generate(compileSteamd("class_with_fqn_types.steamd"));
            tCut.generate(compileSteamd("class_with_import.steamd"));
            tCut.generate(compileSteamd("single_class.steamd"));
        } finally {
            FileUtils.deleteQuietly(tOutputBaseDir);
        }
    }

}
