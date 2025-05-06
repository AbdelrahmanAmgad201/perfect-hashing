package org.example.CommandInterface;

import org.example.maps.LinearHasher;
import org.example.maps.MapInterface;
import org.example.maps.SquareHasher;

import java.util.Scanner;

public class IntitalizeMenu implements State{
    Scanner scanner;
    State nextState;

    public IntitalizeMenu(Scanner scanner) {
        this.scanner = scanner;
        this.nextState = null;
    }

    @Override
    public void excute() {
        String errorMsg = "";
        while (true) {
            System.out.print("\033[H\033[2J");
            if (!errorMsg.isEmpty())
                System.out.println(ConsoleColors.RED + errorMsg + ConsoleColors.RESET);

            System.out.println(ConsoleColors.BLUE + "Initialize Map Type" + ConsoleColors.RESET);
            System.out.println("Choose Map type:");
            printMenu();

            String input = scanner.nextLine().trim().replaceAll("\\s+", "");
            if (input.equalsIgnoreCase("exit")){
                System.out.println("Exiting");
                System.exit(0);
            }

            MapInterface map = null;
            switch (input) {
                case "1":
                    map = new SquareHasher();

                case "2":
                    if (map == null) map = new LinearHasher();
                    nextState = new OperationMenu(scanner, map);
                    return;

                default:
                    errorMsg = "Invalid Input";
            }

        }
    }

    @Override
    public State nextState() {
        return nextState;
    }

    private void printMenu() {
        System.out.println("1- Square Map");
        System.out.println("2- Linear Map");
    }
}
