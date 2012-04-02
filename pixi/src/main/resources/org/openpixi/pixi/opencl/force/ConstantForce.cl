
/*
    CONSTANT FORCE
    ==============

	DESIGN NOTE
    - Constant force variables (below) have to be defined as macros during build.
      FGX, FGY, FDRAG, FEX, FEY, FBZ
    - Further methods can be easily added in the similar manner as getForceX()
      and getForceY().
*/

inline float getForceX(Particle2D p) {
    return -FDRAG * p.vx + p.mass * FGX + p.charge * FEX + p.charge * p.vy * FBZ;
}


inline float getForceY(Particle2D p) {
    return -FDRAG * p.vy + p.mass * FGY + p.charge * FEY - p.charge * p.vx * FBZ;
}
