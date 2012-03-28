package org.openpixi.pixi.physics.boundary;

import org.openpixi.pixi.physics.Particle2D;
import org.openpixi.pixi.physics.force.Force;
import org.openpixi.pixi.physics.solver.Solver;

/**
 * Adds the check method necessary in the pure Java code (without OpenCL).
 * @author jan
 */
public abstract class JBoundary extends Boundary {	    

	public abstract void check(Particle2D particle, Force f, Solver s, double step);
}
