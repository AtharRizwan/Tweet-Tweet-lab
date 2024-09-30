/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk @ about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in @alyssa 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "bbitdiddle", "rivest talk in 30 minutes #hype @bit", d2);
    private static final Tweet tweet4 = new Tweet(4, "bbitdiddle", "@alyssa rivest talk bit@gmail.com in 30 minutes #hype", d2);
    private static final Tweet tweet5 = new Tweet(5, "bbitdiddle", "", d2);
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Testing Strategy
     * 
     * Partition the input as follows:
     * tweets.size(): 0, 1, > 1
     * Tweets having the same timespan
     * 
     */
    @Test
    public void testGetTimespanTwoTweets() {        
    	// More than 1 tweet
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
        
        // One tweet
        Timespan timespan2 = Extract.getTimespan(Arrays.asList(tweet1));
        assertEquals("expected start", d1, timespan2.getStart());
        assertEquals("expected end", d1, timespan2.getEnd());
        
        // Empty List
        Timespan timespan3 = Extract.getTimespan(Arrays.asList());
        assertEquals("expected start", Instant.EPOCH, timespan3.getStart());
        assertEquals("expected end", Instant.EPOCH, timespan3.getEnd());
        
        // Two tweets having the same Timespan
        Timespan timespan4 = Extract.getTimespan(Arrays.asList(tweet1, tweet1));
        assertEquals("expected start", d1, timespan4.getStart());
        assertEquals("expected end", d1, timespan4.getEnd());
        
    }
    
    /*
     * Testing Strategy
     * 
     * Partition the input as follows:
     * tweets.size(): 0, 1, > 1
     * mentionedUsers.size(): 0, 1, > 1
     * Tweet contains an email address
     * Tweet contains stray @
     * Mention at start, middle and end
     * Empty Text
     * 
     */
    @Test
    public void testGetMentionedUsersNoMention() {
    	// Empty list
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList());
        assertTrue("expected empty set", mentionedUsers.isEmpty());
        
        // One tweet, no mentions, stray @
        Set<String> mentionedUsers1 = Extract.getMentionedUsers(Arrays.asList(tweet1));
        assertTrue("expected empty set", mentionedUsers1.isEmpty());
        
        Set<String> expected1 = new HashSet<>(Arrays.asList("alyssa"));
        Set<String> expected2 = new HashSet<>(Arrays.asList("alyssa", "bit"));
        
        // More than one tweet, Mention in middle
        Set<String> mentionedUsers2 = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        assertEquals("expected empty set",	expected1,  mentionedUsers2);
        
        // More than one mention, mention in start and end, includes email address
        Set<String> mentionedUsers3 = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet4));
        assertEquals("expected empty set", expected2, mentionedUsers3);
        
        // Empty Text
        Set<String> mentionedUsers4 = Extract.getMentionedUsers(Arrays.asList(tweet5));
        assertTrue("expected empty set", mentionedUsers4.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
