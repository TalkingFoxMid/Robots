package ru.eatTheFrog.Robots.model.GameAndArbitration;

import ru.eatTheFrog.Robots.Savables.ISavable;
import ru.eatTheFrog.Robots.model.Entities.Food.Food;
import ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces.IDrawableRobot;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameInterfaces.GameUIBridge;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.FoodManager;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.IClock;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.RobotsManager;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.TheClocks;
import ru.eatTheFrog.Robots.model.RobotSchemes.AbstractScheme;
import ru.eatTheFrog.Robots.model.RobotsThreadsCalculateAction;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import static ru.eatTheFrog.Robots.model.static_modules.REFLECTION_OnModelExecutor.OnModelAction;
public class Game extends AbstractOnModelUpdatingCore implements GameUIBridge, ISizedGame {
    ForkJoinPool pool = new ForkJoinPool();

    private double m_width;
    private double m_height;
    private TheClocks theClocks;
    public RobotsManager robotsManager;
    public FoodManager foodManager;

    public Game() {
        super();

        this.foodManager = new FoodManager(this);
        this.innerGoals.add(foodManager);

        this.theClocks = new TheClocks();
        this.innerGoals.add(theClocks);

        this.robotsManager = new RobotsManager(this, foodManager);
        this.innerGoals.add(robotsManager);

    }

    @Override
    public Iterator<Food> getFoodDrawable() {
        return this.foodManager.getFood().iterator();
    }
    public void nextTick() {
        this.onModelUpdate();
    }

    @Override
    public void addRobotFromScheme(AbstractScheme robotScheme) {
        this.robotsManager.addRobotFromScheme(robotScheme);
    }

    public IClock getClocks() {
        return this.theClocks;
    }

    private boolean isWindowNullSized() {
        return this.m_width == 0 || this.m_height == 0;
    }

    @OnModelAction
    public void startCalculation() {
        if (isWindowNullSized()) return;
        var taskVeggies = new RobotsThreadsCalculateAction(
                this.robotsManager.getRobots().spliterator(),
                this);
        pool.invoke(taskVeggies);
    }

    @Override
    public void setWidthHeight(double width, double height) {
        m_width = width;
        m_height = height;
    }


    @Override
    public Iterator<IDrawableRobot> getRobotsDrawable() {
        return this.robotsManager.getRobots().map(x -> (IDrawableRobot)x).iterator();
    }


    public Stream<Food> getFood() {
        return this.foodManager.getFood();
    }


    @Override
    public double getWindowWidth() {
        return this.m_width;
    }

    @Override
    public double getWindowHeight() {
        return this.m_height;
    }
    public long getTime() {
        return this.theClocks.getTime();
    }

//    @Override
//    public void writeExternal(ObjectOutput out) throws IOException {
//        out.writeObject(this.robotsManager);
//        out.writeObject(this.foodManager);
//    }
//
//    @Override
//    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//        this.robotsManager.readExternal(in);
//        this.foodManager.readExternal(in);
//    }
}
