package com.anomalytea.HorseInspector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class HorseInspector extends JavaPlugin {

  @Override
  public void onEnable() {
    saveDefaultConfig();
    loadConfig();

    getServer().getPluginManager().registerEvents(
        new PlayerAttackHorseListener(this), this);

    this.getCommand("horseinspector").setExecutor(new CommandHandler(this));
    this.getCommand("horseinspector").setTabCompleter(new TabComplete());

    if (this.getConfig().getBoolean("check-for-updates")) checkForUpdate();
  }

  @Override
  public void onDisable() {

  }

  public void checkForUpdate() {
    try {
      URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=64721");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String v = rd.readLine();
      rd.close();
      if (v.equals(this.getDescription().getVersion())) {
        this.getLogger().log(Level.INFO, "Version is up-to-date.");
      } else {
        this.getLogger().log(Level.INFO, "Updated version available: {0}", v);
      }
    } catch (IOException e) {
      this.getLogger().log(Level.WARNING, "An error occurred while checking for updates.");
    }
  }

  public List<String> loadConfig() {

    List<String> msg = new ArrayList<>();

    this.reloadConfig();

    // fill in any options that are completely missing from config file
    if (!this.getConfig().isSet("show-tamer")) {
      this.getConfig().set(
          "show-tamer", this.getConfig().getDefaults().getBoolean("show-tamer"));
      this.saveConfig();
    }
    if (!this.getConfig().isSet("item")) {
      this.getConfig().set("item", this.getConfig().getDefaults().getString("item"));
      this.saveConfig();
    }
    if (!this.getConfig().isSet("check-for-updates")) {
      this.getConfig().set(
          "check-for-updates", this.getConfig().getDefaults().getBoolean("check-for-updates"));
      this.saveConfig();
    }
    if (!this.getConfig().isSet("item-name")) {
      this.getConfig().set(
          "item-name", this.getConfig().getDefaults().getString("item-name"));
      this.saveConfig();
    }

    // if item can't match, use default
    if (Material.matchMaterial(this.getConfig().getString("item")) == null) {
      this.getConfig().set(
          "item", Material.matchMaterial(this.getConfig().getDefaults().getString("item")));
      msg.add("Error reading config: invalid item. Using "
          + this.getConfig().getDefaults().getString("item")
          + " instead.");
      this.getLogger().log(Level.WARNING, msg.get(msg.size() - 1));
    }

    msg.add("Config file loaded.");
    this.getLogger().log(Level.INFO, msg.get(msg.size() - 1));

    return msg;
  }

}
