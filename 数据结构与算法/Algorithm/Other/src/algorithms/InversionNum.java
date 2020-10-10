package algorithms;

/**
 * @ClassName InversionNum
 * @Description TODO 逆序对问题 （**目前只能正确输出个数）
 * 在一个数组中，左边的数如果比右边的数大，则这两个数构成一个逆序对，请打印所有逆序对。
 *
 * @Author yinyicao
 * @DateTime 2019/3/27 10:47
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class InversionNum {

    public static int  inversionNum(int[] arr){
        if (arr == null || arr.length < 2){
            return 0;
        }
        return mergeSort(arr,0,arr.length - 1);
    }

    private static int mergeSort(int[] arr, int l, int r) {
        if (l == r){
            return 0;
        }
        int mid = l + ((r - l) >> 1);
        return mergeSort(arr,l,mid) + mergeSort(arr,mid+1,r) + merge(arr,l,mid,r);
    }

    private static int merge(int[] arr, int l, int m, int r) {
        int help[]  = new int[r - l +1];
        int i = 0;
        int p1 = l;
        int p2 = m + 1;
        int res = 0;
        while (p1 <= m && p2 <= r){
            if(arr[p1] > arr[p2]){
                //如果左边的某一个数a大于右边某一个数b，则a左边的数都大于b。
                res += (m - p1 + 1);
                help[i++] = arr[p2 ++];
            }else{
                help[i++] = arr[p1 ++];
            }
//            help[i++] = arr[p1] > arr[p2] ? arr[p2++] : arr[p1++];
        }
        while (p1 <= m){
            help[i++] = arr[p1++];
        }
        while (p2 <= r){
            help[i++] = arr[p2++];
        }

        for (int j = 0; j < help.length; j++) {
            arr[l+j] = help[j];
        }

        return res;

    }
}
