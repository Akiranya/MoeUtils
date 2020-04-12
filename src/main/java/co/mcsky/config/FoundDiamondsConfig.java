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
import java.util.HashMap;
import java.util.HashSet;

public class FoundDiamondsConfig extends YamlConfig {
    public FoundDiamondsConfig(Plugin plugin) {
        CONFIG_HEADER = new String[]{"Configuration of FoundDiamonds."};
        CONFIG_FILE = new File(plugin.getDataFolder(), "found_diamonds.yml");
        try {
            addConverter(MaterialConverter.class);
            addConverter(StringConverter.class);
        } catch (InvalidConverterException e) {
            e.printStackTrace();
        }
    }

    @Getter
    @Comment("Enable this feature?")
    private boolean enable = true;

    @Getter
    @Comment("Max search attempts. Higher values may cause more lag.")
    private int maxIterations = 32;

    @Getter
    @Comment("The interval when to reset block positions.")
    private int purgeInterval = 1800;

    @Getter
    @Comment("List of worlds where you want to enable announcement.")
    private java.util.Set<String> worlds = new HashSet<>() {{
        add("world");
        add("world_nether");
        add("world_the_end");
    }};

    @Getter
    @Comment("Format: \"<block type>: <display name>\"")
    private HashMap<Material, String> blocks = new HashMap<>() {{
        // TODO 使用 LangUtils 代替人工翻译
        put(Material.GOLD_ORE, "&6金矿");
        put(Material.IRON_ORE, "&7铁矿");
        put(Material.COAL_ORE, "&8煤矿");
        put(Material.LAPIS_ORE, "&7铁矿");
        put(Material.DIAMOND_ORE, "&1青金石");
        put(Material.REDSTONE_ORE, "&b钻石");
        put(Material.EMERALD_ORE, "&4红石");
        put(Material.NETHER_QUARTZ_ORE, "&f石英");
    }};

    @Path("messages.prefix")
    public String msg_prefix = "FD";
    @Path("messages.found")
    public String msg_found = "&e%s&7 挖到了 &e%d &7个 %s&b!";

}
