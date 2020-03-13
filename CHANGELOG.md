# Changelog

## [v1.2.2] - 2019/11/06
- Fixed compatibility issue with OverrideAPI and FOV/zoom
- Fixed compatibility isue with Aether and Improved chat
- Config now stores real values instead of slider values, should be converted automatically
- FOV multiplier config option removed now that you can change fov to an exact value in config
- Ingame max chat height increased 100% -> 110% so it can go offscreen
- Fixed Improved chat not lining up with start of chatbox at some scales
- Added option to change chat horizontal gap (2px looked too big at low chat scale)
- Tooltips added for each GuiAPI page button for clarity
- Fixed GuiAPI tooltips popping up inconsistently
- Large code changes to GuiAPIHandler, should be functionally identical

## [v1.2.1] - 2019/10/18
- Fixed graphical errors when using item spreading shortcut (bug still exists: tooltip graphic still shows behind item)
- Fixed invisible Improved Chat config buttons being clickable when hidden, now only show button is clickable when hidden (configurable in txt config) (thanks Logan)
- Fixed Improved Chat multi line colours (thanks Meefy)
- Fixed Improved Chat crash (thanks Meefy)
- Fixed Optifine zoom key not working properly with FOV (thanks Logan)
- Fixed Optifine fancy water being invisible with FOV (thanks Logan)
- Improved tooltip for Improved Chat to describe how to configure (thanks Logan)

## [v1.2.0] - 2019/10/12
- Improved Chat added with scaling, scrolling, shortcuts and more. Use /chatoptions to configure.
- Mod now contained in it's own package (BetaTweaksMP will be moved when server mod updated) 
- FOV slider now compatible with optifine watershader
- Fixed FOV slider not showing up in some instances
- Fixed crash in multiplayer when guiapi not installed
- Fixed issue with dragging shortcuts and minecolony inventories
- Added meme option for fov multiplier (edit in BetaTweaks.cfg)
- Added ability to use a custom fullscreen res:
  - Change clientShowAllResolutionsInConsole to true in BetaTweaks.cfg
  - Open the game (it will lag with this enabled) and see available resolutions in console
  - Put your resolution of choice in BetaTweaks.cfg as it is displayed in the console (eg. clientCustomFullscreenResolution=640,480,32,144)
  - A new keybind will be available in controls for custom fullscreen resolution (default: f8).
  - Note: This is fairly untested. Make sure to disable showAllResolutions after you have your desired res.

## [v1.1.6_4] - 2019/08/24
- Compatibility patch for HMI v4.2.0

## [v1.1.6_3] - 2019/07/11
- Fix for zoom activating with gui open
- Added compatibility for fov/zoom and forge rendering
- Minor code cleanup

## [v1.1.6_2] - 2019/06/22
- Fix for minor flickering with button overrides
- Fix for missing splash text

## [v1.1.6] - 2019/06/22
- Gui overrides are now actually less flickery (coded this last time but failed implementation)
- Fixed overrided screen not going back to the right screen (thanks mine_diver)
- Fixed panorama menu gradient not showing on main menu (thanks mine_diver)
- Panorama menu now works correctly with optifine
- FOV slider underwater fov fixed (thanks LowMango)
- FOV slider invisible optifine water fixed

## [v1.1.5_4] - 2019/05/27
- Fix for spazzing out animation when travelling through portal

## [v1.1.5_3] - 2019/05/25
- Fix for punchable TNT name

## [v1.1.5_2] - 2019/05/25
- Hotfix for crashes caused by not having enough mods installed...

## [v1.1.5] - 2019/05/25
- Added Optifine style zoom keybind & fov slider (disable with ClientDisableEntityRendererOverride in config if you have problems)
- Evenly spreading items with dragging shortcuts now highlights the selected slots in the gui.
- Gui overrides are less flickery
- Aether sheep can now be punched for wool (untested)
- Fixed ESC Menu Texture Pack Button (Thanks LowMango)

