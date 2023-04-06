# Block Enforcer (1.14.4)
This is a plugin to aid with the [Cubed Textures](https://github.com/CubedCommunity/CubedTextures) pack
- Adds a BlockState picker tool (the default tool is a bone)
  - Right clicking a block gives players an item with BlockState NBT data of the block
  - While sneaking it'll also add biome data, which will be applied on place
  - Left clicking a block will provide block info to the player
- Enforces the Item NBT BlockStates when placing them down
- Stops any neighbor updates from changing the Note Block and Redstone BlockStates
- Removes the ability to change the note of a Note Block with a right click, instead it allows for placing a block against them
- Prevents falling blocks from falling down
- Disables (Chorus) Plant placement rules, allowing to place them anywhere
  - By default a Chorus Plant will connect to the block it's been placed against and all surrounding Chorus Plants
  - When sneaking the Chorus Plant will only connect to the block it's been placed against
- Adds ability to place down Potted Plants with renamed Flower Pots
- Adds ability to place down Piston Heads with renamed (Sticky) Pistons
- Adds ability to place down Frosted Ice with renamed Ice
- Stop Turtle Eggs from breaking when stepped on
- Allow certain blocks to be placed anywhere, bypassing the need for them to be grounded

The BlockState picker is also part of my [Pick Block Pro mod](https://github.com/Sjouwer/pick-block-pro) which doesn't require a tool, is fully client side and instead works by holding ALT while block picking. This plugin however doesn't require players to install the mod.

This plugin works best in combination with Stoplag enabled from WorldGuard
