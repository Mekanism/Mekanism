package mekanism.api;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DataHandlerUtils {

    /**
     * Helper to read and load a list of tanks from a {@link ListNBT}
     */
    public static void readTanks(List<? extends INBTSerializable<CompoundNBT>> tanks, ListNBT storedTanks) {
        readContents(tanks, storedTanks, NBTConstants.TANK);
    }

    /**
     * Helper to read and load a list of tanks to a {@link ListNBT}
     */
    public static ListNBT writeTanks(List<? extends INBTSerializable<CompoundNBT>> tanks) {
        return writeContents(tanks, NBTConstants.TANK);
    }

    /**
     * Helper to read and load a list of containers from a {@link ListNBT}
     */
    public static void readContainers(List<? extends INBTSerializable<CompoundNBT>> containers, ListNBT storedContainers) {
        readContents(containers, storedContainers, NBTConstants.CONTAINER);
    }

    /**
     * Helper to read and load a list of containers to a {@link ListNBT}
     */
    public static ListNBT writeContainers(List<? extends INBTSerializable<CompoundNBT>> containers) {
        return writeContents(containers, NBTConstants.CONTAINER);
    }

    /**
     * Helper to read and load a list of tanks from a {@link ListNBT}
     */
    public static void readSlots(List<? extends INBTSerializable<CompoundNBT>> slots, ListNBT storedSlots) {
        readContents(slots, storedSlots, NBTConstants.SLOT);
    }

    /**
     * Helper to read and load a list of tanks to a {@link ListNBT}
     */
    public static ListNBT writeSlots(List<? extends INBTSerializable<CompoundNBT>> slots) {
        return writeContents(slots, NBTConstants.SLOT);
    }

    /**
     * Helper to read and load a list of handler contents from a {@link ListNBT}
     */
    public static void readContents(List<? extends INBTSerializable<CompoundNBT>> contents, ListNBT storedContents, String key) {
        int size = contents.size();
        for (int tagCount = 0; tagCount < storedContents.size(); tagCount++) {
            CompoundNBT tagCompound = storedContents.getCompound(tagCount);
            byte id = tagCompound.getByte(key);
            if (id >= 0 && id < size) {
                contents.get(id).deserializeNBT(tagCompound);
            }
        }
    }

    /**
     * Helper to read and load a list of handler contents to a {@link ListNBT}
     */
    public static ListNBT writeContents(List<? extends INBTSerializable<CompoundNBT>> contents, String key) {
        ListNBT storedContents = new ListNBT();
        for (int tank = 0; tank < contents.size(); tank++) {
            CompoundNBT tagCompound = contents.get(tank).serializeNBT();
            if (!tagCompound.isEmpty()) {
                tagCompound.putByte(key, (byte) tank);
                storedContents.add(tagCompound);
            }
        }
        return storedContents;
    }

    public static int getMaxId(ListNBT storedContents, String key) {
        int maxId = -1;
        for (int tagCount = 0; tagCount < storedContents.size(); tagCount++) {
            byte id = storedContents.getCompound(tagCount).getByte(key);
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId + 1;
    }
}