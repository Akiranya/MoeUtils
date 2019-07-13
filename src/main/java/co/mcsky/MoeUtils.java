package co.mcsky;

import co.mcsky.mobarena.ArenaEventListener;
import co.mcsky.safeportal.PlayerTeleportListener;
import com.garbagemule.MobArena.MobArena;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MoeUtils extends JavaPlugin {
    private MobArena mobarena;
    private Configuration configuration;

    @Override
    public void onDisable() {
        // Save configuration to disk. It will destroy any data in memory.
        this.saveConfig();
    }

    @Override
    public void onEnable() {
        // Save a copy of the default configuration.yml if one is not there
        this.saveDefaultConfig();

        configuration = new Configuration(this);
        if (configuration.isMobArenaEnable) {
            setupMobArena();
            if (mobarena != null) {
                new ArenaEventListener(this, mobarena, new CustomPlayerName());
            }
        }
        if (configuration.isSafePortalEnable) {
            getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);
        }
    }

    public Configuration getMoeConfig() {
        return configuration;
    }

    private void setupMobArena() {
        Plugin plugin = getServer().getPluginManager().getPlugin("MobArena");
        this.mobarena = plugin != null ? (MobArena) plugin : null;
    }

}
