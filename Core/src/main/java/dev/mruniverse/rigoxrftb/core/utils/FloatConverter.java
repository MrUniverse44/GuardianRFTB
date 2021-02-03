package dev.mruniverse.rigoxrftb.core.utils;

public class FloatConverter {
    public float converter(double number) {
        if(number >= 10) return 1.0F;
        if(number == 9) return 0.9F;
        if(number == 8) return 0.8F;
        if(number == 7) return 0.7F;
        if(number == 6) return 0.6F;
        if(number == 5) return 0.5F;
        if(number == 4) return 0.4F;
        if(number == 3) return 0.3F;
        if(number == 2) return 0.2F;
        return 0.1F;
    }
    public int meters(double number) {
        return (int)number;
    }
}
