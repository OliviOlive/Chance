/*
 * 2015
 */
package oli.bbp;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;
import oli.bbp.gfx.OliRenderer;
import oli.bbp.gfx.TweenDeclaration;
import oli.bbp.gfx.gobj.Gobject;
import oli.bbp.gfx.gobj.ImageGobject;
import oli.bbp.gfx.gobj.ParticleSimGobject;
import oli.bbp.gfx.gobj.RectGobject;
import oli.bbp.gfx.gobj.TextGobject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author oliver
 */
public class ScriptReader {
    public static class ScriptFormatException extends RuntimeException {
        public ScriptFormatException(String message) {
            super(message);
        }
        
        public ScriptFormatException(TweenDeclaration td, String message) {
            super(td + ": " + message);
        }
        
        public ScriptFormatException(Gobject gob, String message) {
            super(gob + ": " + message);
        }
        
        public static void assertion(boolean cond, String message) {
            if (! cond) {
                throw new ScriptFormatException(message);
            }
        }
    }
    
    public static Color strToColour(String hexIn) {
        return Color.decode(hexIn);
    }
    
    public static JSONObject jo;
    private static final Logger log = Logger.getLogger(Main.class.getName());
    
    public static void validateTween(TweenDeclaration td) {
        switch (td.affectedProperty) {
            case "bounds":
                if (td.json.length() != 4) {
                    throw new ScriptFormatException(td, "Expected =4 params");
                }
                td.json.put(0, DimensionHelper.getX(td.json.get(0))); // correct all values
                td.json.put(1, DimensionHelper.getY(td.json.get(1))); // if they use %,
                td.json.put(2, DimensionHelper.getX(td.json.get(2))); // they are effectively pre-processed
                td.json.put(3, DimensionHelper.getY(td.json.get(3))); // also asserts that they are correct
                if (td.json.length() != 4) {
                    throw new ScriptFormatException(td, "POST [INSANE] Expected =4 params");
                }
            break;
            case "opacity":
                if (td.json.length() != 1) {
                    throw new ScriptFormatException(td, "Expected =1 params");
                }
                if (! (td.json.get(0) instanceof Double) && ! (td.json.get(0) instanceof Integer)) {
                    throw new ScriptFormatException(td, "param[1] ! Float ⓧ");
                }
                if (td.json.getInt(0) > 1.0) {
                    throw new ScriptFormatException(td, "param[1] > 1 ⓧ");
                }
                if (td.json.getInt(0) < 0.0) {
                    throw new ScriptFormatException(td, "param[1] < 0 ⓧ");
                }
            break;
        }
    }
    
    public static void parseScript(File scriptFile) throws ScriptFormatException, FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(scriptFile);
        jo = new JSONObject(new JSONTokener(fis));

        // now parse the contents
        if (! jo.has("duration")) {
            throw new ScriptFormatException("Missing 'duration' double float in script file.");
        }
        if (! jo.has("gobjs")) {
            throw new ScriptFormatException("Missing 'gobjs' section.");
        }
        if (! jo.has("tween")) {
            throw new ScriptFormatException("Missing 'tween' section.");
        }
        
        OliRenderer.frameDur = DimensionHelper.getFramesFromSeconds(jo.getDouble("duration"));
        JSONObject gobjs = jo.getJSONObject("gobjs");
        JSONArray tween = jo.getJSONArray("tween");
        
        for (Object tkey: gobjs.keySet()) {
            if (! (tkey instanceof String)) {
                throw new ScriptFormatException("Expected string key index, got " + tkey.getClass().getName());
            }
            String key = (String) tkey;
            JSONObject val = gobjs.getJSONObject(key);
            
            switch (val.getString("type")) {
                case "text":
                    Gobject.ago.add(new TextGobject(key, val));
                break;
                case "image":
                    Gobject.ago.add(new ImageGobject(key, val));
                break;
                case "rect":
                    Gobject.ago.add(new RectGobject(key, val));
                break;
                case "parsim":
                    Gobject.ago.add(new ParticleSimGobject(key, val));
                break;
                default:
                    throw new ScriptFormatException("Unknown Gobject type: " + val.getString("type"));
            }
        }
        
        Collections.sort(Gobject.ago, new Gobject.GobjectZIndexComparator());
        
        int len = tween.length();
        for (int i = 0; i < len; ++i) {
            JSONArray ja = tween.getJSONArray(i);
            
            String gobRep = ja.get(1).toString();
            
            int startFrame = DimensionHelper.getFramesFromSeconds(ja.getDouble(2));
            Integer endFrame = null;
            if (! ja.isNull(3)) {
                endFrame = DimensionHelper.getFramesFromSeconds(ja.getDouble(3));
                if (endFrame < startFrame) {
                    throw new ScriptFormatException("Tween(" + gobRep + ", " + ja.getString(0) + ") - The start frame must not exceed the end frame.");
                }
            }
            // NB end Frame null - instant transition (no tweening)
            JSONArray extraja = ja.optJSONArray(4);
            
            if (ja.get(1) instanceof JSONArray) {
                JSONArray jagobs = ja.getJSONArray(1);
                int jagobsl = jagobs.length();
                for (int jagobsi = 0; jagobsi < jagobsl; ++jagobsi) {
                    String jagobsv = jagobs.getString(jagobsi);
                    Gobject g = Gobject.getGobjectById(jagobsv);
                    if (g == null) {
                        throw new ScriptFormatException("Gobject#" + jagobsv + " does not exist -- while parsing Multi-Tween Declarations.");
                    }
                    
                    TweenDeclaration td = new TweenDeclaration(ja.getString(0), g, startFrame, endFrame, extraja);
                    validateTween(td);
                    TweenDeclaration.atd.add(td);
                }
            } else {
                Gobject g = Gobject.getGobjectById(ja.getString(1));
                if (g == null) {
                    throw new ScriptFormatException("Gobject#" + ja.getString(1) + " does not exist -- while parsing Tween Declarations.");
                }
                
                TweenDeclaration td = new TweenDeclaration(ja.getString(0), g, startFrame, endFrame, extraja);
                validateTween(td);
                TweenDeclaration.atd.add(td);
            }
        }
    }
}