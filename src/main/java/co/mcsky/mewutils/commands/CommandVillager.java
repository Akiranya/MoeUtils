package co.mcsky.mewutils.commands;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class CommandVillager implements Supplier<CommandAPICommand[]> {

    private static final ArgumentSuggestions TYPE = ArgumentSuggestions.strings(Arrays.stream(Villager.Type.values()).map(Enum::name).toArray(String[]::new));
    private static final ArgumentSuggestions PROFESSION = ArgumentSuggestions.strings(Arrays.stream(Villager.Profession.values()).map(Enum::name).toArray(String[]::new));
    private static final ArgumentSuggestions REPUTATION = ArgumentSuggestions.strings(Arrays.stream(ReputationType.values()).map(Enum::name).toArray(String[]::new));

    @Override
    public CommandAPICommand[] get() {

        final CommandAPICommand setRestocksToday = new CommandAPICommand("set")
                .withPermission("mew.admin.villager.set")
                .withArguments(new EntitySelectorArgument<Villager>("villagers", EntitySelector.MANY_ENTITIES))
                .withArguments(new MultiLiteralArgument("restockstoday").setListed(false))
                .withArguments(new IntegerArgument("amount"))
                .executesPlayer((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Villager> villagers = (Collection<Villager>) args[0];
                    for (Villager villager : villagers) {
                        villager.setRestocksToday((int) args[1]);
                    }
                });

        final CommandAPICommand setType = new CommandAPICommand("set")
                .withPermission("mew.admin.villager.set")
                .withArguments(new EntitySelectorArgument<Villager>("villagers", EntitySelector.MANY_ENTITIES))
                .withArguments(new MultiLiteralArgument("type").setListed(false))
                .withArguments(new StringArgument("type").replaceSuggestions(TYPE))
                .executesPlayer((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Villager> villagers = (Collection<Villager>) args[0];
                    for (Villager villager : villagers) {
                        villager.setVillagerType(Villager.Type.valueOf((String) args[1]));
                    }
                });

        final CommandAPICommand setLevel = new CommandAPICommand("set")
                .withPermission("mew.admin.villager.set")
                .withArguments(new EntitySelectorArgument<Villager>("villagers", EntitySelector.MANY_ENTITIES))
                .withArguments(new MultiLiteralArgument("level").setListed(false))
                .withArguments(new IntegerArgument("level"))
                .executesPlayer((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Villager> villagers = (Collection<Villager>) args[0];
                    for (Villager villager : villagers) {
                        villager.setVillagerLevel((int) args[1]);
                    }
                });

        final CommandAPICommand setExp = new CommandAPICommand("set")
                .withPermission("mew.admin.villager.set")
                .withArguments(new EntitySelectorArgument<Villager>("villagers", EntitySelector.MANY_ENTITIES))
                .withArguments(new MultiLiteralArgument("exp").setListed(false))
                .withArguments(new IntegerArgument("exp"))
                .executesPlayer((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Villager> villagers = (Collection<Villager>) args[0];
                    for (Villager villager : villagers) {
                        villager.setVillagerExperience((int) args[1]);
                    }
                });

        final CommandAPICommand setProfession = new CommandAPICommand("set")
                .withPermission("mew.admin.villager.set")
                .withArguments(new EntitySelectorArgument<Villager>("villagers", EntitySelector.MANY_ENTITIES))
                .withArguments(new MultiLiteralArgument("profession").setListed(false))
                .withArguments(new StringArgument("profession").replaceSuggestions(PROFESSION))
                .executesPlayer((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Villager> villagers = (Collection<Villager>) args[0];
                    for (Villager villager : villagers) {
                        villager.setProfession(Villager.Profession.valueOf((String) args[1]));
                    }
                });

        final CommandAPICommand setReputation = new CommandAPICommand("set")
                .withPermission("mew.admin.villager.set")
                .withArguments(new EntitySelectorArgument<Villager>("villagers", EntitySelector.MANY_ENTITIES))
                .withArguments(new MultiLiteralArgument("reputation").setListed(false))
                .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                .withArguments(new StringArgument("reputationType").replaceSuggestions(REPUTATION))
                .withArguments(new IntegerArgument("reputation"))
                .executesPlayer((sender, args) -> {
                    @SuppressWarnings("unchecked")
                    Collection<Villager> villagers = (Collection<Villager>) args[0];
                    Player player = (Player) args[1];
                    ReputationType reputationType = ReputationType.valueOf((String) args[2]);
                    for (Villager villager : villagers) {
                        Reputation reputation = villager.getReputation(player.getUniqueId());
                        int reputationValue = (int) args[3];
                        if (reputation == null) {
                            villager.setReputation(player.getUniqueId(), new Reputation(Map.of(reputationType, reputationValue)));
                        } else {
                            reputation.setReputation(reputationType, reputationValue);
                            villager.setReputation(player.getUniqueId(), reputation);
                        }
                    }
                });

        final CommandAPICommand view = new CommandAPICommand("view")
                .withPermission("mew.user.villager.view")
                .withArguments(new EntitySelectorArgument<Villager>("villager", EntitySelector.ONE_ENTITY))
                .withArguments(new EntitySelectorArgument<Player>("player", EntitySelector.ONE_PLAYER))
                .executesPlayer((sender, args) -> {
                    Villager villager = (Villager) args[0];
                    Player player = (Player) args[1];
                    sender.sendMessage("");
                    sender.sendMessage("Type: " + villager.getVillagerType().name());
                    sender.sendMessage("Level: " + villager.getVillagerLevel());
                    sender.sendMessage("Profession: " + villager.getProfession().name());
                    sender.sendMessage("RestocksToday: " + villager.getRestocksToday());
                    sender.sendMessage("Reputation:");
                    Reputation reputation = villager.getReputation(player.getUniqueId());
                    if (reputation == null) return;
                    for (ReputationType type : ReputationType.values()) {
                        sender.sendMessage(type + " : " + reputation.getReputation(type));
                    }
                });

        final CommandAPICommand villager = new CommandAPICommand("villager").withSubcommands(
                setExp,
                setLevel,
                setProfession,
                setType,
                setReputation,
                setRestocksToday,
                view);

        return new CommandAPICommand[]{villager};
    }
}
