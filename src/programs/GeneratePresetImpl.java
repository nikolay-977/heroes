package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class GeneratePresetImpl implements GeneratePreset {

    //  Этап 1: Сортировка юнитов
    //  Для сортировки юнитов используется Stream с Comparator. Сложность сортировки в Java составляет O(nlogn), где n — это размер списка.
    //  Сложность на этом этапе: O(nlogn)
    //  Этап 2: Формирование армии
    //  Этот этап выполняет итерации по отсортированному списку юнитов n. Кроме того, для каждого юнита:
    //  Вычисляется, сколько юнитов данного типа можно добавить через Math.min(maxUnitsPerType, maxPoints, unitCost)
    //  Это константная операция O(1).
    //  Далее выполняется вложенный цикл, который добавляет максимум maxUnitsPerType юнитов одного типа.
    //  Таким образом, внутренний цикл выполняется максимум n * m.
    //  Сложность на этом этапе: O(n*m)
    //  Наиболее затратной операцией здесь является сортировка.
    //  Итоговая сложность алгоритма составляет: O(nlogn)
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        //  Этап 1: Сортировка юнитов
        List<Unit> unitSortedList = sortByAttackAndHealthAnd(unitList);
        //  Этап 2: Формирование армии
        List<Unit> armyList = getArmy(unitSortedList, maxPoints);

        return new Army(armyList);
    }


    private List<Unit> sortByAttackAndHealthAnd(List<Unit> unitList) {

        return unitList.stream()
                .sorted(Comparator
                        // Сравниваем по эффективности атаки (по убыванию)
                        .comparing((Unit u) -> (double) u.getBaseAttack() / u.getCost())
                        .reversed()
                        // Если атака равна, сравниваем по эффективности здоровья (по убыванию)
                        .thenComparing((Unit u) -> (double) u.getHealth() / u.getCost())
                        .reversed()
                        // Если оба критерия равны, сравниваем по стоимости (по возрастанию)
                        .thenComparingInt(Unit::getCost)
                )
                .toList();
    }

    private List<Unit> getArmy(List<Unit> unitList, int maxPoints) {
        List<Unit> armyList = new ArrayList<>();
        int maxUnitsPerType = 11; // Максимальное количество юнитов каждого типа
        int totalPoints = 0;

        for (Unit unit : unitList) {
            int unitCost = unit.getCost();

            // Подсчитываем, сколько юнитов можно добавить данного типа
            int maxPossibleUnits = Math.min(maxUnitsPerType, maxPoints / unitCost);

            for (int i = 0; i < maxPossibleUnits; i++) {
                if (totalPoints + unitCost > maxPoints) {
                    // Если превышается стоимость, прекращаем добавление
                    break;
                }

                int unitNum = i + 1;
                unit.setName(unit.getUnitType() + " " + unitNum);
                armyList.add(unit);
                totalPoints += unitCost;
            }
        }

        return armyList;
    }
}