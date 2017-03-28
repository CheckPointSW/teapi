## Threat Prevention API Python Client

A Python example of the Threat Prevention API implementation. The example references Threat Prevention API Reference Guide and implements the latest version of the API. Full documentation can be found on https://supportcenter.checkpoint.com by searching for _Threat Prevention API Reference Guide_ in the _Documentation_ tab.

## Example

To scan a folder on your computer, emulating each file using Check Point SandBlast technologies, and get detailed PDF reports, you will need a valid Threat Prevention API Key (contact Check Point for an evaluation license), then you can simply execute:

~~~~
Option 1:
python tp_api.py -k YOUR_API_KEY -D /path/to/folder/to/scan -p -r /path/to/reports/folder

Option 2:
python tp_api.py -fp /path/to/file/to/scan -fn file_name -e <Check Point SandBlast Appliance IP address>

The script returns: 
0   All files/file are not malicious
1   One of the files found malicious
-1  Internal error
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

usage: tp_api.py [-h] (-D DIRECTORY | -fp FILE_PATH) [-fn FILE_NAME] [-R]
                 (-k KEY | -e SANDBLAST_APPLIANCE) [-d] [-t] [-a] [--tex]
                 [--tex_folder TEX_FOLDER] [-m {convert,clean}] [-r REPORTS]
                 [-p] [-x]

Threat Prevention API example

optional arguments:
  -h, --help            show this help message and exit
  -D DIRECTORY, --directory DIRECTORY
                        The scanning directory
  -fp FILE_PATH, --file_path FILE_PATH
                        Path to file
  -fn FILE_NAME, --file_name FILE_NAME
                        File Name, relevant when file path supplied
  -R, --recursive       Emulate the files in the directory recursively,
                        relevant when scanning directory supplied
  -k KEY, --key KEY     API key
  -e SANDBLAST_APPLIANCE, --sandblast_appliance SANDBLAST_APPLIANCE
                        Check Point SandBlast Appliance
  -d, --debug           Add debugging

Blades info:
  -t, --te              Activate Threat Emulation
  -a, --av              Activate Anti-Virus
  --tex                 Activate Threat Extraction (supported only with cloud)
  --tex_folder TEX_FOLDER
                        A folder to download the Scrubbing attachments
                        (required when TEX is active)
  -m {convert,clean}, --tex_method {convert,clean}
                        Scrubbing method. Convert to PDF / Extract Potentially malicious components (Clean)

Reports info:
  Download Reports

  -r REPORTS, --reports REPORTS
                        A folder to download the reports to (required for
                        cloud)
  -p, --pdf             Download PDF reports
  -x, --xml             Download XML reports
~~~~

## License

The example is distributed under Apache 2.0 license.