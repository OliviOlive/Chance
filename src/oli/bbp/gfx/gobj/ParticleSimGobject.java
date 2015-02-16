/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import oli.bbp.gfx.TweenDeclaration;
import oli.bbp.sim.Molecule;
import oli.bbp.sim.ParticleSimulator;
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
    
    Color borderCol;
    
    public ParticleSimGobject(String id, JSONObject jo) {
        super(id, jo);
        
        showHistory = jo.optBoolean("showHistory", true);
        showVectors = jo.optBoolean("showVectors", false);
        showTime = jo.optBoolean("showTime", true);
        
        historyOpacity = (float) jo.optDouble("historyOpacity", 0.4);
        
        borderCol = ScriptReader.strToColour(jo.optString("borderColour", "#ffffff"));
        
        if (! jo.has("simulator")) {
            throw new ScriptReader.ScriptFormatException(this, "No simulator parameter passed. Unable to instantiate ParticleSimulator.");
        }
        
        psim = new ParticleSimulator(jo.getJSONObject("simulator"));
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
        
        if (this.showHistory) { // draw history in transparent layer!
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.historyOpacity));
            for (int moli = 0; moli < psim.molCount; ++moli) {
                Molecule m = psim.mols[moli];
                
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
                            new Color(255, 255, 255),
                            new Color(255, 0, 0),
                            new Color(255, 0, 0),
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
                        new Color(255, 200, 220),
                        new Color(255, 0, 0),
                        new Color(255, 0, 0),
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
