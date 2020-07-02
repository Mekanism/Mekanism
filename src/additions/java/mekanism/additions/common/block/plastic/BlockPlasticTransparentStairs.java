package mekanism.additions.common.block.plastic;

import mekanism.api.providers.IBlockProvider;
import mekanism.api.text.EnumColor;
import mekanism.common.block.interfaces.IColoredBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPlasticTransparentStairs extends StairsBlock implements IColoredBlock {

    private final EnumColor color;

    public BlockPlasticTransparentStairs(IBlockProvider blockProvider, EnumColor color) {
        super(() -> blockProvider.getBlock().getDefaultState(),
                Properties.create(BlockPlastic.PLASTIC, color.getMapColor()).hardnessAndResistance(5F, 10F).notSolid());
        this.color = color;
    }

    @Override
    public EnumColor getColor() {
        return color;
    }

    @Override
    @Deprecated
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 0.8f;
    }

    @Override
    @Deprecated
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canCreatureSpawn(BlockState state, IBlockReader world, BlockPos pos, PlacementType type, EntityType<?> entityType) {
    	return false;
    }

    @Override
    public boolean isSideInvisible(final BlockState state, final BlockState adjacentBlockState, final Direction side) {
        final Block adjacentBlock = adjacentBlockState.getBlock();
        if (adjacentBlock instanceof BlockPlasticTransparent || adjacentBlock instanceof BlockPlasticTransparentSlab
                || adjacentBlock instanceof BlockPlasticTransparentStairs) {
            IColoredBlock plastic = ((IColoredBlock) adjacentBlock);
            if (plastic.getColor() == color) {
                VoxelShape shape = state.getShape(null, null);
                VoxelShape adjacentShape = adjacentBlockState.getShape(null, null);

                VoxelShape faceShape = shape.project(side);
                VoxelShape adjacentFaceShape = adjacentShape.project(side.getOpposite());
                return !VoxelShapes.compare(faceShape, adjacentFaceShape, IBooleanFunction.ONLY_FIRST);
            }
        }
        return false;
    }
}