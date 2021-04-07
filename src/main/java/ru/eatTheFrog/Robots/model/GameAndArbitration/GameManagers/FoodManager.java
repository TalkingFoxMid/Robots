package ru.eatTheFrog.Robots.model.GameAndArbitration.GameManagers;

import ru.eatTheFrog.Robots.Savables.ISavable;
import ru.eatTheFrog.Robots.model.Configuration.FoodsConfig;
import ru.eatTheFrog.Robots.model.Entities.Food.Food;
import ru.eatTheFrog.Robots.model.Entities.Food.FoodType;
import ru.eatTheFrog.Robots.model.GameAndArbitration.AbstractOnModelUpdatingCore;
import ru.eatTheFrog.Robots.model.GameAndArbitration.Game;
import ru.eatTheFrog.Robots.sugar.FrequencyExecutor;
import static ru.eatTheFrog.Robots.model.static_modules.REFLECTION_OnModelExecutor.OnModelAction;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

public class FoodManager extends AbstractOnModelUpdatingCore implements ISavable {
    private double foodCount = 0;
    private Game game;
    private Random random = new Random();
    private FrequencyExecutor foodRecounter = new FrequencyExecutor(this::recountFood,
            FoodsConfig.FOOD_SPAWN_PERIOD);
    private FrequencyExecutor foodSpawner = new FrequencyExecutor(() -> spawnFoodRandom(FoodType.GRASS),
            FoodsConfig.FOOD_SPAWN_PERIOD);
    public FoodManager(Game game) {
        super();
        this.game = game;
        this.foods = foods;
        foods = new ArrayList<>();
    }

    public Stream<Food> getFood() {
        return this.foods.parallelStream();
    }
    private ArrayList<Food> foods;

    @OnModelAction
    public void executeFoodSpawner() { foodSpawner.executeFrequency();}
    @OnModelAction
    public void foodRecount() {
        foodRecounter.executeFrequency();
    }
    @OnModelAction
    public void removeOldFood() {
        this.foods.removeIf(x -> x.foodType() != FoodType.GRASS &&
                game.getClocks().getTime() - x.getCreateTime() > FoodsConfig.FOOD_TIME_LIVE);
    }

    public void tryToAddFood(Food food) {
        this.foods.add(food);
    }
    void spawnFoodRandom(FoodType foodType) {
        for (int i = 0; i < FoodsConfig.FOOD_SPAWN_BATCH; i++) {
            if (foods.stream().filter(x -> x.foodType() == FoodType.GRASS).count() >= FoodsConfig.MAX_FOOD)
                return;
            var randomFood = new Food((random.nextDouble() * Double.MAX_VALUE) % game.getWindowWidth(),
                    (random.nextDouble() * Double.MAX_VALUE) % game.getWindowHeight(), FoodType.GRASS, 10,
                    game.getClocks().getTime());
            this.foods.add(randomFood);
            this.foodCount += 1;
        }
    }
    private void recountFood() {
        this.foodCount = foods.size();
    }
    @OnModelAction
    public void removeEatenFood() {
        this.foods.removeIf(Food::isDisposed);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}
