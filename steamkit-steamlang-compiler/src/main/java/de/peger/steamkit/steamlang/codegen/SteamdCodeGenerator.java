package de.peger.steamkit.steamlang.codegen;

import java.io.IOException;

import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileResult;

public interface SteamdCodeGenerator {

    void generate(final SteamdCompileResult... pCompileResults) throws IOException;

}
