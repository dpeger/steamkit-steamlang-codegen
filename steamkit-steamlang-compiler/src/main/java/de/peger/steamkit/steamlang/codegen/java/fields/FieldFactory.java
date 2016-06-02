package de.peger.steamkit.steamlang.codegen.java.fields;

import de.peger.steamkit.steamlang.codegen.SteamdCodeGenContext;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileClass;
import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;

/**
 * @author dpeger
 *
 */
public class FieldFactory {

    public static CodeGenField getField(final SteamdCompileField pField, final SteamdCompileClass pClass,
            final SteamdCodeGenContext pContext) {

        if (ConstantField.isConstantField(pField)) {
            return new ConstantField(pField);
        }
        if (EnumField.isEnumField(pField)) {
            return new EnumField(pField, pContext);
        }
        if (ProtoBufferLengthField.isProtoBufferLengthField(pField, pClass)) {
            return new ProtoBufferLengthField(pField);
        }
        if (BooleanField.isBooleanField(pField)) {
            return new BooleanField(pField);
        }
        if (GameIdField.isGameIdField(pField)) {
            return new GameIdField(pField);
        }
        if (SteamIdField.isSteamIdField(pField)) {
            return new SteamIdField(pField);
        }
        if (ProtoMaskField.isProtoMaskField(pField)) {
            return new ProtoMaskField(pField);
        }
        if (ProtoBufferField.isProtoBufferField(pField)) {
            return new ProtoBufferField(pField, pContext);
        }
        if (PrimitiveArrayField.isPrimitiveArrayField(pField)) {
            return new PrimitiveArrayField(pField);
        }
        if (PrimitiveField.isPrimitiveField(pField)) {
            return new PrimitiveField(pField);
        }

        throw new IllegalArgumentException("The Steamd type '" + pField.getSerializationModifier() + " "
                + pField.getType() + "' is not supported.");
    }

}
