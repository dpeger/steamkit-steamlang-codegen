package de.peger.steamkit.steamlang.codegen;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author dpeger
 */
public class SteamdCodeGenContext {

    /**
     * Generated files will be written to this directory. This path will be the
     * root package of the generated sources.
     */
    private File mOutputBaseDir;

    /**
     * Mappings from a steamd source to a target Java package. All classes/enums
     * of the source are generated in the respective target package.
     */
    private Map<String, String> mOutputPackageMappings = new HashMap<>();

    /**
     * Mappings from package names as they occur in the steamd source to Java
     * packages of the respective classes.
     */
    private Map<String, String> mImportPackageMappings = new HashMap<>();

    /**
     * Additional settings that might be used by code generator implementations
     */
    private Map<String, Object> mExtraSettings = new HashMap<>();

    /**
     * @return the outputBaseDir
     */
    @Nonnull
    public File getOutputBaseDir() {
        return mOutputBaseDir;
    }

    /**
     * @param pOutputBaseDir
     *            the outputBaseDir to set
     */
    public void setOutputBaseDir(@Nonnull final File pOutputBaseDir) {
        mOutputBaseDir = pOutputBaseDir;
    }

    /**
     * @return the outputPackageMappings
     */
    @Nonnull
    public Map<String, String> getOutputPackageMappings() {
        return mOutputPackageMappings;
    }

    /**
     * @param pOutputPackageMappings
     *            the outputPackageMappings to set
     */
    public void setOutputPackageMappings(@Nonnull final Map<String, String> pOutputPackageMappings) {
        mOutputPackageMappings = pOutputPackageMappings;
    }

    /**
     * @return the importPackageMappings
     */
    @Nonnull
    public Map<String, String> getImportPackageMappings() {
        return mImportPackageMappings;
    }

    /**
     * @param pImportPackageMappings
     *            the importPackageMappings to set
     */
    public void setImportPackageMappings(@Nonnull final Map<String, String> pImportPackageMappings) {
        mImportPackageMappings = pImportPackageMappings;
    }

    /**
     * @return the extraSettings
     */
    @Nonnull
    public Map<String, Object> getExtraSettings() {
        return mExtraSettings;
    }

    /**
     * @param pExtraSettings
     *            the extraSettings to set
     */
    public void setExtraSettings(@Nonnull final Map<String, Object> pExtraSettings) {
        mExtraSettings = pExtraSettings;
    }

    public void addOutputPackageMapping(final String pSteamdSrc, final String pTargetPackage) {
        mOutputPackageMappings.put(pSteamdSrc, pTargetPackage);
    }

    @Nonnull
    public String getOutputPackageMapping(final String pSteamdSrc) {
        return Optional.ofNullable(mOutputPackageMappings.get(pSteamdSrc)).orElseGet(() -> {
            return "";
        });
    }

    public void addImportPackageMapping(final String pSteamdPackage, final String pTargetPackage) {
        mImportPackageMappings.put(pSteamdPackage, pTargetPackage);
    }

    @Nonnull
    public String getImportPackageMapping(final String pSteamdPackage) {
        return Optional.ofNullable(mImportPackageMappings.get(pSteamdPackage)).orElseGet(() -> {
            return pSteamdPackage;
        });
    }

    public void addExtraSetting(final String pSettingKey, final Object pSettingValue) {
        mExtraSettings.put(pSettingKey, pSettingValue);
    }

    @Nullable
    public Object getExtraSetting(final String pSettingKey) {
        return mExtraSettings.get(pSettingKey);
    }

}
