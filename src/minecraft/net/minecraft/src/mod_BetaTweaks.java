package net.minecraft.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class mod_BetaTweaks extends BaseMod {

	public String Version() {
		return "v1";
	}

	enum LogoState {
		STANDARD, ANIMATED, CUSTOM
	};

	public static LogoState optionsClientLogo = LogoState.STANDARD;
	public static Boolean optionsClientPanoramaEnabled = false;
	public static Boolean optionsClientQuitGameButton = true;
	public static Boolean optionsClientMultiplayerMenu = true;
	public static Boolean optionsClientScrollableControls = true;
	public static Boolean optionsClientIngameTexturePackButton = false;
	public static Boolean optionsClientDisableAchievementNotifications = false;
	public static Boolean optionsClientIndevStorageBlocks = false;
	public static Boolean optionsClientHideLongGrass = false;
	public static Boolean optionsClientHideDeadBush = false;

	public static Boolean optionsGameplayPunchableSheep = true;
	public static Boolean optionsGameplayLadderGaps = true;
	public static Boolean optionsGameplayLightTNTwithFist = true;
	public static Boolean optionsGameplayHoeDirtSeeds = false;
	public static Boolean optionsGameplayMinecartBoosters = true;
	public static Boolean optionsGameplayElevatorBoats = true;

	public static Boolean guiAPIinstalled;
	public static Boolean modloaderMPinstalled;
	public static GuiScreen parentScreen;
	public static GuiScreen firstGuiScreenAfterHijack;

	private static Boolean TNTinitialised = false;
	private static Boolean storageBlocksInitialised = false;
	private static Boolean customLogoInitialised = false;
	private static Boolean resetTallGrass = true;
	private static Boolean resetDeadBush = true;
	private static Boolean resetAchievements = true;
	private static Boolean resetLogo = true;
	private static World currentWorld = null;
	
	public static float logoScale = 0;
	public static float logoOffsetX = 0;
	public static float logoOffsetY = 0;
	public static float logoAxisTilt= 15;
	public static float logoLightMultiplier = 1;
	public static Boolean logoSplashTextEnabled = true;
	public static float logoSplashTextOffsetX = 0;
	public static float logoSplashTextOffsetY = 0;

	private static int steelSide = 22, steelBottom = 22;
	private static int goldSide = 23, goldBottom = 23;
	private static int diamondSide = 24, diamondBottom = 24;
	private static Boolean undrawn = true;

	private static int lastTickHoeDamage;
	private static int lastTickHoeX;
	private static int lastTickHoeY;
	private static int lastTickHoeZ;

	private static File configFile = new File((Minecraft.getMinecraftDir()) + "/config/BetaTweaks.cfg");
	private static File configLogoFile = new File((Minecraft.getMinecraftDir()) + "/config/OldCustomLogo.cfg");
	private static Long timeStamp = configFile.lastModified();
	private static List<String> image = new ArrayList<String>();
	private static List<Integer> blockID = new ArrayList<Integer>();
	private static List<Integer> metaData = new ArrayList<Integer>();
	private static List<Character> chars = new ArrayList<Character>();
	private static int maxLineLength = 0;
	protected Random rand;

	private KeyBinding playerList = new KeyBinding("List Players", 15);

	mod_BetaTweaks() {

		try {
			Class.forName("ModLoaderMp");
			modloaderMPinstalled = true;
			ModLoader.RegisterKey(this, playerList, false);
			ClassLoader classloader = (net.minecraft.src.ModLoader.class).getClassLoader();
			Method addmodMethod;
			try {
				addmodMethod = ModLoader.class.getDeclaredMethod("addMod", ClassLoader.class, String.class);
				addmodMethod.setAccessible(true);
				try {
					addmodMethod.invoke(null, new Object[] {
							classloader, "BetaTweaksMP.class"
						});
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

		} catch (ClassNotFoundException e) {
			System.out.println("ModLoaderMP not installed");
			modloaderMPinstalled = false;
		}
		try {
			Class.forName("ModSettings");
			guiAPIinstalled = true;
			BetaTweaksGuiAPI.instance.init();

		} catch (ClassNotFoundException e) {
			System.out.println("GUIAPI not installed, change BetaTweaks config in config file");
			guiAPIinstalled = false;
		}

		if (!configFile.exists())
			writeConfig();
		readConfig();
		if (guiAPIinstalled)
			BetaTweaksGuiAPI.instance.loadSettings();

		initSettings(ModLoader.getMinecraftInstance());

		ModLoader.SetInGameHook(this, true, false);
		ModLoader.SetInGUIHook(this, true, false);

		if ((optionsClientLogo != LogoState.STANDARD || optionsClientPanoramaEnabled)
				&& ModLoader.getMinecraftInstance().currentScreen == null)
			ModLoader.getMinecraftInstance().currentScreen = new GuiInitialHijack(new GuiMainMenu(),
					ModLoader.getMinecraftInstance().gameSettings);

	}

	public static void initSettings(Minecraft minecraft) {
		resetLogo = true;
		minecraft.hideQuitButton = !optionsClientQuitGameButton;

		if (optionsGameplayLightTNTwithFist && !TNTinitialised) {
			TNTinitialised = true;
			Block.blocksList[Block.tnt.blockID] = null;
			new BlockTNTPunchable();
		}

		if (optionsClientIndevStorageBlocks && !storageBlocksInitialised) {
			storageBlocksInitialised = true;
			initStorageBlocks();
		}

		if (optionsClientLogo == LogoState.CUSTOM && !customLogoInitialised) {
			if (!configLogoFile.exists())
				writeCustomLogoConfig();
			readCustomLogoConfig();
		}

		if (resetTallGrass) {
			resetTallGrass = false;
			Block.blocksList[Block.tallGrass.blockID] = null;
			Field x = Block.class.getDeclaredFields()[47];
			x.setAccessible(true);
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(x, x.getModifiers() & ~Modifier.FINAL);
				if (optionsClientHideLongGrass) {
					x.set(null, new BlockTallGrassHidden(Block.tallGrass));
				} else {
					x.set(null, (BlockTallGrass) (new BlockTallGrass(31, 39)).setHardness(0.0F)
							.setStepSound(Block.soundGrassFootstep).setBlockName("tallgrass"));
				}
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (resetDeadBush) {
			resetDeadBush = false;
			Block.blocksList[Block.deadBush.blockID] = null;
			Field x = Block.class.getDeclaredFields()[48];
			x.setAccessible(true);
			try {
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(x, x.getModifiers() & ~Modifier.FINAL);
				if (optionsClientHideDeadBush) {
					x.set(null, new BlockDeadBushHidden(Block.deadBush));
				} else {
					x.set(null, (BlockDeadBush) (new BlockDeadBush(32, 55)).setHardness(0.0F)
							.setStepSound(Block.soundGrassFootstep).setBlockName("deadbush"));
				}
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (resetAchievements) {
			resetAchievements = false;
			Field field = Minecraft.class.getDeclaredFields()[29];
			field.setAccessible(true);
			try {
				if (optionsClientDisableAchievementNotifications) {
					field.set(minecraft, new GuiAchievementNull(minecraft));
				} else {
					field.set(minecraft, new GuiAchievement(minecraft));
				}
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean OnTickInGUI(Minecraft minecraft, GuiScreen guiscreen) {

		if (firstGuiScreenAfterHijack != null) {
			minecraft.displayGuiScreen(firstGuiScreenAfterHijack);
			firstGuiScreenAfterHijack = null;
		} else if (guiscreen instanceof GuiMainMenu && !(guiscreen instanceof GuiMainMenuCustom)
				&& (optionsClientLogo != LogoState.STANDARD || optionsClientPanoramaEnabled)) {
			minecraft.displayGuiScreen(new GuiMainMenuCustom());
		} else if (guiscreen instanceof GuiMainMenuCustom && !(guiscreen instanceof GuiMainMenu)
				&& (optionsClientLogo == LogoState.STANDARD && !optionsClientPanoramaEnabled)) {
			minecraft.displayGuiScreen(new GuiMainMenu());
		} else if (optionsClientMultiplayerMenu && guiscreen instanceof GuiMultiplayer) {
			minecraft.displayGuiScreen(new GuiMultiplayerMenu(parentScreen));
		} else if (optionsClientScrollableControls && guiscreen instanceof GuiControls) {
			minecraft.displayGuiScreen(new GuiControlsScrollable(parentScreen, minecraft.gameSettings));
		} else if (optionsClientIngameTexturePackButton && guiscreen instanceof GuiIngameMenu) {
			minecraft.displayGuiScreen(new GuiIngameMenuWithTexturePackButton());
		} else if (!optionsClientIngameTexturePackButton && guiscreen instanceof GuiIngameMenuWithTexturePackButton) {
			minecraft.displayGuiScreen(new GuiIngameMenu());
		} else if (guiscreen != parentScreen) {
			if (guiAPIinstalled && guiscreen instanceof GuiModSelect && parentScreen instanceof GuiModScreen) {
				Boolean temp1 = optionsClientIndevStorageBlocks;
				Boolean temp2 = optionsClientHideLongGrass;
				Boolean temp3 = optionsClientHideDeadBush;
				Boolean temp4 = optionsClientDisableAchievementNotifications;

				BetaTweaksGuiAPI.instance.updateSettings();

				if (temp2 != optionsClientHideLongGrass) {
					resetTallGrass = true;
				}
				if (temp3 != optionsClientHideDeadBush) {
					resetDeadBush = true;
				}
				if (temp4 != optionsClientDisableAchievementNotifications) {
					resetAchievements = true;
				}

				initSettings(minecraft);

				if (temp1 != optionsClientIndevStorageBlocks || temp2 != optionsClientHideLongGrass
						|| temp3 != optionsClientHideDeadBush) {
					System.out.println("POING");
					minecraft.renderEngine.updateDynamicTextures();
					minecraft.renderEngine.refreshTextures();
					if (minecraft.theWorld != null)
						minecraft.renderGlobal.loadRenderers();
				}
			}
			parentScreen = guiscreen;
		}

		if (minecraft.theWorld != currentWorld) {
			if (guiAPIinstalled
					&& (currentWorld == null || !currentWorld.multiplayerWorld) != (minecraft.theWorld == null
							|| !minecraft.theWorld.multiplayerWorld)) {
				BetaTweaksGuiAPI.instance.loadSettings();
			}
			if (modloaderMPinstalled && minecraft.theWorld == null) {
				BetaTweaksMP.serverModInstalled = false;
			}
			currentWorld = minecraft.theWorld;
		}

		return true;
	}

	public boolean OnTickInGame(Minecraft minecraft) {

		if (modloaderMPinstalled && BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsServerAllowPlayerList
				&& Keyboard.isKeyDown(playerList.keyCode) && minecraft.currentScreen == null) {
			Gui x = new Gui();
			ScaledResolution scaledresolution = new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth,
					minecraft.displayHeight);
			int kx = scaledresolution.getScaledWidth();
			FontRenderer fontrenderer = minecraft.fontRenderer;
			int j3 = BetaTweaksMP.maxPlayers;
			int i4 = j3;
			int k4 = 1;
			for (; i4 > 20; i4 = ((j3 + k4) - 1) / k4) {
				k4++;
			}

			int k5 = 300 / k4;
			if (k5 > 150) {
				k5 = 150;
			}
			int j6 = (kx - k4 * k5) / 2;
			byte byte2 = 10;
			x.drawRect(j6 - 1, byte2 - 1, j6 + k5 * k4, byte2 + 9 * i4, 0x80000000);
			for (int k7 = 0; k7 < j3; k7++) {
				int i8 = j6 + (k7 % k4) * k5;
				int l8 = byte2 + (k7 / k4) * 9;
				x.drawRect(i8, l8, (i8 + k5) - 1, l8 + 8, 0x20ffffff);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(3008 /* GL_ALPHA_TEST */);
				if (k7 >= BetaTweaksMP.playerList.size()) {
					continue;
				}
				fontrenderer.drawStringWithShadow(BetaTweaksMP.playerList.get(k7), i8, l8, 0xffffff);
			}
		}

		if ((optionsGameplayLadderGaps && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayLadderGaps)) {

			for (int z = 0; z < minecraft.theWorld.loadedEntityList.size()
					+ minecraft.theWorld.playerEntities.size(); z++) {
				Entity entity;
				if (z < minecraft.theWorld.loadedEntityList.size()) {
					entity = (Entity) minecraft.theWorld.loadedEntityList.get(z);
				} else {
					entity = (Entity) minecraft.theWorld.playerEntities
							.get(z - minecraft.theWorld.loadedEntityList.size());
				}
				if (entity instanceof EntityLiving) {
					int i = MathHelper.floor_double(entity.posX);
					int j = MathHelper.floor_double(entity.boundingBox.minY);
					int k = MathHelper.floor_double(entity.posZ);
					int l = minecraft.theWorld.getBlockId(i, j + 1, k);
					if (!((EntityLiving) entity).isOnLadder() && l == Block.ladder.blockID) {
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
						if (!((EntityLiving) entity).isOnLadder() && entity.isCollidedHorizontally) {
							entity.motionY = 0.20000000000000001D;
							entity.motionY -= 0.080000000000000002D;
							entity.motionY *= 0.98000001907348633D;
						}
					}
				}

			}
		}
		if (((optionsGameplayPunchableSheep && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayPunchableSheep))
				&& minecraft.objectMouseOver != null
				&& minecraft.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY && Mouse.isButtonDown(0)) {

			Entity e = minecraft.objectMouseOver.entityHit;
			if (e instanceof EntitySheep) {
				if (minecraft.theWorld.multiplayerWorld && modloaderMPinstalled
						&& BetaTweaksMP.optionsGameplayPunchableSheep) {
					BetaTweaksMP.sheepPunched(e.entityId);
				} else if (e.beenAttacked) {
					if (!((EntitySheep) e).getSheared()) {

						((EntitySheep) e).setSheared(true);
						Random rand = new Random();
						int i = 2 + rand.nextInt(3);
						for (int j = 0; j < i; j++) {
							EntityItem wool = e.entityDropItem(
									new ItemStack(Block.cloth.blockID, 1, ((EntitySheep) e).getFleeceColor()), 1.0F);
							wool.motionY += rand.nextFloat() * 0.05F;
							wool.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
							wool.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
						}
					}
				}
			}

		}

		if (optionsGameplayMinecartBoosters && !minecraft.theWorld.multiplayerWorld) {
			for (int i = 0; i < minecraft.theWorld.loadedEntityList.size(); i++) {
				Entity entity = (Entity) minecraft.theWorld.loadedEntityList.get(i);
				if (entity instanceof EntityMinecart) {
					List list = minecraft.theWorld.getEntitiesWithinAABBExcludingEntity(entity,
							entity.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
					if (list != null && list.size() > 0) {
						for (int j1 = 0; j1 < list.size(); j1++) {
							Entity entity2 = (Entity) list.get(j1);
							if (entity2 != entity2.riddenByEntity && entity2.canBePushed()
									&& (entity2 instanceof EntityMinecart)) {
								double d = entity2.posX - entity.posX;
								double d1 = entity2.posZ - entity.posZ;
								double d2 = d * d + d1 * d1;
								if (d2 >= 9.9999997473787516E-005D) {
									d2 = MathHelper.sqrt_double(d2);
									d /= d2;
									d1 /= d2;
									double d3 = 1.0D / d2;
									if (d3 > 1.0D) {
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

		if (optionsGameplayElevatorBoats && !minecraft.theWorld.multiplayerWorld) {
			for (int i = 0; i < minecraft.theWorld.loadedEntityList.size(); i++) {
				Entity entity = (Entity) minecraft.theWorld.loadedEntityList.get(i);
				if (entity instanceof EntityBoat) {

					int q = 5;
					double d = 0.0D;
					for (int j = 0; j < q; j++) {
						double d5 = (entity.boundingBox.minY
								+ ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (j + 0)) / (double) i)
								- 0.125D;
						double d9 = (entity.boundingBox.minY
								+ ((entity.boundingBox.maxY - entity.boundingBox.minY) * (double) (j + 1)) / (double) i)
								- 0.125D;
						AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBoxFromPool(entity.boundingBox.minX, d5,
								entity.boundingBox.minZ, entity.boundingBox.maxX, d9, entity.boundingBox.maxZ);
						if (minecraft.theWorld.isAABBInMaterial(axisalignedbb, Material.water)) {
							d += 1.0D / (double) q;
						}
					}

					if (d >= 1.0D) {
						double d3 = d * 2D - 1.0D;
						entity.moveEntity(0, (0.039999999105930328D * d3) / 2.5D - 0.0070000002160668373D, 0);
					}
				}
			}
		}

		if (((optionsGameplayHoeDirtSeeds && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
				&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayHoeDirtSeeds))
				&& minecraft.thePlayer.getCurrentEquippedItem() != null
				&& minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemHoe
				&& lastTickHoeDamage == minecraft.thePlayer.getCurrentEquippedItem().getItemDamage() - 1) {
			lastTickHoeDamage = -1;

			if (minecraft.theWorld.multiplayerWorld && modloaderMPinstalled && BetaTweaksMP.serverModInstalled
					&& BetaTweaksMP.optionsGameplayHoeDirtSeeds) {

				BetaTweaksMP.grassHoed(lastTickHoeX, lastTickHoeY, lastTickHoeZ);
			} else {
				Random rand = new Random();

				if (rand.nextInt(8) == 0) {
					float f = 0.7F;
					float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
					float f2 = 1.2F;
					float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
					EntityItem seeds = new EntityItem(minecraft.theWorld, (double) lastTickHoeX + f1,
							(double) lastTickHoeY + f2, (double) lastTickHoeZ + f3, new ItemStack(Item.seeds));
					seeds.delayBeforeCanPickup = 10;
					minecraft.theWorld.entityJoinedWorld(seeds);
				}
			}
			return true;
		}

		if ((optionsClientHideLongGrass || optionsClientHideDeadBush
				|| ((optionsGameplayHoeDirtSeeds && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
						&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayHoeDirtSeeds)))
				&& Mouse.isButtonDown(1) && minecraft.thePlayer.getCurrentEquippedItem() != null
				&& minecraft.objectMouseOver != null
				&& minecraft.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {

			int x = minecraft.objectMouseOver.blockX;
			int y = minecraft.objectMouseOver.blockY;
			int z = minecraft.objectMouseOver.blockZ;

			if ((optionsGameplayHoeDirtSeeds && !minecraft.theWorld.multiplayerWorld) || (modloaderMPinstalled
					&& BetaTweaksMP.serverModInstalled && BetaTweaksMP.optionsGameplayHoeDirtSeeds)
					&& minecraft.thePlayer.getCurrentEquippedItem() != null
					&& minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemHoe) {
				int b = minecraft.theWorld.getBlockId(x, y, z);
				if (Block.blocksList[b] == Block.grass) {
					lastTickHoeDamage = minecraft.thePlayer.getCurrentEquippedItem().getItemDamage();
					lastTickHoeX = x;
					lastTickHoeY = y;
					lastTickHoeZ = z;
				}

			}

			if (minecraft.objectMouseOver.sideHit == 0) {
				y--;
			}
			if (minecraft.objectMouseOver.sideHit == 1) {
				y++;
			}
			if (minecraft.objectMouseOver.sideHit == 2) {
				z--;
			}
			if (minecraft.objectMouseOver.sideHit == 3) {
				z++;
			}
			if (minecraft.objectMouseOver.sideHit == 4) {
				x--;
			}
			if (minecraft.objectMouseOver.sideHit == 5) {
				x++;
			}
			int b = minecraft.theWorld.getBlockId(x, y, z);
			if (((Block.blocksList[b] == Block.tallGrass && optionsClientHideLongGrass)
					|| (Block.blocksList[b] == Block.deadBush && optionsClientHideDeadBush))) {

				if (minecraft.theWorld.multiplayerWorld && modloaderMPinstalled
						&& BetaTweaksMP.serverModInstalled) {
					BetaTweaksMP.longgrassDestroyed(x, y, z);
				} else if (minecraft.theWorld.multiplayerWorld) {
					minecraft.playerController.clickBlock(x, y, z, 0);
				} else {
					minecraft.theWorld.setBlock(x, y, z, 0);
				}
				minecraft.thePlayer.getCurrentEquippedItem().useItemRightClick(minecraft.theWorld, minecraft.thePlayer);
			}

		}

		return true;
	}

	public static void initStorageBlocks() {
		steelSide = ModLoader.addOverride("/terrain.png", "/BetaTweaks/steelSide.png");
		steelBottom = ModLoader.addOverride("/terrain.png", "/BetaTweaks/steelBottom.png");
		goldSide = ModLoader.addOverride("/terrain.png", "/BetaTweaks/goldSide.png");
		goldBottom = ModLoader.addOverride("/terrain.png", "/BetaTweaks/goldBottom.png");
		diamondSide = ModLoader.addOverride("/terrain.png", "/BetaTweaks/diamondSide.png");
		diamondBottom = ModLoader.addOverride("/terrain.png", "/BetaTweaks/diamondBottom.png");

		Block.blocksList[Block.blockSteel.blockID] = null;
		Block.blocksList[Block.blockGold.blockID] = null;
		Block.blocksList[Block.blockDiamond.blockID] = null;
		new BlockOreStorageIndev(Block.blockSteel, 60);
		new BlockOreStorageIndev(Block.blockGold, 59);
		new BlockOreStorageIndev(Block.blockDiamond, 75);

		Field field = ItemPickaxe.class.getDeclaredFields()[0];
		field.setAccessible(true);
		try {
			Block originalBlocks[] = (Block[]) field.get(null);

			Block newBlocks[] = new Block[originalBlocks.length];
			for (int i = 0; i < originalBlocks.length; i++) {
				newBlocks[i] = Block.blocksList[originalBlocks[i].blockID];
			}
			field.set(null, newBlocks);

		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}

		Item.itemsList[256 + 14] = null;
		Item.itemsList[256 + 18] = null;
		Item.itemsList[256 + 1] = null;
		Item.itemsList[256 + 29] = null;
		Item.itemsList[256 + 22] = null;

		Item.pickaxeWood = (new ItemPickaxe(14, EnumToolMaterial.WOOD)).setIconCoord(0, 6).setItemName("pickaxeWood");
		Item.pickaxeStone = (new ItemPickaxe(18, EnumToolMaterial.STONE)).setIconCoord(1, 6).setItemName("pickaxeStone");
		Item.pickaxeSteel = (new ItemPickaxe(1, EnumToolMaterial.IRON)).setIconCoord(2, 6).setItemName("pickaxeIron");
		Item.pickaxeGold = (new ItemPickaxe(29, EnumToolMaterial.GOLD)).setIconCoord(4, 6).setItemName("pickaxeGold");
		Item.pickaxeDiamond = (new ItemPickaxe(22, EnumToolMaterial.EMERALD)).setIconCoord(3, 6).setItemName("pickaxeDiamond");
		
	}

	public static int getTexture(Block block, int side) {
		if (block == Block.blockSteel) {
			if (side == 0)
				return steelBottom;
			else
				return steelSide;
		} else if (block == Block.blockGold) {
			if (side == 0)
				return goldBottom;
			else
				return goldSide;
		} else if (block == Block.blockDiamond) {
			if (side == 0)
				return diamondBottom;
			else
				return diamondSide;
		} else
			return 0;
	}

	public static void writeConfig() {
		try {
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));
			configWriter.write("// Config file for Beta Tweaks");

			Field[] myFields = mod_BetaTweaks.class.getFields();
			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().contains("options"))
					try {
						configWriter.write(System.lineSeparator() + myFields[i].getName().replaceFirst("options", "")
								+ "=" + myFields[i].get(null).toString());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
			}
			configWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void readConfig() {
		try {
			BufferedReader configReader = new BufferedReader(new FileReader(configFile));
			String s;
			while ((s = configReader.readLine()) != null) {
				if (s.charAt(0) == '/' && s.charAt(1) == '/') {
					continue;
				} // Ignore comments

				if (s.contains("=")) {
					String as[] = s.split("=");
					Field f1 = mod_BetaTweaks.class.getField("options" + (as[0]));

					if (f1.getType() == int.class) {
						f1.set(this, Integer.parseInt(as[1]));
					} else if (f1.getType() == Boolean.class) {
						f1.set(this, Boolean.parseBoolean(as[1]));
					} else if (f1.getType() == LogoState.class) {
						f1.set(this, LogoState.valueOf(as[1].toUpperCase()));
					}
				}
			}
			configReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static String[] getCustomLogo() {
		String as[] = new String[image.size()];
		for (int i = 0; i < image.size(); i++) {
			as[i] = image.get(i);
		}

		return as;
	}

	public static int getCustomLogoWidth() {
		return maxLineLength;
	}

	public static Block getCustomLogoBlock(int i, int j) {
		for (int k = 0; k < blockID.size(); k++) {
			if (image.get(i).charAt(j) == chars.get(k)) {
				if (blockID.get(k) < 255) {
					return Block.blocksList[blockID.get(k)];
				}
			}
		}
		return Block.torchWood;
	}

	public static int getCustomLogoBlockMetaData(int i, int j) {
		for (int k = 0; k < blockID.size(); k++) {
			if (image.get(i).charAt(j) == chars.get(k)) {
				return metaData.get(k);
			}
		}
		return 0;
	}

	public static Boolean customLogoConfigUpdated() {
		if (resetLogo) {
			resetLogo = false;
			return true;
		}
		if (configLogoFile.lastModified() != timeStamp) {
			timeStamp = configLogoFile.lastModified();
			if (!configLogoFile.exists()) {
				writeCustomLogoConfig();
			}
			return true;
		}
		return false;
	}

	public static void readCustomLogoConfig() {
		maxLineLength = 0;
		image.clear();
		chars.clear();
		blockID.clear();
		metaData.clear();

		try {
			BufferedReader configReader = new BufferedReader(new FileReader(configLogoFile));
			String s;
			Boolean readImage = false;
			Boolean readOptions = false;
			while ((s = configReader.readLine()) != null) {
				if (s.charAt(0) == '/' && s.charAt(1) == '/') {
					continue;
				} // Ignore comments
				else if (readOptions) {
					if (s.contains("=")) {
						String as[] = s.split("=");
						Field f1 = mod_BetaTweaks.class.getField("logo" + (as[0]));

						if (f1.getType() == float.class) {
							f1.set(null, Float.parseFloat(as[1]));
						}
						if (f1.getType() == Boolean.class) {
							f1.set(null, Boolean.parseBoolean(as[1]));
						}
					}
				}
				else if (readImage) {
					if (s.equals("#")) {
						readOptions = true;
					}
					else {
						image.add(s);
						if (s.length() > maxLineLength) {
							maxLineLength = s.length();
						}
					}
				} else if (s.contains("=")) {
					String as[] = s.split("=");
					chars.add(as[0].charAt(0));
					if (as[1].contains(":")) {
						String as1[] = as[1].split(":");
						blockID.add(Integer.parseInt(as1[0]));
						metaData.add(Integer.parseInt(as1[1]));
					} else if (as[1].contains("-")) {
						String as1[] = as[1].split("-");
						blockID.add(Integer.parseInt(as1[0]));
						metaData.add(Integer.parseInt(as1[1]));
					} else {
						blockID.add(Integer.parseInt(as[1]));
						metaData.add(0);
					}
				} else if (s.equals("#")) {
					readImage = true;
				}
			}
			configReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
			
		}
	}

	public static void writeCustomLogoConfig() {
		try {
			BufferedWriter configWriter = new BufferedWriter(new FileWriter(configLogoFile));
			configWriter.write(				   "// Config file for customising the animated logo."
					+ System.lineSeparator() + "// NOTE: The logo will automatically update when this file is saved but you may need to reset it (ESC) for all the blocks to show."
					+ System.lineSeparator() + "// "
					+ System.lineSeparator() + "// To customise the logo, first create a key below with block IDs and characters to match the ascii."
					+ System.lineSeparator() + "// This X=1 means that any block denoted with an X in the image section will be a stone block in the main menu."
					+ System.lineSeparator() + "X=35:15"
					+ System.lineSeparator() + "*=82"
					+ System.lineSeparator() + "R=35:10"
					+ System.lineSeparator() + "O=35:1"
					+ System.lineSeparator() + "Y=35:4"
					+ System.lineSeparator() + "G=35:5"
					+ System.lineSeparator() + "C=35:3"
					+ System.lineSeparator() + "B=35:11"
					+ System.lineSeparator() + "W=80"
					+ System.lineSeparator() + "P=35:6"
					+ System.lineSeparator() + "T=12"
					+ System.lineSeparator() + "M=1"
					+ System.lineSeparator() + "// You can use blocks with metadata with ':' or '-' so for example: Y=35-6 would attribute Y with pink wool."
					+ System.lineSeparator() + "// "
					+ System.lineSeparator() + "#"
					+ System.lineSeparator() + "// Below this # symbol is where the logo art is recorded."
					+ System.lineSeparator() + "// If you want to use a blank line you must put at least 1 space in."
					+ System.lineSeparator() + "// If you use a symbol below that has not been assigned a block, it will give the torch block (which does not look good)."
					+ System.lineSeparator() + "// "
					+ System.lineSeparator() + "        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR        RRRRRRRR    "
					+ System.lineSeparator() + "RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRXWWWWWWWWWWWWWWWWWX"
					+ System.lineSeparator() + "RRRRRRRROOOOOOOORRRRRRRROOOOOOOORRMMMMMOOMMMMMMMMMMMMMRMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMOOOORRRRRRXWWWPPPPPPPPPPPPPWWWX"
					+ System.lineSeparator() + "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOMTTTMOOMTTTMTTTMTTTMRMTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMOOOOOOOOOOXWWPPPPPPPRPPXXPPPWWX XXX"
					+ System.lineSeparator() + "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOMTTTMMMMTTTMTTTMTTTMMMTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMTTTTTTTMOOOOOOOOOOXWPPRPPPPPPPX**XRPPWXX**X"
					+ System.lineSeparator() + "OOOOOOOOYYYYYYYYOOOOOOOOYYYYYYYYOOMTTTTMMTTTTMTTTMTTTTMMTTTMTTTMMMMMTTTMMMMMTTTTTTTMTTMTMTTMTTTMMMMMMMTTTMMMYYYYOOOOOOXWPPPPPPPPPPX***PPPWX***X"
					+ System.lineSeparator() + "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYMTTTTMMTTTTMTTTMTTTTMMTTTMTTTMMMMMTTTMYYYMTTTMTTTMTTTMTTTMTTTMMMMYYMTTTMYYYYYYYYXYYYXWPPPPPPPRPPX***XXXX****X"
					+ System.lineSeparator() + "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYMTTTTTTTTTTMTTTMTTTTTTTTTMTTTTTTMMTTTMYYYMTTTMTTTMTTMMMTTMTTTTTTMYYMTTTMYYYYYYYX*XYYXWPPPPPPPPPPX***********X"
					+ System.lineSeparator() + "YYYYYYYYGGGGGGGGYYYYYYYYGGGGGGGGYYMTTTTTTTTTTMTTTMTTTTTTTTTMTTTTTTMMTTTMGGGMTTTTTMMMTTMTMTTMTTTTTTMYYMTTTMGGGGGGYX*XXXXWPPPPPPPPPX*************X"
					+ System.lineSeparator() + "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGMTTTMTTMTTTMTTTMTTTTTTTTTMTTTMMMMMTTTMGGGMTTTTTTTMTTTTTTTMTTTMMMMGGMTTTMGGGGGGGG****XWPPPPPPPPRX***WX****WX**X"
					+ System.lineSeparator() + "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGMTTTMMMMTTTMTTTMTTTMMTTTTMTTTMMMMMTTTMGGGMTTTMTTTMTTTMTTTMTTTMGGGGGMTTTMGGGGGGGGXX**XWPRPPPPPPPX***XX**X*XX**X"
					+ System.lineSeparator() + "GGGGGGGGCCCCCCCCGGGGGGGGCCCCCCCCGGMTTTMCCMTTTMTTTMTTTMMTTTTMTTTMMMMMTTTMMMMMTTTMTTTMTTTMTTTMTTTMGGGGGMTTTMCCCCCCGGGGXXXWPPPPPPPPPX*PP********PPX"
					+ System.lineSeparator() + "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCMTTTMCCMTTTMTTTMTTTMMMTTTMTTTTTTTMTTTTTTTMTTTMTTTMTTTMTTTMTTTMCCCCCMTTTMCCCCCCCCCCCCXWWPPPPPRPPPX***XXXXXX**X"
					+ System.lineSeparator() + "CCCCCCCCBBBBBBBBCCCCCCCCBBBBBBBBCCMTTTMBBMTTTMTTTMTTTMCMTTTMTTTTTTTMTTTTTTTMTTTMTTTMTTTMTTTMTTTMCCCCCMTTTMBBBBBBCWCCCXXWWWPPPPPPPPXX*********X"
					+ System.lineSeparator() + "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBMMMMMBBMMMMMMMMMMMMMCMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMBBBBBMMMMMBBBBBBBBBWX*XXWWWWWWWWWWWXXXXXXXXXX"
					+ System.lineSeparator() + "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBX***XXXXXXXXXXXXXXXXXX*X"
					+ System.lineSeparator() + "BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        BBBBBBBB        WBBBX**X X**       X**XX***X"
					+ System.lineSeparator() + "                                                                                                                    XXX  XXX       XXX  XXXX"
					+ System.lineSeparator() + "                                                                                                                   W"
					+ System.lineSeparator() + "                                                                                                                 W"
					+ System.lineSeparator() + "// "
					+ System.lineSeparator() + "#"
					+ System.lineSeparator() + "// Here are some basic settings to change the position of the logo. The light multiplier has a max value of 1.0"
					+ System.lineSeparator() + "Scale=-30.0"
					+ System.lineSeparator() + "OffsetX=-23.0"
					+ System.lineSeparator() + "OffsetY=+1.5"
					+ System.lineSeparator() + "AxisTilt=0.0"
					+ System.lineSeparator() + "LightMultiplier=0.8"
					+ System.lineSeparator() + "SplashTextEnabled=true"
					+ System.lineSeparator() + "SplashTextOffsetX=-15.0"
					+ System.lineSeparator() + "SplashTextOffsetY=-10.0");
			/*
				Field[] myFields = mod_BetaTweaks.class.getFields();
				for (int i = 0; i < myFields.length; i++) {
					if (myFields[i].getName().contains("logo"))
						try {
							configWriter.write(System.lineSeparator() + myFields[i].getName().replaceFirst("logo", "")
									+ "=" + myFields[i].get(null).toString());
						} catch (Exception exception) {
							exception.printStackTrace();
						}
				}
				*/
				configWriter.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
