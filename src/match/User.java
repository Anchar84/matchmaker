package match;

/**
 * POJO пользователь
 */
final class User implements Comparable {
    private final String login;
    private final int rank;
    private final long entryTime = System.currentTimeMillis();

    User(String login, int rank) {
        this.login = login;
        this.rank = rank;
    }

    String getLogin() {
        return login;
    }

    int getRank() {
        return rank;
    }

    long getEntryTime() {
        return entryTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", rank=" + rank +
                ", entryTime=" + entryTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return login.equals(user.login);
    }

    @Override
    public int hashCode() {
        return login != null ? login.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return 1;

        User user = (User) o;
        return login.compareTo(user.login);
    }
}
