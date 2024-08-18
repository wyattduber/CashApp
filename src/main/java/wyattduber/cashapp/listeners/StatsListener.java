package wyattduber.cashapp.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;
import wyattduber.cashapp.enums.StatType;

public class StatsListener implements Listener {

    private final CashApp ca = CashApp.getPlugin();
    private final Database db;

    public StatsListener() {
        db = ca.db;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.joins, 1);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.leaves, 1);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        var deathCause = event.getDamageSource();
        db.addStat(player.getUniqueId(), StatType.deaths, deathCause.toString(), 1);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        db.addStat(killer.getUniqueId(), StatType.playersKilled, player.getUniqueId().toString(), 1);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        var deathCause = event.getDamageSource();
        db.addStat(player.getUniqueId(), StatType.mobsKilled, deathCause.toString(), 1);
    }

    @EventHandler
    public void onFishCaught(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity fish = event.getCaught();
        db.addStat(player.getUniqueId(), StatType.fishCaught, fish.getType().toString(), 1);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.blocksBroken, event.getBlock().toString(), 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.blocksPlaced, event.getBlock().toString(), 1);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.isSprinting()) {
            db.addStat(player.getUniqueId(), StatType.distanceSprinted, 1);
        } else if (player.isSneaking()) {
            db.addStat(player.getUniqueId(), StatType.distanceCrouched, 1);
        } else if (player.isSwimming()) {
            db.addStat(player.getUniqueId(), StatType.distanceSwam, 1);
        } else if (player.isGliding()) {
            db.addStat(player.getUniqueId(), StatType.distanceFlown, 1);
        } else if (player.isClimbing()) {
            db.addStat(player.getUniqueId(), StatType.distanceClimbed, 1);
        }

        db.addStat(player.getUniqueId(), StatType.distanceWalked, 1);
    }

}
