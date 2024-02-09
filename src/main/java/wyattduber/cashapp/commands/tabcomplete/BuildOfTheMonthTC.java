package wyattduber.cashapp.commands.tabcomplete;

import org.apache.maven.model.Build;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.List;

public class BuildOfTheMonthTC implements TabCompleter {

    private final CashApp ca;

    public BuildOfTheMonthTC() {
        ca = CashApp.getPlugin();
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add(sender.getName());
                yield tabs;
            }
            case 2 -> {
                tabs.add("Surival");
                tabs.add("Nether");
                tabs.add("The End");
                tabs.add("Creative");
                yield tabs;
            }
            case 3 -> {
                tabs.add("X-Coordinate");
                yield tabs;
            }
            case 4 -> {
                tabs.add("Y-Coordinate");
                yield tabs;
            }
            case 5 -> {
                tabs.add("Z-Coordinate");
                yield tabs;
            }
            case 6 -> {
                tabs.add("Optional Message...");
                yield tabs;
            }
            default -> tabs;
        };
    }

}
