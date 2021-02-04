package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @program: Algorithm
 * @description: 输入一个链表，按链表从尾到头的顺序返回一个ArrayList。
 * Tips：可利用栈先入后出的特性
 * @author: yyc
 * @create: 2020-03-30 09:52
 **/
public class ReverseList {
      class ListNode {
        int val;
        ListNode next = null;

        public ListNode() {
          }

        public ListNode(int val) {
            this.val = val;
        }
    }

    /**
     * 逆序链表
     */
    public  static void main(String[] args){
        ReverseList re = new ReverseList();
        ListNode listNode = re.generateListNode();
        //三种方式实现
//        List<Integer> list = ReverseList.printListFromTailToHead1(listNode);
//        List<Integer> list = ReverseList.printListFromTailToHead2(listNode);
        List<Integer> list = ReverseList.printListFromTailToHead3(listNode);
        System.out.println(list);

    }


    /**
     * 生成一个包含十个节点的链表
     * @return
     */
    private ListNode generateListNode() {   //输入一个链表
        ListNode headNode = new ListNode(10);  //头节点
        ListNode listNode;
        ListNode tail;      //表尾节点
        tail = headNode;
        int i = 9;
        while (i != 0){
            listNode = new ListNode(i);
            tail.next = listNode;//指定子节点
            tail  = listNode;//移动尾节点
            i--;
        }
        return headNode;        //返回头节点
    }


    /**
     * 使用Stack的方式实现
     * @param listNode
     * @return
     */
    public static List<Integer> printListFromTailToHead1(ListNode listNode) {
        List<Integer> res1 = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        while (listNode != null){
            stack.push(listNode.val);
            listNode = listNode.next;
        }
        while(!stack.empty()){
            res1.add(stack.pop());
        }
        return res1;
    }

    /**
     * 使用JDK自带重载方法void add(int index, E element)
     * @param listNode
     * @return
     */
    public static List<Integer> printListFromTailToHead2(ListNode listNode) {
        List<Integer> res2 = new ArrayList<>();
        while (listNode != null){
            res2.add(0,listNode.val);//每次都加到下标为0的位置
            listNode = listNode.next;
        }
        return res2;
    }

    /**
     * 递归方式实现
     */
    private static List<Integer> res3 = new ArrayList<>();
    public static List<Integer> printListFromTailToHead3(ListNode listNode) {
        if (listNode != null) {
            printListFromTailToHead3(listNode.next);
            res3.add(listNode.val);
        }
        return res3;
    }
}
