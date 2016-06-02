package de.peger.steamkit.steamlang.codegen;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class SteamdGeneratorTest {

    @Test
    public void testGenerateWithOneDependency() throws Exception {

        File tOutputBaseDir = null;
        try {
            // @formatter:off
            final SteamdCodeGenContext tContext = new SteamdCodeGenContext();
            tOutputBaseDir = Files.createTempDirectory("SteamdGeneratorTest").toFile();
            tContext.setOutputBaseDir(tOutputBaseDir);

            tContext.addOutputPackageMapping("class_with_import.steamd",
                    "org.opensteamworks.steamkit.steamlang.internal");
            tContext.addOutputPackageMapping("multiple_enums.steamd", "org.opensteamworks.steamkit.steamlang.enums");

            tContext.addImportPackageMapping("SteamKit2.GC.Internal", "org.opensteamworks.steamkit.base.proto.gc");
            // @formatter:on

            final SteamdGenerator tCut = new SteamdGenerator(tContext);
            tCut.generate(new File("src/test/resources/class_with_import.steamd"),
                    new File("src/test/resources/multiple_enums.steamd"));
        } finally {
            FileUtils.deleteQuietly(tOutputBaseDir);
        }
    }

    @Test
    public void testGenerateWithUnresolvableDependency() throws Exception {

        try {
            final SteamdCodeGenContext tContext = new SteamdCodeGenContext();

            final SteamdGenerator tCut = new SteamdGenerator(tContext);
            tCut.generate(new File("src/test/resources/class_with_import.steamd"));
            Assert.fail("Expected exception to be thrown as import can not be resolved.");
        } catch (final IllegalStateException e) {
            // expected exception
        }
    }

}
