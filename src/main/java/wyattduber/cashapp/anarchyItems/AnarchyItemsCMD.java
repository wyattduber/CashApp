package wyattduber.cashapp.anarchyItems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.anarchyItems.customitems.ItemManager;
import wyattduber.cashapp.helpers.ChatMessageHelper;
import wyattduber.cashapp.helpers.TabCompleterHelper;

public class AnarchyItemsCMD implements TabExecutor {

    private final CashApp ca;

    public AnarchyItemsCMD() {
        ca = CashApp.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1 || args.length > 2) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("Command can only be run by a player!");
        }

        // Get Player
        assert sender instanceof Player; // Should always pass due to previous check
        Player player = (Player) sender;

        // Assign item based off of argument
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "egg" -> player.getInventory().addItem(ItemManager.egg);
                case "bow" -> player.getInventory().addItem(ItemManager.bow);
                case "crossbow" -> player.getInventory().addItem(ItemManager.crossBow);
                case "trophy" -> ChatMessageHelper.sendMessage(player, "&cValid trophy options are: BoogysPorkchop, BrokenDrillBit, BucketOfFrost, ChaosCore, CrownShard, DefusedEggBomb, DemolitionistFlintStriker, GambitCoin, GreenysPetEgg, NaturesGem, Nemo, PartyCake, ScoutsIntrusiveThoughts, TandsFavoritePotato, ToxicVial, WitherKnightSkull", true);
                default -> ChatMessageHelper.sendMessage(player, "&cValid item options are: Egg, Bow, Crossbow", true);
            }
        } else if (args[0].equalsIgnoreCase("trophy")) {
            switch (args[1].toLowerCase()) {
                case "boogysporkchop" -> player.getInventory().addItem(ItemManager.boogysPorkchop);
                case "brokendrillbit" -> player.getInventory().addItem(ItemManager.brokenDrillBit);
                case "bucketoffrost" -> player.getInventory().addItem(ItemManager.bucketOfFrost);
                case "chaoscore" -> player.getInventory().addItem(ItemManager.chaosCore);
                case "crownshard" -> player.getInventory().addItem(ItemManager.crownShard);
                case "defusedeggbomb" -> player.getInventory().addItem(ItemManager.defusedEggBomb);
                case "demolitionistflintstriker" -> player.getInventory().addItem(ItemManager.demolitionistFlintStriker);
                case "gambitcoin" -> player.getInventory().addItem(ItemManager.gambitCoin);
                case "greenyspetegg" -> player.getInventory().addItem(ItemManager.greenysPetEgg);
                case "naturesgem" -> player.getInventory().addItem(ItemManager.naturesGem);
                case "nemo" -> player.getInventory().addItem(ItemManager.nemo);
                case "partycake" -> player.getInventory().addItem(ItemManager.partyCake);
                case "scoutsintrusivethoughts" -> player.getInventory().addItem(ItemManager.scoutsIntrusiveThoughts);
                case "tandsfavoritepotato" -> player.getInventory().addItem(ItemManager.tandsFavoritePotato);
                case "toxicvial" -> player.getInventory().addItem(ItemManager.toxicVial);
                case "witherknightskull" -> player.getInventory().addItem(ItemManager.witherKnightSkull);
                default -> ChatMessageHelper.sendMessage(player, "&cValid trophy options are: BoogysPorkchop, BrokenDrillBit, BucketOfFrost, ChaosCore, CrownShard, DefusedEggBomb, DemolitionistFlintStriker, GambitCoin, GreenysPetEgg, NaturesGem, Nemo, PartyCake, ScoutsIntrusiveThoughts, TandsFavoritePotato, ToxicVial, WitherKnightSkull", true);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add("Egg");
                tabs.add("Bow");
                tabs.add("Crossbow");
                tabs.add("Trophy");
                tabs = TabCompleterHelper.narrowDownTabCompleteResults(args[0], tabs);
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
                    tabs = TabCompleterHelper.narrowDownTabCompleteResults(args[1], tabs);
                } else {
                    tabs = TabCompleterHelper.narrowDownTabCompleteResultsOnlinePlayerList(args[1]);
                }
                yield tabs;
            }
            case 3 -> {
                if (!args[0].equalsIgnoreCase("Trophy"))
                    tabs = TabCompleterHelper.narrowDownTabCompleteResultsOnlinePlayerList(args[1]);
                yield tabs;
            }
            default -> tabs;
        };
    }
}
