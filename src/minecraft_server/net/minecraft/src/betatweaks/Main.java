package net.minecraft.src.betatweaks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraft.src.betatweaks.config.Config;
import net.minecraft.src.betatweaks.config.SBoolean;

public class Main {

	private static List<Integer> options = new ArrayList<Integer>();
	private int playerCount;
	private static Config cfg = Config.INSTANCE;
	
	private final int PACKET_OUT_PLAYERLIST = 2;
	private final int PACKET_OUT_IS_OP = 1;
	
	private final SBoolean[] boolOptions = {
			cfg.punchSheepForWool, cfg.ladderGaps, cfg.lightTNTwithFist, cfg.hoeGrassForSeeds, cfg.minecartBoosters, cfg.boatElevators,
			cfg.allowPlayerList
	};
	
	public static final Main INSTANCE = new Main();
	private BaseModMp basemod;
	
	public void init(mod_BetaTweaks basemod) {
		this.basemod = basemod;
    	
    	loadOptions();
        initSettings();
	}
	
	public String version() {
		return "v1.28 PRE";
	}
	
	public String getModID() {
		return "BetaTweaksMP " + this.version();
	}
	
	public void loadOptions() {
		options.clear();
		for(SBoolean setting : boolOptions) {
			options.add(setting.isEnabled() ? 1 : 0);
		}
	}
	
	public void initSettings() {
		if (cfg.lightTNTwithFist.isEnabled() && Block.tnt.getClass() == BlockTNT.class) {
			Utils.replaceBlock(new BlockTNTPunchable(), "tnt", "an");
    	}
	}
	
	public void handleLogin(EntityPlayerMP entityplayermp)
    {
		this.playerCount++;
		
		int[] dataInt = new int[1];
		dataInt[0] = Utils.mc.configManager.isOp(entityplayermp.username) ? 1 : 0;
		
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 1;
		packet.dataInt = dataInt;
		ModLoaderMp.SendPacketTo(basemod, entityplayermp, packet);
		
		
		dataInt = new int[options.size() + 1];
		for (int i = 0; i < options.size(); i++) {
			dataInt[i] = options.get(i);
		}
		dataInt[options.size()] = Utils.mc.propertyManagerObj.getIntProperty("max-players", 20);
		
		String[] dataString = new String[1];
		dataString[0] = cfg.serverMOTD.getValue();
		
		packet = new Packet230ModLoader();
		packet.packetType = 0;
		packet.dataInt = dataInt;
		packet.dataString = dataString;
		ModLoaderMp.SendPacketTo(basemod, entityplayermp, packet);
    }
	
	
	public void handlePacket(Packet230ModLoader packet, EntityPlayerMP player) {
		// Incoming packet IDs
		final int CHANGE_OPTIONS = 0;
		final int REMOVE_INVISIBLE_BLOCK = 1;
		final int PUNCH_SHEEP = 2;
		final int HOE_GRASS = 3;
		final int CHECK_IF_OP = 4;

		switch (packet.packetType) {
		case CHANGE_OPTIONS: {
			if (Utils.mc.configManager.isOp(player.username)) {
				for (int i = 0; i < packet.dataInt.length; i++) {
					boolOptions[i].setValue(packet.dataInt[i] == 1);
				}

				cfg.serverMOTD.setValue(packet.dataString[0]);

				loadOptions();
				initSettings();
				ModLoaderMp.sendChatToOps("Beta Tweaks", player.username + " has updated settings");
				int[] dataInt = new int[options.size() + 1];
				for (int i = 0; i < options.size(); i++) {
					dataInt[i] = options.get(i);
				}

				String[] dataString = new String[1];
				dataString[0] = cfg.serverMOTD.getValue();

				packet = new Packet230ModLoader();
				packet.packetType = 0;
				packet.dataInt = dataInt;
				packet.dataString = dataString;
				dataInt[options.size()] = Utils.mc.propertyManagerObj.getIntProperty("max-players", 20);

				cfg.writeConfig();
				ModLoaderMp.SendPacketToAll(basemod, packet);
			}
			break;
		}

		case REMOVE_INVISIBLE_BLOCK: {
			ChunkCoordinates coords = new ChunkCoordinates(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2]);
			World world = ModLoaderMp.GetPlayerWorld(player);
			Block block = Block.blocksList[world.getBlockId(coords.posX, coords.posY, coords.posZ)];
			if (block == Block.tallGrass || block == Block.deadBush) {
				world.setBlock(coords.posX, coords.posY, coords.posZ, 0);
			}
			break;
		}

		case PUNCH_SHEEP: {
			if (cfg.punchSheepForWool.isEnabled()) {
				Entity entity = Utils.getEntityFromID(packet.dataInt[0], ModLoaderMp.GetPlayerWorld(player));
				if(entity instanceof EntitySheep) {
					EntitySheep sheep = (EntitySheep) entity;
					if(!sheep.func_21069_f_() /* If sheep is not sheared */ ) {
						sheep.setSheared(true);

						Random rand = new Random();
						int droppedWoolCount = 2 + rand.nextInt(3);
						for (int i = 0; i < droppedWoolCount; i++) {
							EntityItem wool = sheep.entityDropItem(new ItemStack(Block.cloth, 1, sheep.getFleeceColor()), 1.0F);
							wool.motionY += rand.nextFloat() * 0.05F;
							wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
							wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						}
					}
				}
			}
			break;
		}

		case HOE_GRASS: {
			if (cfg.hoeGrassForSeeds.isEnabled()) {
				ChunkCoordinates coords = new ChunkCoordinates(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2]);
				World world = ModLoaderMp.GetPlayerWorld(player);
				Block block = Block.blocksList[world.getBlockId(coords.posX, coords.posY, coords.posZ)];
				if (block == Block.tilledField) {
					Random rand = new Random();

					if (rand.nextInt(8) == 0) {
						float f = 0.7F;
						float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
						float f2 = 1.2F;
						float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
						EntityItem seeds = new EntityItem(world, (double) coords.posX + f1, (double) coords.posY + f2,
								(double) coords.posZ + f3, new ItemStack(Item.seeds));
						seeds.delayBeforeCanPickup = 10;
						world.entityJoinedWorld(seeds);
					}
				}
			}
			break;
		}
		
