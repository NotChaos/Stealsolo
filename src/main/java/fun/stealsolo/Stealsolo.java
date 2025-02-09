package fun.stealsolo;

import fun.stealsolo.commands.NightvisionCommand;
import fun.stealsolo.events.InventoryClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Stealsolo extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new InventoryClickEvent(), this); // you forgot this sir quacks a lot
        getCommand("nv").setExecutor(new NightvisionCommand());
    }
}