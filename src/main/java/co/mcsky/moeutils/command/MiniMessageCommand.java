package co.mcsky.moeutils.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.clip.placeholderapi.PlaceholderAPI;
import me.lucko.helper.utils.Players;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%main")
public class MiniMessageCommand extends BaseCommand {

    @Subcommand("tell")
    @CommandPermission("moe.admin")
    @CommandCompletion("@players @nothing")
    public void tell(CommandSender sender, @Flags("other") Player player, String text) {
        player.sendMessage(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player, text)));
    }

    @Subcommand("bc")
    @CommandPermission("moe.admin")
    @CommandCompletion("@nothing")
    public void broadcast(CommandSender sender, String text) {
        Players.forEach(p -> p.sendMessage(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(p, text))));
    }

    @Subcommand("bcw")
    @CommandPermission("moe.admin")
    @CommandCompletion("@nothing @nothing")
    public void broadcastWorld(CommandSender sender, String world, String text) {
        Players.forEach(p -> {
            if (p.getWorld().getName().equalsIgnoreCase(world)) {
                p.sendMessage(MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(p, text)));
            }
        });
    }
}
