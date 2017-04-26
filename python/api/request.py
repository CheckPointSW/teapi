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


class Payload:
    def __init__(self, reports, tex_method):
        self.reports = reports
        self.tex_method = tex_method

    def create_payload(self, file_name, file_type, features):
        payload = {
            'file_name': file_name,
            'file_type': file_type,
            'features': features,
        }
        if 'te' in features:
            payload['te'] = self.get_te_payload()
        if 'extraction' in features:
            payload['extraction'] = self.get_tex_payload()
        return payload

    def create_md5_payload(self, md5, file_name, file_type, features):
        json_object = self.create_payload(file_name, file_type, features)
        json_object['md5'] = md5
        return json_object

    def create_sha1_payload(self, sha1, file_name, file_type, features):
        json_object = self.create_payload(file_name, file_type, features)
        json_object['sha1'] = sha1
        return json_object

    def create_sha256_payload(self, sha256, file_name, file_type, features):
        json_object = self.create_payload(file_name, file_type, features)
        json_object['sha256'] = sha256
        return json_object

    def create_query_payload(self, files):
        payload = {'request': []}
        for file_data in files.values():
            payload['request'].append(
                self.create_md5_payload(
                    file_data.md5,
                    file_data.file_name,
                    file_data.file_type,
                    file_data.features
                )
            )
        return json.dumps(payload, indent=4)

    def create_upload_payload(self, file_data):
        return json.dumps(
            {'request': self.create_md5_payload(
                file_data.md5,
                file_data.file_name,
                file_data.file_type,
                file_data.features)},
            indent=4)

    def get_te_payload(self):
        return {
            'reports': self.reports
        }

    def get_tex_payload(self):
        if self.tex_method == 'convert':
            return self.tex_pdf()
        else:
            return self.tex_clean()

    @staticmethod
    def tex_pdf():
        return {
            "method": "pdf",
        }

    @staticmethod
    def tex_clean():
        return {
            "method": "clean",
            "scrubbed_parts_codes": ["sensitive_hyperlinks",
                                     "macros_and_code",
                                     "embedded_objects",
                                     "database_queries",
                                     "pdf_launch_actions",
                                     "pdf_sound_actions",
                                     "pdf_movie_actions",
                                     "pdf_uri_actions",
                                     "pdf_javascript_actions",
                                     "pdf_submit_form_actions",
                                     "pdf_go_to_remote_actions",
                                     "fast_save_data"]
            # "extracted_parts_codes": []
        }
