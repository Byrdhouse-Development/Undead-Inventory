package com.undeadinventory;

import org.bukkit.plugin.java.JavaPlugin;

public class UndeadInventory extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
    }
    @Override
    public void onDisable() {
    }
}
