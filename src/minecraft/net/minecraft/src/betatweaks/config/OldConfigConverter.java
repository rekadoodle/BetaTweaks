package net.minecraft.src.betatweaks.config;

public class OldConfigConverter {

	private static final Config cfg = Config.getInstance();
	
	public static void tryParse(String name, String newValue) {
		if(name.equals("clientDraggingShortcuts")) cfg.draggingShortcuts.setValue(newValue);
		else if(name == "clientLogo") {
			if(newValue.equalsIgnoreCase("Standard")) cfg.logoStyle.setValue(0);
			else if(newValue.equalsIgnoreCase("Animated")) cfg.logoStyle.setValue(1);
			else if(newValue.equalsIgnoreCase("Custom")) cfg.logoStyle.setValue(2);
		}
		else if(name.equals("clientPanoramaEnabled")) cfg.mainmenuPanorama.setValue(newValue);
		else if(name.equals("clientQuitGameButton")) cfg.mainmenuQuitButton.setValue(newValue);
		else if(name.equals("clientMultiplayerMenu")) cfg.serverList.setValue(newValue);
		else if(name.equals("clientImprovedChat")) cfg.improvedChat.setValue(newValue);
		else if(name.equals("clientImprovedChatFontScaleValue")) cfg.improvedChatFontScale.setSliderValue(newValue);
		else if(name.equals("clientImprovedChatWidthValue")) cfg.improvedChatWidthPercentage.setSliderValue(newValue);
		else if(name.equals("clientImprovedChatIngameHeightOffset")) cfg.improvedChatIngameHeightOffset.setSliderValue(newValue);
		else if(name.equals("clientImprovedChatIngameMaxHeight")) cfg.improvedChatIngameHeightPercentage.setSliderValue(newValue);
		else if(name.equals("clientImprovedChatMaxMessagesSize")) cfg.improvedChatMaxScrollableMessages.setSliderValue(newValue);
		else if(name.equals("clientImprovedChatIndicator")) cfg.improvedChatIndicator.setValue(newValue);
		else if(name.equals("improvedChatInvisibleToggleButton")) cfg.improvedChatInvisibleToggleButton.setValue(newValue);
		else if(name.equals("clientScrollableControls")) cfg.scrollableControls.setValue(newValue);
		else if(name.equals("clientIngameTexturePackButton")) cfg.ingameTexurePackButton.setValue(newValue);
		else if(name.equals("clientDisableAchievementNotifications")) cfg.hideAchievementNotifications.setValue(newValue);
		else if(name.equals("clientDisableEntityRendererOverride")) cfg.disableEntityRendererOverride.setValue(newValue);
		else if(name.equals("clientFovSliderVisible")) cfg.fovSlider.setValue(newValue);
		else if(name.equals("clientFovSliderValue")) cfg.fov.setSliderValue(newValue);
		else if(name.equals("clientIndevStorageBlocks")) cfg.indevStorageBlocks.setValue(newValue);
		else if(name.equals("clientHideLongGrass")) cfg.hideLongGrass.setValue(newValue);
		else if(name.equals("clientHideDeadBush")) cfg.hideDeadBush.setValue(newValue);
		else if(name.equals("clientCustomFullscreenResolution")) cfg.customFullscreenRes.setValue(newValue);
		else if(name.equals("gameplayPunchSheepForWool")) cfg.punchSheepForWool.setValue(newValue);
		else if(name.equals("gameplayLadderGaps")) cfg.ladderGaps.setValue(newValue);
		else if(name.equals("gameplayLightTNTwithFist")) cfg.lightTNTwithFist.setValue(newValue);
		else if(name.equals("gameplayHoeDirtForSeeds")) cfg.hoeGrassForSeeds.setValue(newValue);
		else if(name.equals("gameplayMinecartBoosters")) cfg.minecartBoosters.setValue(newValue);
		else if(name.equals("gameplayBoatElevators")) cfg.boatElevators.setValue(newValue);
	}
}
