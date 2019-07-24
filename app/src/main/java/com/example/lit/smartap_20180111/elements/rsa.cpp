#include "rsa.h"

//小素数表
const static int PrimeTable[550]=
{   3,    5,    7,    11,   13,   17,   19,   23,   29,   31,
    37,   41,   43,   47,   53,   59,   61,   67,   71,   73,
    79,   83,   89,   97,   101,  103,  107,  109,  113,  127,
    131,  137,  139,  149,  151,  157,  163,  167,  173,  179,
    181,  191,  193,  197,  199,  211,  223,  227,  229,  233,
    239,  241,  251,  257,  263,  269,  271,  277,  281,  283,
    293,  307,  311,  313,  317,  331,  337,  347,  349,  353,
    359,  367,  373,  379,  383,  389,  397,  401,  409,  419,
    421,  431,  433,  439,  443,  449,  457,  461,  463,  467,
    479,  487,  491,  499,  503,  509,  521,  523,  541,  547,
    557,  563,  569,  571,  577,  587,  593,  599,  601,  607,
    613,  617,  619,  631,  641,  643,  647,  653,  659,  661,
    673,  677,  683,  691,  701,  709,  719,  727,  733,  739,
    743,  751,  757,  761,  769,  773,  787,  797,  809,  811,
    821,  823,  827,  829,  839,  853,  857,  859,  863,  877,
    881,  883,  887,  907,  911,  919,  929,  937,  941,  947,
    953,  967,  971,  977,  983,  991,  997,  1009, 1013, 1019,
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


RSA::RSA()
{

}

RSA::~RSA()
{

}

/*----------------------------创建自己的大数运算库---------------------------------*/
void RSA::init(BigIntDef *a)
{
    a->sign = 1;//-- 1为正，0为负
    a->len = 1;

    for(int i=0;i<BI_MAX_LEN;i++)
    {
        a->value[i] = 0;
    }
}

//大数比较函数
//大数比较，如果大数A位数比大数B多，当然A>B
//如果位数相同，则从高位开始比较，直到分出大小
int RSA::Cmp(BigIntDef a, BigIntDef b)
{
    int i;

    if(a.sign != b.sign)
    {
        if(a.sign)
            return 1;
        else
            return -1;
    }

    if(a.len > b.len)
    {
        if(a.sign)//-- 正数
            return 1;
        else
            return -1;
    }
    else if(a.len < b.len)
    {
        if(a.sign)//-- 正数
            return -1;
        else
            return 1;
    }

    //-- 数组的高位表示大数的高位
    for(i=(a.len-1);i>=0;i--)
    {
        if(a.value[i]>b.value[i])
        {
            if(a.sign)//-- 正数
                return 1;
            else
                return -1;
        }
        else if(a.value[i]<b.value[i])
        {
            if(a.sign)//-- 正数
                return -1;
            else
                return 1;
        }
    }

    return 0;
}

int RSA::CmpUint32(BigIntDef a, unsigned long b)
{
    if(a.sign==0)
    {
        //-- 负数
        return -1;
    }

    if(a.len > 1)
    {
        return 1;
    }
    else if(a.len == 1)
    {
        if(a.value[0] > b)
            return 1;
        else if(a.value[0] == b)
            return 0;
        else
            return -1;
    }
    else
    {
        return -1;
    }
}


//赋值，将A赋值给B，照搬参数的各属性
BigIntDef RSA::Mov(BigIntDef a)
{
    BigIntDef x;

    x.len = a.len;
    x.sign = a.sign;
    for(int i=0;i<BI_MAX_LEN;i++)
    {
        x.value[i] = a.value[i];
    }

    return x;
}

BigIntDef RSA::MovUint64(unsigned long long a)
{
    BigIntDef x;

    x.sign = 1;

    if(a > 0xffffffff)
    {
        x.len = 2;
        x.value[1] = (unsigned long)(a>>32);
        x.value[0] = (unsigned long)a;
    }
    else
    {
        x.len = 1;
        x.value[0]=(unsigned long)a;
    }

    for(int i=x.len;i<BI_MAX_LEN;i++)
        x.value[i]=0;

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
BigIntDef RSA::Add(BigIntDef a, BigIntDef b)
{
    unsigned int carry=0;
    unsigned long long sum=0;
    int len;

    if(a.sign == b.sign)
    {
        len = (a.len >= b.len) ? (a.len) : (b.len);
        a.len = len;

        for(int i=0;i<len;i++)
        {
            sum = a.value[i];
            sum = sum + b.value[i] + carry;

            a.value[i]=(unsigned long)sum;      //-- x.value[i] = sum/0x100000000;
            carry=(unsigned long)(sum>>32);          //判断是否有进位，有进位则carry=1,否则为0； //-- carry = sum%0x100000000;
        }

        a.value[a.len] = carry;
        a.len += carry;
        return a;
    }
    else
    {
        b.sign = 1 - b.sign;
        return Sub(a,b);
    }
}

BigIntDef RSA::AddUint32(BigIntDef a, unsigned long b)
{
    quint64 sum;

    if(a.sign)
    {
        sum = a.value[0];
        sum += b;

        a.value[0]=(unsigned long)sum;
        if(sum>0xffffffff)
        {
            unsigned int i=1;
            while(a.value[i]==0xffffffff)
            {
                a.value[i]=0;
                i++;
            }
            a.value[i]++;
            if(a.len == i)
                a.len++;
        }
    }
    else
    {
        if(a.value[0]>=b)
        {
            a.value[0] -= b;
        }
        else
        {
            a.value[0] = b - a.value[0];
            if(a.len==1)
            {
                a.sign = 1;
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
-       D  E
--------------
=    F  G  H

其中，若C>=E，则H=C-E，carry(借位标志)=0
     若C<E，则H=C-E+0x100000000，carry=1

     若B-carry>=D，则G=B-carry-D，carry=0
     若B-carry<D，则G=B-carry-D+0x10000000，carry=1

     若carry=0，则F=A
     若carry=1，A>1，则F=A-1
     若carry=1，A=1，则F=0
*****************************************************************/
BigIntDef RSA::Sub(BigIntDef a, BigIntDef b)
{
    BigIntDef x;
    init(&x);

    //-- 符号相同
    if(a.sign == b.sign)
    {
        int cmpFlag = Cmp(a,b);

        //-- 两个数相等，直接返回0
        if(cmpFlag == 0)
        {
            return x;//-- 初始化时已赋值为0，直接返回，
        }

        unsigned int len,carry=0;

        unsigned long long num;
        unsigned long *s,*d;

        if(cmpFlag>0)
        {
            //-- a > b
            s = a.value;
            d = b.value;
            len = a.len;
            x.sign = a.sign;
        }
        else
        {
            //-- a < b
            s = b.value;
            d = a.value;
            len = b.len;
            x.sign = 1 - a.sign;
        }

        //-- len 取那个大的数的长度
        //-- 从低位开始减，当前位先减去低位的借位，，当是最低位时，借位carry为0
        for(int i=0;i<len;i++)
        {
            if(s[i] >= (d[i] + carry))
            {
                x.value[i] = s[i] - d[i] - carry;
                carry = 0;
            }
            else
            {
                num = 0x100000000 + s[i];
                x.value[i] = (unsigned long)(num - d[i] - carry);
                carry = 1;
            }
        }

        //-- 由于len取的是大的数的长度，所以最高位可能出现0的情况，为0的话，长度不算，即len-1
        while(x.value[len-1] == 0)
            len--;

        x.len = len;

        return x;
    }
    else
    {
        b.sign = 1 - b.sign;
        return Add(a,b);
    }
}

BigIntDef RSA::SubUint32(BigIntDef a, unsigned long b)
{
    unsigned long long num;
    if(a.sign)
    {
        if(a.value[0] >= b)
        {
            a.value[0] -= b;
            return a;
        }

        if(a.len == 1)
        {
            a.sign = 0;
            a.value[0] = b - a.value[0];
            return a;
        }

        num = 0x100000000 + a.value[0];

        a.value[0] = (unsigned long)(num-b);

        int i=1;
        while(a.value[i]==0)
        {
            a.value[i]=0xffffffff;
            i++;
        }

        a.value[i]--;//借位运算；
        if(a.value[i]==0)
            a.len--;
    }
    else
    {
        a.sign = 1;
        a = AddUint32(a,b);
        a.sign = 0;
    }

    return a;
}


//大数相乘
//调用形式：N.Mul(A)，返回值：N*A
/******************************************************************
例如：
        A  B  C
*          D  E
----------------
=    S  F  G  H     --> 向左靠0位
+ T  I  J  K        --> 向左靠1位
----------------
= U  V  L  M  N

其中，SFGH=ABC*E，TIJK=ABC*D

而对于：
     A  B  C
*          E
-------------
= S  F  G  H

其中，若C*E<=0xffffffff，则H=C*E，carry(进位标志)=0
     若C*E>0xffffffff，则H=(C*E)&0xffffffff
       carry=(C*E)/0xffffffff
     若B*E+carry<=0xffffffff，则G=B*E+carry，carry=0
     若B*E+carry>0xffffffff，则G=(B*E+carry)&0xffffffff
       carry=(B*E+carry)/0xffffffff
     若A*E+carry<=0xffffffff，则F=A*E+carry，carry=0
     若A*E+carry>0xffffffff，则F=(A*E+carry)&0xffffffff
       carry=(A*E+carry)/0xffffffff
     S=carry
*****************************************************************/
BigIntDef RSA::Mul(BigIntDef a, BigIntDef b)
{
    BigIntDef x,y;
    init(&x);
    init(&y);

    unsigned long long mul;
    unsigned long carry;

    for(int i=0;i<b.len;i++)
    {
        y.len = a.len;
        carry = 0;

        for(int j=0;j<a.len;j++)
        {
            mul = a.value[j];
            mul = mul * b.value[i] + carry;
            y.value[j] = (unsigned long) mul;       //-- mul / 0x100000000
            carry = (unsigned long)(mul >> 32);     //-- mul % 0x100000000
        }

        //-- 最高位有进位，则增加多一位
        if(carry && (y.len < BI_MAX_LEN))
        {
            y.len++;
            y.value[y.len - 1] = carry;
        }

        //-- 向左靠i位，右边补0
        if(y.len < (BI_MAX_LEN-i))
        {
            y.len += i;
            for(int k=y.len-1;k>=i;k--)
                y.value[k] = y.value[k-i];

            for(int k=0;k<i;k++)
                y.value[k] = 0;
        }

        x = Add(x,y);
    }

    if(a.sign == b.sign)
        x.sign = 1;
    else
        x.sign = 0;

    return x;
}


BigIntDef RSA::MulUint32(BigIntDef a, unsigned long b)
{
    quint64 mul;
    unsigned long carry=0;

    for(int i=0;i<a.len;i++)
    {
        mul = a.value[i];
        mul = mul * b + carry;
        a.value[i] = (unsigned long)mul;        //-- value = mul / 0x100000000
        carry = (unsigned long)(mul>>32);       //-- carry = mul % 0x100000000 进位
    }

    if(carry)
    {
        a.len++;
        a.value[a.len-1] = carry;
    }

    return a;
}


//大数相除
//调用形式：N.Div(A)，返回值：N/A
//除法的关键在于“试商”，然后就变成了乘法和减法
//这里将被除数与除数的试商转化成了被除数最高位与除数最高位的试商
//BigIntDef RSA::div(BigIntDef a, BigIntDef b)
//{
//    BigIntDef x,a1,b1,t1;
//    init(&x);
//    init(&a1);
//    init(&b1);
//    init(&t1);

//    int len;
//    unsigned long long num,div;
//    unsigned long carry=0;
//    int decLen;
//    int i;

//    if(a.len < b.len)
//    {
//        //-- 被除数的长度小于除数的长度，结果为0，直接返回
//        return x;
//    }

//    if(cmp(a,b)==0)
//    {
//        //-- a,b两数相等，商为1
//        x.value[0] = 1;
//        return x;
//    }

//    //-- a的长度>=b的长度
//    decLen = a.len - b.len;//-- 相差的位数
//    x.len = decLen+1;

//    a1 = mov(a);
//    a1.sign = 1;    //-- 强制为正数，然后b1也为正数，对两个正数求商

//    //                 3
//    //               ------
//    //          23  / 7546               7546 - 2300 减 3次后不够减了，即最高位商为3
//    //                                   7546/23 = 328 ... 2
//    for(i=0;i<=decLen;i++)          //-- 重复调用，同时记录减成功的次数，即为商
//    {
//        b1.len = a1.len;
//        for(i=a1.len-1;i>=0;i--)    //-- 将除数扩大，使得除数和被除数位数相等，就是将除数左移与被除数的位数对齐，然后右边补0
//        {
//            if(i >= decLen)
//                b1.value[i] = b.value[i-decLen];
//            else
//                b1.value[i] = 0;
//        }

//        while(cmp(a1,b1)>=0)        //-- a >= b
//        {
//            a1 = sub(a1,b1);
//            x.value[decLen-i]++;    //-- 每减成功一次，将商的对应位加1
//        }
//    }

//    for(i=0;i<x.len;i++)
//    {
//        if(x.value[i]==0)
//            x.len--;
//    }

//    //-- 此时 a / b = x ... a1

//    if(a.sign == b.sign)
//        x.sign = 1;
//    else
//        x.sign = 0;

//    return x;
//}

BigIntDef RSA::Div(BigIntDef a, BigIntDef b)
{
    BigIntDef x,y,z;

    int len;
    unsigned long long num,div;
    unsigned long carry=0;
    unsigned char sign=1;

    init(&x);
    init(&y);
    init(&z);

    if(a.sign != b.sign)
    {
        sign = 0;
        a.sign = 1;
        b.sign = 1;
    }

    while(Cmp(a,b)>0)
    {
        if(a.value[a.len-1]>b.value[b.len-1])
        {
            len = a.len - b.len;
            div = a.value[a.len-1]/(b.value[b.len-1]+1);
        }
        else if(a.len>b.len)
        {
            len = a.len - b.len - 1;
            num = a.value[a.len-1];
            num = (num<<32)+a.value[a.len-2];

            if(b.value[b.len-1]==0xffffffff)
                div=(num>>32);
            else
                div=num/(b.value[b.len-1]+1);
        }
        else
        {
            x = AddUint32(x,1);
            break;
        }

        z = MovUint64(div);
        z.len += len;

        for(int i=z.len-1;i>=len;i--)
            z.value[i]=z.value[i-len];

        for(i=0;i<len;i++)
            z.value[i]=0;

        x = Add(x,z);
        z = Mul(z,b);
        a = Sub(a,z);
    }

    if(Cmp(a,b)==0)
        x = AddUint32(x,1);

    x.sign = sign;

    return x;
}


BigIntDef RSA::DivUint32(BigIntDef a, unsigned long b)
{
    if(a.len == 1)
    {
        a.value[0] = a.value[0]/b;
        return a;
    }

    unsigned long long div,mul;
    int carry=0;

    for(int i=a.len-1;i>=0;i--)
    {
        div = carry;
        div = (div<<32) + a.value[i];
        a.value[i] = (unsigned long)(div/b);
        mul = (div/b)*b;
        carry = (unsigned long)(div-mul);
    }

    if(a.value[a.len-1]==0)
        a.len--;

    return a;
}




//大数求模
//调用形式：N.Mod(A)，返回值：N%A
//求模与求商原理相同，又或者是/*/c=a-b-b-b-b-b.......until(c<b)*/
//BigIntDef RSA::mod(BigIntDef a, BigIntDef b)
//{
//    BigIntDef x,a1,b1,t1;
//    init(&x);
//    init(&a1);
//    init(&b1);
//    init(&t1);

//    int len;
//    unsigned long long num,div;
//    unsigned long carry=0;
//    int decLen;
//    int i;

//    if(a.len < b.len)
//    {
//        //-- 被除数的长度小于除数的长度，结果为0，余数为a
//        return a;
//    }

//    if(cmp(a,b)==0)
//    {
//        //-- a,b两数相等，商为1,余数为0
//        return x;
//    }

//    //-- a的长度>=b的长度
//    decLen = a.len - b.len;//-- 相差的位数

//    a1 = mov(a);
//    a1.sign = 1;    //-- 强制为正数，然后b1也为正数，对两个正数求商

//    //                 3
//    //               ------
//    //          23  / 7546               7546 - 2300 减 3次后不够减了，即最高位商为3
//    //                                   7546/23 = 328 ... 2
//    for(i=0;i<=decLen;i++)          //-- 重复调用，同时记录减成功的次数，即为商
//    {
//        b1.len = a1.len;
//        for(i=a1.len-1;i>=0;i--)    //-- 将除数扩大，使得除数和被除数位数相等，就是将除数左移与被除数的位数对齐，然后右边补0
//        {
//            if(i >= decLen)
//                b1.value[i] = b.value[i-decLen];
//            else
//                b1.value[i] = 0;
//        }

//        while(cmp(a1,b1)>=0)        //-- a >= b
//        {
//            a1 = sub(a1,b1);
//            x.value[decLen-i]++;    //-- 每减成功一次，将商的对应位加1
//        }
//    }

//    for(i=0;i<x.len;i++)
//    {
//        if(x.value[i]==0)
//            x.len--;
//    }

//    //-- 此时 a / b = x ... a1

//    a1.sign = a.sign;

//    return a1;
//}

BigIntDef RSA::Mod(BigIntDef a, BigIntDef b)
{
    BigIntDef y,temp;

    quint64 div,num;
    unsigned long carry=0;
    unsigned int i,len;
    unsigned char sign=1;

    init(&y);
    init(&temp);

    if(a.sign != b.sign)
    {
        sign = 0;
        a.sign = 1;
        b.sign = 1;
    }

    while(Cmp(a,b)>=0)
    {
        div=a.value[a.len-1];
        num=b.value[b.len-1];
        len=a.len-b.len;

        temp = Sub(a, b);

        if((div==num)&&(len==0))
        {
            a = Mov(temp);
            break;
        }

        if((div<=num)&&len)
        {
            len--;
            div=(div<<32)+a.value[a.len-2];
        }

        div=div/(num+1);
        y = MovUint64(div);
        temp = Mul(b, y);
        y = Mov(temp);

        if(len)
        {
            y.len += len;
            for(i=y.len-1;i>=len;i--)
                y.value[i]=y.value[i-len];

            for(i=0;i<len;i++)
                y.value[i]=0;
        }
        a = Sub(a, y);
    }

    a.sign = sign;

    return a;
}



unsigned long RSA::ModUint32(BigIntDef a, unsigned long b)
{
    if(a.len==1)
        return (a.value[0]%b);

    quint64 div;
    int carry=0;

    for(int i=a.len-1;i>=0;i--)
    {
        div = a.value[i];
        div += carry*0x100000000;
        carry = (unsigned long)(div%b);
    }

    return (unsigned long)carry;
}

/*********************************************************************
求最大公约数；调用方式N.Gcd(A,B);
Gcd（A，B）；返回值为A，B的最大公约数d->N；
**********************************************************************/
BigIntDef RSA::Gcd(BigIntDef &a,BigIntDef &b)
{
    BigIntDef x,y,temp;

    x = Mov(a);
    y = Mov(b);
    temp = Mod(x,y);

    while(temp.value[0]!=0)
    {
        x = Mov(y);
        y = Mov(temp);
        temp = Mod(x,y);
    }
    return y;
}

unsigned long RSA::GcdUint32(BigIntDef &a,unsigned long b)
{
    unsigned long temp;

    temp = ModUint32(a,b);

    while(temp!=0)
    {
        a = MovUint64(b);
        b = temp;
        temp = ModUint32(a,b);
    }
    return b;
}


//BigIntDef RSA::MulMulMod(BigIntDef a, BigIntDef b, BigIntDef n)
//{
//    BigIntDef X,Y,Z;

//    X = MovUint64(1);
//    Y = Mov(a);
//    Z = Mov(b);

//    //X.Mov(1);
//    //Y.Mov(*this);
//    //Z.Mov(A);

//    while((Z.len!=1)||Z.value[0])
//    {
//        if(Z.value[0]&1)
//        {
//            Z = SubUint32(Z,1);
//            X = Mul(X,Y);
//            X = Mod(X,n);

//            //Z.Mov(Z.Sub(1));
//            //X.Mov(X.Mul(Y));
//            //X.Mov(X.Mod(B));
//        }
//        else
//        {
//            Z = DivUint32(Z,2);
//            Y = Mul(Y,Y);
//            Y = Mod(Y,n);

//            //Z.Mov(Z.Div(2));
//            //Y.Mov(Y.Mul(Y));
//            //Y.Mov(Y.Mod(B));
//        }
//    }
//    return X;
//}

//-- 乘方模
BigIntDef RSA::MulMulMod(BigIntDef a, BigIntDef b, BigIntDef n)
{
    BigIntDef x,y,temp;

    int i,j,k;
    unsigned int m;
    unsigned long num;

    init(&x);
    init(&y);
    init(&temp);

    k = b.len*32-32;
    num = b.value[b.len-1];

    while(num)
    {
        num = num>>1;
        k++;
    }

    x = Mov(a);

    for(i=k-2;i>=0;i--)
    {
        y = MulUint32(x, x.value[x.len-1]);
        y = Mod(y,n);

        for(m=1;m<x.len;m++)
        {
            for(j=y.len;j>0;j--){y.value[j] = y.value[j-1];}
            y.value[0] = 0;
            y.len++;
            temp = MulUint32(x, x.value[x.len-m-1]);
            y = Add(y, temp);
            y = Mod(y, n);
        }

        x = Mov(y);

        if((b.value[i>>5]>>(i&31))&1)
        {
            y = MulUint32(a, x.value[x.len-1]);
            y = Mod(y, n);

            for(m=1;m<x.len;m++)
            {
                for(j=y.len;j>0;j--){y.value[j] = y.value[j-1];}
                y.value[0] = 0;
                y.len++;
                temp = MulUint32(a, x.value[x.len-m-1]);
                y = Add(y, temp);
                y = Mod(y, n);
            }
            x = Mov(y);
        }
    }
    return x;
}

/****************************************************************************************
产生随机素数
调用方法：N.GetPrime(bits)
返回值：N被赋值为一个bits位（0x100000000进制长度）的素数
****************************************************************************************/
BigIntDef RSA::GetPrime(int bits)
{
    BigIntDef x;
    unsigned int i;
    BigIntDef temp;
    unsigned long len;

    init(&x);
    x.len = bits/8/4;

begin:
    for(i=0;i<x.len;i++)
    {
        x.value[i]=rand()*0x10000+rand();
    }

    x.value[0]=x.value[0]|1;
    for(i=x.len-1;i>0;i--)
    {
        x.value[i]=x.value[i]<<1;
        if(x.value[i-1]&0x80000000)
            x.value[i]++;
    }

    x.value[0]=x.value[0]<<1;
    x.value[0]++;
    for(i=0;i<550;i++)
    {
        if(ModUint32(x,PrimeTable[i])==0)
            goto begin;
    }

    BigIntDef S,A,I,K;

    K = Mov(x);
    K.value[0]--;

    for(i=0;i<5;i++)
    {
        A = MovUint64(rand()*rand());
        S = DivUint32(K, 2);
        I = MulMulMod(A, S, x);
        if(((I.len!=1)||(I.value[0]!=1))&&(Cmp(I, K)!=0))
            goto begin;
    }

    return x;
}


/****************************************************************************************
求不定方程ax-by=1的最小整数解
调用方式：N.Euc(A)
返回值：X,满足：NX mod A=1
****************************************************************************************/
BigIntDef RSA::Euc(BigIntDef &a, BigIntDef &b)
{
    BigIntDef M,E,X,Y,I,J,temp;
    int x,y;

    M = Mov(b);
    E = Mov(a);
    X = MovUint64(0);
    Y = MovUint64(1);

    //M.Mov(b);
    //E.Mov(*this);
    //X.Mov1(0);
    //Y.Mov1(1);

    x=y=1;
    while((E.len!=1)||(E.value[0]!=0))
    {
        I = Div(M,E);
        J = Mod(M,E);
        M = Mov(E);
        E = Mov(J);
        J = Mov(Y);
        Y = Mul(Y,I);

        //temp = M.Div(E);
        //I.Mov(temp);
        //temp = M.Mod(E);
        //J.Mov(temp);
        //M.Mov(E);
        //E.Mov(J);
        //J.Mov(Y);
        //temp = Y.Mul(I);
        //Y.Mov(temp);


        if(x==y)
        {
            if(Cmp(X,Y)>=0)
            {
                Y = Sub(X,Y);
            }
            else
            {
                Y = Sub(Y,X);
                y=0;
            }

            /*
            if(X.Cmp(Y)>=0) {
                temp = X.Sub(Y);
                Y.Mov(temp);
            }
            else {
                temp = Y.Sub(X);
                Y.Mov(temp);
                y=0;
            }*/
        }
        else
        {
            Y = Add(X,Y);
            //temp = X.Add(Y);
            //Y.Mov(temp);
            x=1-x;
            y=1-y;
        }

        X = Mov(J);
        //X.Mov(J);
    }
    if(x==0)
    {
        X = Sub(b,X);
        //temp = b.Sub(X);
        //X.Mov(temp);
    }
    return X;
}

/****************************************************************************************
拉宾米勒算法测试素数
调用方式：N.Rab()
返回值：若N为素数，返回1，否则返回0
****************************************************************************************/
//int RSA::Rab()
//{
//    unsigned i,j,pass;
//    for(i=0;i<550;i++)
//    {if(Mod1(PrimeTable[i])==0)return 0;}
//    CBigInt S,A,I,K,temp;
//    K.Mov(*this);
//    K.m_ulValue[0]--;
//    for(i=0;i<5;i++)
//    {
//        pass=0;
//        A.Mov1(rand()*rand());
//        S.Mov(K);
//        while((S.m_ulValue[0]&1)==0)
//        {
//            for(j=0;j<S.m_nLength;j++)
//            {
//                S.m_ulValue[j]=S.m_ulValue[j]>>1;
//                if(S.m_ulValue[j+1]&1)S.m_ulValue[j]=S.m_ulValue[j]|0x80000000;
//            }
//            if(S.m_ulValue[S.m_nLength-1]==0)S.m_nLength--;
//            temp = A.RsaTrans(S,*this);
//            I.Mov(temp);
//            if(I.Cmp(K)==0){pass=1;break;}
//        }
//        if((I.m_nLength==1)&&(I.m_ulValue[0]==1))pass=1;
//        if(pass==0)return 0;
//    }
//    return 1;
//}



/*
* 产生密钥，公钥和私钥
* @param[in] 随机产生大数密钥
* @param[in] 利用自定义的大数运算规则进行计算
* @return 返回产生成功与否
* – false 表示产生密钥失败
* @pre \e 产生的密钥存储在定义的类中变量中
* @see e，d，n
*/
bool RSA::GenerateKey(QByteArray &e, QByteArray &d, QByteArray &n,unsigned long nbits,unsigned long ebits)
{
    BigIntDef p,q,p1,q1,m,gcd,e1,n1,d1;
    p = GetPrime(nbits/2);
    q = GetPrime(nbits/2);
    n1 = Mul(p,q);

    p1 = SubUint32(p,1);    //-- p-1
    q1 = SubUint32(q,1);    //-- q-1

    m = Mul(p1,q1);
    do{
        e1 = GetPrime(ebits);
        gcd = Gcd(e1,m);
    }while(CmpUint32(gcd,1)!=0);

    d1 = Euc(e1,m);

    e = BigInt2Str(e1,16);
    d = BigInt2Str(d1,16);
    n = BigInt2Str(n1,16);

    return true;


//    for(i=0;i<MAX;i++)
//        m[i]=p[i]=q[i]=n[i]=d[i]=e[i]=0;/*/简单初始化一下*/

//    prime_random(p,q);/*/随机产生两个大素数*/  //-- 18秒
//    mul(p,q,n);
//    mov(p,p1);
//    p1[0]--;
//    mov(q,q1);
//    q1[0]--;      /*/q-1;*/
//    mul(p1,q1,m);//m=(p-1)*(q-1)
//    erand(e,m);
//    rsad(e,m,d);
//    return true;
}


/****************************************************************************************
从字符串按10进制或16进制格式输入到大数
调用格式：N.Get(str,sys)
返回值：N被赋值为相应大数
sys暂时只能为10或16
****************************************************************************************/
BigIntDef RSA::Str2BigInt(QByteArray str, unsigned int system)
{
    unsigned long k;
    int len = str.length();
    BigIntDef x;
    init(&x);

    int i = 0;

    if(str[0] == '-')
    {
        x.sign = 0;
        i = 1;
    }

    for(;i<len;i++)
    {
        x = MulUint32(x,system);
        char byte = str[i];
        if((byte >= '0')&&(byte <= '9'))
        {
            k = byte - 48;
        }
        else if((byte >= 'A')&&(byte <= 'F'))
        {
            k = byte - 55;
        }
        else if((byte >= 'a')&&(byte <= 'f'))
        {
            k = byte - 87;
        }
        else
        {
            k=0;
        }

        x = AddUint32(x,k);
    }

    return x;
}

/****************************************************************************************
将大数按10进制或16进制格式输出为字符串
调用格式：N.Put(str,sys)
返回值：无，参数str被赋值为N的sys进制字符串
sys暂时只能为10或16
****************************************************************************************/
QByteArray RSA::BigInt2Str(BigIntDef a, unsigned int system)
{
    QByteArray str;
    if((a.len==1)&&(a.value[0]==0))
    {
        str = "0";
        return str;
    }

    QString t="0123456789ABCDEF";
    int c;
    QChar ch;

    while(a.value[a.len-1]>0)
    {
        c = ModUint32(a,system);
        ch = t[c];
        str.insert(0,ch);
        a = DivUint32(a,system);      //-- a = a / system
    }

    return str;
}


/****************************************************
 *rsa编码 1024
 *输入参数:
    d:十六进制的d 私钥
    n:十六进制的n
    sourceData:待加密的数据
    destData:  加密后的数据
 *输出参数:
    bool : true-加密成功 false-加密失败
    destData:  加密后的数据
 *****************************************************/
 bool RSA::Encode(QByteArray &e,QByteArray &n,QByteArray &sourceData, QByteArray &destData)
 {
     BigIntDef keyEBigInt;
     BigIntDef keyNBigInt;
     BigIntDef BigSourceData;
     BigIntDef chiperBigInt;

     QByteArray encodeData = sourceData.toHex();//转换成十六进制数据
     QByteArray decodeData;

     keyEBigInt = Str2BigInt(e, 16);
     keyNBigInt = Str2BigInt(n, 16);

     if(Cmp(keyNBigInt,keyEBigInt)<=0)
     {
         return false;
     }

     BigSourceData = Str2BigInt(encodeData, 16);
     chiperBigInt = MulMulMod(BigSourceData, keyEBigInt, keyNBigInt);
     decodeData = BigInt2Str(chiperBigInt, 16);

     //十六进制转换
     destData.clear();
     destData = QByteArray::fromHex(decodeData);

     return true;
}

 /****************************************************
  *rsa编码 1024
  *输入参数:
     d:十六进制的d 私钥
     n:十六进制的n
     sourceData:待加密的数据
     destData:  加密后的数据
  *输出参数:
     bool : true-加密成功 false-加密失败
     destData:  加密后的数据
  *****************************************************/
bool RSA::Decode(QByteArray &d,QByteArray &n,QByteArray &sourceData, QByteArray &destData)
{
    BigIntDef keyDBigInt;
    BigIntDef keyNBigInt;
    BigIntDef BigSourceData;
    BigIntDef chiperBigInt;


    QByteArray encodeData = sourceData.toHex();//转换成十六进制数据
    QByteArray decodeData;

    keyDBigInt = Str2BigInt(d, 16);
    keyNBigInt = Str2BigInt(n, 16);

    if(Cmp(keyNBigInt,keyDBigInt)<=0)
    {
        return false;
    }

    BigSourceData = Str2BigInt(encodeData, 16);
    if(Cmp(BigSourceData,keyNBigInt)>=0)
    {
        return false;
    }

    chiperBigInt = MulMulMod(BigSourceData, keyDBigInt, keyNBigInt);
    decodeData = BigInt2Str(chiperBigInt, 16);

      //在前面补全'0'，到256位
 //     if (decodeData.size() < 256) {
 //            int zeroCharNum = 256 - decodeData.size();
 //            for (int i=0; i<zeroCharNum; i++) {
 //                decodeData.insert(0,'0');
 //            }
 //     }

 //     destData = decodeData;
 //     return true;

    //十六进制转换
    destData.clear();
    destData = QByteArray::fromHex(decodeData);
    return true;
 }
