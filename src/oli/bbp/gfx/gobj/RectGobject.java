/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Color;
import java.awt.Graphics2D;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import oli.bbp.gfx.TweenDeclaration;
import org.json.JSONObject;

/**
 * A rectangle Gobject.
 * @author oliver
 */
public class RectGobject extends Gobject {
    
    public boolean isFilled = false;
    public Color col;
    
    public RectGobject(String id, JSONObject ja) {
        super(id, ja);
        
        isFilled = ja.optBoolean("filled", false);
        col = ScriptReader.strToColour(ja.optString("colour", "#FFFFFF"));
    }

    @Override
    public void instantChange(TweenDeclaration td, int frameNum) {
        super.instantChange(td, frameNum);
        
        this.common(td, frameNum);
    }

    @Override
    public void tween(TweenDeclaration td, int frameNum) {
        super.tween(td, frameNum);
        
        this.common(td, frameNum);
    }
    
    public void common(TweenDeclaration td, int frameNum) {
        this.col = TweenDeclaration.TweenSimpleHandlers.simpleUpdateColourRGB("colour", td, col);
    }
    

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(this.col);
        if (isFilled) {
            g2d.fillRect(
                    DimensionHelper.getRealDimensions(this.bounds.x),
                    DimensionHelper.getRealDimensions(this.bounds.y),
                    DimensionHelper.getRealDimensions(this.bounds.width),
                    DimensionHelper.getRealDimensions(this.bounds.height)
            );
        } else {
            g2d.drawRect(
                    DimensionHelper.getRealDimensions(this.bounds.x),
                    DimensionHelper.getRealDimensions(this.bounds.y),
                    DimensionHelper.getRealDimensions(this.bounds.width),
                    DimensionHelper.getRealDimensions(this.bounds.height)
            );
        }
    }
    
}
