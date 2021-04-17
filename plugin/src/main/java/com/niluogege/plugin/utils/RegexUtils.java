package com.niluogege.plugin.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegexUtils {

    public static String convertToPatternString(String input) {
        // ?	Zero or one character
        // *	Zero or more of character
        // +	One or more of character
        final String[] searchList = new String[]{".", "?", "*", "+"};
        final String[] replacementList = new String[]{"\\.", ".?", ".*", ".+"};
        return replaceEach(input, searchList, replacementList);
    }

    private static String replaceEach(String text, String[] searchList, String[] replacementList) {
        // TODO: throw new IllegalArgumentException() if any param doesn't make sense
        //validateParams(text, searchList, replacementList);

        SearchTracker tracker = new SearchTracker(text, searchList, replacementList);
        if (!tracker.hasNextMatch(0)) {
            return text;
        }

        StringBuilder buf = new StringBuilder(text.length() * 2);
        int start = 0;

        do {
            SearchTracker.MatchInfo matchInfo = tracker.matchInfo;
            int textIndex = matchInfo.textIndex;
            String pattern = matchInfo.pattern;
            String replacement = matchInfo.replacement;

            buf.append(text.substring(start, textIndex));
            buf.append(replacement);

            start = textIndex + pattern.length();
        } while (tracker.hasNextMatch(start));

        return buf.append(text.substring(start)).toString();
    }

    static class SearchTracker {

        final String text;

        final Map<String, String> patternToReplacement = new HashMap<>();
        final Set<String> pendingPatterns = new HashSet<>();

        MatchInfo matchInfo = null;

        SearchTracker(String text, String[] searchList, String[] replacementList) {
            this.text = text;
            for (int i = 0; i < searchList.length; ++i) {
                String pattern = searchList[i];
                patternToReplacement.put(pattern, replacementList[i]);
                pendingPatterns.add(pattern);
            }
        }

        boolean hasNextMatch(int start) {
            int textIndex = -1;
            String nextPattern = null;

            for (String pattern : new ArrayList<>(pendingPatterns)) {
                int matchIndex = text.indexOf(pattern, start);
                if (matchIndex == -1) {
                    pendingPatterns.remove(pattern);
                } else {
                    if (textIndex == -1 || matchIndex < textIndex) {
                        textIndex = matchIndex;
                        nextPattern = pattern;
                    }
                }
            }

            if (nextPattern != null) {
                matchInfo = new MatchInfo(nextPattern, patternToReplacement.get(nextPattern), textIndex);
                return true;
            }
            return false;
        }

        private static class MatchInfo {
            final String pattern;
            final String replacement;
            final int textIndex;

            MatchInfo(String pattern, String replacement, int textIndex) {
                this.pattern = pattern;
                this.replacement = replacement;
                this.textIndex = textIndex;
            }
        }
    }

}
