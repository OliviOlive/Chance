/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Graphics2D;
import oli.bbp.gfx.TweenDeclaration;
import org.json.JSONObject;

/**
 *
 * @author oliver
 */
public class TextGobject extends Gobject {

    public TextGobject(String id, JSONObject ja) {
        super(id, ja);
    }
    
    @Override
    public void tween(TweenDeclaration td, int frameNum) {
        super.tween(td, frameNum);
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        
    }
    
}
