package nl.maastrichtuniversity.dke.gamecontrollersample.tools.AStar;

import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.BoardHelper;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.Direction;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Grid;
import nl.maastrichtuniversity.dke.gamecontrollersample.gameplay.Helpers.ManhDistance;

import java.util.*;

public class AStar {

    public List<Direction> computePath(HashMap <Integer, HashMap<Integer, Grid>> localMap, Grid currentPos, double currentOrient, Grid target){

        HashMap <Integer, HashMap<Integer, Boolean>> visited = new HashMap<>();

        visited.put(currentPos.getX(), new HashMap<>());
        visited.get(currentPos.getX()).put(currentPos.getY(), Boolean.TRUE);

        PriorityQueue<Node> pQueue = new PriorityQueue<>(new NodeComparator());
        Node currentNode = new Node(currentPos, Direction.getDirectionByAngle(currentOrient), null, ManhDistance.compute(currentPos, target));

        while(!currentNode.position.same(target)){
            for (Direction direction : Direction.values()) {
                int xNext = currentNode.position.getX() + direction.getxRelative();
                int yNext = currentNode.position.getY() + direction.getyRelative();
                ;
                Grid nextCell = BoardHelper.getByCoordinates(localMap, xNext, yNext);
                if (nextCell != null){
                    if ((!nextCell.isWall() && !nextCell.hasGuard() && !nextCell.hasIntruder()) || (nextCell.same(target))) {
                        if (visited.containsKey(nextCell.getX())) {
                            if (visited.get(nextCell.getX()).containsKey(nextCell.getY())) {
                                continue;
                            }
                        } else {
                            visited.put(nextCell.getX(), new HashMap<>());
                        }

                        if (nextCell.isTelePortal() && !target.isTelePortal())
                            continue;

                        visited.get(nextCell.getX()).put(nextCell.getY(), Boolean.TRUE);

                        double distance = ManhDistance.compute(nextCell, target);

                        Node child = new Node(nextCell, direction, currentNode, distance);

                        if (currentNode.direction.equals(direction)) {
                            pQueue.add(child);
                        } else {
                            Node extraChild = new Node(nextCell, direction, child, distance + 0.3);
                            pQueue.add(extraChild);
                        }
                    }
                }
            }

            currentNode = pQueue.poll();

            if(currentNode == null)
                return new ArrayList<>();
        }

        LinkedList<Direction> path = new LinkedList<>();

        while (currentNode.parent != null) {
            path.addFirst(currentNode.direction);
            currentNode = currentNode.parent;
        }

        return path;
    }

    private class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node o1, Node o2) {
            if(o1.distance > o2.distance){
                return 1;
            }
            else if(o1.distance < o2.distance){
                return -1;
            }
            return 0;
        }
    }

}
