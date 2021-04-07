package ru.eatTheFrog.Robots.model.Entities.RobotAndInterfaces;

import ru.eatTheFrog.Robots.Savables.ISavable;
import ru.eatTheFrog.Robots.model.RobotSchemes.AbstractScheme;
import ru.eatTheFrog.Robots.model.static_modules.IReadonlyVector2D;

public interface IRobot extends IDrawableRobot, ILivingRobot, IEnemy, IEnemyEstimateRobot, IReproduceRobot {
    public void onModelUpdate();
    public void doInjured();
    public boolean isInjured();
    public double getSize();
    public IReadonlyVector2D getPosition();
    public void applyPhysics();
    public AbstractScheme getScheme();
    public double estimateEnemy(IEnemy robot);
    public void harm(double damage);
    public double getHealth();
    public void setEnergyPercently(double fraction);

    double getEnergyPercently();
    void applyAging();
}
