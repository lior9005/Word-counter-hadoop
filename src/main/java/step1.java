import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Step1 {

    public static class Step1Mapper extends Mapper<LongWritable, Text, FirstKey, LongWritable> {

    private Set<String> stopWords = new HashSet<>();

    @Override
    protected void setup(Context context) throws IOException {
        // Initialize the Amazon S3 client
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        
        S3Object s3Object = s3Client.getObject("eden-mr-bucket", "heb-stopwords.txt");
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        
        //read the stop words from the file and add them to the HashSet
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim());
            }
        } catch (IOException e) {
            throw new IOException("Error reading stopwords file from S3", e);
        }
    }

//legal pattern - maybe to check if its hebrew?

        @Override
        public void map(LongWritable key, Text line, Context context) throws IOException,  InterruptedException {
            String details[] = line.toString().split("\t");
			String words[] = details[0].split(" ");
			if(words.length != 3)   
                return;
            
            //check if any word in the trigram is a stop word
            if (stopWords.contains(words[0]) || stopWords.contains(words[1]) || stopWords.contains(words[2])) {
                return; 
            }

			LongWritable value = new LongWritable (Long.parseLong(details[2]));	

            //for trigram "a b c":
            //      emit(a,b,c), emit(a,b,*), emit(b,c,*), emit(a,*,*), emit(b,*,*), emit(c,*,*), emit(C_0,*,*), 
			context.write(new FirstKey(words[0],words[1], words[2]), value);
            context.write(new FirstKey(words[0],words[1], "*"), value);
            context.write(new FirstKey(words[1], words[2], "*"), value);
            context.write(new FirstKey(words[0],"*", "*"), value);
            context.write(new FirstKey(words[1],"*", "*"), value);
            context.write(new FirstKey(words[2],"*", "*"), value);
            context.write(new FirstKey("C_0","*", "*"), value);

        }
    }

	public static class Step1Combiner extends Reducer<FirstKey, LongWritable, FirstKey, LongWritable>{
	
		@Override
		public void reduce(FirstKey key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException
		{
			long sum = 0L;
			for(LongWritable v : values){
				sum += v.get();
			}
			context.write(key, new LongWritable(sum));
		}
	}

	public static class Step1Reducer extends Reducer<FirstKey, LongWritable, Text, LongWritable>{
		 
		@Override
		public void reduce(FirstKey key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException
		{
			Long total = 0L;
			for(LongWritable v : values){
				total += v.get();
			}
			if(key.getFirst().toString().equals("C_0")){
				saveC0(total, context);
			}
			else{
				context.write(new Text(key.getFirst().toString() + " " + key.getSecond().toString() + " " +key.getThird().toString()), new LongWritable(total));
            }	
		}

        private void saveC0(Long total, Context context){
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
			String value = String.valueOf(total);
			InputStream stream = new ByteArrayInputStream(value.getBytes());
			ObjectMetadata data = new ObjectMetadata();
			data.setContentLength(value.getBytes().length);
			PutObjectRequest req = new PutObjectRequest("eden-mr-bucket/C_0", "C_0", stream ,data);   
			s3.putObject(req);
		}
	}

    public static class Step1Partitioner extends Partitioner<FirstKey, LongWritable> {
        @Override
        public int getPartition(FirstKey key, LongWritable value, int numPartitions) {
            return key.hashCode() % numPartitions;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("[DEBUG] STEP 1 started!");
        System.out.println(args.length > 0 ? args[0] : "no args");

        Path inputPath =new Path(args[0]);
        Path outputPath =new Path(args[1]);
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Step 1");
        job.setJarByClass(Step1.class);
        job.setMapperClass(Step1Mapper.class);
        job.setCombinerClass(Step1Combiner.class);
        job.setReducerClass(Step1Reducer.class);
        job.setMapOutputKeyClass(FirstKey.class);
        job.setMapOutputValueClass(LongWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


}
