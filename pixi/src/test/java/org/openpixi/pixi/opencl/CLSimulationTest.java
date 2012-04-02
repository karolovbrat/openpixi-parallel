package org.openpixi.pixi.opencl;

import java.io.IOException;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.openpixi.pixi.opencl.force.CLConstantForce;
import org.openpixi.pixi.physics.InitialConditions;
import org.openpixi.pixi.physics.Particle2D;
import org.openpixi.pixi.physics.SimpleParticleMover;
import org.openpixi.pixi.physics.Simulation;
import org.openpixi.pixi.physics.force.ConstantForce;

/**
 * Tests the OpenCL simulation.
 * @author jan
 */
public class CLSimulationTest extends TestCase {
    
    private static final int NUM_OF_PARTICLES = 10;
    private static final double PARTICLE_RADIUS = 0.1;
    private static final int STEPS = 1000;
    private static final double ACCURACY_LIMIT = 1e-2;
    
    
    public CLSimulationTest(String testName) {
        super(testName);
    }
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    
    private void assertEquals(
            Particle2D expected, Particle2D actual, double limit) {
        assertEquals(expected.x, actual.x, limit);
        assertEquals(expected.y, actual.y, limit);
        assertEquals(expected.vx, actual.vx, limit);
        assertEquals(expected.vy, actual.vy, limit);
        assertEquals(expected.ax, actual.ax, limit);
        assertEquals(expected.ay, actual.ay, limit);
    }
    
    
    /**
     * Compares the results of OpenCL simulation 
     * to original pure Java simulation. 
     */
    public void testCLSimulation() throws IOException {
        
        // Initialize the java simulation
        Simulation jSim = new Simulation();
        ConstantForce f = new ConstantForce();
        f.gx = 0.01;
        f.bz = 0.005;
        f.ex = -0.03;
        jSim.f.add(f);
        InitialConditions.createRandomParticles(
                jSim, NUM_OF_PARTICLES, PARTICLE_RADIUS);
        jSim.particleMover.prepareAllParticles(jSim);
        
        // Initialize the OpenCL simulation
        CLSimulation clSim = new CLSimulation();
        CLConstantForce clf = new CLConstantForce();
        clf.gx = 0.01;
        clf.bz = 0.005;
        clf.ex = -0.03;
        clSim.force = clf;
        clSim.setParticles(jSim.particles);
        clSim.createProgram();       
        
        ArrayList<Particle2D> expectedParticles;
        ArrayList<Particle2D> actualParticles;
        
        // Compare particles before simulation (just in case :))
        expectedParticles = jSim.particles;
        actualParticles = clSim.getParticles();
        for (int i = 0; i < NUM_OF_PARTICLES; i++) {
            assertEquals(expectedParticles.get(i), actualParticles.get(i), 
                    ACCURACY_LIMIT);
        }
        
        // Run both simulations
        for (int i = 0; i < STEPS; i++) {
            jSim.step();
            clSim.step();
        }
        
        // Compare results
        expectedParticles = jSim.particles;
        actualParticles = clSim.getParticles();
        for (int i = 0; i < NUM_OF_PARTICLES; i++) {
            assertEquals(expectedParticles.get(i), actualParticles.get(i), 
                    ACCURACY_LIMIT);
        }
    }
}
