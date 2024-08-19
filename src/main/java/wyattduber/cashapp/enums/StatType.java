package wyattduber.cashapp.enums;

import org.bukkit.entity.Player;
import wyattduber.cashapp.CashApp;

public enum StatType implements StatTypePrintable {

    Joins {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has killed " + stat + " mobs.");
        }
    },
    Leaves {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has killed " + stat + " mobs.");
        }
    },
    MobsKilled {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has killed " + stat + " mobs.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has killed " + stat + " " + formattedSubType + "s.");
        }
    },
    PlayersKilled {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has killed " + stat + " players.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has killed " + formattedSubType + " " + stat + " times.");
        }
    },
    Deaths {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has died " + stat + " times.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " was killed by " + formattedSubType + " " + stat + " times.");
        }
    },
    BlocksBroken {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has broken " + stat + " blocks.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has broken " + stat + " " + formattedSubType + "s.");
        }
    },
    BlocksPlaced {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has placed " + stat + " blocks.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has placed " + stat + " " + formattedSubType + "s.");
        }
    },
    MoneyEarned {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has earned $" + stat + ".");
        }
    },
    MoneySpent {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has spent $" + stat);
        }
    },
    FishCaught {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has caught " + stat + " fish.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has caught " + stat + " " + formattedSubType + "s.");
        }
    },
    ItemsCrafted {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has crafted " + stat + " items.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has crafted " + stat + " " + formattedSubType + "s.");
        }
    },
    ItemsEnchanted {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has enchanted " + stat + " items.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has enchanted " + stat + " " + formattedSubType + "s.");
        }
    },
    ItemsSmelted {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has smelted " + stat + " items.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has smelted " + stat + " " + formattedSubType + "s.");
        }
    },
    ItemsDropped {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has dropped " + stat + " items.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has dropped " + stat + " " + formattedSubType + "s.");
        }
    },
    ItemsPickedUp {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has picked up " + stat + " items.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has picked up " + stat + " " + formattedSubType + "s.");
        }
    },
    FoodEaten {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has eaten " + stat + " foodstuffs.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has eaten " + stat + " " + formattedSubType + "s.");
        }
    },
    PotionsDrank {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has drank " + stat + " potions.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has drank " + stat + " " + formattedSubType + "s.");
        }
    },
    XpGained {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has gained " + stat + " XP Points.");
        }
    },
    XpLost {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has lost " + stat + " XP Points.");
        }
    },
    TimesSlept {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has slept " + stat + " times.");
        }
    },
    DamageDealt {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has dealt " + stat + " damage points.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has dealt " + stat + " damage points to " + formattedSubType + ".");
        }
    },
    DamageTaken {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has taken " + stat + " damage points.");
        }
        public void print(Player player, String formattedSubType, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has taken " + stat + " damage points from " + formattedSubType + ".");
        }
    },
    DistanceWalked {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has walked for " + stat + " meters.");
        }
    },
    DistanceCrouched {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has crouched for " + stat + " meters.");
        }
    },
    DistanceSprinted {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has sprinted for " + stat + " meters.");
        }
    },
    DistanceSwam {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has swam for " + stat + " meters.");
        }
    },
    DistanceClimbed {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has climbed for " + stat + " meters.");
        }
    },
    DistanceFlown {
        public void print(Player player, double stat) {
            CashApp.getPlugin().sendMessage(player, "§a" + player.getName() + " has glided for " + stat + " meters.");
        }
    },
}
