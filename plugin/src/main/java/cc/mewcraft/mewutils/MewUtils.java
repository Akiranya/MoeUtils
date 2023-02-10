package cc.mewcraft.mewutils;

import cc.mewcraft.mewcore.hook.HookChecker;
import cc.mewcraft.mewcore.message.Translations;
import cc.mewcraft.mewutils.announceore.FoundOres;
import cc.mewcraft.mewutils.command.CommandManager;
import cc.mewcraft.mewutils.furniture.FurnitureDyeHandler;
import cc.mewcraft.mewutils.furniture.FurnitureModule;
import cc.mewcraft.mewutils.magic.MagicTime;
import cc.mewcraft.mewutils.magic.MagicWeather;
import cc.mewcraft.mewutils.misc.*;
import cc.mewcraft.mewutils.placeholder.MewUtilsExpansion;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.milkbowl.vault.economy.Economy;

public final class MewUtils extends ExtendedJavaPlugin {

    /* plugin inst */
    public static MewUtils p;

    /* configuration */
    private MewConfig config;

    /* language loader */
    private Translations translations;

    /* eco & perm & chat */
    private Economy economy;

    /* modules */
    private MagicTime magicTime;
    private MagicWeather magicWeather;
    private BetterPortals betterPortals;
    private BetterBees betterBees;
    private DeathLogger deathLogger;
    private SlowElytra slowElytra;
    private FoundOres foundOres;
    private MergeLimit mergeLimit;

    public static void debug(String message) {
        if (MewUtils.config().debug) p.getLogger().warning("[DEBUG] " + message);
    }

    public static void debug(Throwable message) {
        if (MewUtils.config().debug) p.getLogger().warning("[DEBUG] " + message.getMessage());
    }

    public static void log(final String msg) {
        p.getLogger().info(msg);
    }

    public static boolean logModule(String module, boolean status) {
        if (status) {
            p.getComponentLogger().info(Component.text()
                .append(Component.text(module).color(TextColor.fromHexString("#99ffcc")))
                .appendSpace()
                .append(Component.text("is enabled!").asComponent())
                .build()
            );
        } else {
            p.getComponentLogger().info(module + " is disabled!");
        }
        return !status;
    }

    public static MewConfig config() {
        return p.config;
    }

    public static Economy economy() {
        return p.economy;
    }

    public static Translations translations() {
        return p.translations;
    }

    public MagicTime getMagicTime() {
        return magicTime;
    }

    public MagicWeather getMagicWeather() {
        return magicWeather;
    }

    public BetterPortals getBetterPortals() {
        return betterPortals;
    }

    public BetterBees getBetterBees() {
        return betterBees;
    }

    public DeathLogger getDeathLogger() {
        return deathLogger;
    }

    public SlowElytra getSlowElytra() {
        return slowElytra;
    }

    public FoundOres getFoundOres() {
        return foundOres;
    }

    @Override
    protected void load() {

    }

    @Override
    protected void enable() {
        p = this;

        try {
            economy = Services.load(Economy.class);
        } catch (Exception e) {
            getLogger().severe("Failed to hook into Vault!");
            e.printStackTrace();
            disable();
            return;
        }

        translations = new Translations(this);
        config = new MewConfig();
        config.load();
        config.save();

        initModules();

        try {
            new CommandManager(this);
        } catch (Exception e) {
            getLogger().severe("Failed to initialise commands!");
            e.printStackTrace();
        }
    }

    @Override
    protected void disable() {

    }

    public void reload() {
        onDisable();
        onEnable();
    }

    private void initModules() {
        if (HookChecker.hasVault()) {
            magicTime = bindModule(new MagicTime());
            magicWeather = bindModule(new MagicWeather());
        }
        betterPortals = bindModule(new BetterPortals());
        deathLogger = bindModule(new DeathLogger());
        betterBees = bindModule(new BetterBees());
        slowElytra = bindModule(new SlowElytra());
        foundOres = bindModule(new FoundOres());
        mergeLimit = bindModule(new MergeLimit());
        new FurnitureModule(this, new FurnitureDyeHandler(this));
    }

    private void hookExternal() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MewUtilsExpansion().register();
            MewUtils.log("Hooked into PlaceholderAPI");
        }
    }

}
