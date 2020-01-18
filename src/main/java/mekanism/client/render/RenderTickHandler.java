package mekanism.client.render;

import java.util.Random;
import java.util.UUID;
import mekanism.api.MekanismAPI;
import mekanism.api.Pos3D;
import mekanism.client.ClientTickHandler;
import mekanism.common.ColorRGBA;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.item.ItemConfigurator.ConfiguratorMode;
import mekanism.common.item.gear.ItemFlamethrower;
import mekanism.common.item.gear.ItemJetpack;
import mekanism.common.item.gear.ItemScubaTank;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import mekanism.common.util.text.TextComponentUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickHandler {

    public static int modeSwitchTimer = 0;
    public Random rand = new Random();
    public Minecraft minecraft = Minecraft.getInstance();

    @SubscribeEvent
    public void tickEnd(RenderTickEvent event) {
        if (event.phase == Phase.END) {
            if (minecraft.player != null && minecraft.world != null && !minecraft.isGamePaused()) {
                FontRenderer font = minecraft.fontRenderer;
                if (font == null) {
                    return;
                }

                PlayerEntity player = minecraft.player;
                World world = minecraft.player.world;
                //TODO: Fix block player is hovering
                BlockRayTraceResult pos = null;//player.rayTrace(40.0D, 1.0F);
                if (pos != null) {
                    BlockPos blockPos = pos.getPos();
                    if (world.isBlockLoaded(blockPos)) {
                        Block block = world.getBlockState(blockPos).getBlock();
                        if (block != null && MekanismAPI.debug && minecraft.currentScreen == null && !minecraft.gameSettings.showDebugInfo) {
                            String tileDisplay = "";
                            TileEntity tile = MekanismUtils.getTileEntity(world, blockPos);
                            if (tile != null && tile.getClass() != null) {
                                tileDisplay = tile.getClass().getSimpleName();
                            }

                            font.drawStringWithShadow("Block: " + block.getTranslationKey(), 1, 1, 0x404040);
                            font.drawStringWithShadow("Metadata: " + world.getBlockState(blockPos), 1, 10, 0x404040);
                            font.drawStringWithShadow("Location: " + MekanismUtils.getCoordDisplay(blockPos), 1, 19, 0x404040);
                            font.drawStringWithShadow("TileEntity: " + tileDisplay, 1, 28, 0x404040);
                            font.drawStringWithShadow("Side: " + pos.getFace(), 1, 37, 0x404040);
                        }
                    }
                }

                //todo use vanilla status bar text?
                if (modeSwitchTimer > 1 && minecraft.currentScreen == null && player.getHeldItemMainhand().getItem() instanceof ItemConfigurator) {
                    ItemStack stack = player.getHeldItemMainhand();
                    ConfiguratorMode mode = ((ItemConfigurator) stack.getItem()).getState(stack);

                    int x = minecraft.mainWindow.getScaledWidth();
                    int y = minecraft.mainWindow.getScaledHeight();
                    //TODO: Check this, though if we use vanilla status bar text it may be a lot simpler instead
                    String text = mode.getTextComponent().getFormattedText();
                    int color = new ColorRGBA(1, 1, 1, (float) modeSwitchTimer / 100F).argb();
                    font.drawString(text, x / 2 - font.getStringWidth(text) / 2, y - 60, color);
                }

                modeSwitchTimer = Math.max(modeSwitchTimer - 1, 0);

                if (modeSwitchTimer == 0) {
                    ClientTickHandler.wheelStatus = 0;
                }

                if (minecraft.currentScreen == null && !minecraft.gameSettings.hideGUI && !player.isSpectator() && !player.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()) {
                    ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

                    int y = minecraft.mainWindow.getScaledHeight();
                    boolean alignLeft = MekanismConfig.client.alignHUDLeft.get();

                    if (stack.getItem() instanceof ItemJetpack) {
                        ItemJetpack jetpack = (ItemJetpack) stack.getItem();
                        //TODO: Lang strings
                        //TODO: Also fix components to not ned the getFormattedText here
                        drawString(TextComponentUtil.build("Mode: ", jetpack.getMode(stack)).getFormattedText(), alignLeft, y - 20, 0xc8c8c8);
                        drawString(TextComponentUtil.build("Hydrogen: ", jetpack.getStored(stack)).getFormattedText(), alignLeft, y - 11, 0xc8c8c8);
                    } else if (stack.getItem() instanceof ItemScubaTank) {
                        ItemScubaTank scubaTank = (ItemScubaTank) stack.getItem();
                        //TODO: Lang Strings
                        drawString(TextComponentUtil.build("Mode: ", OnOff.of(scubaTank.getFlowing(stack), true)).getFormattedText(), alignLeft, y - 20, 0xc8c8c8);
                        drawString(TextComponentUtil.build("Oxygen: ", scubaTank.getStored(stack)).getFormattedText(), alignLeft, y - 11, 0xc8c8c8);
                    }
                }

                // Traverse a copy of jetpack state and do animations
                for (UUID uuid : Mekanism.playerState.getActiveJetpacks()) {
                    PlayerEntity p = minecraft.world.getPlayerByUuid(uuid);

                    if (p == null) {
                        continue;
                    }

                    Pos3D playerPos = new Pos3D(p).translate(0, 1.7, 0);

                    float random = (rand.nextFloat() - 0.5F) * 0.1F;

                    Pos3D vLeft = new Pos3D(-0.43, -0.55, -0.54).rotatePitch(p.isSneaking() ? 20 : 0).rotateYaw(p.renderYawOffset);
                    Pos3D vRight = new Pos3D(0.43, -0.55, -0.54).rotatePitch(p.isSneaking() ? 20 : 0).rotateYaw(p.renderYawOffset);
                    Pos3D vCenter = new Pos3D((rand.nextFloat() - 0.5F) * 0.4F, -0.86, -0.30).rotatePitch(p.isSneaking() ? 25 : 0).rotateYaw(p.renderYawOffset);

                    Pos3D rLeft = vLeft.scale(random);
                    Pos3D rRight = vRight.scale(random);

                    Pos3D mLeft = vLeft.scale(0.2).translate(new Pos3D(p.getMotion()));
                    Pos3D mRight = vRight.scale(0.2).translate(new Pos3D(p.getMotion()));
                    Pos3D mCenter = vCenter.scale(0.2).translate(new Pos3D(p.getMotion()));

                    mLeft = mLeft.translate(rLeft);
                    mRight = mRight.translate(rRight);

                    Pos3D v = playerPos.translate(vLeft).translate(new Pos3D(p.getMotion()));
                    spawnAndSetParticle(ParticleTypes.FLAME, world, v.x, v.y, v.z, mLeft.x, mLeft.y, mLeft.z);
                    spawnAndSetParticle(ParticleTypes.SMOKE, world, v.x, v.y, v.z, mLeft.x, mLeft.y, mLeft.z);

                    v = playerPos.translate(vRight).translate(new Pos3D(p.getMotion()));
                    spawnAndSetParticle(ParticleTypes.FLAME, world, v.x, v.y, v.z, mRight.x, mRight.y, mRight.z);
                    spawnAndSetParticle(ParticleTypes.SMOKE, world, v.x, v.y, v.z, mRight.x, mRight.y, mRight.z);

                    v = playerPos.translate(vCenter).translate(new Pos3D(p.getMotion()));
                    spawnAndSetParticle(ParticleTypes.FLAME, world, v.x, v.y, v.z, mCenter.x, mCenter.y, mCenter.z);
                    spawnAndSetParticle(ParticleTypes.SMOKE, world, v.x, v.y, v.z, mCenter.x, mCenter.y, mCenter.z);
                }

                // Traverse a copy of gasmask state and do animations
                if (world.getDayTime() % 4 == 0) {
                    for (UUID uuid : Mekanism.playerState.getActiveGasmasks()) {
                        PlayerEntity p = minecraft.world.getPlayerByUuid(uuid);
                        if (p == null || !p.isInWater()) {
                            continue;
                        }

                        Pos3D playerPos = new Pos3D(p).translate(0, 1.7, 0);

                        float xRand = (rand.nextFloat() - 0.5F) * 0.08F;
                        float yRand = (rand.nextFloat() - 0.5F) * 0.05F;

                        Pos3D vec = new Pos3D(0.4, 0.4, 0.4).multiply(new Pos3D(p.getLook(1))).translate(0, -0.2, 0);
                        Pos3D motion = vec.scale(0.2).translate(new Pos3D(p.getMotion()));

                        Pos3D v = playerPos.translate(vec);
                        spawnAndSetParticle(ParticleTypes.BUBBLE, world, v.x, v.y, v.z, motion.x, motion.y + 0.2, motion.z);
                    }
                }

                // Traverse a copy of flamethrower state and do animations
                if (world.getDayTime() % 4 == 0) {
                    for (PlayerEntity p : world.getPlayers()) {
                        if (!Mekanism.playerState.isFlamethrowerOn(p) && !p.isSwingInProgress) {
                            ItemStack currentItem = p.inventory.getCurrentItem();
                            if (!currentItem.isEmpty() && currentItem.getItem() instanceof ItemFlamethrower && !((ItemFlamethrower) currentItem.getItem()).getGas(currentItem).isEmpty()) {
                                Pos3D playerPos = new Pos3D(p);
                                Pos3D flameVec;
                                double flameXCoord = 0;
                                double flameYCoord = 1.5;
                                double flameZCoord = 0;
                                Vec3d motion = p.getMotion();
                                Pos3D flameMotion = new Pos3D(motion.getX(), p.onGround ? 0 : motion.getY(), motion.getZ());
                                if (player == p && minecraft.gameSettings.thirdPersonView == 0) {
                                    flameVec = new Pos3D(1, 1, 1).multiply(p.getLook(1)).rotateYaw(5).translate(flameXCoord, flameYCoord + 0.1, flameZCoord);
                                } else {
                                    flameXCoord += 0.25F;
                                    flameXCoord -= 0.45F;
                                    flameZCoord += 0.15F;
                                    if (p.isSneaking()) {
                                        flameYCoord -= 0.55F;
                                        flameZCoord -= 0.15F;
                                    }
                                    flameYCoord -= 0.5F;
                                    flameZCoord += 1.05F;
                                    flameVec = new Pos3D(flameXCoord, flameYCoord, flameZCoord).rotateYaw(p.renderYawOffset);
                                }
                                Pos3D mergedVec = playerPos.translate(flameVec);
                                spawnAndSetParticle(ParticleTypes.FLAME, world, mergedVec.x, mergedVec.y, mergedVec.z, flameMotion.x, flameMotion.y, flameMotion.z);
                            }
                        }
                    }
                }
            }
        }
    }

    public void spawnAndSetParticle(ParticleType<?> s, World world, double x, double y, double z, double velX, double velY, double velZ) {
        //TODO: Fix this
        /*Particle fx = null;
        if (s.equals(ParticleTypes.FLAME)) {
            fx = new EntityJetpackFlameFX(world, x, y, z, velX, velY, velZ);
        } else if (s.equals(ParticleTypes.SMOKE)) {
            fx = new EntityJetpackSmokeFX(world, x, y, z, velX, velY, velZ);
        } else if (s.equals(ParticleTypes.BUBBLE)) {
            fx = new EntityScubaBubbleFX(world, x, y, z, velX, velY, velZ);
        }
        minecraft.particles.addEffect(fx);*/
    }

    private void drawString(String s, boolean leftSide, int y, int color) {
        FontRenderer font = minecraft.fontRenderer;
        // Note that we always offset by 2 pixels when left or right aligned
        if (leftSide) {
            font.drawStringWithShadow(s, 2, y, color);
        } else {
            int width = font.getStringWidth(s) + 2;
            font.drawStringWithShadow(s, minecraft.mainWindow.getScaledWidth() - width, y, color);
        }
    }
}