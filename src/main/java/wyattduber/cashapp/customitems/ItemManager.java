package wyattduber.cashapp.customitems;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
    public static ItemStack ninsLastBraincell;
    public static ItemStack scoutsIntrusiveThoughts;
    public static ItemStack tandsFavoritePotato;
    public static ItemStack toxicVial;
    public static ItemStack witherKnightSkull;
    public static MiniMessage mm = MiniMessage.miniMessage();

    public static void registerCustomItems() {
        // Eggsplosion
        egg = new CustomItem("Eggsplosion", NamedTextColor.YELLOW, "microwaved for 5 minutes, be careful!", NamedTextColor.GRAY, Material.EGG, 64);

        // Wither's Penance Bow
        bow = new CustomItem("Wither's Penance", NamedTextColor.DARK_GRAY, Material.BOW, 1);

        // Wither's Penance Crossbow
        crossBow = new CustomItem("Wither's Penance", NamedTextColor.DARK_GRAY, Material.CROSSBOW, 1);

        /* Trophy Items */
        boogysPorkchop = new CustomItem("Boogy's Porkchop", NamedTextColor.DARK_RED, Material.COOKED_PORKCHOP);
        brokenDrillBit = new CustomItem("Broken Drill Bit", NamedTextColor.GRAY, Material.POINTED_DRIPSTONE);
        bucketOfFrost = new CustomItem("Bucket of Frost", TextColor.fromHexString("#21FFFF"), Material.POWDER_SNOW_BUCKET);
        chaosCore = new CustomItem(mm.deserialize("<gradient:#9600ff:#96ffff>Chaos Core</gradient>!"), Material.NETHER_STAR);
        crownShard = new CustomItem("Crown Shard", TextColor.fromHexString("#960FFF"), Material.AMETHYST_SHARD);
        defusedEggBomb = new CustomItem("Defused Egg Bomb", NamedTextColor.WHITE, Material.EGG);
        demolitionistFlintStriker = new CustomItem("Demolitionist Flint Striker", TextColor.fromHexString("#750F0F"), Material.FLINT);
        gambitCoin = new CustomItem("Gambit Coin", TextColor.fromHexString("FFD700"), Material.GOLD_NUGGET);
        greenysPetEgg = new CustomItem("Greeny's Pet Egg", TextColor.fromHexString("#90EE90"), Material.TURTLE_EGG);
        naturesGem = new CustomItem("Nature's Gem", TextColor.fromHexString("#23FF42"), Material.EMERALD);
        nemo = new CustomItem("Nemo", TextColor.fromHexString("#234FFF"), Material.TROPICAL_FISH);
        ninsLastBraincell = new CustomItem("Nin's Last Braincell", TextColor.fromHexString("#924FFF"), Material.POPPED_CHORUS_FRUIT);
        partyCake = new CustomItem("Party Cake", NamedTextColor.LIGHT_PURPLE, Material.CAKE);
        scoutsIntrusiveThoughts = new CustomItem("Scout's Intrusive Thoughts", TextColor.fromHexString("#000FFB"), Material.BOOK);
        tandsFavoritePotato = new CustomItem("Tand's Favorite Potato", NamedTextColor.GOLD, Material.POTATO);
        toxicVial = new CustomItem("Toxic Vial", TextColor.fromHexString("#E23F32"), Material.POTATO);
        witherKnightSkull = new CustomItem("Wither Knight Skull", TextColor.fromHexString("#002020"), Material.WITHER_SKELETON_SKULL);
    }

}
