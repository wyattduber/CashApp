package wyattduber.cashapp.commands;

import com.Zrips.CMI.CMI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import wyattduber.cashapp.helpers.ChatMessageHelper;

import java.util.List;
import java.util.Random;

public class SnowGolemChristmasItemCommand implements TabExecutor {

    private final Random rand = new Random();
    private ItemStack christmasSnow;

    public SnowGolemChristmasItemCommand() {
        var savedItems = CMI.getInstance().getSavedItemManager().getSavedItems("dc");
        for (var savedItem : savedItems) {
            if (savedItem.getName().equals("ChristmasSnow")) {
                christmasSnow = savedItem.getItem();
                break;
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (rand.nextInt(10) == 1) { // 10% Chance
                player.getInventory().addItem(christmasSnow.clone());
            }
        } else ChatMessageHelper.sendMessage(sender, "You must be a player to use this command!", false);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return null;
    }
}
