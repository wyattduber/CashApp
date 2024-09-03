package wyattduber.cashapp.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import wyattduber.cashapp.CashApp;

public class TabCompleterHelper {

    private static final CashApp ca = CashApp.getPlugin();

    public static ArrayList<String> narrowDownTabCompleteResults(String currentArg, List<String> possibleResults) {
        ArrayList<String> tabs = new ArrayList<>();

        for (String possibleResult : possibleResults) {
            if (possibleResult.startsWith(currentArg)) {
                tabs.add(possibleResult);
            }
        }

        return tabs;
    }

    public static ArrayList<String> narrowDownTabCompleteResultsOnlinePlayerList(String currentArg) {
        ArrayList<String> tabs = new ArrayList<>();

        var onlinePlayers = ca.getServer().getOnlinePlayers().stream().toList();

        for (Player player : onlinePlayers) {
            var possibleResult = player.getName();
            if (possibleResult.startsWith(currentArg)) {
                tabs.add(possibleResult);
            }
        }

        return tabs;
    }

}
