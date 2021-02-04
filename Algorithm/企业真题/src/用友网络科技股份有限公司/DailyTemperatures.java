package 用友网络科技股份有限公司;


import java.util.Stack;

/**
 * 根据2020年每日预测气温列表，请重新生成一个等长的列表，对应每天气温值的位置输出还需要等待多少天温度才会升高。如果之后都不会升高就输出0。
 *
 * 例如，给定一个温度列表 t=，你的输出应该是 [6, 1, 4, 2, 1, 1, 0, 0]。
 *
 * 其中,气温列表长度是2020年的天数。每个气温的值的都是 [-100, 100] 范围内的整数。
 *
 * 输入描述:
 * 输入值为一个整数数字组成数组t，数组中每个值代表一天的平均温度
 * 输出描述:
 * 与输入等长的数组，数组中每个值表示对应输入值位置的当天还需等几天才会升温的天数
 * 示例1输入输出示例仅供调试，后台判题数据一般不包含示例
 * 输入
 * 复制
 * [35, 34, 35, 31, 29, 32, 36, 33]
 * 输出
 * 复制
 * [6, 1, 4, 2, 1, 1, 0, 0]
 */
public class DailyTemperatures {

    public static void main(String[] args) {
        int[] temperatures = new int[]{35, 34, 35, 31, 29, 32, 36, 33};
        int[] res = dailyTemperatures(temperatures);
        for (int re : res) {
            System.out.print(re + " ");
        }
    }

    public static int[] dailyTemperatures(int[] temperatures){
        int []res = new int[temperatures.length];
        Stack<Integer> stack = new Stack<>(); //存下标
        for (int i = temperatures.length - 1; i >=0 ; i--) {
            while (!stack.isEmpty() && temperatures[i] >= temperatures[stack.peek()]){
                stack.pop();
            }
            if (stack.isEmpty()){
                res[i] = 0;
            }else{
                res[i] = stack.peek() - i;
            }
            stack.push(i);
        }
        return res;
    }
}
