## Example

To scan a folder on your computer, emulating each file using Check Point SandBlast technologies, and get detailed PDF reports, you will need a valid Threat Prevention API Key (contact Check Point for an evaluation license), then you can simply execute:

~~~~

Option 1: (without tex)
java -jar tp_api-1.0.jar -D path/to/folder/to/scan/ -R path/to/reports/folder/  -K YOUR_API_KEY -r -p -d

Option 2:(with tex)
java -jar tp_api-1.0.jar -D path/to/folder/to/scan/ -R path/to/reports/folder/  -K YOUR_API_KEY -r -p -d -tex convert path/to/folder/to/clean_files

~~~~

Usage:

~~~~
java -jar tp_api-1.0.jar -h
~~~~

~~~~

usage: java com.checkpoint.tp_api.cli.TeQueryCLI

 -D,--directory <scanning directory>                               The scanning directory
 -d,--debug <Path to .log file>                                    Add debugging
 -h,--help                                                         Show help message
 -K,--key <API Key>                                                API Key
 -p,--pdf                                                          Download PDF reports
 -pr,--proxy <host, port>                                          Proxy Settings
 -R,--reports <reports directory>                                  A folder to download the reports to
 -r,--recursive                                                    Emulate the files in the directory recursively
 -tex,--extraction <method('convert'/'clean'), clean files path>   Activate Threat Extraction (supported only with cloud); Scrubbing method. Convert to PDF / CleanContent
 -x,--xml                                                          Download XML reports

~~~~
