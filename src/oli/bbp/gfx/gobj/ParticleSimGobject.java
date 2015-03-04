/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.HashMap;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import oli.bbp.ScriptReader.ScriptFormatException;
import oli.bbp.gfx.GfxHelper;
import oli.bbp.gfx.TweenDeclaration;
import oli.bbp.sim.Molecule;
import oli.bbp.sim.Molecule.MolVector2D;
import oli.bbp.sim.MoleculeColourGroup;
import oli.bbp.sim.ParticleSimulator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author oliver
 */
public class ParticleSimGobject extends Gobject {
    
    ParticleSimulator psim;
    
    boolean showHistory;
    boolean showVectors;
    
    float historyOpacity;
    float vectorWidth;
    float vectorScalar;
    
    double simSpeed;
    double accumTicks = 0;
    
    Color borderCol;
    Color vectorCol;
    
    String timeMonitor_id;
    TextGobject timeMonitor;
    int timeMonitor_dp;
    double timeMonitor_divisor;
    
    public static HashMap<String, MoleculeColourGroup> molcgs = new HashMap<>();
    
    public ParticleSimGobject(String id, JSONObject jo) {
        super(id, jo);
        
        showHistory = jo.optBoolean("showHistory", true);
        showVectors = jo.optBoolean("showVectors", false);
        
        historyOpacity = (float) jo.optDouble("historyOpacity", 0.4);
        vectorWidth = (float) jo.optDouble("drawnVectorWidth", 3.0);
        vectorScalar = (float) jo.optDouble("drawnVectorScalar", 30.0);
        vectorCol = ScriptReader.strToColour(jo.optString("drawnVectorColour", "#ffffff"));
        
        simSpeed = jo.optDouble("simSpeed", 300.0);
        
        borderCol = ScriptReader.strToColour(jo.optString("borderColour", "#ffffff"));
        
        if (! jo.has("simulator")) {
            throw new ScriptReader.ScriptFormatException(this, "No simulator parameter passed. Unable to instantiate ParticleSimulator.");
        }
        
        if (jo.has("colourGroups")) {
            JSONObject cgjo = jo.getJSONObject("colourGroups");
            for (Object keyo : cgjo.keySet().toArray()) {
                String key = (String) keyo;
                JSONArray cgjovja = cgjo.getJSONArray(key);
                molcgs.put(key, new MoleculeColourGroup(
                        ScriptReader.strToColour(cgjovja.getString(0)),
                        ScriptReader.strToColour(cgjovja.getString(1)),
                        ScriptReader.strToColour(cgjovja.getString(2)),
                        ScriptReader.strToColour(cgjovja.getString(3))
                ));
            }
        }
        
        if (jo.has("timeMonitor")) {
            JSONArray tja = jo.getJSONArray("timeMonitor");
            this.timeMonitor_id = tja.getString(0);
            
            timeMonitor_dp = tja.optInt(1, 3);
            timeMonitor_divisor = tja.optDouble(2, simSpeed);
        }
        
        psim = new ParticleSimulator(jo.getJSONObject("simulator"));
        
    }

    private ParticleSimGobject() {
        
    }
    
