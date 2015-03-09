/*
 * 2015
 */
package oli.bbp.sim;

import java.util.HashMap;

/**
 *
 * @author oliver
 */
public class ParticleSimStateSave {
    public static class HistorySave {
        public static class OldMolecule {
            public int radius, mass;
            public Molecule.MolVector2D basis;
            public String colGroup;
            public OldMolecule(int radius, int mass, Molecule.MolVector2D basis, String colGroup) {
                this.radius = radius;
                this.mass = mass;
                this.basis = basis.scale(1);
                this.colGroup = colGroup;
            }
        }
        public OldMolecule[] oldMols;
        public HistorySave(ParticleSimulator psim) {
            oldMols = new OldMolecule[psim.mols.length];
            for (int i = 0; i < psim.mols.length; ++i) {
                Molecule mol = psim.mols[i];
                oldMols[i] = new OldMolecule(mol.radius, mol.mass, mol.basis, mol.colGroup);
            }
        }
    }
    
    public static HashMap<String, ParticleSimStateSave> saves = new HashMap<>();
    
    public Molecule[] mols;
    public long simTime;
    public int simWidth, simHeight;
    
    public ParticleSimStateSave(ParticleSimulator psim) {
        this.simTime = psim.simTime;
        this.simWidth = psim.simWidth;
        this.simHeight = psim.simHeight;
        
        this.mols = new Molecule[psim.mols.length];
        
        for (int i = 0; i < psim.mols.length; ++i) {
            this.mols[i] = new Molecule(psim.mols[i]);
        }
    }
    
    public void writeToSimulator(ParticleSimulator psim) {
        psim.simTime = this.simTime;
        psim.simWidth = this.simWidth;
        psim.simHeight = this.simHeight;
        psim.mols = new Molecule[this.mols.length];
        
        for (int i = 0; i < this.mols.length; ++i) {
            psim.mols[i] = new Molecule(this.mols[i]);
        }
    }
    
    public static void savePSim(String name, ParticleSimulator psim) {
        saves.put(name, new ParticleSimStateSave(psim));
    }
    
    public static void loadPSim(String name, ParticleSimulator psim) {
        saves.get(name).writeToSimulator(psim);
    }
}
