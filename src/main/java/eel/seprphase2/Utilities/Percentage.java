/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eel.seprphase2.Utilities;

import static java.lang.Math.round;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author drm
 */
public class Percentage {

    private static final Pattern pattern = Pattern.compile("^([0-9]+)%?$");
    private final int percentagePoints;

    public Percentage(int percentagePoints) {
        if (!isValidPercentage(percentagePoints)) {
            throw new IllegalArgumentException("The argument (" +
                                               percentagePoints +
                                               ") is outside the valid range" +
                                               " for percentages [0 - 100]");
        }
        this.percentagePoints = percentagePoints;
    }

    public Percentage(double ratio) {
        this((int) round(ratio * 100));
    }

    public Percentage(String representation) {
        this(pointsFromString(representation));
    }

    public int percentagePoints() {
        return this.percentagePoints;
    }

    public double ratio() {
        return this.percentagePoints / 100.0;
    }
    
    public String toString() {
        return percentagePoints + "%";
    }
    
    
    
    public static boolean isValidPercentage(int points) {
        return points <= 100 && points >= 0;
    }

    public static boolean isValidPercentage(String representation) {
        return isWellFormedPercentage(representation) &&
               isValidPercentage(pointsFromString(representation));
    }

    
    
    
    private static boolean isWellFormedPercentage(String representation) {
        Matcher m = pattern.matcher(representation);
        return m.matches();
    }

    private static int pointsFromString(String representation) {
        Matcher m = pattern.matcher(representation);
        if (!m.matches()) {
            throw new IllegalArgumentException("The string '" +
                                               representation +
                                               "' is not a well-formed percentage.");
        }
        return Integer.parseInt(m.group(1));
    }
    
}