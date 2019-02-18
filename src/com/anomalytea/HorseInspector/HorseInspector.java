package com.anomalytea.HorseInspector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class HorseInspector extends JavaPlugin {

	private boolean configShowTamer;
	private Material configItem;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		loadConfig();
		this.saveConfig();

		getServer().getPluginManager().registerEvents(new PlayerAttackHorseListener(this), this);

		this.getCommand("horseinspector").setExecutor(new CommandHandler(this));
		this.getCommand("horseinspector").setTabCompleter(new TabComplete());

		checkForUpdate();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void checkForUpdate() {
		String tag = "[" + this.getDescription().getName() + "] ";
		try {
			URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=64721");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String v = rd.readLine();
			rd.close();
			if (v.equals(this.getDescription().getVersion())) {
				System.out.println(tag + "Version is up-to-date.");
			} else {
				System.out.println(tag + "Updated version available: " + v);
			}
		} catch (IOException e) {
			System.out.println(tag + "An error occurred while checking for updates.");
		}
	}

	public ArrayList<String> loadConfig() {

		ArrayList<String> msg = new ArrayList<>();

		this.reloadConfig();

		this.configShowTamer = this.getConfig().getBoolean("show-tamer");

		this.configItem = Material.matchMaterial(this.getConfig().getString("item"));
		if (this.configItem == null) {
			this.configItem = Material.STICK;
			msg.add("[" + this.getDescription().getName() + "] Error reading config: invalid item. Using STICK instead.");
			System.out.println(msg.get(msg.size() - 1));
		}

		msg.add("[" + this.getDescription().getName() + "] Config file loaded.");
		System.out.println(msg.get(msg.size() - 1));

		return msg;
	}

	public boolean getConfigShowTamer() {
		return configShowTamer;
	}

	public Material getConfigItem() {
		return configItem;
	}

}
