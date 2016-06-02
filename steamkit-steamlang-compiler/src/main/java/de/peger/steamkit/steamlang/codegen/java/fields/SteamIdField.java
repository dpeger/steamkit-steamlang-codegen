package de.peger.steamkit.steamlang.codegen.java.fields;

import org.apache.commons.lang3.StringUtils;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

import de.peger.steamkit.steamlang.compiler.domain.SteamdCompileField;
import de.peger.steamkit.steamlang.compiler.domain.SteamdSerializationConstant;

/**
 * @author dpeger
 *
 */
public class SteamIdField extends PrimitiveField {

    /**
     * @param pField
     */
    public SteamIdField(final SteamdCompileField pField) {
        super(pField);
    }

    public static boolean isSteamIdField(final SteamdCompileField pField) {
        boolean isSteamIdField = StringUtils.isNotEmpty(pField.getSerializationModifier());
        isSteamIdField = isSteamIdField && (SteamdSerializationConstant.STEAMID == SteamdSerializationConstant
                .forValue(pField.getSerializationModifier()));
        return isSteamIdField;
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, final JVar pByteBuffer, final JCodeModel pModel) {
        // private ulong gameId;
        // public GameID GameId { get { return new GameID( gameId ); } set {
        // gameId = value.ToUInt64(); } }
        super.addSerializationStatement(pSerialize, pByteBuffer, pModel);
    }

}
