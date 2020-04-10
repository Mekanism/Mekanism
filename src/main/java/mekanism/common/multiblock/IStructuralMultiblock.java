package mekanism.common.multiblock;

import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public interface IStructuralMultiblock {

    ActionResultType onActivate(PlayerEntity player, Hand hand, ItemStack stack);

    boolean canInterface(TileEntity controller);

    void setController(Coord4D coord);

    void doUpdate();

    @Nullable
    Coord4D getController();
}