package com.example.lit.smartap_20180111.elements;

import com.example.lit.smartap_20180111.Structure.BigIntDef;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

public class Rsa {
    private int BI_MAX_LEN=40;//数组最大长度
    ////小素数表 长度550
    private int[] PrimeTable={
            3, 5, 7, 11, 13, 17, 19, 23, 29, 31,
            37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
            79, 83, 89, 97, 101, 103, 107, 109, 113, 127,
            131, 137, 139, 149, 151, 157, 163, 167, 173, 179,
            181, 191, 193, 197, 199, 211, 223, 227, 229, 233,
            239, 241, 251, 257, 263, 269, 271, 277, 281, 283,
            293, 307, 311, 313, 317, 331, 337, 347, 349, 353,
            359, 367, 373, 379, 383, 389, 397, 401, 409, 419,
            421, 431, 433, 439, 443, 449, 457, 461, 463, 467,
            479, 487, 491, 499, 503, 509, 521, 523, 541, 547,
            557, 563, 569, 571, 577, 587, 593, 599, 601, 607,
            613, 617, 619, 631, 641, 643, 647, 653, 659, 661,
            673, 677, 683, 691, 701, 709, 719, 727, 733, 739,
            743, 751, 757, 761, 769, 773, 787, 797, 809, 811,
            821, 823, 827, 829, 839, 853, 857, 859, 863, 877,
            881, 883, 887, 907, 911, 919, 929, 937, 941, 947,
            953, 967, 971, 977, 983, 991, 997, 1009, 1013, 1019,
            1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 1087,
            1091, 1093, 1097, 1103, 1109, 1117, 1123, 1129, 1151, 1153,
            1163, 1171, 1181, 1187, 1193, 1201, 1213, 1217, 1223, 1229,
            1231, 1237, 1249, 1259, 1277, 1279, 1283, 1289, 1291, 1297,
            1301, 1303, 1307, 1319, 1321, 1327, 1361, 1367, 1373, 1381,
            1399, 1409, 1423, 1427, 1429, 1433, 1439, 1447, 1451, 1453,
            1459, 1471, 1481, 1483, 1487, 1489, 1493, 1499, 1511, 1523,
            1531, 1543, 1549, 1553, 1559, 1567, 1571, 1579, 1583, 1597,
            1601, 1607, 1609, 1613, 1619, 1621, 1627, 1637, 1657, 1663,
            1667, 1669, 1693, 1697, 1699, 1709, 1721, 1723, 1733, 1741,
            1747, 1753, 1759, 1777, 1783, 1787, 1789, 1801, 1811, 1823,
            1831, 1847, 1861, 1867, 1871, 1873, 1877, 1879, 1889, 1901,
            1907, 1913, 1931, 1933, 1949, 1951, 1973, 1979, 1987, 1993,
            1997, 1999, 2003, 2011, 2017, 2027, 2029, 2039, 2053, 2063,
            2069, 2081, 2083, 2087, 2089, 2099, 2111, 2113, 2129, 2131,
            2137, 2141, 2143, 2153, 2161, 2179, 2203, 2207, 2213, 2221,
            2237, 2239, 2243, 2251, 2267, 2269, 2273, 2281, 2287, 2293,
            2297, 2309, 2311, 2333, 2339, 2341, 2347, 2351, 2357, 2371,
            2377, 2381, 2383, 2389, 2393, 2399, 2411, 2417, 2423, 2437,
            2441, 2447, 2459, 2467, 2473, 2477, 2503, 2521, 2531, 2539,
            2543, 2549, 2551, 2557, 2579, 2591, 2593, 2609, 2617, 2621,
            2633, 2647, 2657, 2659, 2663, 2671, 2677, 2683, 2687, 2689,
            2693, 2699, 2707, 2711, 2713, 2719, 2729, 2731, 2741, 2749,
            2753, 2767, 2777, 2789, 2791, 2797, 2801, 2803, 2819, 2833,
            2837, 2843, 2851, 2857, 2861, 2879, 2887, 2897, 2903, 2909,
            2917, 2927, 2939, 2953, 2957, 2963, 2969, 2971, 2999, 3001,
            3011, 3019, 3023, 3037, 3041, 3049, 3061, 3067, 3079, 3083,
            3089, 3109, 3119, 3121, 3137, 3163, 3167, 3169, 3181, 3187,
            3191, 3203, 3209, 3217, 3221, 3229, 3251, 3253, 3257, 3259,
            3271, 3299, 3301, 3307, 3313, 3319, 3323, 3329, 3331, 3343,
            3347, 3359, 3361, 3371, 3373, 3389, 3391, 3407, 3413, 3433,
            3449, 3457, 3461, 3463, 3467, 3469, 3491, 3499, 3511, 3517,
            3527, 3529, 3533, 3539, 3541, 3547, 3557, 3559, 3571, 3581,
            3583, 3593, 3607, 3613, 3617, 3623, 3631, 3637, 3643, 3659,
            3671, 3673, 3677, 3691, 3697, 3701, 3709, 3719, 3727, 3733,
            3739, 3761, 3767, 3769, 3779, 3793, 3797, 3803, 3821, 3823,
            3833, 3847, 3851, 3853, 3863, 3877, 3881, 3889, 3907, 3911,
            3917, 3919, 3923, 3929, 3931, 3943, 3947, 3967, 3989, 4001
    };
    private Rsa(){
    }

