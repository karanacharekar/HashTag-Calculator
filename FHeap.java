/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Karan Acharekar
 */

import java.io.*;
import java.util.*;

public class FHeap {

    public FNode maxpointer;

    public FHeap() {

        maxpointer = null;

    }

    /**
     * Inserts new nodes into the Fibonacci Heap
     * Sets the Max pointer if it is null
     * Insert is done to the right of max node
     * @param fn-> Node to be inserted
     */
    
     
    public void insert(FNode fn) {
        if (this.maxpointer == null) {

            maxpointer = fn;     /* If fn is the first node to be inserted */
            maxpointer.childcut = false;
            maxpointer.left = maxpointer;
            maxpointer.right = maxpointer;
            maxpointer.parent = null;

        } else {
            fn.childcut = false;
            fn.right = maxpointer.right;
            fn.left = maxpointer;
            this.maxpointer.right.left = fn;
            this.maxpointer.right = fn;
            fn.parent = null;
        }

        if (this.maxpointer.data < fn.data) {
            this.maxpointer = fn;
        }
    }
    
    
/**
 * Increase key is called if hashtag is already present in Fibonacci Heap
 * Increases the count of the hashtag by the number of reoccurrences
 * After increase if the key becomes greater than the parent key, inserts that node at top level
 * @param fn-> The node whose key is to be incremented
 * @param increment -> count by which the key has to be incremented
 */
    
    public void increaseKey(FNode fn, int increment) {
        fn.data = fn.data + increment;
        /* Increments the key */
        if (fn.parent == null && fn.data > maxpointer.data) {
            maxpointer = fn;
        }
        FNode fnpp = null, fnparent = null;

        if (fn.parent != null) {
            fnparent = fn.parent;    /* fnparent is parent of increased node */

            if (fnparent.parent != null) {
                fnpp = fnparent.parent;    /* fnpp is parent of the parent of increased node */
            }
        }

        if (fnparent != null && fn.data > fnparent.data) {
            cut(fn, fnparent);
            if (fnparent.parent != null) {
                cascadingCut(fnparent, fnpp);
            }
        }

    }
/**
 *  Cascading cut function checks if the childcut of parent of the removed node is True.
 * If True, it removes the node and inserts it at root level
 * If False, sets the childcut to True 
 * @param fnparent - Parent of the node to be cut
 * @param fnpp - Parent of the parent of the node to be cut
 */
    
    public void cascadingCut(FNode fnparent, FNode fnpp) {
        if (fnparent.childcut == false) {
            fnparent.childcut = true;
        } else {
            if (fnparent.parent != null) {
                cut(fnparent, fnpp);
            } else {
                cascadingCut(fnpp, fnpp.parent);
            }
        }

    }
    
/**
 * Cut is simply used to detach the node from its parent and siblings.
 * It then calls upon insert function to insert the node at root level 
 * The arguments passed to the cut function are the node to be removed and its parent
 * @param fn -> Node to be cut
 * @param fnparent -> Parent of the Node to be cut
 */
      
