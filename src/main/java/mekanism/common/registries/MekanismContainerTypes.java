package mekanism.common.registries;

import mekanism.common.Mekanism;
import mekanism.common.inventory.container.entity.robit.CraftingRobitContainer;
import mekanism.common.inventory.container.entity.robit.InventoryRobitContainer;
import mekanism.common.inventory.container.entity.robit.MainRobitContainer;
import mekanism.common.inventory.container.entity.robit.RepairRobitContainer;
import mekanism.common.inventory.container.entity.robit.SmeltingRobitContainer;
import mekanism.common.inventory.container.item.DictionaryContainer;
import mekanism.common.inventory.container.item.PersonalChestItemContainer;
import mekanism.common.inventory.container.item.PortableTeleporterContainer;
import mekanism.common.inventory.container.item.SeismicReaderContainer;
import mekanism.common.inventory.container.tile.DigitalMinerConfigContainer;
import mekanism.common.inventory.container.tile.DigitalMinerContainer;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.inventory.container.tile.FactoryContainer;
import mekanism.common.inventory.container.tile.FormulaicAssemblicatorContainer;
import mekanism.common.inventory.container.tile.MatrixStatsTabContainer;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.inventory.container.tile.OredictionificatorContainer;
import mekanism.common.inventory.container.tile.PersonalChestTileContainer;
import mekanism.common.inventory.container.tile.QuantumEntangloporterContainer;
import mekanism.common.inventory.container.tile.SecurityDeskContainer;
import mekanism.common.inventory.container.tile.SideConfigurationContainer;
import mekanism.common.inventory.container.tile.TeleporterContainer;
import mekanism.common.inventory.container.tile.TransporterConfigurationContainer;
import mekanism.common.inventory.container.tile.UpgradeManagementContainer;
import mekanism.common.inventory.container.tile.filter.DMFilterSelectContainer;
import mekanism.common.inventory.container.tile.filter.DMItemStackFilterContainer;
import mekanism.common.inventory.container.tile.filter.DMMaterialFilterContainer;
import mekanism.common.inventory.container.tile.filter.DMModIDFilterContainer;
import mekanism.common.inventory.container.tile.filter.DMTagFilterContainer;
import mekanism.common.inventory.container.tile.filter.LSFilterSelectContainer;
import mekanism.common.inventory.container.tile.filter.LSItemStackFilterContainer;
import mekanism.common.inventory.container.tile.filter.LSMaterialFilterContainer;
import mekanism.common.inventory.container.tile.filter.LSModIDFilterContainer;
import mekanism.common.inventory.container.tile.filter.LSTagFilterContainer;
import mekanism.common.inventory.container.tile.filter.OredictionificatorFilterContainer;
import mekanism.common.registration.impl.ContainerTypeDeferredRegister;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.tile.TileEntityBoilerCasing;
import mekanism.common.tile.TileEntityChemicalCrystallizer;
import mekanism.common.tile.TileEntityChemicalDissolutionChamber;
import mekanism.common.tile.TileEntityChemicalInfuser;
import mekanism.common.tile.TileEntityChemicalInjectionChamber;
import mekanism.common.tile.TileEntityChemicalOxidizer;
import mekanism.common.tile.TileEntityChemicalWasher;
import mekanism.common.tile.TileEntityCombiner;
import mekanism.common.tile.TileEntityCrusher;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.tile.TileEntityDynamicTank;
import mekanism.common.tile.TileEntityElectricPump;
import mekanism.common.tile.TileEntityElectrolyticSeparator;
import mekanism.common.tile.TileEntityEnergizedSmelter;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.TileEntityEnrichmentChamber;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.TileEntityFluidicPlenisher;
import mekanism.common.tile.TileEntityFormulaicAssemblicator;
import mekanism.common.tile.TileEntityFuelwoodHeater;
import mekanism.common.tile.TileEntityGasTank;
import mekanism.common.tile.TileEntityInductionCasing;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.tile.TileEntityMetallurgicInfuser;
import mekanism.common.tile.TileEntityOredictionificator;
import mekanism.common.tile.TileEntityOsmiumCompressor;
import mekanism.common.tile.TileEntityPersonalChest;
import mekanism.common.tile.TileEntityPrecisionSawmill;
import mekanism.common.tile.TileEntityPressurizedReactionChamber;
import mekanism.common.tile.TileEntityPurificationChamber;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import mekanism.common.tile.TileEntityResistiveHeater;
import mekanism.common.tile.TileEntityRotaryCondensentrator;
import mekanism.common.tile.TileEntitySecurityDesk;
import mekanism.common.tile.TileEntitySeismicVibrator;
import mekanism.common.tile.TileEntitySolarNeutronActivator;
import mekanism.common.tile.TileEntityTeleporter;
import mekanism.common.tile.TileEntityThermalEvaporationController;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.laser.TileEntityLaserAmplifier;
import mekanism.common.tile.laser.TileEntityLaserTractorBeam;

