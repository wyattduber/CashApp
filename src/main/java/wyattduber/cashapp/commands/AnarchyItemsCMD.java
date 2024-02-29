package wyattduber.cashapp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.customitems.ItemManager;

public class AnarchyItemsCMD implements CommandExecutor {

    private final CashApp ca;

    public AnarchyItemsCMD() {
        ca = CashApp.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("Command can only be run by a player!");
        }

        // Get Player
        Player player = (Player) sender;

        // Assign item based off of argument
        switch (args[0].toLowerCase()) {
            case "egg" -> player.getInventory().addItem(ItemManager.egg);
            case "bow" -> player.getInventory().addItem(ItemManager.bow);
            case "crossbow" -> player.getInventory().addItem(ItemManager.crossBow);
            default -> ca.sendMessage(player, "&cValid item options are: Egg, Bow, Crossbow");
        }

        return true;
    }
}
