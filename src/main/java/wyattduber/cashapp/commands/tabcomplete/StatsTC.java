package wyattduber.cashapp.commands.tabcomplete;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.enums.StatType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StatsTC implements TabCompleter {

    private final CashApp ca;

    public StatsTC() {
        ca = CashApp.getPlugin();
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player player)) return null; // Only provide tab completion for players

        ArrayList<String> tabs = new ArrayList<>();

        return switch (args.length) {
            // StatType
            case 1 -> {
                for (StatType statType : StatType.values()) {
                    tabs.add(statType.toString());
                }
                yield tabs;
            }
            // StatSubType
            case 2 -> {
                if (Arrays.stream(StatType.values()).noneMatch(stat -> stat.name().equalsIgnoreCase(args[0]))) yield tabs;

                switch (StatType.valueOf(args[0])) {
                    case MobsKilled:
                        tabs.addAll(GetMobTypes());
                        yield tabs;
                    case PlayersKilled:
                        tabs.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                        yield tabs;
                    case BlocksBroken:
                    case BlocksPlaced:
                        tabs.addAll(GetBlockTypes());
                        yield tabs;
                    case FishCaught:
                        tabs.add("cod");
                        tabs.add("salmon");
                        tabs.add("pufferfish");
                        tabs.add("tropicalFish");
                }
                yield tabs;
            }
            default -> tabs;
        };
    }

    private ArrayList<String> GetMobTypes() {
        return Arrays.stream(EntityType.values()).map(entity -> entity.name().toLowerCase()).collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<String> GetBlockTypes() {
        return Arrays.stream(Material.values()).filter(Material::isBlock).map(material -> material.name().toLowerCase()).collect(Collectors.toCollection(ArrayList::new));
    }
}
