package wyattduber.cashapp.enums;

import org.bukkit.entity.Player;

public interface StatTypePrintable {
    default void print(Player player, double stat) {}
    default void print(Player player, String formattedSubType, double stat) {}
}
