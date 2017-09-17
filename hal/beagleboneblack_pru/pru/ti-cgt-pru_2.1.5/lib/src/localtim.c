/****************************************************************************/
/*  localtime v2.1.5                                                        */
/*                                                                          */
/* Copyright (c) 1993-2017 Texas Instruments Incorporated                   */
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
#include <time.h>
#include <limits.h>
#include <_tls.h>

#define SECS_IN_MIN (time_t)60
#define MINS_IN_HR  (time_t)60
#define HRS_IN_DAY  (time_t)24
#define SECS_IN_HR  (SECS_IN_MIN * MINS_IN_HR)
#define SECS_IN_DAY (SECS_IN_HR * HRS_IN_DAY)
 
#define LEAPYEAR(y) (    (y+1900) % 4   == 0                               \
			 && ((y+1900) % 100 != 0 || (y+1900) % 400 == 0))

#define DAYS_IN_YR(y) ((time_t)365 + LEAPYEAR(y))
 
/*--------------------------------------------------------------------------*/
/* To protect against multiple threads trying to access the 'local_tm'      */
/* static structure at the same time, we give each thread its own copy of   */
/* the 'local_tm' struct via TLS mechanisms.                                */
/*--------------------------------------------------------------------------*/
__TI_TLS_DATA_DEF(static, struct tm, local_tm, {0});

_CODE_ACCESS struct tm *localtime(const time_t *timer)
{
    struct tm *local_tm_ptr = __TI_TLS_DATA_PTR(local_tm);
    time_t ltime  = timer ? *timer : 0;
 
    local_tm_ptr->tm_sec  = 0;
    local_tm_ptr->tm_min  = 0;
    local_tm_ptr->tm_hour = 0;
    local_tm_ptr->tm_mday = 1;
    local_tm_ptr->tm_mon  = 0;
    local_tm_ptr->tm_year = 0;
 
    if (timer == 0 || ltime == (time_t)-1) return local_tm_ptr;
 
#if INT_MAX <= 32767
    /*------------------------------------------------------------------*/
    /* MAKE SURE THE NUMBER OF SECONDS SINCE Jan 1, 1900 CAN BE         */
    /* REPRESENTED IN SIGNED INTS.                                      */
    /*------------------------------------------------------------------*/
    local_tm_ptr->tm_sec   =  ltime % SECS_IN_MIN;
    local_tm_ptr->tm_min   = (ltime / SECS_IN_MIN) % MINS_IN_HR;
    local_tm_ptr->tm_hour  = (ltime / SECS_IN_HR)  % HRS_IN_DAY;
 
    /*------------------------------------------------------------------*/
    /* CONVERT ltime TO NUMBER OF DAYS                                  */
    /*------------------------------------------------------------------*/
    ltime /= SECS_IN_DAY;
 
    /*------------------------------------------------------------------*/
    /* TO DETERMINE THE YEAR, INSTEAD OF DIVIDING BY 365, DO A SUBTRACT */
    /* LOOP THAT ACCOUNTS FOR LEAP YEARS.                               */
    /*------------------------------------------------------------------*/
    {
	int year = 0;
	while (ltime >= DAYS_IN_YR(year))
	{
	    ltime -= DAYS_IN_YR(year);
	    ++year;
	}
     
	local_tm_ptr->tm_year  = year;
	local_tm_ptr->tm_mday += ltime;
    }
 
#else
    /*------------------------------------------------------------------*/
    /* MAKE SURE THE NUMBER OF SECONDS SINCE Jan 1, 1900 CAN BE         */
    /* REPRESENTED IN SIGNED INTS.                                      */
    /*------------------------------------------------------------------*/
    if ((int)ltime < 0)
    {
	local_tm_ptr->tm_sec  = ltime % 60; 
	local_tm_ptr->tm_min  = ltime / 60; 
    }
    else local_tm_ptr->tm_sec = ltime;
#endif
 
    /*------------------------------------------------------------------*/
    /* MAKE VALUES IN local INTO A VALID TIME.                          */
    /*------------------------------------------------------------------*/
    mktime(local_tm_ptr);
    return local_tm_ptr;
}
