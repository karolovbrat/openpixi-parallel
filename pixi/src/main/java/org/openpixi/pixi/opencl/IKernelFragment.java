package org.openpixi.pixi.opencl;

import com.nativelibs4java.opencl.CLProgram;
import java.io.IOException;

/**
 * Represents a fragment of the kernel code.
 * @author jan
 */
public interface IKernelFragment {
    
    /**
     * Through this method every piece of OpenCL code can define
     * the macros it is using.
     * @param p Kernel program
     */
    void defineMacros(CLProgram p);
    
    /**
     * Returns the kernel code this fragment is responsible for.
     * @return Part of kernel code.
     */
    String getKernelCode() throws IOException;
}
