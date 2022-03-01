package net.laboulangerie.laboulangeriecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import net.laboulangerie.laboulangeriecore.LaBoulangerieCore;

public class Discord implements CommandExecutor{
	
	private LaBoulangerieCore plugin;
	private FileConfiguration config;
	
	public Discord (LaBoulangerieCore plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();

	}
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
			@NotNull String[] args) {
		String link = plugin.getConfig().getString("links.discord");
		sender.sendMessage(link);
		return true;
	}

}
