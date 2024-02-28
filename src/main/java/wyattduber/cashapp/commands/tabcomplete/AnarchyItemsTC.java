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

        tabs.add("Egg");
        tabs.add("Bow");
        tabs.add("Crossbow");

        return tabs;
    }
}
