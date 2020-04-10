package mekanism.generators.common.content.blocktype;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import mekanism.common.base.ILangEntry;
import mekanism.common.block.attribute.AttributeStateActive;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.attribute.Attributes.AttributeComparator;
import mekanism.common.block.attribute.Attributes.AttributeInventory;
import mekanism.common.block.attribute.Attributes.AttributeRedstone;
import mekanism.common.block.attribute.Attributes.AttributeSecurity;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.generators.common.GeneratorsLang;

public class Generator<TILE extends TileEntityMekanism> extends BlockTypeTile<TILE> {

    protected DoubleSupplier energyStorage;

    protected GeneratorsLang description;

    public Generator(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar, ILangEntry description) {
        super(tileEntityRegistrar, description);

        add(new AttributeStateActive(), new AttributeStateFacing(), new AttributeSecurity(), new AttributeInventory(), new AttributeRedstone(), new AttributeComparator());
    }

    public static class GeneratorBuilder<GENERATOR extends Generator<TILE>, TILE extends TileEntityMekanism, T extends GeneratorBuilder<GENERATOR, TILE, T>> extends BlockTileBuilder<GENERATOR, TILE, T> {

        protected GeneratorBuilder(GENERATOR holder) {
            super(holder);
        }

        public static <TILE extends TileEntityMekanism> GeneratorBuilder<Generator<TILE>, TILE, ?> createGenerator(Supplier<TileEntityTypeRegistryObject<TILE>> tileEntityRegistrar,
              ILangEntry description) {
            return new GeneratorBuilder<>(new Generator<>(tileEntityRegistrar, description));
        }
    }
}
