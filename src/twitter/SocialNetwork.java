/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;
import java.util.*;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        // Regex to find @-mentions in the tweet text
        Pattern patt = Pattern.compile("(?<!\\w)@(\\w+)");
        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            String content = tweet.getText();
            Matcher matcher = patt.matcher(content);
            // Use a set to track unique mentions in the tweet
            Set<String> mentions = new HashSet<>();
            // Loop through all mentions found in the tweet content
            while (matcher.find()) {
                String mentionedUser = matcher.group(1).toLowerCase();
                // Add to mentions if it is not a self-mention
                if (!mentionedUser.equals(author)) {
                    mentions.add(mentionedUser);
                }
            }
            // Only add the author if there are mentions
            if (!mentions.isEmpty()) {
                followsGraph.putIfAbsent(author, new HashSet<>());
                followsGraph.get(author).addAll(mentions);
            }
        }
        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> count = new HashMap<>();
        // Count followers for each user
        for (Set<String> followed : followsGraph.values()) {
            for (String followedUser : followed) {
                count.put(followedUser, count.getOrDefault(followedUser, 0) + 1);
            }
        }
        // Create a list of influencers based on the follower counts
        List<String> influencers = new ArrayList<>(count.keySet());
        // Sort the influencers by follower count in descending order
        influencers.sort((user1, user2) -> {
            int countComparison = Integer.compare(count.get(user2), count.get(user1));
            // If counts are equal, sort alphabetically
            if (countComparison == 0) {
                return user1.compareTo(user2);
            }
            return countComparison;
        });
        return influencers;
    }


}
