package org.openpixi.pixi.physics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openpixi.pixi.physics.boundary.JBoundary;
import org.openpixi.pixi.physics.force.Force;
import org.openpixi.pixi.physics.solver.Solver;

/**
 * Moves multiple particles at once using java threads.
 * The number of created threads is independent of the number of particles.
 * If there is more particles than threads some threads calculate more particles. 
 * @author jan
 */
public class ThreadParticleMover implements IParticleMover {
    
    private abstract class ParticleAction implements Callable<Object> {
        public Solver solver;
        public Force force;
        public List<Particle2D> particles;
        public double step;
        
        private int threadIdx;        

        public ParticleAction(int threadIdx) {
            this.threadIdx = threadIdx;
        }                
        
        public Object call() {
            for (int pIdx = threadIdx; pIdx < particles.size(); pIdx += NUM_OF_THREADS) {
                particleAction(pIdx);
            }  
            return null;
        }
        
        /** 
         * This is a template method and needs to be overriden by the children 
         * of this class.
         * Performs a specific action on a particle with given index.
         */
        public abstract void particleAction(int pIdx);
    }
    
    
    private class Step extends ParticleAction {
        public JBoundary boundary;

        public Step(int threadIdx) {
            super(threadIdx);
        }
        
        @Override
        public void particleAction(int pIdx) {
            solver.step(particles.get(pIdx), force, step);
            boundary.check(particles.get(pIdx), force, solver, step);
        }
    }
    
    
    private class Complete extends ParticleAction {
        public Complete(int threadIdx) {
            super(threadIdx);
        }
        
        @Override
        public void particleAction(int pIdx) {
            solver.complete(particles.get(pIdx), force, step);
        }
    }
    
    
    private class Prepare extends ParticleAction {
        public Prepare(int threadIdx) {
            super(threadIdx);
        }
        
        @Override
        public void particleAction(int pIdx) {
            solver.prepare(particles.get(pIdx), force, step);
        }
    }
    
    
    private static final int NUM_OF_THREADS = 10;
    private ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);
    private List<Callable<Object>> completeTasks = new ArrayList<Callable<Object>>(NUM_OF_THREADS);
    private List<Callable<Object>> prepareTasks = new ArrayList<Callable<Object>>(NUM_OF_THREADS);
    private List<Callable<Object>> stepTasks = new ArrayList<Callable<Object>>(NUM_OF_THREADS);
    
    
    public ThreadParticleMover(Simulation s) {
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            Complete c = new Complete(i);
            c.force = s.f;
            c.particles = s.particles;
            c.solver = s.psolver;
            c.step = s.tstep;
            completeTasks.add(c);
            
            Step step = new Step(i);
            step.boundary = s.boundary;
            step.force = s.f;
            step.particles = s.particles;
            step.solver = s.psolver;
            step.step = s.tstep;
            stepTasks.add(step);
            
            Prepare p = new Prepare(i);
            p.force = s.f;
            p.particles = s.particles;
            p.solver = s.psolver;
            p.step = s.tstep;
            prepareTasks.add(p);
        }
    }
    
    
    public void completeAllParticles(Simulation s) {
        try {
            executor.invokeAll(completeTasks);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadParticleMover.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void particlePush(Simulation s) {        
        try {
            executor.invokeAll(stepTasks);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadParticleMover.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void prepareAllParticles(Simulation s) {
        try {
            executor.invokeAll(prepareTasks);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadParticleMover.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Gracefully end the executor.
     */
    public void finish() {
        executor.shutdown();
    }
    
}
