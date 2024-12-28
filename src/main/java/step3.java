import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;



import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.regions.Regions;


import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class Step3 {
    public static class Step3Mapper extends Mapper<LongWritable, Text, ThirdKey, Text> {
        @Override
        public void map(LongWritable key, Text line, Context context) throws IOException, InterruptedException {
            String[] parsedLine = line.toString().split("\t");
            String[] words = parsedLine[0].split(" ");
            context.write(new ThirdKey(words[0], words[1], words[2]), new Text(parsedLine[1]));
        }
    }

    public static class Step3Reducer extends Reducer<ThirdKey, Text, Text, DoubleWritable> {
        Long C_0;
		public void setup(Context output) throws IOException {
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
			S3Object s3Object = s3.getObject(new GetObjectRequest("lior-mr/C_0", "C_0"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
			C_0 = Long.parseLong(reader.readLine());
		}

        @Override
        public void reduce(ThirdKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Double[] formulaParams = new Double[5];
            for(Text Textvalues : values){
                String[] input = Textvalues.toString().split(" ");
                for(int i = 0; i < 5;i++){
                    double num = Double.parseDouble((input[i]));
                    if(num!=-1){
                        formulaParams[i]=num;
                    }
                }
            }
            context.write(new Text(key.toString()), wordProbality(formulaParams));
        }

        private DoubleWritable wordProbality(Double[] formulaParams){ // C_1, N_1, C_2, N_2, N_3
            Double C_1 = formulaParams[0];
            Double N_1 = formulaParams[1];
            Double C_2 =  formulaParams[2]; 
            Double N_2 = formulaParams[3];
            Double N_3 = formulaParams[4];
            double K_2 = (Math.log(N_2 + 1) + 1)/(Math.log(N_2 + 2));
            double K_3 = (Math.log(N_3 + 1) + 1)/(Math.log(N_3 + 2));
            return new DoubleWritable(K_3*N_3/C_2 + (1-K_3)*K_2*N_2/C_1 + (1-K_3)*(1-K_2)*N_1/C_0);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        Job job = Job.getInstance(conf, "Step3");
        job.setJarByClass(Step3.class);
        job.setMapperClass(Step3Mapper.class);
        job.setReducerClass(Step3Reducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setMapOutputKeyClass(ThirdKey.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path("s3://lior-mr/Step2-output/"));
        FileOutputFormat.setOutputPath(job, new Path("s3://lior-mr/Step3-output/"));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
