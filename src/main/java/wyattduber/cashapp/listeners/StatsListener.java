package wyattduber.cashapp.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import wyattduber.cashapp.CashApp;
import wyattduber.cashapp.database.Database;
import wyattduber.cashapp.enums.StatType;

import java.util.Objects;

public class StatsListener implements Listener {

    private final Database db;

    public StatsListener() {
        CashApp ca = CashApp.getPlugin();
        db = ca.db;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.Joins, 1);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.Leaves, 1);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        var deathCause = event.getDamageSource();
        db.addStat(player.getUniqueId(), StatType.Deaths, deathCause.toString(), 1);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        db.addStat(killer.getUniqueId(), StatType.PlayersKilled, player.getUniqueId().toString(), 1);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        String entityType = Objects.requireNonNull(event.getDamageSource().getCausingEntity()).getType().toString().toLowerCase();
        db.addStat(player.getUniqueId(), StatType.MobsKilled, entityType, 1);
    }

    @EventHandler
    public void onFishCaught(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity fish = event.getCaught();
        db.addStat(player.getUniqueId(), StatType.FishCaught, fish != null ? fish.getType().toString().toLowerCase() : null, 1);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.BlocksBroken, event.getBlock().getType().toString().toLowerCase(), 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.BlocksPlaced, event.getBlock().getType().toString().toLowerCase(), 1);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.isSprinting()) {
            db.addStat(player.getUniqueId(), StatType.DistanceSprinted, 1);
        } else if (player.isSneaking()) {
            db.addStat(player.getUniqueId(), StatType.DistanceCrouched, 1);
        } else if (player.isSwimming()) {
            db.addStat(player.getUniqueId(), StatType.DistanceSwam, 1);
        } else if (player.isGliding()) {
            db.addStat(player.getUniqueId(), StatType.DistanceFlown, 1);
        } else if (player.isClimbing()) {
            db.addStat(player.getUniqueId(), StatType.DistanceClimbed, 1);
        }

        db.addStat(player.getUniqueId(), StatType.DistanceWalked, 1);
    }

    @EventHandler
    public void onItemEnchanted(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        db.addStat(player.getUniqueId(), StatType.ItemsEnchanted, event.getItem().toString(), 1);
        for (Enchantment enchantment : event.getEnchantsToAdd().keySet()) {
            int level = event.getEnchantsToAdd().get(enchantment);
            if (level > 1) {
                db.addStat(player.getUniqueId(), StatType.ItemsEnchanted, enchantment.toString(), level);
            }
        }
    }

    @EventHandler
    public void onItemCrafted(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        db.addStat(player.getUniqueId(), StatType.ItemsCrafted, event.getRecipe().getResult().toString(), 1);
    }

    @EventHandler
    public void onItemSmelted(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.ItemsSmelted, event.getItemType().toString(), 1);
    }

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.ItemsDropped, event.getItemDrop().getItemStack().toString(), 1);
    }

    @EventHandler
    public void onItemPickedUp(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.ItemsPickedUp, event.getItem().getItemStack().toString(), 1);
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.TimesSlept, 1);
    }

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.FoodEaten, event.getItem().toString(), 1);
    }

    @EventHandler
    public void onPlayerDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        db.addStat(player.getUniqueId(), StatType.PotionsDrank, event.getItem().toString(), 1);
    }

    @EventHandler
    public void onPlayerXPAmountChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();

        if (event.getAmount() < 0) {
            db.addStat(player.getUniqueId(), StatType.XpLost, event.getAmount());
        } else {
            db.addStat(player.getUniqueId(), StatType.XpGained, event.getAmount());
        }
    }

    @EventHandler
    public void onDamageDealt(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        db.addStat(player.getUniqueId(), StatType.DamageDealt, event.getDamage());
    }

    @EventHandler
    public void onDamageTaken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        db.addStat(player.getUniqueId(), StatType.DamageTaken, event.getDamage());
    }

}
