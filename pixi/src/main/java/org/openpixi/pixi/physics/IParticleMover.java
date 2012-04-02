/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openpixi.pixi.physics;

/**
 *
 * @author jan
 */
public interface IParticleMover {

    void completeAllParticles(Simulation s);

    void particlePush(Simulation s);

    void prepareAllParticles(Simulation s);
    
    void finish();    
}
