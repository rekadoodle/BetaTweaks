package net.minecraft.server.betatweaks;

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

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.BaseModMp;
import net.minecraft.server.Block;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityBoat;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMinecart;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ModLoader;
import net.minecraft.server.ModLoaderMp;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet230ModLoader;



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
		Packet.a(254, false, true, net.minecraft.server.betatweaks.Packet254ServerPing.class);
		try {
			configFile = new File(new File(".").getCanonicalPath(), "/config/BetaTweaks.cfg");
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
    		Block.byId[Block.TNT.id] = null;
			new BlockTNTPunchable();
    	}
	}
	
	public void HandleLogin(EntityPlayer entityplayermp)
    {
		int[] dataInt = new int[1];
		dataInt[0] = ModLoader.getMinecraftServerInstance().serverConfigurationManager.isOp(entityplayermp.name) ? 1 : 0;
		
		Packet230ModLoader packet = new Packet230ModLoader();
		packet.packetType = 1;
		packet.dataInt = dataInt;
		ModLoaderMp.SendPacketTo(this, entityplayermp, packet);
		
		
		dataInt = new int[options.size() + 1];
		for (int i = 0; i < options.size(); i++) {
			dataInt[i] = options.get(i);
		}
		dataInt[options.size()] = ModLoader.getMinecraftServerInstance().propertyManager.getInt("max-players", 20);
		
		String[] dataString = new String[1];
		dataString[0] = optionsServerMOTD;
		
		packet = new Packet230ModLoader();
		packet.packetType = 0;
		packet.dataInt = dataInt;
		packet.dataString = dataString;
		ModLoaderMp.SendPacketTo(this, entityplayermp, packet);
    }
	
	
	public void HandlePacket(Packet230ModLoader packet, EntityPlayer player)
	{
		switch(packet.packetType)
		{
			case 0:
			{
				if (ModLoader.getMinecraftServerInstance().serverConfigurationManager.isOp(player.name)) {
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
					ModLoaderMp.sendChatToOps("Beta Tweaks", player.name + " has updated settings");
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
					dataInt[options.size()] = ModLoader.getMinecraftServerInstance().propertyManager.getInt("max-players", 20);
					
					writeConfig();
					ModLoaderMp.SendPacketToAll(this, packet);
				}
				break;
			}
			
			case 1:
			{
				int b = ModLoaderMp.GetPlayerWorld(player).getTypeId(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2]);
				if (Block.byId[b] == Block.LONG_GRASS || Block.byId[b] == Block.DEAD_BUSH) {
					ModLoaderMp.GetPlayerWorld(player).setRawTypeId(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2], 0);
				}
				break;
			}
			
			case 2:
			{
				if (optionsGameplayPunchableSheep) {
						for(int i = 0; i < ModLoaderMp.GetPlayerWorld(player).entityList.size(); i++)
						{
							Entity e = (Entity)ModLoaderMp.GetPlayerWorld(player).entityList.get(i);
							if (e.id == packet.dataInt[0] && e instanceof EntitySheep && !((EntitySheep)e).isSheared()) {
								((EntitySheep)e).setSheared(true);
								
								
								Random rand = new Random();
								int integ = 2 + rand.nextInt(3);
									for (int j = 0; j < integ; j++) {
										EntityItem wool = e.a(
											new ItemStack(Block.WOOL.id, 1, ((EntitySheep)e).getColor()), 1.0F);
										wool.motY += rand.nextFloat() * 0.05F;
										wool.motX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
										wool.motZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
								}
								
							}
						}
					
				}
				break;
			}
			
			case 3:
			{
				if (optionsGameplayHoeDirtSeeds) {
						int b = ModLoaderMp.GetPlayerWorld(player).getTypeId(packet.dataInt[0], packet.dataInt[1], packet.dataInt[2]);
						if (Block.byId[b] == Block.SOIL) {
							Random rand = new Random();
							
							if (rand.nextInt(8) == 0) {
								float f = 0.7F;
								float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
								float f2 = 1.2F;
								float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
								EntityItem seeds = new EntityItem(ModLoader.getMinecraftServerInstance().worlds.get(player.dimension), (double)packet.dataInt[0] + f1, (double)packet.dataInt[1] + f2, (double)packet.dataInt[2] + f3, new ItemStack(Item.SEEDS));
								seeds.pickupDelay = 10;
								ModLoaderMp.GetPlayerWorld(player).addEntity(seeds);
								}
							}
							return;
						
					}
				break;
				}
			}
		}

	
	
	public void OnTickInGame(MinecraftServer minecraft) {
		if (playerList.size() != minecraft.serverConfigurationManager.players.size()) {
			playerList.clear();
			if (minecraft.serverConfigurationManager.players.size() > 0) {
			String[] dataString = new String[minecraft.serverConfigurationManager.players.size()];
			for (int q = 0; q < minecraft.serverConfigurationManager.players.size(); q++) {
				playerList.add(((EntityPlayer)minecraft.serverConfigurationManager.players.get(q)).name);
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
				playerIsOp.add(minecraft.serverConfigurationManager.isOp(playerList.get(count)));
				}
			}
		}
		
		for (int count2 = 0; count2 < playerList.size(); count2++) {
			
			if (minecraft.serverConfigurationManager.isOp(playerList.get(count2)) != playerIsOp.get(count2)) {
				playerIsOp.set(count2, minecraft.serverConfigurationManager.isOp(playerList.get(count2)));
				
				int[] dataInt = new int[1];
				dataInt[0] = playerIsOp.get(count2)? 1 : 0;
				Packet230ModLoader newpacket = new Packet230ModLoader();
				newpacket.packetType = 1;
				newpacket.dataInt = dataInt;
				
				ModLoaderMp.SendPacketTo(this, minecraft.serverConfigurationManager.i(playerList.get(count2)), newpacket);
			}
		}

		if (optionsGameplayMinecartBoosters) {
			for (int q = 0; q < minecraft.worlds.size(); q++) {
				for(int i = 0; i < minecraft.worlds.get(q).entityList.size(); i++)
			{
				Entity entity = (Entity)minecraft.worlds.get(q).entityList.get(i);
				
				
				
				
				if (entity instanceof EntityMinecart) {
				List list = minecraft.worlds.get(q).b(entity, entity.boundingBox.b(0.20000000298023224D, 0.0D, 0.20000000298023224D));
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
		
		
		if (optionsGameplayElevatorBoats) {
			for (int k = 0; k < minecraft.worlds.size(); k++) {
			for(int i = 0; i < minecraft.worlds.get(k).entityList.size(); i++) {
				Entity entity = (Entity)minecraft.worlds.get(k).entityList.get(i);
				if (entity instanceof EntityBoat) {
					
				int q = 5;
		        double d = 0.0D;
		        for(int j = 0; j < q; j++)
		        {
		            double d5 = (entity.boundingBox.b + ((entity.boundingBox.e - entity.boundingBox.b) * (double)(j + 0)) / (double)i) - 0.125D;
		            double d9 = (entity.boundingBox.b + ((entity.boundingBox.e - entity.boundingBox.b) * (double)(j + 1)) / (double)i) - 0.125D;
		            AxisAlignedBB axisalignedbb = AxisAlignedBB.b(entity.boundingBox.a, d5, entity.boundingBox.c, entity.boundingBox.d, d9, entity.boundingBox.f);
		            if(minecraft.worlds.get(k).b(axisalignedbb, Material.WATER))
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
		
		if (optionsGameplayLadderGaps) {
			
			for (int q = 0; q < minecraft.worlds.size(); q++) {
				for(int z = 0; z < minecraft.worlds.get(q).entityList.size() + minecraft.worlds.get(q).players.size(); z++) {
					Entity entity;
					if (z < minecraft.worlds.get(q).entityList.size()) {
						entity = (Entity)minecraft.worlds.get(q).entityList.get(z);
					}
					else {
						entity = (Entity)minecraft.worlds.get(q).players.get(z - minecraft.worlds.get(q).entityList.size());
					}
					if (entity instanceof EntityLiving) {
						int i = MathHelper.floor(entity.locX);
						int j = MathHelper.floor(entity.boundingBox.b);
						int k = MathHelper.floor(entity.locZ);
						int l = minecraft.worlds.get(q).getTypeId(i, j + 1, k);
						if (!((EntityLiving)entity).p() && l == Block.LADDER.id) {
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
							if (!((EntityLiving)entity).p() && entity.positionChanged) {
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
					configWriter.write(System.lineSeparator() + myFields[i].getName().replaceFirst("options", "") + "=" + myFields[i].get(null).toString());
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
				if(s.charAt(0) == '/' && s.charAt(1) == '/') {continue;} //Ignore comments
        	
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
