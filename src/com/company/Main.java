package com.company;

import java.io.*;
import java.util.*;

public class Main {

    // Цена продвижения по диагонали в матрице
    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 2;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    // Для вычисления похожести строк мы можем использовать расстояние Левенштейна,
    // так как в задании не было конкретики, как нужно понимать "похожесть" строк.
    // При этом мы будем наказывать за замены в два раза сильнее, так как замены сильнее ухудшают
    // схожесть, чем пропуски.
    private static int calculateLevenshteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) { // Первые строка и столбец
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min( // Минимум по диагонали, слева и справа
                            dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[x.length()][y.length()];
    }


    // Или мы можем просто искать наидлиннейшую подстроку, что на данных примерах работает лучше.
    // При этом в LCS нужно максимизировать значение, а в расстоянии Левенштайна минимизировать.
    private static int calculateLCSLength(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();

        int max = 0;

        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (str1.charAt(i) == str2.charAt(j)) {

                    if (i == 0 || j == 0) {
                        dp[i][j] = 1; // Начинаем подпоследовательность
                    } else {
                        dp[i][j] = dp[i - 1][j - 1] + 1; // Или продолжаем ее
                    }

                    if (max < dp[i][j]) {
                        max = dp[i][j];
                    }
                }

            }
        }
        return max;
    }

    private static int[][] calculateDistanceMatrix(List<String> list1, List<String> list2) {
        int[][] matrix = new int[list1.size()][list2.size()];
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                // matrix[i][j] = calculateLevenshteinDistance(list1.get(i), list2.get(j));
                matrix[i][j] = calculateLCSLength(list1.get(i), list2.get(j));
            }
        }
        return matrix;
    }

    private static int[] findMinExceptUsed(int[][] matrix, List<Integer> usedX, List<Integer> usedY) {
        int minVal = Integer.MAX_VALUE;
        int min_x = -1;
        int min_y = -1;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (!usedX.contains(i) && !usedY.contains(j) && matrix[i][j] < minVal) {
                    minVal = matrix[i][j];
                    min_x = i;
                    min_y = j;
                }
            }
        }
        return new int[]{min_x, min_y};
    }

    private static int[] findMaxExceptUsed(int[][] matrix, List<Integer> usedX, List<Integer> usedY) {
        int maxVal = Integer.MIN_VALUE;
        int max_x = -1;
        int max_y = -1;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (!usedX.contains(i) && !usedY.contains(j) && matrix[i][j] > maxVal) {
                    maxVal = matrix[i][j];
                    max_x = i;
                    max_y = j;
                }
            }
        }
        return new int[]{max_x, max_y};
    }

    // Здесь перед нами стоит выбор между точностью алгоритма и скоростью работы.
    // Для обеспечения оптимального результата нам пришлось бы использовать экспоненциальный алгоритм -
    // сравнить все возможные комбинации и взять тот, у которого сумма расстояний минимальна.
    // Но это слишком усложнило бы код и привело бы к очень медленной скорости работы при увеличении числа строк.
    // В связи с тем, что в задании не указано, как следует понимать "похожесть" строк, будем считать,
    // что наш алгоритм эвристический и применение более быстрого жадного алгоритма для него оправдано.

    private static Map<Integer, Integer> greedyMatchingAlgorithm(int[][] matrix) {
        List<Integer> usedX = new ArrayList<>(); // Нам надо сохранять порядок вставок
        List<Integer> usedY = new ArrayList<>();
        while (usedX.size() < matrix.length && usedY.size() < matrix[0].length) {
//            int[] coords = findMinExceptUsed(matrix, usedX, usedY); // Для Левенштейна
            int[] coords = findMaxExceptUsed(matrix, usedX, usedY); // Для LCS
            usedX.add(coords[0]);
            usedY.add(coords[1]);
        }
        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < usedX.size(); i++) {
            int key = usedX.get(i);
            int value = usedY.get(i);

            // Тут мы возвращаем только те пары, для которых нашлось соответствие
            result.put(key, value);
        }
        return result;

    }

    public static void main(String[] args) {
        String file = "input.txt";
        ArrayList<String> list1 = new ArrayList();
        ArrayList<String> list2 = new ArrayList();

        try {
            Scanner scanner = new Scanner(new File(file));
            int n = Integer.parseInt(scanner.nextLine()); // Не используем nextInt так как надо переходить на новую строку

            for (int i = 0; i < n; i++) {
                list1.add(scanner.nextLine());
            }
            int m = Integer.parseInt(scanner.nextLine());
            for (int i = 0; i < m; i++) {
                list2.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open input.txt file!");
            e.printStackTrace();
        }
        int[][] matrix = calculateDistanceMatrix(list1, list2);
        Map<Integer, Integer> result = greedyMatchingAlgorithm(matrix);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("output.txt");
        } catch (IOException e) {
            System.out.println("Couldn't open output.txt file!");
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        // Сначала выписываем все из первого списка
        Set<Integer> usedY = new HashSet<>();
        for (int i = 0; i < matrix.length; i++) {
            Integer value = result.get(i);
            String x = list1.get(i);
            String y = "?";
            if (value != null) {
                usedY.add(value);
                y = list2.get(value);
            }
            printWriter.printf("%s:%s\n", x, y);
        }
        // А потом все из второго, что не попало в соответствия к первому
        for (int j = 0; j < matrix[0].length; j++) {
            if (!usedY.contains(j)) {
                printWriter.printf("%s:?\n", list2.get(j));
            }
        }
        printWriter.close();


    }
}
