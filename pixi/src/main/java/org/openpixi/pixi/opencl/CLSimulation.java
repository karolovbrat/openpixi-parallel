package org.openpixi.pixi.opencl;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.util.IOUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openpixi.pixi.opencl.boundary.CLBoundary;
import org.openpixi.pixi.opencl.boundary.CLHardWallBoundary;
import org.openpixi.pixi.opencl.force.CLConstantForce;
import org.openpixi.pixi.opencl.solver.CLEuler;
import org.openpixi.pixi.physics.Particle2D;

/**
 * OpenCL equivalent to simulation class.
 * Constructs the final OpenCL kernel based on the chosen 
 * force, boundary, solver and collision algorithms.
 * (At the moment collision algorithms are not implemented in OpenCL)
 * 
 * TODO
 * Introduce prepare and complete functionality.
 * @author jan
 */
public class CLSimulation {
    private static final String PARTICLE2D_SOURCE = "Particle2D.h";
    private static final String MOVER_SOURCE = "ParticleMover.cl";
    private static final String MOVER_KERNEL = "moverStep";
    
    // OpenCL members
    
    private CLContext context;
    private CLQueue queue;
    private CLProgram program;
    
    private int[] globalWorkSizes;
    
    /** Kernel which moves particles and checks the boundary */
    private CLKernel mover;
    private CLEvent moverEvent;
    
    private CLParticles2DConvertor p2DConv;
    
    /** Timestep */
	public double tstep;
	/** Width of simulated area */
	public double width;
	/** Height of simulated area */
	public double  height;
    
    // Variable physics members
    public IKernelFragment force;
    public CLBoundary boundary;
    public IKernelFragment solver;
    
    public CLSimulation() {
        tstep = 1;
		width = 100;
		height = 100;
        
        force = new CLConstantForce();                
        solver = new CLEuler();        
        boundary = new CLHardWallBoundary();
        boundary.setBoundaries(0, 0, width, height);
                
        context = JavaCL.createBestContext();
        queue = context.createDefaultQueue();
    }
    
    
    public void setSize(double width, double height) {		
        this.boundary.setBoundaries(0, 0, width, height);
	}
    
    
    /** Retrieves the calculated particles from the device. */
    public ArrayList<Particle2D> getParticles() {
        return p2DConv.getPixiParticles(moverEvent);
    }
    
    
    /** Copies particles to the device. */
    public void setParticles(List<Particle2D> particles) {
        globalWorkSizes = new int[] {particles.size()};
        p2DConv.setDevParticles(particles);
    }
    
    
    /**
     * Puts together the OpenCL source code.
     * Should be called after:
     * - variable physics members are set or changed
     * - particles have been set with setParticles method 
     * @throws IOException 
     */
    public void createProgram() throws IOException {
        program = context.createProgram(
                IOUtils.readText(this.getClass().getResource(PARTICLE2D_SOURCE)),
                force.getKernelCode(),
                boundary.getKernelCode(),
                solver.getKernelCode(),
                IOUtils.readText(this.getClass().getResource(MOVER_SOURCE)));
        
        force.defineMacros(program);
        boundary.defineMacros(program);
        solver.defineMacros(program);
        
        mover = program.createKernel(MOVER_KERNEL, p2DConv.getDevParticles(), tstep);
    }
    
    
    /** 
     * Moves the particles on the device. 
     */
    public void step() {
        moverEvent = mover.enqueueNDRange(queue, globalWorkSizes);
    }
   
    
    /** 
     * Writes the OpenCL source to a specified file.
     * (For debugging purposes)
     */
    public void writeOpenCLSource(String filename) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(filename));
        out.write(program.getSource());
        out.flush();
        out.close();
    }
    
}
