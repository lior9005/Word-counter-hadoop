import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class shortTest {
    public static void main(String[] args) {
        // File name to process
        String fileName = "short_3gram";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            // Read the file line by line
            while ((line = br.readLine()) != null) {
                // Process each line
                processLine(line);
            }
        } catch (IOException e) {
            // Handle file reading exceptions
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private static void processLine(String line) {
        // Split the input line by tab to get details
        String[] details = line.split("\t");

        // Check if the line contains the expected columns
        if (details.length < 3) {
            System.err.println("Invalid line format: " + line);
            return;
        }

        // Split the first column into words
        String[] words = details[0].split(" ");

        // Check if the input is a valid trigram (3 words)
        if (words.length != 3) {
            System.err.println("Invalid trigram: " + line);
            return;
        }

        // Trim spaces from words
        words[0] = words[0].trim();
        words[1] = words[1].trim();
        words[2] = words[2].trim();

        // Print the processed words and details
        System.out.println("Trigram: " + words[0] + " " + words[1] + " " + words[2]);
        System.out.println("Year: " + details[1]);
        System.out.println("Match Count: " + details[2]);

        // Optional: Print additional columns if present
        if (details.length > 3) {
            for (int i = 3; i < details.length; i++) {
                System.out.println("Additional Detail: " + details[i]);
            }
        }

        System.out.println(); // Separate entries with a blank line
    }
}
