# TODO(developer): Uncomment these variables before running the sample.
# project_id= 'YOUR_PROJECT_ID'
# location = 'YOUR_PROJECT_LOCATION' # Format is 'us' or 'eu'
# processor_id = 'YOUR_PROCESSOR_ID' # Create processor in Cloud Console
# file_path = '/path/to/local/pdf'
import os
from google.cloud import vision
from pathlib import Path
import io
from dotenv import load_dotenv
#TODO: This needs to be in a config file
dotenv_path = Path.cwd() / '.env'
load_dotenv(dotenv_path)
credential_path = Path(os.getenv("google_credentials_path"))
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = str(credential_path)


def detect_text(path):
    """Detects text in the file."""

    client = vision.ImageAnnotatorClient()

    with io.open(path, 'rb') as image_file:
        content = image_file.read()

    image = vision.Image(content=content)

    response = client.text_detection(image=image)
    texts = response.text_annotations
    if len(texts) > 0:
        return texts[0].description
    else:
        return str(0)

#TODO: Check if the rest can be deleted
"""

def process_document_sample(
        project_id: str, location: str, processor_id: str, file_path: str
):
    # You must set the api_endpoint if you use a location other than 'us', e.g.:
    opts = {}
    if location == "eu":
        opts = {"api_endpoint": "eu-documentai.googleapis.com"}

    client = documentai.DocumentProcessorServiceClient(client_options=opts)

    # The full resource name of the processor, e.g.:
    # projects/project-id/locations/location/processor/processor-id
    # You must create new processors in the Cloud Console first
    name = f"projects/{project_id}/locations/{location}/processors/{processor_id}"
    # scan = cv2.imread(str(file_path))
    # scan = resizeImage(scan, 50)
    workingPath = '/home/lennart/Nextcloud/Projects/ms_worker/DATA/imsMS_1149_0005_V.jpg'
    # cv2.imwrite(workingPath, scan)
    with open(workingPath, "rb") as image:
        image_content = image.read()

    # Read the file into memory
    document = {"content": image_content, "mime_type": "application/pdf"}
    print(document)
    # Configure the process request
    request = {"name": name, "raw_document": document}
    print(request)
    # Recognizes text entities in the PDF document
    result = client.process_document(request=request)

    document = result.document

    print("Document processing complete.")

    # For a full list of Document object attributes, please reference this page: https://googleapis.dev/python/documentai/latest/_modules/google/cloud/documentai_v1beta3/types/document.html#Document

    document_pages = document.pages

    # Read the text recognition output from the processor
    print("The document contains the following paragraphs:")
    for page in document_pages:
        paragraphs = page.paragraphs
        for paragraph in paragraphs:
            paragraph_text = get_text(paragraph.layout, document)
            print(f"Paragraph text: {paragraph_text}")


# Extract shards from the text field
def get_text(doc_element: dict, document: dict):
  
    response = ""
    # If a text segment spans several lines, it will
    # be stored in different text segments.
    for segment in doc_element.text_anchor.text_segments:
        start_index = (
            int(segment.start_index)
            if segment in doc_element.text_anchor.text_segments
            else 0
        )
        end_index = int(segment.end_index)
        response += document.text[start_index:end_index]
    return response


# TODO(developer): Uncomment these variables before running the sample.
# project_id= 'YOUR_PROJECT_ID'
# location = 'YOUR_PROJECT_LOCATION' # Format is 'us' or 'eu'
# processor_id = 'YOUR_PROCESSOR_ID' # Create processor in Cloud Console
# gcs_input_uri = "YOUR_INPUT_URI"
# gcs_output_uri = "YOUR_OUTPUT_BUCKET_URI"
# gcs_output_uri_prefix = "YOUR_OUTPUT_URI_PREFIX"


def batch_process_documents(
        project_id,
        location,
        processor_id,
        gcs_input_uri,
        gcs_output_uri,
        gcs_output_uri_prefix,
        timeout: int = 300,
):
    # You must set the api_endpoint if you use a location other than 'us', e.g.:
    opts = {}
    if location == "eu":
        opts = {"api_endpoint": "eu-documentai.googleapis.com"}

    client = documentai.DocumentProcessorServiceClient(client_options=opts)

    destination_uri = f"{gcs_output_uri}/{gcs_output_uri_prefix}/"

    gcs_documents = documentai.GcsDocuments(
        documents=[{"gcs_uri": gcs_input_uri, "mime_type": "application/pdf"}]
    )

    # 'mime_type' can be 'application/pdf', 'image/tiff',
    # and 'image/gif', or 'application/json'
    input_config = documentai.BatchDocumentsInputConfig(gcs_documents=gcs_documents)

    # Where to write results
    output_config = documentai.DocumentOutputConfig(
        gcs_output_config={"gcs_uri": destination_uri}
    )

    # Location can be 'us' or 'eu'
    name = f"projects/{project_id}/locations/{location}/processors/{processor_id}"
    request = documentai.types.document_processor_service.BatchProcessRequest(
        name=name, input_documents=input_config, document_output_config=output_config,
    )

    operation = client.batch_process_documents(request)

    # Wait for the operation to finish
    operation.result(timeout=timeout)

    # Results are written to GCS. Use a regex to find
    # output files
    match = re.match(r"gs://([^/]+)/(.+)", destination_uri)
    output_bucket = match.group(1)
    prefix = match.group(2)

    storage_client = storage.Client()
    bucket = storage_client.get_bucket(output_bucket)
    blob_list = list(bucket.list_blobs(prefix=prefix))
    print("Output files:")

    for i, blob in enumerate(blob_list):
        # If JSON file, download the contents of this blob as a bytes object.
        if ".json" in blob.name:
            blob_as_bytes = blob.download_as_bytes()

            document = documentai.types.Document.from_json(blob_as_bytes)
            print(f"Fetched file {i + 1}")

            # For a full list of Document object attributes, please reference this page:
            # https://cloud.google.com/document-ai/docs/reference/rpc/google.cloud.documentai.v1beta3#document

            # Read the text recognition output from the processor
            for page in document.pages:
                for form_field in page.form_fields:
                    field_name = get_text(form_field.field_name, document)
                    field_value = get_text(form_field.field_value, document)
                    print("Extracted key value pair:")
                    print(f"\t{field_name}, {field_value}")
                for paragraph in page.paragraphs:
                    paragraph_text = get_text(paragraph.layout, document)
                    print(f"Paragraph text:\n{paragraph_text}")
        else:
            print(f"Skipping non-supported file type {blob.name}")


# Extract shards from the text field
def get_text(doc_element: dict, document: dict):
    
    response = ""
    # If a text segment spans several lines, it will
    # be stored in different text segments.
    for segment in doc_element.text_anchor.text_segments:
        start_index = (
            int(segment.start_index)
            if segment in doc_element.text_anchor.text_segments
            else 0
        )
        end_index = int(segment.end_index)
        response += document.text[start_index:end_index]
    return response
"""
