package lw02.prelab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        InputStream inputStream = null;
        File file = new File("transactions.txt");
        if (file.exists()) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException ignored) {}
        }
        if (inputStream == null) {
            File srcFile = new File("src/lw02/prelab/transactions.txt");
            if (srcFile.exists()) {
                try {
                    inputStream = new FileInputStream(srcFile);
                } catch (FileNotFoundException ignored) {}
            }
        }
        if (inputStream == null) {
            inputStream = Main.class.getResourceAsStream("transactions.txt");
        }
        if (inputStream == null) {
            inputStream = Main.class.getResourceAsStream("/transactions.txt");
        }
        if (inputStream == null) {
            inputStream = Main.class.getClassLoader().getResourceAsStream("transactions.txt");
        }

        if (inputStream == null) {
            System.err.println("Error: File transactions.txt tidak ditemukan!");
            return;
        }

        LinkedList<String[]> transactions = new LinkedList<>();
        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    transactions.add(new String[]{parts[0], parts[1], parts[2]});
                }
            }
        }

        LinkedList<String[]> customers = new LinkedList<>();
        for (String[] tx : transactions) {
            String name = tx[0];
            boolean exists = false;
            for (String[] cust : customers) {
                if (cust[0].equals(name)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                customers.add(new String[]{name, "0"});
            }
        }

        Queue<String[]> transactionQueue = new LinkedList<>();
        for (String[] tx : transactions) {
            transactionQueue.offer(tx);
        }

        Stack<String[]> failedTransactions = new Stack<>();

        while (!transactionQueue.isEmpty()) {
            String[] tx = transactionQueue.poll();
            String name = tx[0];
            String type = tx[1];
            int amount = Integer.parseInt(tx[2]);

            String[] currentCustomer = null;
            for (String[] cust : customers) {
                if (cust[0].equals(name)) {
                    currentCustomer = cust;
                    break;
                }
            }

            if (currentCustomer != null) {
                int currentBalance = Integer.parseInt(currentCustomer[1]);

                if (type.equalsIgnoreCase("DEPOSIT")) {
                    currentBalance += amount;
                    currentCustomer[1] = String.valueOf(currentBalance);
                } else if (type.equalsIgnoreCase("WITHDRAW")) {
                    if (amount > currentBalance) {
                        failedTransactions.push(tx);
                    } else {
                        currentBalance -= amount;
                        currentCustomer[1] = String.valueOf(currentBalance);
                    }
                }
            }
        }

        System.out.println("=== Final Balances ===");
        for (String[] cust : customers) {
            System.out.println(cust[0] + " : " + cust[1]);
        }

        System.out.println();

        System.out.println("=== Failed Transactions ===");
        while (!failedTransactions.isEmpty()) {
            String[] tx = failedTransactions.pop();
            System.out.println(tx[0] + " " + tx[1] + " " + tx[2]);
        }
    }
}
