package net.minecraft.src.betatweaks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.BaseModMp;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityBoat;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ModLoaderMp;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet230ModLoader;

public class BetaTweaksMP extends BaseModMp {

	public static Boolean optionsGameplayPunchableSheep = false;
	public static Boolean optionsGameplayLadderGaps = false;
	public static Boolean optionsGameplayLightTNTwithFist = false;
	public static Boolean optionsGameplayHoeDirtSeeds = false;
	public static Boolean optionsGameplayMinecartBoosters = false;
	public static Boolean optionsGameplayElevatorBoats = false;
	
	public static Boolean optionsServerAllowPlayerList = true;
	public static String optionsServerMOTD = "A Minecraft Server";

	private static Boolean TNTinitialised = false;
	private static List<Integer> options = new ArrayList<Integer>();
	private static List<String> playerList = new ArrayList<String>();
	private static List<Boolean> playerIsOp = new ArrayList<Boolean>();
	private static File configFile;
	
	@Override
	public String Version() {
		return "v1";
	}

	BetaTweaksMP() {
		Packet.addIdClassMapping(254, false, true, net.minecraft.src.betatweaks.Packet254ServerPing.class);
		try {
			configFile = new File(new File(".").getCanonicalPath(), "/config/BetaTweaksMP.cfg");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(!configFile.exists()) writeConfig();
		readConfig();
    	
    	loadOptions();
        initSettings(ModLoader.getMinecraftServerInstance());
    	
        ModLoader.SetInGameHook(this, true, false);
    }
	
	public void loadOptions() {
		Field[] myFields = BetaTweaksMP.class.getFields();
		options.clear();
		for (int i = 0; i < myFields.length; i++) {
			if (myFields[i].getName().contains("options")) {
				try {
					options.add(((Boolean)myFields[i].get(this)) ? 1 : 0);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassCastException e) {
				}
			}
		}
	}
	
	public static void initSettings(MinecraftServer minecraft) {
		if (optionsGameplayLightTNTwithFist && !TNTinitialised) {
    		TNTinitialised = true;
    		Block.blocksList[Block.tnt.blockID] = null;
			new BlockTNTPunchable();
    	}
	}
	
	public void HandleLogin(EntityPlayerMP entityplayermp)
    {
		int[] dataInt = new int[1];
		dataInt[0] = ModLoader.getMinecraftServerInstance().configManager.isOp(entityplayermp.username) ? 1 : 0;
		
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 1;
		packet.dataInt = dataInt;
		ModLoaderMp.SendPacketTo(this, entityplayermp, packet);
		
		
		dataInt = new int[options.size() + 1];
		for (int i = 0; i < options.size(); i++) {
			dataInt[i] = options.get(i);
		}
		dataInt[options.size()] = ModLoader.getMinecraftServerInstance().propertyManagerObj.getIntProperty("max-players", 20);
		
		String[] dataString = new String[1];
		dataString[0] = optionsServerMOTD;
		
		packet = new Packet230ModLoader();
		packet.packetType = 0;
		packet.dataInt = dataInt;
		packet.dataString = dataString;
		ModLoaderMp.SendPacketTo(this, entityplayermp, packet);
    }
	
	
	public void HandlePacket(Packet230ModLoader packet, EntityPlayerMP player)
	{
		switch(packet.packetType)
		{
			case 0:
			{
				if (ModLoader.getMinecraftServerInstance().configManager.isOp(player.username)) {
					Field[] myFields = BetaTweaksMP.class.getFields();
					for (int i = 0; i < packet.dataInt.length; i++) {
							try {
								myFields[i].set(this, packet.dataInt[i] == 1);
								
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
					}
					
					optionsServerMOTD = packet.dataString[0];
					
					loadOptions();
					initSettings(ModLoader.getMinecraftServerInstance());
					ModLoaderMp.sendChatToOps("Beta Tweaks", player.username + " has updated settings");
					int[] dataInt = new int[options.size() + 1];
					for (int i = 0; i < options.size(); i++) {
						dataInt[i] = options.get(i);
					}
					
					String[] dataString = new String[1];
					dataString[0] = optionsServerMOTD;
					
					packet = new Packet230ModLoader();
					packet.packetType = 0;
					packet.dataInt = dataInt;
					packet.dataString = dataString;
					dataInt[options.size()] = ModLoader.getMinecraftServerInstance().propertyManagerObj.getIntProperty("max-players", 20);
					
					writeConfig();
					ModLoaderMp.SendPacketToAll(this, packet);
				}
				break;
			}
			
			case 1:
			{
				int b = ModLoaderMp.GetPlayerWorld(player).getBlockId(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2]);
				if (Block.blocksList[b] == Block.tallGrass || Block.blocksList[b] == Block.deadBush) {
					ModLoaderMp.GetPlayerWorld(player).setBlock(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2], 0);
				}
				break;
			}
			
			case 2:
			{
				if (optionsGameplayPunchableSheep) {
						for(int i = 0; i < ModLoaderMp.GetPlayerWorld(player).loadedEntityList.size(); i++)
						{
							Entity e = (Entity)ModLoaderMp.GetPlayerWorld(player).loadedEntityList.get(i);
							if (e.entityId == packet.dataInt[0] && e instanceof EntitySheep && !((EntitySheep)e).func_21069_f_()) {
								((EntitySheep)e).setSheared(true);
								
								
								Random rand = new Random();
								int integ = 2 + rand.nextInt(3);
									for (int j = 0; j < integ; j++) {
										EntityItem wool = e.entityDropItem(
											new ItemStack(Block.cloth.blockID, 1, ((EntitySheep)e).getFleeceColor()), 1.0F);
										wool.motionY += rand.nextFloat() * 0.05F;
										wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
										wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
								}
								
							}
						}
					
				}
				break;
			}
			
			case 3:
			{
				if (optionsGameplayHoeDirtSeeds) {
						int b = ModLoaderMp.GetPlayerWorld(player).getBlockId(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2]);
						if (Block.blocksList[b] == Block.tilledField) {
							Random rand = new Random();
							
							if (rand.nextInt(8) == 0) {
								float f = 0.7F;
								float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
								float f2 = 1.2F;
								float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
								EntityItem seeds = new EntityItem(ModLoader.getMinecraftServerInstance().worldMngr[player.dimension], (double)packet.dataInt[0] + f1, (double)packet.dataInt[1] + f2, (double)packet.dataInt[2] + f3, new ItemStack(Item.seeds));
								seeds.delayBeforeCanPickup = 10;
								ModLoaderMp.GetPlayerWorld(player).entityJoinedWorld(seeds);
								}
							}
							return;
						
					}
				break;
				}
			}
		}

	private boolean boo = true;
	
	public void OnTickInGame(MinecraftServer minecraft) {
		if(boo) {
			boo = false;
			JsonServer.main();
		}
		if (playerList.size() != minecraft.configManager.playerEntities.size()) {
			playerList.clear();
			
			if (minecraft.configManager.playerEntities.size() > 0) {
			String[] dataString = new String[minecraft.configManager.playerEntities.size()];
			for (int q = 0; q < minecraft.configManager.playerEntities.size(); q++) {
				playerList.add(((EntityPlayerMP)minecraft.configManager.playerEntities.get(q)).username);
				dataString[q] = playerList.get(q);
			}
			if (optionsServerAllowPlayerList) {
			Packet230ModLoader packet = new Packet230ModLoader();
			packet.packetType = 2;
			packet.dataString = dataString;
				
			ModLoaderMp.SendPacketToAll(this, packet);
			}
			playerIsOp.clear();
			for (int count = 0; count < playerList.size(); count++) {
				playerIsOp.add(minecraft.configManager.isOp(playerList.get(count)));
				}
			}
		}
		
		for (int count2 = 0; count2 < playerList.size(); count2++) {
			
			if (minecraft.configManager.isOp(playerList.get(count2)) != playerIsOp.get(count2)) {
				playerIsOp.set(count2, minecraft.configManager.isOp(playerList.get(count2)));
				
				int[] dataInt = new int[1];
				dataInt[0] = playerIsOp.get(count2)? 1 : 0;
				Packet230ModLoader newpacket = new Packet230ModLoader();
				newpacket.packetType = 1;
				newpacket.dataInt = dataInt;
				
				ModLoaderMp.SendPacketTo(this, ModLoader.getMinecraftServerInstance().configManager.getPlayerEntity(playerList.get(count2)), newpacket);
			}
		}

		if (optionsGameplayMinecartBoosters) {
			for (int q = 0; q < minecraft.worldMngr.length; q++) {
				for(int i = 0; i < minecraft.worldMngr[q].loadedEntityList.size(); i++)
			{
				Entity entity = (Entity)minecraft.worldMngr[q].loadedEntityList.get(i);
				
				
				
				
				if (entity instanceof EntityMinecart) {
				List list = minecraft.worldMngr[q].getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
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
		
		
		if (optionsGameplayElevatorBoats) {
			for (int k = 0; k < minecraft.worldMngr.length; k++) {
			for(int i = 0; i < minecraft.worldMngr[k].loadedEntityList.size(); i++) {
				Entity entity = (Entity)minecraft.worldMngr[k].loadedEntityList.get(i);
				if (entity instanceof EntityBoat) {
					
				int q = 5;
		        double d = 0.0D;
		        for(int j = 0; j < q; j++)
		        {
		            double d5 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double)(j + 0)) / (double)i) - 0.125D;
		            double d9 = (entity.boundingBox.minY + ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double)(j + 1)) / (double)i) - 0.125D;
		            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBoxFromPool(entity.boundingBox.minX, d5, entity.boundingBox.minZ, entity.boundingBox.maxX, d9, entity.boundingBox.maxZ);
		            if(minecraft.worldMngr[k].isAABBInMaterial(axisalignedbb, Material.water))
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
		
		if (optionsGameplayLadderGaps) {
			
			for (int q = 0; q < minecraft.worldMngr.length; q++) {
				for(int z = 0; z < minecraft.worldMngr[q].loadedEntityList.size() + minecraft.worldMngr[q].playerEntities.size(); z++) {
					Entity entity;
					if (z < minecraft.worldMngr[q].loadedEntityList.size()) {
						entity = (Entity)minecraft.worldMngr[q].loadedEntityList.get(z);
					}
					else {
						entity = (Entity)minecraft.worldMngr[q].playerEntities.get(z - minecraft.worldMngr[q].loadedEntityList.size());
					}
					if (entity instanceof EntityLiving) {
						int i = MathHelper.floor_double(entity.posX);
						int j = MathHelper.floor_double(entity.boundingBox.minY);
						int k = MathHelper.floor_double(entity.posZ);
						int l = minecraft.worldMngr[q].getBlockId(i, j + 1, k);
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
							entity.fallDistance = 0.0F;
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
	
	public static void writeConfig()
    {
		try
		{
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));
			configWriter.write("// Config file for Beta Tweaks");

			Field[] myFields = BetaTweaksMP.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("options"))
				try
				{
					configWriter.write(System.getProperty("line.separator") + myFields[i].getName().replaceFirst("options", "") + "=" + myFields[i].get(null).toString());
				}catch(Exception exception) {exception.printStackTrace(); }
			}
			configWriter.close();
		}
		catch(Exception exception) {exception.printStackTrace(); }
    }

    public  void readConfig()
    {
    	
    	try
		{
			BufferedReader configReader = new BufferedReader(new FileReader(configFile));
			String s;
			while ((s = configReader.readLine()) != null)
			{
				if(s.length() == 0 || (s.charAt(0) == '/' && s.charAt(1) == '/')) {continue;} //Ignore comments
        	
				if(s.contains("="))
				{
					String as[] = s.split("=");
					Field f1 = BetaTweaksMP.class.getField("options" + (as[0]));
					
			        if (f1.getType() == int.class) {
			        	f1.set(this, Integer.parseInt(as[1]));
			        }
			        else if (f1.getType() == Boolean.class) {
			        	f1.set(this, Boolean.parseBoolean(as[1]));
			        }
			        else if (f1.getType() == String.class) {
			        	f1.set(this, as[1]);
			        }
				}
        	}
        	configReader.close();
    	}
    catch(Exception exception) {exception.printStackTrace(); }
    }
}
