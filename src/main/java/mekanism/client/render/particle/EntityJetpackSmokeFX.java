//TODO: Fix this, probably just needs an AT to expose superclass constructor
/*package mekanism.client.render.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityJetpackSmokeFX extends SmokeParticle {

    public EntityJetpackSmokeFX(World world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        super(world, posX, posY, posZ, velX, velY, velZ, 1.0F);
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 190 + (int) (20F * (1.0F - Minecraft.getInstance().gameSettings.gamma));
    }

    @Override
    public void renderParticle(BufferBuilder buffer, @Nonnull ActiveRenderInfo renderInfo, float partialTicks, float rotationX, float rotationZ, float rotationYZ,
          float rotationXY, float rotationXZ) {
        if (age > 0) {
            super.renderParticle(buffer, renderInfo, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        }
    }
}*/