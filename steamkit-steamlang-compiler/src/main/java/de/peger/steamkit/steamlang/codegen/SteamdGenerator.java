package de.peger.steamkit.steamlang.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.peger.steamkit.steamlang.codegen.java.SteamdJavaCodeGenerator;
import de.peger.steamkit.steamlang.compiler.SteamdCompiler;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileResult;

/**
 * @author dpeger
 */
public class SteamdGenerator {

    private final SteamdCodeGenContext mContext;

    public SteamdGenerator(final SteamdCodeGenContext pContext) {
        mContext = pContext;
    }

    public void generate(final File... pSteamdFiles) throws IOException {

        List<SteamdCompileResult> tCompileResults = compileFiles(Arrays.asList(pSteamdFiles));
        tCompileResults = sortByImportDependencies(tCompileResults);

        final SteamdCodeGenerator tCodeGenerator = new SteamdJavaCodeGenerator(mContext);
        tCodeGenerator.generate(tCompileResults.toArray(new SteamdCompileResult[tCompileResults.size()]));
    }

    private List<SteamdCompileResult> compileFiles(final List<File> pSteamdFiles) {

        final List<SteamdCompileResult> tCompileResults = new ArrayList<>();
        final SteamdCompiler tCompiler = new SteamdCompiler();
        for (final File tSteamdFile : pSteamdFiles) {
            SteamdCompileResult tCompileResult = tCompiler.compile(tSteamdFile);
            tCompileResults.add(tCompileResult);
        }
        return tCompileResults;
    }

    private List<SteamdCompileResult> sortByImportDependencies(final List<SteamdCompileResult> pCompileResults) {

        final List<SteamdCompileResult> tSortedResults = new ArrayList<>();
        final List<SteamdCompileResult> tLocalCompileResults = new ArrayList<>(pCompileResults);
        final List<String> tAvailableImports = new ArrayList<>();

        boolean resolvedNewDependency = false;
        do {
            for (final Iterator<SteamdCompileResult> tIt = tLocalCompileResults.iterator(); tIt.hasNext();) {
                final SteamdCompileResult tCompileResult = tIt.next();
                if (tAvailableImports.containsAll(tCompileResult.getImports())) {
                    resolvedNewDependency = true;
                    tIt.remove();
                    tAvailableImports.add(tCompileResult.getSourceName());
                    tSortedResults.add(tCompileResult);
                }
            }
        } while (!tLocalCompileResults.isEmpty() && resolvedNewDependency);

        if (!tLocalCompileResults.isEmpty()) {
            final Set<String> tUnresolvedImports = new HashSet<>();
            tLocalCompileResults.stream().map(p -> p.getImports()).forEach(p -> {
                tUnresolvedImports.addAll(p);
            });
            throw new IllegalStateException("Some imports could not be resolved. Maybe some source files are missing. ["
                    + StringUtils.join(tUnresolvedImports, ',') + "]");
        }

        return tSortedResults;
    }

}
