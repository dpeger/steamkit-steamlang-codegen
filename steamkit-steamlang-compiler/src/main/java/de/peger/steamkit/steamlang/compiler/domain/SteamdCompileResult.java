package de.peger.steamkit.steamlang.compiler.domain;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class SteamdCompileResult {

    private String mSourceName;

    private List<String> mImports = new ArrayList<>();

    private List<SteamdCompileClass> mClasses = new ArrayList<>();

    private List<SteamdCompileClass> mEnums = new ArrayList<>();

    /**
     * @return the sourceName
     */
    public String getSourceName() {
        return mSourceName;
    }

    /**
     * @param pSourceName
     *            the sourceName to set
     */
    public void setSourceName(String pSourceName) {
        mSourceName = pSourceName;
    }

    /**
     * @return the imports
     */
    @Nonnull
    public List<String> getImports() {
        return mImports;
    }

    /**
     * @param pImports
     *            the imports to set
     */
    public void setImports(@Nonnull List<String> pImports) {
        mImports = pImports;
    }

    /**
     * @return the classes
     */
    @Nonnull
    public List<SteamdCompileClass> getClasses() {
        return mClasses;
    }

    /**
     * @param pClasses
     *            the classes to set
     */
    public void setClasses(@Nonnull List<SteamdCompileClass> pClasses) {
        mClasses = pClasses;
    }

    /**
     * @return the enums
     */
    @Nonnull
    public List<SteamdCompileClass> getEnums() {
        return mEnums;
    }

    /**
     * @param pEnums
     *            the enums to set
     */
    public void setEnums(@Nonnull List<SteamdCompileClass> pEnums) {
        mEnums = pEnums;
    }

}
