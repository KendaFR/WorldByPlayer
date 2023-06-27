package fr.kenda.worldbyplayer.utils;

import org.bukkit.ChatColor;

public class ChatUtils {

    public static String separateLine(String message, int maxCharsPerLine) {
        StringBuilder result = new StringBuilder();
        String[] words = message.split(" ");
        int lineLength = 0;

        for (String word : words) {
            // Check if the word contains a color code
            if (word.contains("&")) {
                String colorCode = ChatColor.getLastColors(word);
                // If the word starts with a color code, add it to the new line
                if (lineLength == 0) {
                    result.append(colorCode);
                }
                // Add the color code to the line length
                lineLength += colorCode.length();
            }

            // Add the word and a space to the new line
            result.append(word).append(" ");
            lineLength += word.length() + 1;

            // Check if the line length exceeds the maximum characters per line
            if (lineLength >= maxCharsPerLine) {
                result.append("\n");
                lineLength = 0;
            }
        }

        return result.toString().trim();
    }
}
