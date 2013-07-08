package mekanism.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransmitterRenderer implements ISimpleBlockRenderingHandler
{
	public ModelTransmitter transmitter = new ModelTransmitter();
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) 
	{
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(180F, 0.0F, -1.0F, 0.0F);
    	GL11.glTranslated(0.0F, -1.0F, 0.0F);
    	
    	switch(metadata)
    	{
    		case 0:
    			GL11.glBindTexture(3553, FMLClientHandler.instance().getClient().renderEngine.getTexture("/mods/mekanism/render/PressurizedTube.png"));
    			break;
    		case 1:
    			GL11.glBindTexture(3553, FMLClientHandler.instance().getClient().renderEngine.getTexture("/mods/mekanism/render/UniversalCable.png"));
    			break;
    		case 2:
    			GL11.glBindTexture(3553, FMLClientHandler.instance().getClient().renderEngine.getTexture("/mods/mekanism/render/MechanicalPipe.png"));
    			break;
    	}
    	
    	transmitter.UpOn.render(0.0625F);
    	transmitter.DownOn.render(0.0625F);
    	transmitter.BackOff.render(0.0625F);
    	transmitter.FrontOff.render(0.0625F);
    	transmitter.LeftOff.render(0.0625F);
    	transmitter.RightOff.render(0.0625F);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory() 
	{
		return true;
	}

	@Override
	public int getRenderId() 
	{
		return ClientProxy.TRANSMITTER_RENDER_ID;
	}
}
