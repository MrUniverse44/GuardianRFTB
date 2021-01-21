package dev.mruniverse.rigoxrftb.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public class versionVerificator {
    private static final boolean IS_PAPER_SERVER = Bukkit.getName().equals("Paper");

    public static boolean isPaperServer() {
        return IS_PAPER_SERVER;
    }

    public static String extractNMSVersion() {
        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (matcher.find())
            return matcher.group();
        return null;
    }

    private static int compare(String reference, String comparison) throws NumberFormatException {
        String[] referenceSplit = reference.split("\\.");
        String[] comparisonSplit = comparison.split("\\.");
        int longest = Math.max(referenceSplit.length, comparisonSplit.length);
        int[] referenceNumbersArray = new int[longest];
        int[] comparisonNumbersArray = new int[longest];
        int i;
        for (i = 0; i < referenceSplit.length; i++)
            referenceNumbersArray[i] = Integer.parseInt(referenceSplit[i]);
        for (i = 0; i < comparisonSplit.length; i++)
            comparisonNumbersArray[i] = Integer.parseInt(comparisonSplit[i]);
        for (i = 0; i < longest; i++) {
            int diff = referenceNumbersArray[i] - comparisonNumbersArray[i];
            if (diff > 0)
                return 1;
            if (diff < 0)
                return -1;
        }
        return 0;
    }

    public static boolean isVersionGreaterEqual(String reference, String thanWhat) {
        return (compare(reference, thanWhat) >= 0);
    }

    public static boolean isVersionLessEqual(String reference, String thanWhat) {
        return (compare(reference, thanWhat) <= 0);
    }

    public static boolean isVersionBetweenEqual(String reference, String lowest, String highest) {
        return (isVersionGreaterEqual(reference, lowest) && isVersionLessEqual(reference, highest));
    }
}
