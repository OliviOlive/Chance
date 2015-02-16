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
import java.awt.geom.Point2D;
import java.util.HashMap;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
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
    boolean showTime;
    
    float historyOpacity;
    float vectorWidth;
    float vectorScalar;
    
    Color borderCol;
    Color vectorCol;
    
    public static HashMap<String, MoleculeColourGroup> molcgs = new HashMap<>();
    
    public ParticleSimGobject(String id, JSONObject jo) {
        super(id, jo);
        
        showHistory = jo.optBoolean("showHistory", true);
        showVectors = jo.optBoolean("showVectors", false);
        showTime = jo.optBoolean("showTime", true);
        
        historyOpacity = (float) jo.optDouble("historyOpacity", 0.4);
        vectorWidth = (float) jo.optDouble("drawnVectorWidth", 3.0);
        vectorScalar = (float) jo.optDouble("drawnVectorScalar", 30.0);
        vectorCol = ScriptReader.strToColour(jo.optString("drawnVectorColour", "#ffffff"));
        
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
        
        psim = new ParticleSimulator(jo.getJSONObject("simulator"));
        
    }

    @Override
    public void draw(Graphics2D g2d) {
        psim.update();
        
        g2d.setColor(this.borderCol);
        g2d.drawRect(
                DimensionHelper.getRealDimensions(this.bounds.x),
                DimensionHelper.getRealDimensions(this.bounds.y),
                DimensionHelper.getRealDimensions(this.bounds.width),
                DimensionHelper.getRealDimensions(this.bounds.height)
        );
        
        if (this.showHistory) { // draw history in transparent layer!
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.historyOpacity));
            for (int moli = 0; moli < psim.molCount; ++moli) {
                Molecule m = psim.mols[moli];
                MoleculeColourGroup mcg = molcgs.get(m.colGroup);
                
                if (m.oldBasis == null) continue;
                
                g2d.setPaint(new RadialGradientPaint(
                        new Point2D.Float(
                                DimensionHelper.getRealDimensions((float) (this.bounds.x + m.oldBasis.x)),
                                DimensionHelper.getRealDimensions((float) (this.bounds.y + m.oldBasis.y))
                        ),
                        m.radius,
                        new Point2D.Float(
                                DimensionHelper.getRealDimensions((float) (this.bounds.x + m.oldBasis.x + m.radius / 2)),
                                DimensionHelper.getRealDimensions((float) (this.bounds.y + m.oldBasis.y - m.radius / 2))
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
                        DimensionHelper.getRealDimensions((int) (this.bounds.x + m.oldBasis.x) - m.radius),
                        DimensionHelper.getRealDimensions((int) (this.bounds.y + m.oldBasis.y) - m.radius),
                        DimensionHelper.getRealDimensions(2 * m.radius),
                        DimensionHelper.getRealDimensions(2 * m.radius),
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
                            DimensionHelper.getRealDimensions((float) (this.bounds.x + m.basis.x)),
                            DimensionHelper.getRealDimensions((float) (this.bounds.y + m.basis.y))
                    ),
                    m.radius,
                    new Point2D.Float(
                            DimensionHelper.getRealDimensions((float) (this.bounds.x + m.basis.x + m.radius / 2)),
                            DimensionHelper.getRealDimensions((float) (this.bounds.y + m.basis.y - m.radius / 2))
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
                    DimensionHelper.getRealDimensions((int) (this.bounds.x + m.basis.x) - m.radius),
                    DimensionHelper.getRealDimensions((int) (this.bounds.y + m.basis.y) - m.radius),
                    DimensionHelper.getRealDimensions(2 * m.radius),
                    DimensionHelper.getRealDimensions(2 * m.radius),
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
                        DimensionHelper.getRealDimensions((int) (this.bounds.x + m.basis.x)),
                        DimensionHelper.getRealDimensions((int) (this.bounds.y + m.basis.y)),
                        DimensionHelper.getRealDimensions((int) (this.bounds.x + mv2d.x)),
                        DimensionHelper.getRealDimensions((int) (this.bounds.y + mv2d.y))
                );
            }
            
            g2d.setStroke(restoreStroke);
        }
    }
    
    @Override
    public void tween(TweenDeclaration td, int frameNum) {
        super.tween(td, frameNum);
    }
    
    @Override
    public void instantChange(TweenDeclaration td, int frameNum) {
        super.instantChange(td, frameNum);
    }
    
}
