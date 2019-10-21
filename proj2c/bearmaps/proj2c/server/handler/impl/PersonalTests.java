package bearmaps.proj2c.server.handler.impl;

import org.junit.Assert;
import org.junit.Test;

public class PersonalTests {
    RasterAPIHandler tester = new RasterAPIHandler();

    /** Tests our lonDPP helper using the numbers given from the project spec.
     *  This translates to about 25 feet of distance per pixel.
     *  */
    @Test
    public void lonDPPHelperTest() {
        double expected = 0.00008630532;
        double actual = tester.lonDPPHelper(-122.2104604264636, -122.30410170759153, 1085);
        Assert.assertEquals(expected, actual, 0.0005);
    }

    /** Tests the depth calculation process. */
    @Test
    public void depthTest() {
        int expected = 2;
        int actual = tester.depthHelper(-122.2104604264636, -122.30410170759153, 1085);
        Assert.assertEquals(expected, actual);

        int expected2 = 0;
        int actual2 = tester.depthHelper(-122.2119140625,-122.2998046875, 256);
        Assert.assertEquals(expected2, actual2);

    }
    /** Tests the stepSize calculations. */
    @Test
    public void stepSizeTest() {
        double expected = Math.abs(-122.29980468 - -122.21191406) / 4;
        double actual = tester.stepSizeXHelper(2);
        Assert.assertEquals(expected, actual, .0000005);

        double expected2 = Math.abs(-122.29980468 - -122.21191406) / 8;
        double actual2 = tester.stepSizeXHelper(3);
        Assert.assertEquals(expected2, actual2, .0000005);

        double expected3 = Math.abs(-122.29980468 - -122.21191406) / 16;
        double actual3 = tester.stepSizeXHelper(4);
        Assert.assertEquals(expected2, actual2, .0000005);
    }

    /** Tests the startXIndex calculator. */
    @Test
    public void startXTest() {

        /** Big map. */
        int expected = 0;
        int depth = tester.depthHelper(-122.2119140625,-122.2998046875, 256);
        int actual = tester.startXIndex(-122.29980468, tester.stepSizeXHelper(depth));
        Assert.assertEquals(expected, actual);

        /** Example map. */
        int expected2 = 0;
        int depth2 = tester.depthHelper(-122.2104604264636, -122.30410170759153, 1085);
        int actual2 = tester.startXIndex(-122.30410170759153, tester.stepSizeXHelper(depth2));
        Assert.assertEquals(expected2, actual2);

    }

    /** Tests the endXIndex calculator. */
    @Test
    public void endXTest() {

        /** Big map. */
        int expected = 0;
        int depth = tester.depthHelper(-122.2119140625,-122.2998046875, 256);
        int actual = tester.endXIndex(-122.29980468, tester.stepSizeXHelper(depth), depth);
        Assert.assertEquals(expected, actual);

        /** Example map. */
        int expected2 = 3;
        int depth2 = tester.depthHelper(-122.2104604264636,-122.30410170759153, 1085);
        int actual2 = tester.endXIndex(-122.2104604264636, tester.stepSizeXHelper(depth2), depth2);
        Assert.assertEquals(expected2, actual2);
    }

    /** Tests the startYIndex calculator. */
    @Test
    public void startYTest() {

        /** Big map. */
        int expected = 0;
        int depth = tester.depthHelper(-122.2119140625,-122.2998046875, 256);
        int actual = tester.startYIndex(37.89219554, tester.stepSizeYHelper(depth));
        Assert.assertEquals(expected, actual);

        /** Example map. */
        int expected2 = 1;
        int depth2 = tester.depthHelper(-122.2104604264636, -122.30410170759153, 1085);
        int actual2 = tester.startYIndex(37.870213571328854, tester.stepSizeYHelper(depth2));
        Assert.assertEquals(expected2, actual2);
    }

    /** Tests the startXIndex calculator. */
    @Test
    public void endYTest() {

        /** Big map. */
        int expected = 0;
        int depth = tester.depthHelper(-122.2119140625,-122.2998046875, 256);
        int actual = tester.endYIndex(37.89219554, tester.stepSizeYHelper(depth), depth);
        Assert.assertEquals(expected, actual);

        /** Example map. */
        int expected2 = 3;
        int depth2 = tester.depthHelper(-122.2104604264636, -122.30410170759153, 1085);
        int actual2 = tester.endYIndex(37.8318576119893, tester.stepSizeYHelper(depth2), depth2);
        Assert.assertEquals(expected2, actual2);
    }
}
