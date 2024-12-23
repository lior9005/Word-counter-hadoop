import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.*;

public class App {
    public static AWSCredentialsProvider credentialsProvider;
    public static AmazonS3 S3;
    public static AmazonEC2 ec2;
    public static AmazonElasticMapReduce emr;

    public static int numberOfInstances = 4;

    public static void main(String[]args){
        credentialsProvider = new ProfileCredentialsProvider();
        
        System.out.println("[INFO] Connecting to aws");

        emr = AmazonElasticMapReduceClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-1")
                .build();
        
// what is this for?
        System.out.println( "list cluster");
        System.out.println( emr.listClusters());

        HadoopJarStepConfig hadoopJarStep1 = new HadoopJarStepConfig()
                .withJar("s3://MR-bucket/Step1.jar")
                .withArgs("s3://datasets.elasticmapreduce/ngrams/books/20090715/heb-all/3gram/data", "s3://MR-bucket/Step1-output/");

        StepConfig step1Config = new StepConfig()
                .withName("Step1")
                .withHadoopJarStep(hadoopJarStep1)
                .withActionOnFailure("TERMINATE_JOB_FLOW");

        HadoopJarStepConfig hadoopJarStep2 = new HadoopJarStepConfig()
                .withJar("s3://MR-bucket/Step2.jar")
                .withArgs();

        StepConfig step2Config = new StepConfig()
                .withName("Step2")
                .withHadoopJarStep(hadoopJarStep2)
                .withActionOnFailure("TERMINATE_JOB_FLOW");

        HadoopJarStepConfig hadoopJarStep3 = new HadoopJarStepConfig()
                .withJar("s3://MR-bucket/Step3.jar")
                .withArgs();

        StepConfig step3Config = new StepConfig()
                .withName("Step3")
                .withHadoopJarStep(hadoopJarStep3)
                .withActionOnFailure("TERMINATE_JOB_FLOW");

        HadoopJarStepConfig hadoopJarStep4 = new HadoopJarStepConfig()
                .withJar("s3://MR-bucket/Step4.jar")
                .withArgs();

        StepConfig step4Config = new StepConfig()
                .withName("Step4")
                .withHadoopJarStep(hadoopJarStep4)
                .withActionOnFailure("TERMINATE_JOB_FLOW");

        JobFlowInstancesConfig instances = new JobFlowInstancesConfig()
                .withInstanceCount(numberOfInstances)
                .withMasterInstanceType(InstanceType.M4Large.toString())
                .withSlaveInstanceType(InstanceType.M4Large.toString())
                .withHadoopVersion("2.6.0").withEc2KeyName("MR-ec2")
                .withKeepJobFlowAliveWhenNoSteps(false)
                .withPlacement(new PlacementType("us-east-1a"));

        System.out.println("Set steps");

        RunJobFlowRequest runFlowRequest = new RunJobFlowRequest()
                .withName("Map-Reducer-Word-Counter")
                .withInstances(instances)
                .withSteps(step1Config, step2Config, step3Config, step4Config)
                .withLogUri("s3://MR-bucket/logs/")
                .withServiceRole("EMR_DefaultRole")
                .withJobFlowRole("EMR_EC2_DefaultRole");

        RunJobFlowResult runJobFlowResult = emr.runJobFlow(runFlowRequest);
        String jobFlowId = runJobFlowResult.getJobFlowId();
        System.out.println("Ran job flow with id: " + jobFlowId);
    }
}