//TODO: Go through each container and double check no copy paste error was made with what ContainerType the container is using
public class MekanismContainerTypes {

    public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister(Mekanism.MODID);

    //Items
    public static final ContainerTypeRegistryObject<DictionaryContainer> DICTIONARY = CONTAINER_TYPES.register(MekanismItems.DICTIONARY, DictionaryContainer::new);
    public static final ContainerTypeRegistryObject<PortableTeleporterContainer> PORTABLE_TELEPORTER = CONTAINER_TYPES.register(MekanismItems.PORTABLE_TELEPORTER, PortableTeleporterContainer::new);
    public static final ContainerTypeRegistryObject<SeismicReaderContainer> SEISMIC_READER = CONTAINER_TYPES.register(MekanismItems.SEISMIC_READER, SeismicReaderContainer::new);

    //Entity
    public static final ContainerTypeRegistryObject<MainRobitContainer> MAIN_ROBIT = CONTAINER_TYPES.register("main_robit", MainRobitContainer::new);
    public static final ContainerTypeRegistryObject<InventoryRobitContainer> INVENTORY_ROBIT = CONTAINER_TYPES.register("inventory_robit", InventoryRobitContainer::new);
    //TODO: Should this be like Crafting/Repair except with FurnaceContainer??
    public static final ContainerTypeRegistryObject<SmeltingRobitContainer> SMELTING_ROBIT = CONTAINER_TYPES.register("smelting_robit", SmeltingRobitContainer::new);
    public static final ContainerTypeRegistryObject<CraftingRobitContainer> CRAFTING_ROBIT = CONTAINER_TYPES.register("crafting_robit", CraftingRobitContainer::new);
    public static final ContainerTypeRegistryObject<RepairRobitContainer> REPAIR_ROBIT = CONTAINER_TYPES.register("repair_robit", RepairRobitContainer::new);

