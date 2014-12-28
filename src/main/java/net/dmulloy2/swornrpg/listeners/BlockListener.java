package net.dmulloy2.swornrpg.listeners;

import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.BlockDrop;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author dmulloy2
 */

public class BlockListener implements Listener, Reloadable
{
	private boolean blockDropsEnabled;
	private boolean ironDoorProtection;
	private boolean redemptionEnabled;

	private List<Material> redemptionBlacklist;

	private final SwornRPG plugin;
	public BlockListener(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.reload(); // Load configuration
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakMonitor(BlockBreakEvent event)
	{
		if (! blockDropsEnabled || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (plugin.isSwornNationsEnabled() && plugin.getSwornNationsHandler().isApplicable(player, true))
			return;

		Block block = event.getBlock();
		Material type = block.getType();

		// Block drops
		if (plugin.getBlockDropsMap().containsKey(type))
		{
			for (BlockDrop blockDrop : plugin.getBlockDropsMap().get(type))
			{
				if (Util.random(blockDrop.getChance()) == 0)
				{
					block.getWorld().dropItemNaturally(block.getLocation(), blockDrop.getMaterial().newItemStack(1));
				}
			}

			if (plugin.getBlockDropsMap().containsKey(Material.AIR))
			{
				for (BlockDrop blockDrop : plugin.getBlockDropsMap().get(Material.AIR))
				{
					if (Util.random(blockDrop.getChance()) == 0)
					{
						block.getWorld().dropItemNaturally(block.getLocation(), blockDrop.getMaterial().newItemStack(1));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreakHighest(BlockBreakEvent event)
	{
		if (! ironDoorProtection || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block))
			return;

		// Iron door protection
		if (block.getType() == Material.IRON_DOOR_BLOCK)
		{
			event.setCancelled(true);

			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("iron_door_protect")));
			plugin.debug(plugin.getMessage("log_irondoor_protect"), player.getName(), Util.locationToString(block.getLocation()));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled() || ! redemptionEnabled)
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block))
			return;

		Material material = block.getType();
		if (! material.isBlock() || redemptionBlacklist.contains(material))
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		// Block redemption
		int level = data.getLevel(100);
		if (Util.random(300 / level) == 0)
		{
			ItemStack itemStack = new ItemStack(material);
			MaterialData materialData = block.getState().getData();
			if (materialData != null)
				itemStack.setData(materialData);

			InventoryUtil.giveItem(player, itemStack);

			String itemName = FormatUtil.getFriendlyName(itemStack.getType());
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("building_redeem"), itemName));
		}
	}

	@Override
	public void reload()
	{
		this.blockDropsEnabled = plugin.getConfig().getBoolean("blockDropsEnabled");
		this.ironDoorProtection = plugin.getConfig().getBoolean("ironDoorProtection");
		this.redemptionEnabled = plugin.getConfig().getBoolean("redemptionEnabled");
		this.redemptionBlacklist = MaterialUtil.fromStrings(plugin.getConfig().getStringList("redemptionBlacklist"));
	}
}