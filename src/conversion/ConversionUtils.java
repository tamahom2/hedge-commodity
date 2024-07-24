package conversion;

public class ConversionUtils {

    private static final double BARREL_CL_TO_MT_CONVERSION_FACTOR = 7.33;
    
    private static final double BARREL_WTI_TO_MT_CONVERSION_FACTOR = 7.62;

    
    
    // Convert price from $/barrel to $/MT
    public static double convertBarrelCLToMT(double pricePerBarrel) {
        return pricePerBarrel * BARREL_CL_TO_MT_CONVERSION_FACTOR;
    }
    
    public static double convertBarrelWTIToMT(double pricePerBarrel) {
        return pricePerBarrel * BARREL_WTI_TO_MT_CONVERSION_FACTOR;
    }
    
}