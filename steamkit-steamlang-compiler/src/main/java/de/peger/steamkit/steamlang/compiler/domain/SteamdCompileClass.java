package de.peger.steamkit.steamlang.compiler.domain;

import java.util.ArrayList;
import java.util.List;

public class SteamdCompileClass {

    private String mName;

    private String mTypeReference = null;

    private List<String> mModifiers = new ArrayList<>();

    private List<SteamdCompileField> mFields = new ArrayList<>();

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
     * @return the typeReference
     */
    public String getTypeReference() {
        return mTypeReference;
    }

    /**
     * @param pTypeReference
     *            the typeReference to set
     */
    public void setTypeReference(String pTypeReference) {
        mTypeReference = pTypeReference;
    }

    /**
     * @return the modifiers
     */
    public List<String> getModifiers() {
        return mModifiers;
    }

    /**
     * @param pModifiers
     *            the modifiers to set
     */
    public void setModifiers(List<String> pModifiers) {
        mModifiers = pModifiers;
    }

    /**
     * @return the fields
     */
    public List<SteamdCompileField> getFields() {
        return mFields;
    }

    /**
     * @param pFields
     *            the fields to set
     */
    public void setFields(List<SteamdCompileField> pFields) {
        mFields = pFields;
    }

}
