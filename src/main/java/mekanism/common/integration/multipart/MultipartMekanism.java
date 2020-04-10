/*package mekanism.common.integration.multipart;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.event.DrawMultipartHighlightEvent;
import mcmultipart.api.item.ItemBlockMultipart;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartRegistry;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartCapabilityHelper;
import mcmultipart.api.multipart.MultipartOcclusionHelper;
import mcmultipart.api.ref.MCMPCapabilities;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.api.world.IMultipartBlockAccess;
import mcmultipart.multipart.MultipartRegistry;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlock;
import mekanism.common.base.LazyOptionalHelper;
import mekanism.additions.common.block.BlockGlowPanel;
import mekanism.api.block.IHasModel;
import mekanism.common.block.states.TransmitterType.Size;
import mekanism.common.block.transmitter.BlockLargeTransmitter;
import mekanism.common.block.transmitter.BlockSmallTransmitter;
import mekanism.common.block.transmitter.BlockTransmitter;
import mekanism.common.capabilities.Capabilities;
import mekanism.additions.common.tile.TileEntityGlowPanel;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.CapabilityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

@MCMPAddon
public class MultipartMekanism implements IMCMPAddon {

    public static MultipartTransmitter TRANSMITTER_MP;
    public static MultipartGlowPanel GLOWPANEL_MP;

    public static boolean hasConnectionWith(TileEntity tile, Direction side) {
        if (tile != null) {
            LazyOptionalHelper<IMultipartTile> capabilityHelper = CapabilityUtils.getCapabilityHelper(tile, MCMPCapabilities.MULTIPART_TILE, null);
            capabilityHelper.getIfPresentElseDo(multipartTile -> {
                      if (multipartTile instanceof MultipartTile && ((MultipartTile) multipartTile).getID().equals("transmitter")) {
                          IPartInfo partInfo = ((MultipartTile) multipartTile).getInfo();
                          if (partInfo != null) {
                              for (IPartInfo info : partInfo.getContainer().getParts().values()) {
                                  IMultipart multipart = info.getPart();
                                  Collection<AxisAlignedBB> origBounds = getTransmitterSideBounds(multipartTile, side);
                                  if (MultipartOcclusionHelper.testBoxIntersection(origBounds, multipart.getOcclusionBoxes(info))) {
                                      return false;
                                  }
                              }
                          }
                      }
                      return true;
                  },
                  true
            );
        }
        return true;
    }

    public static Collection<AxisAlignedBB> getTransmitterSideBounds(IMultipartTile tile, Direction side) {
        if (tile.getTileEntity() instanceof TileEntityTransmitter) {
            TileEntityTransmitter transmitter = (TileEntityTransmitter) tile.getTileEntity();
            boolean large = transmitter.getTransmitterType().getSize() == Size.LARGE;
            AxisAlignedBB ret = large ? BlockLargeTransmitter.largeSides[side.ordinal()] : BlockSmallTransmitter.smallSides[side.ordinal()];
            return Collections.singletonList(ret);
        }
        return Collections.emptyList();
    }

    static IMultipartContainer getContainer(IBlockReader world, BlockPos pos) {
        IMultipartContainer container = null;
        if (world instanceof IMultipartBlockAccess) {
            container = ((IMultipartBlockAccess) world).getPartInfo().getContainer();
        } else {
            TileEntity possibleContainer = world.getTileEntity(pos);
            if (possibleContainer instanceof IMultipartContainer) {
                container = (IMultipartContainer) possibleContainer;
            }
        }
        return container;
    }

    public static boolean hasCenterSlot(IBlockReader world, BlockPos pos) {
        boolean hasCenterSlot = false;
        IMultipartContainer container = getContainer(world, pos);
        if (container != null) {
            hasCenterSlot = container.getPart(EnumCenterSlot.CENTER).isPresent();
        }
        return hasCenterSlot;
    }

    public static TileEntity unwrapTileEntity(IBlockReader world) {
        TileEntity tile = null;
        if (world instanceof IMultipartBlockAccess) {
            tile = ((IMultipartBlockAccess) world).getPartInfo().getTile().getTileEntity();
        }
        return tile;
    }

    public static boolean placeMultipartBlock(Block block, ItemStack is, PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY,
          float hitZ, BlockState state) {
        return ItemBlockMultipart.placeAt(is, player, player.getActiveHand(), world, pos, side, hitX, hitY, hitZ,
              block::getStateForPlacement, is.getMetadata(), MultipartRegistry.INSTANCE.getPart(block),
              ((BlockItem) is.getItem())::placeBlockAt, ItemBlockMultipart::placePartAt);
    }

    @SubscribeEvent
    public void onAttachTile(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity tile = event.getObject();
        if (tile instanceof TileEntityTransmitter) {
            register(event, "transmitter");
        } else if (tile instanceof TileEntityGlowPanel) {
            register(event, "glow_panel");
        }
    }

    @Override
    public void registerParts(IMultipartRegistry registry) {
        MinecraftForge.EVENT_BUS.register(this);
        for (MekanismBlock mekanismBlock : MekanismBlock.values()) {
            Block block = mekanismBlock.getBlock();
            if (block instanceof BlockTransmitter) {
                registry.registerPartWrapper(block, TRANSMITTER_MP = new MultipartTransmitter());
            } else if (block instanceof BlockGlowPanel) {
                registry.registerPartWrapper(block, GLOWPANEL_MP = new MultipartGlowPanel());
            }
        }
        MultipartCapabilityHelper.registerCapabilityJoiner(Capabilities.TILE_NETWORK_CAPABILITY, MultipartTileNetworkJoiner::new);
    }

    private void register(AttachCapabilitiesEvent<TileEntity> e, String id) {
        e.addCapability(new ResourceLocation(Mekanism.MODID, id), new ICapabilityProvider() {
            private MultipartTile tile;

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                if (capability == MCMPCapabilities.MULTIPART_TILE) {
                    if (tile == null) {
                        tile = new MultipartTile(e.getObject(), id);
                    }
                    return MCMPCapabilities.MULTIPART_TILE.orEmpty(capability, LazyOptional.of(() -> tile));
                }
                return LazyOptional.empty();
            }
        });
    }

    public void init() {
        registerMicroMaterials();
    }

    public void registerMicroMaterials() {
        for (MekanismBlock mekanismBlock : MekanismBlock.values()) {
            Block block = mekanismBlock.getBlock();
            if (block instanceof IHasModel) {
                FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", mekanismBlock.getItemStack());
            }
        }
    }

    //No idea why mcmultipart doesnt do this itself...
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void drawBlockHighlightEvent(DrawMultipartHighlightEvent ev) {
        BlockState state = ev.getPartInfo().getState();
        if (state.getBlock() instanceof BlockGlowPanel || state.getBlock() instanceof BlockTransmitter) {
            PlayerEntity player = ev.getPlayer();
            @SuppressWarnings("deprecation")
            AxisAlignedBB bb = state.getBlock().getSelectedBoundingBox(state, ev.getPartInfo().getPartWorld(), ev.getPartInfo().getPartPos());
            //NB rendering code copied from MCMultipart
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
            RenderSystem.lineWidth(2.0F);
            RenderSystem.disableTexture2D();
            RenderSystem.depthMask(false);
            double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * ev.getPartialTicks();
            double y = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * ev.getPartialTicks();
            double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * ev.getPartialTicks();
            WorldRenderer.drawSelectionBoundingBox(bb.grow(0.002).offset(-x, -y, -z), 0.0F, 0.0F, 0.0F, 0.4F);
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture2D();
            RenderSystem.disableBlend();
            ev.setCanceled(true);
        }
    }
}*/