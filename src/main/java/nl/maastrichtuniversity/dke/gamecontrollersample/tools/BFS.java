
package nl.maastrichtuniversity.dke.gamecontrollersample.tools;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.TelePortal;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BFS {
    private HashMap <Integer, HashMap<Integer, Grid>> referenceBoard;
    private Grid[][] referenceGrid;
    private Node start;
    private Node target;

    public BFS(){};

    public BFS(HashMap <Integer, HashMap <Integer, Grid>> ReferenceBoard, int[] Start, int[] Target){
        this.referenceBoard = ReferenceBoard;
        this.start = new Node(Start[0], Start[1]);
        this.target = new Node(Target[0], Target[1]);
    }

    public BFS(Grid[][] ReferenceGrid, int[] Start, int[] Target){
        this.referenceGrid = ReferenceGrid;
        this.start = new Node(Start[0], Start[1]);
        this.target = new Node(Target[0], Target[1]);
    }
    public ArrayList<int[]> getPath(){
        //System.out.println("Target =  X: " + target.getX() + "Y: " + target.getY());
        ArrayList<int[]> path = new ArrayList<>();
        //change to queue
        ArrayList<Node> queue = new ArrayList<>();
        //change to visited
        ArrayList<Node> visited = new ArrayList<>();
        Node t;

        if (compare(start, target)){
            path.add(start.getPoint());
            //System.out.println("Same cell " + path.size());
            return path;
        }
        queue.add(start);
        while (!queue.isEmpty()){
//            System.out.println("F: " + queue);
//            System.out.println("E: " + visited);
            Node current = queue.remove(0);
            //System.out.println("Current position = X: " + current.getX() + " Y: " + current.getY());
            visited.add(current);
            int[][] possibleChildren = createChildren(current);
            //System.out.println("PARENT = X: " + current.getX() + ", Y: " + current.getY());
            for (int i = 0; i < possibleChildren.length; i++) {
                if (referenceBoard.containsKey(possibleChildren[i][0]) && referenceBoard.get(possibleChildren[i][0]).containsKey(possibleChildren[i][1])){
                    if (!referenceBoard.get(possibleChildren[i][0]).get(possibleChildren[i][1]).isWall() && !referenceBoard.get(possibleChildren[i][0]).get(possibleChildren[i][1]).hasGuard()){
                        Node child = new Node(possibleChildren[i][0],possibleChildren[i][1]);
                        child.setParent(current);
                        if (!contains(queue, child) && !contains(visited, child)){
                            //System.out.println("CHILD = X: " + child.getX() + ", Y: " + child.getY());
                            if (compare(child, target)){
                                t = child;
                                path = backPropagatePath(t);
                                Collections.reverse(path);
                                //System.out.println("1 =" + path.size());
                                return path;
                            }
                            queue.add(child);
                        }
                    }
                }
            }
        }
        Collections.reverse(path);
        return path;
    }
// METHOD THAT DOESN'T USE A HASHMAP
//    public ArrayList<int[]> getPathNotHashmap(){
//        ArrayList<int[]> path = new ArrayList<int[]>();
//        ArrayList<Node> frontier = new ArrayList<Node>();
//        ArrayList<Node> explored = new ArrayList<Node>();
//        Node t = null;
//        if (compare(start, target)){
//            path.add(start.getPoint());
//            return path;
//        }
//        frontier.add(start);
//        while (frontier.isEmpty() == false){
//            System.out.println(path);
//            Node current = frontier.remove(0);
//            //System.out.println("Current position = X: " + current.getX() + " Y: " + current.getY());
//            explored.add(current);
//            int[][] possibleChildren = createChildren(current);
//            for (int i = 0; i < possibleChildren.length; i++) {
//                if (possibleChildren[i][0] >= 0 && possibleChildren[i][0] < referenceGrid.length && possibleChildren[i][1] >= 0 && possibleChildren[i][1] < referenceGrid[0].length){
//                    if (referenceGrid[possibleChildren[i][0]][possibleChildren[i][1]].isWall() == false){
//                        Node child = new Node(possibleChildren[i][0],possibleChildren[i][1]);
//                        child.setParent(current);
//                        if (contains(frontier, child) == false && contains(explored, child) == false){
//                            if (compare(child, target)){
//                                t = child;
//                                path = backPropagatePath(t);
//                                return path;
//                            }
//                            frontier.add(child);
//                        }
//                    }
//                }
//            }
//        }
//        return path;
//    }

    public int[][] createChildren(Node parent){
        if (referenceBoard.containsKey(parent.getX()) && referenceBoard.get(parent.getX()).containsKey(parent.getY())){
            if (referenceBoard.get(parent.getX()).get(parent.getY()).isTelePortal()){
                TelePortal temp = referenceBoard.get(parent.getX()).get(parent.getY()).getReferenceTelePortal();
                int[][] children = new int[][]{{parent.getX(), parent.getY() + 1},{parent.getX() + 1, parent.getY()},{parent.getX(), parent.getY() - 1},{parent.getX() - 1, parent.getY()},{temp.getXtarget(), temp.getYtarget()}};
                return children;
            }
        }
        int[][] children = new int[][]{{parent.getX(), parent.getY() + 1},{parent.getX() + 1, parent.getY()},{parent.getX(), parent.getY() - 1},{parent.getX() - 1, parent.getY()}};
        return children;
    }

    public boolean compare(Node one, Node two){
        if (one.getX() == two.getX() && one.getY() == two.getY()){
            return true;
        }
        return false;
    }

    public boolean compare(Node one, int[] two){
        if (one.getX() == two[0] && one.getY() == two[1]){
            return true;
        }
        return false;
    }

    public boolean contains(ArrayList<Node> toCheck,Node toCompare){
        for (int i = 0; i < toCheck.size(); i++) {
            if (compare(toCompare, toCheck.get(i))){
                return true;
            }
        }
        return false;
    }
    public ArrayList<int[]> backPropagatePath(Node target){
        ArrayList<int[]> path = new ArrayList<int[]>();
        Node current = target;
        while (current.getParent() != null){
            path.add(current.getPoint());
            current = current.getParent();
        }
        path.add(current.getPoint());
        return path;
    }

    public boolean contains(ArrayList<Node> toCheck,int[] toCompare){
        for (int i = 0; i < toCheck.size(); i++) {
            if (compare(toCheck.get(i),toCompare)){
                return true;
            }
        }
        return false;
    }

}