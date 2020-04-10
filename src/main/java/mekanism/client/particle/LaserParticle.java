package mekanism.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import javax.annotation.Nonnull;
import mekanism.api.Pos3D;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.particle.LaserParticleData;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LaserParticle extends SpriteTexturedParticle {

    private static final float RADIAN_45 = (float) Math.toRadians(45);
    private static final float RADIAN_90 = (float) Math.toRadians(90);

    private final Direction direction;
    private final float halfLength;

    private LaserParticle(World world, Pos3D start, Pos3D end, Direction dir, FloatingLong energy) {
        super(world, (start.x + end.x) / 2D, (start.y + end.y) / 2D, (start.z + end.z) / 2D);
        maxAge = 5;
        particleRed = 1;
        particleGreen = 0;
        particleBlue = 0;
        particleAlpha = 0.1F;
        particleScale = (float) Math.min(energy.divide(MekanismConfig.usage.laser.get().multiply(10)).doubleValue(), 0.6);
        halfLength = (float) (end.distance(start) / 2);
        direction = dir;
    }

    @Override
    public void renderParticle(IVertexBuilder vertexBuilder, ActiveRenderInfo renderInfo, float partialTicks) {
        Vec3d view = renderInfo.getProjectedView();
        float newX = (float) (MathHelper.lerp(partialTicks, prevPosX, posX) - view.getX());
        float newY = (float) (MathHelper.lerp(partialTicks, prevPosY, posY) - view.getY());
        float newZ = (float) (MathHelper.lerp(partialTicks, prevPosZ, posZ) - view.getZ());
        float uMin = getMinU();
        float uMax = getMaxU();
        float vMin = getMinV();
        float vMax = getMaxV();
        //TODO: Do we need to disable cull, we previously had it disabled, was that for purposes of rendering when underwater
        // if it even showed under water before or what
        Quaternion quaternion = direction.getRotation();
        quaternion.multiply(Vector3f.YP.rotation(RADIAN_45));
        drawComponent(vertexBuilder, getResultVector(quaternion, newX, newY, newZ), uMin, uMax, vMin, vMax);
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.multiply(Vector3f.YP.rotation(RADIAN_90));
        drawComponent(vertexBuilder, getResultVector(quaternion2, newX, newY, newZ), uMin, uMax, vMin, vMax);
    }

    private Vector3f[] getResultVector(Quaternion quaternion, float newX, float newY, float newZ) {
        Vector3f[] resultVector = new Vector3f[]{
              new Vector3f(-particleScale, -halfLength, 0),
              new Vector3f(-particleScale, halfLength, 0),
              new Vector3f(particleScale, halfLength, 0),
              new Vector3f(particleScale, -halfLength, 0)
        };
        for (Vector3f vec : resultVector) {
            vec.transform(quaternion);
            vec.add(newX, newY, newZ);
        }
        return resultVector;
    }

    private void drawComponent(IVertexBuilder vertexBuilder, Vector3f[] resultVector, float uMin, float uMax, float vMin, float vMax) {
        vertexBuilder.pos(resultVector[0].getX(), resultVector[0].getY(), resultVector[0].getZ()).tex(uMax, vMax).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(240, 240).endVertex();
        vertexBuilder.pos(resultVector[1].getX(), resultVector[1].getY(), resultVector[1].getZ()).tex(uMax, vMin).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(240, 240).endVertex();
        vertexBuilder.pos(resultVector[2].getX(), resultVector[2].getY(), resultVector[2].getZ()).tex(uMin, vMin).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(240, 240).endVertex();
        vertexBuilder.pos(resultVector[3].getX(), resultVector[3].getY(), resultVector[3].getZ()).tex(uMin, vMax).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(240, 240).endVertex();
    }

    @Nonnull
    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements IParticleFactory<LaserParticleData> {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public LaserParticle makeParticle(LaserParticleData data, @Nonnull World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Pos3D start = new Pos3D(x, y, z);
            Pos3D end = start.translate(data.direction, data.distance);
            LaserParticle particleLaser = new LaserParticle(world, start, end, data.direction, data.energy);
            particleLaser.selectSpriteRandomly(this.spriteSet);
            return particleLaser;
        }
    }
}