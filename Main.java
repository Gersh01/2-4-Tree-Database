/*******************************************************************
* Copyright         : 2024 Alexander Gershfeld
* File Name         : Main.java
* Description       : Implementing a 2-4 Tree database with menu functionality
*                    
* Revision History  :
* Date		    Author 			Comments
* ------------------------------------------------------------------
* 02/05/2024    Alex	        Created - Set up structure
* 02/27/2024    Alex            Finished insert method
* 03/25/2024    Alex            Finished getNode method
* 03/27/2024    Alex            Finished getLeafNode methods, and set up structure for deletion
*
/******************************************************************/
//TO-DO:
/*
 * Requirements for putting into production
 *      Delete functionality
 *          - Deleting a value from an internal node
 *          - Deleting leaf node values
 *      Add a menu
 *          - Insert
 *          - Delete
 *          - Find
 * 
 * Personal Tweaks
 *      Compatability with different data types
 *          - Double
 *          - Float
 *          - Long Int
 *          - Strings
 *          - Characters
 *          - Objects
 *      Once compatible make the class generic and add an implementation
 */

import java.awt.*;
import java.util.ArrayList;

public class Main extends Canvas {
    public static Tree twoFourTree = new Tree();
    
    public static void main(String[] args) {
        //Testing purposes  
        twoFourTree.insert(twoFourTree, 10);
        twoFourTree.insert(twoFourTree, 20);
        twoFourTree.insert(twoFourTree, 30);
        twoFourTree.insert(twoFourTree, 5);
        twoFourTree.insert(twoFourTree, 50);
        twoFourTree.insert(twoFourTree, 60);
        twoFourTree.insert(twoFourTree, 70);
        twoFourTree.insert(twoFourTree, 1);
        twoFourTree.insert(twoFourTree, 2);
        twoFourTree.insert(twoFourTree, 15);
        twoFourTree.insert(twoFourTree, 80);
        twoFourTree.insert(twoFourTree, 90);
        twoFourTree.insert(twoFourTree, 100);

        twoFourTree.print(twoFourTree);
    }
}
class Tree {
    public ArrayList<Integer> nodeValues = new ArrayList<Integer>();
    public ArrayList<Tree> children = new ArrayList<Tree>();
    public static int depth = 0;

    //getter helper methods
    public int getIndex(ArrayList<Integer> list, int value) {
        int idx = 0;
        for(Integer i: list) {
            if(i > value) break;
            idx++;
        }
        return idx;
    }
    public Tree getNode(int value) {
        Tree curNode = new Tree();
        int position;

        curNode.nodeValues = nodeValues;
        curNode.children = children;

        if(nodeValues.contains(value)) return curNode;

        while(!curNode.children.isEmpty()) {
            position = getIndex(curNode.nodeValues,value);
            curNode = curNode.children.get(position);
            if(curNode.nodeValues.contains(value)) return curNode;
        }

        return null;
    }
    public Tree getLeftLeafNode(Tree node, int value) {
        if(node.children.isEmpty()) {
            return node;
        }

        int rightPosition = node.children.size();

        return getLeftLeafNode(node.children.get(rightPosition), value);
    }
    public Tree getRightLeafNode(Tree node, int value) {
        if(node.children.isEmpty()) {
            return node;
        }

        return getLeftLeafNode(node.children.get(0), value);
    }
    public Tree getParent(int value) {
        Tree tmpTree = new Tree();
        ArrayList<Integer> curValues = nodeValues;
        ArrayList<Tree> curChildren = children;
        Tree curNode = null;
        int position = getIndex(curValues, value);

        if(curValues.contains(value)) return null;
        //parent of the root node returns null
        if(!curChildren.isEmpty()) {
            boolean validParent = curChildren.get(position).children.isEmpty();
            
            tmpTree.nodeValues = curValues;
            tmpTree.children = curChildren;
            curNode = tmpTree;

            if(!validParent) {
                while(!validParent) {
                    curNode = curChildren.get(position);
                    curValues = curNode.nodeValues;
                    curChildren = curNode.children;
                }
            }
        }
        return curNode;
    }
   
    //insert method with helper method
    public void rebalanceAdd(Tree node) {
        //grab the third value and the parent node of that value
        int newParentVal = node.nodeValues.get(2);
        Tree parent = getParent(newParentVal);

        //create room for tree manipulation
        Tree leftChild = new Tree();
        Tree rightChild = new Tree();
        
        //split the overloaded node with left having two nodeValues and three children; right has one node value and two children
        leftChild.nodeValues.add(node.nodeValues.get(0));
        leftChild.nodeValues.add(node.nodeValues.get(1));

        rightChild.nodeValues.add(node.nodeValues.get(3));

        if(!node.children.isEmpty()) {
            leftChild.children.add(node.children.get(0));
            leftChild.children.add(node.children.get(1));
            leftChild.children.add(node.children.get(2));

            rightChild.children.add(node.children.get(3));
            rightChild.children.add(node.children.get(4));
        }
        //now that we have the split, check to see if the current node is the root
        if(parent == null) {
            //erase the entire tree since...
            nodeValues = new ArrayList<>();
            children = new ArrayList<>();

            //the new root value is just the newParentVal; the now 'deleted' tree is now put back but as a split pair
            nodeValues.add(newParentVal);
            children.add(leftChild);
            children.add(rightChild); 
            
            //rebalance is completed
            depth++;
            return;
        }

        //if there is a node above the current node, find the new position of newParentVal
        int newPosition = getIndex(parent.nodeValues, newParentVal);

        //add to parent node, update left child and add right child
        parent.nodeValues.add(newPosition, newParentVal);
        parent.children.set(newPosition, leftChild);
        parent.children.add(newPosition+1, rightChild);

        //if results in another packed node, aka cascading, do the same thing but with the current node being the parent
        if(parent.nodeValues.size() == 4)
            rebalanceAdd(parent);
        
        
    }
    public void insert(Tree node, int value) {
        int position = getIndex(node.nodeValues, value);
        
        if(node.children.isEmpty()) {
            node.nodeValues.add(position, value);

            //if adding would exceed size of a valid node
            if(node.nodeValues.size() == 4)
                rebalanceAdd(node);    

            return;
        }
        
        insert(node.children.get(position), value);
    }
    
    //delete method with helper methods
    public void fusion(Tree node, int value) {

    }
    public void transfer(Tree node, int value) {

    }
    public void delete(Tree node, int value) {
        Tree delNode = getNode(value);

        int position = getIndex(delNode.nodeValues, value);
        Tree parent = getParent(value);
        Tree leafNode = getLeftLeafNode(delNode.children.get(position), value);

        //tree with depth of 0
        if(parent == null && delNode.children.isEmpty()) {
            delNode.nodeValues.remove(value);
        }
        //handle for internal node deletion
        else if(!delNode.children.isEmpty()) {

        }
        //handle for leaf node
        else if(parent != null && delNode.children.isEmpty()) {

        }
    }

    //print wrapper with recursive print, in-order traversal
    public void print(Tree node) {
        printTree(node, 0);
        System.out.println();
    }
    public void printTree(Tree node, int myDepth) {
        if(node == null) return;

        for(int i = 0; i < myDepth; i++) System.out.print("\t");

        System.out.print("| ");

        for(Integer i : node.nodeValues) System.out.print(i + " ");
        
        for(Tree t: node.children) {
            System.out.println();
            printTree(t, myDepth+1);
        }
    }
}
