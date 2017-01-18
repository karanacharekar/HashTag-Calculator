/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Karan Acharekar
 */
public class FNode {
    
    FNode left;
    FNode right;
    FNode parent;
    FNode child;
    
    boolean childcut;
    int data;   /* count of the hashtag */
    int degree;  /* degree of the node */
    String hashname;


public FNode(int key, String hashtag)
{   
this.childcut = false;
this.data = key;
degree = 0;
hashname = hashtag; 
}
}
    

