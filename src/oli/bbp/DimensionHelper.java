/*
 * 2015
 */
package oli.bbp;

/**
 *
 * @author oliver
 */
public class DimensionHelper {
    
    public static final int RESOLUTION_WIDTH = 1920;
    public static final int RESOLUTION_HEIGHT = 1080;
    public static final int FRAMES_PER_SECOND = 30;
    
    /**
     * Gets the X coordinate/size from the input integer or string.
     * @param in
     * @return 
     */
    public static int getX(Object in) {
        if (in instanceof Integer) return (int) in;
        if (in instanceof String) {
            String ins = (String) in;
            if (ins.charAt(ins.length() - 1) == '%') {
                String sub = ins.substring(0, ins.length() - 2);
                Double percent = Double.parseDouble(sub);
                return (int) Math.floor((percent / 100) * RESOLUTION_WIDTH);
            } else {
                throw new ScriptReader.ScriptFormatException("Unknown size: " + ins);
            }
        }
        return 0;
    }
    
    /**
     * Gets the Y coordinate/size from the input integer or string.
     * @param in - INTEGER or STRING(num + '%')
     * @return 
     */
    public static int getY(Object in) {
        if (in instanceof Integer) return (int) in;
        if (in instanceof String) {
            String ins = (String) in;
            if (ins.charAt(ins.length() - 1) == '%') {
                String sub = ins.substring(0, ins.length() - 2);
                Double percent = Double.parseDouble(sub);
                return (int) Math.floor((percent / 100) * RESOLUTION_HEIGHT);
            } else {
                throw new ScriptReader.ScriptFormatException("Unknown size: " + ins);
            }
        }
        return 0;
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
