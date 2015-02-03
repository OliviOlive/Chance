/*
 * 2015
 */
package oli.bbp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import oli.bbp.gfx.TweenDeclaration;
import oli.bbp.gfx.gobj.Gobject;
import oli.bbp.gfx.gobj.ImageGobject;
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
    }
    
    public static JSONObject jo;
    private static final Logger log = Logger.getLogger(Main.class.getName());
    
    public static void parseScript(File scriptFile) throws ScriptFormatException, FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(scriptFile);
        jo = new JSONObject(new JSONTokener(fis));

        // now parse the contents
        if (! jo.has("gobjs")) {
            throw new ScriptFormatException("Missing 'gobjs' section.");
        }
        if (! jo.has("tween")) {
            throw new ScriptFormatException("Missing 'tween' section.");
        }
        
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
                default:
                    throw new ScriptFormatException("Unknown Gobject type: " + val.getString("type"));
            }
        }
        
        int len = tween.length();
        for (int i = 0; i < len; ++i) {
            JSONArray ja = tween.getJSONArray(i);
            Gobject g = Gobject.getGobjectById(ja.getString(1));
            if (g == null) {
                throw new ScriptFormatException("Gobject#" + ja.getString(1) + " does not exist -- while parsing Tween Declarations.");
            }
            int startFrame = DimensionHelper.getFramesFromSeconds(ja.getDouble(2));
            int endFrame = DimensionHelper.getFramesFromSeconds(ja.getDouble(3));
            if (endFrame < startFrame) {
                throw new ScriptFormatException("Tween(" + g.id + ", " + ja.getString(0) + ") - The start frame must not exceed the end frame.");
            }
            JSONArray extraja = ja.optJSONArray(4);
            
            TweenDeclaration.atd.add(new TweenDeclaration(ja.getString(0), g, startFrame, endFrame, extraja));
        }
    }
}