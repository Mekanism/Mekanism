package mekanism.common;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.UUID;
import mekanism.client.sound.PlayerSound.SoundType;
import mekanism.client.sound.SoundHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.network.PacketFlamethrowerData;
import mekanism.common.network.PacketJetpackData;
import mekanism.common.network.PacketScubaTankData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class PlayerState {

    private Set<UUID> activeJetpacks = new ObjectOpenHashSet<>();
    private Set<UUID> activeGasmasks = new ObjectOpenHashSet<>();
    private Set<UUID> activeFlamethrowers = new ObjectOpenHashSet<>();

    private IWorld world;

    public void clear() {
        activeJetpacks.clear();
        activeGasmasks.clear();
        activeFlamethrowers.clear();
        if (FMLEnvironment.dist.isClient()) {
            SoundHandler.clearPlayerSounds();
        }
    }

    public void clearPlayer(UUID uuid) {
        activeJetpacks.remove(uuid);
        activeGasmasks.remove(uuid);
        activeFlamethrowers.remove(uuid);
        if (FMLEnvironment.dist.isClient()) {
            SoundHandler.clearPlayerSounds(uuid);
        }
    }

    public void init(IWorld world) {
        this.world = world;
    }

    // ----------------------
    //
    // Jetpack state tracking
    //
    // ----------------------

    public void setJetpackState(UUID uuid, boolean isActive, boolean isLocal) {
        boolean alreadyActive = activeJetpacks.contains(uuid);
        boolean changed = alreadyActive != isActive;
        if (alreadyActive && !isActive) {
            // On -> off
            activeJetpacks.remove(uuid);
        } else if (!alreadyActive && isActive) {
            // Off -> on
            activeJetpacks.add(uuid);
        }

        // If something changed and we're in a remote world, take appropriate action
        if (changed && world.isRemote()) {
            // If the player is the "local" player, we need to tell the server the state has changed
            if (isLocal) {
                Mekanism.packetHandler.sendToServer(PacketJetpackData.UPDATE(uuid, isActive));
            }

            // Start a sound playing if the person is now flying
            if (isActive && MekanismConfig.client.enablePlayerSounds.get()) {
                SoundHandler.startSound(world, uuid, SoundType.JETPACK);
            }
        }
    }

    public boolean isJetpackOn(PlayerEntity p) {
        return activeJetpacks.contains(p.getUniqueID());
    }

    public Set<UUID> getActiveJetpacks() {
        return activeJetpacks;
    }

    public void setActiveJetpacks(Set<UUID> newActiveJetpacks) {
        for (UUID activeUser : newActiveJetpacks) {
            setJetpackState(activeUser, true, false);
        }
    }

    // ----------------------
    //
    // Gasmask state tracking
    //
    // ----------------------

    public void setGasmaskState(UUID uuid, boolean isActive, boolean isLocal) {
        boolean alreadyActive = activeGasmasks.contains(uuid);
        boolean changed = alreadyActive != isActive;
        if (alreadyActive && !isActive) {
            activeGasmasks.remove(uuid); // On -> off
        } else if (!alreadyActive && isActive) {
            activeGasmasks.add(uuid); // Off -> on
        }

        // If something changed and we're in a remote world, take appropriate action
        if (changed && world.isRemote()) {
            // If the player is the "local" player, we need to tell the server the state has changed
            if (isLocal) {
                Mekanism.packetHandler.sendToServer(PacketScubaTankData.UPDATE(uuid, isActive));
            }

            // Start a sound playing if the person is now using a gasmask
            if (isActive && MekanismConfig.client.enablePlayerSounds.get()) {
                SoundHandler.startSound(world, uuid, SoundType.GAS_MASK);
            }
        }
    }

    public boolean isGasmaskOn(PlayerEntity p) {
        return activeGasmasks.contains(p.getUniqueID());
    }

    public Set<UUID> getActiveGasmasks() {
        return activeGasmasks;
    }

    public void setActiveGasmasks(Set<UUID> newActiveGasmasks) {
        for (UUID activeUser : newActiveGasmasks) {
            setGasmaskState(activeUser, true, false);
        }
    }

    // ----------------------
    //
    // Flamethrower state tracking
    //
    // ----------------------

    public void setFlamethrowerState(UUID uuid, boolean isActive, boolean isLocal) {
        setFlamethrowerState(uuid, isActive, isActive, isLocal);
    }

    public void setFlamethrowerState(UUID uuid, boolean hasFlameThrower, boolean isActive, boolean isLocal) {
        boolean alreadyActive = activeFlamethrowers.contains(uuid);
        boolean changed = alreadyActive != isActive;
        if (alreadyActive && !isActive) {
            activeFlamethrowers.remove(uuid); // On -> off
        } else if (!alreadyActive && isActive) {
            activeFlamethrowers.add(uuid); // Off -> on
        }

        if (world.isRemote()) {
            boolean startSound;
            // If something changed and we're in a remote world, take appropriate action
            if (changed) {
                // If the player is the "local" player, we need to tell the server the state has changed
                if (isLocal) {
                    Mekanism.packetHandler.sendToServer(PacketFlamethrowerData.UPDATE(uuid, isActive));
                }

                // Start a sound playing if the person is now using a flamethrower
                startSound = isActive;
            } else {
                //Start the sound if it isn't already active, and still isn't, but has a flame thrower
                // This allows us to catch and start playing the idle sound
                //TODO: Currently this only happens for the local player as "having" a flame thrower is not
                // synced from server to client. This is not that big a deal, though may be something we want
                // to look into eventually
                startSound = !isActive && hasFlameThrower;
                //Note: If they just continue to hold (but not use) a flame thrower it "will" continue having this
                // attempt to start the sound. This is not a major deal as the uuid gets checked before attempting
                // to retrieve the player or actually creating a new sound object.
            }
            if (startSound && MekanismConfig.client.enablePlayerSounds.get()) {
                SoundHandler.startSound(world, uuid, SoundType.FLAMETHROWER);
            }
        }
    }

    public boolean isFlamethrowerOn(PlayerEntity p) {
        return activeFlamethrowers.contains(p.getUniqueID());
    }

    public Set<UUID> getActiveFlamethrowers() {
        return activeFlamethrowers;
    }

    public void setActiveFlamethrowers(Set<UUID> newActiveFlamethrowers) {
        for (UUID activeUser : newActiveFlamethrowers) {
            setFlamethrowerState(activeUser, true, false);
        }
    }

}