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
* 03/30/2024    Alex            Finished fusion delete, internal node delete, and menu functionality
* 04/03/2024    Alex            No duplicates; Calculated worst case runtime for insertions
* 04/04/2024    Alex            Finished deletion operation, calculated worst case runtime for insert and delete
*
/******************************************************************/

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class BTree {

    //TRUE: Menu driven experience, program takes slightly longer (default)
    //FALSE: Enter commands into a textfile and run with textfile
    /*  Commands for each operation:
     *      1 - Insert
     *      2 - Delete
     *      3 - Find
     *      4 - Print
     *      5 - Exit
     *  Followed by a value to operate on, "1 29" (insert 29)
     */
    final private static boolean MENU = true;

    private static Tree twoFourTree = new Tree();
    
    public static void printMenu() {
        System.out.println("----------------------------------------------");
        System.out.println("1 - Add entry");
        System.out.println("2 - Delete entry");
        System.out.println("3 - Check for entry");
        System.out.println("4 - Print tree");
        System.out.println("5 - Exit");
        System.out.println("\nEnter your selection: ");
    }
    public static int handleValueInput(Scanner in) {
        int val;
        while(true) {
            try {
                val = Integer.parseInt(in.next());
            } catch(Exception e) {
                System.out.println("Please enter a valid integer value");
                continue;
            }

            if(val < 1) continue;
            in.nextLine();
            break;
        }

        return val;
    }
    public static void menu() {
        Scanner input = new Scanner(System.in);

        int selection;
        boolean sentinel = true;

        while(sentinel) {
            if(MENU)
                printMenu();

            try {
                selection = Integer.parseInt(input.next());
            } catch(Exception e) {
                System.out.println("Enter a valid Integer");
                continue;
            }

            switch(selection) {
                case 1:
                    if(MENU) {
                        System.out.println("----------------------------------------------");
                        System.out.print("Enter the value you would to add: ");
                    }
                    
                    selection = handleValueInput(input);
                    twoFourTree.insert(twoFourTree, selection); 

                    if(MENU)
                        System.out.println("Success!");

                    break;
                case 2:
                    if(MENU) {
                        System.out.println("----------------------------------------------");
                        System.out.print("Enter the value you would to delete: ");
                    }
                    

                    selection = handleValueInput(input);
                    twoFourTree.delete(twoFourTree, selection);

                    if(MENU)
                        System.out.println("Success!");

                    break;
                case 3:
                    if(MENU) {
                        System.out.println("----------------------------------------------");
                        System.out.print("Enter the value you would like to find: ");
                    }

                    selection = handleValueInput(input);

                    if(twoFourTree.getNode(twoFourTree, selection) != null)
                        System.out.println("Found!");
                    else
                        System.out.println("Not found!");

                    break;
                case 4:
                    System.out.println("----------------------------------------------");
                    twoFourTree.print(twoFourTree);

                    break;
                case 5:
                    System.out.println("Goodbye!");
                    sentinel = false;
                    break;
                default:
                    System.out.println("Enter a value between 1-5");
                    continue;
            }
            
        }
    }
    public static void main(String[] args) {
        long stime = System.currentTimeMillis();

        menu();

        long etime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (etime - stime) + "ms");
    }
}
class Tree {
    private ArrayList<Integer> nodeValues = new ArrayList<Integer>();
    private ArrayList<Tree> children = new ArrayList<Tree>();
    private static int depth = 0;

    //getter helper methods
    public int getIndex(ArrayList<Integer> list, int value) {
        int idx = 0;
        for(Integer i: list) {
            if(i >= value) break;
            idx++;
        }
        return idx;
    }
    public Tree getNode(Tree node, int value) {
        int position;
        
        if(node.nodeValues.contains(value))
            return node;

        if(node.children.isEmpty()) return null;

        position = getIndex(node.nodeValues,value);
        return getNode(node.children.get(position), value);
    }
    public Tree getLeftLeafNode(Tree node, int value) {
        if(node.children.isEmpty()) {
            return node;
        }

        int rightPosition = node.nodeValues.size();

        return getLeftLeafNode(node.children.get(rightPosition), value);
    }
    public Tree getRightLeafNode(Tree node, int value) {
        if(node.children.isEmpty()) {
            return node;
        }

        return getRightLeafNode(node.children.get(0), value);
    }
    public Tree getParent(int value) {
        Tree parentTree = new Tree();
        parentTree.nodeValues = nodeValues;
        parentTree.children = children;
        
        int position;
        Tree childTree = null;

        if(nodeValues.contains(value)) 
            return null;

        while(!parentTree.children.isEmpty()) {
            position = getIndex(parentTree.nodeValues, value);

            childTree = parentTree.children.get(position);

            if(childTree.nodeValues.contains(value)) return parentTree;

            parentTree = childTree;
        }

        return null;
    }
   
