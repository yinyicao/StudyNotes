package sort;

/**
 * @ClassName sort.InsertionSort
 * @Description TODO
 * @Author yinyicao
 * @DateTime 2019/3/24 16:37
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class InsertionSort {

    public static void insertionSort(int[] arr){
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            for (int j = i-1; j >=0 && arr[j] > arr[j+1]; j--) {
                swap(arr,j,j+1);
            }
        }
    }

    private static void swap(int[] arr, int i, int j) {
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }
}
