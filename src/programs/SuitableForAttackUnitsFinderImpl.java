package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    // Внешний цикл в getSuitableUnits проходит по n рядам.
    // Слжность цикла в методе getSuitableUnits O(n)
    // Внутренний в методе getSuitableUnitsFromRow проходит по m юнитам в каждом ряду
    // Слжность цикла в методе getSuitableUnitsFromRow O(m)
    // Итоговая сложность алгоритма составляет: O(n*m)
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        unitsByRow.forEach(
                row -> {
                    suitableUnits.addAll(getSuitableUnitsFromRow(row, isLeftArmyTarget));
                }
        );

        return suitableUnits;
    }

    private List<Unit> getSuitableUnitsFromRow(List<Unit> row, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();
        boolean foundUnit = false;

        for (int unitIndex = 0; unitIndex < row.size(); unitIndex++) {
            Unit unit = row.get(unitIndex);
            if (unit != null && unit.isAlive()) {
                if (isLeftArmyTarget) {
                    if (!foundUnit) {
                        suitableUnits.add(unit);
                    }
                    foundUnit = true; // Юнит найден, теперь проверяем только правых
                } else {
                    if (unitIndex == row.size() - 1 || row.get(unitIndex + 1) == null) {
                        suitableUnits.add(unit);
                    }
                }
            } else {
                if (isLeftArmyTarget) {
                    foundUnit = false; // Сброс, если юнит мертв или null
                }
            }
        }
        return suitableUnits;
    }
}