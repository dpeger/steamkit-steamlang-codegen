package de.peger.steamkit.steamlang.codegen.java;

import com.helger.jcodemodel.JPackage;

import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;

/**
 * @author dpeger
 *
 */
public interface SteamdClassGenerator {

    void generate(final SteamdCompileClass pSteamdClass, final JPackage pPackage);
}
