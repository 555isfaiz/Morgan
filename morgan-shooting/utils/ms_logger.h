#ifndef _ms_logger_
#define _ms_logger_

#include <sstream>

namespace msutils
{
    namespace logger
    {
        enum
        {
            TYPE_INFO = 0,
            TYPE_DEBUG,
            TYPE_WARN,
            TYPE_ERROR,
        };
        typedef void (*mloggerF)(int type, const std::string &str, const char *file, int line);
        extern mloggerF fLogger;
    } // namespace logger

    #define mLogInfo(x)                                                                         \
        if (msutils::logger::fLogger != nullptr)                                                \
        {                                                                                       \
            std::stringstream ss;                                                               \
            ss << x;                                                                            \
            msutils::logger::fLogger(msutils::logger::TYPE_INFO, ss.str(), __FILE__, __LINE__); \
        }
    #define mLogDebug(x)                                                                         \
        if (msutils::logger::fLogger != nullptr)                                                 \
        {                                                                                        \
            std::stringstream ss;                                                                \
            ss << x;                                                                             \
            msutils::logger::fLogger(msutils::logger::TYPE_DEBUG, ss.str(), __FILE__, __LINE__); \
        }
    #define mLogWarn(x)                                                                         \
        if (msutils::logger::fLogger != nullptr)                                                \
        {                                                                                       \
            std::stringstream ss;                                                               \
            ss << x;                                                                            \
            msutils::logger::fLogger(msutils::logger::TYPE_WARN, ss.str(), __FILE__, __LINE__); \
        }
    #define mLogError(x)                                                                         \
        if (msutils::logger::fLogger != nullptr)                                                 \
        {                                                                                        \
            std::stringstream ss;                                                                \
            ss << x;                                                                             \
            msutils::logger::fLogger(msutils::logger::TYPE_ERROR, ss.str(), __FILE__, __LINE__); \
        }
} // namespace msutils

#endif