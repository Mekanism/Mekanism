package mekanism.common.content.miner;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import mekanism.api.Chunk3D;
import mekanism.api.Coord4D;
import mekanism.common.HashList;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Region;
import net.minecraftforge.fluids.IFluidBlock;

public class ThreadMinerSearch extends Thread {

    private TileEntityDigitalMiner tileEntity;

    public State state = State.IDLE;

    private Map<Chunk3D, BitSet> oresToMine = new HashMap<>();
    private Map<Integer, MinerFilter> replaceMap = new HashMap<>();
    private Map<Block, MinerFilter> acceptedItems = new HashMap<>();
    private Region chunkCache = null;

    public int found = 0;

    public ThreadMinerSearch(TileEntityDigitalMiner tile) {
        tileEntity = tile;
    }

    public void setChunkCache(Region cache) {
        this.chunkCache = cache;
    }

    @Override
    public void run() {
        state = State.SEARCHING;
        HashList<MinerFilter> filters = tileEntity.getFilters();
        if (!tileEntity.inverse && filters.isEmpty()) {
            state = State.FINISHED;
            return;
        }
        Coord4D coord = tileEntity.getStartingCoord();
        int diameter = tileEntity.getDiameter();
        int size = tileEntity.getTotalSize();
        Block info;
        BlockPos minerPos = tileEntity.getPos();

        for (int i = 0; i < size; i++) {
            if (tileEntity.isRemoved()) {
                //Make sure the miner is still valid and something hasn't gone wrong
                return;
            }
            int x = coord.x + i % diameter;
            int z = coord.z + (i / diameter) % diameter;
            int y = coord.y + (i / diameter / diameter);
            if (minerPos.getX() == x && minerPos.getY() == y && minerPos.getZ() == z) {
                //Skip the miner itself
                continue;
            }

            BlockPos testPos = new BlockPos(x, y, z);
            if (!chunkCache.isBlockLoaded(testPos) || MekanismUtils.getTileEntity(TileEntityBoundingBlock.class, chunkCache, testPos) != null) {
                //If it is not loaded or it is a bounding block skip it
                continue;
            }

            BlockState state = chunkCache.getBlockState(testPos);
            info = state.getBlock();

            if (info instanceof FlowingFluidBlock || info instanceof IFluidBlock || info.isAir(state, chunkCache, testPos)) {
                //Skip air and liquids
                continue;
            }

            if (state.getBlockHardness(chunkCache, testPos) >= 0) {
                MinerFilter filterFound = null;
                if (acceptedItems.containsKey(info)) {
                    filterFound = acceptedItems.get(info);
                } else {
                    ItemStack stack = new ItemStack(info);
                    if (tileEntity.isReplaceStack(stack)) {
                        continue;
                    }
                    for (MinerFilter filter : filters) {
                        if (filter.canFilter(stack)) {
                            filterFound = filter;
                            break;
                        }
                    }
                    acceptedItems.put(info, filterFound);
                }
                if (tileEntity.inverse == (filterFound == null)) {
                    set(i, new Coord4D(x, y, z, chunkCache.getDimension().getType()));
                    replaceMap.put(i, filterFound);
                    found++;
                }
            }
        }

        state = State.FINISHED;
        tileEntity.oresToMine = oresToMine;
        tileEntity.replaceMap = replaceMap;
        chunkCache = null;
        MekanismUtils.saveChunk(tileEntity);
    }

    public void set(int i, Coord4D location) {
        Chunk3D chunk = new Chunk3D(location);
        oresToMine.computeIfAbsent(chunk, k -> new BitSet());
        oresToMine.get(chunk).set(i);
    }

    public void reset() {
        state = State.IDLE;
        chunkCache = null;
    }

    public enum State {
        IDLE("Not ready"),
        SEARCHING("Searching"),
        PAUSED("Paused"),
        FINISHED("Ready");

        public String desc;

        State(String s) {
            desc = s;
        }
    }
}