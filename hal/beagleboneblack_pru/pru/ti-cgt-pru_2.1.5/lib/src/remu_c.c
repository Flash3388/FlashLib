/******************************************************************************/
/* remu.c  v2.1.5                                                             */
/*                                                                            */
/* Copyright (c) 2013-2017 Texas Instruments Incorporated                     */
/* http://www.ti.com/                                                         */
/*                                                                            */
/*  Redistribution and  use in source  and binary forms, with  or without     */
/*  modification,  are permitted provided  that the  following conditions     */
/*  are met:                                                                  */
/*                                                                            */
/*     Redistributions  of source  code must  retain the  above copyright     */
/*     notice, this list of conditions and the following disclaimer.          */
/*                                                                            */
/*     Redistributions in binary form  must reproduce the above copyright     */
/*     notice, this  list of conditions  and the following  disclaimer in     */
/*     the  documentation  and/or   other  materials  provided  with  the     */
/*     distribution.                                                          */
/*                                                                            */
/*     Neither the  name of Texas Instruments Incorporated  nor the names     */
/*     of its  contributors may  be used to  endorse or  promote products     */
/*     derived  from   this  software  without   specific  prior  written     */
/*     permission.                                                            */
/*                                                                            */
/*  THIS SOFTWARE  IS PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS     */
/*  "AS IS"  AND ANY  EXPRESS OR IMPLIED  WARRANTIES, INCLUDING,  BUT NOT     */
/*  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR     */
/*  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT     */
/*  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,     */
/*  SPECIAL,  EXEMPLARY,  OR CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT  NOT     */
/*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,     */
/*  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY     */
/*  THEORY OF  LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY, OR TORT     */
/*  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE     */
/*  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.      */
/*                                                                            */
/******************************************************************************/
#include <limits.h>

__inline unsigned long __subc(unsigned src1, unsigned src2)
{
    int test = (src1 >= src2);

    if (test) src1 -= src2;

    return src1 << 1 | test;	
}

static inline unsigned _norm(unsigned x)
{
    x = __lmbd(x,1);
    if (x == 32)
        return 32;
    else
        return 31 - x;
}

unsigned __pruabi_remu(unsigned dividend, unsigned divisor)
{
    int i;
    unsigned num, den;
    int shift;
    unsigned first_div = 0;
    unsigned num32;

    shift = _norm(divisor) - _norm(dividend);

    if (dividend < divisor) return dividend;
    if (dividend == 0)      return 0;
    if (divisor == 0)       return UINT_MAX;      

    num = dividend;
    den = divisor << shift;

    num32 = (_norm(dividend) == 0);

    first_div = num32 << shift;

    if (den > num) first_div >>= 1; 

    if (num32)
    {
	if(den > num) { den >>= 1; num -= den; }
	else          { num -= den; den >>= 1; }
    }
    else shift++;

    for (i = 0; i < shift; i++)
	num = __subc(num, den);

    return num >> shift;
}

