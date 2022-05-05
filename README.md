# Block Enforcer (1.14.4)
This is a plugin to aid with special texture packs that uses, among other things, note block BlockStates to add a lot of extra textures to Minecraft. The plugin does the following:  
- Adds a BlockState picker tool, which gives players an item with BlockState NBT data of the block they right clicked
- Enforces the Item NBT BlockState when a Note Block is being placed down
- Stops any neighbor updates from changing the note block BlockStates
- Removes the ability to change the note of a note block with a right click, instead it allows for placing a block
- Prevents falling blocks from falling down

This plugin works best in combination with Stoplag enabled from WorldGuard