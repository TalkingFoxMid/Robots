package ru.eatTheFrog.Robots.model.RobotsModules;

import ru.eatTheFrog.Robots.model.Configuration.RobotsConfig;
import ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces.IEnemy;
import ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces.RobotRaw;
import ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers.RobotsManager;

import static ru.eatTheFrog.Robots.model.RobotsModules.RobotEnemyEstimator.EnemyEstimationPair;
import java.util.stream.Stream;

public class RobotEnemySensor {
    private RobotRaw robot;
    private RobotsManager robotsManager;
    private int seeRadius = RobotsConfig.ROBOTS_SCANNER_RADIUS;

    public RobotEnemySensor(RobotRaw robot, RobotsManager robotsManager) {
        this.robot = robot;
        this.robotsManager = robotsManager;
    }

    public int getSeeRadius() {
        return seeRadius;
    }

    public Stream<IEnemy> getNearestRobots() {
        return robotsManager.getEnemies(robot.robotRobotsSensor, robot)
                .map((x) -> new EnemyEstimationPair(x, robot.estimateEnemy(x)))
                .filter((x) -> x.value != 0)
                .sorted((x, y) -> (int) (x.value - y.value))
                .map(x -> x.enemy);
    }
}
