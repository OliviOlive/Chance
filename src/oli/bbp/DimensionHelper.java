/*
 * 2015
 */
package oli.bbp;

/**
 * Class for Dimension & Coordinate manipulation. Note that time /is/ an included dimension.
 * @author oliver
 */
public class DimensionHelper {
    public static final int RESOLUTION_WIDTH = 1920;
    public static final int RESOLUTION_HEIGHT = 1080;
    
    // it is recommended that -Dolibbp.framerate=60 argument is provided when exporting
    public static final int FRAMES_PER_SECOND = Integer.parseInt(System.getProperty("olibbp.framerate", "30"));
    
    // downscale from production resolution to display resolution
    private static final double DOWNSCALE = 0.666666;
    
    public static double CURRENT_DOWNSCALE = DOWNSCALE;
    
    
    public static int getOf(Object in, int of) {
        if (in instanceof Integer) return (int) in;
        if (in instanceof String) {
            String ins = (String) in;
            if (ins.charAt(ins.length() - 1) == '%') {
                String sub = ins.substring(0, ins.length() - 1);
                Double percent = Double.parseDouble(sub);
                return (int) Math.floor((percent / 100) * of);
            } else {
                throw new ScriptReader.ScriptFormatException("Unknown size: " + ins);
            }
        }
        return 0;
    }
    
    /**
     * Gets the X coordinate/size from the input integer or string.
     * @param in
     * @return 
     */
    public static int getX(Object in) {
        return getOf(in, RESOLUTION_WIDTH);
    }
    
    /**
     * Gets the Y coordinate/size from the input integer or string.
     * @param in - INTEGER or STRING(num + '%')
     * @return 
     */
    public static int getY(Object in) {
        return getOf(in, RESOLUTION_HEIGHT);
    }
    
    /**
     * A method to scale down dimensions to 1280x720 (from 1920x1080)
     * @param in
     * @return 
     */
    public static int scaleDimensions(int in) {
        return (int) Math.floor(in * DOWNSCALE);
    }
    
    public static float scaleDimensions(float in) {
        return (float) Math.floor(in * DOWNSCALE);
    }
    
    public static double scaleDimensions(double in) {
        return Math.floor(in * DOWNSCALE);
    }
    
    public static int getRealDimensions(int in) {
        if (Main.isOnscreen) return scaleDimensions(in);
        return in;
    }
    
    public static float getRealDimensions(float in) {
        if (Main.isOnscreen) return scaleDimensions(in);
        return in;
    }
    
    public static double getRealDimensions(double in) {
        if (Main.isOnscreen) return scaleDimensions(in);
        return in;
    }
    
    /**
     * Converts seconds to number of video frames.
     * @param seconds
     * @return 
     */
    public static int getFramesFromSeconds(Double seconds) {
        return (int) Math.floor(seconds * FRAMES_PER_SECOND);
    }
}
