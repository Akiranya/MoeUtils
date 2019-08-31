package co.mcsky.foundores;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 问题说明：找到 graph 中所有由 1 组成的区块（chunk）。
 *
 * 如果一个 1 的四个垂直/水平方向上也是 1，那么就说这些 1 是相邻的，
 * 并且此相邻性是可以传递的，也就是邻居的邻居也是邻居。
 * 以此推广，一个 1 的所有邻居就组成了一个 chunk。
 *
 * 程序需要返回 graph 中所有的 chunk，每个 chunk 里需要包含其里面所有 1 的位置信息。
 *
 * 例如给定下面一个图：
 *  {1, 1, 1, 0, 0, 0},
 *  {0, 1, 1, 1, 0, 0},
 *  {0, 0, 0, 0, 1, 0},
 *  {0, 0, 0, 0, 0, 0},
 *  {0, 1, 1, 1, 0, 1},
 *  {0, 1, 1, 1, 0, 0}
 *
 * 那么应该返回如下结果：
 * Chunk 1: (0,0) (0,1) (0,2) (1,1) (1,2) (1,3)
 * Chunk 2: (2,4)
 * Chunk 3: (4,1) (4,2) (5,1) (4,3) (5,2) (5,3)
 * Chunk 4: (4,5)
 *
 * P.s. 坐标的格式是 (rows, cols)
 */
public class OneFinder {

    private final int[][] matrix; // 要搜索的矩阵
    private final int rows;
    private final int cols;
    private boolean[][] discovered; // 储存某个 vertex 是否已被探索

    // 一个 vertex 四个垂直/水平方向的邻居的相对坐标
    // 这是搜索邻居的标准，可以根据要求进行修改
    private final V[] neighbors = {
            new V(0, 1),
            new V(1, 0),
            new V(0, -1),
            new V(-1, 0)
    };

    // Helper class
    // 为了方便操作位置信息
    private static class V {
        int row;
        int col;

        V(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    OneFinder(int[][] matrix) {
        this.matrix = matrix;
        this.rows = matrix.length; // 矩阵的 rows
        this.cols = matrix[0].length; // 矩阵的 columns（假设矩阵的所有 cols 长度相等，所以这里只获取第一列的长度就好）
        discovered = new boolean[rows][cols]; // 布尔矩阵初始化后默认都是 false，在 BFS 里也就代表未探索
    }

    List<V> BFS(int row, int col) {
        Queue<V> queue = new LinkedList<>();

        V start_vertex = new V(row, col);
        queue.add(start_vertex); // 添加 start_vertex 到 queue
        discovered[row][col] = true; // 标记 start_vertex 为已探索

        List<V> locations = new ArrayList<>(); // 准备把遍历过的所有 vertex 存起来，放进一个 list 里

        while (!queue.isEmpty()) {
            V v = queue.remove();

            locations.add(v); // vertex 遍历过就存起来

            // 遍历 vertex 的所有（四个）邻居
            for (V neighbor : neighbors) {
                int nRow = v.row + neighbor.row; // v 的邻居的 row 坐标
                int nCol = v.col + neighbor.col; // v 的邻居的 col 坐标
                if (withinBorder(nRow, nCol) && !isDiscovered(nRow, nCol) && isOne(nRow, nCol)) {
                    // 这里 IF 需要满足三个条件：
                    // 1、邻居 v 在边界内
                    // 2、邻居 v 没有被探索
                    // 3、邻居 v 是 1 而非 0
                    queue.add(new V(nRow, nCol)); // 把邻居加进 queue
                    discovered[nRow][nCol] = true; // 把邻居标记为已探索
                }
            }
        }

        return locations;
    }

    /**
     * @param row Row
     * @param col Column
     * @return 是否已经探索过
     */
    boolean isDiscovered(int row, int col) {
        return discovered[row][col];
    }

    /**
     * @param row Row
     * @param col Column
     * @return 是否在矩阵里面
     */
    boolean withinBorder(int row, int col) {
        // 这里只用 < 而不用 <= 是因为需要考虑数组越界的情况
        // 这里的 rows 和 cols 都是 index，最后的 index 永远比 length 小 1
        // 所以当 index 等于 length - 1 时，就应该返回 false（不在合法范围内）
        return row < this.rows && row >= 0 && col < this.cols && col >= 0;
    }

    /**
     * @param row Row
     * @param col Column
     * @return 是否在 1 内
     */
    boolean isOne(int row, int col) {
        return matrix[row][col] != 0;
    }

    public static void main(String[] args) {

        // 要搜索的矩阵/图
        int[][] matrix = {
                {1, 1, 1, 0, 0, 0},
                {0, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 1, 0},
                {1, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 1},
                {0, 1, 1, 1, 0, 1}
        };
        OneFinder obj = new OneFinder(matrix); // 以要搜索的矩阵，创建算法对象
        List<List<V>> chunks = new ArrayList<>(); // 所有包含 1 的 chunks

        // 遍历矩阵里的每个 vertex
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {

                if (obj.isOne(row, col) && !obj.isDiscovered(row, col)) {
                    List<V> vertices = obj.BFS(row, col); // 返回一个 list 形式的已遍历所有 vertex (包含位置信息)
                    chunks.add(vertices); // 把这个 list 添加进另一个 list (chunks)，供之后 print
                }
            }
        }

        // print 每个 chunk 里的位置信息
        int numberChunks = 1;
        for (List<V> chunk : chunks) {
            System.out.print("Chunk " + numberChunks + ": "); // 标题
            for (V v : chunk) {
                System.out.print("(" + v.row + "," + v.col + ") ");
            }
            System.out.println(); // new line
            numberChunks++;
        }

    }

}
