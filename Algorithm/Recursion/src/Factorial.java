import java.math.BigInteger;

/**
 * 分别使用线性递归和尾递归求1000的阶乘，并简单比较两者
 */
public class Factorial {
    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        System.out.println(factorial(new BigInteger("1000")));
        System.out.println(System.currentTimeMillis()- t);
        t = System.currentTimeMillis();
        System.out.println(factorial2(new BigInteger("1000"),BigInteger.ONE));
        System.out.println(System.currentTimeMillis()- t);
    }


    /**
     * 使用线性递归计算阶乘
     * @param n
     * @return
     */
    public static BigInteger factorial(BigInteger n ){
        if (n.compareTo(BigInteger.ZERO) < 0) return BigInteger.ZERO;

        if (n.equals(BigInteger.ONE) || n.equals(BigInteger.ZERO)) {
            return new BigInteger("1");
        }
        return n.multiply(factorial(n.subtract(BigInteger.ONE)));
    }


    /**
     * 使用尾递归计算阶乘
     * 如果一个函数中所有递归形式的调用都出现在函数的末尾，我们称这个递归函数是尾递归的。
     * 当递归调用是整个函数体中最后执行的语句且它的返回值不属于表达式的一部分时，这个递归调用就是尾递归。
     * 尾递归函数的特点是在回归过程中不用做任何操作，这个特性很重要，因为大多数现代的编译器会利用这种特点自动生成优化的代码。
     * 尾递归是极其重要的，不用尾递归，函数的堆栈耗用难以估量，需要保存很多中间函数的堆栈。
     * 通过参数传递结果，达到不压栈的目的
     * @param n
     * @param result
     * @return
     */
    public static BigInteger factorial2(BigInteger n,BigInteger result){
        if (n.compareTo(BigInteger.ZERO) < 0) return BigInteger.ZERO;

        if (n.equals(BigInteger.ONE) || n.equals(BigInteger.ZERO)) {
            return result;
        }

        return  factorial2(n.subtract(BigInteger.ONE),n.multiply(result));
    }
}
