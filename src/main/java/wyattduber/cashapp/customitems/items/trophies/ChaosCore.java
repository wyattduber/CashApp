package wyattduber.cashapp.customitems.items.trophies;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChaosCore {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta meta = item.getItemMeta();
        var mm = MiniMessage.miniMessage();

        // Set Name
        meta.displayName(mm.deserialize("<gradient:#9600ff:#96ffff>Chaos Core</gradient>!"));

        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A reward for killing a Hunter. Somewhat useless except for decoration", TextColor.fromHexString("#4f1880")));
        lore.add(Component.text("but a very unique way to show your strength.", TextColor.fromHexString("#4f1880")));
        lore.add(Component.text("But also can make you at target. Hope you are ready for the storm to come.", TextColor.fromHexString("#4f1880")));
        meta.lore(lore);

        // Set Enchantment Glow
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set the finished modified meta and set the egg item to the created item
        item.setItemMeta(meta);
        return item;
    }

}