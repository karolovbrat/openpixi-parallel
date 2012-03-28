
/*
	DESIGN NOTE
    - Constant force variables (below) have to be defined as macros during build.
      FGX, FGY, FDRAG, FEX, FEY, FBZ
    - Further methods can be easily added in the similar manner as getForceX()
      and getForceY().
*/

inline double getForceX(Particle2D p) {
    return -FDRAG * PVX(p) + PMASS(p) * FGX + PCHARGE(p) * FEX + PCHARGE(p) * PVY(p) * FBZ;
}


inline double getForceY(Particle2D p) {
    return -FDRAG * PVY(Y) + PMASS(p) * FGY + PCHARGE(p) * FEY - PCHARGE(p) * PVX(p) * FBZ;
}
