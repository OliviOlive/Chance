/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
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
    
    static final Logger log = Logger.getLogger("ImageGobject");
    
    public ImageGobject(String id, JSONObject ja) throws IOException {
        super(id, ja);
        
        if (ja.has("src")) {
            File imf = new File(ja.optString("src", "__MISSING"));

            if (! imf.exists()) {
                throw new ScriptReader.ScriptFormatException("Image File does not exist: " + imf.getAbsolutePath());
            }
            Logger.getLogger("ImageGobject: construction").warning("Warning: ImageGobject.src is deprecated.");
            
            bi = ImageIO.read(imf);
        } else if (ja.has("res")) {
            File res = ScriptReader.resMap.get(ja.getString("res"));
            bi = ImageIO.read(res);
        } else {
            log.severe("ImageGobject: No res (or deprecated src) attribute. @ " + this);
        }
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
