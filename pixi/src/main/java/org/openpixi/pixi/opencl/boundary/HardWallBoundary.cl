
/*
	Reflect particle off the hardwall boundary specified by 
	XMIN, XMAX, YMIN, YMAX.
	
	DESIGN NOTE
    - Boundary variables (XMIN, ..) have to be defined as macros during build.
    
    TODO
    - add complete() and prepare() functions from solver
*/

void checkBoundary(__global Particle2D * particle) {
	Particle2D p = *particle;
    
    if(PX(p) - PRADIUS(p) < XMIN) {
        PVX(p) = fabs(PVX(p));
    } 
    else if(PX(p) + PRADIUS(p) > XMAX) {
        PVX(p) = -fabs(PVX(p));
    }
    
    if(PY(p) - PRADIUS(p) < YMIN) {
        PVY(p) = fabs(PVY(p));
    } 
    else if(PY(p) + PRADIUS(p) > YMAX) {
        PVY(p) = -fabs(PVY(p));
    }
    
    *particle = p;
}
