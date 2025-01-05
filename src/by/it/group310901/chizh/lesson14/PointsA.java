package by.it.group310901.chizh.lesson14;

import java.util.*;


public class PointsA {
    public static class DSU<T> implements Iterable<T> {
        private class DisJointSetNode {
            public T Data;
            public int Rank;
            public DisJointSetNode Parent;
            public int Size;
            public DisJointSetNode() {
                this.Size = 1;
            }
        }
        private final Map<T, DisJointSetNode> set = new HashMap<>();
        private int count = 0;
        public int getCount() {
            return count;
        }
        @Override
        public Iterator<T> iterator() {
            return set.values().stream().map(node -> node.Data).iterator();
        }
        public void makeSet(T member) {
            if (set.containsKey(member)) {
                throw new IllegalArgumentException("A set with the given member already exists.");
            }
            DisJointSetNode newSet = new DisJointSetNode();
            newSet.Data = member;
            newSet.Rank = 0;
            newSet.Parent = newSet;
            set.put(member, newSet);
            count++;
        }
        public T findSet(T member) {
            if (!set.containsKey(member)) {
                throw new IllegalArgumentException("No such set with the given member.");
            }
            return findSet(set.get(member)).Data;
        }
        DisJointSetNode findSet(DisJointSetNode node) {
            DisJointSetNode parent = node.Parent;
            if (node != parent) {
                node.Parent = findSet(node.Parent);
                return node.Parent;
            }
            return parent;
        }
        public void union(T memberA, T memberB) {
            T rootA = findSet(memberA);
            T rootB = findSet(memberB);
            if (Objects.equals(rootA, rootB)) {
                return;
            }
            DisJointSetNode nodeA = set.get(rootA);
            DisJointSetNode nodeB = set.get(rootB);
            if (nodeA.Rank == nodeB.Rank) {
                nodeB.Parent = nodeA;
                nodeA.Rank++;
                nodeA.Size += nodeB.Size;
            } else {
                if (nodeA.Rank < nodeB.Rank) {
                    nodeA.Parent = nodeB;
                    nodeB.Size += nodeA.Size;
                } else {
                    nodeB.Parent = nodeA;
                    nodeA.Size += nodeB.Size;
                }
            }
        }
        public boolean contains(T member) {
            return set.containsKey(member);
        }
        public boolean isConnected(T x, T y) {
            return Objects.equals(findSet(x), findSet(y));
        }
        public int getClusterSize(T member) {
            if (!set.containsKey(member)) {
                throw new IllegalArgumentException("No such set with the given member.");
            }
            return findSet(set.get(member)).Size;
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int distanceRequired = scanner.nextInt();
        int count = scanner.nextInt();
        DSU<Point> dsu = new DSU<>();
        for (int i = 0; i < count; i++) {
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            int z = scanner.nextInt();
            Point point = new Point(x, y, z);
            dsu.makeSet(point);
            for (Point existingPoint : dsu) {
                if (point.distanceTo(existingPoint) <= distanceRequired) {
                    dsu.union(point, existingPoint);
                }
            }
        }
        List<Integer> clusterSizes = new ArrayList<>();
        HashSet<Point> set = new HashSet<>();
        for (Point existingPoint : dsu) {
            Point root = dsu.findSet(existingPoint);
            if (set.contains(root))
                continue;
            set.add(root);
            int size = dsu.getClusterSize(root);
            clusterSizes.add(size);
        }
        Collections.sort(clusterSizes);
        Collections.reverse(clusterSizes);
        for (int size : clusterSizes) {
            System.out.print(size + " ");
        }
    }
    static class Point {
        int x, y, z;
        Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        double distanceTo(Point other) {
            return Math.hypot(Math.hypot(x - other.x, y - other.y), z - other.z);
        }
    }
}