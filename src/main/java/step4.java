import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class Step4 {

    public static class Step4Mapper extends Mapper<LongWritable, Text, FourthKey, Text> {

        @Override
        public void map(LongWritable key, Text line, Context context) throws IOException,  InterruptedException {
            String[] pLine = line.toString().split("\t");
			String[] words = pLine[0].split(" ");		
            context.write(new FourthKey(words[0] + " " + words[1] + " " + words[2] + "\t" + pLine[1]), new Text(""));
        }
    }

	public static class Step4Reducer extends Reducer<FourthKey, Text, Text, Text>{
		 
		@Override
		public void reduce(FourthKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException
		{
			String[] pKey = key.getKey().toString().split("\t");
			context.write(new Text(pKey[0]), new Text(pKey[1]));
		}
	}

    public static void main(String[] args) throws Exception {
        System.out.println("[DEBUG] STEP 4 started!");
        System.out.println(args.length > 0 ? args[0] : "no args");

        Path inputPath =new Path("s3://lior-mr/Step3-output/");
        Path outputPath =new Path("s3://lior-mr/Step4-output/");
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Step 4");
        job.setJarByClass(Step4.class);
        job.setMapperClass(Step4Mapper.class);
        job.setReducerClass(Step4Reducer.class);
        job.setMapOutputKeyClass(FourthKey.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(TextInputFormat.class);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
