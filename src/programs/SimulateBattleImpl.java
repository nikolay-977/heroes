package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    //  Метод simulate:
    //  В методе simulate, на каждой итерации основного цикла while, вызывается метод attack для двух армий.
    //  Максимальное количество итераций цикла равно n, так как каждая армия может терять ровно одного юнита за итерацию.
    //  В худшем случае, на i-й итерации размер атакующей армии уменьшается и может составлять порядка n-i для каждой стороны.
    //  Сложность цикла while в методе simulate: T(n) = O(n) * O(n) = O(n^2)
    //  Метод attack:
    //  Удаление всех мёртвых нападающих юнитов O(n)
    //  Цикл атаки O(n)
    //  Сложность метода attack: O(n) + O(n) = O(n)
    //  Наиболее затратной операцией здесь является цикл while в методе simulate.
    //  Итоговая сложность алгоритма составляет: O(n^2)
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Получаем списки юнитов армий
        Set<Unit> playerUnits = new HashSet<>(playerArmy.getUnits());
        Set<Unit> computerUnits = new HashSet<>(computerArmy.getUnits());

        // Пока обе армии имеют живых юнитов, сражение продолжается
        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            attack(playerUnits, computerUnits);
            if (computerUnits.isEmpty()) break;
            attack(computerUnits, playerUnits);
        }
    }

    public void attack(Set<Unit> attackingUnits, Set<Unit> targetUnits) throws InterruptedException {
        // Удаляем всех мёртвых нападающих юнитов
        attackingUnits.removeIf(unit -> !unit.isAlive());
        if (attackingUnits.isEmpty() || targetUnits.isEmpty()) return;


        // Цикл атаки
        for (Unit attacker : attackingUnits) {
            if (!attacker.isAlive()) continue;

            // Выбираем цель
            Unit target = attacker.getProgram().attack();

            // Проверяем, что цель существует и логируем событие
            if (target != null) {
                boolean wasAliveBefore = target.isAlive();
                printBattleLog.printBattleLog(attacker, target);
                // Если цель умирает после атаки
                if (!wasAliveBefore && !target.isAlive()) {
                    targetUnits.remove(target);
                }
            }
        }
    }
}