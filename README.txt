Ass_2 - Map-Reduce (Hadoop)
Creators : Eden Miran and Lior Sharony
UserNames : mirane@post.bgu.ac.il   sharoli@post.bgu.ac.il
ids: 314868019  316380138

*how to run the project*
    - run the command 'mvn install' to create the jar to execute
    - run the command java -jar target/App.jar to execute the program

*implementation*

We implemented the task using 4 steps:
    - step 1: in charge of accumulating the number of occurences of each triplet, the pairs in the triplets and the single words in it
    
    - step 2: 
        Mapper Phase:
            Mapper Input: The input consists of lines of text, each containing words and an occurrence count.

            The input lines are split into words, and we create keys that represent either a word pair or an individual word:
                For 3-grams, the Mapper generates two pairs: w1w2 and w2w3. This allows the Mapper to efficiently analyze two consecutive word pairs from the triplet.
                It also generates a key for the individual word (w2, w3) based on its position in the triplet.

            Value Structure:
                The value holds three pieces of information:
                    -The words (joined into a single string)
                    -The occurrence count of the words
                    -A tag indicating whether this value is from a triplet ("t") or from a single word pair ("f").
                This design ensures that the Reducer can differentiate between data from triplets and individual word pairs and compute the necessary counts accordingly.
        
        Reducer Phase:

            The Reducer processes the grouped key-value pairs to compute aggregate results (as an array):
                 Input Data Processing:
                    Iterates through values associated with each key.
                    Differentiates between triplet data and individual word pair occurrences.
        Computations:
            Aggregates counts for individual words (C_1, N_1) and word pairs (C_2, N_2).
            Processes each triplet's data to generate final statistics.

    - step 2: 
        Mapper Phase:
            Mapper Input: The input consists of lines of text, each containing words and an occurrence count.

            The input lines are split into words, and we create keys that represent either a word pair or an individual word:
                For 3-grams, the Mapper generates two pairs: w1w2 and w2w3. This allows the Mapper to efficiently analyze two consecutive word pairs from the triplet.
                It also generates a key for the individual word (w2, w3) based on its position in the triplet.

            Value Structure:
                The value holds three pieces of information:
                    -The words (joined into a single string)
                    -The occurrence count of the words
                    -A tag indicating whether this value is from a triplet ("t") or from a single word pair ("f").
                This design ensures that the Reducer can differentiate between data from triplets and individual word pairs and compute the necessary counts accordingly.

         Reducer Phase:   
    Key: Represents an individual word, a word pair, or a triplet.
        Examples:
            w1w2 (word pair)
            w2w3 (word pair)
            w1w2w3 (triplet)

    Values: A list of values associated with the key, which include:
        Counts of occurrences.
        Tags that distinguish the data type - "t" for triplets: Indicates that this value is part of a 3-gram triplet.
                                              "f" for word pairs: Indicates that this value belongs to an individual word pair or a single word.
            
    Aggregation of Counts:
        For Individual Words:
            Counts (C1) represent the number of times a specific word appears in any context.
            These counts are calculated by summing values tagged as "f".
        For Word Pairs:
            Counts (C2) represent how often a specific word pair (e.g., w1w2 or w2w3) appears in the dataset.
            These counts are calculated by summing "f" values.
        For Triplets:
            Counts (C3) represent the number of times a specific triplet (e.g., w1w2w3) occurs.
            These are extracted from "t" tagged values.

    Statistical Computation for Triplets:
        For each triplet (w1w2w3), the Reducer computes:
            Counts of w1, w2, w3 as individual words (C1, N1).
            Counts of word pairs (C2, N2) such as w1w2 and w2w3.
            Counts of the triplet itself (C3).

    Output Emission:
        The Reducer emits:
            The triplet as the key.
            A formatted string as the value, containing:
                Counts for individual words.
                Counts for word pairs.
                Counts for the triplet.

Helper Methods in Reducer

    generateFormulaValues:
        Constructs a formatted output string to encapsulate computed statistics.
        Example:

        Input:
          C1: 50, N1: 200, C2: 30, N2: 100, C3: 10
        Output:
          "C1=50, N1=200, C2=30, N2=100, C3=10"

    StringArrayToWords:
        Combines an array of strings (words) into a single string, ensuring proper formatting for triplets or word pairs.

    filterAsteriks:
        Cleanses input data by removing asterisks (*), ensuring clean keys and values.

Reducer Output Example

For a given triplet w1w2w3 with the following input:

    Individual word counts:
        w1: 50
        w2: 100
        w3: 80
    Word pair counts:
        w1w2: 40
        w2w3: 60
    Triplet count:
        w1w2w3: 20

The Reducer emits:

Key: w1w2w3
Value: "C1=50, N1=100, C2=40, N2=60, C3=20"

Challenges Handled by the Reducer

    Data Disambiguation:
        Differentiates between triplet data and individual word or word pair counts using tags (t, f).

    Aggregation Across Groups:
        Aggregates counts across all values for a given key efficiently.

    Performance Optimization:
        Processes only the required data for each key, avoiding redundant computations.

    Preparation for Further Analysis:
        Outputs detailed statistics for each triplet, making it suitable for subsequent steps or machine learning tasks.

Conclusion

The Reducer step is pivotal in converting raw grouped data into actionable statistical summaries. It ensures accurate tracking of occurrences for individual words, word pairs, and triplets, enabling meaningful insights into the dataset. The logic and helper methods enhance clarity and facilitate scalable processing of large datasets.