
--------------------------------------------------------------------------------
OpenCL Parallelization
--------------------------------------------------------------------------------

The OpenCL parallelization of pixi is incorporated to the existing java code
using JavaCL (library for calling OpenCL kernels and writing OpenCL host code
in Java).

The provided sample parallelizes the particle movement and boundary checking.
It basically parallelizes the method SimpleParticleMover.particlePush() 
(in the original code the name of the class was just ParticleMover).
Each particle is moved and its boundary checked independently by one OpenCL 
thread. The other parts of the simulation, current grid update and collision 
detections and resolutions are not implemented in the sample. Therefore,
when comparing the parallel simulation to the original one the current grid
update and collision detections are turned off (commented out). Furthermore, 
the sample was only tested in batch (non-interactive) mode.

When creating the sample a strong emphasis was made on the design. 
Specifically, the sample tries to reproduce or show how to introduce 
the polymorphism present in the sequential (freedom in choosing type of force, 
boundary, solver, etc.) version into the parallel version.
For that purpose classes of the OpenCL version (see org.openpixi.pixi.opencl 
package and its subpackages) closely match the original classes. However, these
matched classes do not have any functionality. They just return the specific 
OpenCL kernels (present in "Other Sources") which implement the functionality 
in question. For example, the CLHardWallBoundary class only returns 
the HardWallBoundary.cl kernel which implements the boundary checking.
The fragments of OpenCL kernels are put together and run by the CLSimulation 
class. Although the sample shows only one type of
force, boundary and solver it should be straight forward to add other force,
boundary and solver types (further design analysis is required to allow combined 
force and grid force which are more complicated). 

Unfortunately, at the current time the sample does not achieve a significantly 
better performance (see Results Table at the end of file). We discovered that
higher number of iterations (simulation steps) decreases the performance of 
the parallel version. This probably happens out of two reasons. First of all, 
each step is a synchronization point and synchronization is expensive (in means
of time). Secondly, the actual code executed in parallel is very fast and very
simple; thus, executing it in parallel does not yield such a great speedup.

Nevertheless, better results should be obtained when parallelizing the collision
detection. In collision detection we need to go over all pairs of the particles
which can all be computed in parallel. Moreover, the collision detection and 
resolution contain more complex code (more operations) which is also good for
the parallelization.

Undoubtedly, from the theoretical point of view the parallelization of the 
CurrentGrid.update() method would be quite interesting. In this method we do 
add and multiply reduction over all the particles within a grid cell. 
The most straight forward approach in which we would do atomic add and multiply 
could be too slow. Some inspiration can be taken from the many available 
materials on how to compute image histograms in parallel which are quite 
similar to the current grid.

--------------------------------------------------------------------------------
Java Thread Parallelization
--------------------------------------------------------------------------------

For reference, we also implemented a parallel version using java threads
(just the particle movement was implemented similarly as in the OpenCL version).
The whole implementation can be seen in ThreadParticleMover class in 
org.openpixi.pixi.physics package.

--------------------------------------------------------------------------------
Results Table
--------------------------------------------------------------------------------

On a strong computer we eventually did get some speedup. However, on ordinary
notebook we experienced a slowdown of the parallel version. The notebook had
only two cores and the overhead of threads was greater than the gain of 
parallelization.

Number of particles: 100000
Number of steps: 100
Number of threads: 10 (in threaded version only)

(strong computer)
Intel Xeon E5620 (4 cores) + NVIDIA GTX480
Original versin: 0.5665s
Threaded version: 0.2992s
OpenCL version: 0.1076s

(ordinary notebook)
Intel Core 2 Duo P8700 (2 cores) + no graphics card => OpenCL runs on processor
Original versin: 0.5611s 
Threaded version: 0.8464s
OpenCL version: 1.0346s


