import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class FileManager {
    private static Scanner scanner = new Scanner(System.in);

    private static String enterPath() {
        System.out.println("Enter path: ");
        return scanner.next();
    }

    private static void startThreads(File fileFrom, File fileWhere) throws FileNotFoundException {
        boolean a = true;
        while (a) {
            System.out.println("Enter count of threads: ");
            int threadCounter = scanner.nextInt();
            if (threadCounter < 2) {
                System.out.println("Count of threads must be more than 1!");
            } else {
                System.out.println("Enter size of buffer: ");
                byte[] buffer = new byte[scanner.nextInt()];
                ExecutorService executorService = Executors.newFixedThreadPool(threadCounter);
                List<Copier> tasks = new ArrayList<>();
                for (int i = 1; i < threadCounter; i++) {
                    tasks.add(new Copier(buffer, fileFrom, fileWhere));
                }
                for (Copier copier : tasks) {
                    executorService.submit(copier);
                }
                executorService.shutdown();
                a = false;
            }
        }
    }

    private static void processFiles(File folderFrom, File folderwhere) throws FileNotFoundException {
        File[] folderEntries = folderFrom.listFiles();
        System.out.println("Enter size of buffer: ");
        byte[] buffer = new byte[scanner.nextInt()];
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (File file : folderEntries) {
            if (file.isDirectory()) {
                processFiles(file, folderwhere);
                continue;
            }
            for (int i = 0; i < 5; i++) {
                executorService.submit(new Copier(buffer, file, folderwhere));
            }
        }
        executorService.shutdown();
    }


    public static void main(String[] args) throws IOException, InterruptedException { 
        String pathFrom;
        File fileFrom = null;
        boolean bool = true;
        while (bool) {
            pathFrom = enterPath();
            fileFrom = new File(pathFrom);
            if (fileFrom.exists()) {
                bool = false;
            } else {
                System.out.println("This file doesn`t exist. Try again (Enter - '1'):");
                if (!(scanner.next().compareTo("1") == 0)) {
                    System.exit(0);
                }
            }
        }
        System.out.println("Enter path where you want to copy: ");
        File fileWhere = new File(enterPath());
        double timeOut = System.currentTimeMillis() / 1000;
        startThreads(fileFrom, fileWhere);
        timeOut = (double) (System.currentTimeMillis() / 1000) - timeOut;
        System.out.print("Processing... Time for comleting the process in seconds: ");
        while (timeOut > 0) {
            System.out.print((int) timeOut + " ");
            timeOut--;
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println();
        System.out.println("Success...");
        //Additional Task
        System.out.println("Enter path directory from where you want to copy: ");
        File directoryFrom = new File(enterPath());
        System.out.println("Enter path directory where you want to copy: ");
        File directoryWhere = new File(enterPath());
        directoryWhere.mkdirs();
        if (directoryFrom.isDirectory()) {
            processFiles(directoryFrom, directoryWhere);
        } else {
            System.out.println("Not success, you may enter path of directory");
            System.exit(0);
        }
        System.out.println("Success...");
    }
}
