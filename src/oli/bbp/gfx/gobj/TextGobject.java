/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
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
    
    public TextGobject(String id, JSONObject ja) {
        super(id, ja);
        this.text = ja.getString("text");
        this.font = new Font(ja.optString("fontName", Font.SANS_SERIF), ja.optInt("fontStyle", Font.PLAIN), ja.optInt("fontSize", 20));
        this.col = ScriptReader.strToColour(ja.optString("colour", "#FFFFFF"));
    }
    
    @Override
    public void tween(TweenDeclaration td, int frameNum) {
        super.tween(td, frameNum);
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        int x, y;
        FontMetrics fm = g2d.getFontMetrics(font);
        int h = fm.getDescent() + fm.getAscent() + fm.getLeading();
        int w = fm.stringWidth(this.text);
        x = DimensionHelper.getRealDimensions((DimensionHelper.RESOLUTION_WIDTH/2) - (w/2));
        y = DimensionHelper.getRealDimensions((DimensionHelper.RESOLUTION_HEIGHT/2) - (h/2));
        g2d.setColor(this.col);
        g2d.setBackground(new Color(255, 255, 255));
        g2d.drawString(this.text, x, y);
    }
    
}
