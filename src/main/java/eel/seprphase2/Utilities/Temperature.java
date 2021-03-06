package eel.seprphase2.Utilities;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 *
 * @author David
 */
public class Temperature implements Serializable {

    static private final double kelvinOffset = 273.15;
    private final double degreesKelvin;

    /**
     *
     */
    public Temperature() {
        degreesKelvin = 0;
    }

    /**
     *
     * @param degreesKelvin
     */
    public Temperature(double degreesKelvin) {
        this.degreesKelvin = degreesKelvin;
    }

    /**
     *
     * @return
     */
    public double inCelsius() {
        return this.degreesKelvin - kelvinOffset;
    }

    /**
     *
     * @return
     */
    public double inKelvin() {
        return this.degreesKelvin;
    }
    
     /**
     *
     * @param other
     */
    public boolean greaterThan(Temperature other) {
        return degreesKelvin > other.degreesKelvin;
    }

    @Override
    public String toString() {
        return Format.toThreeDecimalPlaces(inCelsius());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Temperature other = (Temperature)obj;
        if (this.degreesKelvin != other.degreesKelvin) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param other
     *
     * @return
     */
    public Temperature plus(Temperature other) {
        return new Temperature(this.degreesKelvin + other.degreesKelvin);
    }

    /**
     *
     * @param other
     *
     * @return
     */
    public Temperature minus(Temperature other) {
        return new Temperature(this.degreesKelvin - other.degreesKelvin);
    }

    @Override
    public int hashCode() {
        return (int)this.degreesKelvin;
    }
}
