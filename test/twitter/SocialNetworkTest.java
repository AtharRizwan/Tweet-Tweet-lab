/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.junit.Test;


/*
 * Testing strategy
 *
 * Partition the inputs as follows:
 * tweets.size(): 0, 1, > 1
 * followsGraph.size(): 0, 1, > 1
 * 
 * Partition the outputs as follows:
 * followsGraph.size(): 0, 1, > 1
 * influencers.size(): 0, 1, > 1
 */
public class SocialNetworkTest {
	
	// Tweets with no Mentions
	private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", Instant.now());
	private static final Tweet tweet2 = new Tweet(5, "bbitdiddle", "", Instant.now());
	
	// Tweets with Single Mention
	private static final Tweet tweet3 = new Tweet(2, "bbitdiddle", "rivest talk in @alyssa 30 minutes #hype", Instant.now());
	private static final Tweet tweet4 = new Tweet(3, "alyssa", "rivest talk in 30 minutes #hype @bbitdiddle", Instant.now());
	
	// Tweets with Multiple Mentions
	private static final Tweet tweet5 = new Tweet(4, "bbitdiddle", "@alyssa rivest @bbitdiddle talk bit@gmail.com in @ernie 30 minutes #hype", Instant.now());
	private static final Tweet tweet6 = new Tweet(4, "alyssa", "@ernie rivest talk in 30 minutes @bert #hype", Instant.now());
	
	
	
    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
	// covers tweets.size() = 0,
	// followsGraph.size() = 0
    @Test
    public void testGuessFollowsGraphNoTweets() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
	// covers tweets.size() = 1, > 1
	// followsGraph.size() = 0
    // Tweets contain no mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Map<String, Set<String>> followsGraph1 = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));
        Map<String, Set<String>> followsGraph2 = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2));
        
        assertTrue("expected empty graph", followsGraph1.isEmpty());
        assertTrue("expected empty graph", followsGraph2.isEmpty());
    }

	// covers tweets.size() = 1, > 1
	// followsGraph.size() = 1, > 1
    // Each tweet contains a single mention
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Map<String, Set<String>> followsGraph1 = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3));
        Map<String, Set<String>> followsGraph2 = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet4));
        
        Map<String, Set<String>> out = new HashMap<>();
        out.put("bbitdiddle", new HashSet<>(Arrays.asList("alyssa")));        
        assertEquals("Doesn't handle single mentions correctly", out, followsGraph1);
        out.put("alyssa", new HashSet<>(Arrays.asList("bbitdiddle")));
        assertEquals("Doesn't handle single mentions correctly", out, followsGraph2);
    }
    
	// covers tweets.size() = 1, > 1
	// followsGraph.size() = 1, > 1
    // Each tweet contains multiple mentions
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Map<String, Set<String>> followsGraph1 = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet5));
        Map<String, Set<String>> followsGraph2 = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet5, tweet6));
        
        Map<String, Set<String>> out = new HashMap<>();
        out.put("bbitdiddle", new HashSet<>(Arrays.asList("alyssa", "ernie")));        
        assertEquals("Doesn't handle multiple mentions correctly", out, followsGraph1);
        out.put("alyssa", new HashSet<>(Arrays.asList("ernie", "bert")));
        assertEquals("Doesn't handle multiple mentions correctly", out, followsGraph2);
    }
    
	// covers tweets.size() = > 1
	// followsGraph.size() = 1
    // Tweets contain the same author
    @Test
    public void testGuessFollowsGraphSameAuthor() {
        Map<String, Set<String>> followsGraph1 = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4, tweet6));
        
        Map<String, Set<String>> out = new HashMap<>();
        out.put("alyssa", new HashSet<>(Arrays.asList("bbitdiddle", "bert", "ernie")));        
        assertEquals("Doesn't handle same author correctly", out, followsGraph1);
    }
    
	// covers followsGraph.size() = 0
	// influencers.size() = 0
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
	// covers followsGraph.size() = 1
	// influencers.size() = 0
    @Test
    public void testInfluencersNoFollowing() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bbitdiddle", new HashSet<>(Arrays.asList())); 
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

	// covers followsGraph.size() = 1, > 1
	// influencers.size() = 1
    @Test
    public void testInfluencersSingleUser() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bbitdiddle", new HashSet<>(Arrays.asList("alyssa")));
        List<String> influencers1 = SocialNetwork.influencers(followsGraph);
        followsGraph.put("ernie", new HashSet<>(Arrays.asList("alyssa")));
        List<String> influencers2 = SocialNetwork.influencers(followsGraph);
        List<String> out = Arrays.asList("alyssa");
        
        assertEquals("Doesn't handle single mentions correctly", out, influencers1);
        assertEquals("Doesn't handle single mentions correctly", out, influencers2);
    }
    
	// covers followsGraph.size() = 1, > 1
	// influencers.size() = > 1
    @Test
    public void testInfluencersMultipleUsers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bbitdiddle", new HashSet<>(Arrays.asList("alyssa", "bert")));
        followsGraph.put("ernie", new HashSet<>(Arrays.asList("alyssa")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        List<String> out = Arrays.asList("alyssa", "bert");
        
        assertEquals("Doesn't handle single mentions correctly", out, influencers);
    }
    
	// covers followsGraph.size() = 1, > 1
	// influencers.size() = > 1
    // Influencers have the same number of followers
    @Test
    public void testInfluencersSameFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("bbitdiddle", new HashSet<>(Arrays.asList("alyssa", "bert")));
        followsGraph.put("ernie", new HashSet<>(Arrays.asList("alyssa", "bert")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        List<String> out = Arrays.asList("alyssa", "bert");
        
        assertEquals("Doesn't handle single mentions correctly", out, influencers);
    }
    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