		case CHECK_IF_OP: {
			boolean isOp = Utils.mc.configManager.isOp(player.username);
			Packet230ModLoader returnPacket = new Packet230ModLoader();
			returnPacket.packetType = PACKET_OUT_IS_OP;
			returnPacket.dataInt = new int[] { isOp ? 1 : 0 };
			ModLoaderMp.SendPacketTo(basemod, player, returnPacket);
			break;
		}
		}
	}

	private boolean boo = true;
	
	public void onTickInGame(MinecraftServer mc) {
		if(boo) {
			boo = false;
			//JsonServer.main();
		}
		int currentPlayerCount = mc.configManager.playerEntities.size();
		if (cfg.allowPlayerList.isEnabled() && this.playerCount != currentPlayerCount) {
			this.playerCount = currentPlayerCount;
			Packet230ModLoader packet = new Packet230ModLoader();
			packet.packetType = PACKET_OUT_PLAYERLIST;
			
			@SuppressWarnings("unchecked")
			List<EntityPlayer> playerList = mc.configManager.playerEntities;
			
			packet.dataString = new String[playerList.size()];
			for (int i = 0; i < playerList.size(); i++) {
				packet.dataString[i] = playerList.get(i).username;
			}
			ModLoaderMp.SendPacketToAll(basemod, packet);
		}

		if (cfg.minecartBoosters.isEnabled()) {
			for (int q = 0; q < mc.worldMngr.length; q++) {
				for(int i = 0; i < mc.worldMngr[q].loadedEntityList.size(); i++)
			{
				Entity entity = (Entity)mc.worldMngr[q].loadedEntityList.get(i);
				
				
				
				
				if (entity instanceof EntityMinecart) {
				@SuppressWarnings("rawtypes")
				List list = mc.worldMngr[q].getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
				if(list != null && list.size() > 0)
				{
					for(int j1 = 0; j1 < list.size(); j1++)
					{
						Entity entity2 = (Entity)list.get(j1);
						if(entity2 != entity2.riddenByEntity && entity2.canBePushed() && (entity2 instanceof EntityMinecart))
						{
							double d = entity2.posX - entity.posX;
							double d1 = entity2.posZ - entity.posZ;
							double d2 = d * d + d1 * d1;
							if(d2 >= 9.9999997473787516E-005D)
							{
								d2 = MathHelper.sqrt_double(d2);
								d /= d2;
								d1 /= d2;
								double d3 = 1.0D / d2;
								if(d3 > 1.0D)
								{
									d3 = 1.0D;
								}
								d *= d3;
								d1 *= d3;
								d *= 0.10000000149011612D;
								d1 *= 0.10000000149011612D;
								d *= 1.0F - entity.entityCollisionReduction;
								d1 *= 1.0F - entity.entityCollisionReduction;
								d *= 0.5D;
								d1 *= 0.5D;
								entity.motionX *= 0.20000000298023224D;
								entity.motionZ *= 0.20000000298023224D;
								entity.addVelocity(entity2.motionX - d, 0.0D, entity2.motionZ - d1);
								entity2.motionX *= 0.69999998807907104D;
								entity2.motionZ *= 0.69999998807907104D;
							}
						}
					}
				}
				}
			}
			}
		}
		
		
		if (cfg.boatElevators.isEnabled()) {
			for (int k = 0; k < mc.worldMngr.length; k++) {
			for(int i = 0; i < mc.worldMngr[k].loadedEntityList.size(); i++) {
				Entity entity = (Entity)mc.worldMngr[k].loadedEntityList.get(i);
				if (entity instanceof EntityBoat) {
					
				int q = 5;
		        double d = 0.0D;
		        for(int j = 0; j < q; j++)
		        {
		            double d5 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double)(j + 0)) / (double)i) - 0.125D;
		            double d9 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double)(j + 1)) / (double)i) - 0.125D;
		            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBoxFromPool(entity.boundingBox.minX, d5, entity.boundingBox.minZ, entity.boundingBox.maxX, d9, entity.boundingBox.maxZ);
		            if(mc.worldMngr[k].isAABBInMaterial(axisalignedbb, Material.water))
		            {
		                d += 1.0D / (double)q;
		            }
		        }
		        
					if(d >= 1.0D)
					{
						double d3 = d * 2D - 1.0D;
						entity.moveEntity(0, (0.039999999105930328D * d3)/2.5D - 0.0070000002160668373D, 0);
					}
				}
			}
			}
		}
		
		if (cfg.ladderGaps.isEnabled()) {
			
			for (int q = 0; q < mc.worldMngr.length; q++) {
				for(int z = 0; z < mc.worldMngr[q].loadedEntityList.size() + mc.worldMngr[q].playerEntities.size(); z++) {
					Entity entity;
					if (z < mc.worldMngr[q].loadedEntityList.size()) {
						entity = (Entity)mc.worldMngr[q].loadedEntityList.get(z);
					}
					else {
						entity = (Entity)mc.worldMngr[q].playerEntities.get(z - mc.worldMngr[q].loadedEntityList.size());
					}
					if (entity instanceof EntityLiving) {
						int i = MathHelper.floor_double(entity.posX);
						int j = MathHelper.floor_double(entity.boundingBox.minY);
						int k = MathHelper.floor_double(entity.posZ);
						int l = mc.worldMngr[q].getBlockId(i, j + 1, k);
						if (!((EntityLiving)entity).isOnLadder() && l == Block.ladder.blockID) {
							float f4 = 0.15F;
							if (entity.motionX < (double) (-f4)) {
								entity.motionX = (double) -f4;
							}
							if (entity.motionX > (double) f4) {
								entity.motionX = (double) f4;
							}
							if (entity.motionX < (double) (-f4)) {
								entity.motionX = (double) -f4;
							}
							if (entity.motionX > (double) f4) {
								entity.motionX = (double) f4;
							}
							mod_BetaTweaks.setFallDistance(entity, 0.0F);
							if (entity.motionY < -0.14999999999999999D) {
								entity.motionY = -0.14999999999999999D;
							}
							if (entity.isSneaking() && entity.motionY < 0.0D) {
								entity.motionY = 0.0D;
							}
							if (!((EntityLiving)entity).isOnLadder() && entity.isCollidedHorizontally) {
								entity.motionY = 0.20000000000000001D;
								entity.motionY -= 0.080000000000000002D;
								entity.motionY *= 0.98000001907348633D;
							}
			}
		}
	}
				
				
			}
			}
	}
	
}