    //Blocks
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityChemicalCrystallizer>> CHEMICAL_CRYSTALLIZER = CONTAINER_TYPES.register(MekanismBlocks.CHEMICAL_CRYSTALLIZER, TileEntityChemicalCrystallizer.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityChemicalDissolutionChamber>> CHEMICAL_DISSOLUTION_CHAMBER = CONTAINER_TYPES.register(MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER, TileEntityChemicalDissolutionChamber.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityChemicalInfuser>> CHEMICAL_INFUSER = CONTAINER_TYPES.register(MekanismBlocks.CHEMICAL_INFUSER, TileEntityChemicalInfuser.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityChemicalInjectionChamber>> CHEMICAL_INJECTION_CHAMBER = CONTAINER_TYPES.register(MekanismBlocks.CHEMICAL_INJECTION_CHAMBER, TileEntityChemicalInjectionChamber.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityChemicalOxidizer>> CHEMICAL_OXIDIZER = CONTAINER_TYPES.register(MekanismBlocks.CHEMICAL_OXIDIZER, TileEntityChemicalOxidizer.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityChemicalWasher>> CHEMICAL_WASHER = CONTAINER_TYPES.register(MekanismBlocks.CHEMICAL_WASHER, TileEntityChemicalWasher.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCombiner>> COMBINER = CONTAINER_TYPES.register(MekanismBlocks.COMBINER, TileEntityCombiner.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityCrusher>> CRUSHER = CONTAINER_TYPES.register(MekanismBlocks.CRUSHER, TileEntityCrusher.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityDigitalMiner>> DIGITAL_MINER = CONTAINER_TYPES.register(MekanismBlocks.DIGITAL_MINER, DigitalMinerContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityDynamicTank>> DYNAMIC_TANK = CONTAINER_TYPES.register(MekanismBlocks.DYNAMIC_TANK, TileEntityDynamicTank.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityElectricPump>> ELECTRIC_PUMP = CONTAINER_TYPES.register(MekanismBlocks.ELECTRIC_PUMP, TileEntityElectricPump.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityElectrolyticSeparator>> ELECTROLYTIC_SEPARATOR = CONTAINER_TYPES.register(MekanismBlocks.ELECTROLYTIC_SEPARATOR, TileEntityElectrolyticSeparator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityEnergizedSmelter>> ENERGIZED_SMELTER = CONTAINER_TYPES.register(MekanismBlocks.ENERGIZED_SMELTER, TileEntityEnergizedSmelter.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityEnrichmentChamber>> ENRICHMENT_CHAMBER = CONTAINER_TYPES.register(MekanismBlocks.ENRICHMENT_CHAMBER, TileEntityEnrichmentChamber.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityFluidicPlenisher>> FLUIDIC_PLENISHER = CONTAINER_TYPES.register(MekanismBlocks.FLUIDIC_PLENISHER, TileEntityFluidicPlenisher.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityFormulaicAssemblicator>> FORMULAIC_ASSEMBLICATOR = CONTAINER_TYPES.register(MekanismBlocks.FORMULAIC_ASSEMBLICATOR, FormulaicAssemblicatorContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityFuelwoodHeater>> FUELWOOD_HEATER = CONTAINER_TYPES.register(MekanismBlocks.FUELWOOD_HEATER, TileEntityFuelwoodHeater.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLaserAmplifier>> LASER_AMPLIFIER = CONTAINER_TYPES.register(MekanismBlocks.LASER_AMPLIFIER, TileEntityLaserAmplifier.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityLaserTractorBeam>> LASER_TRACTOR_BEAM = CONTAINER_TYPES.register(MekanismBlocks.LASER_TRACTOR_BEAM, TileEntityLaserTractorBeam.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityMetallurgicInfuser>> METALLURGIC_INFUSER = CONTAINER_TYPES.register(MekanismBlocks.METALLURGIC_INFUSER, TileEntityMetallurgicInfuser.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityOredictionificator>> OREDICTIONIFICATOR = CONTAINER_TYPES.register(MekanismBlocks.OREDICTIONIFICATOR, OredictionificatorContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityOsmiumCompressor>> OSMIUM_COMPRESSOR = CONTAINER_TYPES.register(MekanismBlocks.OSMIUM_COMPRESSOR, TileEntityOsmiumCompressor.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityPrecisionSawmill>> PRECISION_SAWMILL = CONTAINER_TYPES.register(MekanismBlocks.PRECISION_SAWMILL, TileEntityPrecisionSawmill.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityPressurizedReactionChamber>> PRESSURIZED_REACTION_CHAMBER = CONTAINER_TYPES.register(MekanismBlocks.PRESSURIZED_REACTION_CHAMBER, TileEntityPressurizedReactionChamber.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityPurificationChamber>> PURIFICATION_CHAMBER = CONTAINER_TYPES.register(MekanismBlocks.PURIFICATION_CHAMBER, TileEntityPurificationChamber.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityQuantumEntangloporter>> QUANTUM_ENTANGLOPORTER = CONTAINER_TYPES.register(MekanismBlocks.QUANTUM_ENTANGLOPORTER, QuantumEntangloporterContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityResistiveHeater>> RESISTIVE_HEATER = CONTAINER_TYPES.register(MekanismBlocks.RESISTIVE_HEATER, TileEntityResistiveHeater.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityRotaryCondensentrator>> ROTARY_CONDENSENTRATOR = CONTAINER_TYPES.register(MekanismBlocks.ROTARY_CONDENSENTRATOR, TileEntityRotaryCondensentrator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntitySecurityDesk>> SECURITY_DESK = CONTAINER_TYPES.register(MekanismBlocks.SECURITY_DESK, SecurityDeskContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntitySeismicVibrator>> SEISMIC_VIBRATOR = CONTAINER_TYPES.register(MekanismBlocks.SEISMIC_VIBRATOR, TileEntitySeismicVibrator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntitySolarNeutronActivator>> SOLAR_NEUTRON_ACTIVATOR = CONTAINER_TYPES.register(MekanismBlocks.SOLAR_NEUTRON_ACTIVATOR, TileEntitySolarNeutronActivator.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityTeleporter>> TELEPORTER = CONTAINER_TYPES.register(MekanismBlocks.TELEPORTER, TeleporterContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityThermalEvaporationController>> THERMAL_EVAPORATION_CONTROLLER = CONTAINER_TYPES.register(MekanismBlocks.THERMAL_EVAPORATION_CONTROLLER, TileEntityThermalEvaporationController.class);

    //TODO: Decide if tiered ones should be done differently/evaluate how their container name is done
    //Named
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityFactory<?>>> FACTORY = CONTAINER_TYPES.register("factory", FactoryContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityGasTank>> GAS_TANK = CONTAINER_TYPES.register("gas_tank", TileEntityGasTank.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityFluidTank>> FLUID_TANK = CONTAINER_TYPES.register("fluid_tank", TileEntityFluidTank.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityEnergyCube>> ENERGY_CUBE = CONTAINER_TYPES.register("energy_cube", TileEntityEnergyCube.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityInductionCasing>> INDUCTION_MATRIX = CONTAINER_TYPES.register("induction_matrix", TileEntityInductionCasing.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityBoilerCasing>> THERMOELECTRIC_BOILER = CONTAINER_TYPES.register("thermoelectric_boiler", TileEntityBoilerCasing.class);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityMekanism>> UPGRADE_MANAGEMENT = CONTAINER_TYPES.register("upgrade_management", UpgradeManagementContainer::new);
    public static final ContainerTypeRegistryObject<PersonalChestItemContainer> PERSONAL_CHEST_ITEM = CONTAINER_TYPES.register("personal_chest_item", PersonalChestItemContainer::new);
    public static final ContainerTypeRegistryObject<MekanismTileContainer<TileEntityPersonalChest>> PERSONAL_CHEST_BLOCK = CONTAINER_TYPES.register("personal_chest_block", PersonalChestTileContainer::new);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityBoilerCasing>> BOILER_STATS = CONTAINER_TYPES.registerEmpty("boiler_stats", TileEntityBoilerCasing.class);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityInductionCasing>> MATRIX_STATS = CONTAINER_TYPES.register("matrix_stats", MatrixStatsTabContainer::new);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityMekanism>> SIDE_CONFIGURATION = CONTAINER_TYPES.register("side_configuration", SideConfigurationContainer::new);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityMekanism>> TRANSPORTER_CONFIGURATION = CONTAINER_TYPES.register("transporter_configuration", TransporterConfigurationContainer::new);

    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityDigitalMiner>> DIGITAL_MINER_CONFIG = CONTAINER_TYPES.register("digital_miner_config", DigitalMinerConfigContainer::new);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityLogisticalSorter>> LOGISTICAL_SORTER = CONTAINER_TYPES.registerEmpty(MekanismBlocks.LOGISTICAL_SORTER, TileEntityLogisticalSorter.class);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityDigitalMiner>> DM_FILTER_SELECT = CONTAINER_TYPES.register("digital_miner_filter_select", DMFilterSelectContainer::new);
    public static final ContainerTypeRegistryObject<EmptyTileContainer<TileEntityLogisticalSorter>> LS_FILTER_SELECT = CONTAINER_TYPES.register("logistical_sorter_filter_select", LSFilterSelectContainer::new);

    public static final ContainerTypeRegistryObject<DMTagFilterContainer> DM_TAG_FILTER = CONTAINER_TYPES.register("digital_miner_tag_filter", DMTagFilterContainer::new);
    public static final ContainerTypeRegistryObject<LSTagFilterContainer> LS_TAG_FILTER = CONTAINER_TYPES.register("logistical_sorter_tag_filter", LSTagFilterContainer::new);

    public static final ContainerTypeRegistryObject<DMModIDFilterContainer> DM_MOD_ID_FILTER = CONTAINER_TYPES.register("digital_miner_mod_id_filter", DMModIDFilterContainer::new);
    public static final ContainerTypeRegistryObject<LSModIDFilterContainer> LS_MOD_ID_FILTER = CONTAINER_TYPES.register("logistical_sorter_mod_id_filter", LSModIDFilterContainer::new);

    public static final ContainerTypeRegistryObject<DMMaterialFilterContainer> DM_MATERIAL_FILTER = CONTAINER_TYPES.register("digital_miner_material_filter", DMMaterialFilterContainer::new);
    public static final ContainerTypeRegistryObject<LSMaterialFilterContainer> LS_MATERIAL_FILTER = CONTAINER_TYPES.register("logistical_sorter_material_filter", LSMaterialFilterContainer::new);

    public static final ContainerTypeRegistryObject<DMItemStackFilterContainer> DM_ITEMSTACK_FILTER = CONTAINER_TYPES.register("digital_miner_itemstack_filter", DMItemStackFilterContainer::new);
    public static final ContainerTypeRegistryObject<LSItemStackFilterContainer> LS_ITEMSTACK_FILTER = CONTAINER_TYPES.register("logistical_sorter_itemstack_filter", LSItemStackFilterContainer::new);

    public static final ContainerTypeRegistryObject<OredictionificatorFilterContainer> OREDICTIONIFICATOR_FILTER = CONTAINER_TYPES.register("oredictionificator_filter", OredictionificatorFilterContainer::new);
}