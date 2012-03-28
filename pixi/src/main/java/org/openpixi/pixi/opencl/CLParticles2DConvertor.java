package org.openpixi.pixi.opencl;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLQueue;
import java.util.ArrayList;
import java.util.List;
import org.bridj.Pointer;
import org.openpixi.pixi.physics.Particle2D;


/**
 * Manages the copying of particles to and from the device.
 * 
 * DESIGN NOTE
 * - We would need a similar class for further data required in device memory
 *   such as CurrentGrid.
 * @author jan
 */
public class CLParticles2DConvertor {
    
    private static final int PARTICLE_SIZE = 9;
    
    private CLContext context;
    private CLQueue queue;
    private CLBuffer<Double> devParticles;
    
    private int particleCount = 0;
    
    public CLParticles2DConvertor(CLContext context, CLQueue queue) {
        this.context = context;
        this.queue = queue;
    }
    
    
    public CLBuffer<Double> getDevParticles() {
        return devParticles;
    }
    
    
    /**
     * Copies particles to the device.
     * Must be called before getPixiParticles!
     * @param particles List of particles
     * @return OpenCL buffer with particles
     */
    public CLBuffer<Double> setDevParticles(List<Particle2D> particles) {
        particleCount = particles.size();
        
        // To copy the particles we have to convert them from list of Particle2D 
        // structures to list of doubles.
        Pointer<Double> hostParticles = Pointer.allocateDoubles(
                particles.size() * PARTICLE_SIZE);   
        for (int i = 0; i < particles.size(); ++i) {
            hostParticles.set(i * PARTICLE_SIZE + 0, particles.get(i).x);
            hostParticles.set(i * PARTICLE_SIZE + 1, particles.get(i).y);
            hostParticles.set(i * PARTICLE_SIZE + 2, particles.get(i).vx);
            hostParticles.set(i * PARTICLE_SIZE + 3, particles.get(i).vy);
            hostParticles.set(i * PARTICLE_SIZE + 4, particles.get(i).ax);
            hostParticles.set(i * PARTICLE_SIZE + 5, particles.get(i).ay);
            hostParticles.set(i * PARTICLE_SIZE + 6, particles.get(i).radius);
            hostParticles.set(i * PARTICLE_SIZE + 7, particles.get(i).mass);
            hostParticles.set(i * PARTICLE_SIZE + 8, particles.get(i).charge);
        }
        
        devParticles = context.createDoubleBuffer(
                CLMem.Usage.InputOutput, hostParticles, true);
        return devParticles;
    }
    
    
    /**
     * Retrieves the particles in Particle2D format from the device.
     */
    public ArrayList<Particle2D> getPixiParticles(CLEvent ... eventsToWaitFor) {
        assert particleCount != 0;
        
        Pointer<Double> hostParticles = devParticles.read(queue, eventsToWaitFor);
        ArrayList<Particle2D> pixiParticles = new ArrayList<Particle2D>();
        
        // We need to convert the list of doubles coming from the device to
        // list of Particle2D structures.     
        for (int i = 0; i < particleCount; ++i) {
            Particle2D p = new Particle2D();
            p.x = hostParticles.get(i * PARTICLE_SIZE + 0);
            p.y = hostParticles.get(i * PARTICLE_SIZE + 1);
            p.vx = hostParticles.get(i * PARTICLE_SIZE + 2);
            p.vy = hostParticles.get(i * PARTICLE_SIZE + 3);
            p.ax = hostParticles.get(i * PARTICLE_SIZE + 4);
            p.ay = hostParticles.get(i * PARTICLE_SIZE + 5);
            p.radius = hostParticles.get(i * PARTICLE_SIZE + 6);
            p.mass = hostParticles.get(i * PARTICLE_SIZE + 7);
            p.charge = hostParticles.get(i * PARTICLE_SIZE + 8);
            pixiParticles.set(i,p);
        }
        
        return pixiParticles;
    }
    
}
