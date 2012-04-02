
/*
    2D PARTICLE
    ===========

    Defines 2D particle for opencl kernels.
    This file should be included by any kernel working with 2D particles.

    DESIGN NOTE
    - In the future it might be worth to define the particle as struct rather 
      then double vector to allow other types inside particle.
*/

#ifndef __PARTICLE_2D_CL
#define __PARTICLE_2D_CL

#define PARTICLE_SIZE 9

// Defines the used precision (can be switched between float and double)
typedef float prec_t;

typedef struct {
    prec_t x;
    prec_t y;
    prec_t vx;
    prec_t vy;
    prec_t ax;
    prec_t ay;
    prec_t radius;
    prec_t mass;
    prec_t charge;
} Particle2D;

#endif //__PARTICLE_2D_CL
