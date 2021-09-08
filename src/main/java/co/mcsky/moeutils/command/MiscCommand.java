package co.mcsky.moeutils.command;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import co.mcsky.moeutils.MoeUtils;
import co.mcsky.moeutils.data.Datasource;
import co.mcsky.moeutils.foundores.FoundOres;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%main")
public class MiscCommand extends BaseCommand {
    @Dependency
    public FoundOres foundOres;
    @Dependency
    public Datasource datasource;

    @Subcommand("toggle mining")
    public void toggleMiningBroadcast(Player player) {
        if (foundOres.isListener(player)) {
            foundOres.toggleBroadcast(player);
            player.sendMessage(MoeUtils.text("found-ores.toggle-broadcast-off"));
        } else {
            foundOres.toggleBroadcast(player);
            player.sendMessage(MoeUtils.text("found-ores.toggle-broadcast-on"));
        }
    }

    @Subcommand("portals")
    @CommandPermission("moe.admin")
    public class PortalChangerCommand extends BaseCommand {
        @Subcommand("set")
        public void set(Player player) {
            final Location location = player.getLocation().toBlockLocation();
            datasource.getEndPortals().addEndEyeTargetLocation(location);
            player.sendMessage(MoeUtils.text("end-eye-changer.set", "location", ACFBukkitUtil.blockLocationToString(location)));
        }

        @Subcommand("list")
        public void list(CommandSender sender) {
            sender.sendMessage(MoeUtils.text("end-eye-changer.list-title"));
            datasource.getEndPortals().getEndEyeTargetLocations().forEach(location -> sender.sendMessage(MoeUtils.text("end-eye-changer.list", "location", ACFBukkitUtil.blockLocationToString(location))));
        }

        @Subcommand("clear")
        public void clear(CommandSender sender) {
            datasource.getEndPortals().clearTargetLocations();
            sender.sendMessage(MoeUtils.text("end-eye-changer.clear"));
        }
    }
}
