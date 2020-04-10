package mekanism.common.tags;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Builder;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ForgeRegistryTagCollection<T extends IForgeRegistryEntry<T>> extends TagCollection<T> {

    private final IForgeRegistry<T> registry;

    public ForgeRegistryTagCollection(IForgeRegistry<T> registry, String location, String type) {
        super(key -> Optional.ofNullable(registry.getValue(key)), location, false, type);
        this.registry = registry;
    }

    public void write(PacketBuffer buffer) {
        Map<ResourceLocation, Tag<T>> tagMap = this.getTagMap();
        buffer.writeVarInt(tagMap.size());
        for (Entry<ResourceLocation, Tag<T>> entry : tagMap.entrySet()) {
            buffer.writeResourceLocation(entry.getKey());
            Tag<T> tag = entry.getValue();
            buffer.writeVarInt(tag.getAllElements().size());
            for (T element : tag.getAllElements()) {
                ResourceLocation key = this.registry.getKey(element);
                if (key != null) {
                    buffer.writeResourceLocation(key);
                }
            }
        }
    }

    public void read(PacketBuffer buffer) {
        Map<ResourceLocation, Tag<T>> tagMap = new Object2ObjectOpenHashMap<>();
        int tagCount = buffer.readVarInt();
        for (int i = 0; i < tagCount; ++i) {
            ResourceLocation resourceLocation = buffer.readResourceLocation();
            int elementCount = buffer.readVarInt();
            Builder<T> builder = Builder.create();
            for (int j = 0; j < elementCount; ++j) {
                T value = this.registry.getValue(buffer.readResourceLocation());
                if (value != null) {
                    //Should never be null anyways
                    //TODO: Should we throw a warning when it is
                    builder.add(value);
                }
            }
            tagMap.put(resourceLocation, builder.build(resourceLocation));
        }
        this.toImmutable(tagMap);
    }
}