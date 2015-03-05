/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import oli.bbp.gfx.TweenDeclaration;
import org.json.JSONObject;

/**
 *
 * @author oliver
 */
public class TextGobject extends Gobject {
    
    public String text;
    public Font font;
    public Color col;
    public boolean autoFontSize;
    public boolean autoFontSizeOnResize;
    public float fontSize;
    
    public TextGobject(String id, JSONObject ja) {
        super(id, ja);
        this.text = ja.getString("text");
        this.font = new Font(ja.optString("fontName", Font.SANS_SERIF), ja.optInt("fontStyle", Font.PLAIN), ja.optInt("fontSize", 20));
        this.fontSize = this.font.getSize();
        this.col = ScriptReader.strToColour(ja.optString("colour", "#FFFFFF"));
        this.autoFontSize = ja.optBoolean("autoFontSize", false);
        this.autoFontSizeOnResize = ja.optBoolean("autoFontSizeOnResize", false);
    }
    
    public void common(TweenDeclaration td) {
        this.fontSize = TweenDeclaration.TweenSimpleHandlers.simpleUpdateField("fontSize", td, this.fontSize);
        if (this.fontSize != this.font.getSize()) {
            this.font = this.font.deriveFont(this.font.getStyle(), this.fontSize);
        }
        
        this.col = TweenDeclaration.TweenSimpleHandlers.simpleUpdateColourRGB("colour", td, col);
        
        if ("bounds".equals(td.affectedProperty) && this.autoFontSizeOnResize) {
            this.autoFontSize = true;
        }
    }
    
    @Override
    public void tween(TweenDeclaration td, int frameNum) {
        super.tween(td, frameNum);
        this.common(td);
    }

    @Override
    public void instantChange(TweenDeclaration td, int frameNum) {
        super.instantChange(td, frameNum);
        this.common(td);
    }
    
    
    
    @Override
    public void draw(Graphics2D g2d) {
        int x, y;
        
        if (this.autoFontSize) {
            float size = 0;
            while (true) {
                ++size;
                FontMetrics fm = g2d.getFontMetrics(font.deriveFont(this.font.getStyle(), size));
                Rectangle2D strBounds = fm.getStringBounds(this.text, g2d);
                if (strBounds.getWidth() > DimensionHelper.getRealDimensions(bounds.width) || strBounds.getHeight() > DimensionHelper.getRealDimensions(bounds.height)) {
                    --size;
                    break;
                }
            }
            while (true) {
                size+=0.1;
                FontMetrics fm = g2d.getFontMetrics(font.deriveFont(this.font.getStyle(), size));
                Rectangle2D strBounds = fm.getStringBounds(this.text, g2d);
                if (strBounds.getWidth() > DimensionHelper.getRealDimensions(bounds.width) || strBounds.getHeight() > DimensionHelper.getRealDimensions(bounds.height)) {
                    size-=0.1;
                    break;
                }
            }
            while (true) {
                size+=0.01;
                FontMetrics fm = g2d.getFontMetrics(font.deriveFont(this.font.getStyle(), size));
                Rectangle2D strBounds = fm.getStringBounds(this.text, g2d);
                if (strBounds.getWidth() > DimensionHelper.getRealDimensions(bounds.width) || strBounds.getHeight() > DimensionHelper.getRealDimensions(bounds.height)) {
                    size-=0.01;
                    break;
                }
            }
            this.autoFontSize = false;
            this.font = font.deriveFont(this.font.getStyle(), size);
            this.fontSize = size;
        }
        
        FontMetrics fm = g2d.getFontMetrics(font);
        Rectangle2D strBounds = fm.getStringBounds(this.text, g2d);
        
        x = (int) (DimensionHelper.getRealDimensions(bounds.x + (bounds.width / 2)) - (strBounds.getWidth()/2));
        y = (int) (DimensionHelper.getRealDimensions(bounds.y + (bounds.height / 2)) + (strBounds.getHeight()/4));
        g2d.setColor(this.col);
        g2d.setFont(font);
        g2d.drawString(this.text, x, y);
    }
    
}
