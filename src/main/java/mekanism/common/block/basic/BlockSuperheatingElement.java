package mekanism.common.block.basic;

import javax.annotation.Nonnull;
import mekanism.api.block.IHasTileEntity;
import mekanism.common.MekanismLang;
import mekanism.common.base.ILangEntry;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.states.IStateActive;
import mekanism.common.registries.MekanismTileEntityTypes;
import mekanism.common.tile.TileEntitySuperheatingElement;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;

public class BlockSuperheatingElement extends BlockMekanism implements IStateActive, IHasTileEntity<TileEntitySuperheatingElement>, IHasDescription {

    public BlockSuperheatingElement() {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(5F, 10F));
    }

    @Override
    @Deprecated
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        if (!world.isRemote) {
            TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, world, pos);
            if (tile != null) {
                tile.onNeighborChange(neighborBlock);
            }
        }
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        return isActive(state) ? 15 : super.getLightValue(state, world, pos);
    }

    //TODO: Set active state when it changes
    /*@Override
    public boolean isActive(@Nonnull TileEntity tile) {
        if (tile instanceof TileEntitySuperheatingElement) {
            //Should be true
            TileEntitySuperheatingElement heating = (TileEntitySuperheatingElement) tile;
            if (heating.multiblockUUID != null && SynchronizedBoilerData.clientHotMap.get(heating.multiblockUUID) != null) {
                return SynchronizedBoilerData.clientHotMap.get(heating.multiblockUUID);
            }
        }
        return false;
    }*/

    @Override
    public TileEntityType<TileEntitySuperheatingElement> getTileType() {
        return MekanismTileEntityTypes.SUPERHEATING_ELEMENT.getTileEntityType();
    }

    @Nonnull
    @Override
    public ILangEntry getDescription() {
        return MekanismLang.DESCRIPTION_SUPERHEATING_ELEMENT;
    }
}