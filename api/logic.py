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

import json
import os
from re import findall
from api.request import Payload
import requests
from utils.file_data import FileData
from utils.logging import Logger
from utils.logging import LogLevel

import utils.gs
from av import AvData
from te import TeData


DEFAULT_REPORTS = [utils.gs.XML]
DEFAULT_FEATURES = [utils.gs.TE]
DEFAULT_DEBUG = False
DEFAULT_BENIGN_REPORTS = False
DEFAULT_RECURSIVE_EMULATION = False
DEFAULT_MAX_FILES = 500


class Run:
    te_feature = False
    av_feature = False

    pending = {}
    finished = []
    error = []

    cookies = {}
    report_set = set()

    def __init__(self, scan_directory, api_key, reports_folder, features=DEFAULT_FEATURES, debug=DEFAULT_DEBUG,
                 benign_reports=DEFAULT_BENIGN_REPORTS, reports=DEFAULT_REPORTS, recursive=DEFAULT_RECURSIVE_EMULATION):
        """
        Setting the requested parameters and creating
        :param scan_directory: the requested directory
        :param api_key: API Key fot the cloud service
        :param reports_folder: the folder which the reports will be save to
        :param features: the requested features
        :param debug: enable or disable debugs
        :param benign_reports: Request also reports for benign files
        :param reports: type of reports
        :param recursive: find files in the requested directory recursively
        """
        self.headers = {'Authorization': api_key}
        self.reports_folder = reports_folder
        self.benign_reports = benign_reports
        if features:
            self.features = features
        else:
            self.features = DEFAULT_FEATURES
        self.payload = Payload(benign_reports, reports)

        try:
            if not os.path.exists(reports_folder):
                os.makedirs(reports_folder)
        except Exception as e:
            Logger.log(LogLevel.CRITICAL, 'failed to create the needed folders', e)

        max_files = DEFAULT_MAX_FILES
        Logger.log(LogLevel.INFO, 'Calculating hash of files ')
        for root, subdir_list, file_list in os.walk(scan_directory):
            for file_name in file_list:
                if max_files == 0:
                    Logger.log(LogLevel.INFO, 'Max of %d files' % DEFAULT_MAX_FILES)
                    break
                else:
                    max_files -= 1
                if os.path.isfile(os.path.join(root, file_name)):
                    file_data = FileData(file_name, root, list(self.features))
                    file_data.compute_hashes()
                    self.pending[file_data.md5] = file_data
            if not recursive or max_files == 0:
                break

    def set_cookie(self, response):
        """
        Set the response cookie for all of the requests
        :param response: the response which contains the cookie
        """
        if 'te_cookie' in response.cookies:
                self.cookies['te_cookie'] = response.cookies['te_cookie']

    def upload_directory(self):

        for file_data in self.pending.values():  # Use copy of the list for proper removal
            if not file_data.upload:
                continue
            try:
                file_to_send = open(file_data.file_path, 'rb').read()
                json_request = self.payload.create_upload_payload(file_data)

                Logger.log(LogLevel.DEBUG, json_request)

                files = {'request': json_request, 'file': file_to_send}
                json_response = requests.post(utils.gs.UPLOAD_SELECTOR, files=files, headers=self.headers, cookies=self.cookies)
                if not self.handle_response(json_response):
                    break

            except Exception as e:
                Logger.log(LogLevel.ERROR, 'Uploading Error', e)
                continue

    def query_directory(self, first_time):

        payload = self.payload.create_query_payload(self.pending)
        Logger.log(LogLevel.DEBUG, payload)

        try:
            if first_time:
                resp = requests.post(utils.gs.QUERY_SELECTOR, data=payload, headers=self.headers)
                self.set_cookie(resp)
            else:
                resp = requests.post(utils.gs.QUERY_SELECTOR, data=payload, headers=self.headers, cookies=self.cookies)
            self.handle_response(resp, first_time)
        except IOError as e:
            Logger.log(LogLevel.ERROR, 'IO_ERROR', e)

    def handle_response(self, json_response, first_time=False):

        if json_response.status_code != 200:
            Logger.log(LogLevel.ERROR, json_response.status_code)
            Logger.log(LogLevel.ERROR, json_response.text)
            return False

        parse_json = json.loads(json_response.text)
        Logger.log(LogLevel.DEBUG, json.dumps(parse_json, indent=4, sort_keys=True))
        response_list = parse_json[utils.gs.RESPONSE]

        if type(response_list) is not list:
            response_list = [response_list]

        for response_object in response_list:
            file_data = self.pending.get(response_object[utils.gs.MD5])
            if utils.gs.TE in file_data.features and utils.gs.TE in response_object:
                found = TeData.handle_te_response(file_data, response_object, first_time)
                if found:
                    self.download_reports(response_object[utils.gs.TE])
            if utils.gs.AV in file_data.features and utils.gs.AV in response_object:
                AvData.handle_av_response(file_data, response_object)
            if not file_data.features:
                self.finished.append(self.pending.pop(file_data.md5))

        return True

    def download_file(self, file_id, image_id):

        params = {'id': file_id}
        r = requests.get(utils.gs.DOWNLOAD_SELECTOR, headers=self.headers, params=params, stream=True)
        name = findall('filename=\'(.*)\"', r.headers['content-disposition'])
        if len(name) > 0 and name[0]:
            file_name = os.path.join(self.reports_folder, '%s_%s' % (str(image_id), str(name[0])))
        else:
            Logger.log(LogLevel.ERROR, 'ERROR FILE NAME')
            return
        with open(file_name, 'wb') as f:
            for chunk in r.iter_content(chunk_size=1024):
                if chunk:  # filter out keep-alive new chunks
                    f.write(chunk)
                    f.flush()

    def download_reports(self, json_response):

        if 'images' in json_response:
            for image in json_response['images']:
                report = image['report']
                if 'pdf_report' in report:
                    self.download_file(report['pdf_report'], image['id'])
                if 'xml_report' in report:
                    self.download_file(report['xml_report'], image['id'])

    def is_pending_files(self):
        """
        Getter for the amount of files which are pending
        :return: How many files are we are still waiting for results for.
        """
        return len(self.pending) > 0

    def print_arrays(self):
        self.print_array(self.pending, 'Pending')
        self.print_array(self.finished, 'Finished')

    def print_arrays_status(self):
        Logger.log(LogLevel.INFO, 'PROGRESS:')
        self.print_status(self.pending, 'Pending')
        self.print_status(self.finished, 'Finished')

    @staticmethod
    def print_array(array, text):
        array_size = len(array)
        if array_size > 0:
            Logger.log(LogLevel.INFO, '----------- %s %s files -----------' % (str(array_size), text))
            for one_file in array:
                Logger.log(LogLevel.INFO, str(one_file))

    @staticmethod
    def print_status(array, text):
        array_size = len(array)
        if array_size > 0:
            Logger.log(LogLevel.INFO, '%s: %s files' % (text, str(array_size)))
