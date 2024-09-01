package Handler;

import com.google.gson.Gson;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Parser.
 */
public class Parser {

    /**
     * Get desc string.
     *
     * @param input the input
     * @return the string
     */
    public static String getDesc(String input){
        return extractPattern(input, "\\*\\*Description:\\*\\* (.*?)\\n\\n");
    }

    /**
     * Get tournament name string.
     *
     * @param input the input
     * @return the string
     */
    public static String getTournamentName(String input){
        return extractPattern(input, "\\*\\*Tournament Name:\\*\\* (.*?)\\n\\n");
    }

    /**
     * Gets pairings in list format.
     *
     * @param pair the pair
     * @return the pairings in list format
     */
    public static ArrayList<String> getPairingsInListFormat(String pair)  {

        Pattern pattern = Pattern.compile("\\*\\*Round \\d+:\\*\\* \\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pair);

        ArrayList<String> roundsList = new ArrayList<>();

        while (matcher.find()) {
            String roundContent = matcher.group(1).trim();
            roundsList.add(roundContent);
        }

        return roundsList;
    }


    private static String extractPattern(String input, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(input);
        return m.find() ? m.group(1).trim() : "";
    }

}
