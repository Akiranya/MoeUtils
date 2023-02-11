package cc.mewcraft.mewutils.module.furnituredyer;

import com.google.inject.Inject;
import me.lucko.helper.item.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class FurnitureDyeHandler {

    private final Plugin plugin;

    @Inject
    public FurnitureDyeHandler(final Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The entry point of dying furniture.
     *
     * @param player     the player who is dying this furniture
     * @param baseEntity the base entity which holds the furniture item stack
     */
    public void dye(final Player player, final Entity baseEntity) {
        if (!(baseEntity instanceof ArmorStand) && !(baseEntity instanceof ItemFrame)) {
            player.sendRawMessage("该家具不支持染色.");
            return;
        }

        // Get the AnvilGUI Builder
        AnvilGUI.Builder builder = new AnvilGUI.Builder();

        builder.plugin(this.plugin);
        builder.title("输入 RGB Hex");
        builder.itemLeft(ItemStackBuilder.of(Material.PAPER).name("#ff99cc").build());
        builder.onClose(p -> p.sendRawMessage("染色GUI已关闭."));
        builder.onComplete(completion -> {
            String text = completion.getText();

            // Validate input
            Color dyeColor;
            TextColor inputColor = TextColor.fromHexString(text);
            if (inputColor == null) {
                player.sendRawMessage(text + " 不是有效的 RGB Hex. 正确写法: #??????");
                return List.of(AnvilGUI.ResponseAction.close());
            } else {
                dyeColor = Color.fromRGB(inputColor.value());
            }

            // Apply the color
            return List.of(
                AnvilGUI.ResponseAction.run(() -> {
                    if (baseEntity instanceof ArmorStand armorStand) {
                        ItemStack item = armorStand.getItem(EquipmentSlot.HEAD);
                        item.setType(Material.LEATHER_HORSE_ARMOR);
                        item.editMeta(LeatherArmorMeta.class, meta -> meta.setColor(dyeColor));
                    } else {
                        ItemFrame itemFrame = (ItemFrame) baseEntity;
                        ItemStack item = itemFrame.getItem(); // it's a copy
                        item.setType(Material.LEATHER_HORSE_ARMOR);
                        item.editMeta(LeatherArmorMeta.class, meta -> meta.setColor(dyeColor));
                        itemFrame.setItem(item);
                    }
                    player.sendMessage(Component.text()
                        .append(Component.text("家具已染成颜色: "))
                        .append(Component.text("■■■").color(inputColor)));
                }),
                AnvilGUI.ResponseAction.close()
            );
        });
        builder.open(player);
    }

}