    private void init(BigIntDef a){
        a.setSign(1);//1为正，0为负
        a.setLen(1);
        BigInteger[] temp=new BigInteger[BI_MAX_LEN];
        for (int i=0;i<BI_MAX_LEN;i++){
            temp[i]=BigInteger.valueOf(0);
        }
        a.setValue(temp);
    }

    //大数比较函数
    //大数比较，如果大数A位数比大数B多，当然A>B
    //如果位数相同，则从高位开始比较，直到分出大小
    private int cmp(BigIntDef a,BigIntDef b){
        int i;
        if (a.getSign()!=b.getSign()){
            if (a.getSign()==1){
                return 1;
            }else return -1;
        }
        if (a.getLen()>b.getLen()){
            if (a.getSign()==1)
                return 1;
            else return -1;
        }
        else if (a.getLen()<b.getLen()){
            if (a.getSign()==1)
                return -1;
            else return 1;
        }
        //-- 数组的高位表示大数的高位
        for(i=(a.getLen()-1);i>=0;i--)
        {
            if(a.getValue()[i].compareTo(b.getValue()[i]) == 1)
            {
                if(a.getSign()==1)//-- 正数
                    return 1;
                else
                    return -1;
            }
            else if(a.getValue()[i].compareTo( b.getValue()[i]) == -1)
            {
                if(a.getSign()==1)//-- 正数
                    return -1;
                else
                    return 1;
            }
        }

        return 0;
    }

    int CmpUint32(BigIntDef a,BigInteger b){
        if (a.getSign() == 0)
            return -1;

        if (a.getLen() > 1)
            return 1;

        else if (a.getLen()==1){
            return a.getValue()[0] .compareTo(b);
        }
        else {
            return -1;
        }
    }

    //赋值，将A赋值给B，照搬参数的各属性
    BigIntDef mov(BigIntDef a){
        BigIntDef x=new BigIntDef();
        x.setLen(a.getLen());
        x.setSign(a.getSign());
        x.setValue(a.getValue());
        return x;
    }

    BigIntDef movUint64(BigInteger a){
        BigIntDef x=new BigIntDef();
        x.setSign(1);
        BigInteger[] temp=new BigInteger[BI_MAX_LEN];
        int compareInt=a.compareTo(new BigInteger(String.valueOf(0xffffffff)));
        if (compareInt > -1)
        {
            x.setLen(2);
            temp[1] = a.shiftRight(32);
            temp[0] = a;
        }
        else {
            x.setLen(1);
            temp[0]=a;
        }
        for (int i=x.getLen();i < BI_MAX_LEN;i++){
            temp[i]=new BigInteger(String.valueOf(0));
        }
        x.setValue(temp);
        return x;
    }

