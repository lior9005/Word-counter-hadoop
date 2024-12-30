# Map-Reduce (Hadoop) Assignment  
Creators: Eden Miran and Lior Sharony  
Usernames: mirane@post.bgu.ac.il, sharoli@post.bgu.ac.il  
IDs: 314868019, 316380138  

---

## How to Run the Project  
1. Execute the following command to build the project and create the executable JAR file:  
   mvn install

2. Run the project using the following command:  
   java -jar target/App.jar

---

## Implementation Overview  

This project consists of four steps, implemented as separate Map-Reduce jobs. Each step processes data and generates intermediate results for the subsequent step.

### Step 1: Word Counter  
    Purpose: To count the occurrences of:  
        1. Individual words.  
        2. Word pairs.  
        3. Triplets (3-grams).  

    **Output**:  
        Each triplet is represented with placeholders (`*`) for missing words or pairs, providing a complete dataset for downstream processing.

---

### Step 2: Formula Generator  
    **Mapper Phase**:  
        - **Input**:  
            Each line consists of a triplet and its occurrence count.  
        - **Processing**:  
            1. Splits triplets into two pairs (`w1w2` and `w2w3`).  
            2. Generates keys for word pairs and individual words based on the triplet structure.  
            3. Assigns a tag to distinguish values from triplet (`t`) and values from pairs or single (`f`).  

    **Reducer Phase**:  
        - **Processing**:  
               The reducer iterates over all values associated with the key:
            - If the key is a **word pair** (tag `f`), it adds its occurrence count to the corresponding pair counts (`C_1` or `C_2`) and updates the word frequencies (`N_1`, `N_2`).
            - If the key is a **triplet** (tag `t`), it updates the count for the triplet (`N_3`).
            - The values associated with a key can be:
                - For a triplet: The occurrence of that triplet.
                - For a word pair: The occurrence of that word pair.
            finaly itAggregates counts to compute formula parameters (`C_1`, `N_1`, `C_2`, `N_2`, `N_3`).  
        - **Output**:  
            An array of statistics for each key, used for probability calculation in Step 3.

---

### Step 3: Probability Generator  
    **Mapper Phase**:  
        - **Input**:  
            Each line contains a triplet and its associated statistics array.  
        - **Processing**:  
            Converts the input into a `ThirdKey` object, allowing the shuffle and sort phase to group all values for the same key.  

    **Reducer Phase**:  
        - **Processing**:  
            1. Merges the values into a single array for each key, filling in missing spots (`-1`).  
            2. Computes the probability using the formula:
                P = K_3 * N_3 / C_2 + (1 - K_3) * K_2 * N_2 / C_1 + (1 - K_3) * (1 - K_2) * N_1 / C_0
                where K_2 and K_3 are smoothing coefficients.  

        **Output**:  
            A triplet with its calculated probability.

---

### Step 4: Sorter  
    **Purpose**:  
        To sort the triplets by their probabilities.  

    **Mapper Phase**:  
        - Moves the probability value into the key field to ensure sorting is based on probabilities during the shuffle and sort phase.  

    **Reducer Phase**:  
        - Outputs the sorted triplets along with their probabilities.

---

### Report Summary:

    -The complete 3-gram file:
        -number of mappers (instances) : 9 
            -number of key-value pairs from mapper to reducer (format: <number of files, size>)
                with combiner (we used combiner only in step1):
                    -step1: <4454351, 66686815>
                    -step2: <7565970, 155933679>
                    -step3: <6599596, 151390328>
                    -step4: <1650268, 48257631>
                without combiner:
                    -step1: <, >
                    -step2: <, >
                    -step3: <, >
                    -step4: <, >
            -running time : 21 minutes
        
    
