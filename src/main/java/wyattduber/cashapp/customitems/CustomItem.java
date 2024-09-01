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

    public CustomItem(String name, TextColor itemNameColor, Material itemMaterial) {
        super(itemMaterial);
        create(name, itemNameColor);
    }

    public CustomItem(Component formattedName, Material itemMaterial) {
        super(itemMaterial);
        create(formattedName);
    }

    public CustomItem(String name, TextColor itemNameColor, String lore, TextColor itemLoreColor, Material itemMaterial, int itemAmount) {
        super(itemMaterial);
        createWeaponItem(name, itemNameColor, lore, itemLoreColor, itemAmount);
    }

    public CustomItem(String name, TextColor itemNameColor, Material itemMaterial, int itemAmount, List<Enchantment> customEnchants) {
        super(itemMaterial);
        createWeaponItem(name, itemNameColor, itemAmount, customEnchants);
    }

    private void create(String name, TextColor color) {
        ItemMeta meta = this.getItemMeta();

        // Set Name
        meta.displayName(Component.text(name, color));

        // Do the rest of the item meta setting (same for every item)
        setCustomItemMeta(meta);

        // Set the finished modified meta and set the egg item to the created item
        this.setItemMeta(meta);
    }

    private void create(Component formattedName) {
        ItemMeta meta = this.getItemMeta();

        // Set Name
        meta.displayName(formattedName);

        // Do the rest of the item meta setting (same for every item)
        setCustomItemMeta(meta);

        // Set the finished modified meta and set the egg item to the created item
        this.setItemMeta(meta);
    }

    private void createWeaponItem(String name, TextColor itemNameColor, String loreLine, TextColor itemLoreColor, int amount) {
        this.setAmount(amount);
        ItemMeta meta = this.getItemMeta();

        // Set Name
        meta.displayName(Component.text(name, itemNameColor));

        // Do the rest of the item meta setting (same for every item)
        // Set Lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(loreLine, itemLoreColor));
        meta.lore(lore);

        // Set Enchantment Glow
        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Modify the NBT data so we know it's indestructible
        meta.getPersistentDataContainer().set(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN, true);

        // Set the finished modified meta and set the egg item to the created item
        this.setItemMeta(meta);
    }

    private void createWeaponItem(String name, TextColor itemNameColor, int amount, List<Enchantment> customEnchants) {
        this.setAmount(amount);
        ItemMeta meta = this.getItemMeta();

        // Set Name
        meta.displayName(Component.text(name, itemNameColor));

        // Set Enchantments
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        for (Enchantment e : customEnchants)
            meta.addEnchant(e, 5, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Modify the NBT data so we know it's indestructible
        meta.getPersistentDataContainer().set(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN, true);

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
        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Modify the NBT data so we know it's indestructible
        meta.getPersistentDataContainer().set(new NamespacedKey("cashapp", "ca_indestructible"), PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(new NamespacedKey("cashapp", "ca_isanarchyitem"), PersistentDataType.BOOLEAN, true);
    }
}
