package PWS.simulation.bodies;

import PWS.simulation.Simulation;

public abstract class SpaceBody {
    protected double x;
    protected double y;
    protected double z;
    protected double mass;
    protected double r;
    protected double vx;
    protected double vy;
    protected double vz;
    protected final Simulation simulation;

    public SpaceBody(double x, double y, double z, double mass, double r, double vx, double vy, double vz, Simulation simulation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mass = mass;
        this.r = r;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.simulation = simulation;
    }

    //Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getMass() {
        return mass;
    }

    public double getR() {
        return r;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getVz() {
        return vz;
    }

    //Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setR(double r) {
        this.r = r;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setVz(double vz) {
        this.vz = vz;
    }

    public synchronized void updateVelocity() {
        double ax = 0;
        double ay = 0;
        double az = 0;
        for (SpaceBody body : simulation.spaceBodies) {
            if (body==this)
                continue;
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
        this.vx += ax * simulation.getStepSize();
        this.vy += ay * simulation.getStepSize();
        this.vz += az * simulation.getStepSize();
    }

    public synchronized void updatePosition() {
        x += vx * simulation.getStepSize();
        y += vy * simulation.getStepSize();
        z += vz * simulation.getStepSize();
    }

    public synchronized void updateLighting(){}
}
