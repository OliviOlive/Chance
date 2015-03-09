/*
 * 2015
 */
package oli.bbp.gfx;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import oli.bbp.gfx.gobj.Gobject;
import org.json.JSONArray;

/**
 *
 * @author oliver
 */
public class TweenDeclaration {
    /**
     * A static list of all declared Tweens.
     */
    public static ArrayList<TweenDeclaration> atd = new ArrayList<>();

    public ArrayList<Object> gobmem;
    
    public String affectedProperty;
    public Gobject affectedGobject;
    public int startFrame;
    public Integer endFrame; // a null value means it is instant (no tweening)
    public JSONArray json;
    
    public TweenDeclaration(String affProp, Gobject affectedGobject, int startFrame, Integer endFrame, JSONArray json) {
        this.gobmem = new ArrayList<>();
        this.affectedProperty = affProp;
        this.affectedGobject = affectedGobject;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.json = json;
    }
    
    @Override
    public String toString() {
        return "TweenDeclaration(" + this.affectedGobject + " " + this.affectedProperty + " at " + this.startFrame + " until " + this.endFrame + ")";
    }
    
    public static class TweenSimpleHandlers {
        public static int simpleUpdateField(String propName, TweenDeclaration td, int nowVal) {
            if (! td.affectedProperty.equals(propName)) return nowVal;
            if (td.endFrame == null) {
                if (td.startFrame == OliRenderer.frameNum) {
                    return td.json.getInt(0);
                } else {
                    return nowVal;                    
                }
            }
            
            if (td.startFrame == OliRenderer.frameNum) { // first frame
                // store initial value
                td.gobmem.add(nowVal);
                
                // store gradient
                td.gobmem.add((double) (td.json.getDouble(0) - nowVal) / (td.endFrame - td.startFrame));
            }
            
            return (int) Math.floor((double) td.gobmem.get(0) + ((double) td.gobmem.get(1) * (OliRenderer.frameNum - td.startFrame)));
        }
        
        public static float simpleUpdateField(String propName, TweenDeclaration td, float nowVal) {
            if (! td.affectedProperty.equals(propName)) return nowVal;
            if (td.endFrame == null) {
                if (td.startFrame == OliRenderer.frameNum) {
                    return (float) td.json.getDouble(0);
                } else {
                    return nowVal;
                }
            }
            
            if (td.startFrame == OliRenderer.frameNum) { // first frame
                // store initial value
                td.gobmem.add(nowVal);
                
                // store gradient
                td.gobmem.add((float) (td.json.getDouble(0) - nowVal) / (td.endFrame - td.startFrame));
            }
            
            return (float) td.gobmem.get(0) + ((float) td.gobmem.get(1) * (OliRenderer.frameNum - td.startFrame));
        }
        
        public static double simpleUpdateField(String propName, TweenDeclaration td, double nowVal) {
            if (! td.affectedProperty.equals(propName)) return nowVal;
            if (td.endFrame == null) {
                if (td.startFrame == OliRenderer.frameNum) {
                    return td.json.getDouble(0);
                } else {
                    return nowVal;
                }
            }
            
            if (td.startFrame == OliRenderer.frameNum) { // first frame
                // store initial value
                td.gobmem.add(nowVal);
                
                // store gradient
                td.gobmem.add((double) (td.json.getDouble(0) - nowVal) / (td.endFrame - td.startFrame));
            }
            
            return (double) td.gobmem.get(0) + ((double) td.gobmem.get(1) * (OliRenderer.frameNum - td.startFrame));
        }
        
        public static boolean simpleBooleanField(String propName, TweenDeclaration td, boolean nowVal) {
            if (! td.affectedProperty.equals(propName)) {
                return nowVal;
            }
            
            if (td.endFrame != null) {
                Logger.getLogger(td.toString()).warning("Ignored Tween. Boolean properties cannot be tweened, only set by instant changes.");
                return nowVal;
            }
            
            if (td.startFrame == OliRenderer.frameNum) {
                return td.json.getBoolean(0);
            } else {
                return nowVal;
            }
        }
        
        public static Color simpleUpdateColourRGB(String propName, TweenDeclaration td, Color nowVal) {
            if (! td.affectedProperty.equals(propName)) return nowVal;
            if (td.endFrame == null) {
                if (td.startFrame == OliRenderer.frameNum) {
                    return ScriptReader.strToColour(td.json.getString(0));
                } else {
                    return nowVal;
                }
            }
            
            if (td.startFrame == OliRenderer.frameNum) { // first frame
                // store initial value
                td.gobmem.add(nowVal.getRed());
                td.gobmem.add(nowVal.getGreen());
                td.gobmem.add(nowVal.getBlue());
                
                double frameDivisor = td.endFrame - td.startFrame;
                
                double redGradient = (double) (ScriptReader.strToColour(td.json.getString(0)).getRed() - nowVal.getRed()) / frameDivisor;
                double greenGradient = (double) (ScriptReader.strToColour(td.json.getString(0)).getGreen() - nowVal.getGreen()) / frameDivisor;
                double blueGradient = (double) (ScriptReader.strToColour(td.json.getString(0)).getBlue() - nowVal.getBlue()) / frameDivisor;
                
                // store gradient
                td.gobmem.add(redGradient);
                td.gobmem.add(greenGradient);
                td.gobmem.add(blueGradient);
            }
            
            int frameMultiplier = OliRenderer.frameNum - td.startFrame;
            
            double red = ((double) td.gobmem.get(3) * frameMultiplier) + (int) td.gobmem.get(0);
            double green = ((double) td.gobmem.get(4) * frameMultiplier) + (int) td.gobmem.get(1);
            double blue = ((double) td.gobmem.get(5) * frameMultiplier) + (int) td.gobmem.get(2);
            
            if ((int) red > 255) red = 255;
            if ((int) green > 255) green = 255;
            if ((int) blue > 255) blue = 255;
            
            if ((int) red < 0) red = 0;
            if ((int) green < 0) green = 0;
            if ((int) blue < 0) blue = 0;
            
            return new Color((int) red, (int) green, (int) blue);
        }
    }
}
