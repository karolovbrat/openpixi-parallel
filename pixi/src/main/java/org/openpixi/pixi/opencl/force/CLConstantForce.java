package org.openpixi.pixi.opencl.force;

import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.util.IOUtils;
import java.io.IOException;
import org.openpixi.pixi.opencl.IKernelFragment;

/**
 *
 * @author jan
 */
public class CLConstantForce implements IKernelFragment {
    
    private static final String SOURCE_FILE = "ConstantForce.cl";
    
    /** Constant gravity in x-direction */
	public double gx;

	/** Constant gravity in y-direction */
	public double gy;

	/** Drag coefficient */
	public double drag;

	/** Electric field in x - direction */
	public double ex;

	/** Electric field in y - direction */
	public double ey;

	/** Magnetic field in z - direction */
	public double bz;

    public void defineMacros(CLProgram p) {
        p.defineMacro("FGX", gx);
        p.defineMacro("FGY", gy);
        p.defineMacro("FEX", ex);
        p.defineMacro("FEY", ey);
        p.defineMacro("GBZ", bz);
        p.defineMacro("FDRAG", drag);
    }

    public String getKernelCode() throws IOException {
        return IOUtils.readText(this.getClass().getResource(SOURCE_FILE));
    }
    
}
