package algorithms;

/**
 * @ClassName SmallSum
 * @Description TODO 小和问题- 知识点：归并排序（MergeSort）
 *在一个数组中，每一个数左边比当前数小的数累加起来，叫做这个数组的小和。求一个数组 的小和。
 * 例子： [1,3,4,2,5] 1左边比1小的数，没有；
 * 3左边比3小的数，1；
 * 4左边比4小的数，1、3；
 * 2左边比2小的数，1；
 * 5左边比5小的数，1、3、4、2；
 * 所以小和为1+1+3+1+1+3+4+2=16
 * @Author yinyicao
 * @DateTime 2019/3/25 20:07
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class SmallSum {

    public static int smallSum(int[] arr) {
        if (arr == null || arr.length < 2) {
            return 0;
        }
        return mergeSort(arr, 0, arr.length - 1);
    }

    private static int mergeSort(int[] arr, int l, int r){
        if (l == r){
            return 0;
        }
        int mid = l + ((r - l) >> 1);
        return mergeSort(arr,l,mid) + mergeSort(arr, mid + 1, r) +merge(arr,l,mid,r);
    }

    private static int merge(int[] arr, int l, int m, int r) {
        int help[] = new int[r - l +1];
        int i = 0;
        int p1 = l;
        int p2 = m + 1;
        int res = 0;
        while (p1 <= m && p2 <= r){
            res += arr[p1] < arr[p2] ? (r - p2 + 1) * arr[p1] : 0;
            help[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }
        while(p1 <= m){
            help[i++] = arr[p1++];
        }
        while(p2 <= r){
            help[i++] = arr[p2++];
        }
        for (int j = 0; j < help.length; j++) {
            arr[l+j] = help[j];
        }

        return res;
    }
}
