package mekanism.common.security;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.NBTConstants;
import mekanism.common.util.ItemDataUtils;
import net.minecraft.item.ItemStack;

public interface IOwnerItem {

    @Nullable
    default UUID getOwnerUUID(ItemStack stack) {
        if (ItemDataUtils.hasUUID(stack, NBTConstants.OWNER_UUID)) {
            return ItemDataUtils.getUniqueID(stack, NBTConstants.OWNER_UUID);
        }
        return null;
    }

    default void setOwnerUUID(@Nonnull ItemStack stack, @Nullable UUID owner) {
        if (owner == null) {
            ItemDataUtils.removeData(stack, NBTConstants.OWNER_UUID);
        } else {
            ItemDataUtils.setUUID(stack, NBTConstants.OWNER_UUID, owner);
        }
    }
}