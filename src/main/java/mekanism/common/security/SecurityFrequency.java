package mekanism.common.security;

import java.util.UUID;
import mekanism.api.TileNetworkList;
import mekanism.common.HashList;
import mekanism.common.PacketHandler;
import mekanism.common.frequency.Frequency;
import mekanism.common.security.ISecurityTile.SecurityMode;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants.NBT;

//TODO: Figure out what the use of this is
public class SecurityFrequency extends Frequency {

    public static final String SECURITY = "Security";

    public boolean override;

    //TODO: Change trusted to using UUID's internally but continue having it display Strings
    public HashList<String> trusted;

    public SecurityMode securityMode;

    public SecurityFrequency(UUID uuid) {
        super("Security", uuid);
        trusted = new HashList<>();
        securityMode = SecurityMode.PUBLIC;
    }

    public SecurityFrequency(CompoundNBT nbtTags) {
        super(nbtTags);
    }

    public SecurityFrequency(PacketBuffer dataStream) {
        super(dataStream);
    }

    @Override
    public void write(CompoundNBT nbtTags) {
        super.write(nbtTags);
        nbtTags.putBoolean("override", override);
        nbtTags.putInt("securityMode", securityMode.ordinal());

        if (!trusted.isEmpty()) {
            ListNBT trustedList = new ListNBT();
            for (String s : trusted) {
                trustedList.add(new StringNBT(s));
            }
            nbtTags.put("trusted", trustedList);
        }
    }

    @Override
    protected void read(CompoundNBT nbtTags) {
        super.read(nbtTags);

        trusted = new HashList<>();
        securityMode = SecurityMode.PUBLIC;

        override = nbtTags.getBoolean("override");
        securityMode = SecurityMode.byIndexStatic(nbtTags.getInt("securityMode"));

        if (nbtTags.contains("trusted")) {
            ListNBT trustedList = nbtTags.getList("trusted", NBT.TAG_STRING);
            for (int i = 0; i < trustedList.size(); i++) {
                trusted.add(trustedList.getString(i));
            }
        }
    }

    @Override
    public void write(TileNetworkList data) {
        super.write(data);

        data.add(override);
        data.add(securityMode);

        data.add(trusted.size());
        for (String s : trusted) {
            data.add(s);
        }
    }

    @Override
    protected void read(PacketBuffer dataStream) {
        super.read(dataStream);

        trusted = new HashList<>();
        securityMode = SecurityMode.PUBLIC;

        override = dataStream.readBoolean();
        securityMode = dataStream.readEnumValue(SecurityMode.class);

        int size = dataStream.readInt();

        for (int i = 0; i < size; i++) {
            trusted.add(PacketHandler.readString(dataStream));
        }
    }
}