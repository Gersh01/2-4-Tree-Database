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
* 03/29/2024    Alex            Finished delete by transfer, started getting fusion done
*
/******************************************************************/
//TO-DO:
/*
 * Requirements for putting into production
 *      Delete functionality
 *          - Deleting a value from an internal node
 *          - Deleting a leaf node using fusion
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
        Tree parentTree = new Tree();
        parentTree.nodeValues = nodeValues;
        parentTree.children = children;

        Tree childTree = null;
        int position;
        
        while(!parentTree.children.isEmpty()) {
            position = getIndex(parentTree.nodeValues, value);
            childTree = parentTree.children.get(position);

            if(childTree.nodeValues.contains(value)) return parentTree;

            parentTree = childTree;
        }

        return null;
    }
   
    //insert method with helper method
    public void rebalanceTree(Tree node) {
        if(node == null)
            return;

        if(node.nodeValues.size() > 3) {
            rebalanceAdd(node);
        }

        for(Tree t: node.children)
            rebalanceTree(t);

    }
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
    public void fusion(Tree delNode, int value) {
        Tree parentNode = getParent(value);
        Tree leftSibling = null;
        Tree rightSibling = null;

        //if fusion reaches the root
        if(parentNode == null) {
            return;
        }

        int position = getIndex(parentNode.nodeValues, value);
        int parentValue = parentNode.nodeValues.get(position);

        if((position - 1) >= 0)
            leftSibling = parentNode.children.get(position - 1);

        if((position + 1) < parentNode.children.size())
            rightSibling = parentNode.children.get(position + 1);

        parentNode.nodeValues.remove(position);
        delNode.nodeValues.set(0, parentValue);
        
        if(leftSibling != null) {
            for(int i = 0; i < leftSibling.nodeValues.size(); i++)
                delNode.nodeValues.add(i, leftSibling.nodeValues.get(i));

            for(int i = 0; i < leftSibling.children.size(); i++)
                delNode.children.add(i, leftSibling.children.get(i));

            parentNode.children.remove(position);
        }
        else {
            for(int i = 0; i < rightSibling.nodeValues.size(); i++)
                delNode.nodeValues.add(rightSibling.nodeValues.get(i));

            for(int i = 0; i < rightSibling.children.size(); i++)
                delNode.children.add(rightSibling.children.get(i));

            parentNode.children.remove(position + 1);
        }

        //when the parent also only has one value, do another fusion
        if(parentNode.nodeValues.size() == 1) {
            fusion(parentNode, parentValue);
        }
    }
    public boolean transfer(Tree delNode, Tree parentNode, Tree leftSibling, Tree rightSibling, int value) {
        int siblingValue;
        int parentValue;
        int position;

        //if the left has values to share, make this first
        if(leftSibling != null && leftSibling.nodeValues.size() > 1) {
            
            //find the highest value from the left and delete it
            position = leftSibling.nodeValues.size() - 1;
            siblingValue = leftSibling.nodeValues.get(position);
            leftSibling.nodeValues.remove(position);
  
            //find the parent value
            position = getIndex(parentNode.nodeValues, value) - 1;
            parentValue = parentNode.nodeValues.get(position);
  
            //replace with sibling value
            parentNode.nodeValues.set(position, siblingValue);

            //replace the only element in the delNode with parentValue
            delNode.nodeValues.set(0, parentValue);

            return true;
        }
        if(rightSibling != null && rightSibling.nodeValues.size() > 1) {
            
            //find the lowest value from the right and delete it
            siblingValue = rightSibling.nodeValues.get(0);
            rightSibling.nodeValues.remove(0);

            //find the parent value
            position = getIndex(parentNode.nodeValues, value);
            parentValue = parentNode.nodeValues.get(position);

            //replace with sibling value
            parentNode.nodeValues.set(position, siblingValue);

            //replace the only element in the delNode with parentValue
            delNode.nodeValues.set(0, parentValue);

            return true;
        }

        //both left and right siblings also have only one value
        return false;
    }
    public void delete(Tree node, int value) {
        Tree delNode = getNode(value);
        int position = getIndex(delNode.nodeValues, value);
        Tree parent = getParent(value);

        //internal node deletion helper
        Tree leafNode = null;

        //leaf node deletion size > 1 helpers
        Tree leftSibling = null;
        Tree rightSibling = null;

        //tree with depth of 0
        if(parent == null && delNode.children.isEmpty()) {
            delNode.nodeValues.remove(value);
            return;
        }

        position = getIndex(parent.nodeValues, value);

        //if a left sibling exists - finding the siblings first means less 
        //calls for the parent which takes a long time
        if((position - 1) >= 0)
            leftSibling = parent.children.get(position - 1);

        //if a right sibling exists
        if((position + 1) < parent.children.size())
            rightSibling = parent.children.get(position + 1);

        //handle for internal node deletion
        if(!delNode.children.isEmpty()) {
            leafNode = getLeftLeafNode(delNode.children.get(position), value);
        }

        //handle for leaf node
        if(parent != null && delNode.children.isEmpty()) {
            //if leaf node has enough nodes after deletion
            if(delNode.nodeValues.size() > 1) {
                delNode.nodeValues.remove(Integer.valueOf(value));
                return;
            }
            
            //check for transfer first, if transfer does not work do fusion, fusion has a chance to break the 2-4 property
            if(!transfer(delNode, parent, leftSibling, rightSibling, value)) {
                fusion(node, value);    

                Tree tmpTree = new Tree();
                tmpTree.nodeValues = nodeValues;
                tmpTree.children = children;

                rebalanceTree(tmpTree);
            }
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
