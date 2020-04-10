package mekanism.common.tile.component;

import java.util.UUID;
import mekanism.api.Coord4D;
import mekanism.api.NBTConstants;
import mekanism.common.Mekanism;
import mekanism.common.base.ITileComponent;
import mekanism.common.config.MekanismConfig;
import mekanism.common.frequency.Frequency;
import mekanism.common.frequency.FrequencyManager;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableEnum;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.security.SecurityFrequency;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.NBTUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;

public class TileComponentSecurity implements ITileComponent {

    /**
     * TileEntity implementing this component.
     */
    public TileEntityMekanism tile;

    private UUID ownerUUID;
    private String clientOwner;

    private SecurityMode securityMode = SecurityMode.PUBLIC;

    private SecurityFrequency frequency;

    public TileComponentSecurity(TileEntityMekanism tile) {
        this.tile = tile;
        tile.addComponent(this);
    }

    public SecurityFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(UUID owner) {
        FrequencyManager manager = Mekanism.securityFrequencies;
        manager.deactivate(Coord4D.get(tile));

        for (Frequency freq : manager.getFrequencies()) {
            if (freq.ownerUUID.equals(owner)) {
                frequency = (SecurityFrequency) freq;
                frequency.activeCoords.add(Coord4D.get(tile));
                return;
            }
        }

        Frequency freq = new SecurityFrequency(owner).setPublic(true);
        freq.activeCoords.add(Coord4D.get(tile));
        manager.addFrequency(freq);
        frequency = (SecurityFrequency) freq;
        tile.markDirty(false);
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID uuid) {
        frequency = null;
        ownerUUID = uuid;
    }

    public String getClientOwner() {
        return clientOwner;
    }

    public SecurityMode getMode() {
        if (MekanismConfig.general.allowProtection.get()) {
            return securityMode;
        }
        return SecurityMode.PUBLIC;
    }

    public void setMode(SecurityMode mode) {
        securityMode = mode;
        tile.markDirty(false);
    }

    public FrequencyManager getManager(Frequency freq) {
        if (ownerUUID == null || freq == null) {
            return null;
        }
        return Mekanism.securityFrequencies;
    }

    @Override
    public void tick() {
        if (!tile.isRemote()) {
            if (frequency == null && ownerUUID != null) {
                setFrequency(ownerUUID);
            }
            FrequencyManager manager = getManager(frequency);
            if (manager == null) {
                frequency = null;
            } else {
                if (frequency != null && !frequency.valid) {
                    frequency = (SecurityFrequency) manager.validateFrequency(ownerUUID, Coord4D.get(tile), frequency);
                }
                if (frequency != null) {
                    frequency = (SecurityFrequency) manager.update(Coord4D.get(tile), frequency);
                }
            }
        }
    }

    @Override
    public void read(CompoundNBT nbtTags) {
        if (nbtTags.contains(NBTConstants.COMPONENT_SECURITY, NBT.TAG_COMPOUND)) {
            CompoundNBT securityNBT = nbtTags.getCompound(NBTConstants.COMPONENT_SECURITY);
            NBTUtils.setEnumIfPresent(securityNBT, NBTConstants.SECURITY_MODE, SecurityMode::byIndexStatic, mode -> securityMode = mode);
            NBTUtils.setUUIDIfPresent(securityNBT, NBTConstants.OWNER_UUID, uuid -> ownerUUID = uuid);
            if (securityNBT.contains(NBTConstants.FREQUENCY, NBT.TAG_COMPOUND)) {
                frequency = new SecurityFrequency(securityNBT.getCompound(NBTConstants.FREQUENCY), false);
                frequency.valid = false;
            }
        }
    }

    @Override
    public void write(CompoundNBT nbtTags) {
        CompoundNBT securityNBT = new CompoundNBT();
        securityNBT.putInt(NBTConstants.SECURITY_MODE, securityMode.ordinal());
        if (ownerUUID != null) {
            securityNBT.putUniqueId(NBTConstants.OWNER_UUID, ownerUUID);
        }
        if (frequency != null) {
            CompoundNBT frequencyTag = new CompoundNBT();
            frequency.write(frequencyTag);
            securityNBT.put(NBTConstants.FREQUENCY, frequencyTag);
        }
        nbtTags.put(NBTConstants.COMPONENT_SECURITY, securityNBT);
    }

    @Override
    public void invalidate() {
        if (!tile.isRemote() && frequency != null) {
            FrequencyManager manager = getManager(frequency);
            if (manager != null) {
                manager.deactivate(Coord4D.get(tile));
            }
        }
    }

    @Override
    public void trackForMainContainer(MekanismContainer container) {
        container.track(SyncableEnum.create(SecurityMode::byIndexStatic, SecurityMode.PUBLIC, this::getMode, this::setMode));
    }

    @Override
    public void addToUpdateTag(CompoundNBT updateTag) {
        if (ownerUUID != null) {
            updateTag.putUniqueId(NBTConstants.OWNER_UUID, ownerUUID);
            updateTag.putString(NBTConstants.OWNER_NAME, MekanismUtils.getLastKnownUsername(ownerUUID));
        }
    }

    @Override
    public void readFromUpdateTag(CompoundNBT updateTag) {
        NBTUtils.setUUIDIfPresent(updateTag, NBTConstants.OWNER_UUID, uuid -> ownerUUID = uuid);
        NBTUtils.setStringIfPresent(updateTag, NBTConstants.OWNER_NAME, uuid -> clientOwner = uuid);
    }
}