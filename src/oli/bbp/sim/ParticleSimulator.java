/*
 * 2015
 */
package oli.bbp.sim;

import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import oli.bbp.sim.Molecule.MolVector2D;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author oliver
 */
public class ParticleSimulator {    
    public Molecule[] mols;
    public int molCount;
    
    public int simWidth, simHeight;
    
    public double simSpeed = 0;
    
    public ParticleSimulator(JSONObject config) {
        Object o = config.get("size");
        ScriptReader.ScriptFormatException.assertion(
                (o != null) && (o instanceof JSONArray),
                "Sim: 'size' should be array."
        );
        JSONArray ja = (JSONArray) o;
        ScriptReader.ScriptFormatException.assertion(
                ja.length() == 2,
                "Sim: 'size'.length!=2"
        );
        this.simWidth = DimensionHelper.getX(ja.get(0));
        this.simHeight = DimensionHelper.getY(ja.get(1));
        
        if (config.has("particles")) {
            o = config.get("particles");
            ScriptReader.ScriptFormatException.assertion(
                    o instanceof JSONArray,
                    "Sim: 'particles' should be array."
            );
            ja = (JSONArray) o;
            int parl = ja.length();
            this.mols = new Molecule[parl];
            this.molCount = parl;
            for (int pari = 0; pari < parl; ++pari) {
                JSONArray parja = ja.getJSONArray(pari);
                int radius = parja.getInt(0);
                int mass = parja.getInt(1);
                String colGroup = parja.getString(2);
                int posX = DimensionHelper.getOf(parja.getString(3), this.simWidth);
                int posY = DimensionHelper.getOf(parja.getString(4), this.simHeight);
                
                double momX = parja.getDouble(5);
                double momY = parja.getDouble(6);
                
                mols[pari] = new Molecule(
                        new Molecule.MolVector2D(posX, posY),
                        new Molecule.MolVector2D(momX, momY),
                        radius,
                        mass,
                        colGroup
                );
            }
        }
        
        this.simSpeed = config.optDouble("speed", 3.0) / DimensionHelper.FRAMES_PER_SECOND;
    }
    
