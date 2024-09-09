package wyattduber.cashapp.helpers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wyattduber.cashapp.CashApp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessageHelper {

    private static final CashApp ca = CashApp.getPlugin();

    // Regex patterns to match hex color codes and gradient patterns
    private static final Pattern HEX_PATTERN = Pattern.compile("\\{#([a-fA-F0-9]{6})\\}");
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("\\{#([a-fA-F0-9]{6})>}(.+?)\\{#([a-fA-F0-9]{6})<}");

    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage("§f[§aCash§bApp§f] " + replaceColors(message));
        } else {
            ca.log(message);
        }
    }

    /**
     * Replace all color codes and hex/gradient codes and return a TextComponent.
     *
     * @param text the text to replace the color codes in
     * @return TextComponent with color codes and hex colors replaced
     */
    public static TextComponent replaceColors(String text) {
        TextComponent.Builder componentBuilder = Component.text();

        // Replace '&' codes first
        char[] chrarray = text.toCharArray();
        for (int index = 0; index < chrarray.length; index++) {
            char chr = chrarray[index];

            // If it's not an '&', just add the character to the component
            if (chr != '&') {
                componentBuilder.append(Component.text(Character.toString(chr)));
                continue;
            }

            // If we are at the end of the array, break
            if ((index + 1) == chrarray.length) {
                break;
            }

            // Get the next char
            char forward = chrarray[index + 1];

            // Check if it's a valid Minecraft color code
            if ((forward >= '0' && forward <= '9') || (forward >= 'a' && forward <= 'f') || (forward >= 'k' && forward <= 'r')) {
                // Replace '&' with '§' and append to component
                componentBuilder.append(Component.text("§" + forward));
                index++;  // Skip next character since it's already processed
            } else {
                componentBuilder.append(Component.text("&"));  // Keep the '&' if not a valid code
            }
        }

        // Convert remaining string to handle hex and gradient replacements
        String remainingText = componentBuilder.build().content();

        // Handle gradients
        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(remainingText);
        while (gradientMatcher.find()) {
            String startColor = gradientMatcher.group(1);
            String message = gradientMatcher.group(2);
            String endColor = gradientMatcher.group(3);

            TextComponent gradientText = applyGradient(startColor, message, endColor);
            remainingText = remainingText.replace(gradientMatcher.group(0), gradientText.content());
            componentBuilder.append(gradientText);
        }

        // Handle hex colors
        Matcher hexMatcher = HEX_PATTERN.matcher(remainingText);
        while (hexMatcher.find()) {
            String hexCode = hexMatcher.group(1);
            TextComponent hexText = Component.text("").color(TextColor.fromHexString("#" + hexCode));
            remainingText = remainingText.replace(hexMatcher.group(0), hexText.content());
            componentBuilder.append(hexText);
        }

        return componentBuilder.build();
    }

    /**
     * Applies a gradient to the given message between two colors.
     *
     * @param startHex the starting color in hex
     * @param message  the text to apply the gradient to
     * @param endHex   the ending color in hex
     * @return a TextComponent with the gradient applied
     */
    private static TextComponent applyGradient(String startHex, String message, String endHex) {
        // Implement gradient logic using net.kyori.adventure.text.Component
        // (Can implement linear interpolation for colors)

        // For now, return the text with the start color as a placeholder
        return Component.text(message).color(TextColor.fromHexString("#" + startHex));
    }

}
