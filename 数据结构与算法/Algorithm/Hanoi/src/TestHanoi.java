/**
 * 汉诺塔问题
 * @ClassName TestHanoi
 * @Description TODO
 * @Author yinyicao
 * @DateTime 2019/2/21 20:33
 * @Blog http://www.cnblogs.com/hyyq/*/

public class TestHanoi {
    public static void main(String[] args) {
        hanoi(3,'A','B','C');
    }

    /**
     *
     * @param n 共有n个盘子
     * @param from 开始的柱子
     * @param inter 中间的柱子
     * @param to 目标柱子
     *    无论有多少个盘子，都认为只有两个（上面的n-1个盘子和最下面的一个盘子）
     */
    public static void hanoi(int n,char from,char inter,char to){
        //只有一个盘子
        if (n ==1 ){
            System.out.println("从"+from+"柱子上移动"+n+"号盘子到"+to+"柱子上");
            //无论有多少个盘子，都认为只有两个（上面的n-1个盘子看为一个和最下面的一个盘子）
        }else{
            //移动上面所有的盘子到中间
            hanoi(n-1,from,to,inter);
            //移动下面的盘子
            System.out.println("从"+from+"柱子上移动"+n+"号盘子到"+to+"柱子上");
            //把上面的所有盘子从中间位置移动到目标位置
            hanoi(n-1,inter,from,to);
        }
    }
}
