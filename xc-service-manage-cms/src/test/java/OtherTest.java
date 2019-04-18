import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherTest {

    public static void main(String[] args) {
        String str = "abacba";
        List<Integer> integers = palindromeString(str);
        System.out.println(integers);
    }

    /** 复杂度: O(n^2)
     abcdcba
     尝试一个一个一个删除，然后进行验证，验证方式
     1. 可以使用栈这用数据结构进行处理
     2. 也可以使用for循环首尾遍历【这里使用这种】

     给定一个字符串，假如删除某一个索引位置的字符，剩下的字符串为一个回文序列，请找出该字符串所有能满足该条件的位置
     比如 给定字符串 abacba ；输出 [2, 3]
     */
    private static List<Integer> palindromeString(String origin) {

        char[] strs = origin.toCharArray();
        List<Integer> position = new ArrayList<>();

        for (int i = 0; i < strs.length; i++) {

            boolean flag = true;

            int half = strs.length / 2;

            if (i < half) {
                for (int m = 0, n = strs.length - 1; m < n ; m ++, n--) {
                    if (m == i) {
                        m ++;
                    }

                    if (strs[m] != strs[n]) {
                        flag = false;
                        break;
                    }
                }
            } else {
                for (int m = 0, n = strs.length - 1; m < n ; m ++, n--) {
                    if (n == i) {
                        n --;
                    }

                    if (strs[m] != strs[n]) {
                        flag = false;
                        break;
                    }
                }
            }

            if (flag) {
                position.add(i);
            }
        }

        return position;
    }

    @Test
    public void testSql() {
        /**

         select id,MyUuid,Uuid,jiFen,time,grade,Uuidname from Choufen
         where MyUUid = #{Myuuid} and grade = #{grade}
         limit #{offset},#{pageSize}


         offset = (pageNum - 1) * pageSize
         */
        Integer year = 1999;
        Integer month = 12;
        Integer day = 31;

        byte y = year.byteValue();
        byte m = month.byteValue();
        byte d = day.byteValue();

        byte[] s = new byte[3];
        s[0] = y;
        s[1] = m;
        s[2] = d;
    }

    @Test
    public void test1() {
        int a = 1;
        int b = 0;
        System.out.println(a | b);
    }


    /**
     * 求 1-1000000000 中有好多个 1
     *   1 - 999999999 和 1000000000
     *
     * 排列组合公式如下：(条件必须满足 n > m)
     *   m          n!
     * C   =  --------------
     *  n    m! * (n - m)!
     *
     */
    @Test
    public void howManyOne() {
        // 极端情况不能使用此公式，极端情况各个位数上全是1，此时就有10个1.
        /**
         总范围为 1 - 1000000000
         循环中没有计算的情况为 1000000000、111111111
         所以得将这10个1作为初始化值
         */
        //
        long sum = 10; // 极端情况 (9 个 1) + (1 个 1) = (10 个 1)
        long place = 9;

        for (long i = 1; i < place; i++) {
            sum += i * Math.pow(9, place - i) * factorial(place)
                    /(factorial(i) * factorial(place - i));
        }
        System.out.println(sum);
    }

    private long factorial(long n) {
        if (n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }



    /**
     * 测试情况比较少的情况！
     */
    @Test
    public void howManyOneTest() {
        // 极端情况不能使用此公式，极端情况各个位数上全是1，此时就有10个1.
        // 假如是 4 位
        long sum = 5;
        long place = 5;

        for (long i = 1; i < place; i++) {
            sum += i * Math.pow(9, place - i) * factorial(place)
            /(factorial(i) * factorial(place - i));
        }
        System.out.println(sum);


        /**
         01 21 31 41 51 61 71 81 91
         10 12 13 14 15 16 17 18 19
         11
         */
        // 20 个 1
        long sum2 = 0;
        for (long i = 1; i <= 99999; i ++) {
            char[] arr = String.valueOf(i).toCharArray();
            for (int j = 0; j < arr.length; j++) {
                if (arr[j] == '1') {
                    sum2 += 1L;
                }
            }
        }
        System.out.println(sum2);
    }

    @Test
    public void test2() {
        // int n = 1000000000;
        int n = 11;
        int count = 0;
        for (int i = 1; i <= n; i *= 10) {
            int a = n / i, b = n % i;
            //之所以补8，是因为当百位为0，则a/10==(a+8)/10，
            //当百位>=2，补8会产生进位位，效果等同于(a/10+1)
            count += (a + 8) / 10 * i + ((a % 10 == 1) ? b + 1 : 0);
        }
        System.out.println(count);
    }

    @Test
    public void stackTest() {


    }


}