## [v1.1.4] - 2019/05/12
- Should now be compatible with MCExtended (thanks mine_diver)
- Inv dragging tweaks no longer overrides HMI's Recipe Viewer
- You can now equip armour by right clicking with it in your hand with inv dragging tweaks on
- You can now shift click 1 item by shift right clicking with inv dragging tweaks on
- Minor bug with inv dragging tweaks where clicking would sometimes not deposit the item in the slot correctly
- Controls menu drag scrolling improved

## [v1.1.3] - 2019/04/17
- Reverted panorama file location, you can now change it in OldCustomLogo.cfg
- Changed mod menu description, changed icon location

## [v1.1.2] - 'Fr*ck DDOS protection' - 2019/04/16
- After it was found that the server browser was giving End Of Stream on some servers (thanks Meefy), you now have the option
	to not ping servers. Not sure if there is a real fix for this as the current theory for the cause of this is DDOS protection.
- Vanilla servers that have been pinged now don't show Communication Error (this may now pick up other servers..)
- Location of panorama images has been changed to respect the modern versions (thanks mine_diver) and purple line on 1 texture has been fixed (thanks LowMango).
- Various tooltip descriptions improved.
- Name of TNT block fixed when 'Punch TNT to ignite' is enabled.
- Graphical glitch when going fullscreen on client launch fixed. (thanks mine_diver & LowMango)
- Dropping functionality of held and hovered items swapped. Items held are dropped 1 by 1 when you press Q and items hovered drop fast when you hold Q. 
- When collecting items with dragging, the first item would sometimes be skipped. This has been fixed.
- Icons and description added for mine_divers' Mod Menu.

## [v1.1.1] - 'Better for everyone' - 2019/04/11
- Tooltips have been changed from the weird glitchy ones before to clean optifine style ones.
- The ESC Menu Texture Pack button no longer disappears after resizing the window.
- Crash fixed when using ShockAPI (SAPI) and the Indev Storage Blocks feature
- Now supports GuiAPI 0.10.4 as well as 0.11.0, meaning you can easily install it straight onto old packs like Technic and Yogbox
- Code has been compiled in Java 6 =)

## [v1.1] - 2019/04/09

- Added Inventory Dragging Shortcuts!
This is my attempt at the InventoryTweaks/Convenient Inventory industry. 
When enabled, it gives the following features when in an inventory:
  - Holding LMB with an item to increase the stacksize of the item when hovering another item of the same type.
  - Holding LMB with an item on an empty slot to equally spread the stack accross the slots.
  - Holding RMB with an item to drop 1 on each slot you come accross.
  - Shift + LMB on a crafting result to craft a whole stack.
  - Shift + LMB with an empty cursor to shift click any items you hover.
  - Shift + LMB with an item to shift click any items you hover of the same type.
  - Q with an item to quickly drop the item (one by one).
  - Q with an empty cursor to drop 1 of the item being hovered.
  - Shift + Q with an item to drop the stack held.
  - Shift + Q with an empty cursor to drop the stack being hovered
  - Maybe some other stuff I forgot + bugs.
- Server menu is now scrollable with scroll wheel.
- Hovering server ping thing on server menu correctly shows tooltip.
- Hoe grass for seeds works with BTW hemp seeds.
- Improved code fixing pickaxe speed on old storage blocks, improved mod compatibility.
- Total classes & LOC reduced, code optimisations etc.
- This version hasn't been tested on BetaTweaks servers but I think I only changed clientside stuff so it should be okay.

## [v1.0] - 2019/02/22

This mod implements a lot of other mods and some new features.
Some have a large number of changes, some have next to none. Here are the ones I can remember.
- OldCustomLogo
  - No longer edits base classes (Uses a shady main menu hijack instead)
  - Blocks are correctly oriented
  - Mod blocks should load correctly on startup
  - Logo config can be changed and saved and will update the in game logo
		(may require a refresh with ESC to load some new blocks)
  - Added some basic options in the custom logo config for scaling and such
- ScrollableControls
	- You can now set controls to unbound with ESC
	- Crash fixed when you try to bind a key to a mouse button
- Misc Details
	- Hiding long grass and dead bushes is now a client side option
		If you use it on vanilla servers you will see some particles when you break one
	- OPs can change server settings from the GuiAPI menu
	- The main menu shouldn't be overrided if you use the vanilla logo and background so 
		hopefully it is compatible with the Aether main menu
