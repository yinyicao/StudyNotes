package algorithms;

/**
 * @ClassName NetherlandsFlag
 * @Description TODO 荷兰国旗问题(非快排实现)
 * 给定一个数组arr，和一个数num，
 * 请把小于num的数放在数组的左边，等于num的数放在数组的中间，
 * 大于num的数放在数组的右边。
 * @Author yinyicao
 * @DateTime 2019/3/28 20:36
 * @Blog http://www.cnblogs.com/hyyq/
 */
public class NetherlandsFlag {

    /**
     *
     * @param arr
     * @param l
     * @param r
     * @param num
     * @return 等于num的数的区间
     */
    public static int[] partition(int[] arr, int l, int r, int num) {
        int less = l - 1;
        int more = r +1;
        int index = l;
        while (index < more){
            if (arr[index] < num){
                swap(arr,++less,index ++);
            }else if (arr[index] > num){
                swap(arr,-- more,index);
            }else{
                index ++;
            }
        }

        return new int[]{less + 1, more - 1};
    }


    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

}
