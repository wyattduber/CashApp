package wyattduber.cashapp.commands.tabcomplete;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;
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
                        tabs.addAll(GetOnlinePlayerNames());
                        yield tabs;
                    case DamageDealt:
                    case DamageTaken:
                    case Deaths:
                        tabs.addAll(GetOnlinePlayerNames());
                        tabs.addAll(GetMobTypes());
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
                        yield tabs;
                    case ItemsCrafted:
                    case ItemsEnchanted:
                    case ItemsSmelted:
                    case ItemsDropped:
                    case ItemsPickedUp:
                        tabs.addAll(GetAllItemTypes());
                        yield tabs;
                    case FoodEaten:
                        tabs.addAll(GetAllEdibleItemTypes());
                        yield tabs;
                    case PotionsDrank:
                        tabs.addAll(GetAllPotionTypes());
                        yield tabs;
                }
                yield tabs;
            }
            default -> tabs;
        };
    }

    private List<String> GetOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

    private List<String> GetMobTypes() {
        return Arrays.stream(EntityType.values()).map(entity -> entity.name().toLowerCase()).collect(Collectors.toList());
    }

    private List<String> GetBlockTypes() {
        return Arrays.stream(Material.values()).filter(Material::isBlock).map(material -> material.name().toLowerCase()).collect(Collectors.toList());
    }

    private List<String> GetAllItemTypes() {
        return Arrays.stream(Material.values()).filter(Material::isItem).map(material -> material.name().toLowerCase()).collect(Collectors.toList());
    }

    private List<String> GetAllEdibleItemTypes() {
        return Arrays.stream(Material.values()).filter(Material::isEdible).map(material -> material.name().toLowerCase()).collect(Collectors.toList());
    }

    private List<String> GetAllPotionTypes() {
        return Arrays.stream(PotionType.values()).map(material -> material.name().toLowerCase()).collect(Collectors.toList());
    }
}
