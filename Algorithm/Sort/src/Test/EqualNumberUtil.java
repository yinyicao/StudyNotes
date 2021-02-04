package Test;

import java.util.Arrays;

/**
 * 对数器：通过对数器可以检测你的算法是否写对了
 * 对数器的概念和使用
 * 0，有一个你想要测的方法a，
 * 1，实现一个绝对正确但是复杂度不好的方法b，
 * 2，实现一个随机样本产生器
 * 3，实现比对的方法
 * 4，把方法a和方法b比对很多次来验证方法a是否正确。
 * 5，如果有一个样本使得比对出错，打印样本分析是哪个方法出错。
 * 6，当样本数量很多时比对测试依然正确，可以确定方法a已经正确。
 * @ClassName EqualNumberUtil
 * @Description TODO
 * @Author yinyicao
 * @DateTime 2019/3/24 17:13
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class EqualNumberUtil {


    /**
     * 一个绝对正确的排序方法
     * @param arr
     */
    public static void comparator(int[] arr) {
        Arrays.sort(arr);
    }

    /**
     * 随机数组产生器
     * @param maxSize 数组最大容量
     * @param maxValue 数组最大值
     * @return 随机长度、随机值的数组
     */
    public static int[] generateRandomArray(int maxSize, int maxValue) {
        //Math.random() -> double[0，1）
        //(int)((size+1) * Math.random()) -> [0，size] 整数
        //size=6，size+1=7；
        //Math.random() -> [0，1)*7 -> [0，7) double
        //double -> int[0，6] -> int

        //生成长度随机的数组
        int[] arr = new int[(int) ((maxSize + 1) * Math.random())];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) ((maxValue + 1) * Math.random()) - (int) (maxValue * Math.random());
        }
        return arr;
    }

    /**
     * 比对两个数组是否相等
     * @param arr1 数组arr1
     * @param arr2 数组arr2
     * @return arr1与arr2中的值是否相等，是返回true,反之返回false
     */
    public static boolean isEqual(int[] arr1, int[] arr2) {
        if ((arr1 == null && arr2 != null) || (arr1 != null && arr2 == null)) {
            return false;
        }
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }


    /**
     * 复制数组
     * @param arr 数组arr
     * @return  数组res
     */
    public static int[] copyArray(int[] arr) {
        if (arr == null) {
            return null;
        }
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }


    /**
     * 打印数组
     * @param arr
     */
    public static void printArray(int[] arr) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

}
