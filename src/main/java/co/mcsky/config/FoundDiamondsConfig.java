package co.mcsky.config;

import co.mcsky.config.converter.MaterialConverter;
import co.mcsky.config.converter.StringConverter;
import lombok.Getter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.InvalidConverterException;
import net.cubespace.Yamler.Config.Path;
import net.cubespace.Yamler.Config.YamlConfig;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FoundDiamondsConfig extends YamlConfig {

    @Comment("Enable this feature?")
    public boolean enable = true;
    @Comment("Max search attempts. Higher values may cause more lag.")
    public int maxIterations = 32;
    @Comment("The interval (in second) between this plugin resets location history.")
    public int purgeInterval = 1800;
    @Comment("List of worlds where you want to enable announcement.")
    public java.util.Set<String> worlds = new HashSet<>() {{
        add("world");
        add("world_nether");
        add("world_the_end");
    }};
    @Comment("List of blocks which need to be announced.")
    public Set<Material> blocks = new HashSet<>() {{
        add(Material.GOLD_ORE);
        add(Material.IRON_ORE);
        add(Material.COAL_ORE);
        add(Material.LAPIS_ORE);
        add(Material.DIAMOND_ORE);
        add(Material.REDSTONE_ORE);
        add(Material.EMERALD_ORE);
        add(Material.NETHER_QUARTZ_ORE);
    }};
    @Path("messages.prefix")
    public String msg_prefix = "&8[&e矿工茶馆&8]&r ";
    @Path("messages.found")
    public String msg_found = "%s&7 挖到了 &e%d&7 个 &r&l%s!";

    public FoundDiamondsConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of FoundDiamonds."};
        CONFIG_FILE = new File(plugin.getDataFolder(), "found_diamonds.yml");
        try {
            addConverter(StringConverter.class);
            addConverter(MaterialConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

}
