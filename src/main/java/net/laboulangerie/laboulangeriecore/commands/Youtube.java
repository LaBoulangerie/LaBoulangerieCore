package net.laboulangerie.laboulangeriecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class Youtube implements CommandExecutor{
	
	private LaBoulangerieCore plugin;
	public Youtube (LaBoulangerieCore plugin) {
		this.plugin = plugin;
		plugin.getConfig();

	}
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		String link = plugin.getConfig().getString("links.youtube");
		sender.sendMessage(link);
		return true;
	}

}
