
/*
    HARDWALL BOUNDARY
    =================

	Reflect particle off the hardwall boundary specified by 
	XMIN, XMAX, YMIN, YMAX.
	
	DESIGN NOTE
    - Boundary variables (XMIN, ..) have to be defined as macros during build.
    
    TODO
    - add complete() and prepare() functions from solver
*/
Particle2D checkBoundary(Particle2D p) {
    
    if(p.x - p.radius < XMIN) {
        p.vx = fabs(p.vx);
    } 
    else if(p.x + p.radius > XMAX) {
        p.vx = -fabs(p.vx);
    }
    
    if(p.y - p.radius < YMIN) {
        p.vy = fabs(p.vy);
    } 
    else if(p.y + p.radius > YMAX) {
        p.vy = -fabs(p.vy);
    }
    
    return p;
}
