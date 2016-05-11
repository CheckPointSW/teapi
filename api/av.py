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


class AvData:
    def __init__(self, found=False, signature_name='', malware_family=0, malware_type=0, severity=0, confidence=0):
        self.found = found
        self.signature_name = signature_name
        self.malware_family = malware_family  # 0 - 5 (0-none, 1-low, 5 high)
        self.malware_type = malware_type
        self.severity = severity
        self.confidence = confidence

    def __str__(self):
        if self.found:
            return 'AV: Found - Name: %s, Family: %d, Type: %d, Severity: %d, Confidence: %d' % (self.signature_name,
                                                                                                 self.malware_family,
                                                                                                 self.malware_type,
                                                                                                 self.severity,
                                                                                                 self.confidence)
        else:
            return 'AV: Not Found'

    @classmethod
    def found(cls, signature_name, malware_family, malware_type, severity, confidence):
        return cls(True, signature_name, malware_family, malware_type, severity, confidence)

    @classmethod
    def not_found(cls):
        return cls(False)

    @staticmethod
    def handle_av_response(file_data, response_object):
        av_object = response_object[utils.gs.AV]
        response_label = av_object[utils.gs.STATUS][
            utils.gs.LABEL]

        if response_label in (
                utils.gs.FOUND, utils.gs.PARTIALLY_FOUND):
            av_info = av_object[utils.gs.AV_INFO]
            file_data.av = AvData.found(av_info[utils.gs.AV_NAME], av_info[
                utils.gs.AV_FAMILY], av_info[
                                            utils.gs.AV_TYPE],
                                        av_info[utils.gs.AV_SEVERITY], av_info[
                                            utils.gs.AV_CONFIDENCE])
        else:
            file_data.av = AvData.not_found()
        file_data.features.remove(utils.gs.AV)

