/*
 * 2015
 */
package oli.bbp.gfx.gobj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
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
    
    public boolean keepAspectRatio;
    
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
            log.log(Level.SEVERE, "ImageGobject: No res (or deprecated src) attribute. @ {0}", this);
        }
        
        this.keepAspectRatio = ja.optBoolean("keepAspectRatio", false);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (keepAspectRatio) {
            double propWH, propHW;
            propWH = (double) bi.getWidth() / bi.getHeight();
            propHW = (double) bi.getHeight() / bi.getWidth();
            
            double idealWFromH = propWH * this.bounds.height;
            double idealHFromW = propHW * this.bounds.width;
            
            double setW, setH;
        
            if (idealWFromH > this.bounds.width) {
                setH = idealHFromW;
                setW = propWH * idealHFromW;
            } else if (idealHFromW > this.bounds.height) {
                setW = idealWFromH;
                setH = propHW * idealWFromH;
            } else {
                setW = idealWFromH;
                setH = idealHFromW;
            }
            
            Rectangle virtualBounds = new Rectangle(
                    (int) ((-setW/2) + this.bounds.x + (this.bounds.width/2)),
                    (int) ((-setH/2) + this.bounds.y + (this.bounds.height/2)),
                    (int) setW, (int) setH
            );
            
            g2d.drawImage(bi, 
                    DimensionHelper.getRealDimensions(virtualBounds.x),
                    DimensionHelper.getRealDimensions(virtualBounds.y),
                    DimensionHelper.getRealDimensions(virtualBounds.x + virtualBounds.width),
                    DimensionHelper.getRealDimensions(virtualBounds.y + virtualBounds.height),
                    0, 0, bi.getWidth()-1, bi.getHeight()-1, null
            );
        } else {
            g2d.drawImage(bi, 
                    DimensionHelper.getRealDimensions(this.bounds.x),
                    DimensionHelper.getRealDimensions(this.bounds.y),
                    DimensionHelper.getRealDimensions(this.bounds.x + this.bounds.width),
                    DimensionHelper.getRealDimensions(this.bounds.y + this.bounds.height),
                    0, 0, bi.getWidth()-1, bi.getHeight()-1, null
            );
        }
    }
    
}
