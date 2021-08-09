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

import requests

import utils.gs
from api.request import Payload
from api.te import TeData
from api.tex import TexData
from api.token_api import Token
from utils.file_data import FileData
from utils.logging import LogLevel
from utils.logging import Logger
from requests_toolbelt import MultipartEncoder

DEFAULT_REPORTS = [utils.gs.XML]
DEFAULT_FEATURES = [utils.gs.TE]
DEFAULT_DEBUG = False
DEFAULT_RECURSIVE_EMULATION = False
DEFAULT_MAX_FILES = 500


class Run:
    te_feature = False

    pending = {}
    finished = []
    error = []

    cookies = {}
    report_set = set()

    def __init__(self, scan_directory, file_path, file_name, api_key, client_id, access_key, generate_token, server, reports_folder, tex_method, tex_folder,
                 features=DEFAULT_FEATURES,
                 reports=DEFAULT_REPORTS,
                 recursive=DEFAULT_RECURSIVE_EMULATION):
        """
        Setting the requested parameters and creating
        :param scan_directory: the requested directory
        :param file_path: the requested file path
        :param file_name: the requested file name
        :param api_key: API Key for the cloud service
        :param server: Check Point SandBlast Appliance ip address
        :param reports_folder: the folder which the reports will be save to
        :param tex_method: the method to be used with Thereat Extraction
        :param tex_directory: the folder which the Scrubbing attachments will be save to
        :param features: the requested features
        :param reports: type of reports
        :param recursive: find files in the requested directory recursively
        """
        if api_key:
            self.headers = {'Authorization': api_key}
        elif client_id and access_key:
            try:
                token_obj = Token()
                api_token = token_obj.generate(client_id, access_key)
                if generate_token:
                    print(api_token)
                    exit(0)
                else:
                    Logger.log(LogLevel.INFO, 'Generated token for JWT authentication')
                    self.headers = {'x-access-token': api_token}
            except Exception as e:
                Logger.log(LogLevel.CRITICAL, 'failed to generate JWT token', e)
                exit(-1)
        else:
            self.headers = {}

        self.reports_folder = reports_folder
        self.tex_folder = tex_folder

        if features:
            self.features = features
        else:
            self.features = DEFAULT_FEATURES

        self.payload = Payload(reports, tex_method)
        self.server = server
        self.verify = True

        try:
            if reports_folder and not os.path.exists(reports_folder):
                os.makedirs(reports_folder)
        except Exception as e:
            Logger.log(LogLevel.CRITICAL,
                       'failed to create the needed folders', e)

        max_files = DEFAULT_MAX_FILES
        Logger.log(LogLevel.INFO, 'Calculating hash of files ')
        if scan_directory:
            for root, subdir_list, file_list in os.walk(r'%s' % scan_directory):
                for fn in file_list:
                    if max_files == 0:
                        Logger.log(LogLevel.INFO,
                                   'Max of %d files' % DEFAULT_MAX_FILES)
                        break
                    else:
                        max_files -= 1
                    if os.path.isfile(os.path.join(root, fn)):
                        file_data = FileData(fn, os.path.join(root, fn), list(self.features))
                        file_data.compute_hashes()
                        self.pending[file_data.md5] = file_data
                if not recursive or max_files == 0:
                    break
        else:
            file_data = FileData(file_name, file_path, list(self.features))
            file_data.compute_hashes()
            self.pending[file_data.md5] = file_data

    def set_cookie(self, response):
        """
        Set the response cookie for all of the requests
        :param response: the response which contains the cookie
        """
        if 'te_cookie' in response.cookies:
            self.cookies['te_cookie'] = response.cookies['te_cookie']

    def upload_directory(self):
        # Use copy of the list for proper removal
        res = True
        for file_data in self.pending.values():
            if not file_data.upload:
                continue
            try:
                session = requests.Session()
                json_request = self.payload.create_upload_payload(file_data)

                Logger.log(LogLevel.DEBUG, json_request)
                upload_url = utils.gs.get_selector(self.server, utils.gs.UPLOAD)
                with open(file_data.file_path, 'rb') as f:
                    form = MultipartEncoder({
                        "request": json_request,
                        "file": f,
                    })
                    headers = self.headers
                    headers["Content-Type"] = form.content_type
                    resp = session.post(upload_url,
                                        headers=headers,
                                        data=form,
                                        cookies=self.cookies,
                                        verify=self.verify)

                    Logger.log(LogLevel.DEBUG, resp)
                    if not self.handle_response(resp):
                        raise Exception('Failed to handle upload response')

            except Exception as e:
                Logger.log(LogLevel.ERROR, 'Uploading Error', e)
                res = False
                continue
        return res

    def query_directory(self, first_time):
        query_url = utils.gs.get_selector(self.server, utils.gs.QUERY)
        payload = self.payload.create_query_payload(self.pending)
        Logger.log(LogLevel.DEBUG, payload)

        try:
            if first_time:
                resp = requests.post(query_url, data=payload,
                                     headers=self.headers, verify=self.verify)
                self.set_cookie(resp)
                Logger.log(LogLevel.DEBUG, 'set cookie=', self.cookies)
            else:
                Logger.log(LogLevel.DEBUG, 'cookie=', self.cookies)
                resp = requests.post(query_url, data=payload,
                                     headers=self.headers,
                                     cookies=self.cookies,
                                     verify=self.verify)
            if not self.handle_response(resp, first_time):
                return False
        except IOError as e:
            Logger.log(LogLevel.ERROR, 'IO_ERROR', e)
            return False
        return True

    def handle_response(self, json_response, first_time=False):

        if json_response.status_code != 200:
            Logger.log(LogLevel.ERROR, json_response.status_code)
            Logger.log(LogLevel.ERROR, json_response.text)
            if json_response.status_code == 400:
                Logger.log(LogLevel.ERROR, "Bad request: please fix and rerun the command")
                exit(-1)
            return False

        parse_json = json.loads(json_response.text)
        Logger.log(LogLevel.DEBUG,
                   json.dumps(parse_json, indent=4, sort_keys=True))
        response_list = parse_json[utils.gs.RESPONSE]

        if type(response_list) is not list:
            response_list = [response_list]

        for response_object in response_list:
            file_data = self.pending.get(response_object[utils.gs.MD5])
            if utils.gs.TE in file_data.features \
                    and utils.gs.TE in response_object:
                found = TeData.handle_te_response(file_data, response_object,
                                                  first_time)
                if found:
                    self.download_reports(response_object[utils.gs.TE])
            if utils.gs.TEX in file_data.features \
                    and utils.gs.TEX in response_object:
                TexData.handle_tex_response(file_data, response_object, first_time)
                if TexData.extracted_file_download_id:
                    extraction_id = TexData.extracted_file_download_id
                    if not self.download_tex_result(extraction_id):
                        Logger.log(LogLevel.ERROR, 'Failed to download extraction_id:', extraction_id)
                        file_data.tex = TexData.error("Unable to download file_id=%s" % extraction_id)
                        return True
                    else:
                        file_data.tex = TexData.log(
                            "Cleaned file was downloaded successfully file_id= %s" % extraction_id)
            if not file_data.features:
                self.finished.append(self.pending.pop(file_data.md5))

        return True

    def download_report(self, file_id, image_id=0):
        return self.download_file(file_id, self.reports_folder, image_id)

    def download_tex_result(self, file_id):
        return self.download_file(file_id, self.tex_folder)

    def download_file(self, file_id, dest_folder, image_id=0):
        params = {'id': file_id}
        download_url = utils.gs.get_selector(self.server, utils.gs.DOWNLOAD)
        r = requests.get(download_url, headers=self.headers,
                         params=params, stream=True, cookies=self.cookies)
        name = findall('attachment; filename=\"(.*)\"', r.headers['content-disposition'])
        if len(name) > 0 and name[0]:
            if image_id:
                file_name = os.path.join(dest_folder,
                                         '%s_%s' % (str(image_id), str(name[0])))
            else:
                file_name = os.path.join(dest_folder, str(name[0]))
        else:
            Logger.log(LogLevel.INFO, 'FILE NAME IS MISSING IN HEADER')
            file_name = file_id
        try:
            with open(file_name, 'wb') as f:
                for chunk in r.iter_content(chunk_size=1024):
                    if chunk:  # filter out keep-alive new chunks
                        f.write(chunk)
                        f.flush()
        except IOError as e:
            Logger.log(LogLevel.ERROR, 'Save file IO_ERROR', e)
            return False
        return True

    def download_reports(self, json_response):
        if 'images' in json_response:
            for image in json_response['images']:
                report = image['report']
                if 'pdf_report' in report:
                    self.download_report(report['pdf_report'], image['id'])
                if 'xml_report' in report:
                    self.download_report(report['xml_report'], image['id'])
        if 'summary_report' in json_response:
            self.download_report(json_response['summary_report'])

    def is_pending_files(self):
        """
        Getter for the amount of files which are pending
        :return: How many files are we are still waiting for results for.
        """
        return len(self.pending) > 0

    def print_arrays(self):
        if len(self.pending) > 0:
            self.print_array(self.pending, 'Pending')
        if len(self.error) > 0:
            self.print_array(self.error, 'Error')
        if len(self.finished) > 0:
            self.print_array(self.finished, 'Finished')

    def print_arrays_status(self):
        Logger.log(LogLevel.INFO, 'PROGRESS:')
        self.print_status(self.pending, 'Pending')
        self.print_status(self.error, 'Error')
        self.print_status(self.finished, 'Finished')

    def get_final_status(self):
        return self.get_status(self.finished)

    @staticmethod
    def print_array(array, text):
        array_size = len(array)
        if array_size > 0:
            Logger.log(LogLevel.INFO, '----------- %s %s files -----------' % (
                str(array_size), text))
            for one_file in array:
                Logger.log(LogLevel.INFO, str(one_file))

    @staticmethod
    def print_status(array, text):
        array_size = len(array)
        if array_size > 0:
            Logger.log(LogLevel.INFO, '%s: %s files' % (text, str(array_size)))

    @staticmethod
    def get_status(array):
        array_size = len(array)
        ret = -1
        if array_size > 0:
            for one_file in array:
                if one_file.te:
                    if one_file.te.status == utils.gs.TE_VERDICT_MALICIOUS or one_file.te.verdict.lower() == utils.gs.MALICIOUS.lower():
                        return True
                    elif one_file.te.status == utils.gs.TE_VERDICT_BENIGN or one_file.te.verdict.lower() == utils.gs.BENIGN.lower():
                        ret = 0
        return ret
