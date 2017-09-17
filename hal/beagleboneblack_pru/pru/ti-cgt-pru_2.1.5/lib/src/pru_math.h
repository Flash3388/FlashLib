/****************************************************************************/
/*  pru_math.h           v2.1.5                                             */
/*                                                                          */
/* Copyright (c) 2013-2017 Texas Instruments Incorporated                   */
/* http://www.ti.com/                                                       */
/*                                                                          */
/*  Redistribution and  use in source  and binary forms, with  or without   */
/*  modification,  are permitted provided  that the  following conditions   */
/*  are met:                                                                */
/*                                                                          */
/*     Redistributions  of source  code must  retain the  above copyright   */
/*     notice, this list of conditions and the following disclaimer.        */
/*                                                                          */
/*     Redistributions in binary form  must reproduce the above copyright   */
/*     notice, this  list of conditions  and the following  disclaimer in   */
/*     the  documentation  and/or   other  materials  provided  with  the   */
/*     distribution.                                                        */
/*                                                                          */
/*     Neither the  name of Texas Instruments Incorporated  nor the names   */
/*     of its  contributors may  be used to  endorse or  promote products   */
/*     derived  from   this  software  without   specific  prior  written   */
/*     permission.                                                          */
/*                                                                          */
/*  THIS SOFTWARE  IS PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS   */
/*  "AS IS"  AND ANY  EXPRESS OR IMPLIED  WARRANTIES, INCLUDING,  BUT NOT   */
/*  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR   */
/*  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT   */
/*  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,   */
/*  SPECIAL,  EXEMPLARY,  OR CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT  NOT   */
/*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,   */
/*  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY   */
/*  THEORY OF  LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY, OR TORT   */
/*  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE   */
/*  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.    */
/*                                                                          */
/****************************************************************************/

#ifndef __PRUMATH_H__
#define __PRUMATH_H__

#include <stdint.h>

#define Adds_Per_Add  1  /* ratio of REAL add  to add */
#define Adds_Per_Mult 10 /* ratio of REAL mult to add */
#define Adds_Per_Div  50 /* ratio of REAL div  to add */

#define FLT_FIX_Faster_Than_MODF 0 /* (double)((int)(x)) faster than MODFF */

#define Floating_Sub_Has_Guard_Digit 0 /* safe value is 0 */

#undef Support_DENORM     /* do/not support denormalized numbers            */

#ifndef Support_INFNAN
#define Support_INFNAN	  /* do/not support INFINITY's and NAN's            */
#endif

#undef Support_SATURATION /* do/not saturate overflows to MOST_POS,MOST_NEG */

#define INT_MOST_POS_P1 CNST( 2147483648.0) /* most pos int +1 as REAL */
#define INT_MOST_NEG    CNST(-2147483648.0) /* most neg int    as REAL */

#define BPAU 8        /* bits per Addressable Unit                  */
#define BPchar 8      /* bits per char                              */
#define BPshrt 16     /* bits per short                             */
#define BPint 32      /* bits per int                               */
#define BPlong 32     /* bits per long (0 if not supported)         */
#define BPlonglong 64 /* bits per long (0 if not supported)         */

#define BPfloat 32
#define BPdouble 64
#define BPlongdouble BPdouble

#define MSB_In_Hi_Addr 1 /* ENDIANness. 0=big, 1=little */
#define IEEE_Hardware  0 /* 1 if IEEE hardware present  */

#define COUNT_LZERO(nnn) (31 - \
                          (__lmbd(nnn, 1) <= 31 ? __lmbd(nnn, 1) : (unsigned)-1))
#define _llnorm(x) (32 - __lmbd(x,0))

typedef union{
  float f;
  struct{
    unsigned int mantissa:23;
    unsigned int exp:8;
    unsigned int sign:1;
  }fp_format;
}FLOAT2FORM;

typedef union{
  double f;
  struct{
    unsigned int mantissa1 : 32;
    unsigned int mantissa0 : 20;
    unsigned int exp :11;
    unsigned int sign :1;
  }fp_format;
}DOUBLE2FORM;


#define FLOAT_TO_REALNUM(x, y) \
do{ \
FLOAT2FORM xx; \
xx.f = x; \
y.sign = xx.fp_format.sign; \
y.exp = xx.fp_format.exp - 127; \
y.mantissa = 0x80000000 | (xx.fp_format.mantissa << 8); \
}while(0)

#define REALNUM_TO_FLOAT(x, y) \
do{ \
FLOAT2FORM xx; \
xx.fp_format.sign = x.sign; \
xx.fp_format.exp = x.exp + 127; \
xx.fp_format.mantissa = (x.mantissa << 1) >> 9; \
y = xx.f; \
}while(0)

#define DOUBLE_TO_REALNUM(x, y) \
do{ \
DOUBLE2FORM xx; \
xx.f = x; \
y.sign = xx.fp_format.sign; \
y.exp = xx.fp_format.exp - 1023; \
y.mantissa = (uint64_t)xx.fp_format.mantissa0 << 32 | xx.fp_format.mantissa1; \
y.mantissa = 0x8000000000000000 | (y.mantissa << 11); \
}while(0)

#define REALNUM_TO_DOUBLE(x, y) \
do{ \
DOUBLE2FORM xx; \
xx.fp_format.sign = x.sign; \
xx.fp_format.exp = x.exp + 1023; \
xx.fp_format.mantissa0 = ((uint64_t)(x.mantissa << 1) >> 12) >> 32; \
xx.fp_format.mantissa1 = (uint32_t)((x.mantissa << 1) >> 12); \
y = xx.f; \
}while(0)
 
#endif