    //rebalance function, checks every node
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

    //insertion method
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

        int position;
        int parentValue;

        //if fusion reaches the root
        if(parentNode == null) {
            //make the root the last fused node and update the depth as it has now shrunk
            Tree newRoot = delNode.children.get(0);

            nodeValues = newRoot.nodeValues;
            children = newRoot.children;
            depth--;

            return;
        }

        //get the parent value and replace deleted value
        position = getIndex(parentNode.nodeValues, value);

        if(position == parentNode.nodeValues.size())
            parentValue = parentNode.nodeValues.get(position - 1);
        else  
            parentValue = parentNode.nodeValues.get(position);
        
        delNode.nodeValues.set(0, parentValue);

        //get the siblings of the deleted node
        if((position - 1) >= 0)
            leftSibling = parentNode.children.get(position - 1);

        if((position + 1) < parentNode.children.size())
            rightSibling = parentNode.children.get(position + 1);
        
        //Fuse the nodes depending on left or right
        if(rightSibling != null) {
            for(int i = 0; i < rightSibling.nodeValues.size(); i++)
                delNode.nodeValues.add(rightSibling.nodeValues.get(i));

            for(int i = 0; i < rightSibling.children.size(); i++)
                delNode.children.add(rightSibling.children.get(i));

            parentNode.children.remove(position + 1);
        }
        else {
            for(int i = 0; i < leftSibling.nodeValues.size(); i++)
                delNode.nodeValues.add(i, leftSibling.nodeValues.get(i));

            for(int i = 0; i < leftSibling.children.size(); i++)
                delNode.children.add(i, leftSibling.children.get(i));

            parentNode.children.remove(position - 1);
        }

        //when the parent also only has one value, do another fusion
        if(parentNode.nodeValues.size() == 1) {
            fusion(parentNode, parentValue);
        }
        else {
            position = (position == parentNode.nodeValues.size()) ? (position - 1):position;
            parentNode.nodeValues.remove(position);
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
        Tree delNode = getNode(node, value);
        int position = getIndex(delNode.nodeValues, value);
        Tree parent = getParent(value);

        Tree leafNode = null;
        Tree leftSibling = null;
        Tree rightSibling = null;

        //tree with depth of 0
        if(parent == null && delNode.children.isEmpty()) {
            delNode.nodeValues.remove(Integer.valueOf(value));
            return;
        }

        //handle for internal node deletion
        if(!delNode.children.isEmpty()) {
            int newValue;

            position = getIndex(delNode.nodeValues, value);
            position = (position == delNode.nodeValues.size()) ? (position - 1):position;

            //check if left can be removed easily > 1 value
            leafNode = getLeftLeafNode(delNode.children.get(position), value);

            if(leafNode.nodeValues.size() > 1) {
                newValue = leafNode.nodeValues.get(leafNode.nodeValues.size() - 1);
                delNode.nodeValues.set(position, newValue);
                leafNode.nodeValues.remove(Integer.valueOf(newValue));

                return;
            }
            
            //check if right can be removed easily > 1 value
            leafNode = getRightLeafNode(delNode.children.get(position+1), value);

            if(leafNode.nodeValues.size() > 1) {
                newValue = leafNode.nodeValues.get(0);
                delNode.nodeValues.set(position, newValue);
                leafNode.nodeValues.remove(0);

                return;
            }

            //this part is kinda weird, add one to the new leaf value to be deleted in order
            //for the new value from the leaf node to the internal node does not conflict with this process
            newValue = leafNode.nodeValues.get(0);
            delNode.nodeValues.set(position, newValue);
            leafNode.nodeValues.set(0, newValue+1);

            delete(leafNode, newValue+1);
            

        }
        //handle for leaf node
        else if(parent != null && delNode.children.isEmpty()) {
    
            //if leaf node has enough nodes after deletion
            if(delNode.nodeValues.size() > 1) {
                delNode.nodeValues.remove(Integer.valueOf(value));
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

            //check for transfer first, if transfer does not work do fusion, fusion has a chance to break the 2-4 property
            if(!transfer(delNode, parent, leftSibling, rightSibling, value)) {
                fusion(delNode, value);    

                Tree tmpTree = new Tree();
                tmpTree.nodeValues = nodeValues;
                tmpTree.children = children;

                rebalanceTree(tmpTree);

                return;
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

        for(int i = 0; i < myDepth; i++) System.out.print("  ");

        System.out.print("| ");

        for(Integer i : node.nodeValues) System.out.print(i + " ");
        
        for(Tree t: node.children) {
            System.out.println();
            printTree(t, myDepth+1);
        }
    }
}
