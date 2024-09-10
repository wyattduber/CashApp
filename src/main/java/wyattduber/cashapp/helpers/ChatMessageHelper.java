package wyattduber.cashapp.helpers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

    public static void sendMessage(CommandSender sender, String message, boolean includePluginPrefix) {
        // Check if the sender is a player or the console
        if (sender instanceof Player) {
            // Send the full message to players, including hex colors
            sender.sendMessage((includePluginPrefix ? "§f[§aCash§bApp§f] " : "") + replaceColors(message));
        } else {
            // For console, simplify the color codes
            String consoleMessage = replaceColors(message);
            ca.log(consoleMessage.replaceAll("§[0-9a-fk-orxX]|§x(§[0-9a-fA-F]){6}", ""));
        }
    }

    /**
     * Replace all color codes and hex/gradient codes and return a TextComponent.
     *
     * @param text the text to replace the color codes in
     * @return TextComponent with color codes and hex colors replaced
     */
    public static String replaceColors(String text) {
        // First replace '&' codes into '§' codes for standard Minecraft colors
        text = text.replace('&', '§');

        // Create a TextComponent builder
        TextComponent.Builder componentBuilder = Component.text();

        // Handle gradients first
        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(text);
        while (gradientMatcher.find()) {
            String startColor = gradientMatcher.group(1);
            String message = gradientMatcher.group(2);
            String endColor = gradientMatcher.group(3);

            TextComponent gradientText = applyGradient(startColor, message, endColor);
            componentBuilder.append(gradientText);

            // Remove this gradient section from the text to avoid further processing
            text = text.replace(gradientMatcher.group(0), "");
        }

        // Handle hex colors after gradients
        Matcher hexMatcher = HEX_PATTERN.matcher(text);
        while (hexMatcher.find()) {
            String hexCode = hexMatcher.group(1);
            String remainingText = hexMatcher.replaceAll(""); // Remove matched hex code
            TextComponent hexText = Component.text(remainingText).color(TextColor.fromHexString("#" + hexCode));
            componentBuilder.append(hexText);
        }

        // Append any remaining text (which should only have Minecraft color codes at this point)
        componentBuilder.append(Component.text(text));

        // Return the final serialized message
        return PlainTextComponentSerializer.plainText().serialize(componentBuilder.build());
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
        // Convert hex strings to RGB values
        int[] startRGB = hexToRgb(startHex);
        int[] endRGB = hexToRgb(endHex);

        // Create a builder for the gradient component
        TextComponent.Builder gradientBuilder = Component.text();

        // Length of the message
        int length = message.length();

        // Loop over each character in the message
        for (int i = 0; i < length; i++) {
            // Calculate the interpolation factor (0.0 at the start, 1.0 at the end)
            float ratio = (float) i / (length - 1);

            // Interpolate the RGB values
            int red = (int) (startRGB[0] + ratio * (endRGB[0] - startRGB[0]));
            int green = (int) (startRGB[1] + ratio * (endRGB[1] - startRGB[1]));
            int blue = (int) (startRGB[2] + ratio * (endRGB[2] - startRGB[2]));

            // Convert the interpolated RGB values back to a hex string
            String interpolatedHex = String.format("#%02X%02X%02X", red, green, blue);

            // Append the current character with the interpolated color to the component
            gradientBuilder.append(
                    Component.text(String.valueOf(message.charAt(i)))
                            .color(TextColor.fromHexString(interpolatedHex))
            );
        }

        // Return the final gradient component
        return gradientBuilder.build();
    }

    /**
     * Helper method to convert a hex color code to an RGB array.
     *
     * @param hex the hex color code (without the #)
     * @return an array of three integers [red, green, blue]
     */
    private static int[] hexToRgb(String hex) {
        return new int[]{
                Integer.valueOf(hex.substring(0, 2), 16), // Red
                Integer.valueOf(hex.substring(2, 4), 16), // Green
                Integer.valueOf(hex.substring(4, 6), 16)  // Blue
        };
    }

    /**
     * Check if the given char is a valid Minecraft color code.
     *
     * @param colorCode the color code character
     * @return true if valid, false otherwise
     */
    private static boolean isMinecraftColorCode(char colorCode) {
        return (colorCode >= '0' && colorCode <= '9') || (colorCode >= 'a' && colorCode <= 'f') || (colorCode >= 'k' && colorCode <= 'r');
    }

    /**
     * Convert a Minecraft color code char to a TextColor.
     *
     * @param colorCode the Minecraft color code
     * @return the corresponding TextColor
     */
    private static TextColor minecraftColorToTextColor(char colorCode) {
        return switch (colorCode) {
            case '0' -> TextColor.color(0x000000); // Black
            case '1' -> TextColor.color(0x0000AA); // Dark Blue
            case '2' -> TextColor.color(0x00AA00); // Dark Green
            case '3' -> TextColor.color(0x00AAAA); // Dark Aqua
            case '4' -> TextColor.color(0xAA0000); // Dark Red
            case '5' -> TextColor.color(0xAA00AA); // Dark Purple
            case '6' -> TextColor.color(0xFFAA00); // Gold
            case '7' -> TextColor.color(0xAAAAAA); // Gray
            case '8' -> TextColor.color(0x555555); // Dark Gray
            case '9' -> TextColor.color(0x5555FF); // Blue
            case 'a' -> TextColor.color(0x55FF55); // Green
            case 'b' -> TextColor.color(0x55FFFF); // Aqua
            case 'c' -> TextColor.color(0xFF5555); // Red
            case 'd' -> TextColor.color(0xFF55FF); // Light Purple
            case 'e' -> TextColor.color(0xFFFF55); // Yellow
            case 'f' -> TextColor.color(0xFFFFFF); // White
            default -> TextColor.color(0xFFFFFF); // Default to white
        };
    }
}
