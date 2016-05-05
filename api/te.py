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

import utils.gs


class TeData:
    def __init__(self, verdict="", confidence=0, severity=0, status=""):
        """
        This class handles TE response.
        """
        self.status = status
        self.verdict = verdict
        self.confidence = confidence
        self.severity = severity

    @classmethod
    def benign(cls):
        return cls(utils.gs.BENIGN)

    @classmethod
    def malicious(cls, confidence, severity):
        return cls(utils.gs.MALICIOUS, confidence, severity)

    @classmethod
    def error(cls, status):
        te_data = cls(utils.gs.ERROR)
        te_data.status = status
        return te_data

    def __str__(self):
        if self.verdict == utils.gs.BENIGN:
            return "TE: Benign"
        elif self.verdict == utils.gs.MALICIOUS:
            return "TE: Malicious, Severity: %d, Confidence: %d" % (self.severity, self.confidence)
        else:
            return "TE: Error: %s" % self.status

    @staticmethod
    def handle_te_response(file_data, response_object, first_time):

        te_object = response_object[utils.gs.TE]
        response_label = te_object[utils.gs.STATUS][
            utils.gs.LABEL]
        found = False

        if response_label == utils.gs.FOUND or \
                (response_object == utils.gs.PARTIALLY_FOUND and not first_time):
            file_data.upload = False
            if utils.gs.TE_VERDICT in te_object:
                if te_object[
                    utils.gs.TE_VERDICT] == \
                        utils.gs.BENIGN:
                    file_data.te = TeData.benign()
                    found = True
                elif te_object[
                    utils.gs.TE_VERDICT] == \
                        utils.gs.MALICIOUS:
                    file_data.te = TeData.malicious(te_object[
                                                        utils.gs.TE_CONFIDENCE
                                                    ],
                                                    te_object[
                                                        utils.gs.TE_SEVERITY
                                                    ])
                    found = True
                else:
                    file_data.te = TeData.error("verdict is %s" % te_object[
                        utils.gs.TE_VERDICT])
            else:
                file_data.te = TeData.error(te_object[utils.gs.STATUS][
                                                utils.gs.MESSAGE])

            file_data.features.remove(utils.gs.TE)

        elif response_label in (
                utils.gs.NO_QUOTA,
                utils.gs.FORBIDDEN) or \
                (response_label == utils.gs.NOT_FOUND and not first_time):

            file_data.te = TeData.error("The status is " + response_label)
            file_data.features.remove(utils.gs.TE)

        elif response_label in (
                utils.gs.UPLOAD_SUCCESS,
                utils.gs.PENDING):
            pass

        return found
