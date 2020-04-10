package mekanism.common;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.client.SparkleAnimation.INodeChecker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * Common proxy for the Mekanism mod.
 *
 * @author AidanBrady
 */
//TODO: Try to get rid of the need for using a proxy system
public class CommonProxy {

    protected final String[] API_PRESENT_MESSAGE = {"Mekanism API jar detected (Mekanism-<version>-api.jar),",
                                                    "please delete it from your mods folder and restart the game."};

    /**
     * Set up and load the utilities this mod uses.
     */
    public void init() {
        MinecraftForge.EVENT_BUS.register(Mekanism.worldTickHandler);
    }

    /**
     * Whether or not the game is paused.
     */
    public boolean isPaused() {
        return false;
    }

    /**
     * Adds block hit effects on the client side.
     */
    public void addHitEffects(Coord4D coord, BlockRayTraceResult mop) {
    }

    /**
     * Does the multiblock creation animation, starting from the rendering block.
     */
    public void doMultiblockSparkle(TileEntity tile, BlockPos corner1, BlockPos corner2, INodeChecker checker) {
    }

    /**
     * Does the multiblock creation animation, starting from the rendering block.
     */
    public void doMultiblockSparkle(TileEntity tile, BlockPos renderLoc, int length, int width, int height, INodeChecker checker) {
    }

    public double getReach(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            return player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
        }
        return 0;
    }

    public final WeakReference<PlayerEntity> getDummyPlayer(ServerWorld world) {
        return MekFakePlayer.getInstance(world);
    }

    public final WeakReference<PlayerEntity> getDummyPlayer(ServerWorld world, double x, double y, double z) {
        return MekFakePlayer.getInstance(world, x, y, z);
    }

    public final WeakReference<PlayerEntity> getDummyPlayer(ServerWorld world, BlockPos pos) {
        return getDummyPlayer(world, pos.getX(), pos.getY(), pos.getZ());
    }

    //TODO: Evaluate if there is even a reason to have this
    public PlayerEntity getPlayer(Supplier<Context> context) {
        return context.get().getSender();
    }

    @Nullable
    public World tryGetMainWorld() {
        return ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD);
    }

    public void throwApiPresentException() {
        throw new RuntimeException(String.join(" ", API_PRESENT_MESSAGE));
    }
}