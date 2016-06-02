package de.peger.steamkit.steamlang.parser;

import java.io.InputStream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.junit.Test;

public class SteamdParserTest {

    private CommonTree compileInternal(final InputStream pInput) throws Exception {

        final ANTLRInputStream input = new ANTLRInputStream(pInput);
        final TokenStream tokens = new CommonTokenStream(new SteamdLexer(input));

        // parser generates abstract syntax tree
        final SteamdParser parser = new SteamdParser(tokens);
        final SteamdParser.steamd_return ret = parser.steamd();

        // acquire parse result
        final CommonTree ast = (CommonTree) ret.tree;
        // System.out.println(ast.toStringTree());
        printTree(ast);
        return ast;
    }

    private void printTree(CommonTree ast) {
        print(ast, 0);
    }

    private void print(CommonTree tree, int level) {
        // indent level
        for (int i = 0; i < level; i++)
            System.out.print("--");

        // print node description: type code followed by token text
        System.out.println(" " + getTokenType(tree) + " " + tree.getText());

        // print all children
        if (tree.getChildren() != null)
            for (Object ie : tree.getChildren()) {
                print((CommonTree) ie, level + 1);
            }
    }

    private String getTokenType(CommonTree tree) {
        if (tree.getType() == SteamdLexer.EOF) {
            return "EOF";
        }
        return SteamdParser.tokenNames[tree.getType()];
    }

    @Test
    public void testSingleEnumHappyCase() throws Exception {
        try (final InputStream steamdIn = getClass().getClassLoader().getResourceAsStream("single_enum.steamd");) {
            compileInternal(steamdIn);
        }
    }

    @Test
    public void testMultipleEnumsHappyCase() throws Exception {
        try (final InputStream steamdIn = getClass().getClassLoader().getResourceAsStream("multiple_enums.steamd");) {
            compileInternal(steamdIn);
        }
    }

    @Test
    public void testSingleClassHappyCase() throws Exception {
        try (final InputStream steamdIn = getClass().getClassLoader().getResourceAsStream("single_class.steamd");) {
            compileInternal(steamdIn);
        }
    }

    @Test
    public void testClasseWithImportHappyCase() throws Exception {
        try (final InputStream steamdIn = getClass().getClassLoader()
                .getResourceAsStream("class_with_import.steamd");) {
            compileInternal(steamdIn);
        }
    }

    @Test
    public void testEnumWithCommentHappyCase() throws Exception {
        try (final InputStream steamdIn = getClass().getClassLoader()
                .getResourceAsStream("enum_with_comment.steamd");) {
            compileInternal(steamdIn);
        }
    }

    @Test
    public void testEnumsAndClassesHappyCase() throws Exception {
        try (final InputStream steamdIn = getClass().getClassLoader().getResourceAsStream("classes_and_enum.steamd");) {
            compileInternal(steamdIn);
        }
    }

    @Test
    public void testClassesWithFqnTypesHappyCase() throws Exception {
        try (final InputStream steamdIn = getClass().getClassLoader()
                .getResourceAsStream("class_with_fqn_types.steamd");) {
            compileInternal(steamdIn);
        }
    }

}
