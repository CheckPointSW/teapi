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


# URI Strings
URI = 'https://te-api.checkpoint.com/tecloud/api/v1/file/'
TOKEN_URI = 'https://cloudinfra-gw.portal.checkpoint.com/auth/external'
PORT = "18194"
REMOTE_DIR = "tecloud/api/v1/file"

QUERY = 'query'
UPLOAD = 'upload'
DOWNLOAD = 'download'
QUERY_SELECTOR = '%s%s' % (URI, QUERY)
UPLOAD_SELECTOR = '%s%s' % (URI, UPLOAD)
DOWNLOAD_SELECTOR = '%s%s' % (URI, DOWNLOAD)


def get_selector(ip_address,selector):
    url = ""
    if ip_address:
        url = 'https://%s:%s/%s/%s' % (ip_address, PORT, REMOTE_DIR, selector)
    elif selector == QUERY:
        url = QUERY_SELECTOR
    elif selector == UPLOAD:
        url = UPLOAD_SELECTOR
    elif selector == DOWNLOAD:
        url = DOWNLOAD_SELECTOR
    return url

# Request Strings
MD5 = 'md5'
SHA1 = 'sha1'
SHA256 = 'sha256'

TE = 'te'
TEX = 'extraction'

PDF = 'pdf'
XML = 'xml'
SUMMARY = 'summary'

# Response Strings
STATUS = 'status'
LABEL = 'label'
RESPONSE = 'response'

FOUND = 'FOUND'
PARTIALLY_FOUND = 'PARTIALLY_FOUND'
NOT_FOUND = 'NOT_FOUND'
UPLOAD_SUCCESS = 'UPLOAD_SUCCESS'
PENDING = 'PENDING'
NO_QUOTA = 'NO_QUOTA'
FORBIDDEN = 'FORBIDDEN'

BENIGN = 'benign'
MALICIOUS = 'malicious'
ERROR = 'error'
MESSAGE = 'message'

# TE Strings
TE_VERDICT = 'combined_verdict'
TE_SEVERITY = 'severity'
TE_CONFIDENCE = 'confidence'
TE_VERDICT_MALICIOUS = 'verdict is Malicious'
TE_VERDICT_BENIGN = 'verdict is Benign'