    @Override
    public void postConstruct() {
        if (this.timeMonitor_id != null) {
            Gobject go = Gobject.getGobjectById(this.timeMonitor_id);
            if (go == null) {
                throw new ScriptFormatException(this, "timeMonitor[0] must be the name of a TextGobject already initialised. Re-order your Gobjects if necessary.");
            }
            if (! (go instanceof TextGobject)) {
                throw new ScriptFormatException(this, "timeMonitor[0] must be the name of a TextGobject. Any old Gobject will not just do.");
            }
            this.timeMonitor = (TextGobject) go;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(this.borderCol);
        g2d.drawRect(
                DimensionHelper.getRealDimensions(this.bounds.x),
                DimensionHelper.getRealDimensions(this.bounds.y),
                DimensionHelper.getRealDimensions(this.bounds.width),
                DimensionHelper.getRealDimensions(this.bounds.height)
        );
        
        /* Precision errors mean that they were rounded to 0...
        therefore, will use BigDecimal
        double xMod = this.bounds.width / psim.simWidth;
        double yMod = this.bounds.height / psim.simHeight;
        */
        
        BigDecimal xMod = new BigDecimal(this.bounds.width).divide(new BigDecimal(psim.simWidth));
        BigDecimal yMod = new BigDecimal(this.bounds.height).divide(new BigDecimal(psim.simHeight));
        
        BigDecimal downscale = new BigDecimal(DimensionHelper.DOWNSCALE);
        
        xMod = xMod.multiply(downscale);
        yMod = yMod.multiply(downscale);
        
        // get the transformation matrix so we can restore it later
        AffineTransform restoreAT = g2d.getTransform();
        
        // translate so that 0,0 is 0,0 of the gobject
        g2d.translate(DimensionHelper.getRealDimensions(this.bounds.x), DimensionHelper.getRealDimensions(this.bounds.y));
        
        // scale so that dimensions match virtual, in-simulator dimensions;
        g2d.scale(xMod.doubleValue(), yMod.doubleValue());
        
        if (this.showHistory) { // draw history in transparent layer!
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.historyOpacity));
            for (int moli = 0; moli < psim.molCount; ++moli) {
                Molecule m = psim.mols[moli];
                MoleculeColourGroup mcg = molcgs.get(m.colGroup);
                
                if (m.oldBasis == null) continue;
                
                g2d.setPaint(new RadialGradientPaint(
                        new Point2D.Float(
                                (float) m.oldBasis.x,
                                (float) m.oldBasis.y
                        ),
                        m.radius,
                        new Point2D.Float(
                                (float) m.oldBasis.x + m.radius / 2,
                                (float) m.oldBasis.y - m.radius / 2
                        ),
                        new float[]{0f, 0.8f, 1f},
                        new Color[]{
                            mcg.history_primary,
                            mcg.history_secondary,
                            mcg.history_secondary,
                        },
                        CycleMethod.NO_CYCLE
                ));
                g2d.fillArc(
                        (int) m.oldBasis.x - m.radius,
                        (int) m.oldBasis.y - m.radius,
                        2 * m.radius,
                        2 * m.radius,
                        0, 360
                );
            }
            // revert to opaque
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        
        for (int moli = 0; moli < psim.molCount; ++moli) {
            Molecule m = psim.mols[moli];
            MoleculeColourGroup mcg = molcgs.get(m.colGroup);
            
            g2d.setPaint(new RadialGradientPaint(
                    new Point2D.Float(
                            (float) m.basis.x,
                            (float) m.basis.y
                    ),
                    m.radius,
                    new Point2D.Float(
                            (float) m.basis.x + m.radius / 2,
                            (float) m.basis.y - m.radius / 2
                    ),
                    new float[]{0f, 0.8f, 1f},
                    new Color[]{
                        mcg.active_primary,
                        mcg.active_secondary,
                        mcg.active_secondary,
                    },
                    CycleMethod.NO_CYCLE
            ));
            g2d.fillArc(
                    (int) m.basis.x - m.radius,
                    (int) m.basis.y - m.radius,
                    2 * m.radius,
                    2 * m.radius,
                    0, 360
            );
        }
        
        if (showVectors) {
            // render momentum vectors
            Stroke restoreStroke = g2d.getStroke();
            g2d.setColor(vectorCol);
            
            g2d.setStroke(new BasicStroke(vectorWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
            
            for (int moli = 0; moli < psim.molCount; ++moli) {
                Molecule m = psim.mols[moli];
                
                // vectors will be rendered as arrows
                
                if (m.momentum.isNullVector()) continue;
                
                MolVector2D mv2d = new MolVector2D(m.basis);
                mv2d.add(m.momentum.scale(vectorScalar));
                
                GfxHelper.drawArrow(
                        g2d,
                        (int) m.basis.x,
                        (int) m.basis.y,
                        (int) mv2d.x,
                        (int) mv2d.y
                );
            }
            
            g2d.setStroke(restoreStroke);
        }
        
        // restore the Transformation Matrix
        g2d.setTransform(restoreAT);
    }
    
    public void common(TweenDeclaration td, int frameNum) {
        this.simSpeed = TweenDeclaration.TweenSimpleHandlers.simpleUpdateField(
                "simSpeed", td, this.simSpeed
        );
        
        this.vectorWidth = TweenDeclaration.TweenSimpleHandlers.simpleUpdateField(
                "drawnVectorWidth", td, this.vectorWidth
        );
        
        this.vectorScalar = TweenDeclaration.TweenSimpleHandlers.simpleUpdateField(
                "drawnVectorScalar", td, this.vectorScalar
        );
        
        this.showVectors = TweenDeclaration.TweenSimpleHandlers.simpleBooleanField(
                "shownVectors", td, this.showVectors                
        );
    }
    
    @Override
    public void tween(TweenDeclaration td, int frameNum) {
        super.tween(td, frameNum);
        
        common(td, frameNum);
        
        if (td.affectedProperty.equals("simulate")) {
            // because there may be a decimal result returned,
            // ticks should 'accumulate' so that hopefully the difference
            // is not noticed. This way, simulations will run at the same
            // perceived speed at different framerates.
            accumTicks += simSpeed / DimensionHelper.FRAMES_PER_SECOND;
            boolean updateHistory = true;
            while (accumTicks >= 1) {
                accumTicks--;
                psim.update(updateHistory);
                updateHistory = false;
            }
            
            if (this.timeMonitor != null) {
                // update the time monitor
                double timeVal = (double) psim.simTime / timeMonitor_divisor;
                double dpPow = Math.pow(10, timeMonitor_dp);
                String timeRep = Double.toString(Math.floor(timeVal * dpPow) / dpPow);
                this.timeMonitor.text = timeRep;
            }
        }
    }
    
    @Override
    public void instantChange(TweenDeclaration td, int frameNum) {
        super.instantChange(td, frameNum);
        
        if ("dupe".equals(td.affectedProperty)) {
            // duplicate this Gobject -- but keep the same ParticleSimulator
            // this allows the showing of multiple viewports, or the fading in and out
            // of vector arrows, etc
            ParticleSimGobject psgo = new ParticleSimGobject();
            psgo.borderCol = this.borderCol;
            psgo.historyOpacity = this.historyOpacity;
            psgo.psim = this.psim;
            psgo.showHistory = this.showHistory;
            psgo.showVectors = this.showVectors;
            psgo.vectorCol = this.vectorCol;
            psgo.vectorScalar = this.vectorScalar;
            psgo.vectorWidth = this.vectorWidth;
            Gobject go = Gobject.getGobjectById(td.json.getString(0));
            if (! (go instanceof DuplicationDestinationGobject)) {
                throw new ScriptFormatException(this, "Replacee must be of 'dd' type.");
            }
            DuplicationDestinationGobject ddgo = (DuplicationDestinationGobject) go;
            ddgo.supersede(psgo);
        }
        
        if (td.affectedProperty.equals("reverseVectors")) {
            for (Molecule mol : psim.mols) {
                mol.momentum.reverseX();
                mol.momentum.reverseY();
            }
        }
        
        if (td.affectedProperty.equals("isimulate")) {
            // an instant simulation
            // if parameter is negative, this is a de-simulation (experimental)
            int cycles = td.json.getInt(0);
            boolean desim = cycles < 0;
            
            boolean preserveTime = td.json.getBoolean(1);
            long timePreserve = psim.simTime;
            
            if (desim) {
                cycles = -cycles;
                for (Molecule mol : psim.mols) {
                    mol.momentum.reverseX();
                    mol.momentum.reverseY();
                }
            }
            
            for (int i = 0; i < cycles; ++i) {
                psim.update(true);
            }
            
            if (preserveTime) {
                psim.simTime = timePreserve;
            }
            
            if (desim) {
                for (Molecule mol : psim.mols) {
                    mol.momentum.reverseX();
                    mol.momentum.reverseY();
                    mol.oldBasis = new MolVector2D(mol.basis);
                }
            }
        }
        
        common(td, frameNum);
    }
    
}
