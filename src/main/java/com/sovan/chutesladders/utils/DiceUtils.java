package com.sovan.chutesladders.utils;

import java.util.Random;

/**
 * Utility class for rolling dice.
 */
public class DiceUtils {

    private static Random random  = new Random();

    /**
     * Rolls a dice with the given number of faces.
     *
     * @param numberOfFaces the number of faces on the dice
     * @return the result of the roll
     */
    public static int roll(int numberOfFaces){
        return random.nextInt(numberOfFaces)+1;
    }

}
