import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.*;

/**
 * This class permits to have the percentage of male by using map and reduce algorithm.
 */
public class Map_reduce_maleandfemale {

    public static String string;

    /**
     * This class extends the Mapper class
     */
    public static class MapMaleFemale extends Mapper<Object, Text, Text, IntWritable> {

        /**
         * This function override the map function which takes in parameters a key and a value (the input
         * reader class cuts datas and sends it to this function).
         * The value represents one person here. It's split into differents pieces to extract the arguments
         * wanted, here the sexe and the nb lines. Then we write in the context the sexe/nb lines with one 1.
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Text sexe = new Text();

            //we split the value to have arguments
            String arr = value.toString();
            String[] tab = arr.split(";");

            //here we knows that sexe is the first argument so we split it
            String[] tab2 = tab[1].split(",");
                for(String string2 : tab2) {
                    sexe.set(string2);
                    context.write(sexe, new IntWritable(1));
                }
                context.write(new Text("lines"), new IntWritable(1));


        }
    }

    private static int result ;
    private static int moy ;
    private static int cpt = 0;

    /**
     * This class extends the Reducer class
     */
    public static class ReduceMaleFemale extends Reducer<Text,IntWritable,Text,IntWritable> {


        /**
         * This function override the reduce function which takes in parameters a key and an Iterable of values.
         * The iterable of values represents all the value for this key, here the key is the sexe/nb lines and
         * the value are the number of person/lines. We sum the number of values, then if it's the  key number of lines
         * we stock it in the variable moy of mother class and if it's the key male we make the percentage
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
                cpt += val.get();
            }
                result = sum;
                context.write(key, new IntWritable(sum));
            //nb of lines
            if(key.toString().contains("a")){
               moy=sum;
            }
            //percentage of male
            if(key.toString().equals("m")){
                int moyenne = result*100;
                moyenne=moyenne/moy;
                context.write(new Text("percent male"), new IntWritable( moyenne));
            }

        }
    }

    /**
     * This is the main function of the class, it run the map and reduce for the percentage of male.
     * This function take in parameters the file path containing datas in first and the directory path
     * for output files in second.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        string = args[0];
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "male percentage");
        job.setJarByClass(Map_reduce_maleandfemale.class);
        job.setMapperClass(Map_reduce_maleandfemale.MapMaleFemale.class);
        job.setCombinerClass(Map_reduce_maleandfemale.ReduceMaleFemale.class);
        job.setReducerClass(Map_reduce_maleandfemale.ReduceMaleFemale.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);

        int moyenne = result /moy *100;

        System.out.println("moyenne homme:"+ moyenne);




    }
}
