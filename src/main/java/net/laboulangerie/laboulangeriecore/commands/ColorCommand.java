package net.laboulangerie.laboulangeriecore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ColorCommand implements CommandExecutor {

    private static final Object[][] COLOR_DATA = {
        {ChatColor.BLACK, "&0", ChatColor.DARK_GRAY, "&8"},
        {ChatColor.DARK_BLUE, "&1", ChatColor.BLUE, "&9"},
        {ChatColor.DARK_GREEN, "&2", ChatColor.GREEN, "&a"},
        {ChatColor.DARK_AQUA, "&3", ChatColor.AQUA, "&b"},
        {ChatColor.DARK_RED, "&4", ChatColor.RED, "&c"},
        {ChatColor.DARK_PURPLE, "&5", ChatColor.LIGHT_PURPLE, "&d"},
        {ChatColor.GOLD, "&6", ChatColor.YELLOW, "&e"},
        {ChatColor.GRAY, "&7", ChatColor.WHITE, "&f"}
    };

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
            @NotNull String alias, @NotNull String[] args) {

        sender.sendMessage(ChatColor.GOLD + "=== Couleurs Minecraft ===");

        for (Object[] row : COLOR_DATA) {
            ChatColor leftColor = (ChatColor) row[0];
            String leftCode = (String) row[1];
            ChatColor rightColor = (ChatColor) row[2];
            String rightCode = (String) row[3];

            String left = leftColor + "■ " + leftCode + " " + leftColor.name();
            String right = rightColor + "■ " + rightCode + " " + rightColor.name();
            sender.sendMessage(left + ChatColor.RESET + "    " + right);
        }

        return true;
    }
}
