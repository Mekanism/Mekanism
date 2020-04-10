package mekanism.common.tile;

import javax.annotation.Nonnull;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.GasSlotInfo;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;

public class TileEntityChemicalInjectionChamber extends TileEntityAdvancedElectricMachine {

    public TileEntityChemicalInjectionChamber() {
        super(MekanismBlocks.CHEMICAL_INJECTION_CHAMBER, BASE_TICKS_REQUIRED);
        configComponent.addSupported(TransmissionType.GAS);
        ConfigInfo gasConfig = configComponent.getConfig(TransmissionType.GAS);
        if (gasConfig != null) {
            gasConfig.addSlotInfo(DataType.INPUT, new GasSlotInfo(true, false, gasTank));
            //Set default config directions
            gasConfig.fill(DataType.INPUT);
            gasConfig.setCanEject(false);
        }
    }

    @Nonnull
    @Override
    public MekanismRecipeType<ItemStackGasToItemStackRecipe> getRecipeType() {
        return MekanismRecipeType.INJECTING;
    }

    @Override
    public boolean useStatisticalMechanics() {
        return true;
    }
}