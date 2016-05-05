#   Copyright 2015 Check Point Software Technologies LTD
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

from api.logic import Run

WAITING_SEC = 30
MAX_TIME_MIN = 30
MAX_TRIES = MAX_TIME_MIN * 60 / WAITING_SEC

if __name__ == "__main__":

    parser = argparse.ArgumentParser(description='Threat Prevention API example')
    parser.add_argument('-D', '--directory', help='The scanning directory')
    parser.add_argument('-r', '--reports', help='A folder to download the reports to')
    parser.add_argument('-t', '--te', action='store_true', help='Activate Threat Emulation')
    parser.add_argument('-a', '--av', action='store_true', help='Activate Anti-Virus')
    parser.add_argument('-d', '--debug', action='store_true', help='Add debugging')
    parser.add_argument('-k', '--key', help='API key', required=True)
    parser.add_argument('-p', '--pdf', action='store_true', help='Download PDF reports',)
    parser.add_argument('-x', '--xml', action='store_true', help='Download XML reports',)
    parser.add_argument('-b', '--benign', action='store_true',
                        help='Download benign reports (has severe impact on performance)')
    parser.add_argument('-R', '--recursive', action='store_true', help='Emulate the files in the directory recursively')
    args = parser.parse_args()

    # Asking the API to enable features and reports according to what was required by the user.
    features = []
    reports = []

    if args.te:
        features.append("te")
    if args.av:
        features.append("av")

    if args.xml:
        reports.append("xml")
    if args.pdf:
        reports.append("pdf")

    # Verify the user values
    if not os.path.isdir(args.directory):
        print("Invalid directory in input")
        exit(1)

    api = Run(args.directory,
              args.key,
              args.reports,
              features,
              args.debug,
              args.benign,
              reports,
              args.recursive)

    if not api.is_pending_files():
        print("The directory is empty")
        exit(0)

    print("Querying " + str(len(api.pending)) + " files from directory: " + args.directory)

    api.query_directory(True)
    api.print_arrays_status()

    if api.is_pending_files():
        print("UPLOADING"),
        api.upload_directory()
        api.print_arrays_status()

    max_tries = MAX_TRIES
    while api.is_pending_files() and max_tries > 0:
        time.sleep(WAITING_SEC)
        api.query_directory(False)
        api.print_arrays_status()
        max_tries -= 1

    api.print_arrays()
