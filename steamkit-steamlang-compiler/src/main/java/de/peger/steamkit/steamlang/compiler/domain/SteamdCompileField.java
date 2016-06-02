package de.peger.steamkit.steamlang.compiler.domain;

public class SteamdCompileField {

    private String mName;

    private String mType;

    private String mDefaultValue;
    private boolean mReferenceValue;

    private String mComment;

    private boolean mDeprecated;

    private String mSerializationModifier;

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * @param pName
     *            the name to set
     */
    public void setName(String pName) {
        mName = pName;
    }

    /**
     * @return the type
     */
    public String getType() {
        return mType;
    }

    /**
     * @param pType
     *            the type to set
     */
    public void setType(String pType) {
        mType = pType;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return mDefaultValue;
    }

    /**
     * @param pDefaultValue
     *            the defaultValue to set
     */
    public void setDefaultValue(String pDefaultValue) {
        mDefaultValue = pDefaultValue;
    }

    /**
     * @return the referenceValue
     */
    public boolean isReferenceValue() {
        return mReferenceValue;
    }

    /**
     * @param pReferenceValue
     *            the referenceValue to set
     */
    public void setReferenceValue(boolean pReferenceValue) {
        mReferenceValue = pReferenceValue;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return mComment;
    }

    /**
     * @param pComment
     *            the comment to set
     */
    public void setComment(String pComment) {
        mComment = pComment;
    }

    /**
     * @return the deprecated
     */
    public boolean isDeprecated() {
        return mDeprecated;
    }

    /**
     * @param pDeprecated
     *            the deprecated to set
     */
    public void setDeprecated(boolean pDeprecated) {
        mDeprecated = pDeprecated;
    }

    /**
     * @return the serializationModifier
     */
    public String getSerializationModifier() {
        return mSerializationModifier;
    }

    /**
     * @param pSerializationModifier
     *            the serializationModifier to set
     */
    public void setSerializationModifier(String pSerializationModifier) {
        mSerializationModifier = pSerializationModifier;
    }

}
