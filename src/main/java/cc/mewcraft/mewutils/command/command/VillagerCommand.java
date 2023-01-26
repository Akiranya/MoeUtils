package cc.mewcraft.mewutils.command.command;

import cc.mewcraft.lib.commandframework.Command;
import cc.mewcraft.lib.commandframework.arguments.standard.EnumArgument;
import cc.mewcraft.lib.commandframework.arguments.standard.IntegerArgument;
import cc.mewcraft.lib.commandframework.bukkit.arguments.selector.MultipleEntitySelector;
import cc.mewcraft.lib.commandframework.bukkit.parsers.PlayerArgument;
import cc.mewcraft.lib.commandframework.bukkit.parsers.selector.MultipleEntitySelectorArgument;
import cc.mewcraft.lib.commandframework.bukkit.parsers.selector.SingleEntitySelectorArgument;
import cc.mewcraft.mewutils.command.AbstractCommand;
import cc.mewcraft.mewutils.command.CommandManager;
import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

import java.util.HashMap;
import java.util.Optional;

public class VillagerCommand extends AbstractCommand {
    public VillagerCommand(final CommandManager manager) {
        super(manager);
    }

    @Override public void register() {
        Command.Builder<CommandSender> builder = commandManager
            .commandBuilder("mewutils")
            .literal("villager")
            .permission("mew.admin")
            .senderType(Player.class);

        Command<CommandSender> setRestocksToday = builder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("selector"))
            .literal("restockstoday")
            .argument(IntegerArgument.of("amount"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                int amount = commandContext.get("amount");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setRestocksToday(amount);
                }
            }).build();

        Command<CommandSender> setType = builder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("entities"))
            .literal("type")
            .argument(EnumArgument.of(Villager.Type.class, "type"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                Villager.Type type = commandContext.get("type");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setVillagerType(type);
                }
            }).build();

        Command<CommandSender> setLevel = builder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("entities"))
            .literal("level")
            .argument(IntegerArgument.of("level"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                int level = commandContext.get("level");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setVillagerLevel(level);
                }
            }).build();

        Command<CommandSender> setExp = builder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("entities"))
            .literal("exp")
            .argument(IntegerArgument.of("exp"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                int exp = commandContext.get("exp");
                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setVillagerExperience(exp);
                }
            }).build();

        Command<CommandSender> setProfession = builder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("entities"))
            .literal("profession")
            .argument(EnumArgument.of(Villager.Profession.class, "profession"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                Villager.Profession profession = commandContext.get("profession");

                for (Entity entity : selector.getEntities()) {
                    if (entity instanceof Villager villager)
                        villager.setProfession(profession);
                }
            }).build();

        Command<CommandSender> setReputation = builder
            .literal("set")
            .argument(MultipleEntitySelectorArgument.of("entities"))
            .literal("reputation")
            .argument(PlayerArgument.of("player"))
            .argument(EnumArgument.of(ReputationType.class, "reputationType"))
            .argument(IntegerArgument.of("reputationValue"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");
                Player player = commandContext.get("player");
                ReputationType repType = commandContext.get("reputationType");
                int repValue = commandContext.get("reputationValue");

                selector.getEntities().stream()
                    .filter(entity -> entity instanceof Villager)
                    .map(entity -> (Villager) entity)
                    .forEach(villager -> {
                        Reputation playerRep = Optional
                            .ofNullable(villager.getReputation(player.getUniqueId()))
                            .orElseGet(() -> new Reputation(new HashMap<>()));
                        playerRep.setReputation(repType, repValue);
                        villager.setReputation(player.getUniqueId(), playerRep);
                    });
            }).build();

        Command<CommandSender> restock = builder
            .literal("restock")
            .argument(MultipleEntitySelectorArgument.of("entities"))
            .handler(commandContext -> {
                MultipleEntitySelector selector = commandContext.get("selector");

                selector.getEntities().stream()
                    .filter(entity -> entity instanceof Villager)
                    .map(entity -> (Villager) entity)
                    .forEach(villager -> {
                        for (MerchantRecipe recipe : villager.getRecipes()) recipe.setUses(0);
                    });
            }).build();

        Command<CommandSender> view = builder
            .literal("view")
            .argument(SingleEntitySelectorArgument.of("entity"))
            .argument(PlayerArgument.of("player"))
            .handler(commandContext -> {
                Object entity = commandContext.get("entity");
                Player player = commandContext.get("player");
                CommandSender sender = commandContext.getSender();

                if (entity instanceof Villager villager) {
                    sender.sendMessage("");
                    sender.sendMessage("Type: " + villager.getVillagerType().name());
                    sender.sendMessage("Level: " + villager.getVillagerLevel());
                    sender.sendMessage("Profession: " + villager.getProfession().name());
                    sender.sendMessage("RestocksToday: " + villager.getRestocksToday());
                    sender.sendMessage("Reputation:");
                    Reputation rep = villager.getReputation(player.getUniqueId());
                    if (rep == null) return;
                    for (ReputationType type : ReputationType.values()) {
                        sender.sendMessage(type + " : " + rep.getReputation(type));
                    }
                }
            }).build();

        commandManager.register(
            setRestocksToday,
            setLevel,
            setExp,
            setReputation,
            setProfession,
            restock,
            view
        );
    }
}
