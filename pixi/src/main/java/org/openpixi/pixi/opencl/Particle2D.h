
/*
    Defines 2D particle for opencl kernels.
    This file should be included by any kernel working with 2D particles.

    DESIGN NOTE
    - In the future it might be worth to define the particle as struct rather 
      then double vector to allow other types inside particle.
*/

#ifndef __PARTICLE_2D_CL
#define __PARTICLE_2D_CL

#define PX(P) (P.s0)
#define PY(P) (P.s1)
#define PVX(P) (P.s2)
#define PVY(P) (P.s3)
#define PAX(P) (P.s4)
#define PAY(P) (P.s5)
#define PRADIUS(P) (P.s6)
#define PMASS(P) (P.s7)
#define PCHARGE(P) (P.s8)

typedef double9 Particle2D;

#endif //__PARTICLE_2D_CL
