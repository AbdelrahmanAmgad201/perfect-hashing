package org.example;

import org.example.CommandInterface.IntitalizeMenu;
import org.example.CommandInterface.State;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        State state = new IntitalizeMenu(scanner);
        while (state != null) {
            state.excute();
            state = state.nextState();
        }
        System.out.println("Error: entered null state");
    }
}