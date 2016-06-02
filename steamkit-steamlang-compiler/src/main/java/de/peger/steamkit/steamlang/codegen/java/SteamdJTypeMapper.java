package de.peger.steamkit.steamlang.codegen.java;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.codegen.java.util.JCodeModelUtils;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdSerializationConstant;
import de.peger.steamkit.steamlang.compiler.domain.SteamdTypeConstant;

public class SteamdJTypeMapper {

    private static final Map<String, Class<?>> PRIMITIVE_MAP = new ConcurrentHashMap<>();

    static {
        PRIMITIVE_MAP.put(SteamdTypeConstant.ULONG.getValue(), Long.TYPE);
        PRIMITIVE_MAP.put(SteamdTypeConstant.LONG.getValue(), Long.TYPE);
        PRIMITIVE_MAP.put(SteamdTypeConstant.INT.getValue(), Integer.TYPE);
        PRIMITIVE_MAP.put(SteamdTypeConstant.UINT.getValue(), Long.TYPE);
        PRIMITIVE_MAP.put(SteamdTypeConstant.SHORT.getValue(), Short.TYPE);
        PRIMITIVE_MAP.put(SteamdTypeConstant.USHORT.getValue(), Integer.TYPE);
        PRIMITIVE_MAP.put(SteamdTypeConstant.BYTE.getValue(), Byte.TYPE);
    }

    public static boolean isPrimitive(final String pSteamdType) {
        return PRIMITIVE_MAP.containsKey(pSteamdType);
    }

    public static AbstractJType mapSteamdPrimitive(final String pSteamdPrimitive, final JCodeModel pModel) {

        if (pSteamdPrimitive == null) {
            return pModel._ref(Long.TYPE);
        } else if (isPrimitive(pSteamdPrimitive)) {
            return pModel._ref(PRIMITIVE_MAP.get(pSteamdPrimitive));
        } else {
            throw new IllegalArgumentException("The Steamd primitive '" + pSteamdPrimitive + "' is not supported.");
        }
    }

    public static AbstractJType mapSteamdSerializationType(final String pSteamdType, final JCodeModel pModel,
            final SteamdCodeGenContext pContext) throws ClassNotFoundException {

        if (isPrimitive(pSteamdType)) {
            return mapSteamdPrimitive(pSteamdType, pModel);
        } else if (pSteamdType.matches("\\w+<\\d+>")) {
            final String tType = pSteamdType.substring(0, pSteamdType.indexOf('<'));
            final AbstractJType tJType = mapSteamdPrimitive(tType, pModel);
            return tJType.array();
        } else if (pSteamdType.matches("\\w+")) {
            return mapSteamdEnumType(pSteamdType, pModel);
        } else if (pSteamdType.matches("\\w+(\\.\\w+)*")) {
            final String[] tSplit = pSteamdType.split("\\.");
            final String tTypePackage = StringUtils.join(tSplit, '.', 0, tSplit.length - 1);
            final String tMappedPackage = pContext.getImportPackageMapping(tTypePackage);
            final String tTypeName = tSplit[tSplit.length - 1];
            return JCodeModelUtils.createJDefinedClass(tMappedPackage + "." + tTypeName, pModel, true);
        }

        throw new IllegalArgumentException("The Steamd type '" + pSteamdType + "' is not supported");

    }

    public static AbstractJType mapSteamdType(final SteamdCompileField pSteamdField, final JCodeModel pModel,
            final SteamdCodeGenContext pContext) throws ClassNotFoundException {

        final AbstractJType tSerializationType = mapSteamdSerializationType(pSteamdField.getType(), pModel, pContext);
        if (StringUtils.isEmpty(pSteamdField.getSerializationModifier()) || !tSerializationType.isPrimitive()) {
            return tSerializationType;
        }

        final SteamdSerializationConstant tSerializationMod = SteamdSerializationConstant
                .forValue(pSteamdField.getSerializationModifier());
        switch (tSerializationMod) {
        case BOOL:
            return pModel._ref(Boolean.TYPE);
        default:
            return tSerializationType;
        }

    }

    /**
     * If <code>pSteamdType</code> is an existing Java enum class, the
     * corresponding {@link JDefinedClass} is returned. If
     * <code>pSteamdType</code> is regular Java class or interface, the type of
     * the class' first field is returned - under the assumption that the class
     * is a constants class.
     * 
     * @param pSteamdType
     *            Steamd type string. Must refer to an existing class in the
     *            model.
     * @param pModel
     *            The model
     * 
     * @return Type for fields
     * 
     * @throws ClassNotFoundException
     */
    private static AbstractJType mapSteamdEnumType(final String pSteamdType, final JCodeModel pModel)
            throws ClassNotFoundException {

        final JDefinedClass tJClass = JCodeModelUtils.findExistingJClass(pSteamdType, pModel);
        if (tJClass.getClassType() == EClassType.ENUM) {
            return tJClass;
        } else {
            final JFieldVar tClassField = tJClass.fields().values().stream().findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Class '" + tJClass.fullName() + "' does not contain any fields."));
            return tClassField.type();
        }
    }
}
