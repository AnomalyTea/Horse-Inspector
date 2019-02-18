package com.anomalytea.HorseInspector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class HorseInspector extends JavaPlugin {

	private boolean configShowTamer;
	private Material configItem;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		loadConfig();

		getServer().getPluginManager().registerEvents(new PlayerAttackHorseListener(this), this);
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

	public void loadConfig() {
		this.configShowTamer = this.getConfig().getBoolean("show-tamer");

		this.configItem = Material.matchMaterial(this.getConfig().getString("item"));

		if (this.configItem == null) {
			this.configItem = Material.STICK;
			System.out.println("[" + this.getDescription().getName() + "] Error reading config: invalid item. Using STICK instead.");
		}

		this.saveConfig();
	}

	public boolean getConfigShowTamer() {
		return configShowTamer;
	}

	public Material getConfigItem() {
		return configItem;
	}

}
