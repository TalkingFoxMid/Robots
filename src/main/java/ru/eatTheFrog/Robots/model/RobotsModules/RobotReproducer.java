package ru.eatTheFrog.Robots.model.RobotsModules;

import ru.eatTheFrog.Robots.model.Configuration.RobotsConfig;
import ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces.IReproduceRobot;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.RobotsManager;

public class RobotReproducer {
    private final RobotsManager robotsManager;
    private final IReproduceRobot robot;
    public RobotReproducer(RobotsManager robotsManager, IReproduceRobot robot) {
        this.robotsManager = robotsManager;
        this.robot = robot;
    }
    public boolean requestToReproducing() {
        if(robot.getEnergyPercently() < RobotsConfig.MINIMUM_ENERGY_PERCENT_TO_REPRODUCE)
            return false;
        return robotsManager.reproduceRequest(robot);
    }
}
