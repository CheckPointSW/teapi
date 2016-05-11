## Threat Prevention API Python Client

A Python example of the Threat Prevention API implementation. The example references Threat Prevention API Reference Guide and implements the latest version of the API. Full documentation can be found on https://supportcenter.checkpoint.com by searching for _Threat Prevention API Reference Guide_ in the _Documentation_ tab.

## Example

To scan a folder on your computer, emulating each file using Check Point SandBlast technologies, and get detailed PDF reports, you will need a valid Threat Prevention API Key (contact Check Point for an evaluation license), then you can simply execute:

~~~~
python tp_api.py -k YOUR_API_KEY -D /path/to/folder/to/scan -p -r /path/to/reports/folder
~~~~

## Motivation

Enable an easy start for Threat Prevention API users with a detailed example on how to use the API.

## Installation and usage

Start by cloning the project:

~~~~
git clone https://bitbucket.org/chkp/teapi.git
~~~~

Install:

~~~~
python setup.py install
~~~~

Usage:

~~~~
python tp_api.py --help

usage: tp_api.py [-h] -D DIRECTORY -r REPORTS [-t] [-a] [-d] -k KEY [-p] [-x]
                 [-b] [-R]

Threat Prevention API example

optional arguments:
  -h, --help            show this help message and exit
  -D DIRECTORY, --directory DIRECTORY
                        The scanning directory
  -r REPORTS, --reports REPORTS
                        A folder to download the reports to
  -t, --te              Activate Threat Emulation
  -a, --av              Activate Anti-Virus
  -d, --debug           Add debugging
  -k KEY, --key KEY     API key
  -p, --pdf             Download PDF reports
  -x, --xml             Download XML reports
  -b, --benign          Enable benign file reports
  -R, --recursive       Emulate the files in the directory recursively

~~~~

## License

The example is distributed under Apache 2.0 license.