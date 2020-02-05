package mekanism.common.frequency;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.TileNetworkList;
import mekanism.common.PacketHandler;
import mekanism.common.util.MekanismUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class Frequency {

    public static final String TELEPORTER = "Teleporter";

    public String name;
    public UUID ownerUUID;
    public String clientOwner;

    public boolean valid = true;

    public boolean publicFreq;

    public Set<Coord4D> activeCoords = new ObjectOpenHashSet<>();

    public Frequency(String n, UUID uuid) {
        name = n;
        ownerUUID = uuid;
    }

    public Frequency(CompoundNBT nbtTags) {
        read(nbtTags);
    }

    public Frequency(PacketBuffer dataStream) {
        read(dataStream);
    }

    public boolean isPublic() {
        return publicFreq;
    }

    public Frequency setPublic(boolean isPublic) {
        publicFreq = isPublic;
        return this;
    }

    public boolean isPrivate() {
        return !publicFreq;
    }

    public Coord4D getClosestCoords(Coord4D coord) {
        Coord4D closest = null;
        for (Coord4D iterCoord : activeCoords) {
            if (iterCoord.equals(coord)) {
                continue;
            }
            if (closest == null) {
                closest = iterCoord;
                continue;
            }

            if (coord.dimension != closest.dimension && coord.dimension == iterCoord.dimension) {
                closest = iterCoord;
            } else if (coord.dimension != closest.dimension || coord.dimension == iterCoord.dimension) {
                if (coord.distanceTo(closest) > coord.distanceTo(iterCoord)) {
                    closest = iterCoord;
                }
            }
        }
        return closest;
    }

    public void write(CompoundNBT nbtTags) {
        nbtTags.putString("name", name);
        nbtTags.putString("ownerUUID", ownerUUID.toString());
        nbtTags.putBoolean("publicFreq", publicFreq);
    }

    protected void read(CompoundNBT nbtTags) {
        name = nbtTags.getString("name");
        ownerUUID = UUID.fromString(nbtTags.getString("ownerUUID"));
        publicFreq = nbtTags.getBoolean("publicFreq");
    }

    public void write(TileNetworkList data) {
        data.add(name);
        data.add(ownerUUID);
        data.add(MekanismUtils.getLastKnownUsername(ownerUUID));
        data.add(publicFreq);
    }

    protected void read(PacketBuffer dataStream) {
        name = PacketHandler.readString(dataStream);
        ownerUUID = dataStream.readUniqueId();
        clientOwner = PacketHandler.readString(dataStream);
        publicFreq = dataStream.readBoolean();
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + name.hashCode();
        code = 31 * code + ownerUUID.hashCode();
        code = 31 * code + (publicFreq ? 1 : 0);
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Frequency && ((Frequency) obj).name.equals(name) && ((Frequency) obj).ownerUUID.equals(ownerUUID) && ((Frequency) obj).publicFreq == publicFreq;
    }

    public Identity getIdentity() {
        return new Identity(name, publicFreq);
    }

    public static class Identity {

        public String name;
        public boolean publicFreq;

        private Identity(String name, boolean publicFreq) {
            this.name = name;
            this.publicFreq = publicFreq;
        }

        @Nullable
        public static Identity load(CompoundNBT data) {
            if (!data.getString("name").isEmpty()) {
                return new Identity(data.getString("name"), data.getBoolean("publicFreq"));
            }
            return null;
        }

        public CompoundNBT serialize() {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("name", name);
            tag.putBoolean("publicFreq", publicFreq);
            return tag;
        }
    }
}