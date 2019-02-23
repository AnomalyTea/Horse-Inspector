package com.anomalytea.HorseInspector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter {

  private static final String[] COMMANDS = {
      "reload",
  };

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String label, String[] args) {
    final List<String> commands = new ArrayList<>(Arrays.asList(COMMANDS));
    List<String> matches = new ArrayList<>();
    StringUtil.copyPartialMatches(args[0], commands, matches);
    Collections.sort(matches);
    return matches;
  }
}
