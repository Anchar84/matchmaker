package match;

import java.util.Scanner;

/**
 * Функциональный интерфейс обработчика консольных комманд
 * */
@FunctionalInterface
public interface IConsoleCommand {
    void handle(Scanner input);
}
