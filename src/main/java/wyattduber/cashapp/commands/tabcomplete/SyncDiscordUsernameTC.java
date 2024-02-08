package wyattduber.cashapp.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SyncDiscordUsernameTC implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            case 1 -> {
                tabs.add("discord_username");
                yield tabs;
            }
            case 2 -> {
                tabs.add("#/unsync");
                tabs.add("reminder");
                yield tabs;
            }
            case 3 -> {
                if (args[1].equalsIgnoreCase("reminder")) {
                    tabs.add("on");
                    tabs.add("off");
                    yield tabs;
                }
                yield tabs;
            }
            default -> tabs;
        };
    }

}
