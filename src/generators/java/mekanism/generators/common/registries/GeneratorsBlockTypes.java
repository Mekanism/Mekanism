package mekanism.generators.common.registries;

import mekanism.api.Pos3D;
import mekanism.api.math.FloatingLong;
import mekanism.common.block.attribute.AttributeParticleFX;
import mekanism.common.block.attribute.AttributeStateActive;
import mekanism.common.block.attribute.Attributes.AttributeComparator;
import mekanism.common.block.attribute.Attributes.AttributeInventory;
import mekanism.common.block.attribute.Attributes.AttributeRedstoneEmitter;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.content.blocktype.BlockTypeTile.BlockTileBuilder;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.text.TextComponentUtil;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.content.blocktype.BlockShapes;
import mekanism.generators.common.content.blocktype.Generator;
import mekanism.generators.common.content.blocktype.Generator.GeneratorBuilder;
import mekanism.generators.common.tile.TileEntityAdvancedSolarGenerator;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import mekanism.generators.common.tile.TileEntityWindGenerator;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import mekanism.generators.common.tile.reactor.TileEntityReactorFrame;
import mekanism.generators.common.tile.reactor.TileEntityReactorLogicAdapter;
import mekanism.generators.common.tile.reactor.TileEntityReactorPort;
import mekanism.generators.common.tile.turbine.TileEntityElectromagneticCoil;
import mekanism.generators.common.tile.turbine.TileEntityRotationalComplex;
import mekanism.generators.common.tile.turbine.TileEntitySaturatingCondenser;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import mekanism.generators.common.tile.turbine.TileEntityTurbineValve;
import mekanism.generators.common.tile.turbine.TileEntityTurbineVent;
import net.minecraft.particles.ParticleTypes;

public class GeneratorsBlockTypes {

    //TODO: Do this in a cleaner way
    private static final FloatingLong STORAGE = FloatingLong.createConst(160_000);
    private static final FloatingLong STORAGE2 = FloatingLong.createConst(200_000);
    private static final FloatingLong SOLAR_STORAGE = FloatingLong.createConst(96_000);
    private static final FloatingLong REACTOR_STORAGE = FloatingLong.createConst(1_000_000_000);

