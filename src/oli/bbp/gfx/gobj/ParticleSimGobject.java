/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Graphics2D;
import oli.bbp.sim.ParticleSimulator;
import org.json.JSONObject;

/**
 *
 * @author oliver
 */
public class ParticleSimGobject extends Gobject {
    
    ParticleSimulator psim;
    
    public ParticleSimGobject(String id, JSONObject ja) {
        super(id, ja);
    }

    @Override
    public void draw(Graphics2D g2d) {
        
    }
    
}
