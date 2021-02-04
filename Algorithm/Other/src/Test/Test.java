package Test;

import algorithms.InversionNum;

import static Test.EqualNumberUtil.*;
import static algorithms.NetherlandsFlag.partition;
import static algorithms.SmallSum.smallSum;

/**
 * @ClassName Test
 * @Description TODO
 * @Author yinyicao
 * @DateTime 2019/3/25 20:50
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class Test {
    public static void main(String[] args) {
//        SmallSumTest();
//        InversionNumTest();
        NetherlansFlagTest();
    }

    /**
     * 荷兰国旗问题（非快排）
     */
    private static void NetherlansFlagTest(){
        int maxSize = 100;
        int maxValue = 100;
        int[] test = generateRandomArray(maxSize,maxValue);

        printArray(test);
        int[] res = partition(test, 0, test.length - 1, 20);
        printArray(test);
        System.out.println(res[0]);
        System.out.println(res[1]);
    }

    /**
     * 逆序对问题 测试
     */
    private static void InversionNumTest() {
        int testTime = 500000;
        int maxSize = 100;
        int maxValue = 100;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            int[] arr1 = generateRandomArray(maxSize, maxValue);
            int[] arr2 = copyArray(arr1);
            if (InversionNum.inversionNum(arr1) != inversionComparator(arr2)) {
                succeed = false;
                printArray(arr1);
                printArray(arr2);
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Fucking fucked!");
    }

    /**
     * 小和问题   测试
     */
    private static void SmallSumTest() {
        int testTime = 500000;
        int maxSize = 100;
        int maxValue = 100;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            int[] arr1 = generateRandomArray(maxSize, maxValue);
            int[] arr2 = copyArray(arr1);
            if (smallSum(arr1) != smallSumComparator(arr2)) {
                succeed = false;
                printArray(arr1);
                printArray(arr2);
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Fucking fucked!");
    }
}
