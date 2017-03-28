#!/usr/bin/env python

from setuptools import setup, find_packages
from codecs import open
from os import path

here = path.abspath(path.dirname(__file__))

with open(path.join(here, 'README.md'), encoding='utf-8') as f:
    long_description = f.read()

setup(
    name='Threat Prevention API Python Client',
    version='1.0.0',

    description='A Python example for the Threat Prevention API usage.',
    long_description=long_description,

    author='Check Point Software Technologies LTD.',
    url='http://www.checkpoint.com/',

    license='Apache 2.0',

    packages=find_packages(),

    install_requires=['requests','requests_toolbelt'],
)