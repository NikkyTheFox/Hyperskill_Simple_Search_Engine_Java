package search;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static String[] dataset = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, ArrayList<Integer>> invertedIndex = new HashMap<>();

        initialize(args, invertedIndex);
        int option = -1;
        while (option != 0) {
            option = menu(scanner);
            process(dataset, invertedIndex, scanner, option);
        }
        end(scanner);
    }

    public static void initialize(String[] args, Map<String, ArrayList<Integer>> invertedIndex) {
        parseArgs(args);
        populateInvertedIndex(invertedIndex);
    }

    public static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--data")) {
                try {
                    dataset = new String(Files.readAllBytes(Paths.get(args[++i]))).split("\n");
                } catch (Exception e) {
                    System.out.println("Exception during opening a file caught!");
                }
            }
        }
    }

    public static void populateInvertedIndex(Map<String, ArrayList<Integer>> map){
        for(int index = 0; index < dataset.length; index++) {
            String[] words = dataset[index].split(" ");
            for (String word : words) {
                ArrayList<Integer> arrayList = map.getOrDefault(word.toLowerCase(), new ArrayList<>());
                arrayList.add(index);
                map.put(word.toLowerCase(), arrayList);
            }
        }
    }

    public static ArrayList<Integer> retrieveFromInvertedIndex(Map<String, ArrayList<Integer>> invertedIndex, String query) {
        return invertedIndex.getOrDefault(query.toLowerCase(), new ArrayList<>());
    }

    public static int menu(Scanner scanner) {
        System.out.println("1. Search Information.");
        System.out.println("2. Print all data.");
        System.out.println("0. Exit");
        return Integer.parseInt(scanner.nextLine());
    }

    public static void process(String[] dataset, Map<String, ArrayList<Integer>> invertedIndex, Scanner scanner, int option) {
        switch (option) {
            case 1:
                String searchType = scanner.nextLine();
                search(dataset, invertedIndex, searchType, scanner);
                break;
            case 2:
                printAll(dataset);
                break;
            case 0:
                break;
            default:
                System.out.println("Incorrect option! Try Again.");
                break;
        }
    }

    public static void search(String[] dataset, Map<String, ArrayList<Integer>> invertedIndex, String searchType, Scanner scanner) {
        String searchQuery = scanner.nextLine();
        ArrayList<Integer> results = switch (searchType) {
            case "ALL" -> searchAll(invertedIndex, searchQuery);
            case "ANY" -> searchAny(invertedIndex, searchQuery);
            case "NONE" -> searchNone(dataset, invertedIndex, searchQuery);
            default -> new ArrayList<>();
        };
        printByIndex(dataset, results);
    }

    public static ArrayList<Integer> searchAll(Map<String, ArrayList<Integer>> invertedIndex, String query) {
        ArrayList<ArrayList<Integer>> resultArrays = new ArrayList<>();
        for (String word : query.split(" ")) {
           resultArrays.add(retrieveFromInvertedIndex(invertedIndex, word));
        }

        return findIntersection(resultArrays);
    }

    public static ArrayList<Integer> findIntersection(ArrayList<ArrayList<Integer>> arrayListArrayList) {
        if (arrayListArrayList.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Integer> set1 = new HashSet<>(arrayListArrayList.get(0));
        Set<Integer> intersectionSet = new HashSet<>(set1);
        for (int arrayIndex = 1; arrayIndex < arrayListArrayList.size(); arrayIndex++) {
            Set<Integer> tempSet = new HashSet<>();
            for (Integer result : arrayListArrayList.get(arrayIndex)) {
                if (set1.contains(result)) {
                    tempSet.add(result);
                }
            }
            intersectionSet.retainAll(tempSet);
        }
        return new ArrayList<>(intersectionSet);
    }

    public static ArrayList<Integer> searchAny(Map<String, ArrayList<Integer>> invertedIndex, String query) {
        Set<Integer> results = new HashSet<>();
        for (String word : query.split(" ")) {
            results.addAll(retrieveFromInvertedIndex(invertedIndex, word));
        }
        return new ArrayList<>(results);
    }

    public static ArrayList<Integer> searchNone(String[] dataset, Map<String, ArrayList<Integer>> invertedIndex, String query) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < dataset.length; i++) {
            result.add(i);
        }
        Set<Integer> difference = new HashSet<>(result);
        searchAny(invertedIndex, query).forEach(difference::remove);
        return new ArrayList<>(difference);
    }

    public static void printByIndex(String[] dataset, ArrayList<Integer> arrayList){
        for (Integer index : arrayList) {
            System.out.println(dataset[index]);
        }
    }

    public static void printAll(String[] dataset) {
        for (String entry : dataset) {
            System.out.println(entry);
        }
    }

    public static void end(Scanner scanner) {
        scanner.close();
    }

}