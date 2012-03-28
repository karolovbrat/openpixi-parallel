
/*
	Euler algorithm.
	Particle before the update: x(t), v(t), a(t);
			after the update: x(t+dt), v(t+dt), a(t);
*/
void step(__global Particle2D * particle, double step) {
	Particle p = *particle;

	//a(t) = F(v(t), x(t)) / m
	PAX(p) = getForceX(p) / PMASS(p);
	PAY(p) = getForceY(p) / PMASS(p);

	// x(t+dt) = x(t) + v(t)*dt
	PX(p) += PVX(p) * step;
	PY(p) += PVY(p) * step;

	// v(t+dt) = v(t) + a(t)*dt
	PVX(p) += PAX(p) * step;
	PVY(p) += PAY(p) * step;
	
	*particle = p;
}
