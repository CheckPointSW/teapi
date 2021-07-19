#   Copyright 2016 Check Point Software Technologies LTD
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

from time import gmtime, strftime


def enum(*sequential, **named):
    enums = dict(zip(sequential, range(len(sequential))), **named)
    reverse = dict((value, key) for key, value in enums.items())
    enums['reverse_mapping'] = reverse
    return type('Enum', (), enums)


LogLevel = enum(
    CRITICAL=50,
    ERROR=40,
    WARNING=30,
    INFO=20,
    DEBUG=10,
    NOTSET=0
)


class Logger:
    def __init__(self):
        pass

    level = LogLevel.INFO

    @classmethod
    def log(cls, level, message, e=None):

        if cls.level <= level:
            time_str = strftime('%Y-%m-%d %H:%M:%S', gmtime())
            log_str = '[%s][%s] %s' % (
                time_str, LogLevel.reverse_mapping[level], message)
            print(log_str)
            if e:
                print(str(e))
