package co.mcsky;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

    MoeUtils plugin;

    public Commands(MoeUtils plugin) {
        this.plugin = plugin;
        plugin.getCommand("moeutils").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getMoeConfig().loadFile(plugin);
            plugin.onDisable();
            plugin.onEnable();
            sender.sendMessage(plugin.getMoeConfig().globalReloaded);
            return true;
        }

        return false;
    }
}
