
/*
    DESIGN NOTE
    - It would be better to have body of step() and boundaryCheck() methods
      directly in the move() method to have one less global memory access.
      Now, we access a 2D particle in both methods (step() and boundaryCheck()).
*/

#include "Particle2D.h"

__kernel void move(__global Particle2D * particles, double step) {
	__global Particle2D * p = &(particles[get_global_id()]);
	step(p, step);
	boundaryCheck(p);
}
