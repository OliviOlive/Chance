/*
 * 2015
 */
package oli.bbp.sim;

/**
 *
 * @author oliver
 */
public class ParticleSimulator {
    public Molecule[] mols;
    public int molCount;
    
    public int simWidth, simHeight;
    
    public double simSpeed = 0.1;
    
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
