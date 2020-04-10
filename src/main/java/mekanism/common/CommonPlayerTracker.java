package mekanism.common;

import mekanism.api.text.EnumColor;
import mekanism.common.block.BlockCardboardBox;
import mekanism.common.network.PacketClearRecipeCache;
import mekanism.common.network.PacketMekanismTags;
import mekanism.common.network.PacketPlayerData;
import mekanism.common.network.PacketSecurityUpdate;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonPlayerTracker {

    private static final ITextComponent ALPHA_WARNING;
    public static boolean monitoringCardboardBox;

    static {
        TranslationTextComponent hereComponent = MekanismLang.ALPHA_WARNING_HERE.translate();
        hereComponent.getStyle().setUnderlined(true).setColor(TextFormatting.BLUE).setClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/mekanism/Mekanism#alpha-status"));
        ALPHA_WARNING = MekanismLang.LOG_FORMAT.translateColored(EnumColor.RED, MekanismLang.MEKANISM, EnumColor.GRAY, MekanismLang.ALPHA_WARNING.translate(hereComponent));
    }

    public CommonPlayerTracker() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLoginEvent(PlayerLoggedInEvent event) {
        if (!event.getPlayer().world.isRemote) {
            Mekanism.packetHandler.sendTo(new PacketSecurityUpdate(), (ServerPlayerEntity) event.getPlayer());
            Mekanism.packetHandler.sendTo(new PacketMekanismTags(Mekanism.instance.getTagManager()), (ServerPlayerEntity) event.getPlayer());
            Mekanism.packetHandler.sendTo(new PacketClearRecipeCache(), (ServerPlayerEntity) event.getPlayer());
            event.getPlayer().sendMessage(ALPHA_WARNING);
        }
    }

    @SubscribeEvent
    public void onPlayerLogoutEvent(PlayerLoggedOutEvent event) {
        Mekanism.playerState.clearPlayer(event.getPlayer().getUniqueID());
    }

    @SubscribeEvent
    public void onPlayerDimChangedEvent(PlayerChangedDimensionEvent event) {
        Mekanism.playerState.clearPlayer(event.getPlayer().getUniqueID());
    }

    @SubscribeEvent
    public void onPlayerStartTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof PlayerEntity && event.getPlayer() instanceof ServerPlayerEntity) {
            Mekanism.packetHandler.sendTo(new PacketPlayerData(event.getTarget().getUniqueID()), (ServerPlayerEntity) event.getPlayer());
        }
    }

    /**
     * If the player is sneaking and the dest block is a cardboard box, ensure onBlockActivated is called, and that the item use is not.
     *
     * @param blockEvent event
     */
    @SubscribeEvent
    public void rightClickEvent(RightClickBlock blockEvent) {
        if (blockEvent.getPlayer().isShiftKeyDown() && blockEvent.getWorld().getBlockState(blockEvent.getPos()).getBlock() instanceof BlockCardboardBox) {
            blockEvent.setUseBlock(Event.Result.ALLOW);
            blockEvent.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        //TODO: What is the point of this/should we check if it is close to the location things are happening?
        // This is just how it used to be in 1.12
        if (event.getEntity() instanceof ItemEntity && monitoringCardboardBox) {
            event.setCanceled(true);
        }
    }
}