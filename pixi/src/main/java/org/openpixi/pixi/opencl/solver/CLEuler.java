package org.openpixi.pixi.opencl.solver;

import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.util.IOUtils;
import java.io.IOException;
import org.openpixi.pixi.opencl.IKernelFragment;

/**
 *
 * @author jan
 */
public class CLEuler implements IKernelFragment {
    private static final String SOURCE_FILE = "Euler.cl";
    
    public void defineMacros(CLProgram p) {
        // Do nothing
    }

    public String getKernelCode() throws IOException {
        return IOUtils.readText(this.getClass().getResource(SOURCE_FILE));
    }
    
}
