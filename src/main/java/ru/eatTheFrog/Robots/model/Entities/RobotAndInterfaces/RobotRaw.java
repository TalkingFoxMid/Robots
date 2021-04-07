package ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces;

import ru.eatTheFrog.Robots.model.Entities.*;
import ru.eatTheFrog.Robots.model.Configuration.RobotsConfig;
import ru.eatTheFrog.Robots.model.RobotSchemes.AbstractScheme;
import ru.eatTheFrog.Robots.model.RobotsModules.*;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.IClock;
import ru.eatTheFrog.Robots.model.Tasks.LiveTask;
import ru.eatTheFrog.Robots.model.static_modules.IReadonlyVector2D;
import ru.eatTheFrog.Robots.model.static_modules.MutableVector2D;
import ru.eatTheFrog.Robots.model.static_modules.SafetyColorBuilder;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Optional;

import static ru.eatTheFrog.Robots.model.static_modules.Algebra.MathFunctions.relu2;

public class RobotRaw extends PhysicalBody implements IRobot, IWanderizeRobot, IEater {
    // ПОЛЯ ТИПА DOUBLE В РОБОТЕ ДОЛЖНЫ БЫТЬ НАЗВАНЫ ТАК ЖЕ КАК В SCHEME ПО КОТОРОЙ ОН СОЗДАЁТСЯ
    // ИБО ОНИ АВТОМАТИЧЕСКИ КОПИРУЮТСЯ ПРИ СОЗДАНИИ НОВОГО РОБОТА ПО ИМЕНАМ!
    public AbstractScheme schemeCreatedFrom;

    public RobotGameManipulator robotGameManipulator;
    public RobotMouth robotMouth;

    public RobotFoodsSensor robotFoodSensor;
    public RobotEnemySensor robotRobotsSensor;
    public IClock clock;
    public double redColor;
    public double greenColor;
    public double blueColor;
    private long age;
    private boolean deadlyInjured = false;
    public volatile double velocityLimit = 1;
    public volatile double angularAcceleration = 0.001;
    public double acceleration = 0.01;
    public double wanderizeIrregularityRate = 10;

    public EntityType type;
    public LiveTask task;
    //visual and other
    public double length = 30;
    public double thickness = 10;
    public double inclinationToVegetarianism = 1;
    public double getInclinationToVegetarianism() {
        return inclinationToVegetarianism;
    }
    public double getInclinationToFleshEating() {
        return 1-inclinationToVegetarianism;
    }
    public RobotEnemyEstimator robotEnemyEstimator;
    public double attack = 5;
    public double energy = 0;
    public double maxEnergy = 0;
    public RobotReproducer robotReproducer;
    public double health = RobotsConfig.DEFAULT_HEALTH;
    private final double baseMass = 10;
    public Wanderizer wanderizer = new Wanderizer(this);

    public RobotRaw() {

    }

    public double getHealth() {
        return this.health + this.length * this.thickness * RobotsConfig.HEALTH_FROM_SIZE_BONUS;
    }
    public void setEnergyPercently(double fraction) {
        this.energy = this.maxEnergy * fraction;
    }
    public double getEnergyPercently() {
        return this.energy / this.maxEnergy;
    }

    @Override
    public void applyAging() {
        this.age += 1;
        if (this.age > RobotsConfig.MAXIMUM_AGE*10)
            this.doInjured();
    }

    public void initAfterBuilding() {
        this.energy = thickness * RobotsConfig.ENERGY_PER_THICKNESS + RobotsConfig.BASE_ENERGY;
        this.maxEnergy = thickness * RobotsConfig.ENERGY_PER_THICKNESS + RobotsConfig.BASE_ENERGY;

    }

    @Override
    public void applyPhysics() {
        this.rotationSpeed *= relu2(1-getMass()/1000-length/200);
        this.velocity.multiply(relu2(1+this.getSlipping()/1000-getMass()/1000));
    }

