public class JavaArrayExample {

    public static void main(String[] args) {
        // Arrays in Java are used to store multiple values of the same type in a single variable.
        // They are fixed-size and can hold primitives or objects.

        // 1. Declaring an array
        // Syntax: dataType[] arrayName;
        int[] numbers; // Declares an array of integers

        // 2. Initializing an array
        // You can initialize with a specific size or with values
        numbers = new int[5]; // Creates an array with 5 elements, all initialized to 0

        // Or initialize with values directly
        int[] scores = {85, 90, 78, 92, 88}; // Array with initial values

        // 3. Accessing array elements
        // Arrays are zero-indexed, meaning the first element is at index 0
        System.out.println("First score: " + scores[0]); // Output: 85
        System.out.println("Third score: " + scores[2]); // Output: 78

        // 4. Modifying array elements
        scores[1] = 95; // Change the second element to 95
        System.out.println("Updated second score: " + scores[1]); // Output: 95

        // 5. Looping through an array
        // Use a for loop to iterate through all elements
        System.out.println("All scores:");
        for (int i = 0; i < scores.length; i++) {
            System.out.println("Score " + (i + 1) + ": " + scores[i]);
        }

        // You can also use an enhanced for loop (for-each)
        System.out.println("Scores using for-each loop:");
        for (int score : scores) {
            System.out.println(score);
        }

        // 6. Multidimensional arrays
        // Arrays can have multiple dimensions, like a table
        int[][] matrix = new int[3][3]; // 3x3 matrix

        // Initialize a 2D array with values
        int[][] grid = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        // Accessing elements in 2D array
        System.out.println("Element at [1][2]: " + grid[1][2]); // Output: 6

        // Looping through a 2D array
        System.out.println("2D Array elements:");
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println(); // New line after each row
        }

        // 7. Array length
        // Use .length to get the size of the array
        System.out.println("Number of scores: " + scores.length); // Output: 5
        System.out.println("Number of rows in grid: " + grid.length); // Output: 3
        System.out.println("Number of columns in first row: " + grid[0].length); // Output: 3

        // Note: Arrays in Java are objects, and their length is fixed once created.
        // For dynamic sizing, consider using ArrayList from java.util package.
    }
}