    public void cut(FNode fn, FNode fnparent) {
        FNode exchange;   /* exchange is a temp node used to move around the pointers */
        if (fn.right == fn && fn.left == fn) {
            fn.parent.degree = fn.parent.degree - 1;
            fn.parent.child = null;
            fn.parent = null;
            insert(fn);

        } else {
            exchange = fn.left;
            exchange.right = fn.right;
            fn.right.left = exchange;
            fn.right = null;
            fn.left = null;
            if (fnparent.child != null && fnparent.child == fn) {
                fnparent.child = exchange;
            }
            fn.parent.degree = fn.parent.degree - 1;
            fn.parent.child = null;
            fn.parent = null;
            insert(fn);
        }
    }

    
    /*
     * The removeMax function is used to remove the node with maximum key       
     * If the node with max pointer has no child, just adjust the pointers of its left and right node
     * If the node with max pointer has a child, perform pairwise combine of all nodes at root level except the max pointer. Then perform pairwise combine of the child level nodes
     * @return maxnode -> The node to which the maxpointer points 
     */
  
    
    public FNode removeMax() {
        Hashtable<Integer, FNode> combinedtreehashtable = new Hashtable();   /* Hashtable to keep track of degree of combined trees */
        FNode maxnode = maxpointer;
        FNode exchange = null;

        if (maxpointer == null) {
            throw new RuntimeException("empty heap ");
        }

        if (maxpointer != null) {
            exchange = maxpointer.right;   /* exchnage is used to traverse the tree while doing pairwise combine */
            maxpointer.right = exchange.right;
            exchange.right.left = maxpointer;

            while (exchange != maxpointer) {
                exchange.left = exchange;
                exchange.right = exchange;
                pairwisecombine(exchange, combinedtreehashtable);
                exchange = maxpointer.right;
                maxpointer.right = exchange.right;
                exchange.right.left = maxpointer;
            }
            if (maxpointer.child != null) {
                maxpointer.right = maxpointer.child;
                maxpointer.left = maxpointer.child.left;
                maxpointer.child.left.right = maxpointer;
                maxpointer.child.left = maxpointer;
                maxpointer.child = null;
                exchange = maxpointer.right;
                maxpointer.right = exchange.right;
                exchange.right.left = maxpointer;

                while (exchange != maxpointer) {
                    exchange.right = exchange;
                    exchange.left = exchange;
                    pairwisecombine(exchange, combinedtreehashtable);
                    exchange = maxpointer.right;
                    maxpointer.right = exchange.right;
                    exchange.right.left = maxpointer;
                }

            }
        }

        maxpointer = null;

        /* Iterate over the combined tree hashtable and insert all the nodes at the root level */
        
        Enumeration<Integer> enumKey = combinedtreehashtable.keys();
        while (enumKey.hasMoreElements()) {
            Integer key = enumKey.nextElement();
            FNode val = combinedtreehashtable.get(key);
            combinedtreehashtable.remove(key);
            insert(val);

        }
        maxnode.left = maxnode;
        maxnode.right = maxnode;
        return maxnode;

    }

    /**
     * pairwisecombine combine trees of similar degree and inserts the smaller node below the greater node
     * It take a node as argument. Checks if a tree of same degree exist in combinedtreehashtable
     * If it exist, preforms pairwisecombine recursively
     * If does not exists, just put the tree inside the combinedtreehashtable
     * @param fnchild - > node to be inserted into the combined tree hashtable
     * @param combinedtreehashtable -> hash table to keep track of degree of trees to be combined and to store the trees
     */
    
    
    public void pairwisecombine(FNode fnchild, Hashtable<Integer, FNode> combinedtreehashtable) {
        if (combinedtreehashtable.containsKey(fnchild.degree)) {
            FNode fncth = combinedtreehashtable.get(fnchild.degree);
            combinedtreehashtable.remove(fnchild.degree);
            FNode temp3;

            if (fnchild.data > fncth.data) {
                if (fnchild.child == null) {
                    fnchild.child = fncth;
                    fncth.left = fncth;
                    fncth.right = fncth;
                    fncth.parent = fnchild;
                    fnchild.degree++;
                } else {
                    temp3 = fnchild.child;
                    fncth.left = temp3;
                    fncth.right = temp3.right;
                    temp3.right.left = fncth;
                    temp3.right = fncth;
                    fncth.parent = fnchild;
                    fnchild.degree++;

                }

                if (combinedtreehashtable.containsKey(fnchild.degree)) {

                    pairwisecombine(fnchild, combinedtreehashtable);

                } else {

                    combinedtreehashtable.put(fnchild.degree, fnchild);

                }

            } else {
                if (fncth.child == null) {
                    fncth.child = fnchild;
                    fnchild.left = fnchild;
                    fnchild.right = fnchild;
                    fnchild.parent = fncth;
                    fncth.degree++;
                } else {
                    temp3 = fncth.child;
                    fnchild.left = temp3;
                    fnchild.right = temp3.right;
                    temp3.right.left = fnchild;
                    temp3.right = fnchild;
                    fnchild.parent = fncth;
                    fncth.degree++;
                }

                if (combinedtreehashtable.containsKey(fncth.degree)) {

                    pairwisecombine(fncth, combinedtreehashtable);

                } else {

                    combinedtreehashtable.put(fncth.degree, fncth);

                }

            }

        }
        else {

            combinedtreehashtable.put(fnchild.degree, fnchild);

        }

    }
}
