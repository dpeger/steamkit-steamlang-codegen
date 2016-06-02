package de.peger.steamkit.steamlang.codegen.java;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.jcodemodel.JPackage;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.SteamdCodeGenerator;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileResult;

/**
 * @author dpeger
 */
public class SteamdJavaCodeGenerator extends AbstractSteamdJavaCodeGenerator implements SteamdCodeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SteamdJavaCodeGenerator.class);

    /**
     * @param pContext
     *            The code generation context
     */
    public SteamdJavaCodeGenerator(final SteamdCodeGenContext pContext) {
        super("UNKNOWN", pContext);
    }

    @Override
    public final void generate(final SteamdCompileResult... pCompileResults) throws IOException {

        for (final SteamdCompileResult tCompileResult : pCompileResults) {

            LOGGER.info("Generating Java model for '{}'", tCompileResult.getSourceName());

            final JPackage tPackage = getModel()
                    ._package(getContext().getOutputPackageMapping(tCompileResult.getSourceName()));

            final SteamdClassGenerator tEnumGenerator = new SteamdJavaConstantsClassGenerator(
                    tCompileResult.getSourceName(), getContext());
            for (final SteamdCompileClass tSteamdEnum : tCompileResult.getEnums()) {
                tEnumGenerator.generate(tSteamdEnum, tPackage);
            }

            final SteamdClassGenerator tClassGenerator = new SteamdJavaClassGenerator(tCompileResult.getSourceName(),
                    getContext());
            for (final SteamdCompileClass tSteamdClass : tCompileResult.getClasses()) {
                tClassGenerator.generate(tSteamdClass, tPackage);
            }
        }

        final File tOutputDir = getContext().getOutputBaseDir();
        LOGGER.info("Writing generated files to '{}'", tOutputDir.getCanonicalPath());
        getModel().build(tOutputDir, (PrintStream) null);
    }

}
