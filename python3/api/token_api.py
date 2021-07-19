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
import utils.gs
import requests


class Token:
    def __init__(self):
        """
        This class handles JWT Token generation.
        """
        pass

    def generate(self, client_id, access_key):
        data = {
            'clientId': client_id,
            'accessKey': access_key
        }

        headers = {
            'Content-Type': "application/json"
        }

        res = requests.post(utils.gs.TOKEN_URI, data=json.dumps(data), headers=headers, timeout=10)
        token = json.loads(res.text)['data']['token']
        return token
