/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;

/**
 *
 * @author Karan Acharekar
 */
public class hashtagcounter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File file1 = new File(args[0]);   
        File file2 = new File("output.txt");

        BufferedReader br = new BufferedReader(new FileReader(file1));  
        BufferedWriter bw = new BufferedWriter(new FileWriter(file2));
        
        try {
            String pattern = "";
            boolean alreadypresent; 
            String[] parts = new String[3];
            String line = "";
            String hash = "";
            int count = 0, pops = 0;
            boolean flag = true;
            FHeap fib = new FHeap();
            Hashtable<String, FNode> hashtags = new Hashtable<String, FNode>();   /* Stores all the hashtags */

            while (!(line = br.readLine()).equals("") && flag == true)
            {
                String output = "";
                if (!line.contains("#") && line.toLowerCase().contains("stop"))  /* Check if reached the end of file */
                {  
                    pattern = "STOP";
                } else if (line.startsWith("#")) /* Check if it is a hashtag */
                {
                    parts = line.split("\\s+");
                    hash = parts[0];
                    count = Integer.parseInt(parts[1]);
                    pattern = "hashtag";
                } else 
                {
                    pops = Integer.parseInt(line.trim());
                    pattern = "query";
                }

                switch (pattern) /* Switches between opeartions for hashtag, or query or a STOP */ 
                {
                    case "STOP":
                        flag = false;
                        break;

                    case "hashtag":
                        
                        alreadypresent = hashtags.containsKey(hash); /* Check if the hashtag is already in the hashtable */
                   
                        if (alreadypresent == true) /* Checks if hashtag is already present in hashtable */
                        {
                            fib.increaseKey(hashtags.get(hash), count);
                        } else   /* If not present, insert in hashtable as well as in FibonacciHeap */
                        {
                            FNode fn = new FNode(count, hash);
                            hashtags.put(hash, fn);
                            fib.insert(fn);
                        }
                        break;

                    case "query":
                        FNode[] maxnodes = new FNode[pops];

                        for (int i = 0; i < pops; i++) {
                            maxnodes[i] = fib.removeMax();
                            if (i == (pops - 1)) {
                                output = output + maxnodes[i].hashname.substring(1);
                            } else {
                                output = output + maxnodes[i].hashname.substring(1) + ",";
                            }                      
                        }
                        
                        bw.write(output);
                        bw.newLine();
                        for (int i = 0; i < pops; i++) {
                            fib.insert(maxnodes[i]);
                        }
                        break;
                }
                if(!flag){
                    break;
                }
            }
        } 
         
         
        catch (IOException e) {
            System.out.println("File not found");
        } 
        catch(Exception e){
            System.out.println("Error exception :" +e.getMessage());
        }
        finally {
            
            br.close();
            bw.flush();
            bw.close();

        }
    }
}
