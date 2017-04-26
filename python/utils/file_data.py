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

import hashlib
import os


class FileData:
    """
    Represents a file and contains all the data about that file.
    """

    def __init__(self, file_name, file_path, features):
        self.file_name = file_name
        self.file_type = os.path.splitext(file_name)[1][1:]
        self.file_path = file_path
        self.md5 = ''
        self.sha1 = ''
        self.features = features
        self.status = ''
        self.upload = True
        self.te = None
        self.tex = None

    def __str__(self):
        string = 'Name: %s,\tMD5: %s,\tSHA1: %s' % (
            self.file_name, self.md5, self.sha1)
        if self.status:
            string = '%s ERROR: %s' % (string, self.status)
        if self.te:
            string = '%s\n\t%s' % (string, str(self.te))
        if self.tex:
            string = '%s\n\t%s' % (string, str(self.tex))
        return string

    def compute_hashes(self):
        """
        This function should be called when we need to compute the
        hashes of the file. Currently computes sha1 and md5.
        """
        self.md5 = self.md5_of_file(self.file_path)
        self.sha1 = self.sha1_of_file(self.file_path)

    @staticmethod
    def _hash_of_file(f, digest_algorithm):
        """
        Calculated hash of file
        :param f: file
        :param digest_algorithm: digest algorithm according to it the
        file will be calculate
        :return:
        """
        while True:
            block = f.read(2 ** 10)
            if not block:
                break
            digest_algorithm.update(block)
        return digest_algorithm.hexdigest()

    @staticmethod
    def md5_of_file(file_path):
        """
        Calculates md5 of file
        :param file_path: the path of the file
        :return: md5 string of the file
        """
        md5 = hashlib.md5()
        return FileData._hash_of_file(open(file_path, 'rb'), md5)

    @staticmethod
    def sha1_of_file(file_path):
        """
        Calculates sha1 of file
        :param file_path: the path of the file
        :return: sha1 string of the file
        """
        sha1 = hashlib.sha1()
        return FileData._hash_of_file(open(file_path, 'rb'), sha1)