    public void update() {
        for (int moli = 0; moli < molCount; ++moli) {
            Molecule mol = mols[moli];
            
            mol.oldBasis = new Molecule.MolVector2D(mol.basis);
            
            mol.basis.add(mol.momentum.scale(simSpeed));
            
            boolean bouncedOnWall = false;
            
            if ((mol.basis.x - mol.radius < 0) || (mol.basis.x + mol.radius > simWidth)) {
                mol.momentum.reverseX();
                if (mol.basis.x - mol.radius < 0) {
                    mol.basis.x = mol.radius; // clamp to wall
                }
                if (mol.basis.x + mol.radius > simWidth) {
                    mol.basis.x = (simWidth - 1) - mol.radius; // clamp to wall. Note simWidth-1 is max X.
                }
                bouncedOnWall = true;
            }
            if ((mol.basis.y - mol.radius < 0) || (mol.basis.y + mol.radius > simHeight)) {
                mol.momentum.reverseY();
                if (mol.basis.y - mol.radius < 0) {
                    mol.basis.y = mol.radius; // clamp to wall
                }
                if (mol.basis.y + mol.radius > simHeight) {
                    mol.basis.y = (simHeight - 1) - mol.radius; // clamp to wall. Note simHeight-1 is max Y.
                }
                bouncedOnWall = true;
            }
            
            if (! bouncedOnWall) {
                Molecule cmol = this.getCollisionForMolecule(mol);
                
                if (cmol == null) continue;
                
                if (mol.momentum.isNullVector() && cmol.momentum.isNullVector())
                    continue; // unable to collide in this condition
                
                if ((mol.momentum.x == cmol.momentum.x) && (mol.momentum.y == cmol.momentum.y)) {
                    // unable to collide in this condition so nudge mol a bit
                    mol.momentum.x += 0.0001;
                }
                
                // Figure out at what time they collided so we can rollback
                // Solve equation
                // ((x1-x2)+t(mx1-mx2))^2 + ((y1-y2)+t(my1-my2))^2 = mindist^2
                // (dmx + t(dx)) ^ 2 + (dmy + t(dy)) ^ 2 = mindist ^ 2
                
                int mindist = mol.radius + cmol.radius;
                
                double dmx = mol.momentum.x - cmol.momentum.x;
                double dmy = mol.momentum.y - cmol.momentum.y;
                
                double dx = mol.basis.x - cmol.basis.x;
                double dy = mol.basis.y - cmol.basis.y;
                
                double a = (dmx * dmx) + (dmy * dmy); // sqrt a = hypotenuse of momentum (v. magnitude)
                double b = 2 * ((dx * dmx) + (dy * dmy));
                
                double c = ((dx*dx) + (dy*dy)) - (mindist * mindist);
                
                double d = Math.sqrt((b * b) - (4 * a * c));
                
                double time = ((0 - b) - d) / a;
                double timeTwo = ((0 - b) + d) / a;
                
                if (Math.abs(time) > Math.abs(timeTwo))
                    time = timeTwo;
                
                // Rollback by time
                mol.basis.add(mol.momentum.scale(time));
                
                // now they are just touching.
                // Find vector seperating mass! & normalise
                
                dx = mol.basis.x - cmol.basis.x;
                dy = mol.basis.y - cmol.basis.y;
                
                double dnorm = Math.sqrt((dx*dx) + (dy*dy));
                double dxn = dx / dnorm;
                double dyn = dy / dnorm;
                
                int totalMass = mol.mass + cmol.mass;
                
                // find velocity of CoM
                
                MolVector2D comv = mol.momentum.scale(mol.mass);
                comv.add(cmol.momentum.scale(cmol.mass));
                comv = comv.scale(1.0 / totalMass);
                
                double projn = (dxn * (mol.momentum.x - comv.x)) + (dyn * (mol.momentum.y - comv.y));
                MolVector2D projv = new MolVector2D(2 * dxn * projn, 2 * dyn * projn);
                
                // subtract vector from mol's momentum
                mol.momentum.subtract(projv);
                
                double scalarm = (double) mol.mass / cmol.mass;
                cmol.momentum.add(projv.scale(scalarm));
                
                // now rollforth time again (time is negative)
                
                mol.basis.subtract(mol.momentum.scale(time));
                
                // repeat wall-bounce checks
                if ((mol.basis.x - mol.radius < 0) || (mol.basis.x + mol.radius > simWidth)) {
                    mol.momentum.reverseX();
                    if (mol.basis.x - mol.radius < 0) {
                        mol.basis.x = mol.radius; // clamp to wall
                    }
                    if (mol.basis.x + mol.radius > simWidth) {
                        mol.basis.x = (simWidth - 1) - mol.radius; // clamp to wall. Note simWidth-1 is max X.
                    }
                }
                if ((mol.basis.y - mol.radius < 0) || (mol.basis.y + mol.radius > simHeight)) {
                    mol.momentum.reverseY();
                    if (mol.basis.y - mol.radius < 0) {
                        mol.basis.y = mol.radius; // clamp to wall
                    }
                    if (mol.basis.y + mol.radius > simHeight) {
                        mol.basis.y = (simHeight - 1) - mol.radius; // clamp to wall. Note simHeight-1 is max Y.
                    }
                }
            }
        }
    }
    
    public Molecule getCollisionForMolecule(Molecule mol) {
        for (int moli = 0; moli < molCount; ++moli) {
            Molecule molCol = mols[moli];
            
            if (molCol == mol) { // molecules should not collide with themselves.. silly things
                continue;
            }
            
            int minimumSquaredDistanceForNoCollision = mol.radius + molCol.radius;
            minimumSquaredDistanceForNoCollision *= minimumSquaredDistanceForNoCollision;
            
            if (mol.basis.distanceToSquared(molCol.basis) > minimumSquaredDistanceForNoCollision) {
                continue; // too far away for a collision
            }
            
            return molCol;
        }
        return null;
    }
}
