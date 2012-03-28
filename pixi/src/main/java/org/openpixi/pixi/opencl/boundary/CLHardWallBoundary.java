package org.openpixi.pixi.opencl.boundary;

import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.util.IOUtils;
import java.io.IOException;

/**
 * Link to OpenCL hard wall boundary.
 * @author jan
 */
public class CLHardWallBoundary extends CLBoundary {
    private static final String SOURCE_FILE = "HardWallBoundary.cl";    
    
    public void defineMacros(CLProgram p) {
        p.defineMacro("XMIN", xmin);
        p.defineMacro("XMAX", xmax);
        p.defineMacro("YMIN", ymin);
        p.defineMacro("YMAX", ymax);
    }

    public String getKernelCode() throws IOException {
        return IOUtils.readText(this.getClass().getResource(SOURCE_FILE));
    }
    
}
