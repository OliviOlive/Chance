/*
 * 2015
 */
package oli.bbp.sim;

import java.awt.Color;

/**
 *
 * @author oliver
 */
public class MoleculeColourGroup {   
    public Color active_primary;
    public Color active_secondary;
    
    public Color history_primary;
    public Color history_secondary;
    
    public MoleculeColourGroup(Color ap, Color as, Color hp, Color hs) {
        this.active_primary = ap;
        this.active_secondary = as;
        this.history_primary = hp;
        this.history_secondary = hs;
    }
}
