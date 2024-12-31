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
    Purpose: To filter out stop words and non hebrew words and to count the occurrences of:  
        1. Individual words.  
        2. Word pairs.  
        3. Triplets (3-grams).
        4. total words (C_0)  

    **Output**:  
        Each triplet is represented with placeholders (`*`) for missing words or pairs.

---

### Step 2: Formula Generator  
    Mapper Phase:  
        - Input:  
            Each line represents a triplet and its occurrence count.
        - Processing:  
            For a triplet containing three words:
                1. Split the triplet into two word pairs (w1w2 and w2w3)
                2. Generate keys for each word pair and the individual words within the triplet structure.  
                3. Assign a tag (t) to distinguish values derived from the triplet. 
            For word pairs or single words:
                1. Generate a key consisting of the words, with a value that includes the occurrence count and the tag ('f').

    Reducer Phase:  
        - Processing: 
         1. Iterate through all values associated with a key.
         2. Store values tagged with t (triplet-related values) in a list.
         3. Save the value tagged with f (frequency) as the total occurrence count in the corpus.
         4. Traverse the t-tagged list, and for each value, emit the triplet as the key. The output value is a 5-element array [C_1, N_1, C_2, N_2, N_3], where:
            - Known variables are populated.
            - Unknown variables are set to -1.

        - Output:
         1. Key: Triplet.
         2. Value: A 5-element array containing statistics where two elements are populated and three are set to -1, to be used for probability calculations in the next step.

---

### Step 3: Probability Generator  
    **Mapper Phase**:  
        - **Input**:  
            Each line contains a triplet and its associated statistics array.  
        - **Processing**:  
            Converts the input into a `ThirdKey` object, allowing the shuffle & sort phase to group all values for the same key.  

    **Reducer Phase**:  
        - **Processing**:  
            1. Merges the values into a single array for each key, filling in missing spots (marked as -1).
            2. Computes the probability using the formula:
                P = K_3 * N_3 / C_2 + (1 - K_3) * K_2 * N_2 / C_1 + (1 - K_3) * (1 - K_2) * N_1 / C_0

        **Output**:  
            A triplet (key) with its calculated probability (value).

---

### Step 4: Sorter  
    **Purpose**:  
        To sort the triplets by their probabilities.  

    **Mapper Phase**:  
        - Converts the key and value together into a 'fourthKey' object, allowing shuffle & sort according to lexicographical order and probability

    **Reducer Phase**:  
        - Outputs the sorted triplets along with their probabilities.

---

### Report Summary:

    **'interesting' word pairs:**

        אב אחד לכולנו	0.14730065985246524
        אב אחד לכלנו	0.07312266095546321
        אב אחד אנחנו	0.05113289644301842
        אב אחד ואם  	0.02112529937939331
        אב אחד הם   	0.01577495494682486

        אדם צריך להיות 	0.12382017602283545
        אדם צריך לדעת	0.03122362077389886  
        אדם צריך לשאול	0.02320229332089193
        אדם צריך לעשות	0.02113728033668056
        אדם צריך לומר	0.01741033914706503
 
        ברור לי מה  	0.03230635575709762
        ברור לי אם  	0.03143763079518878 
        ברור לי שלא 	0.02089976050312739 
        ברור לי מדוע	0.01698226963599470
        ברור לי שאני	0.01487499393024070

        ברח מן הבית 	0.15740059245279922 
        ברח מן העיר 	0.12504079505891286
        ברח מן הארץ 	0.11509529885629767
        ברח מן הכבוד   	0.1105999218486997
        ברח מן השררה	0.07528083010405548

        דברים אלו לא	0.03397547567122092
        דברים אלו הם 	0.03210931690132967
        דברים אלו של	0.02773177328447581
        דברים אלו אינם	0.02100191715961154
        דברים אלו על	0.02035128377448870

        0.09768279677229123  הוא צריך להיות 
        הוא צריך היה   	0.02174269395508723
        הוא צריך לעשות	0.02171000212969553     
        הוא צריך לדעת	0.01697800677917734
        הוא צריך לומר	0.01569317970513095

        לא ברור אם  	0.15961259613131373 
        לא ברור לי  	0.07335531396495346
        לא ברור מה  	0.05945424387494013
        לא ברור מדוע	0.03251229310699134
        לא ברור לנו 	0.02009397395102635

        לא כדאי לך    	0.05864219603299994
        לא כדאי היה 	0.05837084564845145        
        לא כדאי לו  	0.04264688330733394
        לא כדאי לי  	0.02845598735470633
        לא כדאי לדבר	0.02399739357391885

        מפני מה לא  	0.12710182507224801
        מפני מה אין 	0.05281120733852435
        מפני מה אתה 	0.04676716589836454
        מפני מה אמרו	0.02339387126162612
        מפני מה אמרה	0.02092687754366423

        רוח של קדושה	0.02168312788081943
        רוח של שמחה 	0.02068290415180256 
        רוח של יאוש 	0.02064942205372346
        רוח של חיים 	0.01551877697268899
        רוח של אמונה	0.01184783166513206
---

    **The complete 3-gram file:**
        ** -number of mappers (instances) : 9 **
            -number of key-value pairs from mapper to reducer (format: <number of recoreds, size>)
                with combiner (we used combiner only in step1):
                    -step1: <4454351, 66686815>
                    -step2: <7565970, 155933679>
                    -step3: <6599596, 151390328>
                    -step4: <1650268, 48257631>
                without combiner:
                    -step1: <486084221, 1301341413>
                    -step2: <7565970, 145371426>
                    -step3: <6599596, 141176392>
                    -step4: <1650268, 47054118>
            -running time : 21 minutes

        ** -number of mappers : 5 **   
            running time : 44 minutes

    **partial file:**  **************to complete*********
        
    
