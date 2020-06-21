package com.anomalytea.HorseInspector;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor {

  private HorseInspector plugin;

  public CommandHandler(HorseInspector plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    // Check permissions
    if (sender instanceof Player && !sender.hasPermission("horseinspector.reload")) {
      sender.sendMessage(ChatColor.GREEN + "Error: You do not have permissions to do that.");
      return true;
    }

    // Catch reload command
    if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
      // Load config and store response
      List<String> msg = plugin.loadConfig();

      // If issue by player, send response (if any) to player
      if (sender instanceof Player) {
        for (String m : msg) {
          ((Player) sender).sendMessage(ChatColor.GREEN + m);
        }
      }

      return true;
    }

    return false;
  }

}