    // Heat Generator
    public static final Generator<TileEntityHeatGenerator> HEAT_GENERATOR = GeneratorBuilder
          .createGenerator(() -> GeneratorsTileEntityTypes.HEAT_GENERATOR, GeneratorsLang.DESCRIPTION_HEAT_GENERATOR)
          .withGui(() -> GeneratorsContainerTypes.HEAT_GENERATOR)
          .withEnergyConfig(() -> STORAGE)
          .withCustomShape(BlockShapes.HEAT_GENERATOR)
          .withSound(GeneratorsSounds.HEAT_GENERATOR)
          .with(new AttributeParticleFX()
                .add(ParticleTypes.SMOKE, (rand) -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, -0.52))
                .add(ParticleTypes.FLAME, (rand) -> new Pos3D(rand.nextFloat() * 0.6F - 0.3F, rand.nextFloat() * 6.0F / 16.0F, -0.52)))
          .build();
    // Bio Generator
    public static final Generator<TileEntityBioGenerator> BIO_GENERATOR = GeneratorBuilder
          .createGenerator(() -> GeneratorsTileEntityTypes.BIO_GENERATOR, GeneratorsLang.DESCRIPTION_BIO_GENERATOR)
          .withGui(() -> GeneratorsContainerTypes.BIO_GENERATOR)
          .withEnergyConfig(() -> STORAGE)
          .withCustomShape(BlockShapes.BIO_GENERATOR)
          .withSound(GeneratorsSounds.BIO_GENERATOR)
          .with(new AttributeParticleFX()
                .add(ParticleTypes.SMOKE, (rand) -> new Pos3D(0, 0.3, -0.25)))
          .build();
    // Solar Generator
    public static final Generator<TileEntitySolarGenerator> SOLAR_GENERATOR = GeneratorBuilder
          .createGenerator(() -> GeneratorsTileEntityTypes.SOLAR_GENERATOR, GeneratorsLang.DESCRIPTION_SOLAR_GENERATOR)
          .withGui(() -> GeneratorsContainerTypes.SOLAR_GENERATOR)
          .withEnergyConfig(() -> SOLAR_STORAGE)
          .withCustomShape(BlockShapes.SOLAR_GENERATOR)
          .withSound(GeneratorsSounds.SOLAR_GENERATOR)
          .build();
    // Wind Generator
    public static final Generator<TileEntityWindGenerator> WIND_GENERATOR = GeneratorBuilder
          .createGenerator(() -> GeneratorsTileEntityTypes.WIND_GENERATOR, GeneratorsLang.DESCRIPTION_WIND_GENERATOR)
          .withGui(() -> GeneratorsContainerTypes.WIND_GENERATOR)
          .withEnergyConfig(() -> STORAGE2)
          .withCustomShape(BlockShapes.WIND_GENERATOR)
          .withSound(GeneratorsSounds.WIND_GENERATOR)
          .build();
    // Gas Burning Generator
    public static final Generator<TileEntityGasGenerator> GAS_BURNING_GENERATOR = GeneratorBuilder
          .createGenerator(() -> GeneratorsTileEntityTypes.GAS_BURNING_GENERATOR, GeneratorsLang.DESCRIPTION_GAS_BURNING_GENERATOR)
          .withGui(() -> GeneratorsContainerTypes.GAS_BURNING_GENERATOR)
          .withEnergyConfig(() -> MekanismConfig.general.FROM_H2.get().multiply(1_000))
          .withCustomShape(BlockShapes.GAS_BURNING_GENERATOR)
          .withSound(GeneratorsSounds.GAS_BURNING_GENERATOR)
          .build();
    // Advanced Solar Generator
    public static final Generator<TileEntityAdvancedSolarGenerator> ADVANCED_SOLAR_GENERATOR = GeneratorBuilder
          .createGenerator(() -> GeneratorsTileEntityTypes.ADVANCED_SOLAR_GENERATOR, GeneratorsLang.DESCRIPTION_ADVANCED_SOLAR_GENERATOR)
          .withGui(() -> GeneratorsContainerTypes.ADVANCED_SOLAR_GENERATOR)
          .withEnergyConfig(() -> STORAGE2)
          .withCustomShape(BlockShapes.ADVANCED_SOLAR_GENERATOR)
          .withSound(GeneratorsSounds.SOLAR_GENERATOR)
          .build();

    // Turbine Casing
    public static final BlockTypeTile<TileEntityTurbineCasing> TURBINE_CASING = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.TURBINE_CASING, GeneratorsLang.DESCRIPTION_TURBINE_CASING)
          .withGui(() -> GeneratorsContainerTypes.INDUSTRIAL_TURBINE)
          .build();
    // Turbine Valve
    public static final BlockTypeTile<TileEntityTurbineValve> TURBINE_VALVE = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.TURBINE_VALVE, GeneratorsLang.DESCRIPTION_TURBINE_VALVE)
          .withGui(() -> GeneratorsContainerTypes.INDUSTRIAL_TURBINE)
          .with(new AttributeComparator())
          .build();
    // Turbine Vent
    public static final BlockTypeTile<TileEntityTurbineVent> TURBINE_VENT = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.TURBINE_VENT, GeneratorsLang.DESCRIPTION_TURBINE_VENT)
          .withGui(() -> GeneratorsContainerTypes.INDUSTRIAL_TURBINE)
          .build();
    // Electromagnetic Coil
    public static final BlockTypeTile<TileEntityElectromagneticCoil> ELECTROMAGNETIC_COIL = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.ELECTROMAGNETIC_COIL, GeneratorsLang.DESCRIPTION_ELECTROMAGNETIC_COIL)
          .build();
    // Rotational Complex
    public static final BlockTypeTile<TileEntityRotationalComplex> ROTATIONAL_COMPLEX = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.ROTATIONAL_COMPLEX, GeneratorsLang.DESCRIPTION_ROTATIONAL_COMPLEX)
          .build();
    // Saturating Condenser
    public static final BlockTypeTile<TileEntitySaturatingCondenser> SATURATING_CONDENSER = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.SATURATING_CONDENSER, GeneratorsLang.DESCRIPTION_SATURATING_CONDENSER)
          .build();
    // Turbine Rotor
    public static final BlockTypeTile<TileEntityTurbineRotor> TURBINE_ROTOR = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.TURBINE_ROTOR, GeneratorsLang.DESCRIPTION_TURBINE_ROTOR)
          .build();

    // Reactor Controller
    public static final BlockTypeTile<TileEntityReactorController> REACTOR_CONTROLLER = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.REACTOR_CONTROLLER, GeneratorsLang.DESCRIPTION_REACTOR_CONTROLLER)
          .withGui(() -> GeneratorsContainerTypes.REACTOR_CONTROLLER)
          .withEnergyConfig(() -> REACTOR_STORAGE)
          .withSound(GeneratorsSounds.FUSION_REACTOR)
          .with(new AttributeStateActive(), new AttributeInventory())
          .build();
    // Reactor Port
    public static final BlockTypeTile<TileEntityReactorPort> REACTOR_PORT = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.REACTOR_PORT, GeneratorsLang.DESCRIPTION_REACTOR_PORT)
          .with(new AttributeStateActive())
          .build();
    // Reactor Frame
    public static final BlockTypeTile<TileEntityReactorFrame> REACTOR_FRAME = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.REACTOR_FRAME, GeneratorsLang.DESCRIPTION_REACTOR_FRAME)
          .withEnergyConfig(null, null)
          .build();
    // Reactor Logic Adapter
    public static final BlockTypeTile<TileEntityReactorLogicAdapter> REACTOR_LOGIC_ADAPTER = BlockTileBuilder
          .createBlock(() -> GeneratorsTileEntityTypes.REACTOR_LOGIC_ADAPTER, GeneratorsLang.DESCRIPTION_REACTOR_LOGIC_ADAPTER)
          .withGui(() -> GeneratorsContainerTypes.REACTOR_LOGIC_ADAPTER)
          .with(new AttributeRedstoneEmitter<TileEntityReactorLogicAdapter>((tile) -> tile.checkMode() ? 15 : 0))
          .withCustomContainer((tile) -> new ContainerProvider(TextComponentUtil.translate(tile.getBlockType().getTranslationKey()), (i, inv, player) -> new EmptyTileContainer<>(GeneratorsContainerTypes.REACTOR_LOGIC_ADAPTER, i, inv, tile)))
          .build();
}
