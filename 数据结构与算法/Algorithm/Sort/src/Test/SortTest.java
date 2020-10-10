package Test;

import sort.InsertionSort;

import static Test.EqualNumberUtil.*;
import static sort.BubbleSort.bubbleSort;
import static sort.InsertionSort.insertionSort;
import static sort.MergeSort.mergeSort;
import static sort.QuickSort.quickSort;
import static sort.SelectionSort.selectionSort;


/**
 * @ClassName SortTest
 * @Description TODO
 * @Author yinyicao
 * @DateTime 2019/3/24 17:32
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class SortTest {
    public static void main(String[] args) {
        int testTime = 50000;
        int maxSize = 7;
        int maxValue = 100;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            int[] arr1 = generateRandomArray(maxSize, maxValue);
            int[] arr2 = copyArray(arr1);
//            printArray(arr1);
//            printArray(arr2);

//            selectionSort(arr1);
//            insertionSort(arr1);
//            bubbleSort(arr1);
//            mergeSort(arr1);
            quickSort(arr1);
            comparator(arr2);

//            printArray(arr1);
//            printArray(arr2);
            if (!isEqual(arr1, arr2)) {
                succeed = false;
                break;
            }
        }

        System.out.println(succeed ? "Nice!" : "Fucking fucked!");
//        int[] arr = generateRandomArray(maxSize, maxValue);
//        printArray(arr);
//        bubbleSort(arr);
//        printArray(arr);
    }
}
