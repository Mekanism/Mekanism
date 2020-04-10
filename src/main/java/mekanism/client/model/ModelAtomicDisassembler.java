package mekanism.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelAtomicDisassembler extends Model {

    private static final ResourceLocation DISASSEMBLER_TEXTURE = MekanismUtils.getResource(ResourceType.RENDER, "atomic_disassembler.png");
    private final RenderType RENDER_TYPE = getRenderType(DISASSEMBLER_TEXTURE);

    private final ModelRenderer Shape1;
    private final ModelRenderer Shape2;
    private final ModelRenderer Shape5;
    private final ModelRenderer Shape6;
    private final ModelRenderer Shape7;
    private final ModelRenderer Shape9;
    private final ModelRenderer Shape16;
    private final ModelRenderer Shape10;
    private final ModelRenderer Shape3;
    private final ModelRenderer Shape11;
    private final ModelRenderer Shape4;
    private final ModelRenderer Shape12;
    private final ModelRenderer Shape13;
    private final ModelRenderer Shape14;
    private final ModelRenderer Shape15;
    private final ModelRenderer Shape8;

    public ModelAtomicDisassembler() {
        super(RenderType::getEntitySolid);
        textureWidth = 64;
        textureHeight = 32;

        Shape1 = new ModelRenderer(this, 0, 10);
        Shape1.addBox(0F, -1F, -3F, 1, 16, 1, false);
        Shape1.setRotationPoint(0F, 0F, 0F);
        Shape1.setTextureSize(64, 32);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 34, 9);
        Shape2.addBox(-0.5F, -3.5F, -3.5F, 2, 5, 2, false);
        Shape2.setRotationPoint(0F, 0F, 0F);
        Shape2.setTextureSize(64, 32);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape5 = new ModelRenderer(this, 42, 0);
        Shape5.addBox(0F, -4F, -4F, 1, 2, 10, false);
        Shape5.setRotationPoint(0F, 0F, 0F);
        Shape5.setTextureSize(64, 32);
        Shape5.mirror = true;
        setRotation(Shape5, 0F, 0F, 0F);
        Shape6 = new ModelRenderer(this, 24, 0);
        Shape6.addBox(-5F, -5.7F, -5.5F, 3, 3, 6, false);
        Shape6.setRotationPoint(0F, 0F, 0F);
        Shape6.setTextureSize(64, 32);
        Shape6.mirror = true;
        setRotation(Shape6, 0F, 0F, 0.7853982F);
        Shape7 = new ModelRenderer(this, 0, 0);
        Shape7.addBox(-0.5F, -6F, -7F, 2, 2, 8, false);
        Shape7.setRotationPoint(0F, 0F, 0F);
        Shape7.setTextureSize(64, 32);
        Shape7.mirror = true;
        setRotation(Shape7, 0F, 0F, 0F);
        Shape9 = new ModelRenderer(this, 60, 0);
        Shape9.addBox(0F, -0.5333334F, -9.6F, 1, 3, 1, false);
        Shape9.setRotationPoint(0F, 0F, 0F);
        Shape9.setTextureSize(64, 32);
        Shape9.mirror = true;
        setRotation(Shape9, -0.7853982F, 0F, 0F);
        Shape16 = new ModelRenderer(this, 58, 0);
        Shape16.addBox(0F, -9.58F, -4F, 1, 5, 2, false);
        Shape16.setRotationPoint(0F, 0F, 0F);
        Shape16.setTextureSize(64, 32);
        Shape16.mirror = true;
        setRotation(Shape16, 0.7853982F, 0F, 0F);
        Shape10 = new ModelRenderer(this, 12, 0);
        Shape10.addBox(-0.5F, -8.2F, -2.5F, 2, 1, 1, false);
        Shape10.setRotationPoint(0F, 0F, 0F);
        Shape10.setTextureSize(64, 32);
        Shape10.mirror = true;
        setRotation(Shape10, 0.7853982F, 0F, 0F);
        Shape3 = new ModelRenderer(this, 56, 0);
        Shape3.addBox(0F, -2.44F, -6.07F, 1, 2, 3, false);
        Shape3.setRotationPoint(0F, 0F, 0F);
        Shape3.setTextureSize(64, 32);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
        Shape11 = new ModelRenderer(this, 42, 14);
        Shape11.addBox(-0.5F, -0.5F, 3.5F, 2, 1, 1, false);
        Shape11.setRotationPoint(0F, -4F, 0F);
        Shape11.setTextureSize(64, 32);
        Shape11.mirror = true;
        setRotation(Shape11, 0F, 0F, 0F);
        Shape4 = new ModelRenderer(this, 30, 16);
        Shape4.addBox(-0.5F, -3.5F, -1.5F, 2, 1, 4, false);
        Shape4.setRotationPoint(0F, 0F, 0F);
        Shape4.setTextureSize(64, 32);
        Shape4.mirror = true;
        setRotation(Shape4, 0F, 0F, 0F);
        Shape12 = new ModelRenderer(this, 42, 12);
        Shape12.addBox(-0.5F, -4.5F, 1.5F, 2, 1, 1, false);
        Shape12.setRotationPoint(0F, 0F, 0F);
        Shape12.setTextureSize(64, 32);
        Shape12.mirror = true;
        setRotation(Shape12, 0F, 0F, 0F);
        Shape13 = new ModelRenderer(this, 4, 10);
        Shape13.addBox(0F, -5.3F, 0F, 1, 1, 7, false);
        Shape13.setRotationPoint(0F, 0F, 0F);
        Shape13.setTextureSize(64, 32);
        Shape13.mirror = true;
        setRotation(Shape13, 0F, 0F, 0F);
        Shape14 = new ModelRenderer(this, 60, 0);
        Shape14.addBox(0F, -4F, 6F, 1, 1, 1, false);
        Shape14.setRotationPoint(0F, 0F, 0F);
        Shape14.setTextureSize(64, 32);
        Shape14.mirror = true;
        setRotation(Shape14, 0F, 0F, 0F);
        Shape15 = new ModelRenderer(this, 26, 9);
        Shape15.addBox(-0.5F, 15F, -3.5F, 2, 4, 2, false);
        Shape15.setRotationPoint(0F, 0F, 0F);
        Shape15.setTextureSize(64, 32);
        Shape15.mirror = true;
        setRotation(Shape15, 0F, 0F, 0F);
        Shape8 = new ModelRenderer(this, 37, 0);
        Shape8.addBox(0F, -2F, -2F, 1, 4, 1, false);
        Shape8.setRotationPoint(0F, 0F, 0F);
        Shape8.setTextureSize(64, 32);
        Shape8.mirror = true;
        setRotation(Shape8, 0F, 0F, 0F);
    }

    public void render(@Nonnull MatrixStack matrix, @Nonnull IRenderTypeBuffer renderer, int light, int overlayLight) {
        IVertexBuilder vertexBuilder = renderer.getBuffer(RENDER_TYPE);
        renderBlade(matrix, vertexBuilder, MekanismRenderer.FULL_LIGHT, overlayLight, 1, 1, 1, 1);
        render(matrix, vertexBuilder, light, overlayLight, 1, 1, 1, 1);
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, @Nonnull IVertexBuilder vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        Shape1.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape2.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape6.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape7.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape13.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape10.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape11.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape4.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape12.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape15.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape8.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    private void renderBlade(@Nonnull MatrixStack matrix, @Nonnull IVertexBuilder vertexBuilder, int light, int overlayLight, float red, float green, float blue, float alpha) {
        Shape3.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape5.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape9.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape16.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
        Shape14.render(matrix, vertexBuilder, light, overlayLight, red, green, blue, alpha);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}