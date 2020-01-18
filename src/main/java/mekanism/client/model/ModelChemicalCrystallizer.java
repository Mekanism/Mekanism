package mekanism.client.model;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import org.lwjgl.opengl.GL11;

public class ModelChemicalCrystallizer extends Model {

    private final RendererModel tray;
    private final RendererModel support4;
    private final RendererModel rimBack;
    private final RendererModel portRight;
    private final RendererModel rimRight;
    private final RendererModel rimLeft;
    private final RendererModel rimFront;
    private final RendererModel portLeft;
    private final RendererModel support3;
    private final RendererModel support2;
    private final RendererModel support1;
    private final RendererModel tank;
    private final RendererModel rod1;
    private final RendererModel rod2;
    private final RendererModel rod3;
    private final RendererModel base;
    private final RendererModel Shape1;

    public ModelChemicalCrystallizer() {
        textureWidth = 128;
        textureHeight = 64;

        tray = new RendererModel(this, 48, 0);
        tray.addBox(0F, 0F, 0F, 10, 1, 10);
        tray.setRotationPoint(-5F, 18.5F, -5F);
        tray.setTextureSize(128, 64);
        tray.mirror = true;
        setRotation(tray, 0F, 0F, 0F);
        support4 = new RendererModel(this, 0, 0);
        support4.addBox(0F, 0F, 0F, 1, 5, 1);
        support4.setRotationPoint(6.5F, 13F, 6.5F);
        support4.setTextureSize(128, 64);
        support4.mirror = true;
        setRotation(support4, 0F, 0F, 0F);
        rimBack = new RendererModel(this, 0, 46);
        rimBack.addBox(0F, 0F, 0F, 16, 2, 2);
        rimBack.setRotationPoint(-8F, 17F, 6F);
        rimBack.setTextureSize(128, 64);
        rimBack.mirror = true;
        setRotation(rimBack, 0F, 0F, 0F);
        portRight = new RendererModel(this, 54, 42);
        portRight.mirror = true;
        portRight.addBox(0F, 0F, 0F, 1, 10, 10);
        portRight.setRotationPoint(7.01F, 11F, -5F);
        portRight.setTextureSize(128, 64);
        setRotation(portRight, 0F, 0F, 0F);
        rimRight = new RendererModel(this, 0, 50);
        rimRight.mirror = true;
        rimRight.addBox(0F, 0F, 0F, 2, 2, 12);
        rimRight.setRotationPoint(6F, 17F, -6F);
        rimRight.setTextureSize(128, 64);
        setRotation(rimRight, 0F, 0F, 0F);
        rimLeft = new RendererModel(this, 0, 50);
        rimLeft.addBox(0F, 0F, 0F, 2, 2, 12);
        rimLeft.setRotationPoint(-8F, 17F, -6F);
        rimLeft.setTextureSize(128, 64);
        rimLeft.mirror = true;
        setRotation(rimLeft, 0F, 0F, 0F);
        rimFront = new RendererModel(this, 0, 42);
        rimFront.addBox(0F, 0F, 0F, 16, 2, 2);
        rimFront.setRotationPoint(-8F, 17F, -8F);
        rimFront.setTextureSize(128, 64);
        rimFront.mirror = true;
        setRotation(rimFront, 0F, 0F, 0F);
        portLeft = new RendererModel(this, 36, 42);
        portLeft.addBox(0F, 0F, 0F, 1, 8, 8);
        portLeft.setRotationPoint(-8.01F, 12F, -4F);
        portLeft.setTextureSize(128, 64);
        portLeft.mirror = true;
        setRotation(portLeft, 0F, 0F, 0F);
        support3 = new RendererModel(this, 0, 0);
        support3.addBox(0F, 0F, 0F, 1, 5, 1);
        support3.setRotationPoint(-7.5F, 13F, 6.5F);
        support3.setTextureSize(128, 64);
        support3.mirror = true;
        setRotation(support3, 0F, 0F, 0F);
        support2 = new RendererModel(this, 0, 0);
        support2.addBox(0F, 0F, 0F, 1, 5, 1);
        support2.setRotationPoint(6.5F, 13F, -7.5F);
        support2.setTextureSize(128, 64);
        support2.mirror = true;
        setRotation(support2, 0F, 0F, 0F);
        support1 = new RendererModel(this, 0, 0);
        support1.addBox(0F, 0F, 0F, 1, 5, 1);
        support1.setRotationPoint(-7.5F, 13F, -7.5F);
        support1.setTextureSize(128, 64);
        support1.mirror = true;
        setRotation(support1, 0F, 0F, 0F);
        tank = new RendererModel(this, 0, 0);
        tank.addBox(0F, 0F, 0F, 16, 5, 16);
        tank.setRotationPoint(-8F, 8F, -8F);
        tank.setTextureSize(128, 64);
        tank.mirror = true;
        setRotation(tank, 0F, 0F, 0F);
        rod1 = new RendererModel(this, 8, 0);
        rod1.addBox(0F, 0F, 0F, 1, 2, 1);
        rod1.setRotationPoint(-2F, 13F, 0F);
        rod1.setTextureSize(128, 64);
        rod1.mirror = true;
        setRotation(rod1, 0F, 0F, 0F);
        rod2 = new RendererModel(this, 8, 3);
        rod2.addBox(0F, 0F, 0F, 1, 3, 1);
        rod2.setRotationPoint(1F, 13F, 1F);
        rod2.setTextureSize(128, 64);
        rod2.mirror = true;
        setRotation(rod2, 0F, 0F, 0F);
        rod3 = new RendererModel(this, 4, 0);
        rod3.addBox(0F, 0F, 0F, 1, 4, 1);
        rod3.setRotationPoint(-0.5F, 13F, -2F);
        rod3.setTextureSize(128, 64);
        rod3.mirror = true;
        setRotation(rod3, 0F, 0F, 0F);
        base = new RendererModel(this, 0, 21);
        base.addBox(0F, 0F, 0F, 16, 5, 16);
        base.setRotationPoint(-8F, 19F, -8F);
        base.setTextureSize(128, 64);
        base.mirror = true;
        setRotation(base, 0F, 0F, 0F);
        Shape1 = new RendererModel(this, 64, 11);
        Shape1.addBox(0F, 0F, 0F, 14, 4, 14);
        Shape1.setRotationPoint(-7F, 13F, -7F);
        Shape1.setTextureSize(128, 64);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
    }

    public void render(float size) {
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        tray.render(size);
        support4.render(size);
        rimBack.render(size);
        portRight.render(size);
        rimRight.render(size);
        rimLeft.render(size);
        rimFront.render(size);
        portLeft.render(size);
        support3.render(size);
        support2.render(size);
        support1.render(size);
        tank.render(size);
        rod1.render(size);
        rod2.render(size);
        rod3.render(size);
        base.render(size);
        Shape1.render(size);

        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }

    private void setRotation(RendererModel model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}