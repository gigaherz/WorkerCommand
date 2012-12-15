package gigaherz.workercommand;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class CommandCircuit extends Item {
	
	private final static String[] subNames = {
		"unprogrammed", 
		
		// Tier 1
		"planter", "harvester", "woodcutter",
		
		// Tier 2
		"fertilizer", "tiller",
		
		// Tier 3
		"miner", "filler",
	};

    public CommandCircuit(int id)
    {
        super(id);
        
        // Constructor Configuration
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.tabMisc);
        setIconIndex(0);
        setItemName("commandCircuit");
        setHasSubtypes(true);
    }
    
    @Override
    public int getMetadata (int damageValue) {
    	return damageValue;
    }
    
    public String getTextureFile()
    {
        return CommonProxy.ITEMS_PNG;
    }
    
    @Override
    public String getItemNameIS(ItemStack stack) {
    	int sub = stack.getItemDamage();
    	
    	if(sub >= subNames.length)
    		sub = 0;
    	
    	return getItemName() + "." + subNames[sub];
    }
    
    public ItemStack getStack(int count, int damageValue)
    {
    	ItemStack stack = new ItemStack(this, count);
    	stack.setItemDamage(damageValue);
    	return stack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(int unknown, CreativeTabs tab, List subItems) {
    	for (int meta = 1; meta < subNames.length; meta++) {
    		subItems.add(new ItemStack(this, 1, meta));
    	}
    }
    
    final ItemStack[] planterStacks = new ItemStack[]{
			new ItemStack(Item.seeds, 1),
			new ItemStack(Item.melonSeeds, 1), 
			new ItemStack(Item.pumpkinSeeds, 1), 
			new ItemStack(Item.netherStalkSeeds, 1),
			new ItemStack(Block.cactus, 1), 
			new ItemStack(Item.reed, 1),
    		new ItemStack(Block.sapling, 1, -1)};

    final ItemStack[] harvesterStacks = new ItemStack[]{
			new ItemStack(Item.seeds, 1),
			new ItemStack(Item.melonSeeds, 1), 
			new ItemStack(Item.pumpkinSeeds, 1), 
			new ItemStack(Item.netherStalkSeeds, 1),
			new ItemStack(Item.reed, 1)};

    final ItemStack[] woodcutterStacks = new ItemStack[]{
    		new ItemStack(Block.cactus, 1), 
    		new ItemStack(Block.wood, 1, -1)};
    
    final ItemStack[] woodcutterToolStacks = new ItemStack[]{
			new ItemStack(Item.axeWood, 1),
			new ItemStack(Item.axeStone, 1),
			new ItemStack(Item.axeSteel, 1),
			new ItemStack(Item.axeGold, 1),
			new ItemStack(Item.axeDiamond, 1)};
    
	public boolean canDoWork(WorkerTile workerTile, int damage) 
	{
		switch(damage)
		{
		case 1: // planter
			return hasOneOf(workerTile, planterStacks);
		case 2: // harvester
			return hasSlotWithSpaceFor(workerTile, harvesterStacks);
		case 3: // woodcutter
			return hasSlotWithSpaceFor(workerTile, woodcutterStacks)
	        			&& hasToolOf(workerTile, woodcutterToolStacks);		
		case 4: // fertilizer	
			// TODO: implement
			break;
		case 5: // tiller	
			// TODO: implement
			break;
		case 6: // miner	
			// TODO: implement
			break;
		case 7: // filler	
			return workerTile.hasAnyBlockInInputArea();
		}
		return false;
	}

	private boolean hasToolOf(WorkerTile workerTile, ItemStack... stacks) 
	{
		for(int i=0; i<stacks.length; i++)
		{
			if(workerTile.hasToolInToolArea(stacks[i]))
				return true;
		}
		return false;
	}

	private boolean hasSlotWithSpaceFor(WorkerTile workerTile,
			ItemStack... stacks) {
		for(int i=0; i<stacks.length; i++)
		{
			if(workerTile.hasSpaceInOutputAreaForItem(stacks[i]))
				return true;
		}
		return false;
	}

	private boolean hasOneOf(WorkerTile workerTile, ItemStack... stacks) {
		for(int i=0; i<stacks.length; i++)
		{
			if(workerTile.hasItemInInputArea(stacks[i]))
				return true;
		}
		return false;
	}

	public boolean doWork(WorkerTile workerTile, int damage) {
		switch(damage)
		{
		case 1: // planter
			return runPlanter(workerTile);
		case 2: // harvester
			// TODO: implement
			break;//return true;
		case 3: // woodcutter			
			// TODO: implement
			break;//return true;			
		case 4: // fertilizer	
			// TODO: implement
			break;
		case 5: // tiller	
			// TODO: implement
			break;
		case 6: // miner	
			// TODO: implement
			break;
		case 7: // filler
			return runFiller(workerTile);
		}
		return false;
	}

	private boolean runPlanter(WorkerTile workerTile) 
	{
		for(int i=0;i<9; i++)
		{
			ItemStack stack = workerTile.getStackInSlot(i);
			if(stack == null)
				continue;

			int dmg = stack.getItemDamage();
			
			Item item = null;
			for(ItemStack st : planterStacks)
			{
				if(stack.itemID == st.itemID && (st.getItemDamage() < 0 || (st.getItemDamage() == dmg)))
				{
					item = st.getItem();
				}
			}
			
			if(item == null)
				continue;
			
			int topY = workerTile.getTopY();
			
			if(topY < 0)
				continue;
			
			EntityPlayer fakePlayer = new FakePlayer(workerTile.worldObj);

		    int cx = workerTile.xCoord + workerTile.currentX;
		    int cy = workerTile.yCoord + topY;
		    int cz = workerTile.zCoord + workerTile.currentZ;
		    
	    
	    	int tx=cx, ty=cy-1, tz=cz;
	    			
			if(item.onItemUse(stack, fakePlayer, workerTile.worldObj, tx, ty, tz, 1, 0, 0, 0))
			{
				System.out.println("Worked!");
				return true;
			}
		}
		return false;
	}
	
	private boolean runFiller(WorkerTile workerTile)
	{
		for(int i=0;i<9; i++)
		{
			ItemStack stack = workerTile.getStackInSlot(i);
			if(stack == null)
				continue;

			Item item = stack.getItem();
			
			if(!(item instanceof ItemBlock))
				continue;
			
			int topY = workerTile.getTopY();
			
			if(topY < 0)
				continue;
			
			EntityPlayer fakePlayer = new FakePlayer(workerTile.worldObj);

		    ItemBlock block = (ItemBlock)item;
		    
		    int cx = workerTile.xCoord + workerTile.currentX;
		    int cy = workerTile.yCoord + topY;
		    int cz = workerTile.zCoord + workerTile.currentZ;
		    for(int s=0;s<6;s++)
		    {
		    	int tx=cx, ty=cy, tz=cz;
		    	switch(s)
		    	{
		    	case 0: ty++; break;
		    	case 1: ty--; break;
		    	case 2: tz++; break;
		    	case 3: tz--; break;
		    	case 4: tx++; break;
		    	case 5: tx--; break;
		    	}
		    				
				if(block.onItemUse(stack, fakePlayer, workerTile.worldObj, tx, ty, tz, s, 0, 0, 0))
				{
					System.out.println("Worked!");
					return true;
				}
		    }
		}
		return false;
	}
	
}