    //大数相加
    //调用形式：N.Add(A)，返回值：N+A
    //若两大数符号相同，其值相加，否则改变参数符号再调用大数相减函数
    /******************************************************************
     例如：
          A  B  C
     +       D  E
     --------------
     = S  F  G  H

     其中，若C+E<=0xffffffff，则H=C+E，carry(进位标志)=0
     若C+E>0xffffffff，则H=C+E-0x100000000，carry=1

     若B+D+carry<=0xfffffff，则G=B+D，carry=0
     若B+D+carry>0xfffffff，则G=B+D+carry-0x10000000，carry=1

     若carry=0，则F=A，S=0
     若carry=1，A<0xfffffff，则F=A+1，S=0
     若carry=1，A=0xfffffff，则F=0，S=1
     *****************************************************************/
    BigIntDef add(BigIntDef a,BigIntDef b){
        long carry=0;
        BigInteger sum=new BigInteger(String.valueOf(0));
        int len;
        if (a.getSign() == b.getSign()){
            len = (a.getLen() > b.getSign() ? (a.getLen()):(b.getLen()));
            a.setLen(len);
            for (int i=0;i<len;i++){
                sum = a.getValue()[i];
                sum = sum.add(b.getValue()[i]);
                sum = sum.add(new BigInteger(String.valueOf(carry)));
                a.setValue(sum,i);   //-- x.value[i] = sum/0x100000000;
                carry=sum.shiftRight(32).longValue();  //判断是否有进位，有进位则carry=1,否则为0； //-- carry = sum%0x100000000;
            }
            a.setValue(new BigInteger(String.valueOf(carry)),a.getLen());
            a.setLen(len+carry);
            return a;
        }
        else {
            b.setSign(1-b.getSign());
            return sub(a,b);
        }
    }

    BigIntDef AddUint32(BigIntDef a,BigInteger b){
        BigInteger sum ;
        if(a.getSign()==1 && b.signum()==1){
            sum = a.getValue()[0];
            sum = sum.add(b);

            a.setValue(sum,0);
            int compareInt=sum.compareTo(new BigInteger(String.valueOf(0xffffffff)));

            if (compareInt == 1){
                int i=1;
                while (a.getValue()[i].longValue()==1)
                {
                    a.setValue(new BigInteger(String.valueOf(0)),i);
                    i++;
                }
                a.setValue(a.getValue()[i].add(new BigInteger(String.valueOf(1))),i);
                int alen=a.getLen();
                if (alen == 1){
                    alen++;
                    a.setLen(alen);
                }
            }
        }
        else if(b.signum()==1){
            int compareInt=a.getValue()[0].compareTo(b);
            if (compareInt!=1){
                a.setValue(a.getValue()[0].subtract(b),0);
            }
            else {
                a.setValue(b.subtract(a.getValue()[0]),0 );
                if (a.getLen()==1){
                    a.setSign(1);
                }
            }
        }

        return a;
    }

    //大数相减
//调用形式：N.Sub(A)，返回值：N-A
//若两大数符号相同，其值相减，否则改变参数符号再调用大数相加函数
/******************************************************************
 例如：
        A  B  C
 -         D  E
 --------------
 =      F  G  H

 其中，若C>=E，则H=C-E，carry(借位标志)=0
 若C<E，则H=C-E+0x100000000，carry=1

 若B-carry>=D，则G=B-carry-D，carry=0
 若B-carry<D，则G=B-carry-D+0x10000000，carry=1

 若carry=0，则F=A
 若carry=1，A>1，则F=A-1
 若carry=1，A=1，则F=0
 *****************************************************************/
    BigIntDef sub(BigIntDef a,BigIntDef b){
        BigIntDef x=new BigIntDef();
        init(x);

        //-- 符号相同
        if (a.getSign()==b.getSign()){
            int comFlag = cmp(a,b);
            //-- 两个数相等，直接返回0
            if (comFlag == 0){
                return x; //-- 初始化时已赋值为0，直接返回，
            }

            int len,carry=0;
            BigInteger num;
        }
        return x;
    }

}
