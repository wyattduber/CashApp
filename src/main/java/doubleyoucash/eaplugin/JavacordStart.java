package doubleyoucash.eaplugin;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;

public class JavacordStart {

    public DiscordApi api;
    public Server discordServer;

    private final CashApp ca = CashApp.getPlugin();

    public JavacordStart() {
        parseConfig();
    }

    public void disableAPI() {
        try {
            if (api != null) {
                api.disconnect();
            }
            api = null;
        } catch (Exception e) {
            ca.error("Error Disconnecting from API! Contact the developer.");
        }
    }

    public void reload() {
        disableAPI();
        parseConfig();
    }

    private void parseConfig() {
        if (ca.botToken == null) {
            return;
        }

        try {
            api = new DiscordApiBuilder().setToken(ca.botToken).setAllIntents().login().join();
            ca.log("Connected to " + api.getYourself().getName() + " Bot!");
        } catch (NullPointerException e) {
            ca.warn("Could not connect to API! Please enter a valid Bot Token in config.yml and reload the plugin.");
            ca.warn("If the bot-token is valid, please file an issue on our GitHub.");
        }

        try {
            if (api.getServerById(ca.serverID).isPresent())
                discordServer = api.getServerById(ca.serverID).get();
            ca.log("Connected to " + discordServer.getName() + " Discord Server!");
        } catch (Exception e) {
            ca.warn("Server not Found! Please enter a valid Server ID in config.yml and reload the plugin.");
        }

    }

}
