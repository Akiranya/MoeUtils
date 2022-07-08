package co.mcsky.moeutils;

import co.mcsky.moeutils.chat.CustomPrefix;
import co.mcsky.moeutils.chat.CustomSuffix;
import co.mcsky.moeutils.data.Datasource;
import co.mcsky.moeutils.foundores.FoundOres;
import co.mcsky.moeutils.magic.MagicTime;
import co.mcsky.moeutils.magic.MagicWeather;
import co.mcsky.moeutils.magic.TimeOption;
import co.mcsky.moeutils.magic.WeatherOption;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelector;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MoeCommands {

    public MoeCommands(Datasource datasource, CustomPrefix prefix, CustomSuffix suffix, FoundOres ores, MagicTime time, MagicWeather weather) {

        CommandAPICommand reloadCommand = new CommandAPICommand("reload")
                .withPermission("moe.admin")
                .executes((sender, args) -> {
                    MoeUtils.plugin.reload();
                    sender.sendMessage(MoeUtils.text("common.reloaded"));
                });

        CommandAPICommand versionCommand = new CommandAPICommand("version")
                .withPermission("moe.admin")
                .executes((sender, args) -> {
                    sender.sendMessage(MoeUtils.text("common.version", "version", MoeUtils.plugin.getDescription().getVersion()));
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
                        .withPermission("moe.admin")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(weather.getLastPlayers());
                        }))
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("moe.admin")
                        .executesPlayer((sender, args) -> {
                            weather.resetCooldown();
                            sender.sendMessage(MoeUtils.text("common.reset"));
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
                        .withPermission("moe.admin")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(weather.getLastPlayers());
                        }))
                .withSubcommand(new CommandAPICommand("reset")
                        .withPermission("moe.admin")
                        .executesPlayer((sender, args) -> {
                            weather.resetCooldown();
                            sender.sendMessage(MoeUtils.text("common.reset"));
                        }));

        CommandAPICommand toggleCommand = new CommandAPICommand("toggle")
                .withSubcommand(new CommandAPICommand("mining-notification")
                        .executesPlayer((sender, args) -> {
                            if (ores.isListener(sender)) {
                                ores.toggleBroadcast(sender);
                                sender.sendMessage(MoeUtils.text("found-ores.toggle-broadcast-off"));
                            } else {
                                ores.toggleBroadcast(sender);
                                sender.sendMessage(MoeUtils.text("found-ores.toggle-broadcast-on"));
                            }
                        }));

        CommandAPICommand portalCommand = new CommandAPICommand("portal")
                .withPermission("moe.admin")
                .withSubcommand(new CommandAPICommand("set")
                        .executesPlayer((sender, args) -> {
                            final Location location = sender.getLocation().toBlockLocation();
                            datasource.getEndPortals().addEndEyeTargetLocation(location);
                            sender.sendMessage(MoeUtils.text("custom-ender-eye.set", "location", location.toBlockLocation().toString()));
                        }))
                .withSubcommand(new CommandAPICommand("list")
                        .executesPlayer((sender, args) -> {
                            sender.sendMessage(MoeUtils.text("custom-ender-eye.list-title"));
                            for (Location location : datasource.getEndPortals().getEndEyeTargetLocations()) {
                                sender.sendMessage(MoeUtils.text("custom-ender-eye.list", "location", location.toBlockLocation().toString()));
                            }
                        }))
                .withSubcommand(new CommandAPICommand("clear")
                        .executesPlayer((sender, args) -> {
                            datasource.getEndPortals().clearTargetLocations();
                            sender.sendMessage(MoeUtils.text("custom-ender-eye.clear"));
                        }));

        CommandAPICommand tellCommand = new CommandAPICommand("tell")
                .withPermission("moe.admin")
                .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    final Player p = (Player) args[0];
                    final String m = (String) args[1];
                    p.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(p, m)));
                });

        new CommandAPICommand("mu")
                .withAliases("moeutils")
                .withSubcommand(reloadCommand)
                .withSubcommand(versionCommand)
                .withSubcommand(prefixCommand)
                .withSubcommand(suffixCommand)
                .withSubcommand(timeCommand)
                .withSubcommand(weatherCommand)
                .withSubcommand(toggleCommand)
                .withSubcommand(portalCommand)
                .withSubcommand(tellCommand)
                .register();

    }

}
