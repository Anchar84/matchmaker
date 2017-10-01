package match;

import java.util.*;

/**
 * Возможная группа
 */
final class PossibleGroup {
    private final List<User> users;

    private Long battleStartTime;

    public PossibleGroup(Collection<User> users) {
        this.users = Collections.unmodifiableList(new ArrayList<>(users));
    }

    /**
     * @return множесто пользователей
     */
    List<User> getUsers() {
        return users;
    }

    /**
     * @return время начала битвы
     */
    long getBattleStartTime() {
        //LazyLoad для определения времени начала битвы данной группы пользователей
        if (battleStartTime == null) {
            battleStartTime = 0L;
            //Минимальное время начала сражения равно максимальному времени в парах
            //Ищем во всех возможных парах максимально время
            for (int i = 0; i < users.size(); i++) {
                for (int j = 0; j < users.size(); j++) {
                    long battleTime = GameMath.getBattleStartTime(users.get(i), users.get(j));
                    if (battleTime > battleStartTime) {
                        battleStartTime = battleTime;
                    }
                }
            }
        }
        return battleStartTime;
    }

    String listUsersLogin() {
        final StringBuilder usersLogin = new StringBuilder();
        users.stream().forEach(u -> usersLogin.append(u.getLogin()).append(" "));
        return usersLogin.toString().substring(0, usersLogin.length() - 1);
    }
}

