
/*
    PARTICLE MOVER
    ==============
*/

__kernel void move(__global prec_t * particles, prec_t step) {
	int pid = get_global_id(0) * PARTICLE_SIZE;
    Particle2D p;
    p.x = particles[pid];
    p.y = particles[pid + 1];
    p.vx = particles[pid + 2];
    p.vy = particles[pid + 3];
    p.ax = particles[pid + 4];
    p.ay = particles[pid + 5];
    p.radius = particles[pid + 6];
    p.mass = particles[pid + 7];
    p.charge = particles[pid + 8];

	p = solverStep(p, step);
	p = checkBoundary(p);

    particles[pid] = p.x;
    particles[pid + 1] = p.y;
    particles[pid + 2] = p.vx;
    particles[pid + 3] = p.vy;
    particles[pid + 4] = p.ax;
    particles[pid + 5] = p.ay;
    particles[pid + 6] = p.radius;
    particles[pid + 7] = p.mass;
    particles[pid + 8] = p.charge;
}
