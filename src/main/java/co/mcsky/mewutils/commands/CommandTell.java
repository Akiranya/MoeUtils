package co.mcsky.mewutils.commands;

import co.mcsky.mewutils.MewUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelector;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public class CommandTell implements Supplier<CommandAPICommand> {
    @Override
    public CommandAPICommand get() {
        return new CommandAPICommand("tell")
                .withPermission("mew.admin")
                .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                .withArguments(new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    final Player p = (Player) args[0];
                    final String m = (String) args[1];
                    p.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(p, m)));
                });

    }
}
