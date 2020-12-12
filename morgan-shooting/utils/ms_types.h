#ifndef _ms_types_
#define _ms_types_

#ifdef _MSC_VER
    typedef signed char sint8;
    typedef unsigned char uint8;
    typedef signed short sint16;
    typedef unsigned short uint16;
    typedef __int32 sint32;
    typedef unsigned __int32 uint32;
    typedef signed __int64 sint64;
    typedef unsigned __int64 uint64;
#else
    #include "stdint.h"
    typedef signed char sint8;
    typedef unsigned char uint8;
    typedef signed short sint16;
    typedef unsigned short uint16;
    typedef int32_t sint32;
    typedef uint32_t uint32;
    typedef long long sint64;
    typedef unsigned long long uint64;
#endif

    typedef sint16 int16;
    typedef sint32 int32;
    typedef sint64 int64;

#endif