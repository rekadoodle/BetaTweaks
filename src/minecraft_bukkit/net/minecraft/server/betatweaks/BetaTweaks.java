package net.minecraft.server.betatweaks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.server.*;
import net.minecraft.server.betatweaks.BlockTNTPunchable;
import net.minecraft.server.betatweaks.config.Config;
import net.minecraft.server.betatweaks.config.SBoolean;

public class BetaTweaks {

	private static List<Integer> options = new ArrayList<Integer>();
	private int playerCount;
	private static Config cfg = Config.INSTANCE;
	
	private final int PACKET_OUT_PLAYERLIST = 2;
	private final int PACKET_OUT_IS_OP = 1;
	
	private final SBoolean[] boolOptions = {
			cfg.punchSheepForWool, cfg.ladderGaps, cfg.lightTNTwithFist, cfg.hoeGrassForSeeds, cfg.minecartBoosters, cfg.boatElevators,
			cfg.allowPlayerList
	};
	
	public static final BetaTweaks INSTANCE = new BetaTweaks();
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
		if (cfg.lightTNTwithFist.isEnabled() && Block.TNT.getClass() == BlockTNT.class) {
			Utils.replaceBlock(new BlockTNTPunchable(), "tnt", "an");
    	}
	}
	
	public void handleLogin(EntityPlayer entityplayermp)
    {
		this.playerCount++;
		
		int[] dataInt = new int[1];
		dataInt[0] = Utils.mc.serverConfigurationManager.isOp(entityplayermp.name) ? 1 : 0;
		
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 1;
		packet.dataInt = dataInt;
		ModLoaderMp.SendPacketTo(basemod, entityplayermp, packet);
		
		
		dataInt = new int[options.size() + 1];
		for (int i = 0; i < options.size(); i++) {
			dataInt[i] = options.get(i);
		}
		dataInt[options.size()] = Utils.mc.propertyManager.getInt("max-players", 20);
		
		String[] dataString = new String[1];
		dataString[0] = cfg.serverMOTD.getValue();
		
		packet = new Packet230ModLoader();
		packet.packetType = 0;
		packet.dataInt = dataInt;
		packet.dataString = dataString;
		ModLoaderMp.SendPacketTo(basemod, entityplayermp, packet);
    }
	
	
	public void handlePacket(Packet230ModLoader packet, EntityPlayer player) {
		// Incoming packet IDs
		final int CHANGE_OPTIONS = 0;
		final int REMOVE_INVISIBLE_BLOCK = 1;
		final int PUNCH_SHEEP = 2;
		final int HOE_GRASS = 3;
		final int CHECK_IF_OP = 4;

		switch (packet.packetType) {
		case CHANGE_OPTIONS: {
			if (Utils.mc.serverConfigurationManager.isOp(player.name)) {
				for (int i = 0; i < packet.dataInt.length; i++) {
					boolOptions[i].setValue(packet.dataInt[i] == 1);
				}

				cfg.serverMOTD.setValue(packet.dataString[0]);

				loadOptions();
				initSettings();
				ModLoaderMp.sendChatToOps("Beta Tweaks", player.name + " has updated settings");
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
				dataInt[options.size()] = Utils.mc.propertyManager.getInt("max-players", 20);

				cfg.writeConfig();
				ModLoaderMp.SendPacketToAll(basemod, packet);
			}
			break;
		}

		case REMOVE_INVISIBLE_BLOCK: {
			ChunkCoordinates coords = new ChunkCoordinates(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2]);
			World world = ModLoaderMp.GetPlayerWorld(player);
			Block block = Block.byId[world.getTypeId(coords.x, coords.y, coords.z)];
			if (block == Block.LONG_GRASS || block == Block.DEAD_BUSH) {
				world.setRawTypeId(coords.x, coords.y, coords.z, 0);
			}
			break;
		}

		case PUNCH_SHEEP: {
			if (cfg.punchSheepForWool.isEnabled()) {
				Entity entity = Utils.getEntityFromID(packet.dataInt[0], ModLoaderMp.GetPlayerWorld(player));
				if(entity instanceof EntitySheep) {
					EntitySheep sheep = (EntitySheep) entity;
					if(!sheep.isSheared()) {
						sheep.setSheared(true);

						Random rand = new Random();
						int droppedWoolCount = 2 + rand.nextInt(3);
						for (int i = 0; i < droppedWoolCount; i++) {
							EntityItem wool = sheep.a(new ItemStack(Block.WOOL, 1, sheep.getColor()), 1.0F);
							wool.motY += rand.nextFloat() * 0.05F;
							wool.motX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
							wool.motZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
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
				Block block = Block.byId[world.getTypeId(coords.x, coords.y, coords.z)];
				if (block == Block.SOIL) {
					Random rand = new Random();

					if (rand.nextInt(8) == 0) {
						float f = 0.7F;
						float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
						float f2 = 1.2F;
						float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
						EntityItem seeds = new EntityItem(world, (double) coords.x + f1, (double) coords.y + f2,
								(double) coords.z + f3, new ItemStack(Item.SEEDS));
						seeds.pickupDelay = 10;
						world.addEntity(seeds);
					}
				}
			}
			break;
		}
		
		case CHECK_IF_OP: {
			boolean isOp = Utils.mc.serverConfigurationManager.isOp(player.name);
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
		int currentPlayerCount = mc.serverConfigurationManager.players.size();
		if (cfg.allowPlayerList.isEnabled() && this.playerCount != currentPlayerCount) {
			this.playerCount = currentPlayerCount;
			Packet230ModLoader packet = new Packet230ModLoader();
			packet.packetType = PACKET_OUT_PLAYERLIST;
			
			@SuppressWarnings("unchecked")
			List<EntityPlayer> playerList = mc.serverConfigurationManager.players;
			
			packet.dataString = new String[playerList.size()];
			for (int i = 0; i < playerList.size(); i++) {
				packet.dataString[i] = playerList.get(i).name;
			}
			ModLoaderMp.SendPacketToAll(basemod, packet);
		}

		if (cfg.minecartBoosters.isEnabled()) {
			for (int q = 0; q < mc.worlds.size(); q++) {
				for(int i = 0; i < mc.worlds.get(q).entityList.size(); i++)
			{
				Entity entity = (Entity)mc.worlds.get(q).entityList.get(i);
				
				
				
				
				if (entity instanceof EntityMinecart) {
				@SuppressWarnings("rawtypes")
				List list = mc.worlds.get(q).b(entity, entity.boundingBox.b(0.20000000298023224D, 0.0D, 0.20000000298023224D));
				if(list != null && list.size() > 0)
				{
					for(int j1 = 0; j1 < list.size(); j1++)
					{
						Entity entity2 = (Entity)list.get(j1);
						if(entity2 != entity2.passenger && entity2.d_() && (entity2 instanceof EntityMinecart))
						{
							double d = entity2.locX - entity.locX;
							double d1 = entity2.locZ - entity.locZ;
							double d2 = d * d + d1 * d1;
							if(d2 >= 9.9999997473787516E-005D)
							{
								d2 = MathHelper.a(d2);
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
								d *= 1.0F - entity.bu;
								d1 *= 1.0F - entity.bu;
								d *= 0.5D;
								d1 *= 0.5D;
								entity.motX *= 0.20000000298023224D;
								entity.motZ *= 0.20000000298023224D;
								entity.b(entity2.motX - d, 0.0D, entity2.motZ - d1);
								entity2.motX *= 0.69999998807907104D;
								entity2.motZ *= 0.69999998807907104D;
							}
						}
					}
				}
				}
			}
			}
		}
		
		
		if (cfg.boatElevators.isEnabled()) {
			for (int k = 0; k < mc.worlds.size(); k++) {
				for(int i = 0; i < mc.worlds.get(k).entityList.size(); i++) {
					Entity entity = (Entity)mc.worlds.get(k).entityList.get(i);
					if (entity instanceof EntityBoat) {
						
					int q = 5;
			        double d = 0.0D;
			        for(int j = 0; j < q; j++)
			        {
			            double d5 = (entity.boundingBox.b + ((entity.boundingBox.e - entity.boundingBox.b) * (double)(j + 0)) / (double)i) - 0.125D;
			            double d9 = (entity.boundingBox.b + ((entity.boundingBox.e - entity.boundingBox.b) * (double)(j + 1)) / (double)i) - 0.125D;
			            AxisAlignedBB axisalignedbb = AxisAlignedBB.b(entity.boundingBox.a, d5, entity.boundingBox.c, entity.boundingBox.d, d9, entity.boundingBox.f);
			            if(mc.worlds.get(k).b(axisalignedbb, Material.WATER))
			            {
			                d += 1.0D / (double)q;
			            }
			        }
			        
						if(d >= 1.0D)
						{
							double d3 = d * 2D - 1.0D;
							entity.move(0, (0.039999999105930328D * d3)/2.5D - 0.0070000002160668373D, 0);
						}
					}
				}
				}
			}
		
		if (cfg.ladderGaps.isEnabled()) {

			for (int q = 0; q < mc.worlds.size(); q++) {
				for (int z = 0; z < mc.worlds.get(q).entityList.size() + mc.worlds.get(q).players.size(); z++) {
					Entity entity;
					if (z < mc.worlds.get(q).entityList.size()) {
						entity = (Entity) mc.worlds.get(q).entityList.get(z);
					} else {
						entity = (Entity) mc.worlds.get(q).players.get(z - mc.worlds.get(q).entityList.size());
					}
					if (entity instanceof EntityLiving) {
						int i = MathHelper.floor(entity.locX);
						int j = MathHelper.floor(entity.boundingBox.b);
						int k = MathHelper.floor(entity.locZ);
						int l = mc.worlds.get(q).getTypeId(i, j + 1, k);
						if (!((EntityLiving) entity).p() && l == Block.LADDER.id) {
							float f4 = 0.15F;
							if (entity.motX < (double) (-f4)) {
								entity.motX = (double) -f4;
							}
							if (entity.motX > (double) f4) {
								entity.motX = (double) f4;
							}
							if (entity.motX < (double) (-f4)) {
								entity.motX = (double) -f4;
							}
							if (entity.motX > (double) f4) {
								entity.motX = (double) f4;
							}
							entity.fallDistance = 0.0F;
							if (entity.motY < -0.14999999999999999D) {
								entity.motY = -0.14999999999999999D;
							}
							if (entity.isSneaking() && entity.motY < 0.0D) {
								entity.motY = 0.0D;
							}
							if (!((EntityLiving) entity).p() && entity.positionChanged) {
								entity.motY = 0.20000000000000001D;
								entity.motY -= 0.080000000000000002D;
								entity.motY *= 0.98000001907348633D;
							}
						}
					}
				}

			}
		}
	}
}
