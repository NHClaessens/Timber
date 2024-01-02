# Timber
 Timber mod for minecraft 1.20.4

## Config

```
{
"names": ["Timber"],
"items": ["diamond_axe"],
"blocks": ["oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log", "dark_oak_log", "mangrove_log", "cherry_log", "crimson_stem", "warped_stem"],
"max_blocks": 15,
"hunger_per_block": 1
}
```

| Field             | Info                                                                                | When absent                   |
|-------------------|-------------------------------------------------------------------------------------|-------------------------------|
| `names`           | Required name for an item to be affected                                            | Any item name will work       |
| `items`           | Items on which the effect will work                                                 | Any item will work            |
| `blocks`          | Blocks on which the effect will work                                                | Effect will work on any block |
| `max_blocks`      | Maximum amount of blocks to mine in 1 go, defaults to 15                            | Defaults to 15                |
| `hunger_per_block` | How many hunger points to remove from the player for each block mined, default to 0 | Defaults to 0                 |

> :warning: although the effect can be applied to any block, it might not work as expected since it is intended for trees. Use at your own discretion. 