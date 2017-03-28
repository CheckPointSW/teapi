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

import argparse
import os
import time
from utils.logging import Logger
from utils.logging import LogLevel
import platform
if platform.system() == 'Windows':
    import utils.win32_unicode_argv

from api.logic import Run

WAITING_SEC = 30
MAX_TIME_MIN = 30
MAX_TRIES = MAX_TIME_MIN * 60 / WAITING_SEC


def main():

    parser = \
        argparse.ArgumentParser(description='Threat Prevention API example')

    files_argument_group = parser.add_mutually_exclusive_group(required=True)

    files_argument_group.add_argument('-D', '--directory',
                        help='The scanning directory')

    files_argument_group.add_argument('-fp', '--file_path',
                        help='Path to file')


    parser.add_argument('-fn', '--file_name',
                            help='File Name, relevant when file path supplied')
    parser.add_argument('-R', '--recursive', action='store_true',
                        help='Emulate the files in the directory recursively, relevant when scanning directory supplied')

    server_argument_group = parser.add_mutually_exclusive_group(required=True)
    server_argument_group.add_argument('-k', '--key', help='API key')
    server_argument_group.add_argument('-e', '--sandblast_appliance',help='Check Point SandBlast Appliance')
    
    parser.add_argument('-d', '--debug', action='store_true',
                        help='Add debugging')
    blades_info = parser.add_argument_group('Blades info')
    blades_info.add_argument('-t','--te', action='store_true',
                        help='Activate Threat Emulation')
    blades_info.add_argument('-a', '--av', action='store_true',
                        help='Activate Anti-Virus')

    blades_info.add_argument('--tex', action='store_true',
                        help='Activate Threat Extraction (supported only with cloud)')
    blades_info.add_argument('--tex_folder',
                             help='A folder to download the Scrubbing attachments (required when TEX is active)')
    blades_info.add_argument('-m', '--tex_method',
                        choices=['convert', 'clean'],
                        default='convert',
                        help='Scrubbing method. Convert to PDF / CleanContent')


    reports_section = parser.add_argument_group('Reports info', 'Download Reports')
    reports_section.add_argument('-r', '--reports',
                         help='A folder to download the reports to (required for cloud)',
                         required=False)
    reports_section.add_argument('-p', '--pdf', action='store_true',
                         help='Download PDF reports',)
    reports_section.add_argument('-x', '--xml', action='store_true',
                         help='Download XML reports',)
    args = parser.parse_args()

    Logger.level = LogLevel.DEBUG if args.debug else LogLevel.INFO

    # Asking the API to enable features and reports according
    # to what was required by the user.
    features = []
    reports = []
    server = ""
    key = ""
    file_path = ""
    file_name = ""
    directory = ""


    args.te and features.append('te')
    args.av and features.append('av')
    args.tex and features.append('extraction')

    args.xml and reports.append('xml')
    args.pdf and reports.append('pdf')

    # Verify the user values
    if len(reports) and not args.reports:
        parser.error("Please supply a reports directory")
        exit(-1)

    if args.key:
        key = args.key
        if not args.reports:
            parser.error("API Key supplied, please supply a reports folder")
            exit(-1)

    elif args.sandblast_appliance:
        if args.tex:
            Logger.log(LogLevel.ERROR, 'TEX is not supported with Check Point SandBlast Appliance')
            features.remove('extraction')
        server = args.sandblast_appliance

    if args.tex:
        if not args.tex_folder:
            parser.error("TEX is active, please supply a tex folder")
            exit(-1)
        if not os.path.isdir(args.tex_folder):
            Logger.log(LogLevel.ERROR, 'Invalid tex folder as input')
            exit(-1)

    if args.directory:
        if not os.path.isdir(args.directory):
            Logger.log(LogLevel.ERROR, 'Invalid scanning directory in input')
            exit(-1)
        directory = args.directory
    else:
        file_path = args.file_path.encode('utf-8')
        if args.file_name and args.file_name != 0:
            file_name = args.file_name.encode('utf-8')
        else:
            file_name = os.path.basename(file_path)
        if not os.path.isfile(args.file_path):
            Logger.log(LogLevel.ERROR, 'Invalid file path in input (%s)' % args.file_path)
            exit(-1)



    api = Run(directory,
              file_path,
              file_name,
              key,
              server,
              args.reports,
              args.tex_method,
              args.tex_folder,
              features,
              reports,
              args.recursive)

    if not api.is_pending_files():
        Logger.log(LogLevel.INFO, 'The directory is empty')
        exit(0)

    if directory:
        Logger.log(LogLevel.INFO, 'Querying %d files from directory: %s'    % (len(api.pending), args.directory))
    else: Logger.log(LogLevel.INFO, 'Querying file: %s ' % (file_path))

    api.query_directory(True)
    api.print_arrays_status()

    if api.is_pending_files():
        Logger.log(LogLevel.INFO, 'UPLOADING'),
        api.upload_directory()
        api.print_arrays_status()

    max_tries = MAX_TRIES
    while api.is_pending_files() and max_tries > 0:
        time.sleep(WAITING_SEC)
        api.query_directory(False)
        api.print_arrays_status()
        max_tries -= 1

    api.print_arrays()

    ret = api.get_final_status()
    print "return %d" % ret

    exit(ret)

if __name__ == '__main__':
    main()