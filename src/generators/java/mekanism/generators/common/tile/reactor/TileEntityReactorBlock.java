package mekanism.generators.common.tile.reactor;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import mekanism.api.Coord4D;
import mekanism.api.providers.IBlockProvider;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.FusionReactor;
import mekanism.generators.common.registries.GeneratorsBlocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityReactorBlock extends TileEntityMekanism {

    private FusionReactor fusionReactor;

    private boolean attempted;

    public boolean changed;

    public TileEntityReactorBlock() {
        this(GeneratorsBlocks.REACTOR_FRAME);
    }

    public TileEntityReactorBlock(IBlockProvider blockProvider) {
        super(blockProvider);
    }

    public abstract boolean isFrame();

    public FusionReactor getReactor() {
        return fusionReactor;
    }

    public void setReactor(FusionReactor reactor) {
        if (reactor != fusionReactor) {
            changed = true;
        }
        fusionReactor = reactor;
    }

    @Override
    public void remove() {
        super.remove();
        formMultiblock(false);
    }

    @Override
    protected void onUpdateClient() {
        super.onUpdateClient();
        resetChanged();
    }

    protected void resetChanged() {
        if (changed) {
            changed = false;
        }
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        resetChanged();
        if (ticker == 5 && !attempted && (getReactor() == null || !getReactor().isFormed())) {
            updateController(false);
        }
        attempted = false;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        formMultiblock(true);
    }

    @Override
    public void onAdded() {
        super.onAdded();
        if (!isRemote()) {
            if (getReactor() == null) {
                updateController(true);
            } else {
                formMultiblock(false);
            }
        }
    }

    protected void formMultiblock(boolean keepBurning) {
        if (getReactor() != null) {
            getReactor().formMultiblock(keepBurning);
        }
    }

    private void updateController(boolean fromAdding) {
        if (this instanceof TileEntityReactorController) {
            if (!fromAdding) {
                TileEntityReactorController controller = (TileEntityReactorController) this;
                if (!controller.isFormed()) {
                    formMultiblock(false);
                }
            }
        } else {
            TileEntityReactorController found = new ControllerFinder().find();
            if (found != null && !found.isFormed()) {
                found.formMultiblock(false);
            }
        }
    }

    public class ControllerFinder {

        public TileEntityReactorController found;

        public Set<Coord4D> iterated = new ObjectOpenHashSet<>();

        public void loop(Coord4D pos) {
            if (iterated.size() > 512 || found != null) {
                return;
            }
            World world = getWorld();
            if (world == null) {
                return;
            }

            iterated.add(pos);
            for (Direction side : EnumUtils.DIRECTIONS) {
                Coord4D coord = pos.offset(side);
                BlockPos coordPos = coord.getPos();
                if (!iterated.contains(coord)) {
                    TileEntityReactorBlock tile = MekanismUtils.getTileEntity(TileEntityReactorBlock.class, world, coordPos);
                    if (tile != null) {
                        tile.attempted = true;
                        if (tile instanceof TileEntityReactorController) {
                            found = (TileEntityReactorController) tile;
                            return;
                        }
                        loop(coord);
                    }
                }
            }
        }

        public TileEntityReactorController find() {
            loop(Coord4D.get(TileEntityReactorBlock.this));
            return found;
        }
    }
}