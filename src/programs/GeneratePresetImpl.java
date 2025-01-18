package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

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

        // Счётчики для каждого типа юнитов
        Map<String, Integer> unitCounters = new HashMap<>();

        unitCounters.put("Knight", 0);
        unitCounters.put("Swordsman", 0);
        unitCounters.put("Pikeman", 0);
        unitCounters.put("Archer", 0);

        for (Unit unit : unitList) {
            int unitCost = unit.getCost();
            String unitType = unit.getUnitType();

            // Максимально возможное количество юнитов данного типа
            int maxPossibleUnits = Math.min(maxUnitsPerType, maxPoints / unitCost);

            for (int i = 0; i < maxPossibleUnits; i++) {
                if (totalPoints + unitCost > maxPoints) {
                    // Прекращаем добавление, если превышает максимальный бюджет
                    break;
                }

                // Увеличиваем счётчик данного типа юнитов
                unitCounters.put(unitType, unitCounters.get(unitType) + 1);
                int unitNumber = unitCounters.get(unitType);

                // Вычисляем координаты и имя для нового юнита
                int xCoordinate = getXCoordinate(unitType);
                int yCoordinate = getYCoordinate(unitType, unitNumber - 1);
                String unitName = generateUnitName(unitType, unitNumber);

                // Создаём нового юнита
                Unit newUnit = new Unit(
                        unitName,
                        unitType,
                        unit.getHealth(),
                        unit.getBaseAttack(),
                        unit.getCost(),
                        unit.getAttackType(),
                        unit.getAttackBonuses(),
                        unit.getDefenceBonuses(),
                        xCoordinate,
                        yCoordinate
                );

                // Добавляем юнита в армию
                armyList.add(newUnit);
                totalPoints += unitCost;
            }
        }

        return armyList;
    }

    private String generateUnitName(String unitType, int unitNumber) {
        switch (unitType) {
            case "Knight":
                return "K " + unitNumber;
            case "Swordsman":
                return "S " + unitNumber;
            case "Pikeman":
                return "P " + unitNumber;
            case "Archer":
                return "A " + unitNumber;
            default:
                throw new IllegalArgumentException("Unknown unit type: " + unitType);
        }
    }

    private int getXCoordinate(String unitType) {
        return switch (unitType) {
            case "Knight", "Swordsman" -> 2;
            case "Pikeman" -> 1;
            case "Archer" -> 0;
            default -> throw new IllegalArgumentException("Unknown unit type: " + unitType);
        };
    }

    private int getYCoordinate(String unitType, int unitNumber) {
        return switch (unitType) {
            case "Knight", "Pikeman", "Archer" -> unitNumber * 2;
            case "Swordsman" -> unitNumber * 2 + 1;
            default -> throw new IllegalArgumentException("Unknown unit type: " + unitType);
        };
    }
}