package doubleyoucash.eaplugin.commands;

import doubleyoucash.eaplugin.CashApp;
import doubleyoucash.eaplugin.database.Database;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class STREAK implements TabExecutor {

    private final CashApp ca;
    private final Database db;


    public STREAK() {
        ca = CashApp.getPlugin();
        db = ca.db;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String text, @NotNull String[] args) {
        String ADMIN_HELP_MESSAGE = "All of the above commands can be run on other players, with this format:\n" +
                "/streak <subcommand> [args] <username>";
        String BASIC_HELP_MESSAGE = """
                Streak Sub-Command List:
                /streak togglelogin - Toggle the login streak reminder
                /streak totalvotes - Show how many total votes you have since streaks were released
                /streak getvotes [#] - Shows a detailed list of votes with a custom list limit [#]
                /streak getstreak - Shows your current streak
                /streak lastvote - Shows the time of your last vote
                """;
        if (args.length < 1) {
            sender.sendMessage(BASIC_HELP_MESSAGE);
            if (sender.hasPermission("ca.streak.admin")) {
                sender.sendMessage(ADMIN_HELP_MESSAGE);
                return true;
            } else return false;
        }

        switch (args[0].toLowerCase()) {
            case "togglelogin" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.togglelogin")) toggleLogin(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && !player.hasPermission("ca.streak.togglelogin.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        toggleLoginOther(player);
                    } catch (Exception e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "totalvotes" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.totalvotes")) totalVotes(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && !player.hasPermission("ca.streak.totalvotes.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        totalVotesOther(player);
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "getvotes" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.getvotes")) getVotes(player, 5);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    try {
                        if (sender instanceof Player player && player.hasPermission("ca.streak.getvotes"))
                            getVotes(player, Integer.parseInt(args[1]));
                        else sender.sendMessage("§cYou don't have permission!");
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§cIncorrect format! /streak getvotes [#]");
                    }
                } else if (args.length == 3) {
                    if (sender instanceof Player player && !player.hasPermission("ca.streak.getvotes.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[2]);
                        assert player != null;
                        getVotes(sender, player, Integer.parseInt(args[1]));
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "getstreak" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.getstreak")) getStreak(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.getstreak.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        getStreakOther(sender, player);
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            case "lastvote" -> {
                if (args.length == 1) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.totalvotes")) getLastVote(player);
                    else sender.sendMessage("§cYou don't have permission!");
                } else if (args.length == 2) {
                    if (sender instanceof Player player && player.hasPermission("ca.streak.totalvotes.others")) {
                        player.sendMessage("§cYou don't have permission to use this command on others!");
                        return true;
                    }
                    try {
                        Player player = ca.getServer().getPlayer(args[1]);
                        assert player != null;
                        getLastVoteOther(sender, player);
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cPlayer not found!");
                    }
                } else {
                    wrongArgAmount(sender);
                }
            }
            default -> {
                sender.sendMessage("§cUnknown subcommand. Please read the list below:");
                sender.sendMessage(BASIC_HELP_MESSAGE);
                if (sender.hasPermission("ca.streak.admin")) {
                    sender.sendMessage(ADMIN_HELP_MESSAGE);
                    return true;
                } else return false;
            }
        }
        return true;
    }

    private void toggleLogin(Player player) {
        if (db.toggleShowStreak(player.getUniqueId())) player.sendMessage("Streak Reminder at Login Enabled!");
        else player.sendMessage("Streak Reminder at Login Disabled!");
    }

    private void toggleLoginOther(Player player) {
        if (db.toggleShowStreak(player.getUniqueId())) player.sendMessage("Streak Reminder at Login Enabled for " + player.getName() + "!");
        else player.sendMessage("Streak Reminder at Login Disabled for" + player.getName() + "!");
    }

    private void totalVotes(Player player) {
        player.sendMessage("Total Votes: " + db.getTotalVotes(player.getUniqueId()));
    }

    private void totalVotesOther(Player player) {
        player.sendMessage("Total Votes for user " + player.getName() + ": " + db.getTotalVotes(player.getUniqueId()));
    }

    private void getVotes(Player player, int num) {
        ArrayList<String> list = db.getVotes(player.getUniqueId(), num);
        for (String s : list) {
            player.sendMessage(s);
        }
    }

    private void getVotes(CommandSender sender, Player player, int num) {
        ArrayList<String> list = db.getVotes(player.getUniqueId(), num);
        for (String s : list) {
            sender.sendMessage(s);
        }
    }

    private void getStreak(Player player) {
        int streak = db.getStreak(player.getUniqueId());
        if (streak == 1) player.sendMessage("Current Vote Streak: " + streak + " day");
        else if (streak == -1) player.sendMessage("Current Vote Streak: 0 days");
        else player.sendMessage("Current Vote Streak: " + streak + " days");
    }

    private void getStreakOther(CommandSender sender, Player player) {
        int streak = db.getStreak(player.getUniqueId());
        if (streak == 1) sender.sendMessage("Current Vote Streak for " + player.getName() + ": " + db.getStreak(player.getUniqueId()) + " day");
        else sender.sendMessage("Current Vote Streak for " + player.getName() + ": " + db.getStreak(player.getUniqueId()) + " days");
    }

    private void getLastVote(Player player) {
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
        long lastVote = db.getLastVoteTime(player.getUniqueId());
        if (lastVote == 0) {
            player.sendMessage("No past votes!");
            return;
        }
        Date result = new Date(lastVote);
        player.sendMessage("Time of Last Vote: " + simple.format(result));
    }

    private void getLastVoteOther(CommandSender sender, Player player) {
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
        Date result = new Date(db.getLastVoteTime(player.getUniqueId()));
        sender.sendMessage("Time of Last Vote for " + player.getName() + ": " + simple.format(result));
    }

    private void wrongArgAmount(CommandSender sender) { sender.sendMessage("§cIncorrect amount of arguments!"); }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        ArrayList<String> commands = new ArrayList<>();

        commands.add("togglelogin");
        commands.add("totalvotes");
        commands.add("getvotes");
        commands.add("getstreak");
        commands.add("lastvote");

        return commands;
    }

}
