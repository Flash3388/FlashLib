/*****************************************************************************/
/* IMATH64.C   v2.1.5 - Long Long int arithmetic                             */
/*                                                                           */
/* Copyright (c) 2013-2017 Texas Instruments Incorporated                    */
/* http://www.ti.com/                                                        */
/*                                                                           */
/*  Redistribution and  use in source  and binary forms, with  or without    */
/*  modification,  are permitted provided  that the  following conditions    */
/*  are met:                                                                 */
/*                                                                           */
/*     Redistributions  of source  code must  retain the  above copyright    */
/*     notice, this list of conditions and the following disclaimer.         */
/*                                                                           */
/*     Redistributions in binary form  must reproduce the above copyright    */
/*     notice, this  list of conditions  and the following  disclaimer in    */
/*     the  documentation  and/or   other  materials  provided  with  the    */
/*     distribution.                                                         */
/*                                                                           */
/*     Neither the  name of Texas Instruments Incorporated  nor the names    */
/*     of its  contributors may  be used to  endorse or  promote products    */
/*     derived  from   this  software  without   specific  prior  written    */
/*     permission.                                                           */
/*                                                                           */
/*  THIS SOFTWARE  IS PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS    */
/*  "AS IS"  AND ANY  EXPRESS OR IMPLIED  WARRANTIES, INCLUDING,  BUT NOT    */
/*  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR    */
/*  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT    */
/*  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,    */
/*  SPECIAL,  EXEMPLARY,  OR CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT  NOT    */
/*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,    */
/*  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY    */
/*  THEORY OF  LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY, OR TORT    */
/*  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE    */
/*  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.     */
/*                                                                           */
/*****************************************************************************/
#include <limits.h>
#include <stdlib.h>

#define _divull __pruabi_divull
#define _divlli __pruabi_divlli

unsigned long long _divull (unsigned long long x1, unsigned long long x2);
long long          _divlli (long long a, long long b);

/***********************************************************************/
/*                                                                     */
/* __lmbdull() - Same as __lmbd(int, int) but takes (int, ulong long).   */
/*                                                                     */
/***********************************************************************/
static unsigned __lmbdull(unsigned long long src, int val)
{
    unsigned int p1 = src >> 32; 
    unsigned p2 = src;
    unsigned int pos;

    if ((pos = __lmbd(p1, val)) == 32)
    {
        pos = __lmbd(p2, val);
        if (pos == 32)
            return (unsigned)-1;
        else return pos;
    }
    else 
        return pos + 32;
}

#define __normull(x) (63 - __lmbdull((x),1))

/***********************************************************************/
/*                                                                     */
/* _subcull() - Same as _subc(int, int), but takes 		       */
/* (ulong long, ulong long).      				       */
/*                                                                     */
/***********************************************************************/
static unsigned long long _subcull(unsigned long long src1, 
					    unsigned long long src2)
{
    unsigned long long res1 = ((src1-src2) << 1) | 0x1;
    unsigned long long res2 = src1 << 1;
    if (src1 >= src2)
      return res1;
    else
      return res2;
}

/***********************************************************************/
/*                                                                     */
/* _remull() - Unsigned 64-bit remainder.                              */
/*                                                                     */
/***********************************************************************/
_CODE_ACCESS unsigned long long __pruabi_remull (unsigned long long a, 
                                                 unsigned long long b)
{
    return a - _divull(a,b) * b;
}

/***********************************************************************/
/*                                                                     */
/* _divull() - Unsigned 64-bit division.                               */
/*                                                                     */
/***********************************************************************/
_CODE_ACCESS unsigned long long __pruabi_divull(unsigned long long x1, unsigned long long x2)
{
    register int i;
    register unsigned long long num;
    register unsigned long long den;
    register int shift;
    unsigned long long first_div = 0;
    unsigned long long num64;

    shift = __normull(x2) - __normull(x1);

    if (x1 < x2) return 0;
    if (x1 == 0) return 0;
    /* ! if (x2 == 0) return  -1;  */
    if (x2 == 0) return (unsigned long long) -1;      

    num = x1;
    den = x2 << shift;

    num64 = (__normull(x1) == 0);

    first_div = num64 << shift;

    if (den > num) first_div >>= 1; 

    if (num64)
    {
	if(den > num) { den >>= 1; num -= den; }
	else          { num -= den; den >>= 1; }
    }
    else
	shift++;

    for (i = 0; i < shift; i++)
    {
      num = _subcull(num, den);
    }

    if (shift)
        return num << (64-shift) >> (64-shift) | first_div;
    else
	return first_div;
}

