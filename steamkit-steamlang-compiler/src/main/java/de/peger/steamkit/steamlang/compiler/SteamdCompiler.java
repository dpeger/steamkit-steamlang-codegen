package de.peger.steamkit.steamlang.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileResult;
import de.peger.steamkit.steamlang.parser.SteamdLexer;
import de.peger.steamkit.steamlang.parser.SteamdParser;

public class SteamdCompiler {

    public SteamdCompileResult compile(final File pFile) {
        try (final FileInputStream tFileInput = new FileInputStream(pFile);) {
            final SteamdCompileResult tResult = compile(tFileInput, pFile.getName());
            return tResult;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read input file '" + pFile.getAbsolutePath() + "'", e);
        }
    }

    public SteamdCompileResult compile(final InputStream pInput, final String pSourceName) {
        try {
            // lexer splits input into tokens
            final ANTLRInputStream tInput = new ANTLRInputStream(pInput);
            final CommonTree tAST = compileInternal(tInput);
            final SteamdCompileResult tResult = createResultFromTree(tAST);
            tResult.setSourceName(pSourceName);
            return tResult;
        } catch (RecognitionException e) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read input stream", e);
        }
    }

    public SteamdCompileResult compile(final String pExpression, final String pSourceName) {
        try {
            // lexer splits input into tokens
            ANTLRStringStream tInput = new ANTLRStringStream(pExpression);
            final CommonTree tAST = compileInternal(tInput);
            final SteamdCompileResult tResult = createResultFromTree(tAST);
            tResult.setSourceName(pSourceName);
            return tResult;
        } catch (RecognitionException e) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        }
    }

    private CommonTree compileInternal(final CharStream pInput) throws RecognitionException {

        final TokenStream tokens = new CommonTokenStream(new SteamdLexer(pInput));

        // parser generates abstract syntax tree
        final SteamdParser parser = new SteamdParser(tokens);
        final SteamdParser.steamd_return ret = parser.steamd();

        // acquire parse result
        final CommonTree ast = (CommonTree) ret.getTree();
        return ast;
    }

    private SteamdCompileResult createResultFromTree(final CommonTree pAstRoot) {

        final SteamdCompileResult tResult = new SteamdCompileResult();

        tResult.setImports(processSubTree(pAstRoot, SteamdParser.IMPORTS, this::extractImport));
        tResult.setClasses(processSubTree(pAstRoot, SteamdParser.CLASSES, this::extractClass));
        tResult.setEnums(processSubTree(pAstRoot, SteamdParser.ENUMS, this::extractEnum));

        return tResult;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private <R> List<R> processSubTree(final CommonTree pAstRoot, final int pSubTreeType,
            final Function<CommonTree, R> pExtractFunction) {

        final List<R> tResult = new ArrayList<>();

        final CommonTree tSubTree = (CommonTree) pAstRoot.getFirstChildWithType(pSubTreeType);
        if ((tSubTree != null) && CollectionUtils.isNotEmpty(tSubTree.getChildren())) {
            for (final CommonTree tSubTeeRoot : (List<CommonTree>) tSubTree.getChildren()) {
                final R tSingleResult = pExtractFunction.apply(tSubTeeRoot);
                tResult.add(tSingleResult);
            }
        }

        return tResult;
    }

    private String extractImport(final Tree pImportNode) {
        return pImportNode.getText().substring(1, pImportNode.getText().length() - 1);
    }

    @Nonnull
    private SteamdCompileClass extractClass(final CommonTree pEnumSubTree) {

        final SteamdCompileClass tResult = new SteamdCompileClass();
        tResult.setName(pEnumSubTree.getText());

        {
            final Tree tTypeSubTree = pEnumSubTree.getFirstChildWithType(SteamdParser.TYPEREF);
            tResult.setTypeReference(extractNodeText(tTypeSubTree, StringUtils.EMPTY));
        }

        {
            final Tree tFieldsSubTree = pEnumSubTree.getFirstChildWithType(SteamdParser.FIELDS);
            tResult.setFields(extractFields(tFieldsSubTree));
        }

        return tResult;
    }

    @Nonnull
    private SteamdCompileClass extractEnum(final CommonTree pEnumSubTree) {

        final SteamdCompileClass tResult = new SteamdCompileClass();
        tResult.setName(pEnumSubTree.getText());

        {
            final Tree tTypeSubTree = pEnumSubTree.getFirstChildWithType(SteamdParser.TYPEREF);
            tResult.setTypeReference(extractNodeText(tTypeSubTree, StringUtils.EMPTY));
        }

        {
            final Tree tFieldsSubTree = pEnumSubTree.getFirstChildWithType(SteamdParser.FIELDS);
            tResult.setFields(extractFields(tFieldsSubTree));
        }

        {
            final Tree tFieldsSubTree = pEnumSubTree.getFirstChildWithType(SteamdParser.MODIFIERS);
            tResult.setModifiers(extractModifiers(tFieldsSubTree));
        }

        return tResult;
    }

    @Nonnull
    private List<SteamdCompileField> extractFields(final Tree pFieldsSubTree) {

        final List<SteamdCompileField> tResult = new ArrayList<>();

        for (int i = 0; i < pFieldsSubTree.getChildCount(); i++) {

            final Tree tFieldNode = pFieldsSubTree.getChild(i);

            final SteamdCompileField tField = new SteamdCompileField();
            tField.setName(tFieldNode.getText());

            for (int j = 0; j < tFieldNode.getChildCount(); j++) {

                final Tree tChildNode = tFieldNode.getChild(j);
                switch (tChildNode.getType()) {
                case SteamdParser.FIELD_MODIFIERS:
                    tField.setSerializationModifier(extractNodeText(tChildNode, StringUtils.EMPTY));
                    break;
                case SteamdParser.FIELD_TYPE:
                    tField.setType(extractNodeText(tChildNode, StringUtils.EMPTY));
                    break;
                case SteamdParser.FIELD_VALUE:
                    tField.setDefaultValue(extractNodeText(tChildNode, StringUtils.SPACE));
                    tField.setReferenceValue(containsNodeOfType(tChildNode, SteamdParser.IDENTIFIER));
                    break;
                case SteamdParser.FIELD_COMMENT:
                    tField.setComment(extractNodeText(tChildNode, StringUtils.SPACE));
                    tField.setDeprecated(StringUtils.startsWith(tField.getComment(), "obsolete"));
                    break;

                default:
                    throw new IllegalStateException("Field sub-node with unknown type " + toString(tChildNode) + ": "
                            + tChildNode.toStringTree());
                }
            }

            tResult.add(tField);

        }

        return tResult;
    }

    @Nonnull
    private List<String> extractModifiers(final Tree pTree) {

        final List<String> tModifiers = new ArrayList<>();
        if ((pTree != null) && (pTree.getChildCount() > 0)) {
            for (int i = 0; i < pTree.getChildCount(); i++) {
                tModifiers.add(pTree.getChild(i).getText());
            }
        }

        return tModifiers;
    }

    @Nullable
    private String extractNodeText(final Tree pTree, final String pSeparator) {

        if ((pTree != null) && (pTree.getChildCount() > 0)) {
            final StringBuilder tResultBuilder = new StringBuilder();
            for (int i = 0; i < pTree.getChildCount(); i++) {
                tResultBuilder.append(pTree.getChild(i).getText()).append(pSeparator);
            }
            return tResultBuilder.substring(0, tResultBuilder.length() - pSeparator.length());
        }

        return null;
    }

    private boolean containsNodeOfType(final Tree pTree, final int pNodeType) {
        if ((pTree != null) && (pTree.getChildCount() > 0)) {
            for (int i = 0; i < pTree.getChildCount(); i++) {
                if (pTree.getChild(i).getType() == pNodeType) {
                    return true;
                }
            }
        }

        return false;
    }

    private String toString(final Tree tChildNode) {
        return "'" + tChildNode.getType() + "' (" + SteamdParser.tokenNames[tChildNode.getType()] + ")";
    }
}