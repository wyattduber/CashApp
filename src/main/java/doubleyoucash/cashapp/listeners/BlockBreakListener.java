package doubleyoucash.cashapp.listeners;

import doubleyoucash.cashapp.CashApp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.awt.*;

public class BlockBreakListener implements Listener {

    public final CashApp ca;

    public BlockBreakListener() {
        ca = CashApp.getPlugin();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ca.getServer().getOnlinePlayers().forEach(pl -> pl.sendMessage(pl.getName() + " broke a block!"));

        player.sendMessage("You broke a block!");
    }

}
