/*
 * 2015
 */
package oli.bbp.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author oliver
 */
public class VoidStream implements Runnable {
    InputStream inStr;
    
    public VoidStream(InputStream is) {
        this.inStr = is;
    }
    
    public void start() {
        Thread td = new Thread(this);
        td.start();
    }
    
    public void close() {
        try {
            this.inStr.close();
        } catch (IOException ex) {
            Logger.getLogger(VoidStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // skip all output
                if (this.inStr.read() == -1) return;
            } catch (IOException ex) {
                Logger.getLogger(VoidStream.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
    }
}
