package ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers;

import ru.eatTheFrog.Robots.Savables.ISavable;
import ru.eatTheFrog.Robots.model.Configuration.FoodsConfig;
import ru.eatTheFrog.Robots.model.Configuration.RobotsConfig;
import ru.eatTheFrog.Robots.model.Entities.Food.Food;
import ru.eatTheFrog.Robots.model.Entities.Food.FoodType;
import ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces.IEnemy;
import ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces.IReproduceRobot;
import ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces.IRobot;
import ru.eatTheFrog.Robots.model.Entities.Target;
import ru.eatTheFrog.Robots.model.GameAndArbitration.AbstractOnModelUpdatingCore;
import ru.eatTheFrog.Robots.model.GameAndArbitration.Game;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.FoodManager;
import ru.eatTheFrog.Robots.model.RobotBuilder;
import ru.eatTheFrog.Robots.model.RobotSchemes.AbstractScheme;
import ru.eatTheFrog.Robots.model.RobotsModules.RobotEnemySensor;
import ru.eatTheFrog.Robots.model.static_modules.REFLECTION_OnModelExecutor;
import ru.eatTheFrog.Robots.model.static_modules.distance.FindDistanceModule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class RobotsManager extends AbstractOnModelUpdatingCore implements ISavable {
    private Random random = new Random();
    public Game game;
    private FoodManager foodManager;
    private List<IRobot> robots;
    private final List<IRobot> robotsToCreate = Collections.synchronizedList(new ArrayList<>());

    public RobotsManager(Game game, FoodManager foodManager) {
        this.game = game;
        this.foodManager = foodManager;
        robots = Collections.synchronizedList(new ArrayList<>());
    }
    public void addRobotFromScheme(AbstractScheme robotScheme) {
        robots.add(RobotBuilder.buildFromScheme(this.game, robotScheme));
    }
    public boolean reproduceRequest(IReproduceRobot robot) {
        // Вот тут неочевидный баг был. Если убрать синхронизацию, то боты
        // размножаются одновременно и это выглядит мега-тупо.
        synchronized (robotsToCreate) {
            if (robots.size() + robotsToCreate.size() >= RobotsConfig.MAX_ROBOTS_IN_GAME)
                return false;
            var schemeNew = robot.getScheme().copy().mutate();

            var newRobot = RobotBuilder.buildFromScheme(this.game, schemeNew);
            var energyPercent = robot.getEnergyPercently();
            newRobot.setEnergyPercently(energyPercent/2);
            newRobot.setX(robot.getX());
            newRobot.setY(robot.getY());
            robot.setEnergyPercently(energyPercent/2);
            this.robotsToCreate.add(newRobot);
            return true;
        }
    }
    @REFLECTION_OnModelExecutor.OnModelAction
    public void placeRobotsFromQueue() {
        if (robotsToCreate.size() > 0) {
            robots.addAll(robotsToCreate);
            robotsToCreate.clear();
        }
    }
    @REFLECTION_OnModelExecutor.OnModelAction
    public void respawnBalance() {
        if (robots.size() < RobotsConfig.ROBOTS_FORCE_SPAWNING_COUNT) {
            if (robots.size() > 0)
                for (int i = 0; i < RobotsConfig.ROBOTS_FORCE_SPAWNING_COUNT - robots.size(); i++) {
                    var scheme = robots
                            .get(Math.abs(random.nextInt()) % robots.size())
                            .getScheme()
                            .randomizePosition(this.game.getWindowWidth(), this.game.getWindowHeight())
                            .mutate();
                    addRobotFromScheme(scheme);
                }
        }

    }
    @REFLECTION_OnModelExecutor.OnModelAction
    public void removeInjuredBots() {
        this.robots.stream()
                .filter(IRobot::isInjured)
                .forEach(x -> {
                    this.foodManager.tryToAddFood(
                            new Food(x.getX(), x.getY(), FoodType.FLESH,
                                    x.getThickness()*x.getLength()* FoodsConfig.FLESH_FOOD_POWER,
                                    this.game.getTime())
                    );
                });
        this.robots.removeIf(IRobot::isInjured);
    }
    public Stream<IRobot> getRobots() {
        return this.robots.stream();
    }
    public Stream<IEnemy> getEnemies(RobotEnemySensor enemySensor, Target robot) {
        return this.robots.stream()
                .filter((x) -> FindDistanceModule.getDistance(x, robot) < enemySensor.getSeeRadius())
                .map(x -> (IEnemy)x);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