/***********************************************************************/
/*                                                                     */
/* _remlli() - Signed 64-bit remainder.                                */
/*                                                                     */
/***********************************************************************/
_CODE_ACCESS long long __pruabi_remlli (long long a, long long b)
{
    return a - _divlli(a,b) * b;
}

/***********************************************************************/
/*                                                                     */
/* _divlli() - Signed 64-bit division.                                 */
/*                                                                     */
/***********************************************************************/
_CODE_ACCESS long long __pruabi_divlli(long long a, long long b)
{
   /*-----------------------------------------------------------------------*/
   /* CHECK SIGNS, TAKE ABSOLUTE VALUE, AND USED UNSIGNED DIVIDE.           */
   /*-----------------------------------------------------------------------*/
   long long sign        = (a ^ b) >> 63;
   unsigned long long ua = (a == LLONG_MIN ? a : llabs(a));
   unsigned long long ub = (b == LLONG_MIN ? b : llabs(b));
   unsigned long long q  = _divull(ua, ub);

   if (b == 0) return a ? (((unsigned long long)-1) >> 1) ^ sign : 0;
			/* saturation value or 0 */

   return sign ? -q : q;
}


/* Macros used to implement long long shift operations. For these routines
   to work, you will need to make sure that shifts of ULONG_BITS do not
   generate RTS calls, otherwise you will get infinite recursion.            */

#define ULONG_BITS (sizeof(unsigned long) * CHAR_BIT)
#define _HI(x) ((unsigned long)(((unsigned long long)x) >> ULONG_BITS))
#define _LO(x) ((unsigned long) ((unsigned long long)x))
#define _COMBINE(hi,lo) (((unsigned long long)(hi) << ULONG_BITS) | (lo))

/*****************************************************************************/
/*                                                                           */
/* __lsrll() - logical shift right for "unsigned long long"                  */
/*                                                                           */
/*                                                                           */
/* Generic routine that implements shift right logical in terms of           */
/* unsigned long. Assumes long is half the size of long long.                */
/*****************************************************************************/
unsigned long long __pruabi_lsrll(unsigned long long x, unsigned char shift)
{
    unsigned long hi, lo;
   
    if (shift >= ULONG_BITS)
    {
        return (unsigned long long)(_HI(x) >> (shift - ULONG_BITS));
    }
    else
    {
        lo = _LO(x) >> shift;
        // subtract 1 to support shift of 0
        lo |= _HI(x) << (ULONG_BITS - shift - 1) << 1;
        hi = _HI(x) >> shift;
        return _COMBINE(hi,lo);
    }
}

/*****************************************************************************/
/*                                                                           */
/* __asrll() - logical shift right for "unsigned long long"                  */
/*                                                                           */
/*                                                                           */
/* Generic routine that implements arithmetic right shift in terms of        */
/* unsigned long. Assumes long is half the size of long long.                */
/*****************************************************************************/
long long __pruabi_asrll(long long x, unsigned char shift)
{
    unsigned long hi, lo;

    if (shift >= ULONG_BITS)
    {
        // rely on arithmetic right shift of signed long and sign extension
        // of long to long long.
        return (long long)(((signed long)_HI(x)) >> shift);
    }
    else
    {
        lo = _LO(x) >> shift;
        // subtract 1 to support shift of 0
        lo |= _HI(x) << (ULONG_BITS - shift - 1) << 1;
        hi = (((signed long)_HI(x)) >> shift);
        return _COMBINE(hi,lo);
    }
}

/*****************************************************************************/
/*                                                                           */
/* __lslll() - logical shift left for "unsigned long long"                   */
/*                                                                           */
/*                                                                           */
/* Generic routine that implements logical left shift in terms of            */
/* unsigned long. Assumes long is half the size of long long.                */
/*****************************************************************************/
unsigned long long __pruabi_lslll(unsigned long long x, unsigned char shift)
{
    unsigned long hi, lo;

    if (shift >= ULONG_BITS)
    {
        hi = _LO(x) << (shift - ULONG_BITS);
        return _COMBINE(hi,0);
    }
    else
    {
        hi = _HI(x) << shift;
        // subtract 1 to support shift of 0
        hi |= (_LO(x) >> (ULONG_BITS - shift - 1) >> 1);
        lo = _LO(x) << shift;
        return _COMBINE(hi,lo);
    }
}


