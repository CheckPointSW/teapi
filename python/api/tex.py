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

import utils.gs


class TexData:
    # def __init__(self, status=''):
    def __init__(self, status):
        """
        This class handles TEX response.
        """
        self.status = status

    @classmethod
    def log(cls, status):
        tex_data = cls(utils.gs.MESSAGE)
        tex_data.status = status
        return tex_data

    @classmethod
    def error(cls, status):
        tex_data = cls(utils.gs.ERROR)
        tex_data.status = status
        return tex_data

    def __str__(self):
        return "TEX : %s" % self.status

    @staticmethod
    def handle_tex_response(file_data, response_object, first_time):
        TexData.extracted_file_download_id = None
        should_upload = True
        tex_object = response_object[utils.gs.TEX]
        response_label = tex_object[utils.gs.STATUS][
            utils.gs.LABEL]

        if response_label == utils.gs.FOUND:

            should_upload = False
            """ File was found in response """
            filename = file_data.file_name.decode('utf-8')
            file_data.features.remove(utils.gs.TEX)
            if tex_object["extract_result"] == "CP_EXTRACT_RESULT_NOT_SCRUBBED":
                file_data.tex = TexData.log("%s was not scrubbed" % filename)
                return True

            if tex_object["extract_result"] != "CP_EXTRACT_RESULT_SUCCESS":
                file_data.tex = TexData.error(
                    "Extraction did not succeed %s : extract_result: %s" % (filename, tex_object["extract_result"]))
                return False

            if "extracted_file_download_id" not in tex_object:
                file_data.tex = TexData.error("extracted_file_download_id is missing: %s " % filename)
                return True
            else:
                TexData.extracted_file_download_id = tex_object["extracted_file_download_id"]

        elif response_label in (
                utils.gs.NO_QUOTA,
                utils.gs.FORBIDDEN) or \
                (response_label == utils.gs.NOT_FOUND and not first_time) or \
                (response_label == utils.gs.PARTIALLY_FOUND and not first_time):

            file_data.tex = TexData.log('The status is: %s' % response_label)
            file_data.features.remove(utils.gs.TEX)

        elif response_label in (
                utils.gs.UPLOAD_SUCCESS,
                utils.gs.PENDING):
            pass

        if should_upload:
            file_data.upload = True

        return True
