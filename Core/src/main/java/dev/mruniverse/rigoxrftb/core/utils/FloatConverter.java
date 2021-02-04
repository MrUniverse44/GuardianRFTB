package dev.mruniverse.rigoxrftb.core.utils;

public class FloatConverter {
    public float converter(double number) {
        //if(number >= 10) return 100F;
        //if(number == 9) return 90F;
        //if(number == 8) return 80F;
        //if(number == 7) return 70F;
        //if(number == 6) return 60F;
        //if(number == 5) return 50F;
        //if(number == 4) return 40F;
        //if(number == 3) return 30F;
        //if(number == 2) return 20F;
        //if(number == 1) return 10F;
        //return 5F;
        return (float)(number / 20.D);
    }
    public int meters(double number) {
        return (int)number;
    }
}
