# ManuscriptAssistant

# Setup-guide
After the project is installed on your device the first step is adding a "scans" folder, within it you can arange your manuscript projects for example "MS_0001", which again should be a folder.
Now you'll need to add atleast one image (jpg or png) to that folder.

Now add to your .env (a sample one should be included) the file path to the "path_scans" token.
After that run the once and use the "generat PDF" option, there will be an error but the program will create the nessecary folders.

You'll need to add the following file paths to your .env now:
-path_pdf_folder
-path_xml_folder
-path_issue_folder
-path_xml_template_folder
-google_credentials_path (might be made optional)

Now the program should be mostly functional. The gitlab tokens are optional for the users that want to upload the files to gitlab. Should these not be added, the options "Review manuscript" and "Create/Update Gitlab Issue" will end in an error.

The factors at the bottom of the sample .env, will determan the scaling of the output pdf or the compression factor of all outputs, except ocr's. A value of 100 corresponds to the original size of the file. While the "scaling_factor_pdf" must be bigger than 0, the "compression_factor_jpg" cannot be over 100 and cannot be equal or smaller than 0.

