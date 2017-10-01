package match;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class Main {

    private static final Map<String, IConsoleCommand> cmdHandlers;
    private static final Maker maker = new Maker(Main::printGroup);
    private static final AtomicInteger UID_GENERATOR = new AtomicInteger(0);
    private static boolean isRun = true;

    private Main() {
        //точка входя для консольного приложения, экземпляры создавать нельзя
    }

    static {
        Map<String, IConsoleCommand> cmds = new HashMap<>();
        cmds.put("exit", (input) -> isRun = false);
        cmds.put("addUser", Main::addUser);
        cmds.put("addManyUsers", Main::addManyUser);
        cmds.put("emulate", Main::emulate);
        cmdHandlers = Collections.unmodifiableMap(cmds);
    }

    public static void main(String[] args) {
        final Scanner input = new Scanner(System.in);
        maker.start();
        while (isRun) {
            String cmd = input.next();
            IConsoleCommand handler = cmdHandlers.get(cmd);
            if (handler == null) {
                System.out.println("unknown command: " + cmd);
            } else {
                handler.handle(input);
            }
        }
        maker.shutdown();
    }

    private static void addUser(Scanner input) {
        String login = input.next();
        int rank = input.nextInt();
        try {
            maker.addUser(login, rank);
        } catch (IllegalArgumentException i) {
            System.out.println("cannot add user " + login + ", " + rank + ": " + i.getMessage());
            i.printStackTrace(System.out);
        }
    }

    private static void addManyUser(Scanner input) {
        int userCount = input.nextInt();
        for (int i = 0; i < userCount; i++) {
            maker.addUser("u" + UID_GENERATOR.incrementAndGet(), (int) (30 * Math.random() + 1));
        }
    }


    private static void emulate(Scanner input) {
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        int count = input.nextInt();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep((int) (2000 * Math.random()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                maker.addUser("u" + UID_GENERATOR.incrementAndGet(), (int) (30 * Math.random() + 1));
            });
        }
    }


    private static void printGroup(PossibleGroup group) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(group.getBattleStartTime()), ZoneId.systemDefault());
        System.out.println("(" + ldt.format(DateTimeFormatter.ISO_DATE_TIME) + " " + group.listUsersLogin() + ")");
    }

}
