package wyattduber.cashapp.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;

import java.util.ArrayList;
import java.util.List;

public class DoNotDisturbTC implements TabCompleter {

    private final CashApp ca = CashApp.getPlugin();

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add("on");
                tabs.add("off");
                tabs.add("status");
                yield tabs;
            }
            case 2 -> {
                if (sender.hasPermission("ca.dnd.others")) {
                    ca.getServer().getOnlinePlayers().forEach(player -> tabs.add(player.getName()));
                }
                yield tabs;
            }
            default -> tabs;
        };
    }
}
