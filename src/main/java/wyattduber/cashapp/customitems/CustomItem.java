package wyattduber.cashapp.customitems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CustomItem extends ItemStack {

    public CustomItem(String name, TextColor itemNameColor, Material itemMaterial, int itemAmount) {
        create(name, itemNameColor, itemMaterial, itemAmount);
    }

    public CustomItem(Component formattedName, Material itemMaterial, int itemAmount) {
        create(formattedName, itemMaterial, itemAmount);
    }

    private void create(String name, TextColor color, Material material, int amount) {
        this.setType(material);
        this.setAmount(amount);
        ItemMeta meta = this.getItemMeta();

        // Set Name
        meta.displayName(Component.text(name, color));

        // Do the rest of the item meta setting (same for every item)
        setCustomItemMeta(meta);

        // Set the finished modified meta and set the egg item to the created item
        this.setItemMeta(meta);
    }

    private void create(Component formattedName, Material material, int amount) {
        this.setType(material);
        this.setAmount(amount);
        ItemMeta meta = this.getItemMeta();

        // Set Name
        meta.displayName(formattedName);

        // Do the rest of the item meta setting (same for every item)
        setCustomItemMeta(meta);

        // Set the finished modified meta and set the egg item to the created item
        this.setItemMeta(meta);
    }

    private void setCustomItemMeta(ItemMeta meta) {
        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A reward for killing a Hunter. Somewhat useless except for decoration", TextColor.fromHexString("#4f1880")));
        lore.add(Component.text("but a very unique way to show your strength.", TextColor.fromHexString("#4f1880")));
        lore.add(Component.text("But also can make you at target. Hope you are ready for the storm to come.", TextColor.fromHexString("#4f1880")));
        meta.lore(lore);

        // Set Enchantment Glow
        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Modify the NBT data so we know it's indestructible
        meta.getPersistentDataContainer().set(new NamespacedKey("cashapp", "ca_indestructible"), PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN, true);
    }
}
