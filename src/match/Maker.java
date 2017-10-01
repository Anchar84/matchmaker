package match;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class Maker {

    private final Set<User> usersQueue = new ConcurrentSkipListSet<>();
    private final Consumer<PossibleGroup> groupConsumer;
    private ScheduledExecutorService scheduled;

    Maker(Consumer<PossibleGroup> groupConsumer) {
        this.groupConsumer = groupConsumer;
    }

    void start() {
        if (scheduled != null) {
            return;
        }
        scheduled = Executors.newScheduledThreadPool(1);
        scheduled.scheduleAtFixedRate(this::makeMatch, 0, 200, TimeUnit.MILLISECONDS);
    }

    void shutdown() {
        if (!scheduled.isShutdown()) {
            scheduled.shutdownNow();
            scheduled = null;
        }
    }

    void addUser(String login, int rank) {
        if (login == null) {
            System.out.println("cannot added user, login not defined");
            return;
        }
        final int MIN_USER_RANK = 1;
        final int MAX_USER_RANK = 30;
        if (rank < MIN_USER_RANK || rank > MAX_USER_RANK) {
            System.out.println("cannot added user: " + login + " rank should be in " + MIN_USER_RANK + ".." + MAX_USER_RANK);
            return;
        }
        User user = new User(login, rank);
        if (!usersQueue.contains(user)) {
            System.out.println("user added: " + user);
            usersQueue.add(new User(login, rank));
        } else {
            System.out.println("user " + user + " already in queue");
        }
    }

    private void makeMatch() {
        final int USER_GROUP_SIZE = 8;
        List<User> usersCopy = new ArrayList<>(usersQueue);
        if (usersCopy.size() < USER_GROUP_SIZE) {
            return;
        }
        if (usersCopy.size() == USER_GROUP_SIZE) {
            PossibleGroup singleGroup = new PossibleGroup(usersCopy);
            groupConsumer.accept(singleGroup);
            usersQueue.removeAll(usersCopy);
            return;
        }
        final List<PossibleGroup> groups = new ArrayList<>();
        final Set<User> usersInGroups = new HashSet<>();
        //если пользователей накопилось слишком много то перебирать возможные выборки по 8 игроков становиться слишком долго
        //поэтому сортируем пользователей по времени начала сражения, берем подмножества по 16 элементов и сочитания
        //вычисляем только на этих подможествах
        usersCopy.sort((a, b) -> (int) GameMath.getBattleStartTime(a, b));
        while (usersCopy.size() >= USER_GROUP_SIZE) {
            int subUsersListSize = usersCopy.size() > 2 * USER_GROUP_SIZE ? 2 * USER_GROUP_SIZE : usersCopy.size();
            // все возможные наборы групп пользователей по
            List<Set<User>> usersSubSets = GameMath.getSubsets(usersCopy.subList(0, subUsersListSize), USER_GROUP_SIZE);
            usersSubSets.forEach(us -> groups.add(new PossibleGroup(us)));
            //сотритуе группы по времени начала сражения начиная с самого ближнего
            groups.sort((g1, g2) -> (int) (g1.getBattleStartTime() - g2.getBattleStartTime()));
            groups.forEach(g -> {
                //фильтруем пересекающиеся группы
                boolean crossingUser = false;
                for (User u : g.getUsers()) {
                    if (usersInGroups.contains(u)) {
                        crossingUser = true;
                        break;
                    }
                }
                if (crossingUser) {
                    return;
                }
                usersInGroups.addAll(g.getUsers());
                groupConsumer.accept(g);
            });
            usersCopy.removeAll(usersInGroups);
        }
        usersQueue.removeAll(usersInGroups);
        if (usersQueue.size() != 0) {
            System.out.println(usersQueue.size() + " users left in queue");
        } else {
            System.out.println("users queue is empty");
        }
    }
}
