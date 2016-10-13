import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * This class permits to count the number of first name by origin by using map and reduce algorithm.
 */
public class Map_reduce_origin {

    /**
     * This class extends the Mapper class
     */
    public static class MapOrigin extends Mapper<Object, Text, Text, IntWritable>{

        /**
         * This function override the map function which takes in parameters a key and a value (the input
         * reader class cuts datas and sends it to this function).
         * The value represents one person here. It's split into differents pieces to extract the arguments
         * wanted, here the origin. Then we write in the context the origin with one 1.
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Text origin ;
            //we split the value to have the different attributes
            String arr = value.toString();
            String[] tab = arr.split(";");
            //the origin is the secong arguments, we split to have the several origins
            String[] tab2 = tab[2].split(",");

            //for each origins we write it in the context with a 1
            for(String string : tab2){
                origin = new Text(string);
                context.write(origin, new IntWritable(1));
            }

        }
    }

    /**
     * This class extends the Reducer class
     */
    public static class ReduceOrigin extends Reducer <Text,IntWritable,Text,IntWritable>{

        /**
         * This function override the reduce function which takes in parameters a key and an Iterable of values.
         * The iterable of values represents all the value for this key, here the key is origin and the vlue are the
         * number of person who have this origin. We sum the number of people by origins.
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;

            //for each val we sum them
            for (IntWritable val : values) {
                sum += val.get();
            }

            //we write the result in context
            context.write(key, new IntWritable(sum));
        }
    }

    /**
     * This is the main function of the class, it run the map and reduce for count people by origins.
     * This function take in parameters the file path containing datas in first and the directory path
     * for output files in second.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "first name by origin");
        job.setJarByClass(Map_reduce_origin.class);
        job.setMapperClass(MapOrigin.class);
        job.setCombinerClass(ReduceOrigin.class);
        job.setReducerClass(ReduceOrigin.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
