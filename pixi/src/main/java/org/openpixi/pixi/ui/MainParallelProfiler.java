/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openpixi.pixi.ui;

import java.io.IOException;
import java.util.ArrayList;
import org.openpixi.pixi.opencl.CLSimulation;
import org.openpixi.pixi.opencl.force.CLConstantForce;
import org.openpixi.pixi.physics.*;
import org.openpixi.pixi.physics.force.ConstantForce;

/**
 *
 * @author jan
 */
public class MainParallelProfiler {
    
    private static final int NUM_OF_PARTICLES = 100000;
    private static final int NUM_OF_PRINTED_PARTICLES = 3;
    private static final double PARTICLE_RADIUS = 0.1;
    private static final int STEPS = 100;
    
    public static void main(String[] args) throws IOException {
        
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
        
        // Initialize the java simulation with threads
        Simulation jtSim = new Simulation();
        jtSim.f = jSim.f;        
        // Copy all the particles
        jtSim.particles = new ArrayList<Particle2D>();
        for (Particle2D p: jSim.particles) {
            jtSim.particles.add(new Particle2D(p));
        }
        jtSim.particleMover = new ThreadParticleMover(jtSim);
        jtSim.particleMover.prepareAllParticles(jtSim);          
        
        // Compare running times of all the three simulations
       
        long jStart = System.nanoTime();
        for (int i = 0; i < STEPS; i++) {
            jSim.step();
        }
        double jTime = (double)(System.nanoTime() - jStart) * 1e-9;
        
        ArrayList<Particle2D> clResult;
        long clStart = System.nanoTime();
        for (int i = 0; i < STEPS; i++) {
            clSim.step();
        }
        clResult = clSim.getParticles();
        double clTime = (double)(System.nanoTime() - clStart) * 1e-9;
        
        long jtStart = System.nanoTime();
        for (int i = 0; i < STEPS; i++) {
            jtSim.step();
        }
        double jtTime = (double)(System.nanoTime() - jtStart) * 1e-9;
        jtSim.particleMover.finish();
        
        System.out.println("Number of particles: " + NUM_OF_PARTICLES);
        System.out.println("Number of steps: " + STEPS);
        System.out.printf("\nOrig: %.4fs CL: %.4fs Threads: %.4fs \n\n", jTime, clTime, jtTime);
        System.out.println("Particles x component comparison: ");
        for (int i = 0; i < NUM_OF_PRINTED_PARTICLES; i++) {
            System.out.printf("Orig: %.4f CL: %.4f Threads: %.4f \n", 
                    jSim.particles.get(i).x, 
                    clResult.get(i).x, 
                    jtSim.particles.get(i).x);
        }
    }
}
