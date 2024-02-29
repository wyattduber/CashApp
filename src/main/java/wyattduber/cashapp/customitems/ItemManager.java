package wyattduber.cashapp.customitems;

import org.bukkit.inventory.ItemStack;
import wyattduber.cashapp.customitems.items.Eggsplosion;
import wyattduber.cashapp.customitems.items.WithersPenanceBow;
import wyattduber.cashapp.customitems.items.WithersPenanceCrossbow;


public class ItemManager {

    public static ItemStack egg;
    public static ItemStack bow;
    public static ItemStack crossBow;

    public static void registerCustomItems() {
        // Eggsplosion
        egg = Eggsplosion.create();

        // Wither's Penance Bow
        bow = WithersPenanceBow.create();

        // Wither's Penance Crossbow
        crossBow = WithersPenanceCrossbow.create();
    }

}