    public void addGameManupulator(RobotGameManipulator robotGameManipulator) {
        this.robotGameManipulator = robotGameManipulator;
    }
    public double getMass() {
        var mass = baseMass;
        mass += this.length * this.thickness * 0.1;
        return mass;
    }
    public double getWanderizeIrregularityRate() {
        return this.wanderizeIrregularityRate;
    }
    public void setVelocity(double velocity) {
        this.velocityLimit = velocity;
    }



    public void setDirection(double d) {
        direction = d;
    }

    public IDrawableRobot asDrawable(){
        return (IDrawableRobot) this;
    }

    @Override
    public long getTime() {
        return this.clock.getTime();
    }

    public double getX() {
        return this.position.x;
    }

    public double getY() {
        return this.position.y;
    }

    @Override
    public void setX(double x) {
        this.position.x = x;
    }

    @Override
    public void setY(double y) {
        this.position.y = y;
    }

    public double getDirection() {
        return direction;
    }

    @Override
    public double getLength() {
        return this.length;
    }

    @Override
    public double getThickness() {
        return this.thickness;
    }

    @Override
    public void speedUp() {
        this.velocity.plus(MutableVector2D
                .fromAngle(this.direction)
                .multiply(this.acceleration +this.length/1000)
        );
        if (this.velocity.length() > this.velocityLimit)
            this.velocity = this.velocity.normalize().multiply(this.velocityLimit);
    }
    @Override
    public void frictionUp() {
        this.velocity.multiply(0.99);
    }
    public double getSize() {
        return Math.max(this.length, this.thickness);
    }
    @Override
    public void onModelUpdate() {
        getTiredDueTime();

        this.task.onModelUpdate();
    }
    public void getTiredDueTime() {
        this.energy -= 0.1*RobotsConfig.FATIQUE_RATE;
        if (this.energy < 0)
            this.doInjured();
    }
    public boolean isInjured() {
        return this.deadlyInjured;
    }

    @Override
    public IReadonlyVector2D getPosition() {
        return this.position;
    }


    public void doInjured() {
        this.deadlyInjured = true;
    }

    @Override
    public boolean isDisposed() {
        return this.deadlyInjured;
    }
    public void makeEaten() {
        this.deadlyInjured = true;
    }

    @Override
    public Optional<RobotMouth> getRobotMouth() {
        return Optional.ofNullable(this.robotMouth);
    }

    @Override
    public Optional<RobotFoodsSensor> getFoodSensor() {
        return Optional.ofNullable(this.robotFoodSensor);
    }
    @Override
    public Optional<RobotEnemySensor> getThreatSensor() {
        return Optional.ofNullable(this.robotRobotsSensor);
    }

    @Override
    public Target getTargetWanderTo() {
        return this.wanderizer.getTargetWanderTo();
    }

    @Override
    public void addEnergy(double worth) {
        this.energy += worth;
        if (this.energy > this.maxEnergy) this.energy = maxEnergy;
    }


    @Override
    public MutableVector2D getVelocity() {
        return this.velocity;
    }

    @Override
    public double getRotationAcceleration() {
        return this.angularAcceleration;
    }


    @Override
    public RobotGameManipulator getManipulator() {
        return this.robotGameManipulator;
    }
    @Override
    public Color getColor() {
        return SafetyColorBuilder.buildColorFromRGB(this.redColor, this.greenColor, this.blueColor);
    }

    public void addFoodSensor(RobotFoodsSensor foodSensor) {
        this.robotFoodSensor = foodSensor;
    }
    public void addDangerSensor(RobotEnemySensor dangerSensor) {
        this.robotRobotsSensor = dangerSensor;
    }

    @Override
    public double getAttack() {
        return attack;
    }

    @Override
    public AbstractScheme getScheme() {
        return this.schemeCreatedFrom;
    }

    @Override
    public double estimateEnemy(IEnemy robot) {
        return robotEnemyEstimator.estimateEnemy(robot);
    }

    @Override
    public RobotReproducer getReproducer() {
        return this.robotReproducer;
    }

    @Override
    public void harm(double damage) {
        this.health -= damage;
        if(this.getHealth() < 0)
            this.doInjured();
    }
//
//    @Override
//    public void writeExternal(ObjectOutput out) throws IOException {
//        out.writeLong(age);
//    }
//
//    @Override
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//
//    }
}
