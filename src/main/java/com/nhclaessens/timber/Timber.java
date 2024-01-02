package com.nhclaessens.timber;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nhclaessens.timber.config.SimpleConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timber implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("timber");

	SimpleConfig CONFIG = SimpleConfig.of("TimberConfig").provider(this::provider).request();
	private int MAX_BLOCKS = 0;
	private float HUNGER = 0;

	private String provider(String filename) {
		return """
                {
                  "names": ["Timber"],
                  "items": ["diamond_axe"],
                  "blocks": ["oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log", "dark_oak_log", "mangrove_log", "cherry_log", "crimson_stem", "warped_stem"],
                  "max_blocks": 15,
                  "hunger_per_block": 1
                }""";
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		PlayerBlockBreakEvents.AFTER.register(this::onBlockBreak);

		MAX_BLOCKS = Integer.parseInt(String.valueOf(CONFIG.getOrDefault("max_blocks", 10)));
		HUNGER = Float.parseFloat(String.valueOf(CONFIG.getOrDefault("hunger_per_block", 0.0)));
	}

	public void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity entity) {

		ItemStack tool = player.getMainHandStack();

		if(validItem(tool) && validName(tool) && validBlock(state)) {

			BlockPos[] adjacentPositions = {
					pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
			};

			for (BlockPos adjacentPos : adjacentPositions) {
				BlockState adjacentBlockState = world.getBlockState(adjacentPos);

				// Check if the adjacent block is of the same type
				if (adjacentBlockState.getBlock() == state.getBlock()) {
					// Perform the desired action with the adjacent block
					// For example, you can modify the adjacent block state
					int blocks_broken = breakAdjacentBlocksInAllDirections(world, pos, state, MAX_BLOCKS);

					HungerManager manager = player.getHungerManager();
					manager.setFoodLevel(manager.getFoodLevel() - (int) Math.floor(blocks_broken * HUNGER));
				}
			}
		}
	}

	private boolean validName(ItemStack tool) {
		String name = tool.getName().getString();

		JsonArray validNames = CONFIG.getOrDefaultJsonArray("names", new JsonArray());

		for(JsonElement element : validNames) {
			if(element.toString().replace("\"", "").equals(name)) return true;
		}

		return false;
	}

	private boolean validItem(ItemStack tool) {
		String name = tool.getItem().getTranslationKey();

		JsonArray validItems = CONFIG.getOrDefaultJsonArray("items", new JsonArray());

		for(JsonElement element : validItems) {
			if(("item.minecraft." + element.toString().replace("\"", "")).equals(name)) return true;
		}

		return false;
	}

	private boolean validBlock(BlockState block) {
		String name = block.getBlock().getTranslationKey();

		JsonArray validItems = CONFIG.getOrDefaultJsonArray("blocks", new JsonArray());

		for(JsonElement element : validItems) {
			if(("block.minecraft." + element.toString().replace("\"", "")).equals(name)) return true;
		}

		return false;
	}

	private BlockPos[] getNeighbours(BlockPos pos) {
		BlockPos[] neighborPositions = new BlockPos[26];
		int index = 0;

		for (int xOffset = -1; xOffset <= 1; xOffset++) {
			for (int yOffset = -1; yOffset <= 1; yOffset++) {
				for (int zOffset = -1; zOffset <= 1; zOffset++) {
					if (xOffset == 0 && yOffset == 0 && zOffset == 0) {
						continue;  // Skip the current position
					}
					neighborPositions[index] = pos.add(xOffset, yOffset, zOffset);
					index++;
				}
			}
		}

		return neighborPositions;
	}

	// Recursive function to break adjacent blocks in all directions with a maximum count
	private int breakAdjacentBlocksInAllDirections(World world, BlockPos pos, BlockState targetBlock, int maxBlocksToBreak) {
		if (maxBlocksToBreak <= 0) {
			return 0;  // Stop breaking blocks if the maximum count is reached
		}

		int blocksBroken = 0;

		// Define all directions for adjacent positions
		BlockPos[] neighbours = getNeighbours(pos);

		for (BlockPos adjacentPos : neighbours) {
			if (blocksBroken >= maxBlocksToBreak) {
				break;  // Stop if the maximum count is reached
			}

			BlockState adjacentBlockState = world.getBlockState(adjacentPos);

			if (adjacentBlockState.getBlock() == targetBlock.getBlock()) {
				// Break the block at the adjacent position
				world.breakBlock(adjacentPos, true);
				blocksBroken++;

				// Recursively search for adjacent blocks in the current direction
				blocksBroken += breakAdjacentBlocksInAllDirections(world, adjacentPos, targetBlock, maxBlocksToBreak - blocksBroken);
			}
		}

		return blocksBroken;
	}
}