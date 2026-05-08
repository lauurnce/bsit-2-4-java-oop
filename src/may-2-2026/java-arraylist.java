import java.util.ArrayList; // Import the ArrayList class from java.util package

public class JavaArrayListExample { // Class name changed to valid Java identifier

    public static void main(String[] args) {
        // Create an ArrayList of Strings
        ArrayList<String> fruits = new ArrayList<>();

        // Add elements to the ArrayList
        fruits.add("Apple"); // Add "Apple" to the list
        fruits.add("Banana"); // Add "Banana" to the list
        fruits.add("Cherry"); // Add "Cherry" to the list

        // Print the ArrayList
        System.out.println("Initial fruits list: " + fruits);

        // Access an element by index
        String firstFruit = fruits.get(0); // Get the first element (index 0)
        System.out.println("First fruit: " + firstFruit);

        // Modify an element
        fruits.set(1, "Blueberry"); // Replace "Banana" with "Blueberry" at index 1
        System.out.println("After modifying index 1: " + fruits);

        // Remove an element by index
        fruits.remove(2); // Remove the element at index 2 ("Cherry")
        System.out.println("After removing index 2: " + fruits);

        // Add more elements
        fruits.add("Date"); // Add "Date" to the end
        fruits.add("Elderberry"); // Add "Elderberry" to the end
        System.out.println("After adding more fruits: " + fruits);

        // Check if the list contains an element
        boolean hasApple = fruits.contains("Apple"); // Check if "Apple" is in the list
        System.out.println("Does the list contain 'Apple'? " + hasApple);

        // Get the size of the ArrayList
        int size = fruits.size(); // Get the number of elements
        System.out.println("Size of the list: " + size);

        // Iterate through the ArrayList using a for-each loop
        System.out.println("Iterating through the list:");
        for (String fruit : fruits) {
            System.out.println(fruit);
        }

        // Clear the ArrayList - removes all elements
        fruits.clear(); // Clear all elements from the list
        System.out.println("After clearing the list: " + fruits);
        System.out.println("Size after clear: " + fruits.size());
    }
}
