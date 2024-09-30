/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T15:00:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T09:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "is it reasonable to talk so much?", d3);
    private static final Tweet tweet4 = new Tweet(3, "alyssa", "talk about rivest", d4);
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Testing Strategy
     * 
     * Partition the input as follows:
     * tweets.size(): 0, 1, > 1
     * result.size(): 0, 1, > 1
     * Author name should be case-insensitive
     * 
     */
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByMultipleTweetsMultipleResults() {
        List<Tweet> tweetList = Arrays.asList(tweet1, tweet2, tweet3);
        List<Tweet> writtenBy = Filter.writtenBy(tweetList, "alyssa");
        
        assertEquals("expected list size of 2", 2, writtenBy.size());
        assertTrue("expected list to contain tweet1", writtenBy.contains(tweet1));
        assertTrue("expected list to contain tweet3", writtenBy.contains(tweet3));
        assertFalse("expected list not to contain tweet2", writtenBy.contains(tweet2));
    }

    @Test
    public void testWrittenBySingleTweetByUser() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet1", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "Alyssa"); 
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet1", writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByEmptyList() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "alyssa");
        
        assertTrue("expected empty list for empty input", writtenBy.isEmpty());
    }

    
    /*
     * Testing Strategy
     * 
     * Partition the input as follows:
     * tweets.size(): 0, 1, > 1
     * result.size(): 0, 1, > 1
     * Timestamp coincides with Start or End time
     * Tweets outside the timespan
     * 
     */
    Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
    Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    @Test
    public void testInTimespanEmptyList() {
        List<Tweet> result = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
        assertTrue("Expected empty list for empty input", result.isEmpty());
    }

    @Test
    public void testInTimespanSingleTweetWithin() {
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        assertEquals("Expected list size of 1", 1, result.size());
        assertTrue("Expected list to contain tweet1", result.contains(tweet1));
    }

    @Test
    public void testInTimespanSingleTweetOutside() {
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet3), new Timespan(testStart, testEnd));
        assertTrue("Expected empty list for tweet outside timespan", result.isEmpty());
    }

    @Test
    public void testInTimespanMultipleTweetsSomeWithin() {
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), new Timespan(testStart, testEnd));
        assertEquals("Expected list size of 2", 2, result.size());
        assertTrue("Expected list to contain tweet1", result.contains(tweet1));
        assertTrue("Expected list to contain tweet3", result.contains(tweet2));
        assertFalse("Expected list not to contain tweet2", result.contains(tweet3));
    }

    @Test
    public void testInTimespanEdgeCaseInclusive() {
        // Check inclusion of edge timestamps
        List<Tweet> result = Filter.inTimespan(Arrays.asList(tweet4), new Timespan(testStart, testEnd));
        assertEquals("Expected list size of 1 for edge cases", 1, result.size());
        assertTrue("Expected list to contain tweet4", result.contains(tweet4));
    }

    /*
     * Testing Strategy
     * 
     * Partition the input as follows:
     * tweets.size(): 0, 1, > 1
     * result.size(): 0, 1, > 1
     * Test correct order
     * 
     */
    @Test
    public void testContainingMultipleTweets() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    @Test
    public void testContainingSingleTweet() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    @Test
    public void testContainingNoTweet() {
        List<Tweet> containing = Filter.containing(Arrays.asList(), Arrays.asList("talk"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
