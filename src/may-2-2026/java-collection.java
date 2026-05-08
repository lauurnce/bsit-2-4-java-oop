import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class JavaCollectionDemo {
    public static void main(String[] args) {
        System.out.println("=== Java Collections Demo (List, Set, Map) ===\n");

        // 1) LIST: ordered, allows duplicates (commonly ArrayList)
        List<String> fruits = new ArrayList<>();
        fruits.add("Apples");
        fruits.add("Banana");
        fruits.add("Apple"); // duplicate is allowed
        fruits.add("Mango");

        System.out.println("LIST (ArrayList) - ordered, duplicates allowed");
        System.out.println("fruits: " + fruits);
        System.out.println("first item (index 0): " + fruits.get(0));
        System.out.println("contains 'Banana'? " + fruits.contains("Banana"));

        fruits.remove("Apple"); // removes first matching value
        System.out.println("after remove(\"Apple\"): " + fruits);

        Collections.sort(fruits); // sorts alphabetically
        System.out.println("after sort: " + fruits);

        System.out.println("iterate with for-each:");
        for (String fruit : fruits) {
            System.out.println("- " + fruit);
        }
        System.out.println();

        // 2) SET: unique values (no duplicates), no guaranteed order (commonly HashSet)
        Set<Integer> luckyNumbers = new HashSet<>(Arrays.asList(7, 13, 7, 21, 13, 5));

        System.out.println("SET (HashSet) - unique values, no guaranteed order");
        System.out.println("luckyNumbers: " + luckyNumbers + "  (duplicates removed automatically)");
        System.out.println("contains 21? " + luckyNumbers.contains(21));
        luckyNumbers.remove(13);
        System.out.println("after remove(13): " + luckyNumbers);
        System.out.println();

        // 3) MAP: key-value pairs, keys must be unique (commonly HashMap)
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Ana", 95);
        scores.put("Ben", 88);
        scores.put("Cara", 91);
        scores.put("Ben", 90); // overwrites value for existing key "Ben"

        System.out.println("MAP (HashMap) - key/value pairs, unique keys");
        System.out.println("scores: " + scores);
        System.out.println("Ben's score: " + scores.get("Ben"));
        System.out.println("has key 'Ana'? " + scores.containsKey("Ana"));
        System.out.println("has value 100? " + scores.containsValue(100));

        System.out.println("iterate over entries (key + value):");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println("- " + entry.getKey() + " => " + entry.getValue());
        }

        // Example: update a value safely
        scores.put("Ana", scores.get("Ana") + 1);
        System.out.println("\nafter giving Ana +1: " + scores);
    }
}
