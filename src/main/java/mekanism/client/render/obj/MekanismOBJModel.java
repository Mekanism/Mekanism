package mekanism.client.render.obj;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJModel;

//TODO: Should this be renamed to be more transmitter specific
public class MekanismOBJModel extends OBJModel {

    private ResourceLocation location;

    public MekanismOBJModel(MaterialLibrary matLib, ResourceLocation modelLocation) {
        super(matLib, modelLocation);
        location = modelLocation;
    }

    @Nonnull
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, @Nonnull VertexFormat format) {
        IBakedModel preBaked = super.bake(bakery, spriteGetter, sprite, format);
        return new TransmitterModel(preBaked, this, sprite.getState(), format, TransmitterModel.getTexturesForOBJModel(preBaked), null);
    }

    @Nonnull
    @Override
    public IUnbakedModel process(@Nonnull ImmutableMap<String, String> customData) {
        return new MekanismOBJModel(getMatLib(), location);
    }

    @Nonnull
    @Override
    public IUnbakedModel retexture(@Nonnull ImmutableMap<String, String> textures) {
        return new MekanismOBJModel(getMatLib().makeLibWithReplacements(textures), location);
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        return super.getTextures(modelGetter, missingTextureErrors).stream().filter(r -> !r.getPath().startsWith("#")).collect(Collectors.toList());
    }
}