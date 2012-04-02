
/*
	Euler algorithm
    ===============

	Particle before the update: x(t), v(t), a(t);
			after the update: x(t+dt), v(t+dt), a(t);
*/
Particle2D solverStep(Particle2D p, prec_t step) {

	//a(t) = F(v(t), x(t)) / m
	p.ax = getForceX(p) / p.mass;
	p.ay = getForceY(p) / p.mass;

	// x(t+dt) = x(t) + v(t)*dt
	p.x += p.vx * step;
	p.y += p.vy * step;

	// v(t+dt) = v(t) + a(t)*dt
	p.vx += p.ax * step;
	p.vy += p.ay * step;
	
	return p;
}
