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
public class GameIdField extends PrimitiveField {

    /**
     * @param pField
     */
    public GameIdField(final SteamdCompileField pField) {
        super(pField);
    }

    public static boolean isGameIdField(final SteamdCompileField pField) {
        boolean isGameIdField = StringUtils.isNotEmpty(pField.getSerializationModifier());
        isGameIdField = isGameIdField && (SteamdSerializationConstant.GAMEID == SteamdSerializationConstant
                .forValue(pField.getSerializationModifier()));
        return isGameIdField;
    }

    @Override
    public void addSerializationStatement(final JMethod pSerialize, JVar pByteBuffer, JCodeModel pModel) {
        // private ulong gameId;
        // public GameID GameId { get { return new GameID( gameId ); } set {
        // gameId = value.ToUInt64(); } }
        super.addSerializationStatement(pSerialize, pByteBuffer, pModel);
    }

}
