## API Key usage

For API Key clients, in order to scan a folder on your computer, emulating each file using Check Point SandBlast technologies and get detailed PDF reports,
you will need a valid Threat Prevention API Key (contact Check Point for an evaluation license), then you can simply execute:

~~~~
Option 1:
python tp_api.py -k YOUR_API_KEY -D /path/to/folder/to/scan -p -r /path/to/reports/folder

Option 2:
python tp_api.py -fp /path/to/file/to/scan -fn file_name -e <Check Point SandBlast Appliance IP address>
~~~~

## JWT usage

For JWT authenticated clients, in order to scan a file/folder with automatic token generation, instead of the --api-key argument, you need to provide --client-id argument.  
For simplicity this request generated the token for each request.  
The token is valid for 30 minutes and you should keep it for further requests.

~~~~
python tp_api.py -ci <YOUR_CLIENT_ID> <YOUR_ACCESS_KEY> -D /path/to/folder/to/scan -p -r /path/to/reports/folder
~~~~

For generating the token only, pass the -gt argument, as follows:

~~~~
python tp_api.py -ci <YOUR_CLIENT_ID> <YOUR_ACCESS_KEY> -gt
~~~~

If you wish to see your usage of the service, use your token from previous command and run:

~~~~
curl -H 'Content-Type: application/json' -H 'x-access-token: <your_token_from_prev_step>' -XGET https://te-cloud-us.checkpoint.com/app/aws/usage
~~~~

## Install

python setup.py install

## Usage

The script returns:  
**0**  -  All files/file are not malicious  
**1**  -  One of the files found malicious  
**-1** -  Internal error

~~~~
# python tp_api.py --help
usage: tp_api.py [-h] (-D DIRECTORY | -fp FILE_PATH) [-fn FILE_NAME] [-R]
                 (-k KEY | -e SANDBLAST_APPLIANCE | -ci CLIENT_ID ACCESS_KEY)
                 [-gt] [-d] [-t] [--tex] [--tex_folder TEX_FOLDER]
                 [-m {convert,clean}] [-r REPORTS] [-p] [-x] [-s]

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
  -ci CLIENT_ID ACCESS_KEY, --client_id CLIENT_ID ACCESS_KEY
                        Client ID and Access key, used for JWT token
                        authenticated requests
  -gt, --generate_token
                        Only create the JWT token without sending a request
  -d, --debug           Add debugging

Blades info:
  -t, --te              Activate Threat Emulation
  --tex                 Activate Threat Extraction (supported only with cloud)
  --tex_folder TEX_FOLDER
                        A folder to download the Scrubbing attachments
                        (required when TEX is active)
  -m {convert,clean}, --tex_method {convert,clean}
                        Scrubbing method. Convert to PDF / CleanContent

Reports info:
  Download Reports

  -r REPORTS, --reports REPORTS
                        A folder to download the reports to (required for
                        cloud)
  -p, --pdf             Download PDF reports
  -x, --xml             Download XML reports
  -s, --summary         Download summary reports

~~~~
