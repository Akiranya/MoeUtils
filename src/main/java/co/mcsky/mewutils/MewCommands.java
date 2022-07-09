package co.mcsky.mewutils;

import co.mcsky.mewutils.chat.CustomPrefix;
import co.mcsky.mewutils.chat.CustomSuffix;
import co.mcsky.mewutils.foundores.FoundOres;
import co.mcsky.mewutils.magic.MagicTime;
import co.mcsky.mewutils.magic.MagicWeather;
import co.mcsky.mewutils.magic.TimeOption;
import co.mcsky.mewutils.magic.WeatherOption;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelector;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class MewCommands {

    public MewCommands(CustomPrefix prefix, CustomSuffix suffix, FoundOres ores, MagicTime time, MagicWeather weather) {

        CommandAPICommand reloadCommand = new CommandAPICommand("reload")
                .withPermission("mew.admin")
                .executes((sender, args) -> {
                    MewUtils.plugin.reload();
                    sender.sendMessage(MewUtils.text("common.reloaded"));
                });

        CommandAPICommand versionCommand = new CommandAPICommand("version")
                .withPermission("mew.admin")
                .executes((sender, args) -> {
                    sender.sendMessage(MewUtils.text("common.version", "version", MewUtils.plugin.getDescription().getVersion()));
                });

        CommandAPICommand prefixCommand = new CommandAPICommand("prefix")
                .withArguments(new GreedyStringArgument("prefix"))
                .executesPlayer((sender, args) -> {
                    String p = (String) args[0];
                    if (p.equalsIgnoreCase("clear")) {
                        prefix.clear(sender);
                    } else {
                        prefix.set(sender, p);
                    }
                });

        CommandAPICommand suffixCommand = new CommandAPICommand("suffix")
                .withArguments(new GreedyStringArgument("suffix"))
                .executesPlayer((sender, args) -> {
                    String p = (String) args[0];
                    if (p.equalsIgnoreCase("clear")) {
                        suffix.clear(sender);
                    } else {
                        suffix.set(sender, p);
                    }
                });

        CommandAPICommand timeCommand = new CommandAPICommand("time")
                .withSubcommand(new CommandAPICommand("day")
                        .executesPlayer((sender, args) -> {
                            time.call(TimeOption.DAY, sender);
                        }))
                .withSubcommand(new CommandAPICommand("night")
                        .executesPlayer((sender, args) -> {
                            time.call(TimeOption.NIGHT, sender);
                        }))
                .withSubcommand(new CommandAPICommand("status")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(weather.getLastPlayers());
                        }))
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            weather.resetCooldown();
                            sender.sendMessage(MewUtils.text("common.reset"));
                        }));

        CommandAPICommand weatherCommand = new CommandAPICommand("weather")
                .withSubcommand(new CommandAPICommand("sun")
                        .executesPlayer((sender, args) -> {
                            weather.call(WeatherOption.CLEAR, sender);
                        }))
                .withSubcommand(new CommandAPICommand("rain")
                        .executesPlayer((sender, args) -> {
                            weather.call(WeatherOption.RAIN, sender);
                        }))
                .withSubcommand(new CommandAPICommand("thunder")
                        .executesPlayer((sender, args) -> {
                            weather.call(WeatherOption.THUNDER, sender);
                        }))
                .withSubcommand(new CommandAPICommand("status")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(weather.getLastPlayers());
                        }))
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("mew.admin")
                        .executesPlayer((sender, args) -> {
                            weather.resetCooldown();
                            sender.sendMessage(MewUtils.text("common.reset"));
                        }));

        CommandAPICommand toggleCommand = new CommandAPICommand("toggle")
                .withSubcommand(new CommandAPICommand("mining-notification")
                        .executesPlayer((sender, args) -> {
                            if (ores.isListener(sender)) {
                                ores.toggleBroadcast(sender);
                                sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-off"));
                            } else {
                                ores.toggleBroadcast(sender);
                                sender.sendMessage(MewUtils.text("found-ores.toggle-broadcast-on"));
                            }
                        }));

        CommandAPICommand tellCommand = new CommandAPICommand("tell")
                .withPermission("mew.admin")
                .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    final Player p = (Player) args[0];
                    final String m = (String) args[1];
                    p.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(p, m)));
                });

        new CommandAPICommand("mu")
                .withAliases("mewutils")
                .withSubcommand(reloadCommand)
                .withSubcommand(versionCommand)
                .withSubcommand(prefixCommand)
                .withSubcommand(suffixCommand)
                .withSubcommand(timeCommand)
                .withSubcommand(weatherCommand)
                .withSubcommand(toggleCommand)
                .withSubcommand(tellCommand)
                .register();

    }

}
