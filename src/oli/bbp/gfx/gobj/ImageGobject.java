/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import oli.bbp.DimensionHelper;
import oli.bbp.ScriptReader;
import org.json.JSONObject;

/**
 *
 * @author oliver
 */
public class ImageGobject extends Gobject {

    public BufferedImage bi;
    
    public ImageGobject(String id, JSONObject ja) throws IOException {
        super(id, ja);
        
        File imf = new File(ja.optString("src", "__MISSING"));
        
        if (! imf.exists()) {
            throw new ScriptReader.ScriptFormatException("Image File does not exist: " + imf.getAbsolutePath());
        }
        
        bi = ImageIO.read(imf);
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.drawImage(bi, 
                DimensionHelper.getRealDimensions(this.bounds.x),
                DimensionHelper.getRealDimensions(this.bounds.y),
                DimensionHelper.getRealDimensions(this.bounds.x + this.bounds.width),
                DimensionHelper.getRealDimensions(this.bounds.y + this.bounds.height),
                0, 0, bi.getWidth()-1, bi.getHeight()-1, null
        );
    }
    
}
