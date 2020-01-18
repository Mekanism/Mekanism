package mekanism.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelSecurityDesk extends Model {

    private static final ResourceLocation OVERLAY = MekanismUtils.getResource(ResourceType.RENDER, "security_desk_overlay.png");

    private final RendererModel deskTop;
    private final RendererModel deskBase;
    private final RendererModel led;
    private final RendererModel monitorBack;
    private final RendererModel keyboard;
    private final RendererModel monitor;
    private final RendererModel standNeck;
    private final RendererModel standBase;
    private final RendererModel deskMiddle;
    private final RendererModel monitorScreen;

    public ModelSecurityDesk() {
        textureWidth = 128;
        textureHeight = 64;

        deskTop = new RendererModel(this, 0, 0);
        deskTop.addBox(0F, 0F, 0F, 16, 7, 16);
        deskTop.setRotationPoint(-8F, 11F, -8F);
        deskTop.setTextureSize(128, 64);
        deskTop.mirror = true;
        setRotation(deskTop, 0F, 0F, 0F);
        deskBase = new RendererModel(this, 0, 38);
        deskBase.addBox(0F, 0F, 0F, 16, 5, 16);
        deskBase.setRotationPoint(-8F, 19F, -8F);
        deskBase.setTextureSize(128, 64);
        deskBase.mirror = true;
        setRotation(deskBase, 0F, 0F, 0F);
        led = new RendererModel(this, 0, 0);
        led.addBox(12F, 4.5F, -1.5F, 1, 1, 1);
        led.setRotationPoint(-7F, 5F, 4F);
        led.setTextureSize(128, 64);
        led.mirror = true;
        setRotation(led, -0.4712389F, 0F, 0F);
        monitorBack = new RendererModel(this, 82, 0);
        monitorBack.addBox(1F, -3F, 0F, 12, 6, 1);
        monitorBack.setRotationPoint(-7F, 5F, 4F);
        monitorBack.setTextureSize(128, 64);
        monitorBack.mirror = true;
        setRotation(monitorBack, -0.4712389F, 0F, 0F);
        keyboard = new RendererModel(this, 64, 27);
        keyboard.addBox(0F, 0F, 0F, 10, 1, 5);
        keyboard.setRotationPoint(-5F, 10.5F, -6F);
        keyboard.setTextureSize(128, 64);
        keyboard.mirror = true;
        setRotation(keyboard, 0.0872665F, 0F, 0F);
        monitor = new RendererModel(this, 64, 10);
        monitor.addBox(0F, -5F, -2F, 14, 10, 2);
        monitor.setRotationPoint(-7F, 5F, 4F);
        monitor.setTextureSize(128, 64);
        monitor.mirror = true;
        setRotation(monitor, -0.4712389F, 0F, 0F);
        standNeck = new RendererModel(this, 96, 7);
        standNeck.addBox(0F, -7F, -1F, 2, 7, 1);
        standNeck.setRotationPoint(-1F, 10F, 6F);
        standNeck.setTextureSize(128, 64);
        standNeck.mirror = true;
        setRotation(standNeck, 0.0698132F, 0F, 0F);
        standBase = new RendererModel(this, 64, 22);
        standBase.addBox(0F, 0F, -4F, 8, 1, 4);
        standBase.setRotationPoint(-4F, 10F, 6F);
        standBase.setTextureSize(128, 64);
        standBase.mirror = true;
        setRotation(standBase, 0.1047198F, 0F, 0F);
        deskMiddle = new RendererModel(this, 0, 23);
        deskMiddle.addBox(0F, 0F, 0F, 14, 1, 14);
        deskMiddle.setRotationPoint(-7F, 18F, -7F);
        deskMiddle.setTextureSize(128, 64);
        deskMiddle.mirror = true;
        setRotation(deskMiddle, 0F, 0F, 0F);
        monitorScreen = new RendererModel(this, 64, 33);
        monitorScreen.addBox(0.5F, -4.5F, -2.01F, 13, 9, 2);
        monitorScreen.setRotationPoint(-7F, 5F, 4F);
        monitorScreen.setTextureSize(128, 64);
        monitorScreen.mirror = true;
        setRotation(monitorScreen, -0.4712389F, 0F, 0F);
    }

    public void render(float size, TextureManager manager) {
        GlStateManager.pushMatrix();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        doRender(size);

        manager.bindTexture(OVERLAY);
        GlStateManager.scalef(1.001F, 1.001F, 1.001F);
        GlStateManager.translatef(0, -0.0011F, 0);
        GlowInfo glowInfo = MekanismRenderer.enableGlow();

        doRender(size);

        MekanismRenderer.disableGlow(glowInfo);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.popMatrix();
    }

    private void doRender(float size) {
        deskTop.render(size);
        deskBase.render(size);
        led.render(size);
        monitorBack.render(size);
        keyboard.render(size);
        monitor.render(size);
        standNeck.render(size);
        standBase.render(size);
        deskMiddle.render(size);
        monitorScreen.render(size);
    }

    private void setRotation(RendererModel model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}