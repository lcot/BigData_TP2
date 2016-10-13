import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * This class permits to count the number of first name by the number of origin by using map and reduce algorithm.
 */
public class Map_reduce_originfirstname {

    /**
     * This class extends the Mapper class
     */
    public static class MapOriginfirstname extends Mapper<Object, Text, Text, IntWritable> {

        /**
         * This function override the map function which takes in parameters a key and a value (the input
         * reader class cuts datas and sends it to this function).
         * The value represents one person here. It's split into differents pieces to extract the arguments
         * wanted, here the number of origin. Then we write in the context the origin with one 1.
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Text origins = new Text();
            int cpt=0;

            //we split the value to have arguments
            String arr = value.toString();
            String[] tab = arr.split(";");
            //here we knows that origins are the second argument so we split it
            String[] tab2 = tab[2].split(",");

            //we count the number of origins
            for(String string : tab2){
                cpt++;
            }

            origins.set("nb origins: "+cpt);
            context.write(origins, new IntWritable(1));
        }
    }

    /**
     * This class extends the Reducer class
     */
    public static class ReduceOriginfirstname extends Reducer<Text,IntWritable,Text,IntWritable>{

        private IntWritable result = new IntWritable();

        /**
         * This function override the reduce function which takes in parameters a key and an Iterable of values.
         * The iterable of values represents all the value for this key, here the key is number of origin and
         * the value are the number of person who have this number of origin. We sum the number of people by origins.
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
     * This is the main function of the class, it run the map and reduce for count people by number of origins.
     * This function take in parameters the file path containing datas in first and the directory path
     * for output files in second.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "by first name by origins");
        job.setJarByClass(Map_reduce_originfirstname.class);
        job.setMapperClass(MapOriginfirstname.class);
        job.setCombinerClass(ReduceOriginfirstname.class);
        job.setReducerClass(ReduceOriginfirstname.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
