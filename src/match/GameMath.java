package match;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Вспомогательный класс "игровая математика", содержит статические методы для различных вычислений
 */
final class GameMath {

    private GameMath() {
        //статический хелпер, экземпляры создавать нельзя
    }

    /**
     * @param a Пользователь А
     * @param b Пользователь В
     * @return возвращает минимальное возможное время начала сражения пары игроков
     */
    static long getBattleStartTime(User a, User b) {
        return Math.abs(a.getRank() - b.getRank()) * 2500 + (a.getEntryTime() + b.getEntryTime()) / 2;
    }

    /**
     * @param a Пользователь А
     * @param b Пользователь В
     * @return возвращает true если пара пользователь удовлетворяет условиям начала битры
     */
    static boolean canFigth(User a, User b) {
        return Math.abs(a.getRank() - b.getRank()) <= (2 * System.currentTimeMillis() - a.getEntryTime() - b.getEntryTime()) / 5000;
    }

    /***
     * @param source первоначальное множество
     * @param subSetSize размер искомых подмножеств
     * @param <T>
     * @return возвращает список возможных подмножеств фиксированной длины множества
     */
    static <T> List<Set<T>> getSubsets(List<T> source, int subSetSize) {
        List<Set<T>> subSetsList = new ArrayList<>();
        getSubsets(0, 0, source, new ArrayList<>(source.subList(0, subSetSize)), subSetsList);
        return subSetsList;
    }

    /**
     * Метод участвует в составлении всех возможных комбинаций выбора фиксированного количества элементов из множеста
     *
     * @param pos         позиция, на которой в цикле меняются элемены
     * @param maxUsed
     * @param source      первоначальное множество
     * @param subset      текущее подмножество
     * @param subSetsList аккумулятор возможных комбинаций
     */
    private static <T> void getSubsets(int pos, int maxUsed, List<T> source, List<T> subset, List<Set<T>> subSetsList) {
        if (pos == subset.size()) {
            subSetsList.add(new HashSet<>(subset));
        } else {
            for (int i = maxUsed; i < source.size(); i++) {
                subset.set(pos, source.get(i));
                getSubsets(pos + 1, i + 1, source, subset, subSetsList);
            }
        }
    }
}
