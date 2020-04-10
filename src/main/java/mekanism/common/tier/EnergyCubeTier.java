package mekanism.common.tier;

import java.util.Locale;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.math.FloatingLong;
import mekanism.api.tier.BaseTier;
import mekanism.api.tier.ITier;
import mekanism.common.config.value.CachedFloatingLongValue;
import net.minecraft.util.IStringSerializable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public enum EnergyCubeTier implements ITier, IStringSerializable {
    BASIC(BaseTier.BASIC, FloatingLong.createConst(2_000_000), FloatingLong.createConst(800)),
    ADVANCED(BaseTier.ADVANCED, FloatingLong.createConst(8_000_000), FloatingLong.createConst(3_200)),
    ELITE(BaseTier.ELITE, FloatingLong.createConst(32_000_000), FloatingLong.createConst(12_800)),
    ULTIMATE(BaseTier.ULTIMATE, FloatingLong.createConst(128_000_000), FloatingLong.createConst(51_200)),
    CREATIVE(BaseTier.CREATIVE, FloatingLong.MAX_VALUE, FloatingLong.MAX_VALUE);

    private final FloatingLong baseMaxEnergy;
    private final FloatingLong baseOutput;
    private final BaseTier baseTier;
    private CachedFloatingLongValue storageReference;
    private CachedFloatingLongValue outputReference;

    EnergyCubeTier(BaseTier tier, FloatingLong max, FloatingLong out) {
        baseMaxEnergy = max;
        baseOutput = out;
        baseTier = tier;
    }

    @Override
    public BaseTier getBaseTier() {
        return baseTier;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public FloatingLong getMaxEnergy() {
        return storageReference == null ? getBaseMaxEnergy() : storageReference.get();
    }

    public FloatingLong getOutput() {
        return outputReference == null ? getBaseOutput() : outputReference.get();
    }

    public FloatingLong getBaseMaxEnergy() {
        return baseMaxEnergy;
    }

    public FloatingLong getBaseOutput() {
        return baseOutput;
    }

    /**
     * ONLY CALL THIS FROM TierConfig. It is used to give the EnergyCubeTier a reference to the actual config value object
     */
    public void setConfigReference(CachedFloatingLongValue storageReference, CachedFloatingLongValue outputReference) {
        this.storageReference = storageReference;
        this.outputReference = outputReference;
    }
}