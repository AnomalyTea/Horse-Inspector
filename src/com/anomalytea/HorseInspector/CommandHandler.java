package com.anomalytea.HorseInspector;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandHandler implements CommandExecutor {

  private HorseInspector plugin;

  public CommandHandler(HorseInspector plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    // Require OP permissions to execute command
    if (sender instanceof Player && !((Player) sender).isOp()) {
      ((Player) sender).sendMessage(ChatColor.GREEN +
          "Error: This command can only be issued by an OP.");
      return true;
    }

    // Catch reload command
    if (args.length >= 1 && args[0].equals("reload")) {
      // Load config and store response
      ArrayList<String> msg = plugin.loadConfig();

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
