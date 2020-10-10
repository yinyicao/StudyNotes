package sort;

/**
 * @ClassName sort.SelectionSort
 * @Description TODO
 * @Author yinyicao
 * @DateTime 2019/3/24 16:51
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class SelectionSort {

    public static void selectionSort(int[] arr){
        if (arr == null || arr.length < 2) {
            return;
        }

        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i+1; j < arr.length; j++) {
                minIndex = arr[j] < arr[minIndex] ? j : minIndex;
            }
            swap(arr,i,minIndex);
        }
    }

    private static void swap(int[] arr, int i, int j) {
//        arr[i] = arr[i] ^ arr[j];
//        arr[j] = arr[i] ^ arr[j];
//        arr[i] = arr[i] ^ arr[j];
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
