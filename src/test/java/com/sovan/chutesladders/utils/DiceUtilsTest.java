package com.sovan.chutesladders.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiceUtilsTest {

    @Test
    void testRoll() {
        int numberOfFaces = 6;
        for (int i = 0; i < 100; i++) {
            int roll = DiceUtils.roll(numberOfFaces);
            assertTrue(roll >= 1 && roll <= numberOfFaces, "Roll should be within the valid range");
        }
    }
}
