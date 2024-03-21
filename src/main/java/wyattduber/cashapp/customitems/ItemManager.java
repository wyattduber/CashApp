package wyattduber.cashapp.customitems;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wyattduber.cashapp.customitems.items.Eggsplosion;
import wyattduber.cashapp.customitems.items.WithersPenanceBow;
import wyattduber.cashapp.customitems.items.WithersPenanceCrossbow;
import wyattduber.cashapp.customitems.items.trophies.*;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public static ItemStack egg;
    public static ItemStack bow;
    public static ItemStack crossBow;
    public static ItemStack boogysPorkchop;
    public static ItemStack brokenDrillBit;
    public static ItemStack bucketOfFrost;
    public static ItemStack chaosCore;
    public static ItemStack crownShard;
    public static ItemStack defusedEggBomb;
    public static ItemStack demolitionistFlintStriker;
    public static ItemStack gambitCoin;
    public static ItemStack greenysPetEgg;
    public static ItemStack naturesGem;
    public static ItemStack nemo;
    public static ItemStack partyCake;
    public static ItemStack scoutsIntrusiveThoughts;
    public static ItemStack tandsFavoritePotato;
    public static ItemStack toxicVial;
    public static ItemStack witherKnightSkull;
    public static List<ItemMeta> trophyItemMetas = new ArrayList<>();

    public static void registerCustomItems() {
        // Eggsplosion
        egg = Eggsplosion.create();

        // Wither's Penance Bow
        bow = WithersPenanceBow.create();

        // Wither's Penance Crossbow
        crossBow = WithersPenanceCrossbow.create();

        /* Trophy Items */
        boogysPorkchop = BoogysPorkchop.create();
        brokenDrillBit = BrokenDrillBit.create();
        bucketOfFrost = BucketOfFrost.create();
        chaosCore = ChaosCore.create();
        crownShard = CrownShard.create();
        defusedEggBomb = DefusedEggBomb.create();
        demolitionistFlintStriker = DemolitionistFlintStriker.create();
        gambitCoin = GambitCoin.create();
        greenysPetEgg = GreenysPetEgg.create();
        naturesGem = NaturesGem.create();
        nemo = Nemo.create();
        partyCake = PartyCake.create();
        scoutsIntrusiveThoughts = ScoutsIntrusiveThoughts.create();
        tandsFavoritePotato = TandsFavoritePotato.create();
        toxicVial = ToxicVial.create();
        witherKnightSkull = WitherKnightSkull.create();

        trophyItemMetas.add(boogysPorkchop.getItemMeta());
        trophyItemMetas.add(brokenDrillBit.getItemMeta());
        trophyItemMetas.add(bucketOfFrost.getItemMeta());
        trophyItemMetas.add(chaosCore.getItemMeta());
        trophyItemMetas.add(crownShard.getItemMeta());
        trophyItemMetas.add(defusedEggBomb.getItemMeta());
        trophyItemMetas.add(demolitionistFlintStriker.getItemMeta());
        trophyItemMetas.add(gambitCoin.getItemMeta());
        trophyItemMetas.add(greenysPetEgg.getItemMeta());
        trophyItemMetas.add(naturesGem.getItemMeta());
        trophyItemMetas.add(nemo.getItemMeta());
        trophyItemMetas.add(partyCake.getItemMeta());
        trophyItemMetas.add(scoutsIntrusiveThoughts.getItemMeta());
        trophyItemMetas.add(tandsFavoritePotato.getItemMeta());
        trophyItemMetas.add(toxicVial.getItemMeta());
        trophyItemMetas.add(witherKnightSkull.getItemMeta());
    }

}
