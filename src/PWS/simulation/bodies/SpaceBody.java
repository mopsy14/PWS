package PWS.simulation.bodies;

import PWS.simulation.Simulation;

public abstract class SpaceBody {
    public double x;
    public double y;
    public double z;
    public double mass;
    public double r;
    public double vx;
    public double vy;
    public double vz;

    public synchronized void updateVelocity() {
        double ax = 0;
        double ay = 0;
        double az = 0;
        for (SpaceBody body : Simulation.INSTANCE.spaceBodies) {
            double dx = (body.x-this.x);
            double dy = (body.y-this.y);
            double dz = (body.z-this.z);
            double rSquare = dx*dx + dy*dy + dz*dz; // Bereken kwadraat van afstand.
            //Bereken vervolgens de waarde die gelijk is voor alle assen en vermenigvuldig dan met de afstand op die as.
            double multiplier = 6.674e-11 * body.mass / Math.sqrt(rSquare*rSquare*rSquare);
            ax += multiplier * dx;
            ay += multiplier * dy;
            az += multiplier * dz;
        }
        this.vx += ax * Simulation.INSTANCE.getStepSize();
        this.vy += ay * Simulation.INSTANCE.getStepSize();
        this.vz += az * Simulation.INSTANCE.getStepSize();
    }

    public synchronized void updatePosition() {
        x += vx * Simulation.INSTANCE.getStepSize();
        y += vy * Simulation.INSTANCE.getStepSize();
        z += vz * Simulation.INSTANCE.getStepSize();
    }

}
