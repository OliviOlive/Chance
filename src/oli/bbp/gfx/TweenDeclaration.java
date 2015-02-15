/*
 * 2015
 */
package oli.bbp.gfx;

import java.util.ArrayList;
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
}
