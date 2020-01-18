package mekanism.additions.client;

import mekanism.additions.client.render.entity.RenderBalloon;
import mekanism.additions.client.render.entity.RenderObsidianTNTPrimed;
import mekanism.additions.common.MekanismAdditions;
import mekanism.additions.common.entity.EntityBabySkeleton;
import mekanism.additions.common.entity.EntityBalloon;
import mekanism.additions.common.entity.EntityObsidianTNT;
import mekanism.additions.common.item.ItemBalloon;
import mekanism.additions.common.registries.AdditionsBlocks;
import mekanism.additions.common.registries.AdditionsItems;
import mekanism.api.block.IColoredBlock;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MekanismAdditions.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AdditionsClientRegistration {

    //TODO: Move this to a utils class
    private static void registerItemColorHandler(ItemColors colors, IItemColor itemColor, IItemProvider... items) {
        for (IItemProvider itemProvider : items) {
            colors.register(itemColor, itemProvider.getItem());
        }
    }

    //TODO: Move this to a utils class
    private static void registerBlockColorHandler(BlockColors blockColors, ItemColors itemColors, IBlockColor blockColor, IItemColor itemColor, IBlockProvider... blocks) {
        for (IBlockProvider additionsBlock : blocks) {
            blockColors.register(blockColor, additionsBlock.getBlock());
            itemColors.register(itemColor, additionsBlock.getItem());
        }
    }

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        new AdditionsKeyHandler();

        //Register entity rendering handlers
        RenderingRegistry.registerEntityRenderingHandler(EntityObsidianTNT.class, RenderObsidianTNTPrimed::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBalloon.class, RenderBalloon::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBabySkeleton.class, SkeletonRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(ColorHandlerEvent.Item event) {
        registerBlockColorHandler(event.getBlockColors(), event.getItemColors(), (state, world, pos, tintIndex) -> {
                  Block block = state.getBlock();
                  if (block instanceof IColoredBlock) {
                      return MekanismRenderer.getColorARGB(((IColoredBlock) block).getColor(), 1);
                  }
                  return -1;
              }, (stack, tintIndex) -> {
                  Item item = stack.getItem();
                  if (item instanceof BlockItem) {
                      Block block = ((BlockItem) item).getBlock();
                      if (block instanceof IColoredBlock) {
                          return MekanismRenderer.getColorARGB(((IColoredBlock) block).getColor(), 1);
                      }
                  }
                  return -1;
              },
              //Plastic Blocks
              AdditionsBlocks.BLACK_PLASTIC_BLOCK, AdditionsBlocks.RED_PLASTIC_BLOCK, AdditionsBlocks.GREEN_PLASTIC_BLOCK, AdditionsBlocks.BROWN_PLASTIC_BLOCK,
              AdditionsBlocks.BLUE_PLASTIC_BLOCK, AdditionsBlocks.PURPLE_PLASTIC_BLOCK, AdditionsBlocks.CYAN_PLASTIC_BLOCK, AdditionsBlocks.LIGHT_GRAY_PLASTIC_BLOCK,
              AdditionsBlocks.GRAY_PLASTIC_BLOCK, AdditionsBlocks.PINK_PLASTIC_BLOCK, AdditionsBlocks.LIME_PLASTIC_BLOCK, AdditionsBlocks.YELLOW_PLASTIC_BLOCK,
              AdditionsBlocks.LIGHT_BLUE_PLASTIC_BLOCK, AdditionsBlocks.MAGENTA_PLASTIC_BLOCK, AdditionsBlocks.ORANGE_PLASTIC_BLOCK, AdditionsBlocks.WHITE_PLASTIC_BLOCK,
              //Slick Plastic Blocks
              AdditionsBlocks.BLACK_SLICK_PLASTIC_BLOCK, AdditionsBlocks.RED_SLICK_PLASTIC_BLOCK, AdditionsBlocks.GREEN_SLICK_PLASTIC_BLOCK,
              AdditionsBlocks.BROWN_SLICK_PLASTIC_BLOCK, AdditionsBlocks.BLUE_SLICK_PLASTIC_BLOCK, AdditionsBlocks.PURPLE_SLICK_PLASTIC_BLOCK, AdditionsBlocks.CYAN_SLICK_PLASTIC_BLOCK,
              AdditionsBlocks.LIGHT_GRAY_SLICK_PLASTIC_BLOCK, AdditionsBlocks.GRAY_SLICK_PLASTIC_BLOCK, AdditionsBlocks.PINK_SLICK_PLASTIC_BLOCK,
              AdditionsBlocks.LIME_SLICK_PLASTIC_BLOCK, AdditionsBlocks.YELLOW_SLICK_PLASTIC_BLOCK, AdditionsBlocks.LIGHT_BLUE_SLICK_PLASTIC_BLOCK,
              AdditionsBlocks.MAGENTA_SLICK_PLASTIC_BLOCK, AdditionsBlocks.ORANGE_SLICK_PLASTIC_BLOCK, AdditionsBlocks.WHITE_SLICK_PLASTIC_BLOCK,
              //Plastic Glow Blocks
              AdditionsBlocks.BLACK_PLASTIC_GLOW_BLOCK, AdditionsBlocks.RED_PLASTIC_GLOW_BLOCK, AdditionsBlocks.GREEN_PLASTIC_GLOW_BLOCK, AdditionsBlocks.BROWN_PLASTIC_GLOW_BLOCK,
              AdditionsBlocks.BLUE_PLASTIC_GLOW_BLOCK, AdditionsBlocks.PURPLE_PLASTIC_GLOW_BLOCK, AdditionsBlocks.CYAN_PLASTIC_GLOW_BLOCK, AdditionsBlocks.LIGHT_GRAY_PLASTIC_GLOW_BLOCK,
              AdditionsBlocks.GRAY_PLASTIC_GLOW_BLOCK, AdditionsBlocks.PINK_PLASTIC_GLOW_BLOCK, AdditionsBlocks.LIME_PLASTIC_GLOW_BLOCK, AdditionsBlocks.YELLOW_PLASTIC_GLOW_BLOCK,
              AdditionsBlocks.LIGHT_BLUE_PLASTIC_GLOW_BLOCK, AdditionsBlocks.MAGENTA_PLASTIC_GLOW_BLOCK, AdditionsBlocks.ORANGE_PLASTIC_GLOW_BLOCK, AdditionsBlocks.WHITE_PLASTIC_GLOW_BLOCK,
              //Reinforced Plastic Blocks
              AdditionsBlocks.BLACK_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.RED_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.GREEN_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlocks.BROWN_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.BLUE_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.PURPLE_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlocks.CYAN_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.LIGHT_GRAY_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.GRAY_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlocks.PINK_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.LIME_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.YELLOW_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlocks.LIGHT_BLUE_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.MAGENTA_REINFORCED_PLASTIC_BLOCK, AdditionsBlocks.ORANGE_REINFORCED_PLASTIC_BLOCK,
              AdditionsBlocks.WHITE_REINFORCED_PLASTIC_BLOCK,
              //Plastic Road
              AdditionsBlocks.BLACK_PLASTIC_ROAD, AdditionsBlocks.RED_PLASTIC_ROAD, AdditionsBlocks.GREEN_PLASTIC_ROAD, AdditionsBlocks.BROWN_PLASTIC_ROAD,
              AdditionsBlocks.BLUE_PLASTIC_ROAD, AdditionsBlocks.PURPLE_PLASTIC_ROAD, AdditionsBlocks.CYAN_PLASTIC_ROAD, AdditionsBlocks.LIGHT_GRAY_PLASTIC_ROAD,
              AdditionsBlocks.GRAY_PLASTIC_ROAD, AdditionsBlocks.PINK_PLASTIC_ROAD, AdditionsBlocks.LIME_PLASTIC_ROAD, AdditionsBlocks.YELLOW_PLASTIC_ROAD,
              AdditionsBlocks.LIGHT_BLUE_PLASTIC_ROAD, AdditionsBlocks.MAGENTA_PLASTIC_ROAD, AdditionsBlocks.ORANGE_PLASTIC_ROAD, AdditionsBlocks.WHITE_PLASTIC_ROAD,
              //Plastic Fences
              AdditionsBlocks.BLACK_PLASTIC_FENCE, AdditionsBlocks.RED_PLASTIC_FENCE, AdditionsBlocks.GREEN_PLASTIC_FENCE, AdditionsBlocks.BROWN_PLASTIC_FENCE,
              AdditionsBlocks.BLUE_PLASTIC_FENCE, AdditionsBlocks.PURPLE_PLASTIC_FENCE, AdditionsBlocks.CYAN_PLASTIC_FENCE, AdditionsBlocks.LIGHT_GRAY_PLASTIC_FENCE,
              AdditionsBlocks.GRAY_PLASTIC_FENCE, AdditionsBlocks.PINK_PLASTIC_FENCE, AdditionsBlocks.LIME_PLASTIC_FENCE, AdditionsBlocks.YELLOW_PLASTIC_FENCE,
              AdditionsBlocks.LIGHT_BLUE_PLASTIC_FENCE, AdditionsBlocks.MAGENTA_PLASTIC_FENCE, AdditionsBlocks.ORANGE_PLASTIC_FENCE, AdditionsBlocks.WHITE_PLASTIC_FENCE,
              //Plastic Fence Gates
              AdditionsBlocks.BLACK_PLASTIC_FENCE_GATE, AdditionsBlocks.RED_PLASTIC_FENCE_GATE, AdditionsBlocks.GREEN_PLASTIC_FENCE_GATE, AdditionsBlocks.BROWN_PLASTIC_FENCE_GATE,
              AdditionsBlocks.BLUE_PLASTIC_FENCE_GATE, AdditionsBlocks.PURPLE_PLASTIC_FENCE_GATE, AdditionsBlocks.CYAN_PLASTIC_FENCE_GATE,
              AdditionsBlocks.LIGHT_GRAY_PLASTIC_FENCE_GATE, AdditionsBlocks.GRAY_PLASTIC_FENCE_GATE, AdditionsBlocks.PINK_PLASTIC_FENCE_GATE,
              AdditionsBlocks.LIME_PLASTIC_FENCE_GATE, AdditionsBlocks.YELLOW_PLASTIC_FENCE_GATE, AdditionsBlocks.LIGHT_BLUE_PLASTIC_FENCE_GATE,
              AdditionsBlocks.MAGENTA_PLASTIC_FENCE_GATE, AdditionsBlocks.ORANGE_PLASTIC_FENCE_GATE, AdditionsBlocks.WHITE_PLASTIC_FENCE_GATE,
              //Plastic Slabs
              AdditionsBlocks.BLACK_PLASTIC_SLAB, AdditionsBlocks.RED_PLASTIC_SLAB, AdditionsBlocks.GREEN_PLASTIC_SLAB, AdditionsBlocks.BROWN_PLASTIC_SLAB,
              AdditionsBlocks.BLUE_PLASTIC_SLAB, AdditionsBlocks.PURPLE_PLASTIC_SLAB, AdditionsBlocks.CYAN_PLASTIC_SLAB, AdditionsBlocks.LIGHT_GRAY_PLASTIC_SLAB,
              AdditionsBlocks.GRAY_PLASTIC_SLAB, AdditionsBlocks.PINK_PLASTIC_SLAB, AdditionsBlocks.LIME_PLASTIC_SLAB, AdditionsBlocks.YELLOW_PLASTIC_SLAB,
              AdditionsBlocks.LIGHT_BLUE_PLASTIC_SLAB, AdditionsBlocks.MAGENTA_PLASTIC_SLAB, AdditionsBlocks.ORANGE_PLASTIC_SLAB, AdditionsBlocks.WHITE_PLASTIC_SLAB,
              //Plastic Fence Gates
              AdditionsBlocks.BLACK_PLASTIC_STAIRS, AdditionsBlocks.RED_PLASTIC_STAIRS, AdditionsBlocks.GREEN_PLASTIC_STAIRS, AdditionsBlocks.BROWN_PLASTIC_STAIRS,
              AdditionsBlocks.BLUE_PLASTIC_STAIRS, AdditionsBlocks.PURPLE_PLASTIC_STAIRS, AdditionsBlocks.CYAN_PLASTIC_STAIRS, AdditionsBlocks.LIGHT_GRAY_PLASTIC_STAIRS,
              AdditionsBlocks.GRAY_PLASTIC_STAIRS, AdditionsBlocks.PINK_PLASTIC_STAIRS, AdditionsBlocks.LIME_PLASTIC_STAIRS, AdditionsBlocks.YELLOW_PLASTIC_STAIRS,
              AdditionsBlocks.LIGHT_BLUE_PLASTIC_STAIRS, AdditionsBlocks.MAGENTA_PLASTIC_STAIRS, AdditionsBlocks.ORANGE_PLASTIC_STAIRS, AdditionsBlocks.WHITE_PLASTIC_STAIRS,
              //Glow Panels
              AdditionsBlocks.BLACK_GLOW_PANEL, AdditionsBlocks.RED_GLOW_PANEL, AdditionsBlocks.GREEN_GLOW_PANEL, AdditionsBlocks.BROWN_GLOW_PANEL,
              AdditionsBlocks.BLUE_GLOW_PANEL, AdditionsBlocks.PURPLE_GLOW_PANEL, AdditionsBlocks.CYAN_GLOW_PANEL, AdditionsBlocks.LIGHT_GRAY_GLOW_PANEL,
              AdditionsBlocks.GRAY_GLOW_PANEL, AdditionsBlocks.PINK_GLOW_PANEL, AdditionsBlocks.LIME_GLOW_PANEL, AdditionsBlocks.YELLOW_GLOW_PANEL,
              AdditionsBlocks.LIGHT_BLUE_GLOW_PANEL, AdditionsBlocks.MAGENTA_GLOW_PANEL, AdditionsBlocks.ORANGE_GLOW_PANEL, AdditionsBlocks.WHITE_GLOW_PANEL);

        registerItemColorHandler(event.getItemColors(), (stack, tintIndex) -> {
                  Item item = stack.getItem();
                  if (item instanceof ItemBalloon) {
                      ItemBalloon balloon = (ItemBalloon) item;
                      return MekanismRenderer.getColorARGB(balloon.getColor(), 1);
                  }
                  return -1;
              }, AdditionsItems.BLACK_BALLOON, AdditionsItems.RED_BALLOON, AdditionsItems.GREEN_BALLOON, AdditionsItems.BROWN_BALLOON, AdditionsItems.BLUE_BALLOON,
              AdditionsItems.PURPLE_BALLOON, AdditionsItems.CYAN_BALLOON, AdditionsItems.LIGHT_GRAY_BALLOON, AdditionsItems.GRAY_BALLOON, AdditionsItems.PINK_BALLOON,
              AdditionsItems.LIME_BALLOON, AdditionsItems.YELLOW_BALLOON, AdditionsItems.LIGHT_BLUE_BALLOON, AdditionsItems.MAGENTA_BALLOON, AdditionsItems.ORANGE_BALLOON,
              AdditionsItems.WHITE_BALLOON);
    }
}