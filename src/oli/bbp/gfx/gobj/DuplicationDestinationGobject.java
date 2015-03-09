/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Graphics2D;
import oli.bbp.gfx.TweenDeclaration;
import org.json.JSONObject;

/**
 * A dummy Gobject with the sole intention of being replaced by a duplicate of
 * an already existing Gobject.
 * @author oliver
 */
public class DuplicationDestinationGobject extends Gobject {
    
    public Gobject supersededBy;
    
    public DuplicationDestinationGobject(String id, JSONObject ja) {
        super(id, ja);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (supersededBy == null)
            throw new UnsupportedOperationException("DuplicationDestinationGobject cannot be drawn, until they are replaced. Fix this issue within your script. Note: To avoid a Gobject from being drawn, set its opacity to 0!");
        this.supersededBy.draw(g2d);
    }
    
    /**
     * Replaces this Gobject. All tween and instantChanges will be redirected.
     * @param by 
     */
    public void supersede(Gobject by) {
        supersededBy = by;
        supersededBy.bounds = this.bounds;
    }
    
    @Override
    public void tween(TweenDeclaration td, int frameNum) {
        if (supersededBy == null)
            throw new UnsupportedOperationException("DuplicationDestinationGobject cannot handle tweens, until they are replaced. Fix this issue within your script.");
        
        if (td.affectedProperty.equals("free")) {
            this.supersededBy = null;
            return;
        }
        
        supersededBy.tween(td, frameNum);
        
        this.opacity = supersededBy.opacity; // pretty important
    }
    
    @Override
    public void instantChange(TweenDeclaration td, int frameNum) {
        if (supersededBy == null)
            throw new UnsupportedOperationException("DuplicationDestinationGobject cannot handle instant changes, until they are replaced. Fix this issue within your script.");
        
        supersededBy.instantChange(td, frameNum);
        
        this.opacity = supersededBy.opacity; // pretty important
    }
    
}
