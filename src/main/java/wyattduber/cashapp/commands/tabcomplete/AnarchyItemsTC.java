package wyattduber.cashapp.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.List;

public class AnarchyItemsTC implements TabCompleter {

    private final CashApp ca;

    public AnarchyItemsTC() {
        ca = CashApp.getPlugin();
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add("Egg");
                tabs.add("Bow");
                tabs.add("Crossbow");
                tabs.add("Trophy");
                yield tabs;
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("Trophy")) {
                    tabs.add("BoogysPorkchop");
                    tabs.add("BrokenDrillBit");
                    tabs.add("BucketOfFrost");
                    tabs.add("ChaosCore");
                    tabs.add("CrownShard");
                    tabs.add("DefusedEggBomb");
                    tabs.add("DemolitionistFlintStriker");
                    tabs.add("GambitCoin");
                    tabs.add("GreenysPetEgg");
                    tabs.add("NaturesGem");
                    tabs.add("Nemo");
                    tabs.add("PartyCake");
                    tabs.add("ScoutsIntrusiveThoughts");
                    tabs.add("TandsFavoritePotato");
                    tabs.add("ToxicVial");
                    tabs.add("WitherKnightSkull");
                }
                yield tabs;
            }
            default -> tabs;
        };
    }
}
