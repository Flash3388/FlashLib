/*****************************************************************************/
/*  llmpy.c v2.1.5                                                           */
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
#include <stdint.h>

typedef unsigned short ushort;
typedef unsigned int uint;

//extern unsigned long _mpyl(register long a, register long b);

/*---------------------------------------------------------------------------*/
/* MPYLL: Generic long long mpy routine                                      */
/*                                                                           */
/* Assumes matcher rule exists to shift left/right a long long value by a    */
/* const.                                                                    */
/*                                                                           */
/* Method of implementation:                                                 */
/*  Each src is divided into four 16-bit quantities, as shown below          */
/*       _______________________________                                     */
/*       | top(T)|  hi(H)| mid(M)| lo(L)|                                    */
/*       --------------------------------                                    */
/* We compute partial results as follows:                                    */
/* c1 = src1.L*src2.L                                                        */
/* c2 = (uint64_t)(src1.M*src2.L + src2.M*src1.L) << 16                      */
/* c3 = ((uint64_t)(src1.H*src2.L+src1.M*src2.M+src2.H*src1.L)) << 32        */
/* c4 = {(uint64_t)( ((int)(src1.T*src2.L+src2.T+src1.L))+                   */
/*                 (uint)(src1.H*src2.M+src2.H*src1.M) )} << 48              */
/* dst = c1+c2+c3+c4                                                         */
/*---------------------------------------------------------------------------*/

/* 
   Must be unsigned multiplication to prevent sign extension when
   converting to uint64_t
*/
#define _mpyu(x,y) ((uint32_t)(x) * (uint32_t)(y))

uint64_t __pruabi_mpyll(uint64_t s1, uint64_t s2)
{
    uint16_t  s1_l, s1_m, s1_h, s1_t;
    uint16_t  s2_l, s2_m, s2_h, s2_t;
    uint32_t  t1, t4, t6;
    uint64_t  dst, t3, t5, t7;

    s1_l = (uint16_t)s1;
    s1_m = s1 >> 16;
    s1_h = s1 >> 32;
    s1_t = s1 >> 48;

    s2_l = (uint16_t)s2;
    s2_m = s2 >> 16;
    s2_h = s2 >> 32;
    s2_t = s2 >> 48;

    /*-----------------------------------------------------------------------*/
    /* Begin computing partial results. Compute (1.lo*2.lo) first. This gets */
    /* added directly (with no left shifts).                                 */
    /*-----------------------------------------------------------------------*/
    t1 = _mpyu(s1_l, s2_l);

    /*-----------------------------------------------------------------------*/
    /* Compute (1.M*2.lo)+(2.M*1.lo), save result as long, and shl by 16     */
    /*-----------------------------------------------------------------------*/
    t3 = ((uint64_t)_mpyu(s1_m, s2_l) + (uint64_t)_mpyu(s2_m, s1_l)) << 16;

    /*-----------------------------------------------------------------------*/
    /* Compute (1.M*2.M)+(1.H*2.lo)+(2.H*1.lo) and add them together. An     */
    /* integer addition would suffice as this is going to be left shifted by */
    /* 32 and we'll lose the upper bits anyway.                              */
    /*-----------------------------------------------------------------------*/
    t4 = _mpyu(s1_m, s2_m) + _mpyu(s1_h, s2_l) + _mpyu(s2_h, s1_l);
    t5 = ((uint64_t)t4) << 32;

    /*-----------------------------------------------------------------------*/
    /* Compute (1.T * 2.lo)+(2.T*1.lo) as signed mpy. Add the result with    */
    /* (1.H*2.M)+(2.H*1.M). Shift result left by 32 and save as long long    */
    /*-----------------------------------------------------------------------*/
    t6 =  _mpyu(s1_t, s2_l) + _mpyu(s2_t, s1_l)
        + _mpyu(s2_m, s1_h) + _mpyu(s1_m, s2_h);
    t7 = ((uint64_t)t6) << 48;

    /*-----------------------------------------------------------------------*/
    /* Finally, add all partial results                                      */
    /*-----------------------------------------------------------------------*/
    dst = t1 + t3 + t5 + t7;

    return(dst);
}
