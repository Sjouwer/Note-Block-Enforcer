# Block Enforcer (1.14.4)
This is a plugin to aid with special texture packs that uses, among other things, note block BlockStates to add a lot of extra textures to Minecraft. The plugin does the following:  
- Adds a BlockState picker tool, which gives players an item with BlockState NBT data of the block they right clicked (the default tool is a bone)
- Enforces the Item NBT BlockState when a Note Block is being placed down
- Stops any neighbor updates from changing the note block BlockStates
- Removes the ability to change the note of a note block with a right click, instead it allows for placing a block
- Prevents falling blocks from falling down
- Disables Chorus Plant placement rules, allowing to place them everywhere
- Adds ability to place down Potted Plants with renamed Flower Pots
- Adds ability to place down Piston Heads with renamed (Sticky) Pistons
- Adds ability to place down Frosted Ice with renamed Ice

The BlockState picker is also part of my [Pick Block Pro mod](https://github.com/Sjouwer/pick-block-pro) which doesn't require a tool, is fully client side and instead works by holding ALT while block picking. This plugin however doesn't require players to install the mod.

This plugin works best in combination with Stoplag enabled from WorldGuard
