package de.peger.steamkit.steamlang.codegen.java.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;

public class JCodeModelUtilsTest {

    private void internalTestExtractFieldName(final String tExpectedName, final String tPrefix) {
        final String tActualName = JCodeModelUtils.extractFieldName(tPrefix + tExpectedName);
        Assert.assertEquals(tExpectedName, tActualName);
    }

    @Test
    public void testExtractFieldNameWithParameterPrefix() {

        final String tName = "TestExtractFieldNameWithParameterPrefix";
        final String tPrefix = JCodeModelUtils.PREFIX_PARAMETER;

        internalTestExtractFieldName(tName, tPrefix);
    }

    @Test
    public void testExtractFieldNameWithMemberPrefix() {

        final String tName = "TestExtractFieldNameWithMemberPrefix";
        final String tPrefix = JCodeModelUtils.PREFIX_MEMBER;

        internalTestExtractFieldName(tName, tPrefix);
    }

    @Test
    public void testExtractFieldNameWithLocalPrefix() {

        final String tName = "TestExtractFieldNameWithLocalPrefix";
        final String tPrefix = JCodeModelUtils.PREFIX_LOCAL;

        internalTestExtractFieldName(tName, tPrefix);
    }

    @Test
    public void testExtractFieldNameWithoutPrefix() {

        final String tName = "TestExtractFieldNameWithoutPrefix";
        final String tPrefix = StringUtils.EMPTY;

        internalTestExtractFieldName(tName, tPrefix);
    }

    @Test
    public void testExtractFieldNameWithEmptyName() {

        final String tName = StringUtils.EMPTY;
        final String tPrefix = StringUtils.EMPTY;

        internalTestExtractFieldName(tName, tPrefix);
    }
}
