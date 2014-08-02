package mekanism.api;

import mekanism.api.util.EnergyUtils.EnergyType;

public class MekanismConfig
{
	public static class general
	{
		public static boolean updateNotifications = true;
		public static boolean controlCircuitOreDict = true;
		public static boolean logPackets = false;
		public static boolean dynamicTankEasterEgg = false;
		public static boolean voiceServerEnabled = true;
		public static boolean cardboardSpawners = true;
		public static boolean machineEffects = true;
		public static boolean enableWorldRegeneration = true;
		public static boolean creativeOverrideElectricChest = true;
		public static boolean spawnBabySkeletons = true;
		public static int obsidianTNTBlastRadius = 12;
		public static int osmiumPerChunk = 12;
		public static int copperPerChunk = 16;
		public static int tinPerChunk = 14;
		public static int saltPerChunk = 2;
		public static int obsidianTNTDelay = 100;
		public static int UPDATE_DELAY = 10;
		public static int VOICE_PORT = 36123;
		public static int maxUpgradeMultiplier = 10;
		public static int userWorldGenVersion = 0;
		public static double ENERGY_PER_REDSTONE = 10000;
		public static EnergyType activeType = EnergyType.J;
		public static double TO_IC2;
		public static double TO_BC;
		public static double TO_TE;
		public static double TO_UE = .001;
		public static double FROM_UE = 1/TO_UE;
		public static double FROM_H2;
		public static double FROM_IC2;
		public static double FROM_BC;
		public static double FROM_TE;
	}

	public static class usage
	{
		public static double enrichmentChamberUsage;
		public static double osmiumCompressorUsage;
		public static double combinerUsage;
		public static double crusherUsage;
		public static double factoryUsage;
		public static double metallurgicInfuserUsage;
		public static double purificationChamberUsage;
		public static double energizedSmelterUsage;
		public static double digitalMinerUsage;
		public static double electricPumpUsage;
		public static double rotaryCondensentratorUsage;
		public static double oxidationChamberUsage;
		public static double chemicalInfuserUsage;
		public static double chemicalInjectionChamberUsage;
		public static double precisionSawmillUsage;
		public static double chemicalDissolutionChamberUsage;
		public static double chemicalWasherUsage;
		public static double chemicalCrystallizerUsage;
		public static double seismicVibratorUsage;
		public static double pressurizedReactionBaseUsage;
		public static double fluidicPlenisherUsage;
	}

	public static class generators
	{
		public static double advancedSolarGeneration;
		public static double bioGeneration;
		public static double heatGeneration;
		public static double solarGeneration;
		public static double windGeneration;
	}

	public static class tools
	{
		public static double armorSpawnRate;
	}
}
