import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

public class Step2{
    public static class Step2Mapper extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        public void map(LongWritable key, Text line, Context context) throws IOException, InterruptedException {
			String[] parsedLine = line.toString().split("\t");
            Long occNum = Long.parseLong(parsedLine[1]);
            String[] words = filterAsteriks(parsedLine[0].split(" ")); //filter asteriks

            //context format: <key, value, identifier>
            if(words.length == 3){
                String w1w2 = words[0] + " " + words[1];
                String w2w3 = words[1] + " " + words[2];
                Text value = valueGenerator(words, occNum, true);
                context.write(new Text(w1w2), value); //first pair
                if(!w1w2.equals(w2w3)){ //check if the pairs are different
                    context.write(new Text(w2w3), value); //second pair
                }

                //writing w2,w3
                context.write(new Text(words[1]), value);
                if(!words[1].equals(words[2])){
                    context.write(new Text(words[2]), value);
                }                
            }
            else{
                
                Text value = valueGenerator(words, occNum, false);
                context.write(new Text(StringArrayToWords(words)), value);
            }
        }

        private Text valueGenerator(String[] words, Long occNum, boolean tripletOcc){
            String identifier = tripletOcc ? "t" : "f";
            return new Text(StringArrayToWords(words) + "\t" + occNum.toString() + "\t" + identifier);
        }

        private String StringArrayToWords(String[] words){
            String res = "";
            for (String word : words){
                res += word + " ";
            }
            return res.trim();
        }

        private String[] filterAsteriks (String[] words) {
            List<String> filteredWords = new ArrayList<String>();
            for (String word : words) {
                if (!word.equals("*")) {
                    filteredWords.add(word);
                }
            }
            return filteredWords.toArray(new String[0]);
        }
    }

    public static class Step2Partitioner extends Partitioner<Text, Text> {
        @Override
        public int getPartition(Text key, Text value, int numPartitions) {
            return Math.abs(key.toString().hashCode()) % numPartitions;
        }
    }

    public static class Step2Reducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Long occNum = 0L;
            List<String[]> triplets = new ArrayList<>();
            String[] parsedKey = key.toString().split(" ");

            for(Text value : values){
                String[] parsedValue = value.toString().split("\t");
                if(parsedValue[2].equals("t")){
                    triplets.add(parsedValue);
                }
                else{
                    occNum = Long.parseLong(parsedValue[1]);
                }
            }

            for(String[] triplet : triplets){
                Long[] C_1N_1 = new Long[2];
				Long[] C_2N_2 = new Long[2];
                Long tripletOcc = Long.parseLong(triplet[1]);
                String[] tripletWords = triplet[0].split(" ");
                if (parsedKey.length == 1) { //one word
                    if (tripletWords[1].equals(parsedKey[0])) {
                        C_1N_1[0] = occNum; //w2
                    }
                    if (tripletWords[2].equals(parsedKey[0])) {
                        C_1N_1[1] = occNum; //w3
                    }
                }
                else { //two words
                    if (tripletWords[0].equals(parsedKey[0]) && tripletWords[1].equals(parsedKey[1])) {
                        C_2N_2[0] = occNum; //w1w2
                    }
                    if (tripletWords[1].equals(parsedKey[0]) && tripletWords[2].equals(parsedKey[1])) {
                        C_2N_2[1] = occNum; //w2w3
                    }
                }
                Text formulaValues = generateFormulaValues(C_1N_1, C_2N_2, tripletOcc);
                Text contextKey = new Text(tripletWords[0] + " " + tripletWords[1] + " " + tripletWords[2]);
                context.write(contextKey, formulaValues);
            }
        
        }

        private Text generateFormulaValues(Long[] C_1N_1, Long[] C_2N_2, Long tripletOcc){
            Long[] mergedArray = new Long[5]; // Resulting array of size 5
            mergedArray[0] = C_1N_1[0];
            mergedArray[1] = C_1N_1[1];
            mergedArray[2] = C_2N_2[0];
            mergedArray[3] = C_2N_2[1];
            mergedArray[4] = tripletOcc;
            return new Text(Arrays.stream(mergedArray)
                            .map(String::valueOf)
                            .reduce("", (a, b) -> a + " " + b));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf, "Step2");
        job.setJarByClass(Step2.class);
        job.setMapperClass(Step2Mapper.class);
        job.setPartitionerClass(Step2Partitioner.class);
        job.setReducerClass(Step2Reducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path("s3://lior-mr/Step1-output/"));
        FileOutputFormat.setOutputPath(job, new Path("s3://lior-mr/Step2-output/"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
