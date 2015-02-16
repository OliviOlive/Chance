/*
 * 2015
 */
package oli.bbp.sim;

import java.awt.Color;

/**
 *
 * @author oliver
 */
public class Molecule {
    public static class MolVector2D {
        public double x, y;
        
        public MolVector2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public MolVector2D(MolVector2D mv2d) {
            this.x = mv2d.x;
            this.y = mv2d.y;
        }
        
        public void add(MolVector2D mv2d) {
            this.x += mv2d.x;
            this.y += mv2d.y;
        }
        
        public void subtract(MolVector2D mv2d) {
            this.x -= mv2d.x;
            this.y -= mv2d.y;
        }
        
        public void reverseX() {
            this.x = -this.x;
        }
        public void reverseY() {
            this.y = -this.y;
        }
        
        public boolean isNullVector() {
            return (this.x == 0.0) && (this.y == 0.0);
        }
        
        public double distanceToSquared(MolVector2D mv2d) {
            double diffX = mv2d.x - this.x;
            double diffY = mv2d.y - this.y;
            return (diffX*diffX) + (diffY*diffY);
        }
        
        public double distanceTo(MolVector2D mv2d) {
            return Math.sqrt(this.distanceToSquared(mv2d));
        }
        
        public MolVector2D scale(double scalar) {
            return new MolVector2D(this.x * scalar, this.y * scalar);
        }
    }
    
    public MolVector2D basis;
    public MolVector2D momentum;
    
    public MolVector2D oldBasis;
    
    public double kineticEnergy, velocity;
    public int radius, mass;
    
    public String colGroup;
    
    public Molecule(MolVector2D basis, MolVector2D momentum, int radius, int mass, String colGroup) {
        this.basis = basis;
        this.momentum = momentum;
        this.radius = radius;
        this.mass = mass;
        this.colGroup = colGroup;
    }
}